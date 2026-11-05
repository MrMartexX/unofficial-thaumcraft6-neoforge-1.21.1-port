Set-StrictMode -Version Latest

function ConvertTo-Tc6ProbeSummary([object]$Document) {
    if ($null -eq $Document -or $null -eq $Document.PSObject.Properties['streams'] -or
        $null -eq $Document.PSObject.Properties['format']) {
        throw 'ffprobe JSON lacks streams/format'
    }
    $video = @($Document.streams | Where-Object { $_.codec_type -eq 'video' })
    if ($video.Count -ne 1) { throw 'ffprobe must describe exactly one selected video stream' }
    $stream = $video[0]
    $width = 0; $height = 0
    if (-not [int]::TryParse([string]$stream.width, [ref]$width) -or
        -not [int]::TryParse([string]$stream.height, [ref]$height) -or
        $width -lt 16 -or $height -lt 16 -or $width -gt 8192 -or $height -gt 8192) {
        throw 'Invalid video frame dimensions from ffprobe'
    }
    $culture = [Globalization.CultureInfo]::InvariantCulture
    $styles = [Globalization.NumberStyles]::Float
    $duration = [double]0
    if (-not [double]::TryParse([string]$Document.format.duration, $styles, $culture, [ref]$duration) -or
        [double]::IsNaN($duration) -or [double]::IsInfinity($duration) -or
        $duration -le 0 -or $duration -gt 86400) {
        throw 'ffprobe duration unavailable/out of range'
    }
    $fps = $null
    $rate = if ($null -ne $stream.PSObject.Properties['avg_frame_rate']) { [string]$stream.avg_frame_rate } else { '' }
    if ($rate -match '^(\d{1,9})/(\d{1,9})$') {
        $numerator = [double]::Parse($Matches[1], $culture)
        $denominator = [double]::Parse($Matches[2], $culture)
        if ($denominator -gt 0 -and $numerator -gt 0) {
            $value = $numerator / $denominator
            if ($value -ge 1 -and $value -le 1000) { $fps = [math]::Round($value, 4) }
        }
    }
    return [pscustomobject][ordered]@{
        durationSeconds = $duration
        width = $width
        height = $height
        averageFps = $fps
        durationEvidence = 'FFPROBE_CONTAINER_METADATA'
        frameTimingVerified = $false
        framesDecoded = $false
    }
}

function Test-Tc6SegmentBounds([object]$Plan, [object]$Legacy, [object]$Modern) {
    $duration = [double]$Plan.durationSeconds
    $starts = @([double]$Plan.legacyStartSeconds, [double]$Plan.modernStartSeconds)
    foreach ($time in @($duration, $starts[0], $starts[1])) {
        if ([double]::IsNaN($time) -or [double]::IsInfinity($time)) { throw 'Non-finite video timing' }
    }
    if ($duration -le 0 -or $duration -gt 600 -or $starts[0] -lt 0 -or $starts[1] -lt 0) {
        throw 'Video segment timing out of range'
    }
    $leftFits = $starts[0] + $duration -le ([double]$Legacy.durationSeconds + 0.001)
    $rightFits = $starts[1] + $duration -le ([double]$Modern.durationSeconds + 0.001)
    $dimensionsEqual = $Legacy.width -eq $Modern.width -and $Legacy.height -eq $Modern.height
    $fpsComparable = $null -ne $Legacy.averageFps -and $null -ne $Modern.averageFps
    $fpsDifference = if ($fpsComparable) { [math]::Abs([double]$Legacy.averageFps - [double]$Modern.averageFps) } else { $null }
    return [pscustomobject][ordered]@{
        withinContainerDurations = [bool]($leftFits -and $rightFits)
        legacyWithinDuration = [bool]$leftFits
        modernWithinDuration = [bool]$rightFits
        frameDimensionsEqual = [bool]$dimensionsEqual
        averageFpsComparable = [bool]$fpsComparable
        averageFpsDifference = $fpsDifference
        frameTiming = 'NOT_VERIFIED'
        cameraAlignment = 'NOT_VERIFIED'
        gameplayObservation = 'NOT_RUN'
    }
}

function Get-Tc6FfmpegClipArguments([string]$InputPath, [string]$OutputPath, [double]$StartSeconds, [double]$DurationSeconds) {
    if ([string]::IsNullOrWhiteSpace($InputPath) -or [string]::IsNullOrWhiteSpace($OutputPath)) {
        throw 'FFmpeg output must be a separate path'
    }
    if ([StringComparer]::OrdinalIgnoreCase.Equals([IO.Path]::GetFullPath($InputPath), [IO.Path]::GetFullPath($OutputPath))) {
        throw 'FFmpeg output must be a separate path'
    }
    if ([double]::IsNaN($StartSeconds) -or [double]::IsInfinity($StartSeconds) -or
        [double]::IsNaN($DurationSeconds) -or [double]::IsInfinity($DurationSeconds) -or
        $StartSeconds -lt 0 -or $StartSeconds -gt 86400 -or
        $DurationSeconds -le 0 -or $DurationSeconds -gt 600) { throw 'Invalid FFmpeg segment times' }
    $culture = [Globalization.CultureInfo]::InvariantCulture
    $start = $StartSeconds.ToString('0.000', $culture)
    $duration = $DurationSeconds.ToString('0.000', $culture)
    return @('-hide_banner', '-loglevel', 'error', '-nostdin', '-n',
        '-ss', $start, '-i', $InputPath, '-t', $duration,
        '-map', '0:v:0', '-an', '-sn', '-dn',
        '-c:v', 'libx264', '-preset', 'medium', '-crf', '16', '-pix_fmt', 'yuv420p',
        $OutputPath)
}
