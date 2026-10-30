param(
    [string]$ProjectRoot = (Resolve-Path "$PSScriptRoot\..\..").Path,
    [switch]$WithRuntime = $false,
    [string]$WorldName = "tc_complete_parity_matrix_audit",
    [int]$ServerPort = 25579,
    [string]$MarkdownPath = "",
    [string]$RuntimeOutputPath = ""
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($MarkdownPath)) {
    $MarkdownPath = Join-Path $ProjectRoot "06_docs\audits\generated\tc6_complete_parity_matrix.md"
}
if ([string]::IsNullOrWhiteSpace($RuntimeOutputPath)) {
    $RuntimeOutputPath = Join-Path $ProjectRoot "06_docs\audits\generated\tc6_runtime_parity_matrix.md"
}

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "TC6 Parity Test Framework (TC6-PTF): Complete Master Audit" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Run Python Comprehensive Static Matrix Audit
$pythonScript = Join-Path $ProjectRoot "tools\audits\audit-complete-parity-matrix.py"
Write-Host "[1/2] Executing static parity audit against 1.12.2 legacy source..." -ForegroundColor Yellow

python $pythonScript --repo-root $ProjectRoot
if ($LASTEXITCODE -ne 0) {
    throw "Static parity matrix audit failed with exit code $LASTEXITCODE"
}
Write-Host "Static parity matrix generated at: $MarkdownPath" -ForegroundColor Green

# 2. Run Server Runtime Audit (if requested)
if ($WithRuntime) {
    Write-Host "[2/2] Launching dedicated server for runtime contract verification..." -ForegroundColor Yellow
    $portRoot = Join-Path $ProjectRoot "05_neoforge_port"
    Push-Location $portRoot
    try {
        & .\gradlew.bat runServer --no-daemon `
            "-PtcRunServerWorld=$WorldName" `
            "-PtcRunServerPort=$ServerPort" `
            "-PtcCompleteParityMatrixAudit=true" `
            "-PtcCompleteParityMatrixAuditPath=$RuntimeOutputPath"
        if ($LASTEXITCODE -ne 0) {
            throw "Runtime parity matrix audit failed with exit code $LASTEXITCODE"
        }
        Write-Host "Runtime parity matrix generated at: $RuntimeOutputPath" -ForegroundColor Green
    } finally {
        Pop-Location
    }
} else {
    Write-Host "[2/2] Skipped runtime server pass (use -WithRuntime to trigger dedicated server execution)." -ForegroundColor DarkGray
}

Write-Host "Master parity audit complete!" -ForegroundColor Cyan
