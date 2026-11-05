Set-StrictMode -Version Latest
. (Join-Path $PSScriptRoot 'probe-core.ps1')

function Assert-Tc6PreviewContract([object]$Preview, [object]$Plan, [object]$Probe,
                                   [object]$Legacy, [object]$Modern, [string]$RunRoot) {
    if ([string]$Preview.id -cnotmatch '^VPREV_[A-Za-z0-9_]+\z' -or
        [string]$Plan.id -cnotmatch '^VPLAN_[A-Za-z0-9_]+\z' -or
        [string]$Probe.id -cnotmatch '^VPROBE_[A-Za-z0-9_]+\z' -or
        $Preview.planId -cne $Plan.id -or $Preview.probeId -cne $Probe.id -or
        $Probe.planId -cne $Plan.id -or $Legacy.sceneId -cne $Plan.sceneId -or
        $Modern.sceneId -cne $Plan.sceneId -or $Legacy.side -cne 'legacy' -or
        $Modern.side -cne 'modern' -or $Legacy.id -cne $Plan.legacyVideoId -or
        $Modern.id -cne $Plan.modernVideoId -or
        $Legacy.sha256 -cne $Plan.legacySha256 -or
        $Modern.sha256 -cne $Plan.modernSha256 -or
        $Probe.legacyVideoSha256 -cne $Legacy.sha256 -or
        $Probe.modernVideoSha256 -cne $Modern.sha256 -or
        $Probe.source -cne 'LOCAL_FFPROBE_JSON' -or
        $Preview.ffmpegExecuted -cne $false -or $Preview.outputsCreated -cne $false -or
        $Preview.gameplayObservation -cne 'NOT_RUN' -or
        $Probe.gameplayObservation -cne 'NOT_RUN') {
        throw 'Preview, probed videos and source identities disagree'
    }
    $bounds = Test-Tc6SegmentBounds $Plan $Probe.legacy $Probe.modern
    if (-not $bounds.withinContainerDurations -or -not $bounds.frameDimensionsEqual) {
        throw 'Preview clip timings or video resolutions are incompatible'
    }
    $root = [IO.Path]::GetFullPath($RunRoot).TrimEnd([char[]]@('/', '\'))
    $clipBase = [IO.Path]::GetFullPath((Join-Path $root 'clips'))
    $outputRoot = [IO.Path]::GetFullPath((Join-Path $clipBase ([string]$Plan.id + '/' + [string]$Preview.id)))
    if (-not $outputRoot.StartsWith($clipBase + [IO.Path]::DirectorySeparatorChar,
            [StringComparison]::OrdinalIgnoreCase)) { throw 'Output escapes tool-owned clips directory' }
    $legacyOutput = Join-Path $outputRoot 'legacy.mp4'
    $modernOutput = Join-Path $outputRoot 'modern.mp4'
    $inputs = @([string]$Legacy.sourcePathLocal, [string]$Modern.sourcePathLocal)
    foreach ($source in $inputs) {
        $full = [IO.Path]::GetFullPath($source)
        if ($full.StartsWith($clipBase + [IO.Path]::DirectorySeparatorChar,
                [StringComparison]::OrdinalIgnoreCase)) {
            throw 'Cannot use an output clip as a source video'
        }
    }
    $legacyArgs = @(Get-Tc6FfmpegClipArguments $inputs[0] $legacyOutput ([double]$Plan.legacyStartSeconds) ([double]$Plan.durationSeconds))
    $modernArgs = @(Get-Tc6FfmpegClipArguments $inputs[1] $modernOutput ([double]$Plan.modernStartSeconds) ([double]$Plan.durationSeconds))
    foreach ($side in @('legacy', 'modern')) {
        $actual = if ($side -eq 'legacy') { @($Preview.legacyArgumentsLocal) } else { @($Preview.modernArgumentsLocal) }
        $expected = if ($side -eq 'legacy') { $legacyArgs } else { $modernArgs }
        if ($actual.Count -ne $expected.Count) { throw 'Altered ffmpeg preview argument count' }
        for ($i = 0; $i -lt $expected.Count; $i++) {
            if ($actual[$i] -cne $expected[$i]) { throw 'Altered ffmpeg preview argument detected' }
        }
    }
    return [pscustomobject]@{
        clipBase = $clipBase; outputRoot = $outputRoot
        legacyOutput = $legacyOutput; modernOutput = $modernOutput
        legacyArguments = $legacyArgs; modernArguments = $modernArgs
    }
}

function Assert-Tc6NewClipDirectory([object]$Layout, [string]$RunRoot, [string]$PlanId) {
    $root = [IO.Path]::GetFullPath($RunRoot)
    $planDir = Join-Path $Layout.clipBase $PlanId
    foreach ($parent in @($root, $Layout.clipBase, $planDir, $Layout.outputRoot)) {
        if (Test-Path -LiteralPath $parent) {
            $entry = Get-Item -LiteralPath $parent -Force
            if (-not $entry.PSIsContainer -or ($entry.Attributes -band [IO.FileAttributes]::ReparsePoint)) {
                throw 'Output path contains a file or reparse point'
            }
        }
    }
    if (Test-Path -LiteralPath $Layout.outputRoot) {
        throw 'Preview output directory already exists; create a fresh preview'
    }
}
