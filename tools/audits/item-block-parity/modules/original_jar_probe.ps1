[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$OriginalJar,
    [Parameter(Mandatory = $true)][string]$LegacyRoot,
    [Parameter(Mandatory = $true)][string]$SecondaryLegacyRoot,
    [Parameter(Mandatory = $true)][string]$PortRoot,
    [string]$LegacyManifestPath,
    [string]$PortManifestPath,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = 'Stop'
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path

function Resolve-WithinRepo([string]$PathValue) {
    if ([string]::IsNullOrWhiteSpace($PathValue)) { return '' }
    if ([System.IO.Path]::IsPathRooted($PathValue)) {
        if (Test-Path -LiteralPath $PathValue) { return (Resolve-Path -LiteralPath $PathValue).Path }
        return $PathValue
    }
    $candidate = Join-Path $RepoRoot $PathValue
    if (Test-Path -LiteralPath $candidate) { return (Resolve-Path -LiteralPath $candidate).Path }
    return $candidate
}

$OriginalJar = Resolve-WithinRepo $OriginalJar
$LegacyRoot = Resolve-WithinRepo $LegacyRoot
$SecondaryLegacyRoot = Resolve-WithinRepo $SecondaryLegacyRoot
$PortRoot = Resolve-WithinRepo $PortRoot
if ($LegacyManifestPath) { $LegacyManifestPath = Resolve-WithinRepo $LegacyManifestPath }
if ($PortManifestPath) { $PortManifestPath = Resolve-WithinRepo $PortManifestPath }
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot 'tools/audits/item-block-parity/rules' }
$reportRoot = Join-Path $RepoRoot 'tools/reports/local/item-block-parity'
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_original_jar_probe_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_original_jar_probe_report.md' }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return '' }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace('\', '/')
}

function Escape-MarkdownCell([object]$Value) {
    if ($null -eq $Value) { return '' }
    $s = if ($Value -is [array]) { ($Value -join '; ') } else { [string]$Value }
    return $s.Replace('|', '\|').Replace("`r", ' ').Replace("`n", ' ')
}

function Normalize-ClassKey([string]$Value) {
    if ([string]::IsNullOrWhiteSpace($Value)) { return '' }
    $s = $Value.Replace('\', '/').Trim()
    if ($s.EndsWith('.class')) { $s = $s.Substring(0, $s.Length - 6) }
    if ($s.EndsWith('.java')) { $s = $s.Substring(0, $s.Length - 5) }
    if ($s.Contains('$')) { $s = $s.Substring(0, $s.IndexOf('$')) }
    $s = $s -replace '^src/main/java/', ''
    $s = $s -replace '^src/generated/java/', ''
    return $s.ToLowerInvariant()
}

function Get-SourceKey {
    param([string]$FullPath, [string]$Root, [string]$Text)
    $package = ''
    $className = [System.IO.Path]::GetFileNameWithoutExtension($FullPath)
    $m = [regex]::Match($Text, '(?m)^\s*package\s+([A-Za-z0-9_.]+)\s*;')
    if ($m.Success) { $package = $m.Groups[1].Value.Replace('.', '/') }
    if ($package) { return Normalize-ClassKey ("$package/$className") }
    $relative = [System.IO.Path]::GetRelativePath($Root, $FullPath).Replace('\', '/')
    return Normalize-ClassKey $relative
}

function Get-JavaSourceIndex([string]$Root) {
    $files = @()
    $map = @{}
    if (-not (Test-Path -LiteralPath $Root -PathType Container)) {
        return [pscustomobject]@{ files = @(); map = $map }
    }
    $all = @(Get-ChildItem -LiteralPath $Root -Recurse -File -Include '*.java' -ErrorAction SilentlyContinue)
    foreach ($file in $all) {
        $text = Get-Content -Raw -LiteralPath $file.FullName
        $key = Get-SourceKey -FullPath $file.FullName -Root $Root -Text $text
        if (-not $map.ContainsKey($key)) { $map[$key] = @() }
        $map[$key] += [pscustomobject]@{
            path = ConvertTo-RelativeRepoPath $file.FullName
            length = $text.Length
        }
        $files += $file
    }
    return [pscustomobject]@{ files = $files; map = $map }
}

function Get-MatchedTerms {
    param([string]$Text, [string[]]$Terms)
    $hits = @()
    $lower = if ($Text) { $Text.ToLowerInvariant() } else { '' }
    foreach ($term in @($Terms)) {
        if ([string]::IsNullOrWhiteSpace($term)) { continue }
        if ($lower.Contains(([string]$term).ToLowerInvariant())) { $hits += [string]$term }
    }
    return @($hits | Select-Object -Unique)
}

function Add-Row {
    param(
        [ref]$Rows,
        [string]$Status,
        [string]$Severity,
        [string]$Classification,
        [string]$Subject,
        [string]$Path,
        [string[]]$Evidence = @(),
        [string]$Recommendation = ''
    )
    $Rows.Value += [pscustomobject][ordered]@{
        check = 'original_jar_probe'
        status = $Status
        severity = $Severity
        classification = $Classification
        subject = $Subject
        path = $Path
        evidence = @($Evidence)
        recommendation = $Recommendation
    }
}

function Read-Rules {
    $default = [pscustomobject]@{
        candidateIdTerms = @('thaumcraft','wand','focus','golem','aura','aspect','research','essentia','crucible','infusion','arcane','caster','seal','void','thaumium','taint','eldritch')
        resourceExtensions = @('.json','.mcmeta','.png','.ogg','.lang','.txt','.cfg','.properties')
    }
    $rulesPath = Join-Path $RulesRoot 'original-jar-probe-rules.json'
    if (-not (Test-Path -LiteralPath $rulesPath -PathType Leaf)) { return $default }
    $raw = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
    if (-not $raw.PSObject.Properties['candidateIdTerms']) { $raw | Add-Member -NotePropertyName candidateIdTerms -NotePropertyValue $default.candidateIdTerms }
    if (-not $raw.PSObject.Properties['resourceExtensions']) { $raw | Add-Member -NotePropertyName resourceExtensions -NotePropertyValue $default.resourceExtensions }
    return $raw
}

$rules = Read-Rules
$rows = @()
$jarExists = Test-Path -LiteralPath $OriginalJar -PathType Leaf
$jarEntries = @()
$classEntries = @()
$resourceEntries = @()
$thaumcraftClassEntries = @()
$manifestCandidateIds = 0
$jarClassMap = @{}

if (-not $jarExists) {
    Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'medium' -Classification 'jar_missing' -Subject 'original jar missing' -Path (ConvertTo-RelativeRepoPath $OriginalJar) -Evidence @('original jar path does not exist') -Recommendation 'Place the original Thaumcraft jar at the configured OriginalJar path or update -OriginalJar before treating jar evidence as authoritative.'
} else {
    Add-Type -AssemblyName System.IO.Compression.FileSystem | Out-Null
    $zip = [System.IO.Compression.ZipFile]::OpenRead($OriginalJar)
    try {
        $jarEntries = @($zip.Entries | Where-Object { -not [string]::IsNullOrWhiteSpace($_.FullName) -and -not $_.FullName.EndsWith('/') })
        $classEntries = @($jarEntries | Where-Object { $_.FullName.ToLowerInvariant().EndsWith('.class') })
        $resourceEntries = @($jarEntries | Where-Object { -not $_.FullName.ToLowerInvariant().EndsWith('.class') })
        $thaumcraftClassEntries = @($classEntries | Where-Object { $_.FullName.ToLowerInvariant().Contains('thaumcraft') })
        Add-Row -Rows ([ref]$rows) -Status 'pass' -Severity 'info' -Classification 'jar_readable' -Subject 'original jar readable' -Path (ConvertTo-RelativeRepoPath $OriginalJar) -Evidence @("entries=$($jarEntries.Count)", "classes=$($classEntries.Count)", "resources=$($resourceEntries.Count)") -Recommendation 'Use this report as jar-backed evidence, not as a direct gameplay parity gate.'
        foreach ($entry in $classEntries) {
            $key = Normalize-ClassKey $entry.FullName
            if (-not $jarClassMap.ContainsKey($key)) { $jarClassMap[$key] = @() }
            $jarClassMap[$key] += $entry.FullName
        }
        foreach ($entry in @($resourceEntries | Where-Object { (Get-MatchedTerms -Text $_.FullName -Terms $rules.candidateIdTerms).Count -gt 0 } | Select-Object -First 200)) {
            $terms = Get-MatchedTerms -Text $entry.FullName -Terms $rules.candidateIdTerms
            $manifestCandidateIds++
            Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'low' -Classification 'jar_resource_candidate' -Subject $entry.FullName -Path $entry.FullName -Evidence @($terms) -Recommendation 'Review original jar resource against generated/data/resource parity reports when class/resource authority differs.'
        }
    } finally {
        $zip.Dispose()
    }
}

$primary = Get-JavaSourceIndex -Root $LegacyRoot
$secondary = Get-JavaSourceIndex -Root $SecondaryLegacyRoot
$port = Get-JavaSourceIndex -Root $PortRoot
$legacyKeys = [System.Collections.Generic.HashSet[string]]::new()
foreach ($k in @($primary.map.Keys)) { [void]$legacyKeys.Add([string]$k) }
foreach ($k in @($secondary.map.Keys)) { [void]$legacyKeys.Add([string]$k) }
$portKeys = [System.Collections.Generic.HashSet[string]]::new()
foreach ($k in @($port.map.Keys)) { [void]$portKeys.Add([string]$k) }

$jarKeys = @($jarClassMap.Keys | Sort-Object)
$legacyKeysArray = @($legacyKeys | Sort-Object)
$jarOnlyClasses = 0
$legacyNoJarClasses = 0
$portJarOverlapClasses = 0

foreach ($key in $jarKeys) {
    $inLegacy = $legacyKeys.Contains($key)
    $inPort = $portKeys.Contains($key)
    if ($inPort) { $portJarOverlapClasses++ }
    if (-not $inLegacy -and $key.Contains('thaumcraft')) {
        $jarOnlyClasses++
        Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'medium' -Classification 'jar_class_no_legacy_source' -Subject $key -Path (($jarClassMap[$key] | Select-Object -First 1) -as [string]) -Evidence @('original jar class has no primary/secondary source key match') -Recommendation 'Check whether decompile omitted this class, whether it is an inner/synthetic class, or whether source authority needs a manual override.'
    }
}

foreach ($key in $legacyKeysArray) {
    if (-not $jarClassMap.ContainsKey($key) -and $key.Contains('thaumcraft')) {
        $legacyNoJarClasses++
        $samplePath = ''
        if ($primary.map.ContainsKey($key)) { $samplePath = @($primary.map[$key])[0].path }
        elseif ($secondary.map.ContainsKey($key)) { $samplePath = @($secondary.map[$key])[0].path }
        Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'medium' -Classification 'legacy_source_no_jar_class' -Subject $key -Path $samplePath -Evidence @('legacy source key has no original jar class match') -Recommendation 'Confirm whether this source is auxiliary/decompiler-only, renamed, generated, or not shipped in the original jar.'
    }
}

foreach ($key in @($portKeys | Sort-Object)) {
    if ($jarClassMap.ContainsKey($key)) {
        Add-Row -Rows ([ref]$rows) -Status 'reviewNeeded' -Severity 'low' -Classification 'port_class_matches_original_jar_key' -Subject $key -Path (@($port.map[$key])[0].path) -Evidence @('port source class key overlaps original jar class key') -Recommendation 'Review exact semantics separately; key overlap alone does not prove behavior parity.'
    }
}

$passCount = @($rows | Where-Object { $_.status -eq 'pass' }).Count
$reviewCount = @($rows | Where-Object { $_.status -eq 'reviewNeeded' }).Count
$errorCount = @($rows | Where-Object { $_.status -eq 'error' }).Count
$summary = [pscustomobject][ordered]@{
    rows = @($rows).Count
    pass = $passCount
    reviewNeeded = $reviewCount
    errors = $errorCount
    jarExists = [bool]$jarExists
    jarEntries = @($jarEntries).Count
    jarClassEntries = @($classEntries).Count
    jarResourceEntries = @($resourceEntries).Count
    thaumcraftClassEntries = @($thaumcraftClassEntries).Count
    primarySourceFiles = @($primary.files).Count
    secondarySourceFiles = @($secondary.files).Count
    portSourceFiles = @($port.files).Count
    jarOnlyClasses = $jarOnlyClasses
    legacyNoJarClasses = $legacyNoJarClasses
    portJarOverlapClasses = $portJarOverlapClasses
    manifestCandidateIds = $manifestCandidateIds
}

$report = [pscustomobject][ordered]@{
    schemaVersion = 1
    generatedAtUtc = (Get-Date).ToUniversalTime().ToString('o')
    check = 'original_jar_probe'
    checks = @($Checks)
    summary = $summary
    results = @($rows)
}

$reportDir = Split-Path -Parent $OutputJson
if (-not (Test-Path -LiteralPath $reportDir -PathType Container)) {
    New-Item -ItemType Directory -Path $reportDir -Force | Out-Null
}
$report | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $OutputJson -Encoding UTF8

$md = @()
$md += '# Original jar probe report'
$md += ''
$md += '| Metric | Value |'
$md += '|---|---:|'
foreach ($prop in $summary.PSObject.Properties) {
    $md += "| $($prop.Name) | $($prop.Value) |"
}
$md += ''
$md += '| Status | Severity | Classification | Subject | Path | Evidence | Recommendation |'
$md += '|---|---|---|---|---|---|---|'
foreach ($row in $rows) {
    $md += "| $(Escape-MarkdownCell $row.status) | $(Escape-MarkdownCell $row.severity) | $(Escape-MarkdownCell $row.classification) | $(Escape-MarkdownCell $row.subject) | $(Escape-MarkdownCell $row.path) | $(Escape-MarkdownCell $row.evidence) | $(Escape-MarkdownCell $row.recommendation) |"
}
$md -join "`n" | Set-Content -LiteralPath $OutputMarkdown -Encoding UTF8

Write-Output "Original jar probe report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors), jarExists=$($summary.jarExists), jarEntries=$($summary.jarEntries), jarClassEntries=$($summary.jarClassEntries), jarResourceEntries=$($summary.jarResourceEntries), thaumcraftClassEntries=$($summary.thaumcraftClassEntries), primarySourceFiles=$($summary.primarySourceFiles), secondarySourceFiles=$($summary.secondarySourceFiles), portSourceFiles=$($summary.portSourceFiles), jarOnlyClasses=$($summary.jarOnlyClasses), legacyNoJarClasses=$($summary.legacyNoJarClasses), portJarOverlapClasses=$($summary.portJarOverlapClasses), manifestCandidateIds=$($summary.manifestCandidateIds)"
