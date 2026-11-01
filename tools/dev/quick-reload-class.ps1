# Fast Class Recompile & Hot-Reload Trigger
param(
    [switch]$Continuous
)

$ErrorActionPreference = "Stop"
$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$moduleRoot = Join-Path $repoRoot '05_neoforge_port'
$gradleBat = Join-Path $moduleRoot 'gradlew.bat'

Write-Host "=================================================="
Write-Host "TC6 Quick Class Recompile (Hot-Swap Target)"
Write-Host "=================================================="

if ($Continuous) {
    Write-Host "Starting Gradle continuous compile loop..."
    Write-Host "Press Ctrl+C to stop."
    cmd.exe /d /c "`"$gradleBat`" compileJava -t"
} else {
    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    cmd.exe /d /c "`"$gradleBat`" compileJava"
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Classes recompiled successfully in $($sw.Elapsed.TotalSeconds)s!" -ForegroundColor Green
        Write-Host "If Minecraft is running with -agentlib:jdwp (port 5005), classes will hot-swap automatically."
    } else {
        Write-Error "Compilation failed with exit code $LASTEXITCODE"
    }
}
