param(
    [string]$ProjectRoot = (Resolve-Path "$PSScriptRoot\..\..").Path,
    [string]$WorldName = "tc_infernal_furnace_audit",
    [int]$ServerPort = 25579,
    [string]$OutputPath = ""
)

$ErrorActionPreference = "Stop"

$portRoot = Join-Path $ProjectRoot "05_neoforge_port"
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $ProjectRoot "06_docs\audits\generated\thaumcraft_1_21_infernal_furnace_behavior_audit.md"
}

Push-Location $portRoot
try {
    & .\gradlew.bat runServer --no-daemon `
        "-PtcRunServerWorld=$WorldName" `
        "-PtcRunServerPort=$ServerPort" `
        "-PtcInfernalFurnaceBehaviorAudit=true" `
        "-PtcInfernalFurnaceBehaviorAuditPath=$OutputPath"
    if ($LASTEXITCODE -ne 0) {
        throw "Infernal Furnace behavior audit failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}
