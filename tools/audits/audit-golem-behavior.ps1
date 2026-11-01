param(
    [string]$ProjectRoot = (Resolve-Path "$PSScriptRoot\..\..").Path,
    [string]$WorldName = "tc_golem_behavior_audit",
    [int]$ServerPort = 25585,
    [string]$OutputPath = ""
)

$ErrorActionPreference = "Stop"

$portRoot = Join-Path $ProjectRoot "05_neoforge_port"
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $ProjectRoot "06_docs\audits\generated\thaumcraft_1_21_golem_behavior_audit.md"
}

Push-Location $portRoot
try {
    & .\gradlew.bat runServer --no-daemon `
        "-PtcRunServerWorld=$WorldName" `
        "-PtcRunServerPort=$ServerPort" `
        "-PtcGolemBehaviorAudit=true" `
        "-PtcGolemBehaviorAuditPath=$OutputPath"
    if ($LASTEXITCODE -ne 0) {
        throw "Golem behavior audit failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}
