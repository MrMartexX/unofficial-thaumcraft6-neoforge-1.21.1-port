[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('plan', 'status', 'verify')][string]$Action,
    [Parameter(Mandatory = $true)][string]$RunPath,
    [string]$LegacyVideoId,
    [string]$ModernVideoId,
    [double]$LegacyStartSeconds,
    [double]$ModernStartSeconds,
    [double]$DurationSeconds
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$root = (Resolve-Path -LiteralPath $RunPath -ErrorAction Stop).Path
$session = Get-Content -LiteralPath (Join-Path $root 'session.json') -Raw -Encoding UTF8 | ConvertFrom-Json
if ($session.schema -ne 'tc6-visual-ab-session/1' -or [string]$session.runId -notmatch '^RUN_[A-Za-z0-9_-]+$') {
    throw 'Invalid parent RUN session'
}
$mediaPath = Join-Path $root 'media.json'
$plansPath = Join-Path $root 'video-plans.json'

function Read-Media {
    if (-not (Test-Path -LiteralPath $script:mediaPath -PathType Leaf)) { throw 'No registered media in this RUN' }
    if ((Get-Item -LiteralPath $script:mediaPath).Attributes -band [IO.FileAttributes]::ReparsePoint) {
        throw 'Media manifest symlink rejected'
    }
    $m = Get-Content -LiteralPath $script:mediaPath -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($m.schema -ne 'tc6-visual-ab-media/1' -or $m.runId -cne $script:session.runId -or
        $m.catalogVersion -cne $script:session.catalogVersion) { throw 'Media identity mismatch' }
    return $m
}

function Read-Plans {
    if (-not (Test-Path -LiteralPath $script:plansPath -PathType Leaf)) { return $null }
    if ((Get-Item -LiteralPath $script:plansPath).Attributes -band [IO.FileAttributes]::ReparsePoint) {
        throw 'Video plan symlink rejected'
    }
    $p = Get-Content -LiteralPath $script:plansPath -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($p.schema -ne 'tc6-visual-ab-video-plans/1' -or $p.runId -cne $script:session.runId) {
        throw 'Video plan identity mismatch'
    }
    if (@($p.plans).Count -gt 256) { throw 'Video plan limit exceeded' }
    return $p
}

function Find-Video([object]$Media, [string]$Id, [string]$Side) {
    $matches = @($Media.entries | Where-Object { $_.id -ceq $Id -and $_.kind -eq 'video' -and $_.side -ceq $Side })
    if ($matches.Count -ne 1) { throw "Unknown or wrong-side $Side video ID" }
    $record = $matches[0]
    if ([string]$record.sha256 -notmatch '^[0-9a-f]{64}$' -or
        [string]$record.sceneId -notmatch '^G[0-9]{2}\.' -or
        $record.videoValidation -ne 'NOT_DECODED_NOT_SEGMENTED') { throw 'Invalid video record' }
    return $record
}

function Verify-External([object]$Record) {
    $path = [string]$Record.sourcePathLocal
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return $false }
    $item = Get-Item -LiteralPath $path
    if ($item.Attributes -band [IO.FileAttributes]::ReparsePoint) { return $false }
    if ($item.Extension.ToLowerInvariant() -notin @('.mp4', '.mkv', '.mov', '.webm')) { return $false }
    return ((Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash.ToLowerInvariant() -ceq [string]$Record.sha256)
}

function Save-Atomic([object]$Object) {
    $temp = Join-Path $script:root ('.tc6-ab-video-' + [guid]::NewGuid().ToString('N') + '.tmp')
    $backup = Join-Path $script:root ('.tc6-ab-video-' + [guid]::NewGuid().ToString('N') + '.bak')
    [IO.File]::WriteAllText($temp, ($Object | ConvertTo-Json -Depth 20) + "`n", [Text.UTF8Encoding]::new($false))
    $committed = $false
    try {
        if (Test-Path -LiteralPath $script:plansPath -PathType Leaf) {
            [IO.File]::Replace($temp, $script:plansPath, $backup)
        } else {
            [IO.File]::Move($temp, $script:plansPath)
        }
        $committed = $true
    } finally {
        if (Test-Path -LiteralPath $temp -PathType Leaf) { Remove-Item -LiteralPath $temp -Force -ErrorAction SilentlyContinue }
        if ($committed -and (Test-Path -LiteralPath $backup -PathType Leaf)) { Remove-Item -LiteralPath $backup -Force -ErrorAction SilentlyContinue }
    }
}

$plans = Read-Plans
switch ($Action) {
    'plan' {
        foreach ($required in @('LegacyVideoId', 'ModernVideoId', 'LegacyStartSeconds', 'ModernStartSeconds', 'DurationSeconds')) {
            if (-not $PSBoundParameters.ContainsKey($required)) { throw "Missing required plan parameter: $required" }
        }
        foreach ($value in @($LegacyStartSeconds, $ModernStartSeconds, $DurationSeconds)) {
            if ([double]::IsNaN($value) -or [double]::IsInfinity($value)) { throw 'Non-finite video timing rejected' }
        }
        if ($LegacyStartSeconds -lt 0 -or $ModernStartSeconds -lt 0 -or
            $LegacyStartSeconds -gt 86400 -or $ModernStartSeconds -gt 86400 -or
            $DurationSeconds -le 0 -or $DurationSeconds -gt 600) {
            throw 'Start must be 0..86400 s and clip duration 0..600 s'
        }
        $media = Read-Media
        $legacy = Find-Video $media $LegacyVideoId 'legacy'
        $modern = Find-Video $media $ModernVideoId 'modern'
        if ($legacy.sceneId -cne $modern.sceneId) { throw 'Videos belong to different source scenes' }
        if (-not (Verify-External $legacy) -or -not (Verify-External $modern)) {
            throw 'Source video is missing or its SHA-256 changed'
        }
        if ($null -ne $plans) {
            $duplicate = @($plans.plans | Where-Object {
                $_.legacyVideoId -ceq $LegacyVideoId -and $_.modernVideoId -ceq $ModernVideoId -and
                [double]$_.legacyStartSeconds -eq $LegacyStartSeconds -and
                [double]$_.modernStartSeconds -eq $ModernStartSeconds -and
                [double]$_.durationSeconds -eq $DurationSeconds
            })
            if ($duplicate.Count -gt 0) {
                [pscustomobject]@{ status = 'VIDEO_PLAN_DUPLICATE'; planId = $duplicate[0].id; observation = 'NOT_RUN' } | ConvertTo-Json -Compress
                break
            }
        } else {
            $plans = [pscustomobject][ordered]@{
                schema = 'tc6-visual-ab-video-plans/1'; runId = $session.runId; plans = @()
            }
        }
        if (@($plans.plans).Count -ge 256) { throw 'Maximum of 256 video plans reached' }
        $id = 'VPLAN_' + [DateTime]::UtcNow.ToString('yyyyMMddTHHmmssfffZ') + '_' + [guid]::NewGuid().ToString('N').Substring(0, 8)
        $record = [pscustomobject][ordered]@{
            id = $id
            sceneId = [string]$legacy.sceneId
            createdUtc = [DateTime]::UtcNow.ToString('o')
            legacyVideoId = $LegacyVideoId
            legacySha256 = [string]$legacy.sha256
            modernVideoId = $ModernVideoId
            modernSha256 = [string]$modern.sha256
            legacyStartSeconds = $LegacyStartSeconds
            modernStartSeconds = $ModernStartSeconds
            durationSeconds = $DurationSeconds
            timingBasis = 'USER_PROVIDED_UNVERIFIED'
            durationProbed = $false
            framesDecoded = $false
            clipsRendered = $false
            cameraSynchronized = $false
            observation = 'NOT_RUN'
        }
        $plans.plans = @($plans.plans) + @($record)
        Save-Atomic $plans
        [pscustomobject]@{
            status = 'VIDEO_PLAN_CREATED_UNVERIFIED'; planId = $id; sceneId = $record.sceneId
            durationSeconds = $DurationSeconds; observation = 'NOT_RUN'
            warning = 'Offsets are manual; video duration/camera alignment not probed. No clips were generated.'
        } | ConvertTo-Json -Compress -Depth 5
    }
    'status' {
        [pscustomobject]@{
            status = if ($null -eq $plans) { 'NO_VIDEO_PLANS' } else { 'VIDEO_PLANS_UNVERIFIED' }
            runId = $session.runId
            count = if ($null -eq $plans) { 0 } else { @($plans.plans).Count }
            observation = 'NOT_RUN'
        } | ConvertTo-Json -Compress
    }
    'verify' {
        if ($null -eq $plans) { throw 'No video plans to verify' }
        $media = Read-Media
        $valid = 0; $invalid = 0
        $issues = [Collections.Generic.List[object]]::new()
        foreach ($plan in @($plans.plans)) {
            try {
                $l = Find-Video $media ([string]$plan.legacyVideoId) 'legacy'
                $m = Find-Video $media ([string]$plan.modernVideoId) 'modern'
                if ($l.sceneId -cne $m.sceneId -or $l.sceneId -cne $plan.sceneId -or
                    $l.sha256 -cne $plan.legacySha256 -or $m.sha256 -cne $plan.modernSha256 -or
                    -not (Verify-External $l) -or -not (Verify-External $m)) { throw 'Video integrity/identity mismatch' }
                $valid++
            } catch {
                $invalid++
                $issues.Add([pscustomobject]@{ planId = $plan.id; issue = 'MISSING_CHANGED_OR_UNMATCHED_SOURCE' })
            }
        }
        [pscustomobject]@{
            status = 'VIDEO_PLAN_INTEGRITY_CHECKED_NOT_DECODED'
            valid = $valid; invalid = $invalid; issues = @($issues.ToArray())
            timingValidation = 'NOT_RUN'; cameraSynchronized = $false; observation = 'NOT_RUN'
        } | ConvertTo-Json -Compress -Depth 6
    }
}
