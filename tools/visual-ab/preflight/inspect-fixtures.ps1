[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$ConfigPath,
    [switch]$SaveReport
)
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

if (-not (Test-Path -LiteralPath $ConfigPath -PathType Leaf)) {
    throw "Local configuration missing: $ConfigPath"
}
$config = Get-Content -LiteralPath $ConfigPath -Raw -Encoding UTF8 | ConvertFrom-Json
if ($config.schema -ne "tc6-visual-ab-host-config/1") { throw "Unsupported host config schema" }

$worldEntries = [System.Collections.Generic.List[object]]::new()
$checks = [System.Collections.Generic.List[object]]::new()
foreach ($side in @("legacy", "modern")) {
    $profile = $config.profiles.$side
    $gameDirectory = [string]$profile.gameDirectory
    $savesPath = Join-Path $gameDirectory "saves"
    $pattern = [string]$profile.worldNamePattern
    $prefix = if ($pattern.EndsWith("*")) { $pattern.Substring(0, $pattern.Length - 1) } else { $pattern }
    if (-not (Test-Path -LiteralPath $savesPath -PathType Container)) {
        $checks.Add([pscustomobject]@{ id = "$side-saves"; status = "WARN"; detail = "Saves directory not found" })
        continue
    }
    $candidates = @(Get-ChildItem -LiteralPath $savesPath -Directory | Where-Object { $_.Name -like $pattern })
    if ($candidates.Count -eq 0) {
        $checks.Add([pscustomobject]@{ id = "$side-worlds"; status = "WARN"; detail = "No matching worlds" })
    }
    foreach ($world in $candidates) {
        $pairKey = $null
        $role = "OTHER"
        if ($world.Name -eq $prefix) {
            $pairKey = "PRIMARY"
            $role = "PRIMARY"
        } elseif ($world.Name.StartsWith($prefix + "_RESET_", [StringComparison]::OrdinalIgnoreCase)) {
            $pairKey = $world.Name.Substring($prefix.Length + 1)
            $role = "RESET"
        }

        $levelDat = Test-Path -LiteralPath (Join-Path $world.FullName "level.dat") -PathType Leaf
        $regionPath = Join-Path $world.FullName "region"
        $regionFiles = @()
        if (Test-Path -LiteralPath $regionPath -PathType Container) {
            $regionFiles = @(Get-ChildItem -LiteralPath $regionPath -Filter "*.mca" -File -ErrorAction SilentlyContinue)
        }
        $generatedRegion = ($regionFiles.Count -gt 0)
        $structuralState = if (-not $levelDat) {
            "NEEDS_VERIFICATION"
        } elseif (-not $generatedRegion) {
            "UNINITIALIZED_OR_NOT_GENERATED"
        } else {
            "FILES_PRESENT_CLIENT_UNVERIFIED"
        }
        $worldEntries.Add([pscustomobject][ordered]@{
            side = $side
            worldName = $world.Name
            role = $role
            pairKey = $pairKey
            levelDatPresent = [bool]$levelDat
            regionFilesPresent = [bool]$generatedRegion
            structuralState = $structuralState
            clientVerification = "NOT_RUN"
        })
    }
}

$pairs = [System.Collections.Generic.List[object]]::new()
$pairKeys = @($worldEntries | Where-Object { $null -ne $_.pairKey } | ForEach-Object { $_.pairKey } | Sort-Object -Unique)
foreach ($key in $pairKeys) {
    $left = @($worldEntries | Where-Object { $_.pairKey -eq $key -and $_.side -eq "legacy" })
    $right = @($worldEntries | Where-Object { $_.pairKey -eq $key -and $_.side -eq "modern" })
    $pairStatus = if ($left.Count -eq 1 -and $right.Count -eq 1) { "PATHS_PAIRED_CLIENT_UNVERIFIED" } else { "MISSING_OR_AMBIGUOUS_COUNTERPART" }
    $pairs.Add([pscustomobject][ordered]@{
        pairKey = $key
        status = $pairStatus
        legacyWorldName = if ($left.Count -eq 1) { $left[0].worldName } else { $null }
        modernWorldName = if ($right.Count -eq 1) { $right[0].worldName } else { $null }
    })
}
if ($pairKeys.Count -eq 0) {
    $checks.Add([pscustomobject]@{ id = "world-pairs"; status = "WARN"; detail = "No primary/reset pairing keys found" })
}
$missingPairs = @($pairs | Where-Object { $_.status -ne "PATHS_PAIRED_CLIENT_UNVERIFIED" }).Count
if ($missingPairs -gt 0) {
    $checks.Add([pscustomobject]@{ id = "world-pairs"; status = "WARN"; detail = "$missingPairs pair(s) need a matching counterpart" })
}

$report = [pscustomobject][ordered]@{
    schema = "tc6-visual-ab-fixture-inventory/1"
    generatedUtc = [DateTime]::UtcNow.ToString("o")
    worldCount = $worldEntries.Count
    pairedCount = @($pairs | Where-Object { $_.status -eq "PATHS_PAIRED_CLIENT_UNVERIFIED" }).Count
    worlds = @($worldEntries.ToArray())
    pairs = @($pairs.ToArray())
    checks = @($checks.ToArray())
    statement = "Read-only directory inspection. No NBT validation, game startup, setup functions or world resets executed."
}
$report | ConvertTo-Json -Depth 8
if ($SaveReport) {
    $root = [string]$config.repositoryRoot
    if (-not (Test-Path -LiteralPath $root -PathType Container)) {
        throw "Cannot save report: repositoryRoot does not exist"
    }
    $destination = Join-Path $root "tools/reports/local/visual-ab"
    New-Item -ItemType Directory -Force -Path $destination | Out-Null
    $reportFile = Join-Path $destination ("fixtures-" + [DateTime]::UtcNow.ToString("yyyyMMdd-HHmmss") + ".json")
    $report | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $reportFile -Encoding UTF8
    Write-Host "Local report: $reportFile"
}
