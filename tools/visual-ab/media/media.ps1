[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('snapshot-log', 'index-video', 'status', 'verify')][string]$Action,
    [Parameter(Mandatory = $true)][string]$RunPath,
    [string]$SceneId,
    [ValidateSet('legacy', 'modern')][string]$Side,
    [string]$LogPath,
    [string]$VideoPath,
    [string]$ConfigPath
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$toolRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$catalog = Get-Content -LiteralPath (Join-Path $toolRoot 'catalog/v0-state-candidates.json') -Raw -Encoding UTF8 | ConvertFrom-Json

function Write-AtomicJson([string]$Destination, [object]$Object) {
    $parent = Split-Path -Parent $Destination
    if (-not (Test-Path -LiteralPath $parent -PathType Container)) { throw 'Destination parent missing' }
    $temporary = Join-Path $parent ('.tc6-ab-' + [guid]::NewGuid().ToString('N') + '.tmp')
    $backup = Join-Path $parent ('.tc6-ab-' + [guid]::NewGuid().ToString('N') + '.bak')
    $written = $false
    [IO.File]::WriteAllText($temporary, ($Object | ConvertTo-Json -Depth 30) + "`n", [Text.UTF8Encoding]::new($false))
    try {
        if (Test-Path -LiteralPath $Destination -PathType Leaf) {
            [IO.File]::Replace($temporary, $Destination, $backup)
        } else {
            [IO.File]::Move($temporary, $Destination)
        }
        $written = $true
    } finally {
        if (Test-Path -LiteralPath $temporary -PathType Leaf) { Remove-Item -LiteralPath $temporary -Force -ErrorAction SilentlyContinue }
        if ($written -and (Test-Path -LiteralPath $backup -PathType Leaf)) { Remove-Item -LiteralPath $backup -Force -ErrorAction SilentlyContinue }
    }
}

function Resolve-RunFile([string]$Root, [string]$Relative) {
    if ([string]::IsNullOrWhiteSpace($Relative) -or [IO.Path]::IsPathRooted($Relative) -or
        $Relative -match '(^|[/\\])\.\.([/\\]|$)' -or $Relative.Contains(':')) { throw 'Unsafe media path' }
    $base = [IO.Path]::GetFullPath($Root).TrimEnd([char[]]@('/', '\'))
    $full = [IO.Path]::GetFullPath((Join-Path $base $Relative))
    if (-not $full.StartsWith(($base + [IO.Path]::DirectorySeparatorChar), [StringComparison]::OrdinalIgnoreCase)) {
        throw 'Media path escapes RUN directory'
    }
    if (-not (Test-Path -LiteralPath $full -PathType Leaf)) { throw 'Registered media copy missing' }
    $partPath = $base
    foreach ($part in ($Relative -split '[\\/]')) {
        $partPath = Join-Path $partPath $part
        if ((Get-Item -LiteralPath $partPath).Attributes -band [IO.FileAttributes]::ReparsePoint) {
            throw 'Reparse-point media copy rejected'
        }
    }
    return $full
}

function Read-Run([string]$Directory) {
    $resolved = (Resolve-Path -LiteralPath $Directory -ErrorAction Stop).Path
    $session = Get-Content -LiteralPath (Join-Path $resolved 'session.json') -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($session.schema -ne 'tc6-visual-ab-session/1' -or $session.catalogVersion -ne $script:catalog.version) {
        throw 'Unsupported session schema/catalog'
    }
    $manifestPath = Join-Path $resolved 'media.json'
    if ((Test-Path -LiteralPath $manifestPath -PathType Leaf) -and
        ((Get-Item -LiteralPath $manifestPath).Attributes -band [IO.FileAttributes]::ReparsePoint)) {
        throw 'Media manifest symlink is not accepted'
    }
    $manifest = if (Test-Path -LiteralPath $manifestPath -PathType Leaf) {
        Get-Content -LiteralPath $manifestPath -Raw -Encoding UTF8 | ConvertFrom-Json
    } else {
        [pscustomobject][ordered]@{
            schema = 'tc6-visual-ab-media/1'
            runId = $session.runId
            catalogVersion = $session.catalogVersion
            createdUtc = [DateTime]::UtcNow.ToString('o')
            updatedUtc = [DateTime]::UtcNow.ToString('o')
            entries = @()
        }
    }
    if ($manifest.schema -ne 'tc6-visual-ab-media/1' -or $manifest.runId -cne $session.runId -or
        $manifest.catalogVersion -cne $session.catalogVersion) { throw 'Media manifest does not match session' }
    return [pscustomobject]@{ root = $resolved; manifestPath = $manifestPath; session = $session; manifest = $manifest }
}

function Resolve-Selection([object]$Run) {
    $id = $SceneId
    $sideValue = $Side
    if ($null -ne $Run.session.selection) {
        if (-not $id) { $id = [string]$Run.session.selection.sceneId }
        if (-not $sideValue) { $sideValue = [string]$Run.session.selection.side }
    }
    if (-not $id -or @($script:catalog.scenes | Where-Object { $_.id -ceq $id }).Count -ne 1) {
        throw 'Select a known SceneId first'
    }
    if ($sideValue -notin @('legacy', 'modern')) { throw 'Select legacy or modern side first' }
    return [pscustomobject]@{ sceneId = $id; side = $sideValue }
}

function Save-MediaRecord([object]$Run, [object]$Record) {
    $Run.manifest.entries = @($Run.manifest.entries) + @($Record)
    $Run.manifest.updatedUtc = [DateTime]::UtcNow.ToString('o')
    Write-AtomicJson $Run.manifestPath $Run.manifest
}

function Get-LogPrivacyFlags([string]$Source) {
    # Only return signal labels; never print matching personal values or log lines.
    $text = [IO.File]::ReadAllText($Source, [Text.Encoding]::UTF8)
    $flags = [Collections.Generic.List[string]]::new()
    if ($text -match '(?i)([A-Z]:\\Users\\|/home/[^/\s]+/|/Users/[^/\s]+/)') { $flags.Add('LOCAL_USER_PATH_LIKE') }
    if ($text -match '(?i)\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}\b') { $flags.Add('EMAIL_LIKE') }
    if ($text -match '\b(?:\d{1,3}\.){3}\d{1,3}\b') { $flags.Add('IP_ADDRESS_LIKE') }
    if ($text -match '(?i)(bearer\s+\S+|access[_-]?token|auth[_-]?token|session[_-]?id)') { $flags.Add('AUTH_MARKER_LIKE') }
    return @($flags.ToArray())
}

$run = Read-Run $RunPath
switch ($Action) {
    'snapshot-log' {
        $selection = Resolve-Selection $run
        $sourcePath = $LogPath
        if (-not $sourcePath) {
            if (-not $ConfigPath) {
                $repo = (Resolve-Path -LiteralPath (Join-Path $toolRoot '../..')).Path
                $ConfigPath = Join-Path $repo 'tools/reports/local/visual-ab/config.local.json'
            }
            if (-not (Test-Path -LiteralPath $ConfigPath -PathType Leaf)) { throw 'ConfigPath or LogPath is required' }
            $config = Get-Content -LiteralPath $ConfigPath -Raw -Encoding UTF8 | ConvertFrom-Json
            $gameDir = [string]$config.profiles.($selection.side).gameDirectory
            if ([string]::IsNullOrWhiteSpace($gameDir)) { throw 'Game directory not configured' }
            $sourcePath = Join-Path $gameDir 'logs/latest.log'
        }
        $source = Get-Item -LiteralPath $sourcePath -ErrorAction Stop
        if ($source.PSIsContainer -or $source.Extension.ToLowerInvariant() -ne '.log') { throw 'Expected an existing .log file' }
        if ($source.Length -le 0 -or $source.Length -gt 32MB) { throw 'Log must be 1 byte through 32 MiB' }
        if ($source.Attributes -band [IO.FileAttributes]::ReparsePoint) { throw 'Log symlinks are not accepted' }
        $hash = (Get-FileHash -LiteralPath $source.FullName -Algorithm SHA256).Hash.ToLowerInvariant()
        $duplicates = @($run.manifest.entries | Where-Object {
            $_.kind -eq 'log' -and $_.sceneId -ceq $selection.sceneId -and
            $_.side -ceq $selection.side -and $_.sha256 -ceq $hash
        })
        if ($duplicates.Count -gt 0) {
            [pscustomobject]@{ status = 'SKIPPED_DUPLICATE'; mediaId = $duplicates[0].id; observation = 'NOT_RUN' } | ConvertTo-Json -Compress
            break
        }
        $id = 'LOG_' + [DateTime]::UtcNow.ToString('yyyyMMddTHHmmssfffZ') + '_' + [guid]::NewGuid().ToString('N').Substring(0, 8)
        $relative = 'logs/' + $selection.side + '/' + $selection.sceneId + '/' + $id + '.log'
        $destination = Join-Path $run.root $relative
        New-Item -ItemType Directory -Path (Split-Path -Parent $destination) -Force | Out-Null
        Copy-Item -LiteralPath $source.FullName -Destination $destination -ErrorAction Stop
        if ((Get-FileHash -LiteralPath $destination -Algorithm SHA256).Hash.ToLowerInvariant() -cne $hash) {
            throw 'Log changed during copy; media record was not registered'
        }
        $flags = @(Get-LogPrivacyFlags $destination)
        $record = [pscustomobject][ordered]@{
            id = $id; kind = 'log'; side = $selection.side; sceneId = $selection.sceneId
            relativePath = $relative; sourcePathLocal = $null; sourceNameLocal = $source.Name
            sourceLastWriteUtc = $source.LastWriteTimeUtc.ToString('o')
            importedUtc = [DateTime]::UtcNow.ToString('o')
            sizeBytes = [long]$source.Length; sha256 = $hash
            fileIntegrity = 'HASH_VERIFIED_AT_CAPTURE'
            association = 'USER_SELECTED_NOT_CLIENT_VERIFIED'
            privacyReview = 'REVIEW_REQUIRED'; privacyFlags = @($flags)
            observation = 'NOT_RUN'
        }
        Save-MediaRecord $run $record
        [pscustomobject]@{
            status = 'LOG_CAPTURED_PRIVACY_REVIEW_REQUIRED'
            mediaId = $id; relativePath = $relative; sha256 = $hash
            privacyFlags = @($flags); observation = 'NOT_RUN'
        } | ConvertTo-Json -Compress -Depth 5
    }
    'index-video' {
        $selection = Resolve-Selection $run
        if (-not $VideoPath) { throw 'VideoPath is required; no video recorder is started automatically' }
        $video = Get-Item -LiteralPath $VideoPath -ErrorAction Stop
        if ($video.PSIsContainer -or $video.Extension.ToLowerInvariant() -notin @('.mp4', '.mkv', '.webm', '.mov')) {
            throw 'Only existing MP4/MKV/WebM/MOV files can be indexed'
        }
        if ($video.Length -lt 32 -or $video.Length -gt 128GB) { throw 'Video size is outside allowed range' }
        if ($video.Attributes -band [IO.FileAttributes]::ReparsePoint) { throw 'Video symlinks are not accepted' }
        $hash = (Get-FileHash -LiteralPath $video.FullName -Algorithm SHA256).Hash.ToLowerInvariant()
        $duplicates = @($run.manifest.entries | Where-Object {
            $_.kind -eq 'video' -and $_.sceneId -ceq $selection.sceneId -and
            $_.side -ceq $selection.side -and $_.sha256 -ceq $hash
        })
        if ($duplicates.Count -gt 0) {
            [pscustomobject]@{ status = 'SKIPPED_DUPLICATE'; mediaId = $duplicates[0].id; observation = 'NOT_RUN' } | ConvertTo-Json -Compress
            break
        }
        $id = 'VIDEO_' + [DateTime]::UtcNow.ToString('yyyyMMddTHHmmssfffZ') + '_' + [guid]::NewGuid().ToString('N').Substring(0, 8)
        $record = [pscustomobject][ordered]@{
            id = $id; kind = 'video'; side = $selection.side; sceneId = $selection.sceneId
            relativePath = $null; sourcePathLocal = $video.FullName; sourceNameLocal = $video.Name
            sourceLastWriteUtc = $video.LastWriteTimeUtc.ToString('o')
            importedUtc = [DateTime]::UtcNow.ToString('o')
            sizeBytes = [long]$video.Length; sha256 = $hash
            fileIntegrity = 'EXTERNAL_HASH_VERIFIED_AT_INDEX'
            association = 'USER_SELECTED_NOT_CLIENT_VERIFIED'
            privacyReview = 'REVIEW_REQUIRED'; privacyFlags = @('EXTERNAL_LOCAL_PATH')
            videoValidation = 'NOT_DECODED_NOT_SEGMENTED'
            observation = 'NOT_RUN'
        }
        Save-MediaRecord $run $record
        [pscustomobject]@{ status = 'VIDEO_INDEXED_UNVERIFIED'; mediaId = $id; sha256 = $hash; observation = 'NOT_RUN' } | ConvertTo-Json -Compress
    }
    'status' {
        [pscustomobject]@{
            schema = 'tc6-visual-ab-media-status/1'; runId = $run.session.runId
            logsCaptured = @($run.manifest.entries | Where-Object { $_.kind -eq 'log' }).Count
            videosIndexed = @($run.manifest.entries | Where-Object { $_.kind -eq 'video' }).Count
            pendingPrivacyReview = @($run.manifest.entries | Where-Object { $_.privacyReview -eq 'REVIEW_REQUIRED' }).Count
            observation = 'NOT_RUN'
            statement = 'Media capture/indexing is not gameplay or source association verification.'
        } | ConvertTo-Json -Compress -Depth 5
    }
    'verify' {
        $valid = 0; $invalid = 0
        $issues = [Collections.Generic.List[object]]::new()
        foreach ($entry in @($run.manifest.entries)) {
            try {
                if ($entry.kind -eq 'log') {
                    $source = Resolve-RunFile $run.root ([string]$entry.relativePath)
                } elseif ($entry.kind -eq 'video') {
                    if (-not (Test-Path -LiteralPath ([string]$entry.sourcePathLocal) -PathType Leaf)) { throw 'Missing external media' }
                    $source = [string]$entry.sourcePathLocal
                    if ((Get-Item -LiteralPath $source).Attributes -band [IO.FileAttributes]::ReparsePoint) {
                        throw 'External video symlink rejected'
                    }
                } else { throw 'Unknown kind' }
                $actual = (Get-FileHash -LiteralPath $source -Algorithm SHA256).Hash.ToLowerInvariant()
                if ($actual -cne [string]$entry.sha256) { throw 'Hash mismatch' }
                $valid++
            } catch {
                $invalid++
                $issues.Add([pscustomobject]@{ id = $entry.id; issue = 'MISSING_OR_HASH_MISMATCH' })
            }
        }
        [pscustomobject]@{
            schema = 'tc6-visual-ab-media-verification/1'; runId = $run.session.runId
            valid = $valid; invalid = $invalid; issues = @($issues.ToArray())
            observation = 'NOT_RUN'; privacyReview = 'REQUIRED'
        } | ConvertTo-Json -Compress -Depth 6
    }
}
