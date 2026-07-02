param(
    [string]$ProjectRoot = (Resolve-Path "$PSScriptRoot\..\..").Path,
    [string]$WorldName = "tc_lamp_device_audit",
    [int]$ServerPort = 25578,
    [string]$OutputPath = ""
)

$ErrorActionPreference = "Stop"

$portRoot = Join-Path $ProjectRoot "05_neoforge_port"
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $ProjectRoot "06_docs\audits\generated\thaumcraft_1_21_lamp_device_audit.md"
}

Push-Location $portRoot
try {
    & .\gradlew.bat runServer --no-daemon `
        "-PtcRunServerWorld=$WorldName" `
        "-PtcRunServerPort=$ServerPort" `
        "-PtcLampDeviceAudit=true" `
        "-PtcLampDeviceAuditPath=$OutputPath"
    if ($LASTEXITCODE -ne 0) {
        throw "Lamp device audit failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}
