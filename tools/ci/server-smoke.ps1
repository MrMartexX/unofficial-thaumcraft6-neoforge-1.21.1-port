param(
    [int]$TimeoutSeconds = 420,
    [switch]$FailOnWarnings
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

Write-Host "Starting dedicated server smoke test with timeout $TimeoutSeconds seconds."
Write-Host "Module root: $moduleRoot"
Write-Host "Smoke log: $logPath"
Write-Host "Latest smoke log alias: $latestLogPath"

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

        Assert-SmokeLogQuality -Text $combined

        $readyPattern = Test-AnyPattern -Text $combined -Patterns $readyPatterns
        if ($readyPattern) {
            $combined = Write-CombinedLog
            Assert-SmokeLogQuality -Text $combined

            Write-Host "Dedicated server reached startup marker: $readyPattern"
            Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue
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
    [void](Write-CombinedLog)
}
