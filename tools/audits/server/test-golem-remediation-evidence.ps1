param(
    [string]$ReportDirectory = '',
    [string]$ServerLog = '',
    [datetime]$NotBefore = [datetime]::MinValue
)
$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
if (!$ReportDirectory) { $ReportDirectory = Join-Path $repo 'tools/reports/local/review-20261102' }
if (!$ServerLog) { $ServerLog = Join-Path $ReportDirectory 'remaining-final.log' }
$log = Get-Content -LiteralPath $ServerLog -Raw
foreach ($audit in @(
    @{ Name = 'golem-remaining.md'; Exporter = 'TCGolemBehaviorAudit'; Count = 71 },
    @{ Name = 'focus-remaining.md'; Exporter = 'TCFocusMediumEffectAudit'; Count = 16 }
)) {
    $path = Join-Path $ReportDirectory $audit.Name
    if ((Get-Item -LiteralPath $path).LastWriteTime -lt $NotBefore) { throw "Stale report: $path" }
    $report = Get-Content -LiteralPath $path -Raw
    if ($report -match '(?im)\|\s*\*\*FAIL\*\*\s*\||SOME TESTS FAILED') { throw "Failed audit: $path" }
    $passed = [regex]::Matches($report, '(?m)^\| `[^`]+` \| \*\*PASS\*\* \|').Count
    if ($passed -ne $audit.Count -or $report -notmatch 'ALL TESTS PASSED') { throw "Unexpected audit total: $path ($passed)" }
    $marker = '\[' + [regex]::Escape($audit.Exporter) + '\] Exported audit report to .*' + [regex]::Escape($audit.Name) + ' \(allPass=true\)'
    if ($log -notmatch $marker) { throw "This server run did not export $($audit.Name) successfully" }
}
$matrixPath = Join-Path $ReportDirectory 'aggregate-remaining.md'
if ((Get-Item -LiteralPath $matrixPath).LastWriteTime -lt $NotBefore) { throw 'Stale aggregate report' }
if ((Get-Content -LiteralPath $matrixPath -Raw) -notmatch 'Overall: 40 / 40 checks passed' -or $log -notmatch 'checks=40/40 passed') {
    throw 'Missing successful aggregate export in this run'
}
Write-Output 'PASS: fresh server evidence: golems71/71, focus16/16, aggregate40/40. Gradle exit status alone is not used.'
