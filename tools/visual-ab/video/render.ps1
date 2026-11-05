[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('render', 'status', 'verify')][string]$Action,
    [Parameter(Mandatory = $true)][string]$RunPath,
    [string]$PreviewId,
    [string]$FfmpegPath = 'ffmpeg'
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'render-core.ps1')
$root = (Resolve-Path -LiteralPath $RunPath -ErrorAction Stop).Path
$session = Get-Content -LiteralPath (Join-Path $root 'session.json') -Raw -Encoding UTF8 | ConvertFrom-Json
if ($session.schema -cne 'tc6-visual-ab-session/1' -or
    [string]$session.runId -cnotmatch '^RUN_[A-Za-z0-9_-]+\z') { throw 'Invalid RUN session' }

function Read-Manifest([string]$Name, [string]$Schema, [string]$Field, [int]$Limit) {
    $path = Join-Path $script:root $Name
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return $null }
    if ((Get-Item -LiteralPath $path).Attributes -band [IO.FileAttributes]::ReparsePoint) {
        throw 'Private manifest reparse point rejected'
    }
    $value = Get-Content -LiteralPath $path -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($value.schema -cne $Schema -or $value.runId -cne $script:session.runId -or
        @($value.$Field).Count -gt $Limit) { throw 'Invalid private manifest' }
    return $value
}

function Exactly-One([object[]]$Records, [string]$Id) {
    $matches = @($Records | Where-Object { $_.id -ceq $Id })
    if ($matches.Count -ne 1) { throw 'Unknown or duplicate record ID' }
    return $matches[0]
}

function Read-RenderContext([string]$Id) {
    if ($Id -cnotmatch '^VPREV_[A-Za-z0-9_]+\z') { throw 'Valid PreviewId required' }
    $previews = Read-Manifest 'video-previews.json' 'tc6-visual-ab-video-previews/1' 'entries' 128
    $probes = Read-Manifest 'video-probes.json' 'tc6-visual-ab-video-probes/1' 'entries' 128
    $plans = Read-Manifest 'video-plans.json' 'tc6-visual-ab-video-plans/1' 'plans' 256
    $media = Read-Manifest 'media.json' 'tc6-visual-ab-media/1' 'entries' 4096
    if ($null -eq $previews -or $null -eq $probes -or $null -eq $plans -or $null -eq $media -or
        $media.catalogVersion -cne $script:session.catalogVersion) { throw 'Missing or mismatched video evidence' }
    $preview = Exactly-One @($previews.entries) $Id
    $plan = Exactly-One @($plans.plans) ([string]$preview.planId)
    $probe = Exactly-One @($probes.entries) ([string]$preview.probeId)
    $legacy = Exactly-One @($media.entries) ([string]$plan.legacyVideoId)
    $modern = Exactly-One @($media.entries) ([string]$plan.modernVideoId)
    if ($legacy.kind -cne 'video' -or $modern.kind -cne 'video' -or
        $legacy.videoValidation -cne 'NOT_DECODED_NOT_SEGMENTED' -or
        $modern.videoValidation -cne 'NOT_DECODED_NOT_SEGMENTED') {
        throw 'Only original indexed videos can be clip sources'
    }
    $layout = Assert-Tc6PreviewContract $preview $plan $probe $legacy $modern $script:root
    return [pscustomobject]@{ preview = $preview; plan = $plan; probe = $probe
        legacy = $legacy; modern = $modern; layout = $layout }
}

function Assert-SourceUnchanged([object]$Entry) {
    $file = Get-Item -LiteralPath ([string]$Entry.sourcePathLocal) -ErrorAction Stop
    if ($file.PSIsContainer -or ($file.Attributes -band [IO.FileAttributes]::ReparsePoint) -or
        $file.Extension.ToLowerInvariant() -notin @('.mp4', '.mkv', '.webm', '.mov') -or
        $file.Length -lt 32 -or $file.Length -gt 128GB -or
        [string]$Entry.sha256 -cnotmatch '^[0-9a-f]{64}\z') { throw 'Invalid video source file' }
    $actual = (Get-FileHash -LiteralPath $file.FullName -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actual -cne $Entry.sha256) { throw 'Source video SHA-256 mismatch; render refused' }
    return [pscustomobject]@{ path = $file.FullName; sha256 = $actual
        size = $file.Length; ticks = $file.LastWriteTimeUtc.Ticks }
}

function Save-RenderManifest([object]$Document) {
    $destination = Join-Path $script:root 'video-renders.json'
    if (Test-Path -LiteralPath $destination -PathType Leaf) {
        if ((Get-Item -LiteralPath $destination).Attributes -band [IO.FileAttributes]::ReparsePoint) {
            throw 'Render manifest reparse point rejected'
        }
    }
    $tmp = Join-Path $script:root ('.tc6-ab-renders-' + [guid]::NewGuid().ToString('N') + '.tmp')
    $bak = Join-Path $script:root ('.tc6-ab-renders-' + [guid]::NewGuid().ToString('N') + '.bak')
    [IO.File]::WriteAllText($tmp, ($Document | ConvertTo-Json -Depth 20) + "`n", [Text.UTF8Encoding]::new($false))
    $committed = $false
    try {
        if (Test-Path -LiteralPath $destination -PathType Leaf) {
            [IO.File]::Replace($tmp, $destination, $bak)
        } else {
            [IO.File]::Move($tmp, $destination)
        }
        $committed = $true
    } finally {
        if (Test-Path -LiteralPath $tmp -PathType Leaf) { Remove-Item -LiteralPath $tmp -Force -ErrorAction SilentlyContinue }
        if ($committed -and (Test-Path -LiteralPath $bak -PathType Leaf)) {
            Remove-Item -LiteralPath $bak -Force -ErrorAction SilentlyContinue
        }
    }
}

function Invoke-BoundedEncoder([string]$Executable, [string[]]$Arguments) {
    $info = [Diagnostics.ProcessStartInfo]::new()
    $info.FileName = $Executable
    $info.UseShellExecute = $false
    $info.RedirectStandardError = $true
    $info.RedirectStandardOutput = $true
    foreach ($arg in $Arguments) { [void]$info.ArgumentList.Add($arg) }
    $process = [Diagnostics.Process]::Start($info)
    if ($null -eq $process) { throw 'Cannot start ffmpeg' }
    try {
        $errTask = $process.StandardError.ReadToEndAsync()
        $outTask = $process.StandardOutput.ReadToEndAsync()
        if (-not $process.WaitForExit(600000)) {
            $process.Kill($true)
            $process.WaitForExit()
            throw 'ffmpeg exceeded 10 minutes; partial clips retained'
        }
        $null = $errTask.GetAwaiter().GetResult()
        $null = $outTask.GetAwaiter().GetResult()
        if ($process.ExitCode -ne 0) { throw 'ffmpeg exited with error; partial clips retained' }
    } finally { $process.Dispose() }
}

function Assert-Encoded([string]$Path, [string]$PlanId, [string]$RenderedPreviewId) {
    $f = Get-Item -LiteralPath $Path -ErrorAction Stop
    if ($f.PSIsContainer -or ($f.Attributes -band [IO.FileAttributes]::ReparsePoint) -or
        $f.Length -lt 1024) { throw 'Encoded clip missing or implausibly small' }
    return [pscustomobject]@{ relativePath = 'clips/' + $PlanId + '/' +
        $RenderedPreviewId + '/' + $f.Name
        sha256 = (Get-FileHash -LiteralPath $f.FullName -Algorithm SHA256).Hash.ToLowerInvariant()
        sizeBytes = $f.Length }
}

$rendered = Read-Manifest 'video-renders.json' 'tc6-visual-ab-video-renders/1' 'entries' 128
switch ($Action) {
    'status' {
        [pscustomobject]@{ status = 'VIDEO_RENDER_STATUS'; successfulPairs = if ($null -eq $rendered) { 0 } else { @($rendered.entries).Count }
            manualReview = 'REQUIRED'; cameraAlignment = 'NOT_VERIFIED'; gameplayObservation = 'NOT_RUN' } |
            ConvertTo-Json -Compress
    }
    'render' {
        $ctx = Read-RenderContext $PreviewId
        if ($null -ne $rendered -and @($rendered.entries | Where-Object { $_.previewId -ceq $PreviewId }).Count -gt 0) {
            throw 'Preview already rendered; no duplicate encoding'
        }
        if ($null -eq $rendered) {
            $rendered = [pscustomobject][ordered]@{ schema = 'tc6-visual-ab-video-renders/1';
                runId = $session.runId; entries = @() }
        }
        if (@($rendered.entries).Count -ge 128) { throw 'Render record limit exceeded' }
        $beforeLegacy = Assert-SourceUnchanged $ctx.legacy
        $beforeModern = Assert-SourceUnchanged $ctx.modern
        if ($beforeLegacy.path -cne [string]$ctx.legacy.sourcePathLocal -or
            $beforeModern.path -cne [string]$ctx.modern.sourcePathLocal) {
            throw 'Indexed source paths are not canonical'
        }
        Assert-Tc6NewClipDirectory $ctx.layout $root ([string]$ctx.plan.id)
        $exe = Get-Command -Name $FfmpegPath -CommandType Application -ErrorAction Stop
        if ([IO.Path]::GetFileName($exe.Source) -notin @('ffmpeg', 'ffmpeg.exe')) {
            throw 'Expected an ffmpeg executable, not a different application'
        }
        foreach ($parent in @($ctx.layout.clipBase, (Join-Path $ctx.layout.clipBase ([string]$ctx.plan.id)))) {
            if (Test-Path -LiteralPath $parent) {
                if ((Get-Item -LiteralPath $parent).Attributes -band [IO.FileAttributes]::ReparsePoint) {
                    throw 'Output ancestor reparse point rejected'
                }
            } else { [void][IO.Directory]::CreateDirectory($parent) }
        }
        [void][IO.Directory]::CreateDirectory($ctx.layout.outputRoot)
        $marker = Join-Path $ctx.layout.outputRoot '.tc6-ab-render-started.lock'
        $handle = [IO.File]::Open($marker, [IO.FileMode]::CreateNew, [IO.FileAccess]::Write, [IO.FileShare]::None)
        $handle.Dispose()
        # Deliberately retain outputs and the lock after failures; no deletion of uncertain user data.
        Invoke-BoundedEncoder $exe.Source @($ctx.layout.legacyArguments)
        $left = Assert-Encoded $ctx.layout.legacyOutput ([string]$ctx.plan.id) $PreviewId
        Invoke-BoundedEncoder $exe.Source @($ctx.layout.modernArguments)
        $right = Assert-Encoded $ctx.layout.modernOutput ([string]$ctx.plan.id) $PreviewId
        $afterLegacy = Assert-SourceUnchanged $ctx.legacy
        $afterModern = Assert-SourceUnchanged $ctx.modern
        if ($beforeLegacy.size -ne $afterLegacy.size -or $beforeLegacy.ticks -ne $afterLegacy.ticks -or
            $beforeModern.size -ne $afterModern.size -or $beforeModern.ticks -ne $afterModern.ticks) {
            throw 'Video sources changed during encoding; clips are untrusted'
        }
        $renderId = 'VREND_' + [DateTime]::UtcNow.ToString('yyyyMMddTHHmmssfffZ') + '_' +
            [guid]::NewGuid().ToString('N').Substring(0, 8)
        $record = [pscustomobject][ordered]@{
            id = $renderId; previewId = [string]$ctx.preview.id; planId = [string]$ctx.plan.id
            createdUtc = [DateTime]::UtcNow.ToString('o')
            legacy = $left; modern = $right
            legacyVideoSha256 = [string]$ctx.legacy.sha256
            modernVideoSha256 = [string]$ctx.modern.sha256
            status = 'ENCODED_UNREVIEWED'; recompressed = $true
            timingAlignment = 'NOT_VERIFIED'; cameraAlignment = 'NOT_VERIFIED'
            frameReview = 'NOT_RUN'; gameplayObservation = 'NOT_RUN'
        }
        $rendered.entries = @($rendered.entries) + @($record)
        Save-RenderManifest $rendered
        [pscustomobject]@{ status = 'CLIP_PAIR_ENCODED_UNREVIEWED'; renderId = $renderId
            previewId = $PreviewId; legacyPath = $ctx.layout.legacyOutput
            modernPath = $ctx.layout.modernOutput; cameraAlignment = 'NOT_VERIFIED'
            gameplayObservation = 'NOT_RUN' } | ConvertTo-Json -Compress -Depth 6
    }
    'verify' {
        $good = 0; $bad = 0
        $issues = [Collections.Generic.List[object]]::new()
        if ($null -ne $rendered) {
            foreach ($entry in @($rendered.entries)) {
                try {
                    $ctx = Read-RenderContext ([string]$entry.previewId)
                    if ($ctx.plan.id -cne $entry.planId -or $entry.status -cne 'ENCODED_UNREVIEWED' -or
                        $entry.frameReview -cne 'NOT_RUN' -or
                        $entry.gameplayObservation -cne 'NOT_RUN') { throw 'Invalid render record' }
                    $null = Assert-SourceUnchanged $ctx.legacy
                    $null = Assert-SourceUnchanged $ctx.modern
                    foreach ($side in @('legacy', 'modern')) {
                        $relative = 'clips/' + $entry.planId + '/' + $entry.previewId + '/' + $side + '.mp4'
                        $clipPath = Join-Path $root $relative
                        if ([string]$entry.$side.relativePath -cne $relative -or
                            -not (Test-Path -LiteralPath $clipPath -PathType Leaf) -or
                            ((Get-Item -LiteralPath $clipPath).Attributes -band [IO.FileAttributes]::ReparsePoint) -or
                            (Get-FileHash -LiteralPath $clipPath -Algorithm SHA256).Hash.ToLowerInvariant() -cne
                            [string]$entry.$side.sha256) { throw 'Rendered clip SHA-256 mismatch' }
                    }
                    $good++
                } catch {
                    $bad++
                    $issues.Add([pscustomobject]@{ renderId = [string]$entry.id; issue = 'SOURCE_OR_CLIP_INVALID' })
                }
            }
        }
        [pscustomobject]@{ status = 'RENDER_HASHES_RECHECKED_NOT_VISUALLY_REVIEWED';
            valid = $good; invalid = $bad; issues = @($issues.ToArray())
            cameraAlignment = 'NOT_VERIFIED'; gameplayObservation = 'NOT_RUN' } |
            ConvertTo-Json -Compress -Depth 6
    }
}
