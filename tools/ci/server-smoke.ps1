param(
    [int]$TimeoutSeconds = 420
)

$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$moduleRoot = Join-Path $repoRoot '05_neoforge_port'
$gradleBat = Join-Path $moduleRoot 'gradlew.bat'
$logDir = Join-Path $moduleRoot 'build\ci-logs'
$stdoutPath = Join-Path $logDir 'runServer-smoke.stdout.log'
$stderrPath = Join-Path $logDir 'runServer-smoke.stderr.log'
$logPath = Join-Path $logDir 'runServer-smoke.log'
$runDir = Join-Path $moduleRoot 'run'

New-Item -ItemType Directory -Force -Path $logDir | Out-Null
New-Item -ItemType Directory -Force -Path $runDir | Out-Null
Set-Content -LiteralPath (Join-Path $runDir 'eula.txt') -Value 'eula=true' -Encoding utf8

foreach ($path in @($stdoutPath, $stderrPath, $logPath)) {
    if (Test-Path $path) {
        Remove-Item $path -Force
    }
}

$readyPatterns = @(
    'Done \(',
    'For help, type',
    'Server started'
)

$earlyProgressPatterns = @(
    'Starting minecraft server',
    'Preparing level',
    'Preparing spawn area',
    'Loading server properties'
)

$failurePatterns = @(
    'BUILD FAILED',
    'Exception in thread',
    'Crash report',
    'Failed to start',
    'NoClassDefFoundError',
    'ClassNotFoundException',
    'ModLoadingException',
    'MixinTransformerError'
)

function Read-FileShared {
    param([string]$Path)

    if (-not (Test-Path -LiteralPath $Path)) {
        return ''
    }

    try {
        $stream = [System.IO.File]::Open($Path, [System.IO.FileMode]::Open, [System.IO.FileAccess]::Read, [System.IO.FileShare]::ReadWrite)
        try {
            $reader = [System.IO.StreamReader]::new($stream, [System.Text.Encoding]::UTF8, $true)
            try {
                return $reader.ReadToEnd()
            }
            finally {
                $reader.Dispose()
            }
        }
        finally {
            $stream.Dispose()
        }
    }
    catch {
        return ''
    }
}

function Get-CombinedLog {
    $stdout = Read-FileShared -Path $stdoutPath
    $stderr = Read-FileShared -Path $stderrPath
    return ($stdout + "`n" + $stderr)
}

function Write-CombinedLog {
    $combined = Get-CombinedLog
    [System.IO.File]::WriteAllText($logPath, $combined, [System.Text.UTF8Encoding]::new($false))
    return $combined
}

function Test-AnyPattern {
    param(
        [string]$Text,
        [string[]]$Patterns
    )

    foreach ($pattern in $Patterns) {
        if ($Text -match $pattern) {
            return $pattern
        }
    }
    return $null
}

function Get-LogTail {
    param(
        [string]$Text,
        [int]$LineCount = 80
    )

    $lines = $Text -split "`r?`n"
    if ($lines.Count -le $LineCount) {
        return ($lines -join "`n")
    }
    return (($lines | Select-Object -Last $LineCount) -join "`n")
}

Write-Host "Starting dedicated server smoke test with timeout $TimeoutSeconds seconds."
Write-Host "Module root: $moduleRoot"
Write-Host "Smoke log: $logPath"

$process = Start-Process `
    -FilePath $gradleBat `
    -ArgumentList @('runServer', '--no-daemon') `
    -WorkingDirectory $moduleRoot `
    -RedirectStandardOutput $stdoutPath `
    -RedirectStandardError $stderrPath `
    -NoNewWindow `
    -PassThru

try {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    $lastHeartbeat = Get-Date
    $sawEarlyProgress = $false

    while (-not $process.HasExited -and (Get-Date) -lt $deadline) {
        Start-Sleep -Seconds 2
        $combined = Write-CombinedLog

        $failurePattern = Test-AnyPattern -Text $combined -Patterns $failurePatterns
        if ($failurePattern) {
            throw "Dedicated server smoke test detected failure pattern: $failurePattern`n`n$(Get-LogTail -Text $combined)"
        }

        $readyPattern = Test-AnyPattern -Text $combined -Patterns $readyPatterns
        if ($readyPattern) {
            Write-Host "Dedicated server reached startup marker: $readyPattern"
            Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue
            $process.WaitForExit(30000) | Out-Null
            exit 0
        }

        if (-not $sawEarlyProgress) {
            $progressPattern = Test-AnyPattern -Text $combined -Patterns $earlyProgressPatterns
            if ($progressPattern) {
                $sawEarlyProgress = $true
                Write-Host "Dedicated server reached early startup marker: $progressPattern"
            }
        }

        if (((Get-Date) - $lastHeartbeat).TotalSeconds -ge 30) {
            Write-Host 'Dedicated server smoke test is still waiting for a startup marker...'
            $lastHeartbeat = Get-Date
        }
    }

    $combined = Write-CombinedLog

    if ($process.HasExited) {
        if ($process.ExitCode -ne 0) {
            throw "Dedicated server smoke test exited early with code $($process.ExitCode).`n`n$(Get-LogTail -Text $combined)"
        }
        throw "Dedicated server smoke test exited before a startup marker was detected.`n`n$(Get-LogTail -Text $combined)"
    }

    throw "Dedicated server smoke test timed out after $TimeoutSeconds seconds before startup marker was detected.`n`n$(Get-LogTail -Text $combined)"
}
finally {
    if ($process -and -not $process.HasExited) {
        Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue
        $process.WaitForExit(30000) | Out-Null
    }
    [void](Write-CombinedLog)
}
