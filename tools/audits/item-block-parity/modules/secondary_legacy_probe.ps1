[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$PrimaryManifestPath,
    [string]$SecondaryLegacyRoot = "03_self_decompiled_check/vineflower_thaumcraft6",
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$secondaryPath = Join-Path $RepoRoot $SecondaryLegacyRoot
if (-not (Test-Path -LiteralPath $secondaryPath -PathType Container)) {
    throw "Secondary legacy source root not found: $secondaryPath"
}
if (-not (Test-Path -LiteralPath $PrimaryManifestPath -PathType Leaf)) {
    throw "Primary legacy manifest not found: $PrimaryManifestPath"
}
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/legacy_secondary_probe_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/legacy_secondary_probe_report.md" }

function ConvertTo-RelativeSecondaryPath([string]$FullPath) {
    return [System.IO.Path]::GetRelativePath($secondaryPath, $FullPath).Replace("\", "/")
}
function Get-SimpleClassName([string]$ClassName) {
    if ([string]::IsNullOrWhiteSpace($ClassName)) { return "" }
    $value = $ClassName -replace '\$.*$', ''
    if ($value.Contains(".")) { return $value.Split(".")[-1] }
    return $value
}

$primary = Get-Content -Raw -LiteralPath $PrimaryManifestPath | ConvertFrom-Json
$secondaryJavaFiles = @(Get-ChildItem -LiteralPath $secondaryPath -Recurse -Filter "*.java" -File)
$classIndex = @{}
foreach ($file in $secondaryJavaFiles) {
    $className = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
    if (-not $classIndex.ContainsKey($className)) { $classIndex[$className] = [System.Collections.Generic.List[string]]::new() }
    $classIndex[$className].Add((ConvertTo-RelativeSecondaryPath $file.FullName))
}

$results = [System.Collections.Generic.List[object]]::new()
foreach ($entry in @($primary.entries)) {
    if ([string]::IsNullOrWhiteSpace($entry.legacyClass)) { continue }
    $simple = Get-SimpleClassName $entry.legacyClass
    $secondaryMatches = if ($classIndex.ContainsKey($simple)) { @($classIndex[$simple]) } else { @() }
    $status = "SECONDARY_DIFF_REVIEW_NEEDED"
    $evidence = "No matching secondary class file for $simple"
    if (-not $entry.legacyClassFile) {
        $status = "PRIMARY_SOURCE_GAP"
        $evidence = "Primary manifest did not resolve a class file for $($entry.legacyClass)"
    } elseif ($secondaryMatches.Count -gt 0) {
        $status = "SECONDARY_MATCH"
        $evidence = "Secondary class file(s): $($secondaryMatches -join ', ')"
    }
    $results.Add([pscustomobject][ordered]@{
        kind = $entry.kind
        id = "thaumcraft:$($entry.registryId)"
        legacyClass = $entry.legacyClass
        legacyClassFile = $entry.legacyClassFile
        secondaryClassFiles = @($secondaryMatches)
        status = $status
        evidence = $evidence
    })
}

$orderedResults = @($results | Sort-Object status, kind, id)
$summary = [ordered]@{
    entries = $orderedResults.Count
    secondaryMatches = @($orderedResults | Where-Object status -eq "SECONDARY_MATCH").Count
    secondaryReviewNeeded = @($orderedResults | Where-Object status -eq "SECONDARY_DIFF_REVIEW_NEEDED").Count
    primarySourceGaps = @($orderedResults | Where-Object status -eq "PRIMARY_SOURCE_GAP").Count
    secondaryJavaFiles = $secondaryJavaFiles.Count
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    primaryManifest = $PrimaryManifestPath
    secondarySource = $SecondaryLegacyRoot.Replace("\", "/")
    policy = "Probe-only. Secondary source never replaces the primary manifest automatically. Review-needed rows require manual source inspection."
    summary = $summary
    results = $orderedResults
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Secondary legacy source probe")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Result | Count |")
$lines.Add("|---|---:|")
$lines.Add("| SECONDARY_MATCH | $($summary.secondaryMatches) |")
$lines.Add("| SECONDARY_DIFF_REVIEW_NEEDED | $($summary.secondaryReviewNeeded) |")
$lines.Add("| PRIMARY_SOURCE_GAP | $($summary.primarySourceGaps) |")
$lines.Add("| Secondary Java files | $($summary.secondaryJavaFiles) |")
$lines.Add("")
$lines.Add("## Review-needed rows")
$lines.Add("")
$lines.Add("| Kind | ID | Status | Class | Evidence |")
$lines.Add("|---|---|---|---|---|")
foreach ($row in $orderedResults | Where-Object { $_.status -ne "SECONDARY_MATCH" }) {
    $safeEvidence = $row.evidence.Replace("|", "\|")
    $lines.Add("| $($row.kind) | ``$($row.id)`` | $($row.status) | ``$($row.legacyClass)`` | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Secondary legacy probe report: $OutputMarkdown"
Write-Output "SECONDARY_MATCH=$($summary.secondaryMatches), REVIEW_NEEDED=$($summary.secondaryReviewNeeded), PRIMARY_SOURCE_GAP=$($summary.primarySourceGaps), secondaryJavaFiles=$($summary.secondaryJavaFiles)"
