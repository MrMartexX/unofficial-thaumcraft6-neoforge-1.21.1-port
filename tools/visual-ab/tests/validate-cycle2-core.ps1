[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$cli = Join-Path $PSScriptRoot "../session/session.ps1"
if (-not (Test-Path -LiteralPath $cli -PathType Leaf)) { throw "Missing session controller" }
$testRoot = Join-Path ([System.IO.Path]::GetTempPath()) ("TC6_AB_TEST_" + [guid]::NewGuid().ToString("N"))
New-Item -ItemType Directory -Path $testRoot -ErrorAction Stop | Out-Null
try {
    $sourceDir = Join-Path $testRoot "original"
    New-Item -ItemType Directory -Path $sourceDir | Out-Null
    $sourceImage = Join-Path $sourceDir "2026-11-04_18.45.00.png"
    # A tiny real PNG with a correct signature, not a fake text file renamed to .png.
    $pngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO8JmN8AAAAASUVORK5CYII="
    [System.IO.File]::WriteAllBytes($sourceImage, [Convert]::FromBase64String($pngBase64))
    $sourceHash = (Get-FileHash -LiteralPath $sourceImage -Algorithm SHA256).Hash
    $resultsRoot = Join-Path $testRoot "results"

    $createdRaw = & pwsh -NoProfile -File $cli -Action start -ResultsRoot $resultsRoot -PairKey "TEST_ONLY"
    if ($LASTEXITCODE -ne 0) { throw "Session start failed" }
    $created = $createdRaw | ConvertFrom-Json
    if ($created.status -ne "SESSION_CREATED" -or -not (Test-Path -LiteralPath $created.runPath)) {
        throw "Session start did not produce a new run directory"
    }
    $runPath = [string]$created.runPath

    $selectionRaw = & pwsh -NoProfile -File $cli -Action select -RunPath $runPath -SceneId "G07.V0GUI-seal.PICKUP_ADVANCED.FRONT" -Side legacy
    if ($LASTEXITCODE -ne 0 -or ($selectionRaw | ConvertFrom-Json).status -ne "SCENE_SELECTED") {
        throw "Scene selection failed"
    }

    $firstRaw = & pwsh -NoProfile -File $cli -Action capture -RunPath $runPath -FilePath $sourceImage
    if ($LASTEXITCODE -ne 0) { throw "First screenshot import failed" }
    $first = $firstRaw | ConvertFrom-Json
    if ($first.status -ne "CAPTURED" -or $first.side -ne "legacy") { throw "Invalid first import" }

    $secondRaw = & pwsh -NoProfile -File $cli -Action capture -RunPath $runPath -FilePath $sourceImage
    if ($LASTEXITCODE -ne 0 -or ($secondRaw | ConvertFrom-Json).status -ne "SKIPPED_DUPLICATE") {
        throw "Duplicate screenshot was not detected"
    }

    $watchRaw = & pwsh -NoProfile -File $cli -Action watch -RunPath $runPath -ScreenshotsDirectory $sourceDir -MaxSeconds 1 -MaxFiles 1
    $watchSummary = @($watchRaw | Select-Object -Last 1)[0] | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $watchSummary.registeredCount -ne 0 -or $watchSummary.status -ne "WATCH_TIMEOUT") {
        throw "Watcher incorrectly imported a screenshot that existed before activation"
    }

    $selectionRaw = & pwsh -NoProfile -File $cli -Action select -RunPath $runPath -SceneId "G07.V0GUI-seal.PICKUP_ADVANCED.FRONT" -Side modern
    if ($LASTEXITCODE -ne 0) { throw "Second scene selection failed" }
    $modernRaw = & pwsh -NoProfile -File $cli -Action capture -RunPath $runPath -FilePath $sourceImage
    if ($LASTEXITCODE -ne 0 -or ($modernRaw | ConvertFrom-Json).status -ne "CAPTURED") {
        throw "Modern evidence import failed"
    }

    $verifyRaw = & pwsh -NoProfile -File $cli -Action verify -RunPath $runPath
    if ($LASTEXITCODE -ne 0) { throw "Evidence verify failed" }
    $verified = $verifyRaw | ConvertFrom-Json
    if ($verified.valid -ne 2 -or $verified.invalid -ne 0 -or $verified.runtimeState -ne "NOT_RUN") {
        throw "Evidence checksum state unexpectedly changed"
    }

    $statusRaw = & pwsh -NoProfile -File $cli -Action status -RunPath $runPath
    if ($LASTEXITCODE -ne 0) { throw "Status reload failed" }
    $status = $statusRaw | ConvertFrom-Json
    if ($status.evidenceCount -ne 2 -or $status.observation -ne "NOT_RUN" -or $status.review -ne "NOT_REVIEWED") {
        throw "Session did not persist or produced a false gameplay verdict"
    }
    if ((Get-FileHash -LiteralPath $sourceImage -Algorithm SHA256).Hash -ne $sourceHash) {
        throw "Original screenshot was modified"
    }
    Write-Host "PASS: session start/select, legacy/modern evidence, SHA256, no duplicate, stale F2 ignored"
    Write-Host "PASS: fresh-process status recovery, 2 intact evidence files, no gameplay PASS"
} finally {
    if (Test-Path -LiteralPath $testRoot -PathType Container) {
        Remove-Item -LiteralPath $testRoot -Recurse -Force
    }
}
