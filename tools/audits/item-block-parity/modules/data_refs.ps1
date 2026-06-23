[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [string]$PortRoot = "05_neoforge_port",
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_data_reference_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_data_reference_report.md" }

$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$itemIds = @{}
$blockIds = @{}
foreach ($entry in @($port.entries)) {
    if ($entry.kind -eq "item") { $itemIds[$entry.registryId] = $true }
    if ($entry.kind -eq "block") { $blockIds[$entry.registryId] = $true }
}

function ConvertTo-RelativePortPath([string]$FullPath) {
    return [System.IO.Path]::GetRelativePath($portPath, $FullPath).Replace("\", "/")
}
function Get-CategoryForFile([string]$RelativePath, [string]$Text) {
    $normalized = $RelativePath.Replace("\", "/").ToLowerInvariant()
    $categories = [System.Collections.Generic.List[string]]::new()
    if ($normalized -match '/recipes?/' -or $normalized -match '^src/main/resources/data/[^/]+/recipes?/') { $categories.Add("recipes") }
    if ($normalized -match '/tags/') { $categories.Add("tags") }
    if ($normalized -match 'aspects?' -or $Text -match '"aspects?"') { $categories.Add("aspects") }
    if ($normalized -match 'research' -or $Text -match 'research') { $categories.Add("research_refs") }
    if ($normalized -match 'thaumonomicon' -or $Text -match 'thaumonomicon') { $categories.Add("thaumonomicon_refs") }
    return @($categories | Select-Object -Unique)
}
function Get-NamespacedThaumcraftRefs([string]$Text) {
    return @([regex]::Matches($Text, '"thaumcraft:(?<id>[a-z0-9_./-]+)"') | ForEach-Object { $_.Groups["id"].Value } | Sort-Object -Unique)
}

$selected = @($Checks | Where-Object { $_ -in @("recipes", "tags", "aspects", "research_refs", "thaumonomicon_refs") } | Select-Object -Unique)
if ($selected.Count -eq 0) { $selected = @("recipes", "tags", "aspects", "research_refs", "thaumonomicon_refs") }

$jsonFiles = @(Get-ChildItem -LiteralPath (Join-Path $portPath "src/main/resources") -Recurse -Filter "*.json" -File -ErrorAction SilentlyContinue)
$results = [System.Collections.Generic.List[object]]::new()
$fileSummaries = [System.Collections.Generic.List[object]]::new()
foreach ($file in $jsonFiles) {
    $relative = ConvertTo-RelativePortPath $file.FullName
    $text = Get-Content -Raw -LiteralPath $file.FullName
    $categories = @(Get-CategoryForFile $relative $text | Where-Object { $_ -in $selected })
    if ($categories.Count -eq 0) { continue }
    $refs = @(Get-NamespacedThaumcraftRefs $text)
    $fileSummaries.Add([pscustomobject][ordered]@{ path = $relative; categories = @($categories); thaumcraftRefs = @($refs) })
    foreach ($ref in $refs) {
        $itemKnown = $itemIds.ContainsKey($ref)
        $blockKnown = $blockIds.ContainsKey($ref)
        $status = if ($itemKnown -or $blockKnown) { "PASS" } else { "DATA_REF_REVIEW_NEEDED" }
        $kind = if ($itemKnown -and $blockKnown) { "item_block" } elseif ($itemKnown) { "item" } elseif ($blockKnown) { "block" } else { "unknown" }
        foreach ($category in $categories) {
            $results.Add([pscustomobject][ordered]@{
                category = $category
                path = $relative
                ref = "thaumcraft:$ref"
                kind = $kind
                status = $status
                evidence = if ($status -eq "PASS") { "Reference resolves to registered item/block manifest entry" } else { "Reference is not a registered item/block ID; may be recipe type, research key, tag, entity, aspect or future content" }
            })
        }
    }
}

$orderedResults = @($results | Sort-Object category, status, path, ref)
$summaryByCategory = @($selected | ForEach-Object {
    $category = $_
    $rows = @($orderedResults | Where-Object category -eq $category)
    [pscustomobject][ordered]@{
        category = $category
        files = @($fileSummaries | Where-Object { $_.categories -contains $category }).Count
        refs = $rows.Count
        pass = @($rows | Where-Object status -eq "PASS").Count
        reviewNeeded = @($rows | Where-Object status -eq "DATA_REF_REVIEW_NEEDED").Count
    }
})
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @($selected)
    policy = "Report-only data/reference scan. DATA_REF_REVIEW_NEEDED is not a failure because thaumcraft namespace can reference recipe types, research keys, tags, entities, aspects or future content, not only item/block registry IDs."
    summary = [ordered]@{
        files = @($fileSummaries).Count
        refs = $orderedResults.Count
        pass = @($orderedResults | Where-Object status -eq "PASS").Count
        reviewNeeded = @($orderedResults | Where-Object status -eq "DATA_REF_REVIEW_NEEDED").Count
        byCategory = @($summaryByCategory)
    }
    files = @($fileSummaries | Sort-Object path)
    results = $orderedResults
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 14 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block data reference report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Category | Files | Refs | PASS | REVIEW_NEEDED |")
$lines.Add("|---|---:|---:|---:|---:|")
foreach ($row in $summaryByCategory) { $lines.Add("| $($row.category) | $($row.files) | $($row.refs) | $($row.pass) | $($row.reviewNeeded) |") }
$lines.Add("")
$lines.Add("## Review-needed references")
$lines.Add("")
$lines.Add("| Category | File | Ref | Evidence |")
$lines.Add("|---|---|---|---|")
foreach ($row in $orderedResults | Where-Object status -eq "DATA_REF_REVIEW_NEEDED") {
    $safeEvidence = $row.evidence.Replace("|", "\|")
    $lines.Add("| $($row.category) | ``$($row.path)`` | ``$($row.ref)`` | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Data reference report: $OutputMarkdown"
Write-Output "Refs=$($report.summary.refs), pass=$($report.summary.pass), reviewNeeded=$($report.summary.reviewNeeded), files=$($report.summary.files)"
