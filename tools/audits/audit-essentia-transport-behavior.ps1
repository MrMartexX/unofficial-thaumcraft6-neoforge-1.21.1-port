param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$OutputPath = '',
    [string]$WorldName = 'tc_essentia_transport_behavior_audit',
    [int]$ServerPort = 0
)

$ErrorActionPreference = 'Stop'
$repo = Resolve-Path -LiteralPath $RepoRoot
$moduleRoot = Join-Path $repo '05_neoforge_port'
$gradleBat = Join-Path $moduleRoot 'gradlew.bat'
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $repo 'tools/reports/local/essentia/thaumcraft_1_21_essentia_transport_behavior_audit.md'
}
$outputFullPath = [System.IO.Path]::GetFullPath($OutputPath)
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputFullPath) | Out-Null

Push-Location $moduleRoot
try {
    & $gradleBat runServer --no-daemon "-PtcRunServerWorld=$WorldName" "-PtcRunServerPort=$ServerPort" `
        '-PtcEssentiaTransportBehaviorAudit=true' "-PtcEssentiaTransportBehaviorAuditPath=$outputFullPath"
    if ($LASTEXITCODE -ne 0) {
        throw "Essentia transport behavior audit runServer failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}

if (-not (Test-Path -LiteralPath $outputFullPath)) {
    throw "Essentia transport behavior audit did not write expected output: $outputFullPath"
}
$failedLine = Select-String -LiteralPath $outputFullPath -Pattern '^\| Failed \| ([0-9]+) \|$' | Select-Object -First 1
if ($null -eq $failedLine) {
    throw "Essentia transport behavior audit report has no parseable Failed count: $outputFullPath"
}
$failed = [int]$failedLine.Matches[0].Groups[1].Value
if ($failed -ne 0) {
    throw "Essentia transport behavior audit reported $failed failed checks: $outputFullPath"
}
Write-Host "Essentia transport behavior audit written to $outputFullPath"
