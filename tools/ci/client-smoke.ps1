param(
    [ValidateRange(10, 3600)][int]$TimeoutSeconds = 300,
    [switch]$KillStaleRunClient
)

$ErrorActionPreference = 'Stop'
if ($KillStaleRunClient) { throw 'Global Minecraft cleanup is disabled. Stop the specific stale project instance explicitly.' }
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$moduleRoot = Join-Path $repoRoot '05_neoforge_port'
$gradleBat = Join-Path $moduleRoot 'gradlew.bat'
$logDir = Join-Path $moduleRoot 'build\ci-logs'
New-Item -ItemType Directory -Force -Path $logDir | Out-Null
$runId = Get-Date -Format 'yyyyMMdd-HHmmss-ffff'
$stdoutPath = Join-Path $logDir "runClient-smoke.$runId.stdout.log"
$stderrPath = Join-Path $logDir "runClient-smoke.$runId.stderr.log"
$combinedPath = Join-Path $logDir "runClient-smoke.$runId.log"
$oldOptions = $env:JAVA_TOOL_OPTIONS
$process = $null
$readyAt = $null
$passed = $false
$failure = 'Client did not reach a rendered title screen before the timeout.'
$fatalPattern = 'Crash report|Exception in thread "Render thread"|A fatal error has been detected by the Java Runtime Environment|Failed to register entity renderer|Encountered an unexpected exception'
$clock = [Diagnostics.Stopwatch]::StartNew()
try {
    $env:JAVA_TOOL_OPTIONS = ($oldOptions + ' -Dtc.clientSmoke=true').Trim()
    $process = Start-Process -FilePath $env:ComSpec -ArgumentList @('/d', '/c', "`"$gradleBat`" runClient --no-daemon") `
        -WorkingDirectory $moduleRoot -WindowStyle Hidden -PassThru `
        -RedirectStandardOutput $stdoutPath -RedirectStandardError $stderrPath
    $env:JAVA_TOOL_OPTIONS = $oldOptions
    while ($clock.Elapsed.TotalSeconds -lt $TimeoutSeconds) {
        Start-Sleep -Milliseconds 500
        $stdout = if (Test-Path -LiteralPath $stdoutPath) { Get-Content -LiteralPath $stdoutPath -Raw } else { '' }
        $stderr = if (Test-Path -LiteralPath $stderrPath) { Get-Content -LiteralPath $stderrPath -Raw } else { '' }
        $combined = $stdout + "`n[STDERR]`n" + $stderr
        if ($combined -match $fatalPattern) { $failure = 'Client reported a fatal error; inspect the captured stdout/stderr.'; break }
        if ($process.HasExited) { $failure = "Client launch exited before acceptance (exit $($process.ExitCode))."; break }
        if ($null -eq $readyAt -and $combined -match 'TC_CLIENT_READY_AFTER_TITLE_RENDER') { $readyAt = $clock.Elapsed.TotalSeconds }
        if ($null -ne $readyAt -and $clock.Elapsed.TotalSeconds - $readyAt -ge 5) { $passed = $true; break }
    }
} finally {
    $env:JAVA_TOOL_OPTIONS = $oldOptions
    if ($null -ne $process -and -not $process.HasExited) { $process.Kill($true); $process.WaitForExit(10000) | Out-Null }
    $stdout = if (Test-Path -LiteralPath $stdoutPath) { Get-Content -LiteralPath $stdoutPath -Raw } else { '' }
    $stderr = if (Test-Path -LiteralPath $stderrPath) { Get-Content -LiteralPath $stderrPath -Raw } else { '' }
    [IO.File]::WriteAllText($combinedPath, $stdout + "`n[STDERR]`n" + $stderr)
    Copy-Item -LiteralPath $combinedPath -Destination (Join-Path $logDir 'runClient-smoke.log') -Force
}
if (-not $passed) { throw $failure }
Write-Host 'Client rendered the title screen and survived the startup grace period. Gameplay and visual parity require separate tests.'
