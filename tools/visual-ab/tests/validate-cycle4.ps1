[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$sessionCli = Join-Path $PSScriptRoot '../session/session.ps1'
$mediaCli = Join-Path $PSScriptRoot '../media/media.ps1'
$environmentCli = Join-Path $PSScriptRoot '../environment/inspect.ps1'
$videoCli = Join-Path $PSScriptRoot '../video/plan.ps1'
$reportCli = Join-Path $PSScriptRoot '../reporting/report.ps1'
$controllerCli = Join-Path $PSScriptRoot '../session/controller.ps1'
foreach ($file in @($environmentCli, $videoCli, $controllerCli, $reportCli, $PSCommandPath)) {
    $tokens = $null; $parseErrors = $null
    $null = [Management.Automation.Language.Parser]::ParseFile($file, [ref]$tokens, [ref]$parseErrors)
    if (@($parseErrors).Count -gt 0) { throw "PowerShell AST parse failed: $file`: $($parseErrors[0])" }
}

$scratch = Join-Path ([IO.Path]::GetTempPath()) ('TC6_AB_C4_' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $scratch -ErrorAction Stop | Out-Null
try {
    $legacyDir = Join-Path $scratch 'fake-legacy'
    $modernDir = Join-Path $scratch 'fake-modern'
    foreach ($dir in @($legacyDir, $modernDir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        New-Item -ItemType Directory -Path (Join-Path $dir 'mods') -Force | Out-Null
    }
    [IO.File]::WriteAllText((Join-Path $legacyDir 'options.txt'),
        "fov:0.0`ngamma:0.0`nguiScale:3`nrenderDistance:12`nmaxFps:120`nresourcePacks:[]`n")
    [IO.File]::WriteAllText((Join-Path $modernDir 'options.txt'),
        "fov:0.0`ngamma:0.5`nguiScale:4`nrenderDistance:32`nmaxFps:260`nresourcePacks:[`"vanilla`",`"mod_resources`"]`n")
    [IO.File]::WriteAllBytes((Join-Path $legacyDir 'mods/Thaumcraft-1.12.2-6.1.BETA26.jar'), [byte[]](1, 2, 3))
    [IO.File]::WriteAllBytes((Join-Path $modernDir 'mods/port-synthetic.jar'), [byte[]](4, 5, 6))
    New-Item -ItemType Directory -Path (Join-Path $modernDir 'resourcepacks') -Force | Out-Null
    [IO.File]::WriteAllBytes((Join-Path $modernDir 'resourcepacks/PrivateFixturePack.zip'), [byte[]](7))
    $configPath = Join-Path $scratch 'config.local.json'
    $config = [pscustomobject]@{
        schema = 'tc6-visual-ab-host-config/1'
        profiles = [pscustomobject]@{
            legacy = [pscustomobject]@{ gameDirectory = $legacyDir; minecraft = '1.12.2'; expectedMods = @('Thaumcraft-*') }
            modern = [pscustomobject]@{ gameDirectory = $modernDir; minecraft = '1.21.1'; expectedMods = @('port-*') }
        }
    }
    [IO.File]::WriteAllText($configPath, ($config | ConvertTo-Json -Depth 8), [Text.UTF8Encoding]::new($false))

    $session = (& pwsh -NoProfile -File $sessionCli -Action start -ResultsRoot (Join-Path $scratch 'runs') -PairKey 'SYNTHETIC_C4') | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $session.status -ne 'SESSION_CREATED') { throw 'Cycle4 synthetic RUN failed' }
    $run = [string]$session.runPath
    $capture = (& pwsh -NoProfile -File $environmentCli -Action capture -RunPath $run -ConfigPath $configPath) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $capture.status -ne 'ENV_SNAPSHOT_RECORDED_CLIENT_UNVERIFIED' -or
        $capture.calibration -ne 'CALIBRATION_REQUIRED' -or $capture.observation -ne 'NOT_RUN' -or
        @($capture.rawMismatchKeys) -cnotcontains 'gamma' -or @($capture.rawMismatchKeys) -cnotcontains 'guiScale') {
        throw 'Read-only environment capture falsely calibrated a synthetic mismatch'
    }
    $status = (& pwsh -NoProfile -File $environmentCli -Action status -RunPath $run) | ConvertFrom-Json
    $verify = (& pwsh -NoProfile -File $environmentCli -Action verify -RunPath $run -ConfigPath $configPath) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $status.snapshotCount -ne 1 -or
        $verify.status -ne 'DISK_FINGERPRINTS_UNCHANGED_CLIENT_UNVERIFIED' -or @($verify.driftFields).Count -ne 0) {
        throw 'Environment snapshot status/fingerprint verification failed'
    }
    $environment = Get-Content -LiteralPath (Join-Path $run 'environment.json') -Raw -Encoding UTF8 | ConvertFrom-Json
    if (@($environment.snapshots).Count -ne 1 -or $environment.snapshots[0].legacy.javaRuntime -ne 'NOT_OBSERVED') {
        throw 'Java/runtime was incorrectly attested'
    }
    [IO.File]::AppendAllText((Join-Path $modernDir 'options.txt'), "maxFps:75`n")
    $drift = (& pwsh -NoProfile -File $environmentCli -Action verify -RunPath $run -ConfigPath $configPath) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $drift.status -ne 'DISK_FINGERPRINT_DRIFT' -or
        @($drift.driftFields) -cnotcontains 'modern/optionsSha256') { throw 'Modified synthetic graphics options were not detected' }

    $videoLegacy = Join-Path $scratch 'fake-legacy.mp4'
    $videoModern = Join-Path $scratch 'fake-modern.mp4'
    [IO.File]::WriteAllBytes($videoLegacy, [byte[]](0..127))
    [IO.File]::WriteAllBytes($videoModern, [byte[]](128..255))
    $null = & pwsh -NoProfile -File $sessionCli -Action select -RunPath $run -SceneId 'G00.W-05.BASELINE.OVERVIEW' -Side legacy
    if ($LASTEXITCODE -ne 0) { throw 'Cannot select legacy side' }
    $legacyIndex = (& pwsh -NoProfile -File $mediaCli -Action index-video -RunPath $run -VideoPath $videoLegacy) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $legacyIndex.status -ne 'VIDEO_INDEXED_UNVERIFIED') { throw 'Legacy video index failed' }
    $null = & pwsh -NoProfile -File $sessionCli -Action select -RunPath $run -SceneId 'G00.W-05.BASELINE.OVERVIEW' -Side modern
    if ($LASTEXITCODE -ne 0) { throw 'Cannot select modern side' }
    $modernIndex = (& pwsh -NoProfile -File $mediaCli -Action index-video -RunPath $run -VideoPath $videoModern) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $modernIndex.status -ne 'VIDEO_INDEXED_UNVERIFIED') { throw 'Modern video index failed' }
    $videoArgs = @('-Action', 'plan', '-RunPath', $run, '-LegacyVideoId', [string]$legacyIndex.mediaId,
        '-ModernVideoId', [string]$modernIndex.mediaId, '-LegacyStartSeconds', '2.5',
        '-ModernStartSeconds', '3.25', '-DurationSeconds', '6')
    $plan = (& pwsh -NoProfile -File $videoCli @videoArgs) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $plan.status -ne 'VIDEO_PLAN_CREATED_UNVERIFIED' -or $plan.observation -ne 'NOT_RUN') {
        throw 'Unverified video segment plan failed'
    }
    $dup = (& pwsh -NoProfile -File $videoCli @videoArgs) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $dup.status -ne 'VIDEO_PLAN_DUPLICATE') { throw 'Duplicate video plan was accepted' }
    $planVerify = (& pwsh -NoProfile -File $videoCli -Action verify -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $planVerify.valid -ne 1 -or $planVerify.invalid -ne 0 -or
        $planVerify.cameraSynchronized -ne $false) { throw 'Video plan unexpectedly failed SHA or claimed camera sync' }
    $badPlan = @('-Action', 'plan', '-RunPath', $run, '-LegacyVideoId', [string]$modernIndex.mediaId,
        '-ModernVideoId', [string]$legacyIndex.mediaId, '-LegacyStartSeconds', '0',
        '-ModernStartSeconds', '0', '-DurationSeconds', '2')
    $null = & pwsh -NoProfile -File $videoCli @badPlan 2>&1
    if ($LASTEXITCODE -eq 0) { throw 'Wrong-side videos accepted into a plan' }

    $pngFile = Join-Path $scratch 'fake.png'
    [IO.File]::WriteAllBytes($pngFile, [Convert]::FromBase64String('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO8JmN8AAAAASUVORK5CYII='))
    $registered = (& pwsh -NoProfile -File $sessionCli -Action capture -RunPath $run -FilePath $pngFile) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $registered.status -ne 'CAPTURED') { throw 'Synthetic screenshot capture failed' }
    $report = (& pwsh -NoProfile -File $reportCli -Action build -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $report.status -ne 'REPORT_CREATED') { throw 'Cycle4 report build failed' }
    $jsonText = Get-Content -LiteralPath $report.jsonPath -Raw -Encoding UTF8
    $json = $jsonText | ConvertFrom-Json
    if ($json.environment.snapshotCount -ne 1 -or $json.videoPlans.count -ne 1 -or
        $json.observation -ne 'NOT_RUN' -or $json.environment.calibration -ne 'CALIBRATION_REQUIRED' -or
        $jsonText.Contains('PrivateFixturePack') -or $jsonText.Contains('fake-modern.mp4') -or
        $jsonText.Contains($legacyDir)) { throw 'Report calibration/privacy contract failed' }
    $exported = (& pwsh -NoProfile -File $reportCli -Action export -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $exported.status -ne 'EXPORT_VERIFIED') { throw 'Cycle4 private-safe ZIP export failed' }
    $zip = [IO.Compression.ZipFile]::OpenRead([string]$exported.path)
    try {
        foreach ($entry in $zip.Entries) {
            if ($entry.FullName -in @('environment.json', 'video-plans.json', 'media.json') -or
                $entry.FullName -like 'videos/*' -or $entry.FullName -like 'logs/*') {
                throw 'Private environment/video data escaped ZIP exclusion'
            }
        }
    } finally { $zip.Dispose() }
    [IO.File]::AppendAllText($videoModern, 'SYNTHETIC_DRIFT')
    $changedVideo = (& pwsh -NoProfile -File $videoCli -Action verify -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $changedVideo.invalid -ne 1 -or $changedVideo.valid -ne 0) {
        throw 'Modified video file was not detected'
    }
    Write-Host 'PASS: read-only version-aware environment snapshots; calibration never auto-confirmed'
    Write-Host 'PASS: options fingerprint drift; video ID pairing, manual offsets, duplicates and tamper detection'
    Write-Host 'PASS: private options/video manifests excluded from ZIP; gameplay still NOT_RUN'
} finally {
    if (Test-Path -LiteralPath $scratch -PathType Container) {
        Remove-Item -LiteralPath $scratch -Recurse -Force
    }
}
