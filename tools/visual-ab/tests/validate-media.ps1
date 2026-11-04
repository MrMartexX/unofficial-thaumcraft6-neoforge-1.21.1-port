[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$sessionCli = Join-Path $PSScriptRoot '../session/session.ps1'
$mediaCli = Join-Path $PSScriptRoot '../media/media.ps1'
$reportCli = Join-Path $PSScriptRoot '../reporting/report.ps1'
$controllerCli = Join-Path $PSScriptRoot '../session/controller.ps1'
foreach ($scriptPath in @($mediaCli, $reportCli, $controllerCli, $PSCommandPath)) {
    $tokens = $null; $parseErrors = $null
    $null = [System.Management.Automation.Language.Parser]::ParseFile($scriptPath, [ref]$tokens, [ref]$parseErrors)
    if (@($parseErrors).Count -gt 0) { throw "PowerShell parse failed: $scriptPath`: $($parseErrors[0])" }
}

$scratch = Join-Path ([IO.Path]::GetTempPath()) ('TC6_AB_MEDIA_' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $scratch -ErrorAction Stop | Out-Null
try {
    $log = Join-Path $scratch 'synthetic.log'
    # Synthetic signals only, no real Minecraft/user log or personal data involved.
    $syntheticText = 'Test path: C:\Users\FixturePerson\AppData; fake@example.invalid; 192.0.2.5; bearer SYNTHETIC_ONLY'
    [IO.File]::WriteAllText($log, $syntheticText, [Text.UTF8Encoding]::new($false))
    $logHash = (Get-FileHash -LiteralPath $log -Algorithm SHA256).Hash.ToLowerInvariant()
    $video = Join-Path $scratch 'synthetic.mp4'
    [IO.File]::WriteAllBytes($video, [byte[]](0..127))
    $videoHash = (Get-FileHash -LiteralPath $video -Algorithm SHA256).Hash.ToLowerInvariant()

    $created = (& pwsh -NoProfile -File $sessionCli -Action start -ResultsRoot (Join-Path $scratch 'runs') -PairKey 'SYNTHETIC_MEDIA_ONLY') | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $created.status -ne 'SESSION_CREATED') { throw 'Synthetic media session creation failed' }
    $run = [string]$created.runPath
    $selected = (& pwsh -NoProfile -File $sessionCli -Action select -RunPath $run -SceneId 'G00.W-05.BASELINE.OVERVIEW' -Side modern) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $selected.status -ne 'SCENE_SELECTED') { throw 'Synthetic scene selection failed' }

    $capturedLog = (& pwsh -NoProfile -File $mediaCli -Action snapshot-log -RunPath $run -LogPath $log) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $capturedLog.status -ne 'LOG_CAPTURED_PRIVACY_REVIEW_REQUIRED' -or
        $capturedLog.sha256 -cne $logHash -or $capturedLog.observation -ne 'NOT_RUN') { throw 'Synthetic log snapshot failed' }
    if (@($capturedLog.privacyFlags).Count -ne 4) { throw 'Expected four synthetic privacy signal labels' }
    $duplicateLog = (& pwsh -NoProfile -File $mediaCli -Action snapshot-log -RunPath $run -LogPath $log) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $duplicateLog.status -ne 'SKIPPED_DUPLICATE') { throw 'Duplicate log accepted' }

    $indexed = (& pwsh -NoProfile -File $mediaCli -Action index-video -RunPath $run -VideoPath $video) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $indexed.status -ne 'VIDEO_INDEXED_UNVERIFIED' -or $indexed.sha256 -cne $videoHash) {
        throw 'Synthetic video indexing failed'
    }
    $duplicateVideo = (& pwsh -NoProfile -File $mediaCli -Action index-video -RunPath $run -VideoPath $video) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $duplicateVideo.status -ne 'SKIPPED_DUPLICATE') { throw 'Duplicate video accepted' }

    $status = (& pwsh -NoProfile -File $mediaCli -Action status -RunPath $run) | ConvertFrom-Json
    $verified = (& pwsh -NoProfile -File $mediaCli -Action verify -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $status.logsCaptured -ne 1 -or $status.videosIndexed -ne 1 -or
        $status.pendingPrivacyReview -ne 2 -or $verified.valid -ne 2 -or $verified.invalid -ne 0) {
        throw 'Media inventory/hash verification failed'
    }

    $manifest = Get-Content -LiteralPath (Join-Path $run 'media.json') -Raw -Encoding UTF8 | ConvertFrom-Json
    $logEntry = @($manifest.entries | Where-Object { $_.kind -eq 'log' })
    $videoEntry = @($manifest.entries | Where-Object { $_.kind -eq 'video' })
    if ($logEntry.Count -ne 1 -or $videoEntry.Count -ne 1 -or
        $logEntry[0].privacyReview -ne 'REVIEW_REQUIRED' -or
        $videoEntry[0].videoValidation -ne 'NOT_DECODED_NOT_SEGMENTED') { throw 'Unexpected private media manifest' }
    $copy = Join-Path $run $logEntry[0].relativePath
    if (-not (Test-Path -LiteralPath $copy -PathType Leaf) -or
        (Get-FileHash -LiteralPath $copy -Algorithm SHA256).Hash.ToLowerInvariant() -cne $logHash) {
        throw 'Tool-owned copy is missing or corrupted'
    }

    $image = Join-Path $scratch 'synthetic.png'
    $png = 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO8JmN8AAAAASUVORK5CYII='
    [IO.File]::WriteAllBytes($image, [Convert]::FromBase64String($png))
    $capture = (& pwsh -NoProfile -File $sessionCli -Action capture -RunPath $run -FilePath $image) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $capture.status -ne 'CAPTURED') { throw 'Cannot register synthetic screenshot' }

    $report = (& pwsh -NoProfile -File $reportCli -Action build -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $report.status -ne 'REPORT_CREATED') { throw 'Media report build failed' }
    $reportText = Get-Content -LiteralPath $report.jsonPath -Raw -Encoding UTF8
    $reportData = $reportText | ConvertFrom-Json
    $scene = @($reportData.sceneResults | Where-Object { $_.sceneId -eq 'G00.W-05.BASELINE.OVERVIEW' })
    if ($reportData.media.logCopiesRecorded -ne 1 -or $reportData.media.externalVideosIndexed -ne 1 -or
        $reportData.media.exportedMediaFiles -ne 0 -or $reportData.observation -ne 'NOT_RUN' -or
        $scene.Count -ne 1 -or $scene[0].processing -ne 'PENDING_OTHER_MEDIA') {
        throw 'Media inventory was confused with reviewed game evidence'
    }
    if ($reportText.Contains($syntheticText) -or $reportText.Contains($video) -or
        $reportText.Contains('FixturePerson') -or $reportText.Contains('fake@example.invalid') -or
        $reportText.Contains('synthetic.mp4') -or $reportText.Contains('sourcePathLocal')) {
        throw 'Private media data leaked into report JSON'
    }

    $exported = (& pwsh -NoProfile -File $reportCli -Action export -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $exported.status -ne 'EXPORT_VERIFIED') { throw 'Media-safe export failed' }
    $zip = [IO.Compression.ZipFile]::OpenRead([string]$exported.path)
    try {
        $names = @($zip.Entries | ForEach-Object { $_.FullName })
        if (@($names | Where-Object { $_ -like 'logs/*' -or $_ -like 'videos/*' -or $_ -eq 'media.json' }).Count -gt 0 -or
            $names.Count -ne 4) { throw 'Private logs/video or media manifest exposed in ZIP' }
    } finally { $zip.Dispose() }

    # Corrupt ONLY a synthetic tool-owned log copy: hash verifier must detect it.
    [IO.File]::AppendAllText($copy, 'SYNTHETIC_TAMPER')
    $bad = (& pwsh -NoProfile -File $mediaCli -Action verify -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $bad.invalid -ne 1 -or $bad.valid -ne 1 -or
        (Get-FileHash -LiteralPath $log -Algorithm SHA256).Hash.ToLowerInvariant() -cne $logHash -or
        (Get-FileHash -LiteralPath $video -Algorithm SHA256).Hash.ToLowerInvariant() -cne $videoHash) {
        throw 'Tampered synthetic copy was not detected or source changed'
    }
    Write-Host 'PASS: synthetic logs copied privately, four PII signals flagged, duplicate intake rejected'
    Write-Host 'PASS: external videos indexed by SHA256 without decode/copy; media inventory and tamper detection'
    Write-Host 'PASS: report counts only, ZIP omits private media; gameplay and media review remain NOT_RUN'
} finally {
    if (Test-Path -LiteralPath $scratch -PathType Container) {
        Remove-Item -LiteralPath $scratch -Recurse -Force
    }
}
