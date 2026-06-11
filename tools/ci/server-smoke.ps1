param(
    [int]$TimeoutSeconds = 420
)

$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$moduleRoot = Join-Path $repoRoot '05_neoforge_port'
$gradleBat = Join-Path $moduleRoot 'gradlew.bat'
$logDir = Join-Path $moduleRoot 'build\ci-logs'
$logPath = Join-Path $logDir 'runServer-smoke.log'

New-Item -ItemType Directory -Force -Path $logDir | Out-Null
if (Test-Path $logPath) {
    Remove-Item $logPath -Force
}

$readyPatterns = @(
    'Done \(',
    'For help, type',
    'Server started',
    'Starting minecraft server'
)

$failurePatterns = @(
    'BUILD FAILED',
    'Exception in thread',
    'Crash report',
    'Failed to start',
    'NoClassDefFoundError',
    'ClassNotFoundException'
)

$psi = [System.Diagnostics.ProcessStartInfo]::new()
$psi.FileName = $gradleBat
$psi.Arguments = 'runServer --no-daemon'
$psi.WorkingDirectory = $moduleRoot
$psi.UseShellExecute = $false
$psi.RedirectStandardOutput = $true
$psi.RedirectStandardError = $true
$psi.CreateNoWindow = $true

$process = [System.Diagnostics.Process]::new()
$process.StartInfo = $psi

$logWriter = [System.IO.StreamWriter]::new($logPath, $false, [System.Text.UTF8Encoding]::new($false))
$script:ready = $false
$script:failedPattern = $null

$outputHandler = [System.Diagnostics.DataReceivedEventHandler]{
    param($sender, $eventArgs)
    if ($null -eq $eventArgs.Data) { return }
    $line = $eventArgs.Data
    $logWriter.WriteLine($line)
    $logWriter.Flush()
    Write-Host $line

    foreach ($pattern in $readyPatterns) {
        if ($line -match $pattern) {
            $script:ready = $true
        }
    }
    foreach ($pattern in $failurePatterns) {
        if ($line -match $pattern) {
            $script:failedPattern = $pattern
        }
    }
}

$errorHandler = [System.Diagnostics.DataReceivedEventHandler]{
    param($sender, $eventArgs)
    if ($null -eq $eventArgs.Data) { return }
    $line = $eventArgs.Data
    $logWriter.WriteLine($line)
    $logWriter.Flush()
    Write-Host $line

    foreach ($pattern in $failurePatterns) {
        if ($line -match $pattern) {
            $script:failedPattern = $pattern
        }
    }
}

$process.add_OutputDataReceived($outputHandler)
$process.add_ErrorDataReceived($errorHandler)

try {
    Write-Host "Starting dedicated server smoke test with timeout $TimeoutSeconds seconds."
    [void]$process.Start()
    $process.BeginOutputReadLine()
    $process.BeginErrorReadLine()

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while (-not $process.HasExited -and (Get-Date) -lt $deadline) {
        if ($script:failedPattern) {
            throw "Dedicated server smoke test detected failure pattern: $script:failedPattern"
        }
        if ($script:ready) {
            Write-Host 'Dedicated server reached startup marker. Stopping smoke-test process.'
            $process.Kill($true)
            $process.WaitForExit(30000) | Out-Null
            exit 0
        }
        Start-Sleep -Seconds 2
    }

    if ($process.HasExited) {
        if ($process.ExitCode -ne 0) {
            throw "Dedicated server smoke test exited early with code $($process.ExitCode)."
        }
        if (-not $script:ready) {
            throw 'Dedicated server smoke test exited before a startup marker was detected.'
        }
        exit 0
    }

    throw "Dedicated server smoke test timed out after $TimeoutSeconds seconds before startup marker was detected."
}
finally {
    if (-not $process.HasExited) {
        $process.Kill($true)
        $process.WaitForExit(30000) | Out-Null
    }
    $logWriter.Dispose()
}
