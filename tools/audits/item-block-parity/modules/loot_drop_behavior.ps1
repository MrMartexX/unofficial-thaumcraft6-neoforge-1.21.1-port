[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$LegacyManifestPath,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [string]$LegacyRoot = "02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master",
    [string]$PortRoot = "05_neoforge_port",
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$legacyPath = Join-Path $RepoRoot $LegacyRoot
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $LegacyManifestPath -PathType Leaf)) { throw "Legacy manifest not found: $LegacyManifestPath" }
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not $RulesRoot) { $RulesRoot = Join-Path (Split-Path -Parent $PSScriptRoot) "rules" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_loot_drop_behavior_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_loot_drop_behavior_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Read-RuleDocument([string]$Root, [string]$FileName) {
    if ([string]::IsNullOrWhiteSpace($Root)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    $path = Join-Path $Root $FileName
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    return Get-Content -Raw -LiteralPath $path | ConvertFrom-Json
}
function Normalize-Id([string]$Kind, [string]$Id) {
    if ([string]::IsNullOrWhiteSpace($Id)) { return "" }
    if ($Id.StartsWith("thaumcraft:")) { return $Id }
    return "thaumcraft:$Id"
}
function New-RuleLookup($Document) {
    $lookup = @{}
    foreach ($entry in @($Document.entries)) {
        if (-not $entry.kind -or -not $entry.id) { continue }
        $kind = [string]$entry.kind
        $id = Normalize-Id $kind ([string]$entry.id)
        $lookup["${kind}:$id"] = $entry
        if ($id.StartsWith("thaumcraft:")) { $lookup["${kind}:$($id.Substring('thaumcraft:'.Length))"] = $entry }
    }
    return $lookup
}
function Get-Rule($Lookup, [string]$Kind, [string]$RegistryId) {
    $qualified = Normalize-Id $Kind $RegistryId
    foreach ($key in @("${Kind}:$qualified", "${Kind}:$RegistryId")) {
        if ($Lookup.ContainsKey($key)) { return $Lookup[$key] }
    }
    return $null
}
function Get-RuleReason($Rule) {
    if ($null -eq $Rule) { return "" }
    if ($Rule.reason) { return [string]$Rule.reason }
    return "Reviewed loot/drop equivalence rule"
}
function Get-RuleAcceptedProperties($Rule) {
    if ($null -eq $Rule -or -not $Rule.acceptedProperties) { return @() }
    return @($Rule.acceptedProperties | ForEach-Object { [string]$_ })
}
function Format-List($Values) {
    $items = @($Values | Where-Object { $_ } | Select-Object -Unique)
    if ($items.Count -eq 0) { return "<none>" }
    return $items -join ", "
}
function Read-RelativeText([string]$Root, [string]$RelativePath) {
    if ([string]::IsNullOrWhiteSpace($RelativePath)) { return $null }
    $full = Join-Path $Root $RelativePath
    if (Test-Path -LiteralPath $full -PathType Leaf) { return Get-Content -Raw -LiteralPath $full }
    return $null
}
function Get-EntryValue($Entry, [string[]]$Names) {
    foreach ($name in $Names) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and $null -ne $prop.Value -and -not [string]::IsNullOrWhiteSpace([string]$prop.Value)) { return $prop.Value }
    }
    return $null
}
function Get-EntryId($Entry) {
    $value = Get-EntryValue $Entry @("registryId", "id", "name")
    if ([string]::IsNullOrWhiteSpace([string]$value)) { return "" }
    $s = [string]$value
    if ($s.StartsWith("thaumcraft:")) { return $s.Substring("thaumcraft:".Length) }
    return $s
}
function Resolve-LegacyText($Entry) {
    $relative = Get-EntryValue $Entry @("legacySourceFile", "legacyClassFile", "classFile", "sourceFile", "path")
    $text = Read-RelativeText $legacyPath ([string]$relative)
    if ($null -ne $text) { return [pscustomobject]@{ text = $text; source = $relative } }
    $legacyClass = Get-EntryValue $Entry @("legacyClass", "declaredClass", "className")
    if (-not [string]::IsNullOrWhiteSpace([string]$legacyClass) -and (Test-Path -LiteralPath $legacyPath -PathType Container)) {
        $simple = ([string]$legacyClass) -replace '^.*\.', '' -replace '\$.*$', ''
        $candidate = @(Get-ChildItem -LiteralPath $legacyPath -Recurse -Filter "$simple.java" -File -ErrorAction SilentlyContinue | Select-Object -First 1)
        if ($candidate.Count -gt 0) {
            return [pscustomobject]@{ text = (Get-Content -Raw -LiteralPath $candidate[0].FullName); source = ConvertTo-RelativeRepoPath $candidate[0].FullName }
        }
    }
    return [pscustomobject]@{ text = $null; source = $null }
}
function Get-RegistrationExpression([string]$RegistryText, [string]$Id) {
    if ([string]::IsNullOrWhiteSpace($RegistryText)) { return "" }
    $pattern = 'public\s+static\s+final\s+Supplier<Block>\s+(?<symbol>[A-Z0-9_]+)\s*=\s*BLOCKS\.register\(\s*"' + [regex]::Escape($Id) + '"\s*,\s*(?<expression>.*?)\);'
    $match = [regex]::Match($RegistryText, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)
    if ($match.Success) { return $match.Groups["expression"].Value.Trim() }
    return ""
}
function Get-PortBlockSourceText($Entry, [string]$BlocksText) {
    $pieces = [System.Collections.Generic.List[string]]::new()
    $expr = Get-RegistrationExpression $BlocksText $Entry.registryId
    if ($expr) { $pieces.Add($expr) }
    if ($Entry.portClassFile) {
        $classText = Read-RelativeText $portPath ([string]$Entry.portClassFile)
        if ($classText) { $pieces.Add($classText) }
    }
    return ($pieces -join "`n")
}
function Get-LootTablePath([string]$BlockId) {
    $dataRoot = Join-Path $portPath "src/main/resources/data/thaumcraft"
    $candidates = @(
        (Join-Path $dataRoot "loot_table/blocks/$BlockId.json"),
        (Join-Path $dataRoot "loot_tables/blocks/$BlockId.json")
    )
    foreach ($path in $candidates) {
        if (Test-Path -LiteralPath $path -PathType Leaf) { return $path }
    }
    return $candidates[0]
}
function Read-LootTableSnapshot([string]$BlockId) {
    $path = Get-LootTablePath $BlockId
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        return [pscustomobject][ordered]@{
            exists = $false
            path = ConvertTo-RelativeRepoPath $path
            validJson = $false
            drops = @()
            functions = @()
            conditions = @()
            selfDrop = $false
            noDrop = $false
            explosionDecay = $false
            silkTouch = $false
            fortune = $false
            evidence = "loot table missing"
        }
    }
    $raw = Get-Content -Raw -LiteralPath $path
    $valid = $true
    try { $null = $raw | ConvertFrom-Json } catch { $valid = $false }
    $drops = @([regex]::Matches($raw, '"name"\s*:\s*"(?<name>(?:thaumcraft|minecraft):[^"]+)"') | ForEach-Object { $_.Groups["name"].Value } | Sort-Object -Unique)
    $functions = @([regex]::Matches($raw, '"function"\s*:\s*"(?<name>[^"]+)"') | ForEach-Object { $_.Groups["name"].Value } | Sort-Object -Unique)
    $conditions = @([regex]::Matches($raw, '"condition"\s*:\s*"(?<name>[^"]+)"') | ForEach-Object { $_.Groups["name"].Value } | Sort-Object -Unique)
    $selfName = "thaumcraft:$BlockId"
    return [pscustomobject][ordered]@{
        exists = $true
        path = ConvertTo-RelativeRepoPath $path
        validJson = $valid
        drops = @($drops)
        functions = @($functions)
        conditions = @($conditions)
        selfDrop = $drops -contains $selfName
        noDrop = $valid -and $drops.Count -eq 0 -and ($raw -notmatch '"entries"\s*:')
        explosionDecay = [bool]($raw -match 'explosion_decay|survives_explosion')
        silkTouch = [bool]($raw -match 'silk_touch|match_tool')
        fortune = [bool]($raw -match 'fortune|apply_bonus|looting_enchant')
        evidence = "drops=$(Format-List $drops); conditions=$(Format-List $conditions); functions=$(Format-List $functions)"
    }
}
function Extract-FirstInt([string]$Text, [string[]]$Patterns) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return $null }
    foreach ($pattern in $Patterns) {
        $match = [regex]::Match($Text, $pattern, [System.Text.RegularExpressions.RegexOptions]::IgnoreCase -bor [System.Text.RegularExpressions.RegexOptions]::Singleline)
        if ($match.Success) { return [int]$match.Groups["value"].Value }
    }
    return $null
}
function Extract-ReferencedDrops([string]$Text) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return @() }
    $patterns = @(
        'new\s+ItemStack\s*\(\s*(?<owner>ItemsTC|BlocksTC|Items|Blocks)\.(?<symbol>[A-Za-z0-9_]+)',
        'return\s+(?<owner>ItemsTC|BlocksTC|Items|Blocks)\.(?<symbol>[A-Za-z0-9_]+)',
        'dropItem\s*\([^,]+,\s*(?<owner>ItemsTC|BlocksTC|Items|Blocks)\.(?<symbol>[A-Za-z0-9_]+)'
    )
    $drops = [System.Collections.Generic.List[string]]::new()
    foreach ($pattern in $patterns) {
        foreach ($match in [regex]::Matches($Text, $pattern, [System.Text.RegularExpressions.RegexOptions]::IgnoreCase)) {
            $drops.Add("$($match.Groups['owner'].Value).$($match.Groups['symbol'].Value)")
        }
    }
    return @($drops | Sort-Object -Unique)
}
function Get-SourceDropSnapshot([string]$Text, [string]$Side) {
    $hasDropMethod = [bool]($Text -match '\bgetDrops\b|\bquantityDropped\b|\bdamageDropped\b|\bharvestBlock\b|\bbreakBlock\b|\bdropBlockAsItem\b|\bdropResources\b|\bgetDrops\s*\(')
    $noDrop = [bool]($Text -match 'quantityDropped\s*\([^)]*\).*?return\s+0\s*;' -or $Text -match 'Collections\.emptyList|new\s+ArrayList\s*<[^>]*>\s*\(\s*\)')
    $quantity = Extract-FirstInt $Text @('quantityDropped\s*\([^)]*\)\s*\{[^}]*return\s+(?<value>\d+)\s*;', 'set_count[^\d]+(?<value>\d+)')
    $metaDrop = [bool]($Text -match '\bdamageDropped\b|\bgetMetaFromState\b|\bcopy_state\b|\bcopy_components\b')
    $silk = [bool]($Text -match '\bcanSilkHarvest\b|\bgetSilkTouchDrop\b|silk.?touch|match_tool')
    $fortune = [bool]($Text -match '\bquantityDroppedWithBonus\b|\bfortune\b|apply_bonus')
    $explosion = [bool]($Text -match 'explosion_decay|survives_explosion|Explosion')
    $customDrops = @(Extract-ReferencedDrops $Text)
    return [pscustomobject][ordered]@{
        side = $Side
        hasSource = -not [string]::IsNullOrWhiteSpace($Text)
        hasDropMethod = $hasDropMethod
        noDrop = $noDrop
        quantity = $quantity
        metadataDrop = $metaDrop
        silkTouch = $silk
        fortune = $fortune
        explosionAware = $explosion
        customDrops = @($customDrops)
    }
}
function Add-Result($Rows, [string]$Id, [string]$Subcheck, [string]$Status, [string]$Evidence, $LegacyValue, $PortValue, [string]$LegacySource, [string]$PortSource, $Rule = $null) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "drop_behavior"
        subcheck = $Subcheck
        kind = "block"
        id = "thaumcraft:$Id"
        status = $Status
        legacyValue = $LegacyValue
        portValue = $PortValue
        evidence = $Evidence
        legacySource = $LegacySource
        portSource = $PortSource
        rule = if ($null -eq $Rule) { $null } else { [pscustomobject][ordered]@{ reason = Get-RuleReason $Rule } }
    })
}
function Compare-BoolClue($LegacyValue, $PortValue, [string]$Property, $Rule) {
    $accepted = Get-RuleAcceptedProperties $Rule
    if ($accepted -contains $Property) { return "DROP_RULE_ACCEPTED" }
    if ($LegacyValue -eq $PortValue) { return "DROP_MATCH" }
    if ($LegacyValue -eq $true -and $PortValue -eq $false) { return "DROP_REVIEW_NEEDED" }
    return "DROP_NOT_EVIDENCED"
}

$legacy = Get-Content -Raw -LiteralPath $LegacyManifestPath | ConvertFrom-Json
$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$rules = New-RuleLookup (Read-RuleDocument $RulesRoot "loot-drop-equivalence.json")
$blocksPath = Join-Path $portPath "src/main/java/thaumcraft/common/registry/TCBlocks.java"
$blocksText = if (Test-Path -LiteralPath $blocksPath -PathType Leaf) { Get-Content -Raw -LiteralPath $blocksPath } else { "" }
$legacyBlocksById = @{}
foreach ($entry in @($legacy.entries)) {
    $id = Get-EntryId $entry
    $kindValue = Get-EntryValue $entry @("kind", "type")
    if ([string]::IsNullOrWhiteSpace($id)) { continue }
    if ($kindValue -and [string]$kindValue -ne "block") { continue }
    if (-not $legacyBlocksById.ContainsKey($id)) { $legacyBlocksById[$id] = $entry }
}

$results = [System.Collections.Generic.List[object]]::new()
$snapshots = [System.Collections.Generic.List[object]]::new()
foreach ($block in @($port.entries | Where-Object kind -eq "block" | Sort-Object registryId)) {
    $id = [string]$block.registryId
    $legacyEntry = if ($legacyBlocksById.ContainsKey($id)) { $legacyBlocksById[$id] } else { $null }
    $legacyResolved = if ($null -ne $legacyEntry) { Resolve-LegacyText $legacyEntry } else { [pscustomobject]@{ text = $null; source = $null } }
    $portText = Get-PortBlockSourceText $block $blocksText
    $loot = Read-LootTableSnapshot $id
    $legacySnapshot = Get-SourceDropSnapshot $legacyResolved.text "legacy"
    $portSourceSnapshot = Get-SourceDropSnapshot $portText "port"
    $rule = Get-Rule $rules "block" $id
    $portSource = if ($block.portClassFile) { [string]$block.portClassFile } else { "src/main/java/thaumcraft/common/registry/TCBlocks.java" }
    $snapshots.Add([pscustomobject][ordered]@{
        id = "thaumcraft:$id"
        legacySource = $legacyResolved.source
        portSource = $portSource
        lootTable = $loot.path
        legacy = $legacySnapshot
        portSourceClues = $portSourceSnapshot
        loot = $loot
        hasRule = $null -ne $rule
    })

    if ($null -eq $legacyEntry -or [string]::IsNullOrWhiteSpace([string]$legacyResolved.text)) {
        Add-Result $results $id "legacy_drop_source" "LEGACY_DROP_SOURCE_MISSING" "No legacy block source text found for drop behavior comparison" $null $null $legacyResolved.source $portSource $rule
    }

    if (-not $loot.exists) {
        Add-Result $results $id "loot_table_presence" "DROP_TABLE_MISSING" "Port block loot table missing" $null $false $legacyResolved.source $loot.path $rule
    } elseif (-not $loot.validJson) {
        Add-Result $results $id "loot_table_json" "DROP_REVIEW_NEEDED" "Port loot table exists but JSON could not be parsed" $null $false $legacyResolved.source $loot.path $rule
    } else {
        Add-Result $results $id "loot_table_presence" "DROP_MATCH" "Port block loot table exists and JSON is readable" $null $true $legacyResolved.source $loot.path $rule
    }

    if ($legacySnapshot.noDrop -or $loot.noDrop) {
        $status = Compare-BoolClue $legacySnapshot.noDrop $loot.noDrop "noDrop" $rule
        Add-Result $results $id "no_drop" $status "legacyNoDrop=$($legacySnapshot.noDrop); portNoDrop=$($loot.noDrop)" $legacySnapshot.noDrop $loot.noDrop $legacyResolved.source $loot.path $rule
    }

    $legacyHasCustom = $legacySnapshot.hasDropMethod -or $legacySnapshot.customDrops.Count -gt 0 -or $legacySnapshot.metadataDrop -or $legacySnapshot.silkTouch -or $legacySnapshot.fortune
    $portHasLootEvidence = $loot.exists -and ($loot.drops.Count -gt 0 -or $loot.conditions.Count -gt 0 -or $loot.functions.Count -gt 0)
    if ($legacyHasCustom) {
        $status = if ($portHasLootEvidence -or $portSourceSnapshot.hasDropMethod) { "DROP_REVIEW_NEEDED" } else { "DROP_VALUE_MISMATCH" }
        if ((Get-RuleAcceptedProperties $rule) -contains "customDropLogic") { $status = "DROP_RULE_ACCEPTED" }
        Add-Result $results $id "custom_drop_logic" $status "Legacy has custom/drop-method clues; verify port loot/source semantics. legacyDrops=$(Format-List $legacySnapshot.customDrops); loot=$($loot.evidence)" (Format-List $legacySnapshot.customDrops) (Format-List $loot.drops) $legacyResolved.source $loot.path $rule
    } elseif ($loot.selfDrop) {
        Add-Result $results $id "self_drop" "DROP_MATCH" "Port loot table self-drops thaumcraft:$id and no legacy custom drop clue was detected" "self_or_default" "thaumcraft:$id" $legacyResolved.source $loot.path $rule
    } elseif ($loot.exists -and $loot.drops.Count -gt 0) {
        Add-Result $results $id "non_self_drop" "DROP_REVIEW_NEEDED" "Port loot table drops non-self entries: $(Format-List $loot.drops)" $null (Format-List $loot.drops) $legacyResolved.source $loot.path $rule
    } elseif ($loot.exists -and -not $loot.noDrop) {
        Add-Result $results $id "drop_evidence" "DROP_NOT_EVIDENCED" "Loot table exists but explicit item drop evidence was not found by regex scanner" $null $loot.evidence $legacyResolved.source $loot.path $rule
    }

    foreach ($property in @("metadataDrop", "silkTouch", "fortune", "explosionAware")) {
        $legacyValue = [bool]$legacySnapshot.$property
        $portValue = if ($property -eq "metadataDrop") { [bool]($portSourceSnapshot.metadataDrop -or ($loot.functions -match "copy")) }
            elseif ($property -eq "silkTouch") { [bool]($portSourceSnapshot.silkTouch -or $loot.silkTouch) }
            elseif ($property -eq "fortune") { [bool]($portSourceSnapshot.fortune -or $loot.fortune) }
            else { [bool]($portSourceSnapshot.explosionAware -or $loot.explosionDecay) }
        if ($legacyValue -or $portValue) {
            $status = Compare-BoolClue $legacyValue $portValue $property $rule
            Add-Result $results $id $property $status "legacy=$legacyValue; port=$portValue" $legacyValue $portValue $legacyResolved.source $loot.path $rule
        }
    }

    if ($null -ne $legacySnapshot.quantity) {
        $accepted = Get-RuleAcceptedProperties $rule
        $status = if ($accepted -contains "quantity") { "DROP_RULE_ACCEPTED" } elseif ($loot.functions -match "set_count|apply_bonus" -or $portSourceSnapshot.quantity) { "DROP_REVIEW_NEEDED" } else { "DROP_NOT_EVIDENCED" }
        Add-Result $results $id "quantity" $status "legacyQuantity=$($legacySnapshot.quantity); portLootFunctions=$(Format-List $loot.functions)" $legacySnapshot.quantity (Format-List $loot.functions) $legacyResolved.source $loot.path $rule
    }
}

$orderedResults = @($results | Sort-Object status, id, subcheck)
$summaryBySubcheck = @($orderedResults | Group-Object subcheck | Sort-Object Name | ForEach-Object {
    [pscustomobject][ordered]@{
        subcheck = $_.Name
        rows = $_.Count
        matches = @($_.Group | Where-Object status -eq "DROP_MATCH").Count
        valueMismatch = @($_.Group | Where-Object status -eq "DROP_VALUE_MISMATCH").Count
        reviewNeeded = @($_.Group | Where-Object status -eq "DROP_REVIEW_NEEDED").Count
        tableMissing = @($_.Group | Where-Object status -eq "DROP_TABLE_MISSING").Count
        notEvidenced = @($_.Group | Where-Object status -eq "DROP_NOT_EVIDENCED").Count
        ruleAccepted = @($_.Group | Where-Object status -eq "DROP_RULE_ACCEPTED").Count
        legacyMissing = @($_.Group | Where-Object status -eq "LEGACY_DROP_SOURCE_MISSING").Count
    }
})
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("drop_behavior")
    policy = "Report-only loot/drop behavior scan. It compares source-evidenced legacy drop clues with port loot tables and port source clues. It does not claim exact gameplay parity without targeted runtime/GameTest verification."
    summary = [ordered]@{
        rows = $orderedResults.Count
        matches = @($orderedResults | Where-Object status -eq "DROP_MATCH").Count
        valueMismatch = @($orderedResults | Where-Object status -eq "DROP_VALUE_MISMATCH").Count
        reviewNeeded = @($orderedResults | Where-Object status -eq "DROP_REVIEW_NEEDED").Count
        tableMissing = @($orderedResults | Where-Object status -eq "DROP_TABLE_MISSING").Count
        notEvidenced = @($orderedResults | Where-Object status -eq "DROP_NOT_EVIDENCED").Count
        ruleAccepted = @($orderedResults | Where-Object status -eq "DROP_RULE_ACCEPTED").Count
        legacyMissing = @($orderedResults | Where-Object status -eq "LEGACY_DROP_SOURCE_MISSING").Count
        blocks = @($snapshots).Count
        bySubcheck = @($summaryBySubcheck)
    }
    snapshots = @($snapshots | Sort-Object id)
    results = $orderedResults
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 18 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block loot drop behavior report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | Matches | Value mismatch | Review needed | Table missing | Not evidenced | Rule accepted | Legacy missing | Blocks |")
$lines.Add("|---:|---:|---:|---:|---:|---:|---:|---:|---:|")
$lines.Add("| $($report.summary.rows) | $($report.summary.matches) | $($report.summary.valueMismatch) | $($report.summary.reviewNeeded) | $($report.summary.tableMissing) | $($report.summary.notEvidenced) | $($report.summary.ruleAccepted) | $($report.summary.legacyMissing) | $($report.summary.blocks) |")
$lines.Add("")
$lines.Add("## By subcheck")
$lines.Add("")
$lines.Add("| Subcheck | Rows | Matches | Value mismatch | Review needed | Table missing | Not evidenced | Rule accepted | Legacy missing |")
$lines.Add("|---|---:|---:|---:|---:|---:|---:|---:|---:|")
foreach ($row in $summaryBySubcheck) {
    $lines.Add("| $($row.subcheck) | $($row.rows) | $($row.matches) | $($row.valueMismatch) | $($row.reviewNeeded) | $($row.tableMissing) | $($row.notEvidenced) | $($row.ruleAccepted) | $($row.legacyMissing) |")
}
$lines.Add("")
$lines.Add("## Non-matching rows")
$lines.Add("")
$lines.Add("| ID | Subcheck | Status | Legacy | Port | Port source | Evidence |")
$lines.Add("|---|---|---|---|---|---|---|")
foreach ($row in $orderedResults | Where-Object { $_.status -ne "DROP_MATCH" }) {
    $safeEvidence = ([string]$row.evidence).Replace("|", "\|")
    $legacyValue = if ($null -eq $row.legacyValue) { "" } else { ([string]$row.legacyValue).Replace("|", "\|") }
    $portValue = if ($null -eq $row.portValue) { "" } else { ([string]$row.portValue).Replace("|", "\|") }
    $lines.Add("| ``$($row.id)`` | $($row.subcheck) | $($row.status) | $legacyValue | $portValue | ``$($row.portSource)`` | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Loot/drop behavior report: $OutputMarkdown"
Write-Output "Rows=$($report.summary.rows), matches=$($report.summary.matches), valueMismatch=$($report.summary.valueMismatch), reviewNeeded=$($report.summary.reviewNeeded), tableMissing=$($report.summary.tableMissing), notEvidenced=$($report.summary.notEvidenced), ruleAccepted=$($report.summary.ruleAccepted), legacyMissing=$($report.summary.legacyMissing), blocks=$($report.summary.blocks)"
