[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('capture', 'status', 'verify')][string]$Action,
    [Parameter(Mandatory = $true)][string]$RunPath,
    [string]$ConfigPath
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$toolRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$catalog = Get-Content -LiteralPath (Join-Path $toolRoot 'catalog/v0-state-candidates.json') -Raw -Encoding UTF8 | ConvertFrom-Json
$optionKeys = @('fov', 'gamma', 'guiScale', 'renderDistance', 'simulationDistance', 'maxFps',
    'particles', 'fullscreen', 'enableVsync', 'mipmapLevels', 'graphicsMode',
    'fancyGraphics', 'resourcePacks', 'overrideWidth', 'overrideHeight', 'lang')
$crossVersionKeys = @('fov', 'gamma', 'guiScale', 'renderDistance', 'maxFps',
    'particles', 'fullscreen', 'enableVsync', 'mipmapLevels', 'resourcePacks')

function Read-Session([string]$Directory) {
    $resolved = (Resolve-Path -LiteralPath $Directory -ErrorAction Stop).Path
    $sessionPath = Join-Path $resolved 'session.json'
    $session = Get-Content -LiteralPath $sessionPath -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($session.schema -ne 'tc6-visual-ab-session/1' -or $session.catalogVersion -cne $script:catalog.version) {
        throw 'Session schema or catalog mismatch'
    }
    if ([string]$session.runId -notmatch '^RUN_[A-Za-z0-9_-]+$') { throw 'Invalid run ID' }
    return [pscustomobject]@{ root = $resolved; data = $session }
}

function Read-Snapshots([object]$Run) {
    $path = Join-Path $Run.root 'environment.json'
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return $null }
    if ((Get-Item -LiteralPath $path).Attributes -band [IO.FileAttributes]::ReparsePoint) {
        throw 'Environment manifest symlink rejected'
    }
    $manifest = Get-Content -LiteralPath $path -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($manifest.schema -ne 'tc6-visual-ab-environment/1' -or
        $manifest.runId -cne $Run.data.runId -or $manifest.catalogVersion -cne $Run.data.catalogVersion) {
        throw 'Environment manifest identity mismatch'
    }
    if (@($manifest.snapshots).Count -gt 100) { throw 'Environment snapshot limit exceeded' }
    return $manifest
}

function Read-Config([string]$Requested) {
    if (-not $Requested) {
        $repo = (Resolve-Path -LiteralPath (Join-Path $script:toolRoot '../..')).Path
        $Requested = Join-Path $repo 'tools/reports/local/visual-ab/config.local.json'
    }
    if (-not (Test-Path -LiteralPath $Requested -PathType Leaf)) { throw 'Local config not found' }
    $config = Get-Content -LiteralPath $Requested -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($config.schema -ne 'tc6-visual-ab-host-config/1') { throw 'Unsupported host config schema' }
    foreach ($side in @('legacy', 'modern')) {
        $p = $config.profiles.$side
        if ([string]::IsNullOrWhiteSpace([string]$p.gameDirectory) -or
            -not (Test-Path -LiteralPath ([string]$p.gameDirectory) -PathType Container)) {
            throw "Configured $side game directory is not available"
        }
    }
    return $config
}

function Get-Sha256Text([string]$Value) {
    $bytes = [Text.UTF8Encoding]::new($false).GetBytes($Value)
    return [Convert]::ToHexString([Security.Cryptography.SHA256]::HashData($bytes)).ToLowerInvariant()
}

function Get-FileNames([string]$Folder, [string]$Filter = '*', [bool]$FilesOnly = $false) {
    if (-not (Test-Path -LiteralPath $Folder -PathType Container)) { return @() }
    $directory = Get-Item -LiteralPath $Folder
    if ($directory.Attributes -band [IO.FileAttributes]::ReparsePoint) { throw 'Reparse point directory rejected' }
    $names = @(Get-ChildItem -LiteralPath $Folder -Filter $Filter -Force -ErrorAction Stop |
        Where-Object { -not $FilesOnly -or -not $_.PSIsContainer } |
        Where-Object { -not ($_.Attributes -band [IO.FileAttributes]::ReparsePoint) } |
        Select-Object -ExpandProperty Name | Sort-Object)
    if ($names.Count -gt 1024) { throw 'Directory listing exceeds 1024 entries' }
    return $names
}

function Get-ClientInventory([string]$Side, [object]$Profile) {
    $game = (Resolve-Path -LiteralPath ([string]$Profile.gameDirectory)).Path
    $optionPath = Join-Path $game 'options.txt'
    $options = [ordered]@{}
    $optionsSha = $null
    $optionsState = 'MISSING'
    if (Test-Path -LiteralPath $optionPath -PathType Leaf) {
        $file = Get-Item -LiteralPath $optionPath
        if ($file.Attributes -band [IO.FileAttributes]::ReparsePoint) { throw 'options.txt symlink rejected' }
        if ($file.Length -gt 1MB) { throw 'options.txt exceeds 1 MiB' }
        foreach ($line in (Get-Content -LiteralPath $optionPath -Encoding UTF8)) {
            $parts = $line -split ':', 2
            if ($parts.Count -eq 2 -and $script:optionKeys -ccontains $parts[0]) {
                $options[$parts[0]] = $parts[1]
            }
        }
        $optionsSha = (Get-FileHash -LiteralPath $optionPath -Algorithm SHA256).Hash.ToLowerInvariant()
        $optionsState = 'READ_FROM_DISK_CLIENT_UNVERIFIED'
    }
    $modNames = @(Get-FileNames (Join-Path $game 'mods') '*.jar' $true)
    $packs = @(Get-FileNames (Join-Path $game 'resourcepacks'))
    $shaders = @(Get-FileNames (Join-Path $game 'shaderpacks'))
    $expected = [Collections.Generic.List[object]]::new()
    foreach ($pattern in @($Profile.expectedMods)) {
        $expected.Add([pscustomobject]@{
            pattern = [string]$pattern
            foundByFilename = @($modNames | Where-Object { $_ -like $pattern }).Count -gt 0
        })
    }
    return [pscustomobject][ordered]@{
        side = $Side
        configuredMinecraftVersion = [string]$Profile.minecraft
        configuredVersionEvidence = 'CONFIG_ONLY_NOT_RUNNING_CLIENT'
        gameDirectoryPathSha256 = (Get-Sha256Text ([IO.Path]::GetFullPath($game)))
        optionsState = $optionsState
        optionsSha256 = $optionsSha
        rawOptions = [pscustomobject]$options
        modsDirectoryPresent = (Test-Path -LiteralPath (Join-Path $game 'mods') -PathType Container)
        modNamesLocal = @($modNames)
        modFilenamesSha256 = (Get-Sha256Text ($modNames -join "`n"))
        expectedModPatterns = @($expected.ToArray())
        resourcePackNamesLocal = @($packs)
        resourcePackNamesSha256 = (Get-Sha256Text ($packs -join "`n"))
        shaderPackNamesLocal = @($shaders)
        shaderPackNamesSha256 = (Get-Sha256Text ($shaders -join "`n"))
        javaRuntime = 'NOT_OBSERVED'
        loaderRuntime = 'NOT_OBSERVED'
        activeResourcePackOrder = 'NOT_CLIENT_VERIFIED'
    }
}

function Get-Comparison([object]$Legacy, [object]$Modern) {
    $different = [Collections.Generic.List[string]]::new()
    $missing = [Collections.Generic.List[string]]::new()
    $same = [Collections.Generic.List[string]]::new()
    foreach ($key in $script:crossVersionKeys) {
        $lp = $Legacy.rawOptions.PSObject.Properties[$key]
        $mp = $Modern.rawOptions.PSObject.Properties[$key]
        if ($null -eq $lp -or $null -eq $mp) {
            $missing.Add($key)
        } elseif ([string]$lp.Value -cne [string]$mp.Value) {
            $different.Add($key)
        } else {
            $same.Add($key)
        }
    }
    return [pscustomobject][ordered]@{
        rawValuesDifferent = @($different.ToArray())
        missingOnOneOrBothSides = @($missing.ToArray())
        rawValuesEqualButNotCalibrated = @($same.ToArray())
        crossVersionFovGammaGuiScaleEquivalence = 'MANUAL_PIXEL_AND_UI_CHECK_REQUIRED'
        clientRuntimeAndCamera = 'NOT_VERIFIED'
        calibration = 'CALIBRATION_REQUIRED'
        observation = 'NOT_RUN'
    }
}

function Write-Atomic([string]$Destination, [object]$Data) {
    $parent = Split-Path -Parent $Destination
    $temp = Join-Path $parent ('.tc6-ab-env-' + [guid]::NewGuid().ToString('N') + '.tmp')
    $backup = Join-Path $parent ('.tc6-ab-env-' + [guid]::NewGuid().ToString('N') + '.bak')
    $done = $false
    [IO.File]::WriteAllText($temp, ($Data | ConvertTo-Json -Depth 30) + "`n", [Text.UTF8Encoding]::new($false))
    try {
        if (Test-Path -LiteralPath $Destination -PathType Leaf) {
            [IO.File]::Replace($temp, $Destination, $backup)
        } else {
            [IO.File]::Move($temp, $Destination)
        }
        $done = $true
    } finally {
        if (Test-Path -LiteralPath $temp -PathType Leaf) { Remove-Item -LiteralPath $temp -Force -ErrorAction SilentlyContinue }
        if ($done -and (Test-Path -LiteralPath $backup -PathType Leaf)) { Remove-Item -LiteralPath $backup -Force -ErrorAction SilentlyContinue }
    }
}

$run = Read-Session $RunPath
$manifest = Read-Snapshots $run
switch ($Action) {
    'capture' {
        $config = Read-Config $ConfigPath
        $left = Get-ClientInventory 'legacy' $config.profiles.legacy
        $right = Get-ClientInventory 'modern' $config.profiles.modern
        $compared = Get-Comparison $left $right
        $id = 'ENV_' + [DateTime]::UtcNow.ToString('yyyyMMddTHHmmssfffZ') + '_' + [guid]::NewGuid().ToString('N').Substring(0, 8)
        $snapshot = [pscustomobject][ordered]@{
            id = $id
            capturedUtc = [DateTime]::UtcNow.ToString('o')
            legacy = $left
            modern = $right
            comparison = $compared
            source = 'LOCAL_READ_ONLY_FILESYSTEM'
            observation = 'NOT_RUN'
            clientVerification = 'NOT_RUN'
        }
        if ($null -eq $manifest) {
            $manifest = [pscustomobject][ordered]@{
                schema = 'tc6-visual-ab-environment/1'
                runId = $run.data.runId
                catalogVersion = $run.data.catalogVersion
                snapshots = @()
            }
        }
        if (@($manifest.snapshots).Count -ge 100) { throw 'Maximum of 100 environment snapshots reached' }
        $manifest.snapshots = @($manifest.snapshots) + @($snapshot)
        Write-Atomic (Join-Path $run.root 'environment.json') $manifest
        [pscustomobject]@{
            status = 'ENV_SNAPSHOT_RECORDED_CLIENT_UNVERIFIED'
            runId = $run.data.runId
            snapshotId = $id
            rawMismatchKeys = @($compared.rawValuesDifferent)
            rawMissingKeys = @($compared.missingOnOneOrBothSides)
            calibration = 'CALIBRATION_REQUIRED'
            observation = 'NOT_RUN'
        } | ConvertTo-Json -Compress -Depth 6
    }
    'status' {
        if ($null -eq $manifest -or @($manifest.snapshots).Count -eq 0) {
            [pscustomobject]@{ status = 'ENV_NOT_CAPTURED'; runId = $run.data.runId; observation = 'NOT_RUN' } | ConvertTo-Json -Compress
            break
        }
        $last = @($manifest.snapshots)[-1]
        [pscustomobject]@{
            status = 'ENV_CAPTURED_REVIEW_REQUIRED'
            runId = $run.data.runId
            snapshotCount = @($manifest.snapshots).Count
            latestSnapshotId = $last.id
            rawMismatchKeys = @($last.comparison.rawValuesDifferent)
            calibration = 'CALIBRATION_REQUIRED'
            observation = 'NOT_RUN'
        } | ConvertTo-Json -Compress -Depth 6
    }
    'verify' {
        if ($null -eq $manifest -or @($manifest.snapshots).Count -eq 0) { throw 'Capture an environment snapshot before verification' }
        $config = Read-Config $ConfigPath
        $last = @($manifest.snapshots)[-1]
        $drift = [Collections.Generic.List[string]]::new()
        foreach ($side in @('legacy', 'modern')) {
            $fresh = Get-ClientInventory $side $config.profiles.$side
            $recorded = $last.$side
            foreach ($key in @('gameDirectoryPathSha256', 'optionsSha256', 'modFilenamesSha256', 'resourcePackNamesSha256', 'shaderPackNamesSha256')) {
                if ([string]$fresh.$key -cne [string]$recorded.$key) { $drift.Add("$side/$key") }
            }
        }
        [pscustomobject]@{
            status = if ($drift.Count -eq 0) { 'SELECTED_DISK_METADATA_UNCHANGED_CLIENT_UNVERIFIED' } else { 'SELECTED_DISK_METADATA_DRIFT' }
            snapshotId = $last.id
            driftFields = @($drift.ToArray())
            calibration = 'CALIBRATION_REQUIRED'
            observation = 'NOT_RUN'
        } | ConvertTo-Json -Compress -Depth 6
    }
}
