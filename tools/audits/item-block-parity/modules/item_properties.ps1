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
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_item_property_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_item_property_report.md" }

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
function Test-Truthy($Value) {
    if ($null -eq $Value) { return $false }
    if ($Value -is [bool]) { return $Value }
    return [string]$Value -eq "True"
}
function Get-RuleReason($Rule) {
    if ($null -eq $Rule) { return "" }
    if ($Rule.reason) { return [string]$Rule.reason }
    return "Reviewed item-property equivalence rule"
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
    $pattern = 'public\s+static\s+final\s+Supplier<(?<type>[^>]+)>\s+(?<symbol>[A-Z0-9_]+)\s*=\s*(?<expression>.*?"' + [regex]::Escape($Id) + '".*?);'
    $match = [regex]::Match($RegistryText, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)
    if ($match.Success) { return $match.Groups["expression"].Value.Trim() }
    return ""
}
function Get-PortSourceText($Entry, [string]$ItemsText) {
    $pieces = [System.Collections.Generic.List[string]]::new()
    $expr = Get-RegistrationExpression $ItemsText $Entry.registryId
    if ($expr) { $pieces.Add($expr) }
    if ($Entry.portClassFile) {
        $classText = Read-RelativeText $portPath ([string]$Entry.portClassFile)
        if ($classText) { $pieces.Add($classText) }
    }
    return ($pieces -join "`n")
}
function Extract-FirstInt([string]$Text, [string[]]$Patterns) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return $null }
    foreach ($pattern in $Patterns) {
        $match = [regex]::Match($Text, $pattern, [System.Text.RegularExpressions.RegexOptions]::IgnoreCase)
        if ($match.Success) { return [int]$match.Groups["value"].Value }
    }
    return $null
}
function Extract-Rarity([string]$Text) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return $null }
    $patterns = @(
        'rarity\s*\(\s*Rarity\.(?<value>[A-Z_]+)\s*\)',
        'EnumRarity\.(?<value>[A-Z_]+)',
        'getRarity\s*\([^)]*\)\s*\{[^}]*return\s+(?:EnumRarity\.|Rarity\.)(?<value>[A-Z_]+)',
        'setRarity\s*\(\s*(?:EnumRarity\.|Rarity\.)(?<value>[A-Z_]+)\s*\)'
    )
    foreach ($pattern in $patterns) {
        $match = [regex]::Match($Text, $pattern, [System.Text.RegularExpressions.RegexOptions]::IgnoreCase -bor [System.Text.RegularExpressions.RegexOptions]::Singleline)
        if ($match.Success) { return $match.Groups["value"].Value.ToLowerInvariant() }
    }
    return $null
}
function Get-PropertySnapshot([string]$Text, [string]$Side) {
    $stack = if ($Side -eq "port") {
        Extract-FirstInt $Text @('stacksTo\s*\(\s*(?<value>\d+)\s*\)', 'maxStackSize\s*\(\s*(?<value>\d+)\s*\)')
    } else {
        Extract-FirstInt $Text @('setMaxStackSize\s*\(\s*(?<value>\d+)\s*\)', 'maxStackSize\s*=\s*(?<value>\d+)', 'getItemStackLimit\s*\([^)]*\)\s*\{[^}]*return\s+(?<value>\d+)')
    }
    $durability = if ($Side -eq "port") {
        Extract-FirstInt $Text @('durability\s*\(\s*(?<value>\d+)\s*\)', 'defaultDurability\s*\(\s*(?<value>\d+)\s*\)')
    } else {
        Extract-FirstInt $Text @('setMaxDamage\s*\(\s*(?<value>\d+)\s*\)', 'maxDamage\s*=\s*(?<value>\d+)', 'getMaxDamage\s*\([^)]*\)\s*\{[^}]*return\s+(?<value>\d+)')
    }
    $rarity = Extract-Rarity $Text
    $flags = [ordered]@{
        noRepair = [bool]($Text -match '\bsetNoRepair\s*\(|\bnoRepair\s*\(')
        fireResistant = [bool]($Text -match '\bfireResistant\s*\(|\bisImmuneToFire\b')
        craftRemainder = [bool]($Text -match '\bcraftRemainder\s*\(|\bcontainerItem\b|\bgetContainerItem\b')
        food = [bool]($Text -match '\bfood\s*\(|\bFoodProperties\b|\bItemFood\b')
        customUse = [bool]($Text -match '\bonItemRightClick\b|\bonItemUse\b|\buseOn\s*\(|\buse\s*\(|\bfinishUsingItem\b|\binteractLivingEntity\b')
        tooltip = [bool]($Text -match '\bappendHoverText\b|\baddInformation\b|\btooltip\b')
        subtypes = [bool]($Text -match '\bsetHasSubtypes\s*\(|\bgetSubItems\b|\bgetMetadata\b|\bvariants?\b')
        enchantability = [bool]($Text -match '\bgetItemEnchantability\b|\benchantable\s*\(')
    }
    return [pscustomobject][ordered]@{
        stackSize = $stack
        durability = $durability
        rarity = $rarity
        flags = [pscustomobject]$flags
    }
}
function Add-Result($Rows, [string]$Id, [string]$Subcheck, [string]$Status, [string]$Evidence, $LegacyValue, $PortValue, [string]$LegacySource, [string]$PortSource, $Rule = $null) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "item_properties"
        subcheck = $Subcheck
        kind = "item"
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
function Get-RuleAcceptedProperties($Rule) {
    if ($null -eq $Rule -or -not $Rule.acceptedProperties) { return @() }
    return @($Rule.acceptedProperties | ForEach-Object { [string]$_ })
}
function Get-CompareStatus($LegacyValue, $PortValue, [string]$Property, $Rule) {
    $accepted = Get-RuleAcceptedProperties $Rule
    if ($accepted -contains $Property) { return "ITEM_PROPERTY_RULE_ACCEPTED" }
    if ($null -eq $LegacyValue -and $null -eq $PortValue) { return "ITEM_PROPERTY_NO_EVIDENCE" }
    if ($null -eq $LegacyValue -or $null -eq $PortValue) { return "ITEM_PROPERTY_REVIEW_NEEDED" }
    if ([string]$LegacyValue -eq [string]$PortValue) { return "ITEM_PROPERTY_MATCH" }
    return "ITEM_PROPERTY_VALUE_MISMATCH"
}

$legacy = Get-Content -Raw -LiteralPath $LegacyManifestPath | ConvertFrom-Json
$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$itemRules = New-RuleLookup (Read-RuleDocument $RulesRoot "item-property-equivalence.json")
$itemsRegistryPath = Join-Path $portPath "src/main/java/thaumcraft/common/registry/TCItems.java"
$itemsText = if (Test-Path -LiteralPath $itemsRegistryPath -PathType Leaf) { Get-Content -Raw -LiteralPath $itemsRegistryPath } else { "" }

$legacyById = @{}
foreach ($entry in @($legacy.entries | Where-Object { $_.kind -eq "item" })) {
    if ($entry.registryId -and -not $legacyById.ContainsKey([string]$entry.registryId)) { $legacyById[[string]$entry.registryId] = $entry }
}

$results = [System.Collections.Generic.List[object]]::new()
$snapshots = [System.Collections.Generic.List[object]]::new()
foreach ($portEntry in @($port.entries | Where-Object { $_.kind -eq "item" } | Sort-Object registryId)) {
    $id = [string]$portEntry.registryId
    $rule = Get-Rule $itemRules "item" $id
    $portText = Get-PortSourceText $portEntry $itemsText
    $portSnapshot = Get-PropertySnapshot $portText "port"
    $legacyEntry = if ($legacyById.ContainsKey($id)) { $legacyById[$id] } else { $null }
    $legacyResolved = if ($legacyEntry) { Resolve-LegacyText $legacyEntry } else { [pscustomobject]@{ text = $null; source = $null } }
    $legacySnapshot = Get-PropertySnapshot $legacyResolved.text "legacy"
    $portSource = if ($portEntry.portClassFile) { [string]$portEntry.portClassFile } else { "src/main/java/thaumcraft/common/registry/TCItems.java" }
    $legacySource = if ($legacyResolved.source) { [string]$legacyResolved.source } else { "<missing>" }

    $snapshots.Add([pscustomobject][ordered]@{
        id = "thaumcraft:$id"
        blockItem = [bool]$portEntry.blockItem
        declaredClass = $portEntry.declaredClass
        portSource = $portSource
        legacySource = $legacySource
        port = $portSnapshot
        legacy = $legacySnapshot
        hasRule = $null -ne $rule
    })

    if ($null -eq $legacyEntry) {
        Add-Result $results $id "legacy_item_presence" "LEGACY_ITEM_MISSING" "No direct legacy item manifest entry found for this port item ID" $null $id "<missing>" $portSource $rule
        continue
    }
    if ($null -eq $legacyResolved.text) {
        Add-Result $results $id "legacy_item_source" "ITEM_PROPERTY_REVIEW_NEEDED" "Legacy item manifest entry exists, but source text could not be resolved" $null $id $legacySource $portSource $rule
    }

    foreach ($propertyName in @("stackSize", "durability", "rarity")) {
        $legacyValue = $legacySnapshot.$propertyName
        $portValue = $portSnapshot.$propertyName
        $status = Get-CompareStatus $legacyValue $portValue $propertyName $rule
        if ($status -eq "ITEM_PROPERTY_NO_EVIDENCE") { continue }
        $evidence = "$propertyName legacy=$legacyValue; port=$portValue"
        if ($status -eq "ITEM_PROPERTY_RULE_ACCEPTED") { $evidence = "$evidence; accepted by rule: $(Get-RuleReason $rule)" }
        Add-Result $results $id $propertyName $status $evidence $legacyValue $portValue $legacySource $portSource $rule
    }

    foreach ($flagName in @("noRepair", "fireResistant", "craftRemainder", "food", "customUse", "tooltip", "subtypes", "enchantability")) {
        $legacyFlag = [bool]$legacySnapshot.flags.$flagName
        $portFlag = [bool]$portSnapshot.flags.$flagName
        if (-not $legacyFlag -and -not $portFlag) { continue }
        $status = Get-CompareStatus $legacyFlag $portFlag $flagName $rule
        $evidence = "$flagName legacy=$legacyFlag; port=$portFlag"
        if ($status -eq "ITEM_PROPERTY_RULE_ACCEPTED") { $evidence = "$evidence; accepted by rule: $(Get-RuleReason $rule)" }
        Add-Result $results $id $flagName $status $evidence $legacyFlag $portFlag $legacySource $portSource $rule
    }
}

$orderedResults = @($results | Sort-Object status, id, subcheck)
$summaryBySubcheck = @($orderedResults | Group-Object subcheck | Sort-Object Name | ForEach-Object {
    [pscustomobject][ordered]@{
        subcheck = $_.Name
        rows = $_.Count
        matches = @($_.Group | Where-Object status -eq "ITEM_PROPERTY_MATCH").Count
        valueMismatch = @($_.Group | Where-Object status -eq "ITEM_PROPERTY_VALUE_MISMATCH").Count
        reviewNeeded = @($_.Group | Where-Object status -eq "ITEM_PROPERTY_REVIEW_NEEDED").Count
        ruleAccepted = @($_.Group | Where-Object status -eq "ITEM_PROPERTY_RULE_ACCEPTED").Count
        legacyMissing = @($_.Group | Where-Object status -eq "LEGACY_ITEM_MISSING").Count
    }
})
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("item_properties")
    policy = "Report-only item property parity scan. It compares source-evidenced stack size, durability, rarity and behavior/property clues where both legacy and port source evidence can be resolved. It does not assert full gameplay parity."
    summary = [ordered]@{
        rows = $orderedResults.Count
        matches = @($orderedResults | Where-Object status -eq "ITEM_PROPERTY_MATCH").Count
        valueMismatch = @($orderedResults | Where-Object status -eq "ITEM_PROPERTY_VALUE_MISMATCH").Count
        reviewNeeded = @($orderedResults | Where-Object status -eq "ITEM_PROPERTY_REVIEW_NEEDED").Count
        ruleAccepted = @($orderedResults | Where-Object status -eq "ITEM_PROPERTY_RULE_ACCEPTED").Count
        legacyMissing = @($orderedResults | Where-Object status -eq "LEGACY_ITEM_MISSING").Count
        snapshots = @($snapshots).Count
        bySubcheck = @($summaryBySubcheck)
    }
    snapshots = @($snapshots | Sort-Object id)
    results = $orderedResults
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 18 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item property parity report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | Matches | Value mismatch | Review needed | Rule accepted | Legacy missing | Snapshots |")
$lines.Add("|---:|---:|---:|---:|---:|---:|---:|")
$lines.Add("| $($report.summary.rows) | $($report.summary.matches) | $($report.summary.valueMismatch) | $($report.summary.reviewNeeded) | $($report.summary.ruleAccepted) | $($report.summary.legacyMissing) | $($report.summary.snapshots) |")
$lines.Add("")
$lines.Add("## By subcheck")
$lines.Add("")
$lines.Add("| Subcheck | Rows | Match | Mismatch | Review | Rule | Legacy missing |")
$lines.Add("|---|---:|---:|---:|---:|---:|---:|")
foreach ($row in $summaryBySubcheck) { $lines.Add("| $($row.subcheck) | $($row.rows) | $($row.matches) | $($row.valueMismatch) | $($row.reviewNeeded) | $($row.ruleAccepted) | $($row.legacyMissing) |") }
$lines.Add("")
$lines.Add("## Non-match rows")
$lines.Add("")
$lines.Add("| ID | Subcheck | Status | Legacy | Port | Evidence |")
$lines.Add("|---|---|---|---|---|---|")
foreach ($row in $orderedResults | Where-Object { $_.status -ne "ITEM_PROPERTY_MATCH" }) {
    $safeEvidence = ([string]$row.evidence).Replace("|", "\|")
    $legacyValue = if ($null -eq $row.legacyValue) { "<none>" } else { [string]$row.legacyValue }
    $portValue = if ($null -eq $row.portValue) { "<none>" } else { [string]$row.portValue }
    $lines.Add("| ``$($row.id)`` | $($row.subcheck) | $($row.status) | ``$legacyValue`` | ``$portValue`` | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Item property parity report: $OutputMarkdown"
Write-Output "Rows=$($report.summary.rows), matches=$($report.summary.matches), valueMismatch=$($report.summary.valueMismatch), reviewNeeded=$($report.summary.reviewNeeded), ruleAccepted=$($report.summary.ruleAccepted), legacyMissing=$($report.summary.legacyMissing), snapshots=$($report.summary.snapshots)"
