[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('inspect', 'preview', 'status', 'verify')][string]$Action,
    [Parameter(Mandatory = $true)][string]$RunPath,
    [string]$PlanId,
    [string]$FfprobePath = 'ffprobe'
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'probe-core.ps1')
$root = (Resolve-Path -LiteralPath $RunPath -ErrorAction Stop).Path
$session = Get-Content -LiteralPath (Join-Path $root 'session.json') -Raw -Encoding UTF8 | ConvertFrom-Json
if ($session.schema -ne 'tc6-visual-ab-session/1' -or [string]$session.runId -notmatch '^RUN_[A-Za-z0-9_-]+$') {
    throw 'Invalid parent RUN session'
}
$probePath = Join-Path $root 'video-probes.json'
$previewPath = Join-Path $root 'video-previews.json'

function Read-Private([string]$Name, [string]$Schema, [string]$ArrayProperty, [int]$Limit) {
    $path = Join-Path $script:root $Name
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return $null }
    if ((Get-Item -LiteralPath $path).Attributes -band [IO.FileAttributes]::ReparsePoint) {
        throw 'Private manifest symlink rejected'
    }
    $value = Get-Content -LiteralPath $path -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($value.schema -ne $Schema -or $value.runId -cne $script:session.runId -or
        @($value.$ArrayProperty).Count -gt $Limit) {
        throw 'Private manifest schema/run/limit mismatch'
    }
    return $value
}

function Save-Private([string]$Destination, [object]$Value) {
    $temp = Join-Path $script:root ('.tc6-ab-probe-' + [guid]::NewGuid().ToString('N') + '.tmp')
    $backup = Join-Path $script:root ('.tc6-ab-probe-' + [guid]::NewGuid().ToString('N') + '.bak')
    [IO.File]::WriteAllText($temp, ($Value | ConvertTo-Json -Depth 25) + "`n", [Text.UTF8Encoding]::new($false))
    $written = $false
    try {
        if (Test-Path -LiteralPath $Destination -PathType Leaf) {
            [IO.File]::Replace($temp, $Destination, $backup)
        } else {
            [IO.File]::Move($temp, $Destination)
        }
        $written = $true
    } finally {
        if (Test-Path -LiteralPath $temp -PathType Leaf) { Remove-Item -LiteralPath $temp -Force -ErrorAction SilentlyContinue }
        if ($written -and (Test-Path -LiteralPath $backup -PathType Leaf)) { Remove-Item -LiteralPath $backup -Force -ErrorAction SilentlyContinue }
    }
}

function Get-PlanContext([string]$Id) {
    if ($Id -notmatch '^VPLAN_[A-Za-z0-9_]+$') { throw 'Valid PlanId required' }
    $plans = Read-Private 'video-plans.json' 'tc6-visual-ab-video-plans/1' 'plans' 256
    $media = Read-Private 'media.json' 'tc6-visual-ab-media/1' 'entries' 4096
    if ($null -eq $plans -or $null -eq $media) { throw 'Video plans and indexed media must already exist' }
    $matchesPlan = @($plans.plans | Where-Object { $_.id -ceq $Id })
    if ($matchesPlan.Count -ne 1) { throw 'Unknown/duplicate PlanId' }
    $plan = $matchesPlan[0]
    $sources = @{}
    foreach ($side in @('legacy', 'modern')) {
        $videoId = if ($side -eq 'legacy') { [string]$plan.legacyVideoId } else { [string]$plan.modernVideoId }
        $sha = if ($side -eq 'legacy') { [string]$plan.legacySha256 } else { [string]$plan.modernSha256 }
        $matches = @($media.entries | Where-Object { $_.id -ceq $videoId -and $_.kind -eq 'video' -and $_.side -ceq $side })
        if ($matches.Count -ne 1 -or [string]$sha -notmatch '^[0-9a-f]{64}$') { throw 'Plan video identity invalid' }
        $entry = $matches[0]
        if ($entry.sha256 -cne $sha -or $entry.sceneId -cne $plan.sceneId -or
            $entry.videoValidation -ne 'NOT_DECODED_NOT_SEGMENTED') { throw 'Video identity/scene changed' }
        $sources[$side] = $entry
    }
    return [pscustomobject]@{ plan = $plan; legacy = $sources.legacy; modern = $sources.modern }
}

function Test-VideoSource([object]$Entry) {
    $path = [string]$Entry.sourcePathLocal
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { throw 'Indexed video no longer exists' }
    $file = Get-Item -LiteralPath $path
    if ($file.Attributes -band [IO.FileAttributes]::ReparsePoint) { throw 'Video symlink rejected' }
    if ($file.Extension.ToLowerInvariant() -notin @('.mp4', '.mkv', '.webm', '.mov')) { throw 'Unsupported indexed video extension' }
    if ($file.Length -lt 32 -or $file.Length -gt 128GB) { throw 'Video size outside supported range' }
    if ((Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash.ToLowerInvariant() -cne [string]$Entry.sha256) {
        throw 'Indexed video SHA-256 has changed'
    }
    return [pscustomobject]@{ path = $file.FullName; length = $file.Length; ticks = $file.LastWriteTimeUtc.Ticks }
}

function Invoke-BoundedFfprobe([string]$Executable, [string]$InputPath) {
    $resolved = Get-Command -Name $Executable -CommandType Application -ErrorAction Stop
    $info = [Diagnostics.ProcessStartInfo]::new()
    $info.FileName = $resolved.Source
    $info.UseShellExecute = $false
    $info.RedirectStandardOutput = $true
    $info.RedirectStandardError = $true
    foreach ($arg in @('-v', 'error', '-select_streams', 'v:0',
            '-show_entries', 'stream=codec_type,width,height,avg_frame_rate:format=duration',
            '-of', 'json', '-i', $InputPath)) {
        [void]$info.ArgumentList.Add($arg)
    }
    $process = [Diagnostics.Process]::Start($info)
    if ($null -eq $process) { throw 'Cannot start ffprobe executable' }
    try {
        $stdoutTask = $process.StandardOutput.ReadToEndAsync()
        $stderrTask = $process.StandardError.ReadToEndAsync()
        if (-not $process.WaitForExit(30000)) {
            $process.Kill($true)
            throw 'ffprobe exceeded 30 seconds'
        }
        $stdout = $stdoutTask.GetAwaiter().GetResult()
        $null = $stderrTask.GetAwaiter().GetResult()
        if ($process.ExitCode -ne 0 -or [string]::IsNullOrWhiteSpace($stdout)) {
            throw 'ffprobe could not inspect the video; no metadata was accepted'
        }
        if ($stdout.Length -gt 262144) { throw 'ffprobe JSON exceeded 256 KiB' }
        return ($stdout | ConvertFrom-Json -ErrorAction Stop)
    } finally {
        $process.Dispose()
    }
}

$probes = Read-Private 'video-probes.json' 'tc6-visual-ab-video-probes/1' 'entries' 128
$previews = Read-Private 'video-previews.json' 'tc6-visual-ab-video-previews/1' 'entries' 128
switch ($Action) {
    'inspect' {
        $context = Get-PlanContext $PlanId
        $beforeLegacy = Test-VideoSource $context.legacy
        $beforeModern = Test-VideoSource $context.modern
        $legacy = ConvertTo-Tc6ProbeSummary (Invoke-BoundedFfprobe $FfprobePath $beforeLegacy.path)
        $modern = ConvertTo-Tc6ProbeSummary (Invoke-BoundedFfprobe $FfprobePath $beforeModern.path)
        $afterLegacy = Test-VideoSource $context.legacy
        $afterModern = Test-VideoSource $context.modern
        if ($beforeLegacy.length -ne $afterLegacy.length -or $beforeLegacy.ticks -ne $afterLegacy.ticks -or
            $beforeModern.length -ne $afterModern.length -or $beforeModern.ticks -ne $afterModern.ticks) {
            throw 'Video changed during ffprobe inspection'
        }
        $bounds = Test-Tc6SegmentBounds $context.plan $legacy $modern
        if (-not $bounds.withinContainerDurations) { throw 'User-selected segment exceeds ffprobe container duration' }
        if ($null -eq $probes) {
            $probes = [pscustomobject][ordered]@{ schema = 'tc6-visual-ab-video-probes/1'; runId = $session.runId; entries = @() }
        }
        if (@($probes.entries).Count -ge 128) { throw 'Video probe record limit exceeded' }
        $id = 'VPROBE_' + [DateTime]::UtcNow.ToString('yyyyMMddTHHmmssfffZ') + '_' + [guid]::NewGuid().ToString('N').Substring(0, 8)
        $record = [pscustomobject][ordered]@{
            id = $id; planId = $PlanId; createdUtc = [DateTime]::UtcNow.ToString('o')
            legacyVideoSha256 = [string]$context.legacy.sha256
            modernVideoSha256 = [string]$context.modern.sha256
            legacy = $legacy; modern = $modern; bounds = $bounds
            source = 'LOCAL_FFPROBE_JSON'; framesDecoded = $false
            cameraAlignment = 'NOT_VERIFIED'; gameplayObservation = 'NOT_RUN'
        }
        $probes.entries = @($probes.entries) + @($record)
        Save-Private $probePath $probes
        [pscustomobject]@{
            status = 'FFPROBE_METADATA_RECORDED_NOT_ALIGNED'; probeId = $id; planId = $PlanId
            resolutionMatch = $bounds.frameDimensionsEqual
            fpsComparable = $bounds.averageFpsComparable
            cameraAlignment = 'NOT_VERIFIED'; gameplayObservation = 'NOT_RUN'
        } | ConvertTo-Json -Compress -Depth 5
    }
    'preview' {
        $context = Get-PlanContext $PlanId
        $leftFile = Test-VideoSource $context.legacy
        $rightFile = Test-VideoSource $context.modern
        $candidates = if ($null -eq $probes) { @() } else { @($probes.entries | Where-Object { $_.planId -ceq $PlanId }) }
        if ($candidates.Count -eq 0) { throw 'Run ffprobe inspection before generating command preview' }
        $latest = $candidates[-1]
        if ($latest.legacyVideoSha256 -cne $context.legacy.sha256 -or
            $latest.modernVideoSha256 -cne $context.modern.sha256 -or
            $latest.source -ne 'LOCAL_FFPROBE_JSON') { throw 'Stale or untrusted ffprobe metadata' }
        $bounds = Test-Tc6SegmentBounds $context.plan $latest.legacy $latest.modern
        if (-not $bounds.withinContainerDurations) { throw 'Segment outside probed duration' }
        if (-not $bounds.frameDimensionsEqual) { throw 'Frame dimensions differ; manual calibration required before preview' }
        if ($null -eq $previews) {
            $previews = [pscustomobject][ordered]@{ schema = 'tc6-visual-ab-video-previews/1'; runId = $session.runId; entries = @() }
        }
        if (@($previews.entries).Count -ge 128) { throw 'FFmpeg preview limit exceeded' }
        $id = 'VPREV_' + [DateTime]::UtcNow.ToString('yyyyMMddTHHmmssfffZ') + '_' + [guid]::NewGuid().ToString('N').Substring(0, 8)
        $clipRoot = Join-Path $root ('clips/' + $PlanId + '/' + $id)
        $legacyOutput = Join-Path $clipRoot 'legacy.mp4'
        $modernOutput = Join-Path $clipRoot 'modern.mp4'
        $record = [pscustomobject][ordered]@{
            id = $id; planId = $PlanId; probeId = $latest.id
            createdUtc = [DateTime]::UtcNow.ToString('o')
            legacyArgumentsLocal = @(Get-Tc6FfmpegClipArguments $leftFile.path $legacyOutput ([double]$context.plan.legacyStartSeconds) ([double]$context.plan.durationSeconds))
            modernArgumentsLocal = @(Get-Tc6FfmpegClipArguments $rightFile.path $modernOutput ([double]$context.plan.modernStartSeconds) ([double]$context.plan.durationSeconds))
            ffmpegExecutable = 'NOT_SELECTED'
            ffmpegExecuted = $false
            outputsCreated = $false
            recompressionWouldChangePixelValues = $true
            averageFrameRateDifferenceRequiresReview = -not $bounds.averageFpsComparable -or $bounds.averageFpsDifference -gt 0.01
            timingAndCameraAlignment = 'USER_OFFSETS_NOT_VERIFIED'
            gameplayObservation = 'NOT_RUN'
        }
        $previews.entries = @($previews.entries) + @($record)
        Save-Private $previewPath $previews
        [pscustomobject]@{
            status = 'FFMPEG_ARGUMENTS_PREVIEW_ONLY'; previewId = $id; planId = $PlanId
            ffmpegExecuted = $false; outputsCreated = $false; gameplayObservation = 'NOT_RUN'
        } | ConvertTo-Json -Compress -Depth 5
    }
    'status' {
        [pscustomobject]@{
            status = 'VIDEO_PROBE_PREVIEW_STATUS'
            planProbes = if ($null -eq $probes) { 0 } else { @($probes.entries).Count }
            argumentPreviews = if ($null -eq $previews) { 0 } else { @($previews.entries).Count }
            ffmpegExecuted = $false; gameplayObservation = 'NOT_RUN'
        } | ConvertTo-Json -Compress
    }
    'verify' {
        if ($null -eq $probes) { throw 'No ffprobe records to verify' }
        $valid = 0; $invalid = 0
        $issues = [Collections.Generic.List[object]]::new()
        foreach ($record in @($probes.entries)) {
            try {
                $context = Get-PlanContext ([string]$record.planId)
                $null = Test-VideoSource $context.legacy
                $null = Test-VideoSource $context.modern
                if ($record.legacyVideoSha256 -cne $context.legacy.sha256 -or
                    $record.modernVideoSha256 -cne $context.modern.sha256) { throw 'Probe/source identity changed' }
                $valid++
            } catch {
                $invalid++
                $issues.Add([pscustomobject]@{ probeId = $record.id; issue = 'SOURCE_CHANGED_OR_MISSING' })
            }
        }
        [pscustomobject]@{
            status = 'FFPROBE_SOURCE_HASHES_RECHECKED_NOT_FRAME_VERIFIED'
            valid = $valid; invalid = $invalid; issues = @($issues.ToArray())
            cameraAlignment = 'NOT_VERIFIED'; gameplayObservation = 'NOT_RUN'
        } | ConvertTo-Json -Compress -Depth 6
    }
}
