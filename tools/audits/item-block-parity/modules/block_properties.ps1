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
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_block_property_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_block_property_report.md" }

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
    return "Reviewed block-property equivalence rule"
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
function Get-PortSourceText($Entry, [string]$BlocksText) {
    $pieces = [System.Collections.Generic.List[string]]::new()
    $expr = Get-RegistrationExpression $BlocksText $Entry.registryId
    if ($expr) { $pieces.Add($expr) }
    if ($Entry.portClassFile) {
        $classText = Read-RelativeText $portPath ([string]$Entry.portClassFile)
        if ($classText) { $pieces.Add($classText) }
    }
    return ($pieces -join "`n")
}
function Extract-FirstNumber([string]$Text, [string[]]$Patterns) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return $null }
    foreach ($pattern in $Patterns) {
        $match = [regex]::Match($Text, $pattern, [System.Text.RegularExpressions.RegexOptions]::IgnoreCase -bor [System.Text.RegularExpressions.RegexOptions]::Singleline)
        if ($match.Success) { return [double]::Parse($match.Groups["value"].Value, [System.Globalization.CultureInfo]::InvariantCulture) }
    }
    return $null
}
function Extract-Strength($Text) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return [pscustomobject]@{ hardness = $null; resistance = $null } }
    $m = [regex]::Match($Text, '\.strength\s*\(\s*(?<hard>-?\d+(?:\.\d+)?)[fFdD]?\s*(?:,\s*(?<res>-?\d+(?:\.\d+)?)[fFdD]?\s*)?\)', [System.Text.RegularExpressions.RegexOptions]::IgnoreCase)
    if ($m.Success) {
        $hard = [double]::Parse($m.Groups["hard"].Value, [System.Globalization.CultureInfo]::InvariantCulture)
        $res = if ($m.Groups["res"].Success -and $m.Groups["res"].Value) { [double]::Parse($m.Groups["res"].Value, [System.Globalization.CultureInfo]::InvariantCulture) } else { $hard }
        return [pscustomobject]@{ hardness = $hard; resistance = $res }
    }
    return [pscustomobject]@{ hardness = $null; resistance = $null }
}
function Extract-SymbolValue([string]$Text, [string[]]$Patterns) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return $null }
    foreach ($pattern in $Patterns) {
        $match = [regex]::Match($Text, $pattern, [System.Text.RegularExpressions.RegexOptions]::IgnoreCase -bor [System.Text.RegularExpressions.RegexOptions]::Singleline)
        if ($match.Success) { return $match.Groups["value"].Value.ToLowerInvariant() }
    }
    return $null
}
function Get-BlockPropertySnapshot([string]$Text, [string]$Side) {
    $strength = Extract-Strength $Text
    $hardness = if ($null -ne $strength.hardness) { $strength.hardness } elseif ($Side -eq "port") {
        Extract-FirstNumber $Text @('\.destroyTime\s*\(\s*(?<value>-?\d+(?:\.\d+)?)[fFdD]?\s*\)')
    } else {
        Extract-FirstNumber $Text @('setHardness\s*\(\s*(?<value>-?\d+(?:\.\d+)?)[fFdD]?\s*\)', 'blockHardness\s*=\s*(?<value>-?\d+(?:\.\d+)?)')
    }
    $resistance = if ($null -ne $strength.resistance) { $strength.resistance } elseif ($Side -eq "port") {
        Extract-FirstNumber $Text @('\.explosionResistance\s*\(\s*(?<value>-?\d+(?:\.\d+)?)[fFdD]?\s*\)')
    } else {
        Extract-FirstNumber $Text @('setResistance\s*\(\s*(?<value>-?\d+(?:\.\d+)?)[fFdD]?\s*\)', 'blockResistance\s*=\s*(?<value>-?\d+(?:\.\d+)?)')
    }
    $sound = if ($Side -eq "port") {
        Extract-SymbolValue $Text @('\.sound\s*\(\s*SoundType\.(?<value>[A-Z0-9_]+)\s*\)', '\.sound\s*\(\s*(?<value>[A-Za-z0-9_.$]+)\s*\)')
    } else {
        Extract-SymbolValue $Text @('setSoundType\s*\(\s*SoundType\.(?<value>[A-Z0-9_]+)\s*\)', 'SoundType\.(?<value>[A-Z0-9_]+)')
    }
    $light = if ($Side -eq "port") {
        Extract-FirstNumber $Text @('\.lightLevel\s*\([^\)]*?->\s*(?<value>\d+)\s*\)', '\.lightLevel\s*\(\s*(?<value>\d+)\s*\)')
    } else {
        Extract-FirstNumber $Text @('setLightLevel\s*\(\s*(?<value>-?\d+(?:\.\d+)?)[fFdD]?\s*\)', 'getLightValue\s*\([^)]*\)\s*\{[^}]*return\s+(?<value>\d+)')
    }
    $flags = [ordered]@{
        randomTicks = [bool]($Text -match '\brandomTicks\s*\(|\bsetTickRandomly\s*\(\s*true\s*\)')
        noOcclusion = [bool]($Text -match '\bnoOcclusion\s*\(|\bisOpaqueCube\s*\([^)]*\)\s*\{[^}]*return\s+false|\bisFullCube\s*\([^)]*\)\s*\{[^}]*return\s+false')
        noCollision = [bool]($Text -match '\bnoCollission\s*\(|\bnoCollision\s*\(|\bgetCollisionShape\b')
        requiresCorrectTool = [bool]($Text -match '\brequiresCorrectToolForDrops\s*\(|\bsetHarvestLevel\b|\bHarvestLevel\b')
        dynamicShape = [bool]($Text -match '\bdynamicShape\s*\(|\bgetShape\b|\bVoxelShape\b|\bAxisAlignedBB\b|getBoundingBox')
        redstone = [bool]($Text -match '\bPOWERED\b|\bENABLED\b|\bredstone\b|\bneighborChanged\b')
    }
    return [pscustomobject][ordered]@{
        hardness = $hardness
        resistance = $resistance
        sound = $sound
        lightLevel = $light
        flags = [pscustomobject]$flags
    }
}
function Add-Result($Rows, [string]$Id, [string]$Subcheck, [string]$Status, [string]$Evidence, $LegacyValue, $PortValue, [string]$LegacySource, [string]$PortSource, $Rule = $null) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "block_properties"
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
function Get-CompareStatus($LegacyValue, $PortValue, [string]$Subcheck, $Rule) {
    $accepted = Get-RuleAcceptedProperties $Rule
    if ($accepted -contains $Subcheck) { return "BLOCK_PROPERTY_RULE_ACCEPTED" }
    $legacyKnown = $null -ne $LegacyValue -and -not [string]::IsNullOrWhiteSpace([string]$LegacyValue)
    $portKnown = $null -ne $PortValue -and -not [string]::IsNullOrWhiteSpace([string]$PortValue)
    if ($legacyKnown -and $portKnown) {
        if ([string]$LegacyValue -eq [string]$PortValue) { return "BLOCK_PROPERTY_MATCH" }
        return "BLOCK_PROPERTY_VALUE_MISMATCH"
    }
    if (-not $legacyKnown -and $portKnown) { return "LEGACY_PROPERTY_SOURCE_MISSING" }
    if ($legacyKnown -and -not $portKnown) { return "BLOCK_PROPERTY_REVIEW_NEEDED" }
    return "BLOCK_PROPERTY_NOT_EVIDENCED"
}
function Add-ScalarCompare($Rows, [string]$Id, [string]$Subcheck, $LegacyValue, $PortValue, [string]$LegacySource, [string]$PortSource, $Rule) {
    $status = Get-CompareStatus $LegacyValue $PortValue $Subcheck $Rule
    $evidence = "legacy=$LegacyValue; port=$PortValue"
    if ($status -eq "BLOCK_PROPERTY_RULE_ACCEPTED") { $evidence = "Difference accepted by block-property-equivalence rule: $(Get-RuleReason $Rule); legacy=$LegacyValue; port=$PortValue" }
    Add-Result $Rows $Id $Subcheck $status $evidence $LegacyValue $PortValue $LegacySource $PortSource $Rule
}
function Add-FlagCompare($Rows, [string]$Id, [string]$Subcheck, [bool]$LegacyValue, [bool]$PortValue, [string]$LegacySource, [string]$PortSource, $Rule) {
    $status = if ((Get-RuleAcceptedProperties $Rule) -contains $Subcheck) {
        "BLOCK_PROPERTY_RULE_ACCEPTED"
    } elseif ($LegacyValue -eq $PortValue) {
        "BLOCK_PROPERTY_MATCH"
    } else {
        "BLOCK_PROPERTY_VALUE_MISMATCH"
    }
    $evidence = "legacy=$LegacyValue; port=$PortValue"
    if ($status -eq "BLOCK_PROPERTY_RULE_ACCEPTED") { $evidence = "Difference accepted by block-property-equivalence rule: $(Get-RuleReason $Rule); legacy=$LegacyValue; port=$PortValue" }
    Add-Result $Rows $Id $Subcheck $status $evidence $LegacyValue $PortValue $LegacySource $PortSource $Rule
}

$legacy = Get-Content -Raw -LiteralPath $LegacyManifestPath | ConvertFrom-Json
$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$rules = New-RuleLookup (Read-RuleDocument $RulesRoot "block-property-equivalence.json")
$blocksRegistryPath = Join-Path $portPath "src/main/java/thaumcraft/common/registry/TCBlocks.java"
if (-not (Test-Path -LiteralPath $blocksRegistryPath -PathType Leaf)) { throw "TCBlocks.java not found: $blocksRegistryPath" }
$blocksText = Get-Content -Raw -LiteralPath $blocksRegistryPath

$legacyById = @{}
foreach ($entry in @($legacy.entries)) {
    $kind = if ($entry.kind) { [string]$entry.kind } else { "" }
    $id = if ($entry.registryId) { [string]$entry.registryId } elseif ($entry.id) { [string]$entry.id } else { "" }
    if ($id.StartsWith("thaumcraft:")) { $id = $id.Substring("thaumcraft:".Length) }
    if ($kind -eq "block" -and $id -and -not $legacyById.ContainsKey($id)) { $legacyById[$id] = $entry }
}

$results = [System.Collections.Generic.List[object]]::new()
$snapshots = [System.Collections.Generic.List[object]]::new()
foreach ($entry in @($port.entries | Where-Object { $_.kind -eq "block" } | Sort-Object registryId)) {
    $id = [string]$entry.registryId
    $rule = Get-Rule $rules "block" $id
    $portText = Get-PortSourceText $entry $blocksText
    $portSource = if ($entry.portClassFile) { [string]$entry.portClassFile } else { "src/main/java/thaumcraft/common/registry/TCBlocks.java" }
    $portSnapshot = Get-BlockPropertySnapshot $portText "port"
    $legacyEntry = if ($legacyById.ContainsKey($id)) { $legacyById[$id] } else { $null }
    $legacyResolved = if ($null -ne $legacyEntry) { Resolve-LegacyText $legacyEntry } else { [pscustomobject]@{ text = $null; source = $null } }
    $legacySnapshot = if ($legacyResolved.text) { Get-BlockPropertySnapshot $legacyResolved.text "legacy" } else { $null }
    $legacySource = if ($legacyResolved.source) { [string]$legacyResolved.source } else { "<missing>" }
    $snapshots.Add([pscustomobject][ordered]@{
        id = "thaumcraft:$id"
        portSource = $portSource
        legacySource = $legacySource
        hasRule = $null -ne $rule
        port = $portSnapshot
        legacy = $legacySnapshot
    })
    if ($null -eq $legacySnapshot) {
        Add-Result $results $id "legacy_source" "LEGACY_PROPERTY_SOURCE_MISSING" "No legacy source text resolved for block property comparison" $null $null $legacySource $portSource $rule
        continue
    }
    Add-ScalarCompare $results $id "hardness" $legacySnapshot.hardness $portSnapshot.hardness $legacySource $portSource $rule
    Add-ScalarCompare $results $id "resistance" $legacySnapshot.resistance $portSnapshot.resistance $legacySource $portSource $rule
    Add-ScalarCompare $results $id "sound" $legacySnapshot.sound $portSnapshot.sound $legacySource $portSource $rule
    Add-ScalarCompare $results $id "light_level" $legacySnapshot.lightLevel $portSnapshot.lightLevel $legacySource $portSource $rule
    foreach ($flag in @("randomTicks", "noOcclusion", "noCollision", "requiresCorrectTool", "dynamicShape", "redstone")) {
        Add-FlagCompare $results $id $flag ([bool]$legacySnapshot.flags.$flag) ([bool]$portSnapshot.flags.$flag) $legacySource $portSource $rule
    }
}

$orderedResults = @($results | Sort-Object status, id, subcheck)
$summaryBySubcheck = @($orderedResults | Group-Object subcheck | Sort-Object Name | ForEach-Object {
    [pscustomobject][ordered]@{
        subcheck = $_.Name
        rows = $_.Count
        matches = @($_.Group | Where-Object status -eq "BLOCK_PROPERTY_MATCH").Count
        valueMismatch = @($_.Group | Where-Object status -eq "BLOCK_PROPERTY_VALUE_MISMATCH").Count
        reviewNeeded = @($_.Group | Where-Object status -eq "BLOCK_PROPERTY_REVIEW_NEEDED").Count
        notEvidenced = @($_.Group | Where-Object status -eq "BLOCK_PROPERTY_NOT_EVIDENCED").Count
        ruleAccepted = @($_.Group | Where-Object status -eq "BLOCK_PROPERTY_RULE_ACCEPTED").Count
        legacyMissing = @($_.Group | Where-Object status -eq "LEGACY_PROPERTY_SOURCE_MISSING").Count
    }
})
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("block_properties")
    policy = "Report-only block property parity scan. It compares source-evidenced hardness, resistance, sound, light and behavior flags. It does not claim full collision/render/gameplay parity without runtime tests/manual review."
    summary = [ordered]@{
        rows = $orderedResults.Count
        matches = @($orderedResults | Where-Object status -eq "BLOCK_PROPERTY_MATCH").Count
        valueMismatch = @($orderedResults | Where-Object status -eq "BLOCK_PROPERTY_VALUE_MISMATCH").Count
        reviewNeeded = @($orderedResults | Where-Object status -eq "BLOCK_PROPERTY_REVIEW_NEEDED").Count
        notEvidenced = @($orderedResults | Where-Object status -eq "BLOCK_PROPERTY_NOT_EVIDENCED").Count
        ruleAccepted = @($orderedResults | Where-Object status -eq "BLOCK_PROPERTY_RULE_ACCEPTED").Count
        legacyMissing = @($orderedResults | Where-Object status -eq "LEGACY_PROPERTY_SOURCE_MISSING").Count
        snapshots = @($snapshots).Count
        bySubcheck = @($summaryBySubcheck)
    }
    snapshots = @($snapshots)
    results = $orderedResults
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block block property parity report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | Matches | Value mismatch | Review needed | Not evidenced | Rule accepted | Legacy missing | Snapshots |")
$lines.Add("|---:|---:|---:|---:|---:|---:|---:|---:|")
$lines.Add("| $($report.summary.rows) | $($report.summary.matches) | $($report.summary.valueMismatch) | $($report.summary.reviewNeeded) | $($report.summary.notEvidenced) | $($report.summary.ruleAccepted) | $($report.summary.legacyMissing) | $($report.summary.snapshots) |")
$lines.Add("")
$lines.Add("## By subcheck")
$lines.Add("")
$lines.Add("| Subcheck | Rows | Matches | Value mismatch | Review needed | Not evidenced | Rule accepted | Legacy missing |")
$lines.Add("|---|---:|---:|---:|---:|---:|---:|---:|")
foreach ($row in $summaryBySubcheck) { $lines.Add("| $($row.subcheck) | $($row.rows) | $($row.matches) | $($row.valueMismatch) | $($row.reviewNeeded) | $($row.notEvidenced) | $($row.ruleAccepted) | $($row.legacyMissing) |") }
$lines.Add("")
$lines.Add("## Review, mismatch and missing rows")
$lines.Add("")
$lines.Add("| ID | Subcheck | Status | Legacy | Port | Evidence |")
$lines.Add("|---|---|---|---|---|---|")
foreach ($row in $orderedResults | Where-Object { $_.status -ne "BLOCK_PROPERTY_MATCH" -and $_.status -ne "BLOCK_PROPERTY_NOT_EVIDENCED" }) {
    $safeEvidence = ([string]$row.evidence).Replace("|", "\|")
    $legacy = if ($null -ne $row.legacyValue) { [string]$row.legacyValue } else { "<null>" }
    $portValue = if ($null -ne $row.portValue) { [string]$row.portValue } else { "<null>" }
    $lines.Add("| ``$($row.id)`` | $($row.subcheck) | $($row.status) | ``$legacy`` | ``$portValue`` | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Block property parity report: $OutputMarkdown"
Write-Output "Rows=$($report.summary.rows), matches=$($report.summary.matches), valueMismatch=$($report.summary.valueMismatch), reviewNeeded=$($report.summary.reviewNeeded), notEvidenced=$($report.summary.notEvidenced), ruleAccepted=$($report.summary.ruleAccepted), legacyMissing=$($report.summary.legacyMissing), snapshots=$($report.summary.snapshots)"
