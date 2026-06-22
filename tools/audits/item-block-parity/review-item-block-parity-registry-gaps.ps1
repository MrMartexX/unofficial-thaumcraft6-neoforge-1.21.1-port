[CmdletBinding()]
param(
    [string]$RepoRoot,

    [string]$CandidateReportPath,

    [string]$PortManifestPath,

    [string]$OutputJson,

    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"

if (-not $RepoRoot) {
    $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path
}
$RepoRoot = (Resolve-Path $RepoRoot).Path

$reportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity"
if (-not $CandidateReportPath) {
    $CandidateReportPath = Join-Path $reportRoot "item_block_parity_gap_candidates.json"
}
if (-not $PortManifestPath) {
    $PortManifestPath = Join-Path $reportRoot "port_manifest.json"
}
if (-not $OutputJson) {
    $OutputJson = Join-Path $reportRoot "item_block_parity_registry_gap_review.json"
}
if (-not $OutputMarkdown) {
    $OutputMarkdown = Join-Path $reportRoot "item_block_parity_registry_gap_review.md"
}

foreach ($path in @($CandidateReportPath, $PortManifestPath)) {
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "Required input not found: $path"
    }
}

$candidateReport = Get-Content -Raw -LiteralPath $CandidateReportPath | ConvertFrom-Json
$portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json

function Get-PlainId([string]$Id) {
    if ([string]::IsNullOrWhiteSpace($Id)) { return "" }
    if ($Id.Contains(":")) { return $Id.Split(":", 2)[1] }
    return $Id
}

function Normalize-Id([string]$Id) {
    return ((Get-PlainId $Id) -replace '[^a-z0-9]', '').ToLowerInvariant()
}

function Split-IdTokens([string]$Id) {
    $plain = Get-PlainId $Id
    $withCamelBreaks = [regex]::Replace($plain, '(?<=[a-z0-9])([A-Z])', '_$1')
    return @($withCamelBreaks.ToLowerInvariant().Split('_') | Where-Object { $_ })
}

function Get-LevenshteinDistance([string]$Left, [string]$Right) {
    if ($null -eq $Left) { $Left = "" }
    if ($null -eq $Right) { $Right = "" }

    $n = [int]$Left.Length
    $m = [int]$Right.Length
    if ($n -eq 0) { return [int]$m }
    if ($m -eq 0) { return [int]$n }

    [int[]]$previous = New-Object int[] ($m + 1)
    [int[]]$current = New-Object int[] ($m + 1)

    for ($j = 0; $j -le $m; $j++) {
        $previous[$j] = [int]$j
    }

    for ($i = 1; $i -le $n; $i++) {
        $current[0] = [int]$i
        for ($j = 1; $j -le $m; $j++) {
            $cost = if ($Left[$i - 1] -eq $Right[$j - 1]) { 0 } else { 1 }
            $delete = [int]($previous[$j] + 1)
            $insert = [int]($current[$j - 1] + 1)
            $substitute = [int]($previous[$j - 1] + $cost)
            $current[$j] = [int][math]::Min([math]::Min($delete, $insert), $substitute)
        }

        $swap = $previous
        $previous = $current
        $current = $swap
    }

    return [int]$previous[$m]
}

function Get-SharedTokenCount([string[]]$LeftTokens, [string[]]$RightTokens) {
    $rightSet = @{}
    foreach ($token in @($RightTokens)) {
        if (-not [string]::IsNullOrWhiteSpace($token)) {
            $rightSet[$token] = $true
        }
    }

    [int]$count = 0
    foreach ($token in @($LeftTokens)) {
        if ($rightSet.ContainsKey($token)) {
            $count++
        }
    }
    return [int]$count
}

$portEntriesByKind = @{}
foreach ($entry in @($portManifest.entries)) {
    if (-not $portEntriesByKind.ContainsKey($entry.kind)) {
        $portEntriesByKind[$entry.kind] = [System.Collections.Generic.List[object]]::new()
    }
    $portEntriesByKind[$entry.kind].Add($entry)
}

function Find-BestPortMatch($Candidate, $PortEntriesByKind) {
    $kind = $Candidate.kind
    $legacyPlain = Get-PlainId $Candidate.id
    $legacyNorm = Normalize-Id $Candidate.id
    $legacyTokens = Split-IdTokens $legacyPlain

    if (-not $PortEntriesByKind.ContainsKey($kind)) {
        return $null
    }

    $matches = [System.Collections.Generic.List[object]]::new()
    foreach ($entry in $PortEntriesByKind[$kind]) {
        $portPlain = [string]$entry.registryId
        $portNorm = Normalize-Id $portPlain
        [int]$distance = @(Get-LevenshteinDistance $legacyNorm $portNorm)[-1]
        [int]$sharedTokens = @(Get-SharedTokenCount $legacyTokens (Split-IdTokens $portPlain))[-1]
        $normalizedEqual = $legacyNorm -eq $portNorm
        $containsNormalized = $portNorm.Contains($legacyNorm) -or $legacyNorm.Contains($portNorm)

        [int]$score = $distance
        if ($normalizedEqual) { $score = $score - 100 }
        if ($containsNormalized) { $score = $score - 20 }
        $score = $score - ([int]$sharedTokens * 4)

        $matches.Add([pscustomobject][ordered]@{
            portId = $portPlain
            normalizedEqual = $normalizedEqual
            containsNormalized = $containsNormalized
            distance = $distance
            sharedTokens = $sharedTokens
            score = $score
        })
    }

    return $matches | Sort-Object score, distance, portId | Select-Object -First 1
}

function Get-ReviewCategory($Candidate, $BestMatch) {
    if ($null -eq $BestMatch) {
        return "real_missing_or_deferred_candidate"
    }
    if ($BestMatch.normalizedEqual) {
        return "known_rename_candidate"
    }
    if ($BestMatch.distance -le 3 -or $BestMatch.containsNormalized -or $BestMatch.sharedTokens -ge 2) {
        return "possible_rename_candidate"
    }
    if ($Candidate.evidence -match 'meta|variant|damage|EnumDyeColor|ItemBlock' -or $Candidate.id -match '^(crystal|nitor|candle|banner|slab|stair|metal|ingot|nugget|plate)') {
        return "variant_or_split_id_candidate"
    }
    return "real_missing_or_deferred_candidate"
}

function Get-RecommendedRuleFile([string]$ReviewCategory) {
    switch ($ReviewCategory) {
        "known_rename_candidate" { return "known-renames.json" }
        "possible_rename_candidate" { return "known-renames.json after manual evidence review" }
        "variant_or_split_id_candidate" { return "variant-mapping.json" }
        default { return "deferred-boundaries.json or implementation backlog" }
    }
}

$registryCandidates = @($candidateReport.candidates | Where-Object { $_.category -eq "registry_gap_candidate" })
$reviewRows = [System.Collections.Generic.List[object]]::new()

foreach ($candidate in $registryCandidates) {
    $bestMatch = Find-BestPortMatch $candidate $portEntriesByKind
    $reviewCategory = Get-ReviewCategory $candidate $bestMatch
    $reviewRows.Add([pscustomobject][ordered]@{
        kind = $candidate.kind
        id = $candidate.id
        legacyId = $candidate.legacyId
        reviewCategory = $reviewCategory
        suggestedPortId = if ($null -ne $bestMatch) { "thaumcraft:$($bestMatch.portId)" } else { $null }
        matchDistance = if ($null -ne $bestMatch) { $bestMatch.distance } else { $null }
        sharedTokens = if ($null -ne $bestMatch) { $bestMatch.sharedTokens } else { $null }
        recommendedRuleFile = Get-RecommendedRuleFile $reviewCategory
        evidence = $candidate.evidence
    })
}

$orderedRows = @($reviewRows | Sort-Object reviewCategory, kind, id)
$summaryByCategory = @(
    $orderedRows |
        Group-Object reviewCategory |
        Sort-Object Name |
        ForEach-Object {
            [pscustomobject][ordered]@{
                reviewCategory = $_.Name
                count = $_.Count
            }
        }
)

$output = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    candidateReport = $CandidateReportPath
    portManifest = $PortManifestPath
    registryCandidateCount = $orderedRows.Count
    summaryByCategory = $summaryByCategory
    candidates = $orderedRows
}

foreach ($path in @($OutputJson, $OutputMarkdown)) {
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
}

$output | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block parity registry gap review")
$lines.Add("")
$lines.Add("Generated: $($output.generatedAtUtc)")
$lines.Add("")
$lines.Add("Source candidates: ``$CandidateReportPath``")
$lines.Add("")
$lines.Add("This is a review helper. Suggested matches are heuristics and must not be copied into rule files without source evidence.")
$lines.Add("")
$lines.Add("## Summary by review category")
$lines.Add("")
$lines.Add("| Review category | Count |")
$lines.Add("|---|---:|")
foreach ($group in $summaryByCategory) {
    $lines.Add("| $($group.reviewCategory) | $($group.count) |")
}
$lines.Add("")
$lines.Add("## Registry candidates")
$lines.Add("")
$lines.Add("| Category | Kind | Legacy ID | Suggested port ID | Distance | Rule target | Evidence |")
$lines.Add("|---|---|---|---|---:|---|---|")
foreach ($row in $orderedRows) {
    $evidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
    $suggested = if ($row.suggestedPortId) { "``$($row.suggestedPortId)``" } else { "" }
    $distance = if ($null -ne $row.matchDistance) { $row.matchDistance } else { "" }
    $lines.Add("| $($row.reviewCategory) | $($row.kind) | ``$($row.id)`` | $suggested | $distance | $($row.recommendedRuleFile) | $evidence |")
}

$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Registry gap review: $OutputMarkdown"
Write-Output "RegistryCandidates=$($orderedRows.Count)"
foreach ($group in $summaryByCategory) {
    Write-Output "$($group.reviewCategory)=$($group.count)"
}