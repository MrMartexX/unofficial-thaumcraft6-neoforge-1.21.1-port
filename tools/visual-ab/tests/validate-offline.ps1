[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$testScripts = @(
    (Join-Path $PSScriptRoot "validate-cycle1.ps1"),
    (Join-Path $PSScriptRoot "validate-cycle2-core.ps1")
)
foreach ($path in $testScripts) {
    Write-Host "Running $([System.IO.Path]::GetFileName($path))"
    & pwsh -NoProfile -File $path
    if ($LASTEXITCODE -ne 0) {
        throw "Offline validation failed in $path (exit code $LASTEXITCODE)"
    }
}
Write-Host "PASS: combined offline Harness source and session integrity checks"
Write-Host "No Minecraft client was launched; visual parity remains NOT_RUN"
