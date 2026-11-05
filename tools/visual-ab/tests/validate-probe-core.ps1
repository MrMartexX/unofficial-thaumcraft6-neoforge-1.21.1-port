[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$core = Join-Path $PSScriptRoot '../video/probe-core.ps1'
$probeCli = Join-Path $PSScriptRoot '../video/probe.ps1'
foreach ($path in @($core, $probeCli, $PSCommandPath)) {
    $tokens = $null; $parseErrors = $null
    $null = [Management.Automation.Language.Parser]::ParseFile($path, [ref]$tokens, [ref]$parseErrors)
    if (@($parseErrors).Count -gt 0) { throw "PowerShell AST failed in $path`: $($parseErrors[0])" }
}
. $core

$legacyJson = '{"format":{"duration":"20.125000"},"streams":[{"codec_type":"video","width":1280,"height":720,"avg_frame_rate":"30000/1001"}]}'
$modernJson = '{"format":{"duration":"25.75"},"streams":[{"codec_type":"video","width":1280,"height":720,"avg_frame_rate":"30/1"}]}'
$legacy = ConvertTo-Tc6ProbeSummary ($legacyJson | ConvertFrom-Json)
$modern = ConvertTo-Tc6ProbeSummary ($modernJson | ConvertFrom-Json)
if ($legacy.durationSeconds -ne 20.125 -or $legacy.width -ne 1280 -or $legacy.height -ne 720 -or
    $legacy.averageFps -ne 29.97 -or $legacy.framesDecoded -ne $false -or $modern.averageFps -ne 30) {
    throw 'Valid synthetic ffprobe JSON was parsed incorrectly'
}
$plan = [pscustomobject]@{ legacyStartSeconds = 5.25; modernStartSeconds = 7.5; durationSeconds = 10 }
$bounds = Test-Tc6SegmentBounds $plan $legacy $modern
if (-not $bounds.withinContainerDurations -or -not $bounds.frameDimensionsEqual -or
    $bounds.cameraAlignment -ne 'NOT_VERIFIED' -or $bounds.gameplayObservation -ne 'NOT_RUN') {
    throw 'Synthetic video bounds/verification semantics failed'
}

$outOfBounds = [pscustomobject]@{ legacyStartSeconds = 12; modernStartSeconds = 21; durationSeconds = 10 }
$badBounds = Test-Tc6SegmentBounds $outOfBounds $legacy $modern
if ($badBounds.withinContainerDurations -or $badBounds.legacyWithinDuration -or $badBounds.modernWithinDuration) {
    throw 'Out-of-duration segments were accepted'
}
$differentSize = ConvertTo-Tc6ProbeSummary ('{"format":{"duration":"30"},"streams":[{"codec_type":"video","width":1920,"height":1080,"avg_frame_rate":"0/0"}]}' | ConvertFrom-Json)
$dimensions = Test-Tc6SegmentBounds $plan $legacy $differentSize
if ($dimensions.frameDimensionsEqual -or $dimensions.averageFpsComparable -or $differentSize.averageFps) {
    throw 'Different frame sizes/unavailable FPS were falsely considered aligned'
}

foreach ($invalidJson in @(
    '{"format":{"duration":"N/A"},"streams":[{"codec_type":"video","width":1280,"height":720}]}',
    '{"format":{"duration":"-10"},"streams":[{"codec_type":"video","width":1280,"height":720}]}',
    '{"format":{"duration":"30"},"streams":[]}',
    '{"format":{"duration":"30"},"streams":[{"codec_type":"audio","width":1280,"height":720}]}',
    '{"format":{"duration":"30"},"streams":[{"codec_type":"video","width":99999,"height":720}]}',
    '{"format":{"duration":"30"},"streams":[{"codec_type":"video","width":1280,"height":0}]}'
)) {
    $rejected = $false
    try { $null = ConvertTo-Tc6ProbeSummary ($invalidJson | ConvertFrom-Json) }
    catch { $rejected = $true }
    if (-not $rejected) { throw 'Malformed synthetic ffprobe data was accepted' }
}

$sourcePath = 'D:\SYNTHETIC\some recording.mp4'
$outputPath = 'D:\SYNTHETIC\isolated output\clip.mp4'
$argsList = @(Get-Tc6FfmpegClipArguments $sourcePath $outputPath 5.25 10)
if (@($argsList | Where-Object { $_ -eq '-n' }).Count -ne 1 -or
    @($argsList | Where-Object { $_ -eq '-y' }).Count -gt 0 -or
    $argsList[-1] -cne $outputPath -or $argsList[[Array]::IndexOf($argsList, '-i') + 1] -cne $sourcePath -or
    $argsList[[Array]::IndexOf($argsList, '-ss') + 1] -cne '5.250' -or
    $argsList[[Array]::IndexOf($argsList, '-t') + 1] -cne '10.000') {
    throw 'Unsafe or incorrect ffmpeg argument vector'
}
$samePathRejected = $false
try { $null = Get-Tc6FfmpegClipArguments $sourcePath $sourcePath 0 1 }
catch { $samePathRejected = $true }
if (-not $samePathRejected) { throw 'Preview allowed input/output overwrite' }
$caseVariantRejected = $false
try { $null = Get-Tc6FfmpegClipArguments $sourcePath ($sourcePath.ToUpperInvariant()) 0 1 }
catch { $caseVariantRejected = $true }
if (-not $caseVariantRejected) { throw 'Case-variant path could overwrite source on Windows' }

$probeSource = Get-Content -LiteralPath $probeCli -Raw -Encoding UTF8
if ($probeSource -notmatch 'ffmpegExecuted = \$false' -or $probeSource -notmatch 'WaitForExit\(30000\)' -or
    $probeSource -match '\&\s+\$[Ff]fmpeg') {
    throw 'Probe tool appears to auto-run ffmpeg or has no bounded ffprobe wait'
}
Write-Host 'PASS: synthetic ffprobe JSON parsed, invalid duration/streams rejected, segment boundaries enforced'
Write-Host 'PASS: ffmpeg arguments protect original files (-n), no transcode or gameplay PASS executed'
