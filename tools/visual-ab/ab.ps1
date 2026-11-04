[CmdletBinding()]
param(
    [ValidateSet("doctor", "catalog")]
    [string]$Action = "doctor",
    [string]$ConfigPath,
    [string]$CatalogPath,
    [switch]$SaveReport
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$repoFromScript = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "../..")).Path
if (-not $CatalogPath) {
    $CatalogPath = Join-Path $PSScriptRoot "catalog/v0-groups.json"
}
if (-not $ConfigPath) {
    $ConfigPath = Join-Path $repoFromScript "tools/reports/local/visual-ab/config.local.json"
}

function Read-JsonFile([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) {
        throw "Required JSON file not found: $Path"
    }
    return (Get-Content -LiteralPath $Path -Raw -Encoding UTF8 | ConvertFrom-Json)
}

function Test-Catalog([object]$Catalog) {
    if ($Catalog.schema -ne "tc6-visual-ab-scene-catalog/1") {
        throw "Unsupported scene catalog schema"
    }
    $groups = @($Catalog.groups)
    if ($groups.Count -ne 13) {
        throw "Catalog must have G00 through G12 (found $($groups.Count))"
    }
    $expected = @(0..12 | ForEach-Object { "G{0:D2}" -f $_ })
    $actual = @($groups | ForEach-Object { $_.id })
    if (@($actual | Select-Object -Unique).Count -ne 13) {
        throw "Duplicate scene group IDs"
    }
    foreach ($id in $expected) {
        if ($actual -notcontains $id) { throw "Missing scene group $id" }
    }
    $requiredFindings = @($Catalog.findingIds)
    $covered = @($groups | ForEach-Object { $_.researchFindingIds } | Sort-Object -Unique)
    if ($requiredFindings.Count -ne 37) {
        throw "Expected 37 source findings; got $($requiredFindings.Count)"
    }
    if (@($requiredFindings | Select-Object -Unique).Count -ne 37) {
        throw "Duplicate source finding IDs"
    }
    foreach ($id in $requiredFindings) {
        if ($covered -notcontains $id) {
            throw "Source finding $id has no group mapping"
        }
    }
    return [pscustomobject]@{
        groupCount = $groups.Count
        findingCount = $requiredFindings.Count
        referencedFindingCount = $covered.Count
        status = "SOURCE_CATALOG_VALID"
        runtimeState = "NOT_RUN"
    }
}

$catalog = Read-JsonFile $CatalogPath
$catalogStatus = Test-Catalog $catalog

if ($Action -eq "catalog") {
    $catalogStatus | ConvertTo-Json -Depth 4
    exit 0
}

$config = Read-JsonFile $ConfigPath
if ($config.schema -ne "tc6-visual-ab-host-config/1") {
    throw "Unsupported host configuration schema"
}

$checks = [System.Collections.Generic.List[object]]::new()
function Add-Check([string]$Id, [string]$Status, [string]$Detail) {
    $script:checks.Add([pscustomobject][ordered]@{
        id = $Id
        status = $Status
        detail = $Detail
    })
}

$repo = [string]$config.repositoryRoot
if (-not (Test-Path -LiteralPath $repo -PathType Container)) {
    Add-Check "repository" "ERROR" "Configure repositoryRoot in config.local.json"
} else {
    Add-Check "repository" "OK" "Repository directory exists"
    if (Get-Command git -ErrorAction SilentlyContinue) {
        $head = @(& git -C $repo rev-parse HEAD 2>$null)
        $branch = @(& git -C $repo branch --show-current 2>$null)
        $dirty = @(& git -C $repo status --porcelain 2>$null)
        $gitBranch = "(detached HEAD)"
        if ($branch.Count -gt 0) {
            $branchValue = [string]$branch[0]
            if (-not [string]::IsNullOrWhiteSpace($branchValue)) {
                $gitBranch = $branchValue
            }
        }
        if ($LASTEXITCODE -eq 0 -and $head.Count -gt 0) {
            Add-Check "git-head" "OK" "HEAD=$($head[0]); branch=$gitBranch; dirtyEntries=$($dirty.Count)"
        } else {
            Add-Check "git-head" "WARN" "Git metadata could not be read"
        }
    } else {
        Add-Check "git-head" "WARN" "Git executable not found"
    }
    $fixturePath = Join-Path $repo ([string]$config.fixtureRelativeRoot)
    $fixtureReadme = Join-Path $fixturePath "README.md"
    if (Test-Path -LiteralPath $fixtureReadme -PathType Leaf) {
        Add-Check "paired-fixtures" "OK" "Paired world documentation exists"
    } else {
        Add-Check "paired-fixtures" "WARN" "Paired world documentation not found; check active branch"
    }
}

$optionKeys = @("fov", "gamma", "guiScale", "renderDistance", "maxFps", "particles", "fullscreen")
$profileOptions = @{}
foreach ($side in @("legacy", "modern")) {
    $profile = $config.profiles.$side
    $gameDir = [string]$profile.gameDirectory
    if (-not (Test-Path -LiteralPath $gameDir -PathType Container)) {
        Add-Check "$side-game-dir" "ERROR" "Configure a valid gameDirectory"
        continue
    }
    Add-Check "$side-game-dir" "OK" "Game directory exists"
    $modFolder = Join-Path $gameDir "mods"
    $mods = @()
    if (Test-Path -LiteralPath $modFolder -PathType Container) {
        $mods = @(Get-ChildItem -LiteralPath $modFolder -Filter "*.jar" -File | Select-Object -ExpandProperty Name)
    }
    foreach ($pattern in @($profile.expectedMods)) {
        if (@($mods | Where-Object { $_ -like $pattern }).Count -gt 0) {
            Add-Check "$side-mod" "OK" "$pattern found"
        } else {
            Add-Check "$side-mod" "ERROR" "$pattern not found"
        }
    }
    $saves = Join-Path $gameDir "saves"
    $worlds = @()
    if (Test-Path -LiteralPath $saves -PathType Container) {
        $worlds = @(Get-ChildItem -LiteralPath $saves -Directory | Select-Object -ExpandProperty Name)
    }
    $pattern = [string]$profile.worldNamePattern
    $matching = @($worlds | Where-Object { $_ -like $pattern })
    if ($matching.Count -gt 0) {
        Add-Check "$side-worlds" "OK" "$($matching.Count) matching world directories"
    } else {
        Add-Check "$side-worlds" "WARN" "No matching worlds for $pattern"
    }
    $optionsPath = Join-Path $gameDir "options.txt"
    $safeOptions = [ordered]@{}
    if (Test-Path -LiteralPath $optionsPath -PathType Leaf) {
        foreach ($line in (Get-Content -LiteralPath $optionsPath)) {
            $parts = $line -split ":", 2
            if ($parts.Count -eq 2 -and $parts[0] -in $optionKeys) {
                $safeOptions[$parts[0]] = $parts[1]
            }
        }
        Add-Check "$side-options" "OK" "Selected graphics settings read"
    } else {
        Add-Check "$side-options" "WARN" "options.txt not found"
    }
    $profileOptions[$side] = $safeOptions
}

if ($profileOptions.ContainsKey("legacy") -and $profileOptions.ContainsKey("modern")) {
    foreach ($key in @("gamma", "guiScale", "renderDistance", "maxFps")) {
        $left = $profileOptions.legacy[$key]
        $right = $profileOptions.modern[$key]
        if ($null -ne $left -and $null -ne $right -and $left -ne $right) {
            Add-Check "graphics-$key" "WARN" "Different raw values (legacy=$left, modern=$right); calibration required"
        }
    }
}

$errors = @($checks | Where-Object { $_.status -eq "ERROR" }).Count
$warnings = @($checks | Where-Object { $_.status -eq "WARN" }).Count
$report = [pscustomobject][ordered]@{
    schema = "tc6-visual-ab-doctor/1"
    generatedUtc = [DateTime]::UtcNow.ToString("o")
    catalog = $catalogStatus
    errors = $errors
    warnings = $warnings
    checks = @($checks.ToArray())
    statement = "Source and filesystem diagnostic only. No Minecraft clients were launched."
}
$report | ConvertTo-Json -Depth 8

if ($SaveReport -and (Test-Path -LiteralPath $repo -PathType Container)) {
    $destination = Join-Path $repo "tools/reports/local/visual-ab"
    New-Item -ItemType Directory -Force -Path $destination | Out-Null
    $reportFile = Join-Path $destination ("doctor-" + [DateTime]::UtcNow.ToString("yyyyMMdd-HHmmss") + ".json")
    $report | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $reportFile -Encoding UTF8
    Write-Host "Local report: $reportFile"
}
if ($errors -gt 0) { exit 2 }
