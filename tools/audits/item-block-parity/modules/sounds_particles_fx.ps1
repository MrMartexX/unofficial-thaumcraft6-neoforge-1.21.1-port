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
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_sound_particle_fx_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_sound_particle_fx_report.md" }

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
    return "Reviewed sound/particle/FX equivalence rule"
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
function Get-RegistrationExpression([string]$RegistryText, [string]$Kind, [string]$Id) {
    if ([string]::IsNullOrWhiteSpace($RegistryText)) { return "" }
    if ($Kind -eq "block") {
        $pattern = 'public\s+static\s+final\s+Supplier<Block>\s+(?<symbol>[A-Z0-9_]+)\s*=\s*BLOCKS\.register\(\s*"' + [regex]::Escape($Id) + '"\s*,\s*(?<expression>.*?)\);'
    } else {
        $pattern = 'public\s+static\s+final\s+Supplier<(?<type>[^>]+)>\s+(?<symbol>[A-Z0-9_]+)\s*=\s*(?<expression>.*?"' + [regex]::Escape($Id) + '".*?);'
    }
    $match = [regex]::Match($RegistryText, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)
    if ($match.Success) { return $match.Groups["expression"].Value.Trim() }
    return ""
}
function Get-PortSourceText($Entry, [string]$BlocksText, [string]$ItemsText) {
    $pieces = [System.Collections.Generic.List[string]]::new()
    $kind = [string]$Entry.kind
    $expr = if ($kind -eq "block") { Get-RegistrationExpression $BlocksText "block" $Entry.registryId } else { Get-RegistrationExpression $ItemsText "item" $Entry.registryId }
    if ($expr) { $pieces.Add($expr) }
    if ($Entry.portClassFile) {
        $classText = Read-RelativeText $portPath ([string]$Entry.portClassFile)
        if ($classText) { $pieces.Add($classText) }
    }
    return ($pieces -join "`n")
}
function Get-RegexValues([string]$Text, [string[]]$Patterns) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return @() }
    $values = [System.Collections.Generic.List[string]]::new()
    foreach ($pattern in $Patterns) {
        foreach ($match in [regex]::Matches($Text, $pattern, [System.Text.RegularExpressions.RegexOptions]::IgnoreCase -bor [System.Text.RegularExpressions.RegexOptions]::Singleline)) {
            if ($match.Groups["value"].Success -and -not [string]::IsNullOrWhiteSpace($match.Groups["value"].Value)) {
                $values.Add($match.Groups["value"].Value.ToLowerInvariant())
            } else {
                $values.Add($match.Value.ToLowerInvariant())
            }
        }
    }
    return @($values | Sort-Object -Unique)
}
function Get-AudioVisualSnapshot([string]$Text, [string]$Side) {
    $soundTypes = Get-RegexValues $Text @(
        'SoundType\.(?<value>[A-Z0-9_]+)',
        '\.sound\s*\(\s*(?<value>[A-Za-z0-9_.$]+)\s*\)',
        'setSoundType\s*\(\s*(?<value>[A-Za-z0-9_.$]+)\s*\)'
    )
    $soundEvents = Get-RegexValues $Text @(
        'SoundEvents\.(?<value>[A-Z0-9_]+)',
        'TCSounds\.(?<value>[A-Z0-9_]+)',
        'SoundsTC\.(?<value>[A-Z0-9_]+)',
        'TCSoundEvents\.(?<value>[A-Z0-9_]+)',
        'playSound\s*\(',
        'level\.playSound\s*\(',
        'world\.playSound\s*\('
    )
    $particles = Get-RegexValues $Text @(
        'ParticleTypes\.(?<value>[A-Z0-9_]+)',
        'TCParticles\.(?<value>[A-Z0-9_]+)',
        'addParticle\s*\(',
        'sendParticles\s*\(',
        'spawnParticle\s*\(',
        'ParticleOptions',
        'DustParticleOptions'
    )
    $fxRefs = Get-RegexValues $Text @(
        'FXDispatcher',
        'thaumcraft\.client\.fx\.[A-Za-z0-9_$.]+',
        '\bFX[A-Za-z0-9_]+\b',
        'ParticleEngine',
        'particleEngine',
        'Minecraft\.getInstance\s*\(\)'
    )
    $flags = [ordered]@{
        hasSoundType = [bool]($soundTypes.Count -gt 0)
        hasSoundEventCall = [bool]($soundEvents.Count -gt 0)
        hasParticleCall = [bool]($particles.Count -gt 0)
        hasClientFx = [bool]($fxRefs.Count -gt 0 -or $Text -match 'net\.minecraft\.client|@OnlyIn\s*\(\s*Dist\.CLIENT|Dist\.CLIENT')
        hasAnimateTick = [bool]($Text -match '\banimateTick\s*\(|\brandomDisplayTick\s*\(')
        hasClientRendererClue = [bool]($Text -match 'BlockEntityRenderer|EntityRenderer|RenderType|PoseStack|client\.render')
    }
    return [pscustomobject][ordered]@{
        soundTypes = @($soundTypes)
        soundEvents = @($soundEvents)
        particles = @($particles)
        fxRefs = @($fxRefs)
        flags = [pscustomobject]$flags
    }
}
function ValuesEqual($A, $B) {
    $left = @($A | Where-Object { $_ } | Sort-Object -Unique)
    $right = @($B | Where-Object { $_ } | Sort-Object -Unique)
    if ($left.Count -ne $right.Count) { return $false }
    for ($i = 0; $i -lt $left.Count; $i++) { if ($left[$i] -ne $right[$i]) { return $false } }
    return $true
}
function Get-ValueStatus($LegacyValue, $PortValue, [string]$Property, $Rule) {
    $accepted = Get-RuleAcceptedProperties $Rule
    $legacyHas = @($LegacyValue).Count -gt 0
    $portHas = @($PortValue).Count -gt 0
    if (-not $legacyHas -and -not $portHas) { return "SOUND_FX_NOT_EVIDENCED" }
    if (ValuesEqual $LegacyValue $PortValue) { return "SOUND_FX_MATCH" }
    if ($accepted -contains $Property) { return "SOUND_FX_RULE_ACCEPTED" }
    return "SOUND_FX_REVIEW_NEEDED"
}
function Get-FlagStatus([bool]$LegacyValue, [bool]$PortValue, [string]$Property, $Rule) {
    $accepted = Get-RuleAcceptedProperties $Rule
    if (-not $LegacyValue -and -not $PortValue) { return "SOUND_FX_NOT_EVIDENCED" }
    if ($LegacyValue -eq $PortValue) { return "SOUND_FX_MATCH" }
    if ($accepted -contains $Property) { return "SOUND_FX_RULE_ACCEPTED" }
    return "SOUND_FX_REVIEW_NEEDED"
}
function Add-Result($Rows, [string]$Kind, [string]$Id, [string]$Subcheck, [string]$Status, $LegacyValue, $PortValue, [string]$Evidence, [string]$LegacySource, [string]$PortSource, $Rule = $null) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "sounds_particles"
        subcheck = $Subcheck
        kind = $Kind
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

$legacy = Get-Content -Raw -LiteralPath $LegacyManifestPath | ConvertFrom-Json
$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$blocksPath = Join-Path $portPath "src/main/java/thaumcraft/common/registry/TCBlocks.java"
$itemsPath = Join-Path $portPath "src/main/java/thaumcraft/common/registry/TCItems.java"
$blocksText = if (Test-Path -LiteralPath $blocksPath -PathType Leaf) { Get-Content -Raw -LiteralPath $blocksPath } else { "" }
$itemsText = if (Test-Path -LiteralPath $itemsPath -PathType Leaf) { Get-Content -Raw -LiteralPath $itemsPath } else { "" }
$rules = New-RuleLookup (Read-RuleDocument $RulesRoot "sound-particle-equivalence.json")

$legacyByKindId = @{}
foreach ($entry in @($legacy.entries)) {
    $kind = if ($entry.kind) { [string]$entry.kind } else { "" }
    $id = Get-EntryId $entry
    if (-not [string]::IsNullOrWhiteSpace($kind) -and -not [string]::IsNullOrWhiteSpace($id)) { $legacyByKindId["${kind}:$id"] = $entry }
}

$results = [System.Collections.Generic.List[object]]::new()
$snapshots = [System.Collections.Generic.List[object]]::new()
$selectedEntries = @($port.entries | Where-Object { $_.kind -in @("block", "item") } | Sort-Object kind, registryId)
foreach ($entry in $selectedEntries) {
    $kind = [string]$entry.kind
    $id = [string]$entry.registryId
    $legacyEntry = if ($legacyByKindId.ContainsKey("${kind}:$id")) { $legacyByKindId["${kind}:$id"] } else { $null }
    $legacyResolved = if ($null -ne $legacyEntry) { Resolve-LegacyText $legacyEntry } else { [pscustomobject]@{ text = $null; source = $null } }
    $portText = Get-PortSourceText $entry $blocksText $itemsText
    $legacySource = if ($legacyResolved.source) { [string]$legacyResolved.source } else { "<missing>" }
    $portSource = if ($entry.portClassFile) { [string]$entry.portClassFile } elseif ($kind -eq "block") { "src/main/java/thaumcraft/common/registry/TCBlocks.java" } else { "src/main/java/thaumcraft/common/registry/TCItems.java" }
    $rule = Get-Rule $rules $kind $id

    if ($null -eq $legacyResolved.text) {
        Add-Result $results $kind $id "legacy_sound_particle_source" "LEGACY_SOURCE_MISSING" $null $null "Legacy source text not found for sound/particle/FX scan" $legacySource $portSource $rule
        continue
    }

    $legacySnapshot = Get-AudioVisualSnapshot $legacyResolved.text "legacy"
    $portSnapshot = Get-AudioVisualSnapshot $portText "port"
    $snapshots.Add([pscustomobject][ordered]@{
        kind = $kind
        id = "thaumcraft:$id"
        legacySource = $legacySource
        portSource = $portSource
        legacy = $legacySnapshot
        port = $portSnapshot
        hasRule = $null -ne $rule
    })

    $comparisons = @(
        @{ name = "sound_type_refs"; legacy = @($legacySnapshot.soundTypes); port = @($portSnapshot.soundTypes) },
        @{ name = "sound_event_refs"; legacy = @($legacySnapshot.soundEvents); port = @($portSnapshot.soundEvents) },
        @{ name = "particle_refs"; legacy = @($legacySnapshot.particles); port = @($portSnapshot.particles) },
        @{ name = "client_fx_refs"; legacy = @($legacySnapshot.fxRefs); port = @($portSnapshot.fxRefs) }
    )
    foreach ($comparison in $comparisons) {
        $status = Get-ValueStatus $comparison.legacy $comparison.port $comparison.name $rule
        $evidence = "legacy=$(Format-List $comparison.legacy); port=$(Format-List $comparison.port)"
        if ($status -eq "SOUND_FX_RULE_ACCEPTED") { $evidence = "$evidence; accepted by rule: $(Get-RuleReason $rule)" }
        Add-Result $results $kind $id $comparison.name $status @($comparison.legacy) @($comparison.port) $evidence $legacySource $portSource $rule
    }

    foreach ($flagName in @("hasSoundType", "hasSoundEventCall", "hasParticleCall", "hasClientFx", "hasAnimateTick", "hasClientRendererClue")) {
        $legacyFlag = [bool]$legacySnapshot.flags.$flagName
        $portFlag = [bool]$portSnapshot.flags.$flagName
        $status = Get-FlagStatus $legacyFlag $portFlag $flagName $rule
        $evidence = "legacy=$legacyFlag; port=$portFlag"
        if ($status -eq "SOUND_FX_RULE_ACCEPTED") { $evidence = "$evidence; accepted by rule: $(Get-RuleReason $rule)" }
        Add-Result $results $kind $id $flagName $status $legacyFlag $portFlag $evidence $legacySource $portSource $rule
    }
}

$orderedResults = @($results | Sort-Object status, kind, id, subcheck)
$summaryBySubcheck = @($orderedResults | Group-Object subcheck | Sort-Object Name | ForEach-Object {
    [pscustomobject][ordered]@{
        subcheck = $_.Name
        rows = $_.Count
        matches = @($_.Group | Where-Object status -eq "SOUND_FX_MATCH").Count
        reviewNeeded = @($_.Group | Where-Object status -eq "SOUND_FX_REVIEW_NEEDED").Count
        notEvidenced = @($_.Group | Where-Object status -eq "SOUND_FX_NOT_EVIDENCED").Count
        ruleAccepted = @($_.Group | Where-Object status -eq "SOUND_FX_RULE_ACCEPTED").Count
        legacyMissing = @($_.Group | Where-Object status -eq "LEGACY_SOURCE_MISSING").Count
    }
})
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("sounds_particles")
    policy = "Report-only sound/particle/FX reference scan. It records source-evidenced SoundType, sound event calls, particle calls, client FX references and animation-tick clues. It does not claim audible or visual runtime parity without client/server/manual evidence."
    summary = [ordered]@{
        rows = $orderedResults.Count
        matches = @($orderedResults | Where-Object status -eq "SOUND_FX_MATCH").Count
        reviewNeeded = @($orderedResults | Where-Object status -eq "SOUND_FX_REVIEW_NEEDED").Count
        notEvidenced = @($orderedResults | Where-Object status -eq "SOUND_FX_NOT_EVIDENCED").Count
        ruleAccepted = @($orderedResults | Where-Object status -eq "SOUND_FX_RULE_ACCEPTED").Count
        legacyMissing = @($orderedResults | Where-Object status -eq "LEGACY_SOURCE_MISSING").Count
        snapshots = @($snapshots).Count
        bySubcheck = @($summaryBySubcheck)
    }
    snapshots = @($snapshots | Sort-Object kind, id)
    results = $orderedResults
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 20 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block sound particle FX report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | Matches | Review needed | Not evidenced | Rule accepted | Legacy missing | Snapshots |")
$lines.Add("|---:|---:|---:|---:|---:|---:|---:|")
$lines.Add("| $($report.summary.rows) | $($report.summary.matches) | $($report.summary.reviewNeeded) | $($report.summary.notEvidenced) | $($report.summary.ruleAccepted) | $($report.summary.legacyMissing) | $($report.summary.snapshots) |")
$lines.Add("")
$lines.Add("## By subcheck")
$lines.Add("")
$lines.Add("| Subcheck | Rows | Matches | Review needed | Not evidenced | Rule accepted | Legacy missing |")
$lines.Add("|---|---:|---:|---:|---:|---:|---:|")
foreach ($row in $summaryBySubcheck) { $lines.Add("| $($row.subcheck) | $($row.rows) | $($row.matches) | $($row.reviewNeeded) | $($row.notEvidenced) | $($row.ruleAccepted) | $($row.legacyMissing) |") }
$lines.Add("")
$lines.Add("## Review-needed / rule / missing rows")
$lines.Add("")
$lines.Add("| Kind | ID | Subcheck | Status | Legacy | Port | Evidence |")
$lines.Add("|---|---|---|---|---|---|---|")
foreach ($row in $orderedResults | Where-Object { $_.status -ne "SOUND_FX_MATCH" -and $_.status -ne "SOUND_FX_NOT_EVIDENCED" }) {
    $safeEvidence = ([string]$row.evidence).Replace("|", "\|")
    $legacyValue = if ($row.legacyValue -is [array]) { Format-List $row.legacyValue } else { [string]$row.legacyValue }
    $portValue = if ($row.portValue -is [array]) { Format-List $row.portValue } else { [string]$row.portValue }
    $lines.Add("| $($row.kind) | ``$($row.id)`` | $($row.subcheck) | $($row.status) | $legacyValue | $portValue | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Sound/particle/FX report: $OutputMarkdown"
Write-Output "Rows=$($report.summary.rows), matches=$($report.summary.matches), reviewNeeded=$($report.summary.reviewNeeded), notEvidenced=$($report.summary.notEvidenced), ruleAccepted=$($report.summary.ruleAccepted), legacyMissing=$($report.summary.legacyMissing), snapshots=$($report.summary.snapshots)"
