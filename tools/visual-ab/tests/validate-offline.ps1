[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$testScripts = @(
    (Join-Path $PSScriptRoot "validate-cycle1.ps1"),
    (Join-Path $PSScriptRoot "validate-cycle2-core.ps1"),
    (Join-Path $PSScriptRoot "validate-controller-contract.ps1"),
    (Join-Path $PSScriptRoot "validate-cycle3.ps1"),
    (Join-Path $PSScriptRoot "validate-media.ps1"),
    (Join-Path $PSScriptRoot "validate-cycle4.ps1"),
    (Join-Path $PSScriptRoot "validate-probe-core.ps1")
)
foreach ($path in $testScripts) {
    Write-Host "Running $([System.IO.Path]::GetFileName($path))"
    & pwsh -NoProfile -File $path
    if ($LASTEXITCODE -ne 0) {
        throw "Offline validation failed in $path (exit code $LASTEXITCODE)"
    }
}
Write-Host 'PASS: combined offline Harness source, session, comparison, media, environment, video planning, ffprobe metadata and export checks'
Write-Host 'No Minecraft client launched; gameplay and visual parity remain NOT_RUN'
