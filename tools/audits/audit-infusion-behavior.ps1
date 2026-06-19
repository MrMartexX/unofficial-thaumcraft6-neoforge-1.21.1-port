param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$OutputPath = '',
    [string]$WorldName = 'tc_infusion_behavior_audit',
    [int]$ServerPort = 0,
    [int]$TimeoutSeconds = 420
)

$ErrorActionPreference = 'Stop'

$repo = Resolve-Path -LiteralPath $RepoRoot
$moduleRoot = Join-Path $repo '05_neoforge_port'
$gradleBat = Join-Path $moduleRoot 'gradlew.bat'
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $repo 'tools/reports/local/infusion/thaumcraft_1_21_infusion_behavior_audit.md'
}
$outputFullPath = [System.IO.Path]::GetFullPath($OutputPath)
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputFullPath) | Out-Null

Push-Location $moduleRoot
try {
    & $gradleBat runServer --no-daemon "-PtcRunServerWorld=$WorldName" "-PtcRunServerPort=$ServerPort" "-PtcInfusionBehaviorAudit=true" "-PtcInfusionBehaviorAuditPath=$outputFullPath"
    if ($LASTEXITCODE -ne 0) {
        throw "Infusion behavior audit runServer failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}

if (-not (Test-Path -LiteralPath $outputFullPath)) {
    throw "Infusion behavior audit did not write expected output: $outputFullPath"
}

Write-Host "Infusion behavior audit written to $outputFullPath"
