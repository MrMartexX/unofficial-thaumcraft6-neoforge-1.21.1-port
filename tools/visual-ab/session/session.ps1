[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("start", "select", "capture", "watch", "status", "verify")]
    [string]$Action,
    [string]$ResultsRoot,
    [string]$RunPath,
    [string]$SceneId,
    [ValidateSet("legacy", "modern")][string]$Side,
    [string]$FilePath,
    [string]$ScreenshotsDirectory,
    [string]$ConfigPath,
    [string]$RuntimeTargetSha,
    [string]$PairKey,
    [ValidateRange(1, 3600)][int]$MaxSeconds = 90,
    [ValidateRange(1, 1000)][int]$MaxFiles = 1
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$toolRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "..")).Path
$candidateCatalogPath = Join-Path $toolRoot "catalog/v0-state-candidates.json"
$candidateCatalog = Get-Content -LiteralPath $candidateCatalogPath -Raw -Encoding UTF8 | ConvertFrom-Json
if ($candidateCatalog.schema -ne "tc6-visual-ab-scene-candidates/1") {
    throw "Unsupported state candidate catalog"
}
$knownIds = @($candidateCatalog.scenes | ForEach-Object { $_.id })

function Assert-Scene([string]$Id) {
    if ([string]::IsNullOrWhiteSpace($Id) -or $script:knownIds -cnotcontains $Id) {
        throw "SceneId is not in the pinned state-candidate catalog: $Id"
    }
}

function Write-AtomicJson([string]$Destination, [object]$Value) {
    $parent = Split-Path -Parent $Destination
    if (-not (Test-Path -LiteralPath $parent -PathType Container)) {
        throw "Target directory not found: $parent"
    }
    $temp = Join-Path $parent (".tc6-ab-" + [guid]::NewGuid().ToString("N") + ".tmp")
    $json = $Value | ConvertTo-Json -Depth 30
    [System.IO.File]::WriteAllText($temp, $json + "`n", [System.Text.UTF8Encoding]::new($false))
    $backup = Join-Path $parent (".tc6-ab-" + [guid]::NewGuid().ToString("N") + ".bak")
    $committed = $false
    try {
        if (Test-Path -LiteralPath $Destination -PathType Leaf) {
            # Windows requires a non-empty backup path for File.Replace; keep
            # the previous state until the replacement has succeeded.
            [System.IO.File]::Replace($temp, $Destination, $backup)
        } else {
            [System.IO.File]::Move($temp, $Destination)
        }
        $committed = $true
    } finally {
        if (Test-Path -LiteralPath $temp -PathType Leaf) {
            Remove-Item -LiteralPath $temp -Force -ErrorAction SilentlyContinue
        }
        if ($committed -and (Test-Path -LiteralPath $backup -PathType Leaf)) {
            Remove-Item -LiteralPath $backup -Force -ErrorAction SilentlyContinue
        }
    }
}

function Read-Session([string]$Directory) {
    if ([string]::IsNullOrWhiteSpace($Directory)) { throw "RunPath is required for this action" }
    if (-not (Test-Path -LiteralPath $Directory -PathType Container)) { throw "Run directory not found: $Directory" }
    $resolved = (Resolve-Path -LiteralPath $Directory).Path
    $statePath = Join-Path $resolved "session.json"
    if (-not (Test-Path -LiteralPath $statePath -PathType Leaf)) { throw "Missing session.json: $resolved" }
    $state = Get-Content -LiteralPath $statePath -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($state.schema -ne "tc6-visual-ab-session/1") { throw "Unsupported session schema" }
    if ($state.catalogVersion -ne $script:candidateCatalog.version) {
        throw "Scene catalog changed since this session started; use the pinned catalog"
    }
    return [pscustomobject]@{ path = $resolved; statePath = $statePath; state = $state }
}

function Assert-Png([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { throw "PNG file not found: $Path" }
    if ([System.IO.Path]::GetExtension($Path).ToLowerInvariant() -ne ".png") {
        throw "Only Minecraft F2 PNG screenshots are supported by this collector"
    }
    $stream = [System.IO.File]::Open($Path, [System.IO.FileMode]::Open, [System.IO.FileAccess]::Read, [System.IO.FileShare]::Read)
    try {
        if ($stream.Length -lt 8) { throw "Invalid PNG: too short" }
        $sig = [byte[]]::new(8)
        if ($stream.Read($sig, 0, 8) -ne 8) { throw "Invalid PNG: cannot read signature" }
        $expected = [byte[]](137, 80, 78, 71, 13, 10, 26, 10)
        for ($i = 0; $i -lt 8; $i++) {
            if ($sig[$i] -ne $expected[$i]) { throw "File is not a PNG image" }
        }
    } finally {
        $stream.Dispose()
    }
}

function Import-Png([object]$SessionInfo, [string]$SourcePath, [string]$Method) {
    $state = $SessionInfo.state
    if ($null -eq $state.selection) { throw "Select a scene/side before importing a screenshot" }
    Assert-Png $SourcePath
    $source = Get-Item -LiteralPath $SourcePath -ErrorAction Stop
    $originalHash = (Get-FileHash -LiteralPath $source.FullName -Algorithm SHA256).Hash.ToLowerInvariant()
    $scene = [string]$state.selection.sceneId
    $sideName = [string]$state.selection.side
    $duplicates = @($state.evidence | Where-Object {
        $_.sceneId -ceq $scene -and $_.side -ceq $sideName -and $_.sha256 -ceq $originalHash
    })
    if ($duplicates.Count -gt 0) {
        return [pscustomobject]@{
            status = "SKIPPED_DUPLICATE"; sceneId = $scene; side = $sideName; sha256 = $originalHash
        }
    }

    # Both the scene ID and side are validated against the catalog, so no caller-supplied path segments enter the evidence path.
    Assert-Scene $scene
    if ($sideName -notin @("legacy", "modern")) { throw "Invalid session side" }
    $evidenceDir = Join-Path $SessionInfo.path (Join-Path "evidence" (Join-Path $sideName $scene))
    New-Item -ItemType Directory -Force -Path $evidenceDir | Out-Null
    $captureId = "IMG_" + [DateTime]::UtcNow.ToString("yyyyMMddTHHmmssfffZ") + "_" + [guid]::NewGuid().ToString("N").Substring(0, 8)
    $name = $captureId + ".png"
    $destination = Join-Path $evidenceDir $name
    Copy-Item -LiteralPath $source.FullName -Destination $destination -ErrorAction Stop
    $copiedHash = (Get-FileHash -LiteralPath $destination -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($copiedHash -cne $originalHash) {
        throw "Evidence copy hash mismatch; source remains untouched"
    }
    $relative = "evidence/$sideName/$scene/$name"
    $record = [pscustomobject][ordered]@{
        id = $captureId
        sceneId = $scene
        side = $sideName
        relativePath = $relative
        sourceFileName = $source.Name
        sourceFileLastWriteUtc = $source.LastWriteTimeUtc.ToString("o")
        importedUtc = [DateTime]::UtcNow.ToString("o")
        acquisitionMethod = $Method
        sizeBytes = [long]$source.Length
        sha256 = $originalHash
        integrity = "VALIDATED"
        review = "NOT_REVIEWED"
        observation = "NOT_RUN"
    }
    $state.evidence = @($state.evidence) + @($record)
    $state.updatedUtc = [DateTime]::UtcNow.ToString("o")
    Write-AtomicJson $SessionInfo.statePath $state
    return [pscustomobject]@{
        status = "CAPTURED"; id = $captureId; sceneId = $scene; side = $sideName
        relativePath = $relative; sha256 = $originalHash
    }
}

function Inspect-Evidence([object]$SessionInfo) {
    $root = [System.IO.Path]::GetFullPath($SessionInfo.path).TrimEnd([char[]]@('\', '/'))
    $rootPrefix = $root + [System.IO.Path]::DirectorySeparatorChar
    $valid = 0; $invalid = 0
    $issues = [System.Collections.Generic.List[object]]::new()
    foreach ($item in @($SessionInfo.state.evidence)) {
        $relative = [string]$item.relativePath
        if ($relative -match '(^|[\\/])\.\.([\\/]|$)' -or [System.IO.Path]::IsPathRooted($relative)) {
            $invalid++
            $issues.Add([pscustomobject]@{ id = $item.id; issue = "UNSAFE_RELATIVE_PATH" })
            continue
        }
        $path = [System.IO.Path]::GetFullPath((Join-Path $root $relative))
        if (-not $path.StartsWith($rootPrefix, [StringComparison]::OrdinalIgnoreCase)) {
            $invalid++
            $issues.Add([pscustomobject]@{ id = $item.id; issue = "PATH_OUTSIDE_RUN" })
            continue
        }
        if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
            $invalid++
            $issues.Add([pscustomobject]@{ id = $item.id; issue = "MISSING_FILE" })
            continue
        }
        $hash = (Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash.ToLowerInvariant()
        if ($hash -cne [string]$item.sha256) {
            $invalid++
            $issues.Add([pscustomobject]@{ id = $item.id; issue = "HASH_MISMATCH" })
        } else {
            $valid++
        }
    }
    return [pscustomobject][ordered]@{
        schema = "tc6-visual-ab-evidence-verification/1"
        runId = $SessionInfo.state.runId
        valid = $valid; invalid = $invalid
        issues = @($issues.ToArray())
        runtimeState = "NOT_RUN"
        statement = "Checksum verification does not establish gameplay visual parity."
    }
}

if ($Action -eq "start") {
    if ([string]::IsNullOrWhiteSpace($ResultsRoot)) { throw "ResultsRoot is required to start a session" }
    if ($RuntimeTargetSha -and $RuntimeTargetSha -notmatch '^[0-9a-fA-F]{40}$') {
        throw "RuntimeTargetSha must be a verified full 40-character Git SHA"
    }
    $root = [System.IO.Path]::GetFullPath($ResultsRoot)
    New-Item -ItemType Directory -Force -Path $root | Out-Null
    $runId = "RUN_" + [DateTime]::UtcNow.ToString("yyyyMMddTHHmmssZ") + "_" + [guid]::NewGuid().ToString("N").Substring(0, 8)
    $runDir = Join-Path $root $runId
    New-Item -ItemType Directory -Path $runDir -ErrorAction Stop | Out-Null
    $state = [pscustomobject][ordered]@{
        schema = "tc6-visual-ab-session/1"
        runId = $runId
        catalogVersion = $candidateCatalog.version
        researchBaselineSha = $candidateCatalog.source.researchBaselineSha
        runtimeTargetSha = if ($RuntimeTargetSha) { $RuntimeTargetSha.ToLowerInvariant() } else { $null }
        runtimeTargetVerification = "NOT_VERIFIED"
        worldPairKey = if ($PairKey) { $PairKey } else { $null }
        calibrationStatus = "CALIBRATION_REQUIRED"
        createdUtc = [DateTime]::UtcNow.ToString("o")
        updatedUtc = [DateTime]::UtcNow.ToString("o")
        selection = $null
        evidence = @()
        observation = "NOT_RUN"
        review = "NOT_REVIEWED"
    }
    Write-AtomicJson (Join-Path $runDir "session.json") $state
    [pscustomobject]@{
        status = "SESSION_CREATED"; runId = $runId; runPath = $runDir
        catalogVersion = $state.catalogVersion; observation = "NOT_RUN"
    } | ConvertTo-Json -Compress -Depth 6
    exit 0
}

$sessionInfo = Read-Session $RunPath
$state = $sessionInfo.state
switch ($Action) {
    "select" {
        Assert-Scene $SceneId
        if (-not $Side) { throw "Side is required (legacy or modern)" }
        $state.selection = [pscustomobject]@{
            sceneId = $SceneId; side = $Side; selectedUtc = [DateTime]::UtcNow.ToString("o")
        }
        $state.updatedUtc = [DateTime]::UtcNow.ToString("o")
        Write-AtomicJson $sessionInfo.statePath $state
        [pscustomobject]@{
            status = "SCENE_SELECTED"; sceneId = $SceneId; side = $Side
            observation = "NOT_RUN"
        } | ConvertTo-Json -Compress -Depth 5
    }
    "capture" {
        if (-not $FilePath) { throw "FilePath is required for manual capture import" }
        Import-Png $sessionInfo $FilePath "MANUAL_IMPORT" | ConvertTo-Json -Compress -Depth 5
    }
    "status" {
        [pscustomobject][ordered]@{
            schema = "tc6-visual-ab-session-status/1"
            runId = $state.runId
            runPath = $sessionInfo.path
            activeSceneId = if ($null -ne $state.selection) { $state.selection.sceneId } else { $null }
            activeSide = if ($null -ne $state.selection) { $state.selection.side } else { $null }
            evidenceCount = @($state.evidence).Count
            calibrationStatus = $state.calibrationStatus
            runtimeTargetSha = $state.runtimeTargetSha
            observation = $state.observation
            review = $state.review
        } | ConvertTo-Json -Compress -Depth 6
    }
    "verify" {
        Inspect-Evidence $sessionInfo | ConvertTo-Json -Compress -Depth 8
    }
    "watch" {
        if ($null -eq $state.selection) { throw "Select a scene/side before watching screenshots" }
        if (-not $ScreenshotsDirectory) {
            if (-not $ConfigPath) {
                $repoRoot = (Resolve-Path -LiteralPath (Join-Path $toolRoot "../..")).Path
                $ConfigPath = Join-Path $repoRoot "tools/reports/local/visual-ab/config.local.json"
            }
            if (-not (Test-Path -LiteralPath $ConfigPath -PathType Leaf)) {
                throw "Specify ScreenshotsDirectory or a valid ConfigPath"
            }
            $config = Get-Content -LiteralPath $ConfigPath -Raw -Encoding UTF8 | ConvertFrom-Json
            $activeSide = [string]$state.selection.side
            $ScreenshotsDirectory = Join-Path ([string]$config.profiles.$activeSide.gameDirectory) "screenshots"
        }
        if (-not (Test-Path -LiteralPath $ScreenshotsDirectory -PathType Container)) {
            throw "Minecraft screenshots directory not found: $ScreenshotsDirectory"
        }
        $known = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::OrdinalIgnoreCase)
        foreach ($f in @(Get-ChildItem -LiteralPath $ScreenshotsDirectory -File -Filter "*.png")) {
            [void]$known.Add($f.Name)
        }
        Write-Host "Watching for NEW F2 PNGs, scene=$($state.selection.sceneId), side=$($state.selection.side), maxSeconds=$MaxSeconds, maxFiles=$MaxFiles"
        Write-Host "Existing screenshots are ignored. Press F2 in Minecraft while this command is running."
        $started = [DateTime]::UtcNow; $imported = 0
        while (([DateTime]::UtcNow - $started).TotalSeconds -lt $MaxSeconds -and $imported -lt $MaxFiles) {
            foreach ($candidate in @(Get-ChildItem -LiteralPath $ScreenshotsDirectory -File -Filter "*.png")) {
                if ($known.Contains($candidate.Name)) { continue }
                try {
                    # F2 may still be writing the PNG. Import only after a short stable-size check.
                    $sizeBefore = $candidate.Length
                    if ($sizeBefore -le 8) { continue }
                    Start-Sleep -Milliseconds 350
                    $latest = Get-Item -LiteralPath $candidate.FullName -ErrorAction Stop
                    if ($latest.Length -ne $sizeBefore) { continue }
                    $freshSession = Read-Session $sessionInfo.path
                    if ($freshSession.state.selection.sceneId -cne $state.selection.sceneId -or
                        $freshSession.state.selection.side -cne $state.selection.side) {
                        throw "Active scene or side changed while watcher was running; capture aborted to avoid misattribution"
                    }
                    $result = Import-Png $freshSession $candidate.FullName "WATCHED_NEW_FILE"
                    [void]$known.Add($candidate.Name)
                    if ($result.status -eq "CAPTURED") { $imported++ }
                    Write-Host ("Capture: " + ($result | ConvertTo-Json -Compress -Depth 4))
                    if ($imported -ge $MaxFiles) { break }
                } catch [System.IO.IOException] {
                    # File may be locked for a brief time; retry during the next scan.
                }
            }
            if ($imported -lt $MaxFiles) { Start-Sleep -Milliseconds 500 }
        }
        [pscustomobject]@{
            status = if ($imported -ge $MaxFiles) { "CAPTURE_LIMIT_REACHED" } else { "WATCH_TIMEOUT" }
            registeredCount = $imported
            requestedCount = $MaxFiles
            observation = "NOT_RUN"
        } | ConvertTo-Json -Compress -Depth 5
    }
}
