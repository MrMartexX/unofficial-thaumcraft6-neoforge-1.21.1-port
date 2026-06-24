[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [string]$PortRoot = "05_neoforge_port",
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_runtime_datapack_smoke_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_runtime_datapack_smoke_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}

function Add-Row(
    $Rows,
    [string]$Subcheck,
    [string]$Status,
    [string]$Path,
    [string]$Evidence,
    [string]$Severity = "info"
) {
    $Rows.Add([pscustomobject][ordered]@{
        check = "datapack_load"
        subcheck = $Subcheck
        status = $Status
        severity = $Severity
        path = $Path
        evidence = $Evidence
    })
}

function Test-JsonFile([string]$Path) {
    try {
        $null = Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json
        return [pscustomobject][ordered]@{ ok = $true; error = "" }
    } catch {
        return [pscustomobject][ordered]@{ ok = $false; error = $_.Exception.Message }
    }
}

function Get-DirectoryStatus([string]$Root, [string[]]$Candidates) {
    foreach ($candidate in $Candidates) {
        $full = Join-Path $Root $candidate
        if (Test-Path -LiteralPath $full -PathType Container) {
            $files = @(Get-ChildItem -LiteralPath $full -Recurse -File -Filter "*.json" -ErrorAction SilentlyContinue)
            return [pscustomobject][ordered]@{
                exists = $true
                relative = ConvertTo-RelativeRepoPath $full
                files = $files.Count
                matched = $candidate
            }
        }
    }
    return [pscustomobject][ordered]@{
        exists = $false
        relative = ""
        files = 0
        matched = ""
    }
}

$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$resourcesRoot = Join-Path $portPath "src/main/resources"
$dataRoot = Join-Path $resourcesRoot "data/thaumcraft"
$assetsRoot = Join-Path $resourcesRoot "assets/thaumcraft"
$rows = [System.Collections.Generic.List[object]]::new()

if (-not (Test-Path -LiteralPath $resourcesRoot -PathType Container)) {
    Add-Row $rows "resources_root" "DATAPACK_LOAD_ERROR" (ConvertTo-RelativeRepoPath $resourcesRoot) "src/main/resources is missing" "error"
} else {
    Add-Row $rows "resources_root" "DATAPACK_LOAD_PASS" (ConvertTo-RelativeRepoPath $resourcesRoot) "src/main/resources exists" "info"
}

$packMcmeta = Join-Path $resourcesRoot "pack.mcmeta"
if (Test-Path -LiteralPath $packMcmeta -PathType Leaf) {
    $json = Test-JsonFile $packMcmeta
    if ($json.ok) {
        Add-Row $rows "pack_mcmeta" "DATAPACK_LOAD_PASS" (ConvertTo-RelativeRepoPath $packMcmeta) "pack.mcmeta parses as JSON" "info"
    } else {
        Add-Row $rows "pack_mcmeta" "DATAPACK_LOAD_ERROR" (ConvertTo-RelativeRepoPath $packMcmeta) $json.error "error"
    }
} else {
    Add-Row $rows "pack_mcmeta" "DATAPACK_LOAD_REVIEW_NEEDED" (ConvertTo-RelativeRepoPath $packMcmeta) "pack.mcmeta missing; confirm build tooling supplies pack metadata" "review"
}

if (Test-Path -LiteralPath $dataRoot -PathType Container) {
    Add-Row $rows "thaumcraft_data_namespace" "DATAPACK_LOAD_PASS" (ConvertTo-RelativeRepoPath $dataRoot) "data/thaumcraft namespace exists" "info"
} else {
    Add-Row $rows "thaumcraft_data_namespace" "DATAPACK_LOAD_REVIEW_NEEDED" (ConvertTo-RelativeRepoPath $dataRoot) "data/thaumcraft namespace missing" "review"
}

if (Test-Path -LiteralPath $assetsRoot -PathType Container) {
    Add-Row $rows "thaumcraft_assets_namespace" "DATAPACK_LOAD_PASS" (ConvertTo-RelativeRepoPath $assetsRoot) "assets/thaumcraft namespace exists" "info"
} else {
    Add-Row $rows "thaumcraft_assets_namespace" "DATAPACK_LOAD_REVIEW_NEEDED" (ConvertTo-RelativeRepoPath $assetsRoot) "assets/thaumcraft namespace missing" "review"
}

$directoryGroups = @(
    [pscustomobject]@{ subcheck = "recipe_directory"; candidates = @("recipe", "recipes"); required = $false },
    [pscustomobject]@{ subcheck = "loot_table_directory"; candidates = @("loot_table", "loot_tables"); required = $false },
    [pscustomobject]@{ subcheck = "tags_directory"; candidates = @("tags"); required = $false },
    [pscustomobject]@{ subcheck = "advancement_directory"; candidates = @("advancement", "advancements"); required = $false },
    [pscustomobject]@{ subcheck = "worldgen_directory"; candidates = @("worldgen"); required = $false }
)

foreach ($group in $directoryGroups) {
    $status = Get-DirectoryStatus $dataRoot $group.candidates
    if ($status.exists) {
        Add-Row $rows $group.subcheck "DATAPACK_LOAD_PASS" $status.relative "Found $($status.files) JSON files under $($status.matched)" "info"
    } else {
        Add-Row $rows $group.subcheck "DATAPACK_LOAD_REVIEW_NEEDED" (ConvertTo-RelativeRepoPath $dataRoot) "No $($group.subcheck) directory found; acceptable only if this content type is intentionally absent" "review"
    }
}

$jsonRoots = @()
if (Test-Path -LiteralPath $dataRoot -PathType Container) { $jsonRoots += $dataRoot }
if (Test-Path -LiteralPath (Join-Path $resourcesRoot "META-INF") -PathType Container) { $jsonRoots += (Join-Path $resourcesRoot "META-INF") }

$jsonFiles = @()
foreach ($root in $jsonRoots) {
    $jsonFiles += @(Get-ChildItem -LiteralPath $root -Recurse -File -Filter "*.json" -ErrorAction SilentlyContinue)
}
foreach ($file in @($jsonFiles | Sort-Object FullName)) {
    $json = Test-JsonFile $file.FullName
    if ($json.ok) {
        Add-Row $rows "json_parse" "DATAPACK_LOAD_PASS" (ConvertTo-RelativeRepoPath $file.FullName) "JSON parses" "info"
    } else {
        Add-Row $rows "json_parse" "DATAPACK_LOAD_ERROR" (ConvertTo-RelativeRepoPath $file.FullName) $json.error "error"
    }
}

# Lightweight load-shape checks for common datapack JSON types. These are report-only because schema details vary by pack format and loader.
$shapeFiles = @()
if (Test-Path -LiteralPath $dataRoot -PathType Container) {
    $shapeFiles += @(Get-ChildItem -LiteralPath $dataRoot -Recurse -File -Filter "*.json" -ErrorAction SilentlyContinue)
}
foreach ($file in $shapeFiles) {
    $rel = ConvertTo-RelativeRepoPath $file.FullName
    $raw = Get-Content -Raw -LiteralPath $file.FullName
    try { $json = $raw | ConvertFrom-Json } catch { continue }
    $pathNorm = $rel.Replace("\", "/")
    if ($pathNorm -match "/loot_table[s]?/") {
        if ($null -eq $json.pools) {
            Add-Row $rows "loot_shape" "DATAPACK_LOAD_REVIEW_NEEDED" $rel "Loot table has no pools property; confirm intentional empty table format" "review"
        } else {
            Add-Row $rows "loot_shape" "DATAPACK_LOAD_PASS" $rel "Loot table has pools property" "info"
        }
    } elseif ($pathNorm -match "/recipe[s]?/") {
        if ($null -eq $json.type) {
            Add-Row $rows "recipe_shape" "DATAPACK_LOAD_REVIEW_NEEDED" $rel "Recipe JSON has no type property" "review"
        } else {
            Add-Row $rows "recipe_shape" "DATAPACK_LOAD_PASS" $rel "Recipe has type=$($json.type)" "info"
        }
    } elseif ($pathNorm -match "/tags/") {
        if ($null -eq $json.values) {
            Add-Row $rows "tag_shape" "DATAPACK_LOAD_REVIEW_NEEDED" $rel "Tag JSON has no values property" "review"
        } else {
            Add-Row $rows "tag_shape" "DATAPACK_LOAD_PASS" $rel "Tag has values property" "info"
        }
    }
}

$ordered = @($rows | Sort-Object severity, subcheck, path)
$summary = [ordered]@{
    rows = $ordered.Count
    pass = @($ordered | Where-Object status -eq "DATAPACK_LOAD_PASS").Count
    reviewNeeded = @($ordered | Where-Object status -eq "DATAPACK_LOAD_REVIEW_NEEDED").Count
    errors = @($ordered | Where-Object status -eq "DATAPACK_LOAD_ERROR").Count
    jsonFiles = $jsonFiles.Count
    portEntries = @($port.entries).Count
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Report-only static runtime/datapack load smoke audit. This validates load-critical JSON/layout clues and does not claim gameplay parity."
    inputs = [ordered]@{
        portManifest = ConvertTo-RelativeRepoPath $PortManifestPath
        portRoot = $PortRoot
        resourcesRoot = ConvertTo-RelativeRepoPath $resourcesRoot
        dataRoot = ConvertTo-RelativeRepoPath $dataRoot
    }
    summary = $summary
    results = @($ordered)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 14 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Runtime/datapack load smoke report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: report-only static load smoke. This checks JSON/layout/load clues, not gameplay parity.")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("- Rows: $($summary.rows)")
$lines.Add("- Pass: $($summary.pass)")
$lines.Add("- Review needed: $($summary.reviewNeeded)")
$lines.Add("- Errors: $($summary.errors)")
$lines.Add("- JSON files checked: $($summary.jsonFiles)")
$lines.Add("")
$lines.Add("## Review/error rows")
$lines.Add("")
$lines.Add("| Status | Subcheck | Path | Evidence |")
$lines.Add("|---|---|---|---|")
foreach ($row in @($ordered | Where-Object { $_.status -ne "DATAPACK_LOAD_PASS" } | Select-Object -First 300)) {
    $evidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
    $lines.Add("| $($row.status) | $($row.subcheck) | $($row.path) | $evidence |")
}
if ((@($ordered | Where-Object { $_.status -ne "DATAPACK_LOAD_PASS" })).Count -eq 0) {
    $lines.Add("| DATAPACK_LOAD_PASS | all | <none> | No review/error rows |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Runtime/datapack load smoke report: $OutputMarkdown"
Write-Output "Rows=$($summary.rows), pass=$($summary.pass), reviewNeeded=$($summary.reviewNeeded), errors=$($summary.errors), jsonFiles=$($summary.jsonFiles)"