param(
    [int]$TimeoutSeconds = 420,
    [string]$WorldName = '',
    [int]$ServerPort = -1,
    [switch]$FailOnWarnings,
    [switch]$KillStaleRunServer
)

$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$moduleRoot = Join-Path $repoRoot '05_neoforge_port'
$gradleBat = Join-Path $moduleRoot 'gradlew.bat'
$logDir = Join-Path $moduleRoot 'build\ci-logs'
$runId = Get-Date -Format 'yyyyMMdd-HHmmss-ffff'
$stdoutPath = Join-Path $logDir "runServer-smoke.$runId.stdout.log"
$stderrPath = Join-Path $logDir "runServer-smoke.$runId.stderr.log"
$logPath = Join-Path $logDir "runServer-smoke.$runId.log"
$latestLogPath = Join-Path $logDir 'runServer-smoke.log'
$runDir = Join-Path $moduleRoot 'run'

New-Item -ItemType Directory -Force -Path $logDir | Out-Null
New-Item -ItemType Directory -Force -Path $runDir | Out-Null
Set-Content -LiteralPath (Join-Path $runDir 'eula.txt') -Value 'eula=true' -Encoding utf8

$serverPropertiesPath = Join-Path $runDir 'server.properties'
if (-not (Test-Path -LiteralPath $serverPropertiesPath)) {
    $serverProperties = @(
        'online-mode=false',
        'spawn-protection=0',
        'motd=Thaumcraft CI smoke',
        'enable-command-block=false',
        'allow-flight=true',
        'view-distance=6',
        'simulation-distance=4'
    )
    Set-Content -LiteralPath $serverPropertiesPath -Value $serverProperties -Encoding utf8
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

$hardFailurePatterns = @(
    'BUILD FAILED',
    'Exception in thread',
    'Crash report',
    'Failed to start',
    'NoClassDefFoundError',
    'ClassNotFoundException',
    'ModLoadingException',
    'MixinTransformerError'
)

$logQualityFailurePatterns = @(
    '\[[^\]]+/ERROR\]',
    'Invalid path in pack',
    'Parsing error loading recipe',
    'Failed to parse recipe',
    'Failed to load tag',
    'Couldn''t load tag',
    'Failed to load datapacks',
    'Failed to validate datapack',
    'Registry entry .* does not exist',
    'Unknown registry key',
    'Unknown item',
    'Unknown block'
)

$warningPatterns = @(
    '\[[^\]]+/WARN\]',
    '(?m)^\s*WARNING[:\s]'
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

    try {
        [System.IO.File]::WriteAllText($latestLogPath, $combined, [System.Text.UTF8Encoding]::new($false))
    }
    catch {
        Write-Warning "Could not update latest smoke log '$latestLogPath': $($_.Exception.Message)"
    }

    return $combined
}

function Test-AnyPattern {
    param(
        [string]$Text,
        [string[]]$Patterns
    )

    foreach ($pattern in $Patterns) {
        if ($Text -cmatch $pattern) {
            return $pattern
        }
    }
    return $null
}

function Get-LogTail {
    param(
        [string]$Text,
        [int]$LineCount = 120
    )

    $lines = $Text -split "`r?`n"
    if ($lines.Count -le $LineCount) {
        return ($lines -join "`n")
    }
    return (($lines | Select-Object -Last $LineCount) -join "`n")
}

function Assert-SmokeLogQuality {
    param([string]$Text)

    $hardFailurePattern = Test-AnyPattern -Text $Text -Patterns $hardFailurePatterns
    if ($hardFailurePattern) {
        throw "Dedicated server smoke test detected hard failure pattern: $hardFailurePattern`n`n$(Get-LogTail -Text $Text)"
    }

    $qualityFailurePattern = Test-AnyPattern -Text $Text -Patterns $logQualityFailurePatterns
    if ($qualityFailurePattern) {
        throw "Dedicated server smoke test detected log quality failure pattern: $qualityFailurePattern`n`n$(Get-LogTail -Text $Text)"
    }

    if ($FailOnWarnings) {
        $warningPattern = Test-AnyPattern -Text $Text -Patterns $warningPatterns
        if ($warningPattern) {
            throw "Dedicated server smoke test detected warning pattern because -FailOnWarnings was enabled: $warningPattern`n`n$(Get-LogTail -Text $Text)"
        }
    }
}

function Get-RepoRunServerProcesses {
    if (-not $IsWindows -or -not (Get-Command Get-CimInstance -ErrorAction SilentlyContinue)) {
        return @()
    }

    $repoPattern = [regex]::Escape([string]$repoRoot)
    $modulePattern = [regex]::Escape([string]$moduleRoot)

    @(Get-CimInstance Win32_Process |
        Where-Object {
            $_.Name -match '^(java|javaw|gradle).*\.exe$' -and
            $_.CommandLine -and
            (
                $_.CommandLine -match $repoPattern -or
                $_.CommandLine -match $modulePattern
            ) -and
            (
                $_.CommandLine -match 'GradleWrapperMain runServer' -or
                $_.CommandLine -match 'NeoForgeServerDevLaunchHandler' -or
                $_.CommandLine -match 'serverRunProgramArgs\.txt' -or
                $_.CommandLine -match 'net\.neoforged\.devlaunch\.Main'
            )
        })
}

function Get-RepoRunServerProcessSummary {
    @(Get-RepoRunServerProcesses |
        ForEach-Object {
            "PID $($_.ProcessId) $($_.Name): $($_.CommandLine)"
        })
}

$smokeStartTime = $null

function Get-SmokeStartedRunServerProcesses {
    if ($null -eq $smokeStartTime) {
        return @()
    }

    $cutoff = $smokeStartTime.AddSeconds(-2)
    @(Get-RepoRunServerProcesses |
        Where-Object {
            $_.CreationDate -and $_.CreationDate -ge $cutoff
        })
}

function Stop-SmokeStartedRunServerProcesses {
    $processes = @(Get-SmokeStartedRunServerProcesses)
    foreach ($process in $processes) {
        Stop-Process -Id $process.ProcessId -Force -ErrorAction SilentlyContinue
    }
}

function Test-FileLocked {
    param([string]$Path)

    if (-not (Test-Path -LiteralPath $Path)) {
        return $false
    }

    $stream = $null
    try {
        $stream = [System.IO.File]::Open(
            $Path,
            [System.IO.FileMode]::Open,
            [System.IO.FileAccess]::ReadWrite,
            [System.IO.FileShare]::None
        )
        return $false
    }
    catch [System.IO.IOException] {
        return $true
    }
    finally {
        if ($stream) {
            $stream.Dispose()
        }
    }
}

function Assert-WorldSessionLockAvailable {
    $worldDirName = if ([string]::IsNullOrWhiteSpace($WorldName)) { 'world' } else { $WorldName }
    $lockPath = Join-Path (Join-Path $runDir $worldDirName) 'session.lock'
    if (-not (Test-FileLocked -Path $lockPath)) {
        return
    }

    $processes = @(Get-RepoRunServerProcesses)
    $processHint = if ($processes.Count -gt 0) {
        "Likely local stale runServer/Java processes:`n" + ((Get-RepoRunServerProcessSummary) -join "`n")
    } else {
        "No matching Java process was found automatically. Check Task Manager for a stale runServer/java.exe process."
    }

    if (-not $KillStaleRunServer) {
        throw "Dedicated server run directory is already locked before smoke start: $lockPath`nStop the stale server process and rerun smoke, or rerun with -KillStaleRunServer.`n`n$processHint"
    }

    if ($processes.Count -eq 0) {
        throw "Dedicated server run directory is already locked before smoke start: $lockPath`n-KillStaleRunServer was set, but no matching repo runServer process was found.`n$processHint"
    }

    Write-Warning "Dedicated server run directory is locked before smoke start. Stopping stale repo runServer processes because -KillStaleRunServer was set."
    foreach ($process in $processes) {
        Write-Warning "Stopping PID $($process.ProcessId): $($process.Name)"
        Stop-Process -Id $process.ProcessId -Force -ErrorAction SilentlyContinue
    }

    Start-Sleep -Seconds 3

    if (Test-FileLocked -Path $lockPath) {
        $remaining = @(Get-RepoRunServerProcessSummary)
        $remainingHint = if ($remaining.Count -gt 0) {
            "Remaining matching processes:`n" + ($remaining -join "`n")
        } else {
            "No matching Java process remains, but the lock is still held. Close related terminals or inspect the file handle manually."
        }

        throw "Dedicated server run directory is still locked after attempting stale process cleanup: $lockPath`n$remainingHint"
    }

    Write-Host "Stale runServer lock cleared."
}

Assert-WorldSessionLockAvailable

Write-Host "Starting dedicated server smoke test with timeout $TimeoutSeconds seconds."
Write-Host "Module root: $moduleRoot"
Write-Host "Smoke log: $logPath"
Write-Host "Latest smoke log alias: $latestLogPath"

$gradleArgs = @('runServer', '--no-daemon')
if (-not [string]::IsNullOrWhiteSpace($WorldName)) {
    $gradleArgs += "-PtcRunServerWorld=$WorldName"
}
if ($ServerPort -ge 0) {
    $gradleArgs += "-PtcRunServerPort=$ServerPort"
}

$smokeStartTime = Get-Date
$process = Start-Process `
    -FilePath $gradleBat `
    -ArgumentList $gradleArgs `
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

        Assert-SmokeLogQuality -Text $combined

        $readyPattern = Test-AnyPattern -Text $combined -Patterns $readyPatterns
        if ($readyPattern) {
            $combined = Write-CombinedLog
            Assert-SmokeLogQuality -Text $combined

            Write-Host "Dedicated server reached startup marker: $readyPattern"
            Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue
            Stop-SmokeStartedRunServerProcesses
            $process.WaitForExit(30000) | Out-Null

            $combined = Write-CombinedLog
            Assert-SmokeLogQuality -Text $combined
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
    Assert-SmokeLogQuality -Text $combined

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
    Stop-SmokeStartedRunServerProcesses
    [void](Write-CombinedLog)
}
