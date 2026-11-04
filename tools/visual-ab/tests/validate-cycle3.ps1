[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$sessionCli = Join-Path $PSScriptRoot '../session/session.ps1'
$comparisonCli = Join-Path $PSScriptRoot '../comparison/compare.ps1'
$reportCli = Join-Path $PSScriptRoot '../reporting/report.ps1'
foreach ($scriptPath in @($comparisonCli, $reportCli, $PSCommandPath)) {
    $tokens = $null; $parseErrors = $null
    $null = [System.Management.Automation.Language.Parser]::ParseFile($scriptPath, [ref]$tokens, [ref]$parseErrors)
    if (@($parseErrors).Count -gt 0) { throw "PowerShell AST parse failed in $scriptPath`: $($parseErrors[0])" }
}
$scratch = Join-Path ([IO.Path]::GetTempPath()) ('TC6_AB_C3_' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $scratch -ErrorAction Stop | Out-Null
try {
    $image = Join-Path $scratch 'synthetic.png'
    $pngBase64 = 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO8JmN8AAAAASUVORK5CYII='
    [IO.File]::WriteAllBytes($image, [Convert]::FromBase64String($pngBase64))
    $originalHash = (Get-FileHash -LiteralPath $image -Algorithm SHA256).Hash
    $created = (& pwsh -NoProfile -File $sessionCli -Action start -ResultsRoot (Join-Path $scratch 'runs') -PairKey 'SYNTHETIC_ONLY') | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $created.status -ne 'SESSION_CREATED') { throw 'Cannot start synthetic session' }
    $run = [string]$created.runPath
    foreach ($side in @('legacy', 'modern')) {
        $null = & pwsh -NoProfile -File $sessionCli -Action select -RunPath $run -SceneId 'G07.V0GUI-seal.PICKUP_ADVANCED.FRONT' -Side $side
        if ($LASTEXITCODE -ne 0) { throw "Cannot select $side" }
        $captured = (& pwsh -NoProfile -File $sessionCli -Action capture -RunPath $run -FilePath $image) | ConvertFrom-Json
        if ($LASTEXITCODE -ne 0 -or $captured.status -ne 'CAPTURED') { throw "Cannot import $side" }
    }

    $comparison = (& pwsh -NoProfile -File $comparisonCli -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $comparison.status -ne 'REVIEW_VIEWER_CREATED' -or
        $comparison.observation -ne 'NOT_RUN' -or -not (Test-Path -LiteralPath $comparison.htmlPath)) {
        throw 'Offline A/B comparison viewer failed'
    }
    $html = Get-Content -LiteralPath $comparison.htmlPath -Raw -Encoding UTF8
    if ($html -notmatch 'data:image/png;base64,' -or $html -notmatch 'UNALIGNED_FRAMES' -or
        $html -notmatch 'Difference Image' -or $html -notmatch 'opacityText' -or
        $html -notmatch 'updateOverlay' -or $html -match 'wipeDivider') {
        throw 'Missing viewer media, overlay controls or mismatch guard'
    }

    $report = (& pwsh -NoProfile -File $reportCli -Action build -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $report.status -ne 'REPORT_CREATED' -or $report.observation -ne 'NOT_RUN') {
        throw 'Report generation failed'
    }
    $data = Get-Content -LiteralPath $report.jsonPath -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($data.integrity -ne 'VALIDATED' -or $data.observation -ne 'NOT_RUN' -or
        @($data.sceneResults)[0].processing -ne 'PENDING_IMAGES' -or @($data.evidence).Count -ne 2) {
        throw 'Incomplete source-derived scenario was incorrectly marked finished'
    }

    # The real-world G00 transport smoke has 0 required F2s but still lacks logs.
    $null = & pwsh -NoProfile -File $sessionCli -Action select -RunPath $run -SceneId 'G00.W-05.BASELINE.OVERVIEW' -Side modern
    if ($LASTEXITCODE -ne 0) { throw 'Cannot select the log-dependent G00 source card' }
    $g00Report = (& pwsh -NoProfile -File $reportCli -Action build -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0) { throw 'Cannot build G00 pending-media report' }
    $g00Data = Get-Content -LiteralPath $g00Report.jsonPath -Raw -Encoding UTF8 | ConvertFrom-Json
    $g00 = @($g00Data.sceneResults | Where-Object { $_.sceneId -eq 'G00.W-05.BASELINE.OVERVIEW' })
    if ($g00.Count -ne 1 -or $g00[0].processing -ne 'PENDING_OTHER_MEDIA' -or
        $g00[0].observation -ne 'NOT_RUN') { throw 'G00 log requirement was incorrectly considered complete' }

    $export = (& pwsh -NoProfile -File $reportCli -Action export -RunPath $run) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $export.status -ne 'EXPORT_VERIFIED') { throw 'Export failed' }
    $verified = (& pwsh -NoProfile -File $reportCli -Action verify-archive -ArchivePath $export.path) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $verified.status -ne 'ARCHIVE_VERIFIED' -or
        $verified.sha256 -ne $export.sha256 -or $verified.files -ne 5) {
        throw 'Round-trip archive verification failed'
    }
    $importRoot = Join-Path $scratch 'imports'
    $imported = (& pwsh -NoProfile -File $reportCli -Action import -ArchivePath $export.path -ImportRoot $importRoot) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $imported.status -ne 'IMPORTED_VERIFIED') { throw 'Import failed' }
    $repeated = (& pwsh -NoProfile -File $reportCli -Action import -ArchivePath $export.path -ImportRoot $importRoot) | ConvertFrom-Json
    if ($LASTEXITCODE -ne 0 -or $repeated.status -ne 'ALREADY_IMPORTED_VERIFIED' -or $repeated.path -ne $imported.path) {
        throw 'Repeat import is not idempotent'
    }

    # Negative tests operate on copies created by this test and never on user screenshots/worlds.
    $tampered = Join-Path $scratch 'tampered.zip'
    Copy-Item -LiteralPath $export.path -Destination $tampered
    $zip = [IO.Compression.ZipFile]::Open($tampered, [IO.Compression.ZipArchiveMode]::Update)
    try {
        $entry = @($zip.Entries | Where-Object { $_.FullName -like 'evidence/*' })[0]
        $stream = $entry.Open()
        try {
            $stream.Position = 0
            $stream.WriteByte(0)
        } finally { $stream.Dispose() }
    } finally { $zip.Dispose() }
    $null = & pwsh -NoProfile -File $reportCli -Action verify-archive -ArchivePath $tampered 2>&1
    if ($LASTEXITCODE -eq 0) { throw 'Tampered archive passed verification' }

    $invalidZip = Join-Path $scratch 'traversal.zip'
    $zip = [IO.Compression.ZipFile]::Open($invalidZip, [IO.Compression.ZipArchiveMode]::Create)
    try { [void]$zip.CreateEntry('../outside.txt') } finally { $zip.Dispose() }
    $null = & pwsh -NoProfile -File $reportCli -Action verify-archive -ArchivePath $invalidZip 2>&1
    if ($LASTEXITCODE -eq 0) { throw 'Traversal archive was accepted' }

    if ((Get-FileHash -LiteralPath $image -Algorithm SHA256).Hash -ne $originalHash) {
        throw 'Source image changed during Cycle 3 tests'
    }
    Write-Host 'PASS: self-contained A/B HTML, overlay/diff viewer, incomplete evidence stays NOT_RUN'
    Write-Host 'PASS: report, ZIP SHA256, verified import, repeat import idempotence'
    Write-Host 'PASS: tampered/traversal packages rejected; original image unchanged'
    Write-Host 'NOTE: visual camera alignment, logs/video and gameplay parity still require client review'
} finally {
    if (Test-Path -LiteralPath $scratch -PathType Container) {
        Remove-Item -LiteralPath $scratch -Recurse -Force
    }
}
