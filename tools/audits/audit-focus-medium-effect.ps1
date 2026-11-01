param(
    [string]$ProjectRoot = (Resolve-Path "$PSScriptRoot\..\..").Path,
    [string]$WorldName = "tc_focus_medium_effect_audit",
    [int]$ServerPort = 25586,
    [string]$OutputPath = ""
)

$ErrorActionPreference = "Stop"

$portRoot = Join-Path $ProjectRoot "05_neoforge_port"
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $ProjectRoot "06_docs\audits\generated\thaumcraft_1_21_focus_medium_effect_audit.md"
}

Push-Location $portRoot
try {
    & .\gradlew.bat runServer --no-daemon `
        "-PtcRunServerWorld=$WorldName" `
        "-PtcRunServerPort=$ServerPort" `
        "-PtcFocusMediumEffectAudit=true" `
        "-PtcFocusMediumEffectAuditPath=$OutputPath"
    if ($LASTEXITCODE -ne 0) {
        throw "Focus medium/effect parity audit failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}
