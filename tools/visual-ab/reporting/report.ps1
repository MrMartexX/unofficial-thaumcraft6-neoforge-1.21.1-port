[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('build', 'export', 'verify-archive', 'import')][string]$Action,
    [string]$RunPath,
    [string]$ArchivePath,
    [string]$ImportRoot
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.ZipFile
$toolRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$catalog = Get-Content -LiteralPath (Join-Path $toolRoot 'catalog/v0-state-candidates.json') -Raw -Encoding UTF8 | ConvertFrom-Json

function Assert-Relative([string]$Relative) {
    if ([string]::IsNullOrWhiteSpace($Relative) -or $Relative.Contains(':') -or $Relative.Contains('\') -or
        $Relative.StartsWith('/') -or $Relative -match '(^|/)\.\.?(/|$)' -or
        $Relative -match '(^|/)[ \t]*$') {
        throw "Unsafe archive/evidence path: $Relative"
    }
    if ($Relative -notmatch '^[a-zA-Z0-9_./-]+$') { throw "Unexpected characters in archive path: $Relative" }
}

function Assert-RegularFile([string]$RunRoot, [string]$Relative) {
    Assert-Relative $Relative
    $root = [IO.Path]::GetFullPath($RunRoot).TrimEnd([char[]]@('/', '\'))
    $path = [IO.Path]::GetFullPath((Join-Path $root $Relative))
    if (-not $path.StartsWith(($root + [IO.Path]::DirectorySeparatorChar), [StringComparison]::OrdinalIgnoreCase)) {
        throw "File escapes run directory: $Relative"
    }
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { throw "Missing file: $Relative" }
    $relativeParts = $Relative.Split('/')
    $partPath = $root
    foreach ($part in $relativeParts) {
        $partPath = Join-Path $partPath $part
        $item = Get-Item -LiteralPath $partPath -ErrorAction Stop
        if ($item.Attributes -band [IO.FileAttributes]::ReparsePoint) {
            throw "Reparse point rejected: $Relative"
        }
    }
    return $path
}

function Read-Run([string]$Directory) {
    if ([string]::IsNullOrWhiteSpace($Directory)) { throw 'RunPath is required' }
    $resolved = (Resolve-Path -LiteralPath $Directory -ErrorAction Stop).Path
    $file = Join-Path $resolved 'session.json'
    $session = Get-Content -LiteralPath $file -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($session.schema -ne 'tc6-visual-ab-session/1' -or $session.catalogVersion -ne $script:catalog.version) {
        throw 'Session schema or catalog version mismatch'
    }
    if ([string]$session.runId -notmatch '^RUN_[A-Za-z0-9_-]+$') { throw 'Invalid session ID' }
    return [pscustomobject]@{ root = $resolved; data = $session }
}

function Get-MediaRecords([object]$Run) {
    $manifestPath = Join-Path $Run.root 'media.json'
    if (-not (Test-Path -LiteralPath $manifestPath -PathType Leaf)) { return @() }
    if ((Get-Item -LiteralPath $manifestPath).Attributes -band [IO.FileAttributes]::ReparsePoint) {
        throw 'Media manifest reparse point rejected'
    }
    $manifest = Get-Content -LiteralPath $manifestPath -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($manifest.schema -ne 'tc6-visual-ab-media/1' -or $manifest.runId -cne $Run.data.runId -or
        $manifest.catalogVersion -cne $Run.data.catalogVersion) { throw 'Invalid media manifest identity' }
    $records = @($manifest.entries)
    if ($records.Count -gt 4096) { throw 'Media manifest entry limit exceeded' }
    $knownSceneIds = @($script:catalog.scenes | ForEach-Object { $_.id })
    foreach ($entry in $records) {
        if ($entry.kind -notin @('log', 'video') -or $entry.side -notin @('legacy', 'modern') -or
            $knownSceneIds -cnotcontains [string]$entry.sceneId -or
            [string]$entry.sha256 -notmatch '^[0-9a-f]{64}$' -or
            $entry.privacyReview -ne 'REVIEW_REQUIRED' -or $entry.observation -ne 'NOT_RUN') {
            throw 'Invalid media record; refusing report'
        }
    }
    return $records
}

function Get-PrivateRecordCount([object]$Run, [string]$FileName, [string]$Schema, [string]$FieldName, [int]$Limit) {
    $path = Join-Path $Run.root $FileName
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return 0 }
    if ((Get-Item -LiteralPath $path).Attributes -band [IO.FileAttributes]::ReparsePoint) {
        throw 'Private manifest reparse point rejected'
    }
    $data = Get-Content -LiteralPath $path -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($data.schema -ne $Schema -or $data.runId -cne $Run.data.runId) {
        throw 'Private manifest identity mismatch'
    }
    $items = @($data.$FieldName)
    if ($items.Count -gt $Limit) { throw 'Private manifest entry limit exceeded' }
    return $items.Count
}

function Get-ReportData([object]$Run) {
    $session = $Run.data
    $mediaRecords = @(Get-MediaRecords $Run)
    $environmentCount = Get-PrivateRecordCount $Run 'environment.json' 'tc6-visual-ab-environment/1' 'snapshots' 100
    $videoPlanCount = Get-PrivateRecordCount $Run 'video-plans.json' 'tc6-visual-ab-video-plans/1' 'plans' 256
    $sceneIds = @($session.evidence | ForEach-Object { $_.sceneId }) + @($session.notes | ForEach-Object { $_.sceneId })
    $sceneIds += @($mediaRecords | ForEach-Object { $_.sceneId })
    if ($null -ne $session.selection) { $sceneIds += [string]$session.selection.sceneId }
    $sceneIds = @($sceneIds | Where-Object { $_ } | Sort-Object -Unique)
    $records = [System.Collections.Generic.List[object]]::new()
    $evidenceOut = [System.Collections.Generic.List[object]]::new()
    $allValid = $true
    $seenPaths = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::OrdinalIgnoreCase)
    foreach ($item in @($session.evidence)) {
        $relative = [string]$item.relativePath
        if (-not $seenPaths.Add($relative)) { throw "Duplicate evidence path: $relative" }
        if ($relative -notmatch '^evidence/(legacy|modern)/[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+\.png$') {
            throw "Unexpected evidence path: $relative"
        }
        $file = Assert-RegularFile $Run.root $relative
        $hash = (Get-FileHash -LiteralPath $file -Algorithm SHA256).Hash.ToLowerInvariant()
        $valid = $hash -ceq [string]$item.sha256
        if (-not $valid) { $allValid = $false }
        $evidenceOut.Add([pscustomobject][ordered]@{
            id = $item.id; sceneId = $item.sceneId; side = $item.side
            relativePath = $relative; sha256 = $item.sha256
            integrity = if ($valid) { 'VALIDATED' } else { 'INVALID' }
        })
    }
    foreach ($sceneId in $sceneIds) {
        $definitions = @($script:catalog.scenes | Where-Object { $_.id -ceq $sceneId })
        if ($definitions.Count -ne 1) { throw "Unknown scene ID: $sceneId" }
        $def = $definitions[0]
        $legacy = @($evidenceOut | Where-Object { $_.sceneId -ceq $sceneId -and $_.side -ceq 'legacy' })
        $modern = @($evidenceOut | Where-Object { $_.sceneId -ceq $sceneId -and $_.side -ceq 'modern' })
        $required = [int]$def.capture.screenshots
        $invalid = @($legacy + $modern | Where-Object { $_.integrity -eq 'INVALID' }).Count -gt 0
        $processing = if ($invalid) { 'INVALID' }
            elseif ($legacy.Count -lt $required -or $modern.Count -lt $required) { 'PENDING_IMAGES' }
            elseif ([bool]$def.capture.logs -or [bool]$def.capture.video) { 'PENDING_OTHER_MEDIA' }
            else { 'CAPTURED_REVIEW_REQUIRED' }
        $records.Add([pscustomobject][ordered]@{
            sceneId = $sceneId
            candidateStatus = $def.stateStatus
            expectedImagesPerSide = $required
            legacyImages = $legacy.Count; modernImages = $modern.Count
            logCopiesRecorded = @($mediaRecords | Where-Object { $_.sceneId -ceq $sceneId -and $_.kind -eq 'log' }).Count
            externalVideosIndexed = @($mediaRecords | Where-Object { $_.sceneId -ceq $sceneId -and $_.kind -eq 'video' }).Count
            logsPending = [bool]$def.capture.logs
            videoPending = [bool]$def.capture.video
            preparationVerified = $false
            observation = 'NOT_RUN'
            processing = $processing
            review = 'NOT_REVIEWED'
        })
    }
    return [pscustomobject][ordered]@{
        schema = 'tc6-visual-ab-report/1'
        generatedUtc = [DateTime]::UtcNow.ToString('o')
        runId = $session.runId
        catalogVersion = $session.catalogVersion
        researchBaselineSha = $session.researchBaselineSha
        runtimeTargetSha = $session.runtimeTargetSha
        runtimeTargetVerification = $session.runtimeTargetVerification
        worldPairKey = $session.worldPairKey
        calibrationStatus = $session.calibrationStatus
        sceneResults = @($records.ToArray())
        evidence = @($evidenceOut.ToArray())
        media = [pscustomobject][ordered]@{
            logCopiesRecorded = @($mediaRecords | Where-Object { $_.kind -eq 'log' }).Count
            externalVideosIndexed = @($mediaRecords | Where-Object { $_.kind -eq 'video' }).Count
            privacyReviewsPending = $mediaRecords.Count
            sourceAssociation = 'NOT_VERIFIED'
            mediaContentReviewed = $false
            exportedMediaFiles = 0
        }
        environment = [pscustomobject][ordered]@{
            snapshotCount = $environmentCount
            clientVerification = 'NOT_RUN'
            calibration = 'CALIBRATION_REQUIRED'
            rawOptionsExcludedFromExport = $true
        }
        videoPlans = [pscustomobject][ordered]@{
            count = $videoPlanCount
            alignmentVerification = 'NOT_RUN'
            videoContentsExcludedFromExport = $true
        }
        integrity = if ($allValid) { 'VALIDATED' } else { 'INVALID' }
        observation = 'NOT_RUN'
        review = 'NOT_REVIEWED'
        notesOmittedForPrivacy = @($session.notes).Count
        statement = 'Filesystem and capture metadata only; no automatic gameplay PASS. Logs and video are not bundled.'
    }
}

function Build-Report([object]$Run) {
    $data = Get-ReportData $Run
    $reportsDir = Join-Path $Run.root 'reports'
    New-Item -ItemType Directory -Path $reportsDir -Force | Out-Null
    $id = 'REPORT_' + [DateTime]::UtcNow.ToString('yyyyMMddTHHmmssfffZ') + '_' + [guid]::NewGuid().ToString('N').Substring(0, 8)
    $jsonPath = Join-Path $reportsDir ($id + '.json')
    $mdPath = Join-Path $reportsDir ($id + '.md')
    $lines = [System.Collections.Generic.List[string]]::new()
    $lines.Add('# Thaumcraft Visual A/B: preliminary evidence report')
    $lines.Add('')
    $lines.Add('Run: `' + $data.runId + '`')
    $lines.Add('Calibration: `' + $data.calibrationStatus + '`')
    $lines.Add('Observation: `NOT_RUN`. Filesystem integrity is NOT gameplay equivalence.')
    $lines.Add('')
    $lines.Add('| Scene | 1.12.2 | 1.21.1 | Processing |')
    $lines.Add('| --- | ---: | ---: | --- |')
    foreach ($scene in $data.sceneResults) {
        $lines.Add('| `' + $scene.sceneId + '` | ' + $scene.legacyImages + ' | ' + $scene.modernImages + ' | `' + $scene.processing + '` |')
    }
    $lines.Add('')
    $lines.Add('Evidence integrity: `' + $data.integrity + '`. SHA-256 is checked for every listed image.')
    $lines.Add('Full game preparation, logs/video, visual alignment and human review remain separate gates.')
    $lines.Add('Local log snapshots: ' + $data.media.logCopiesRecorded + '; external videos indexed: ' + $data.media.externalVideosIndexed + '. Neither was exported or reviewed.')
    $lines.Add('Read-only environment snapshots: ' + $data.environment.snapshotCount + '; unverified video segment plans: ' + $data.videoPlans.count + '. Raw options and timings remain local.')
    $lines.Add('Notes omitted from export by default: ' + $data.notesOmittedForPrivacy + '.')
    [IO.File]::WriteAllText($jsonPath, ($data | ConvertTo-Json -Depth 25) + "`n", [Text.UTF8Encoding]::new($false))
    [IO.File]::WriteAllText($mdPath, ($lines -join "`n") + "`n", [Text.UTF8Encoding]::new($false))
    return [pscustomobject]@{ id = $id; jsonPath = $jsonPath; markdownPath = $mdPath; data = $data }
}

function Hash-Stream([IO.Stream]$Stream) {
    return [Convert]::ToHexString([System.Security.Cryptography.SHA256]::HashData($Stream)).ToLowerInvariant()
}

function Verify-Archive([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { throw "Archive missing: $Path" }
    $zip = [IO.Compression.ZipFile]::OpenRead($Path)
    try {
        $seen = [Collections.Generic.HashSet[string]]::new([StringComparer]::OrdinalIgnoreCase)
        $entries = @($zip.Entries)
        $total = [long]0
        foreach ($entry in $entries) {
            Assert-Relative $entry.FullName
            if (-not $seen.Add($entry.FullName)) { throw "Duplicate archive entry: $($entry.FullName)" }
            if ($entry.FullName.EndsWith('/') -or $entry.Length -gt 128MB) { throw "Directory/oversized entry rejected" }
            $total += $entry.Length
            if ($total -gt 2GB) { throw 'Archive exceeds 2 GiB unpacked limit' }
        }
        $check = $zip.GetEntry('checksums.sha256')
        if ($null -eq $check) { throw 'No checksums.sha256 in archive' }
        $reader = [IO.StreamReader]::new($check.Open(), [Text.Encoding]::UTF8)
        try { $checksumText = $reader.ReadToEnd() } finally { $reader.Dispose() }
        $expected = @{}
        foreach ($line in @($checksumText -split "`n")) {
            if (-not $line.Trim()) { continue }
            if ($line -notmatch '^([0-9a-f]{64})  ([a-zA-Z0-9_./-]+)\r?$') { throw "Invalid checksum record" }
            $checksum = $Matches[1]; $name = $Matches[2]
            Assert-Relative $name
            if ($expected.ContainsKey($name)) { throw "Duplicate checksum entry: $name" }
            $expected[$name] = $checksum
        }
        if (-not $expected.ContainsKey('results.json') -or -not $expected.ContainsKey('report.md')) {
            throw 'Required report files are missing from checksums'
        }
        if ($expected.Count -ne $entries.Count - 1) { throw 'Archive entries do not match checksum manifest' }
        foreach ($entry in $entries) {
            if ($entry.FullName -eq 'checksums.sha256') { continue }
            if (-not $expected.ContainsKey($entry.FullName)) { throw "Unlisted archive entry: $($entry.FullName)" }
            $stream = $entry.Open()
            try { $actual = Hash-Stream $stream } finally { $stream.Dispose() }
            if ($actual -cne $expected[$entry.FullName]) { throw "Archive SHA256 mismatch: $($entry.FullName)" }
        }
        $resultStream = $zip.GetEntry('results.json').Open()
        $resultReader = [IO.StreamReader]::new($resultStream, [Text.Encoding]::UTF8)
        try { $result = $resultReader.ReadToEnd() | ConvertFrom-Json } finally { $resultReader.Dispose() }
        if ($result.schema -ne 'tc6-visual-ab-report/1' -or [string]$result.runId -notmatch '^RUN_[A-Za-z0-9_-]+$') {
            throw 'Invalid report schema/run ID'
        }
        if ($result.catalogVersion -ne $script:catalog.version -or $result.integrity -ne 'VALIDATED' -or
            $result.observation -ne 'NOT_RUN' -or $result.review -ne 'NOT_REVIEWED') {
            throw 'Archive claims an unsupported catalog, integrity or review state'
        }
        if (($null -ne $result.PSObject.Properties['environment'] -and
                ($result.environment.clientVerification -ne 'NOT_RUN' -or $result.environment.calibration -ne 'CALIBRATION_REQUIRED')) -or
            ($null -ne $result.PSObject.Properties['videoPlans'] -and
                $result.videoPlans.alignmentVerification -ne 'NOT_RUN')) {
            throw 'Archive falsely claims client calibration or video alignment'
        }
        $knownSceneIds = @($script:catalog.scenes | ForEach-Object { $_.id })
        foreach ($scene in @($result.sceneResults)) {
            if ($knownSceneIds -cnotcontains [string]$scene.sceneId -or $scene.observation -ne 'NOT_RUN') {
                throw 'Unknown or falsely reviewed scene in archive'
            }
        }
        $evidenceIds = [Collections.Generic.HashSet[string]]::new([StringComparer]::Ordinal)
        foreach ($e in @($result.evidence)) {
            if ($knownSceneIds -cnotcontains [string]$e.sceneId -or $e.side -notin @('legacy', 'modern') -or
                -not $evidenceIds.Add([string]$e.id)) {
                throw 'Invalid evidence identity/scene in archive'
            }
        }
        $listed = @($result.evidence | ForEach-Object { [string]$_.relativePath } | Sort-Object -Unique)
        $archived = @($expected.Keys | Where-Object { $_ -like 'evidence/*' } | Sort-Object -Unique)
        if (($listed -join '|') -cne ($archived -join '|')) { throw 'Report evidence does not match package contents' }
        foreach ($e in @($result.evidence)) {
            if ($expected[[string]$e.relativePath] -cne [string]$e.sha256 -or $e.integrity -ne 'VALIDATED') {
                throw 'Report evidence hashes/integrity do not match package'
            }
        }
        return [pscustomobject]@{ status = 'ARCHIVE_VERIFIED'; runId = $result.runId; files = $entries.Count; sha256 = (Get-FileHash -LiteralPath $Path -Algorithm SHA256).Hash.ToLowerInvariant(); manifest = $expected }
    } finally { $zip.Dispose() }
}

switch ($Action) {
    'build' {
        $run = Read-Run $RunPath
        $built = Build-Report $run
        [pscustomobject]@{ status = 'REPORT_CREATED'; runId = $run.data.runId; jsonPath = $built.jsonPath; markdownPath = $built.markdownPath; integrity = $built.data.integrity; observation = 'NOT_RUN' } | ConvertTo-Json -Depth 5
    }
    'export' {
        $run = Read-Run $RunPath
        $built = Build-Report $run
        if ($built.data.integrity -ne 'VALIDATED') { throw 'Export blocked by invalid evidence integrity' }
        if (@($built.data.evidence).Count -eq 0) { throw 'Export requires at least one registered, validated screenshot' }
        $files = [ordered]@{ 'results.json' = $built.jsonPath; 'report.md' = $built.markdownPath }
        foreach ($entry in @($built.data.evidence)) {
            $relative = [string]$entry.relativePath
            $files[$relative] = Assert-RegularFile $run.root $relative
        }
        $exportDir = Join-Path $run.root 'export'
        New-Item -ItemType Directory -Path $exportDir -Force | Out-Null
        $exportName = 'TC6_AB_' + $run.data.runId + '_' + [guid]::NewGuid().ToString('N').Substring(0, 8) + '.zip'
        $destination = Join-Path $exportDir $exportName
        $zip = [IO.Compression.ZipFile]::Open($destination, [IO.Compression.ZipArchiveMode]::Create)
        try {
            $lines = [System.Collections.Generic.List[string]]::new()
            foreach ($key in $files.Keys) {
                Assert-Relative $key
                [void][IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zip, $files[$key], $key, [IO.Compression.CompressionLevel]::Optimal)
                $hash = (Get-FileHash -LiteralPath $files[$key] -Algorithm SHA256).Hash.ToLowerInvariant()
                $lines.Add($hash + '  ' + $key)
            }
            $manifestEntry = $zip.CreateEntry('checksums.sha256')
            $writer = [IO.StreamWriter]::new($manifestEntry.Open(), [Text.UTF8Encoding]::new($false))
            try { $writer.Write(($lines -join "`n") + "`n") } finally { $writer.Dispose() }
        } finally { $zip.Dispose() }
        $verification = Verify-Archive $destination
        [pscustomobject]@{ status = 'EXPORT_VERIFIED'; path = $destination; sha256 = $verification.sha256; files = $verification.files; runId = $run.data.runId; observation = 'NOT_RUN' } | ConvertTo-Json -Depth 5
    }
    'verify-archive' {
        if (-not $ArchivePath) { throw 'ArchivePath is required' }
        $v = Verify-Archive $ArchivePath
        [pscustomobject]@{ status = $v.status; runId = $v.runId; files = $v.files; sha256 = $v.sha256; observation = 'NOT_RUN' } | ConvertTo-Json -Depth 5
    }
    'import' {
        if (-not $ArchivePath -or -not $ImportRoot) { throw 'ArchivePath and ImportRoot are required' }
        $v = Verify-Archive $ArchivePath
        $root = [IO.Path]::GetFullPath($ImportRoot)
        New-Item -ItemType Directory -Path $root -Force | Out-Null
        $target = Join-Path $root ('IMPORTED_' + $v.runId + '_' + $v.sha256.Substring(0, 12))
        if (Test-Path -LiteralPath $target) {
            if ((Get-Item -LiteralPath $target).Attributes -band [IO.FileAttributes]::ReparsePoint) {
                throw 'Import target is a reparse point; refusing to follow it'
            }
            $receipt = Join-Path $target 'import-receipt.json'
            if (-not (Test-Path -LiteralPath $receipt -PathType Leaf)) { throw 'Import directory exists without receipt; refusing overwrite' }
            $previous = Get-Content -LiteralPath $receipt -Raw -Encoding UTF8 | ConvertFrom-Json
            if ($previous.archiveSha256 -cne $v.sha256) { throw 'Conflicting import receipt' }
            foreach ($relative in $v.manifest.Keys) {
                $path = Assert-RegularFile $target $relative
                if ((Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash.ToLowerInvariant() -cne $v.manifest[$relative]) {
                    throw "Previously imported file changed: $relative"
                }
            }
            [pscustomobject]@{ status = 'ALREADY_IMPORTED_VERIFIED'; path = $target; runId = $v.runId; observation = 'NOT_RUN' } | ConvertTo-Json -Depth 5
            break
        }
        $temporary = Join-Path $root ('.tc6-ab-import-' + [guid]::NewGuid().ToString('N'))
        New-Item -ItemType Directory -Path $temporary -ErrorAction Stop | Out-Null
        $completed = $false
        try {
            $zip = [IO.Compression.ZipFile]::OpenRead($ArchivePath)
            try {
                foreach ($entry in $zip.Entries) {
                    Assert-Relative $entry.FullName
                    $dest = Join-Path $temporary $entry.FullName
                    $parent = Split-Path -Parent $dest
                    New-Item -ItemType Directory -Path $parent -Force | Out-Null
                    $sourceStream = $entry.Open()
                    $destStream = [IO.File]::Open($dest, [IO.FileMode]::CreateNew, [IO.FileAccess]::Write)
                    try { $sourceStream.CopyTo($destStream) } finally { $destStream.Dispose(); $sourceStream.Dispose() }
                }
            } finally { $zip.Dispose() }
            [IO.File]::WriteAllText((Join-Path $temporary 'import-receipt.json'), (([pscustomobject]@{ schema = 'tc6-visual-ab-import-receipt/1'; archiveSha256 = $v.sha256; runId = $v.runId }) | ConvertTo-Json) + "`n", [Text.UTF8Encoding]::new($false))
            Move-Item -LiteralPath $temporary -Destination $target -ErrorAction Stop
            $completed = $true
        } finally {
            if (-not $completed -and (Test-Path -LiteralPath $temporary -PathType Container)) {
                Remove-Item -LiteralPath $temporary -Recurse -Force
            }
        }
        [pscustomobject]@{ status = 'IMPORTED_VERIFIED'; path = $target; runId = $v.runId; observation = 'NOT_RUN' } | ConvertTo-Json -Depth 5
    }
}
