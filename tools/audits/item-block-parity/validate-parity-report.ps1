[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$LegacyManifest,
    [Parameter(Mandatory = $true)][string]$PortManifest,
    [Parameter(Mandatory = $true)][string]$ReportPath
)

$ErrorActionPreference = "Stop"
foreach ($path in @($LegacyManifest, $PortManifest, $ReportPath)) {
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { throw "Parity JSON not found: $path" }
}

$legacy = Get-Content -Raw -LiteralPath $LegacyManifest | ConvertFrom-Json
$port = Get-Content -Raw -LiteralPath $PortManifest | ConvertFrom-Json
$report = Get-Content -Raw -LiteralPath $ReportPath | ConvertFrom-Json
foreach ($document in @($legacy, $port, $report)) {
    if ($document.schemaVersion -ne 1) { throw "Unsupported parity schema version: $($document.schemaVersion)" }
}
if (-not $legacy.sourceFiles -or -not $legacy.entries) { throw "Legacy manifest is structurally incomplete." }
if (-not $port.sourceFiles -or -not $port.entries) { throw "Port manifest is structurally incomplete." }
if (-not $report.implementedChecks -or $null -eq $report.results -or -not $report.summary) { throw "Parity report is structurally incomplete." }

$allowedStatuses = @("PASS", "MISSING", "EXTRA", "RENAMED_WITH_MAPPING", "VARIANT_MAPPED", "DEFERRED", "INTENTIONAL_MISSING", "NOT_EVALUATED")
$seen = @{}
foreach ($result in $report.results) {
    if ($result.status -notin $allowedStatuses) { throw "Unknown parity status '$($result.status)' for $($result.id)." }
    $key = "$($result.kind)|$($result.id)|$($result.check)"
    if ($seen.ContainsKey($key)) { throw "Duplicate parity result: $key" }
    $seen[$key] = $true
}

$pass = @($report.results | Where-Object status -eq "PASS").Count
$mapped = @($report.results | Where-Object status -eq "RENAMED_WITH_MAPPING").Count
$missing = @($report.results | Where-Object status -eq "MISSING").Count
if ($report.summary.results -ne $report.results.Count -or $report.summary.pass -ne $pass -or $report.summary.renamed -ne $mapped -or $report.summary.missing -ne $missing) {
    throw "Parity report summary does not match its result rows."
}
$overlap = @($report.implementedChecks | Where-Object { $_ -in $report.notEvaluatedChecks })
if ($overlap.Count -gt 0) { throw "Checks cannot be both implemented and not evaluated: $($overlap -join ', ')" }

Write-Output "Validated item/block parity manifests and report: legacy=$($legacy.entries.Count), port=$($port.entries.Count), results=$($report.results.Count)."
