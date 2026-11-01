param(
    [int]$TimeoutSeconds = 300,
    [switch]$KillStaleRunClient
)

$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$moduleRoot = Join-Path $repoRoot '05_neoforge_port'
$gradleBat = Join-Path $moduleRoot 'gradlew.bat'
$logDir = Join-Path $moduleRoot 'build\ci-logs'
$runId = Get-Date -Format 'yyyyMMdd-HHmmss-ffff'
$stdoutPath = Join-Path $logDir "runClient-smoke.$runId.stdout.log"
$stderrPath = Join-Path $logDir "runClient-smoke.$runId.stderr.log"
$logPath = Join-Path $logDir "runClient-smoke.$runId.log"
$latestLogPath = Join-Path $logDir 'runClient-smoke.log'
$runDir = Join-Path $moduleRoot 'run'

New-Item -ItemType Directory -Force -Path $logDir | Out-Null
New-Item -ItemType Directory -Force -Path $runDir | Out-Null

$clientReadyPatterns = @(
    'Sound engine started',
    'OpenAL initialized',
    'Setting user:',
    'Narrator library for',
    'Backend library: LWJGL',
    'Reloading ResourceManager'
)

$clientFatalPatterns = @(
    'Crash report',
    'Exception in thread "Render thread"',
    '# A fatal error has been detected by the Java Runtime Environment',
    'Failed to register entity renderer'
)

Write-Host "=================================================="
Write-Host "TC6 Client Smoke Test"
Write-Host "Timeout: ${TimeoutSeconds}s"
Write-Host "=================================================="

# Check for stale java processes if requested
if ($KillStaleRunClient) {
    Get-Process -Name java -ErrorAction SilentlyContinue | Where-Object { $_.MainWindowTitle -match 'Minecraft' } | Stop-Process -Force -ErrorAction SilentlyContinue
}

$startInfo = New-Object System.Diagnostics.ProcessStartInfo
$startInfo.FileName = 'cmd.exe'
$startInfo.Arguments = "/d /c `"$gradleBat`" runClient --no-daemon"
$startInfo.WorkingDirectory = $moduleRoot
$startInfo.UseShellExecute = $false
$startInfo.RedirectStandardOutput = $true
$startInfo.RedirectStandardError = $true
$startInfo.CreateNoWindow = $true

$proc = New-Object System.Diagnostics.Process
$proc.StartInfo = $startInfo

$stdoutStream = [System.IO.File]::CreateText($stdoutPath)
$stderrStream = [System.IO.File]::CreateText($stderrPath)
$combinedStream = [System.IO.File]::CreateText($logPath)

$readyFound = $false
$fatalFound = $false
$fatalMessage = ""

$outputHandler = {
    param($sender, $e)
    if ($e.Data -ne $null) {
        $line = $e.Data
        $stdoutStream.WriteLine($line)
        $combinedStream.WriteLine($line)
        
        foreach ($pattern in $clientReadyPatterns) {
            if ($line -match $pattern) {
                $script:readyFound = $true
            }
        }
        foreach ($fatal in $clientFatalPatterns) {
            if ($line -match $fatal) {
                $script:fatalFound = $true
                $script:fatalMessage = $line
            }
        }
    }
}

$proc.add_OutputDataReceived($outputHandler)
$proc.add_ErrorDataReceived({
    param($sender, $e)
    if ($e.Data -ne $null) {
        $stderrStream.WriteLine($e.Data)
        $combinedStream.WriteLine("[STDERR] " + $e.Data)
    }
})

$proc.Start() | Out-Null
$proc.BeginOutputReadLine()
$proc.BeginErrorReadLine()

$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
$pollIntervalMs = 500

try {
    while ($stopwatch.Elapsed.TotalSeconds -lt $TimeoutSeconds) {
        Start-Sleep -Milliseconds $pollIntervalMs

        if ($fatalFound) {
            Write-Host "[ERROR] Client fatal error detected: $fatalMessage"
            break
        }

        if ($readyFound) {
            Write-Host "[SUCCESS] Client initialized successfully (models, textures, and renderers verified)!"
            break
        }

        if ($proc.HasExited) {
            Write-Host "[WARN] Process exited prematurely with code $($proc.ExitCode)"
            break
        }
    }
} finally {
    if (-not $proc.HasExited) {
        Write-Host "Stopping client process..."
        try {
            # Gracefully stop or kill
            $proc.Kill($true)
        } catch {}
    }
    $stdoutStream.Flush()
    $stderrStream.Flush()
    $combinedStream.Flush()
    $stdoutStream.Close()
    $stderrStream.Close()
    $combinedStream.Close()
    Copy-Item -Force -LiteralPath $logPath -Destination $latestLogPath
}

if ($fatalFound) {
    Write-Error "Client smoke failed: $fatalMessage"
    exit 1
}

if ($readyFound) {
    Write-Host "Client smoke passed in $($stopwatch.Elapsed.TotalSeconds) seconds."
    exit 0
} else {
    Write-Error "Client smoke timed out or exited without reaching ready state."
    exit 1
}
