[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$core = Join-Path $PSScriptRoot '../video/render-core.ps1'
$cli = Join-Path $PSScriptRoot '../video/render.ps1'
$controller = Join-Path $PSScriptRoot '../session/controller.ps1'
$report = Join-Path $PSScriptRoot '../reporting/report.ps1'
foreach ($path in @($core, $cli, $controller, $report, $PSCommandPath)) {
    $tokens = $null; $errors = $null
    $null = [Management.Automation.Language.Parser]::ParseFile($path, [ref]$tokens, [ref]$errors)
    if (@($errors).Count -gt 0) { throw "PowerShell syntax error in $path`: $($errors[0])" }
}
. $core

$temp = Join-Path ([IO.Path]::GetTempPath()) ('TC6_AB_RENDER_TEST_' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $temp -ErrorAction Stop | Out-Null
try {
    $run = Join-Path $temp 'RUN_SYNTHETIC'
    New-Item -ItemType Directory -Path $run | Out-Null
    $legacyPath = Join-Path $temp 'original legacy.mp4'
    $modernPath = Join-Path $temp 'original modern.mp4'
    $plan = [pscustomobject]@{
        id = 'VPLAN_SYNTHETIC'; sceneId = 'G00.W-05.BASELINE.OVERVIEW'
        legacyVideoId = 'MEDIA_LEFT'; modernVideoId = 'MEDIA_RIGHT'
        legacySha256 = ('a' * 64); modernSha256 = ('b' * 64)
        legacyStartSeconds = 5.25; modernStartSeconds = 7.5; durationSeconds = 10
    }
    $legacy = [pscustomobject]@{ id = 'MEDIA_LEFT'; sceneId = $plan.sceneId; side = 'legacy';
        sourcePathLocal = $legacyPath; sha256 = $plan.legacySha256 }
    $modern = [pscustomobject]@{ id = 'MEDIA_RIGHT'; sceneId = $plan.sceneId; side = 'modern';
        sourcePathLocal = $modernPath; sha256 = $plan.modernSha256 }
    $properties = [pscustomobject]@{ durationSeconds = 30; width = 1280; height = 720; averageFps = 30 }
    $probe = [pscustomobject]@{ id = 'VPROBE_SYNTHETIC'; planId = $plan.id;
        legacyVideoSha256 = $legacy.sha256; modernVideoSha256 = $modern.sha256
        source = 'LOCAL_FFPROBE_JSON'; legacy = $properties; modern = $properties;
        gameplayObservation = 'NOT_RUN' }
    $clipDir = Join-Path $run 'clips/VPLAN_SYNTHETIC/VPREV_SYNTHETIC'
    $preview = [pscustomobject]@{
        id = 'VPREV_SYNTHETIC'; planId = $plan.id; probeId = $probe.id
        ffmpegExecuted = $false; outputsCreated = $false; gameplayObservation = 'NOT_RUN'
        legacyArgumentsLocal = @(Get-Tc6FfmpegClipArguments $legacyPath (Join-Path $clipDir 'legacy.mp4') 5.25 10)
        modernArgumentsLocal = @(Get-Tc6FfmpegClipArguments $modernPath (Join-Path $clipDir 'modern.mp4') 7.5 10)
    }
    $layout = Assert-Tc6PreviewContract $preview $plan $probe $legacy $modern $run
    if ($layout.legacyOutput -cne (Join-Path $clipDir 'legacy.mp4') -or
        @($layout.legacyArguments | Where-Object { $_ -eq '-n' }).Count -ne 1 -or
        @($layout.legacyArguments | Where-Object { $_ -eq '-y' }).Count -ne 0) {
        throw 'Safe new output layout or ffmpeg -n guard missing'
    }
    Assert-Tc6NewClipDirectory $layout $run $plan.id
    $tampered = $preview | ConvertTo-Json -Depth 20 | ConvertFrom-Json
    $tampered.legacyArgumentsLocal[-1] = $legacyPath
    $rejected = $false
    try { $null = Assert-Tc6PreviewContract $tampered $plan $probe $legacy $modern $run }
    catch { $rejected = $true }
    if (-not $rejected) { throw 'Tampered preview could overwrite a video source' }

    $tampered = $preview | ConvertTo-Json -Depth 20 | ConvertFrom-Json
    $tampered.modernArgumentsLocal[4] = '-y'
    $rejected = $false
    try { $null = Assert-Tc6PreviewContract $tampered $plan $probe $legacy $modern $run }
    catch { $rejected = $true }
    if (-not $rejected) { throw 'Tampered preview could enable overwrite' }

    $tampered = $preview | ConvertTo-Json -Depth 20 | ConvertFrom-Json
    $tampered.planId = '../outside'
    $rejected = $false
    try { $null = Assert-Tc6PreviewContract $tampered $plan $probe $legacy $modern $run }
    catch { $rejected = $true }
    if (-not $rejected) { throw 'Preview traversal was accepted' }

    $badSource = $legacy | ConvertTo-Json -Depth 20 | ConvertFrom-Json
    $badSource.sourcePathLocal = Join-Path $run 'clips/old.mp4'
    $rejected = $false
    try { $null = Assert-Tc6PreviewContract $preview $plan $probe $badSource $modern $run }
    catch { $rejected = $true }
    if (-not $rejected) { throw 'Output clips could be registered as raw sources' }

    New-Item -ItemType Directory -Path $clipDir -Force | Out-Null
    $rejected = $false
    try { Assert-Tc6NewClipDirectory $layout $run $plan.id }
    catch { $rejected = $true }
    if (-not $rejected) { throw 'Existing clip directory could be overwritten' }

    $scriptText = Get-Content -LiteralPath $cli -Raw -Encoding UTF8
    $controllerText = Get-Content -LiteralPath $controller -Raw -Encoding UTF8
    $reportText = Get-Content -LiteralPath $report -Raw -Encoding UTF8
    if ($scriptText -notmatch 'WaitForExit\(600000\)' -or
        $scriptText -notmatch 'Get-FileHash' -or
        $controllerText -notmatch 'RENDER \(Enter = отмена\)' -or
        $reportText -notmatch 'video-renders.json' -or
        $reportText -notmatch 'renderedClipsReviewed = \$false') {
        throw 'Safe CLI, explicit confirmation or privacy-safe report contract missing'
    }
    Write-Host 'PASS: synthetic render contracts reject altered commands, -y, traversal and existing outputs'
    Write-Host 'PASS: bounded explicit ffmpeg action and SHA checks; private clips unreviewed, no gameplay PASS'
} finally {
    if (Test-Path -LiteralPath $temp -PathType Container) { Remove-Item -LiteralPath $temp -Recurse -Force }
}
