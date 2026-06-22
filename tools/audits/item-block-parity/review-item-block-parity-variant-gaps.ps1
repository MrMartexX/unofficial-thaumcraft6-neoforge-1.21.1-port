[CmdletBinding()]
param(
    [string]$RepoRoot,

    [string]$RegistryReviewPath,

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
if (-not $RegistryReviewPath) {
    $RegistryReviewPath = Join-Path $reportRoot "item_block_parity_registry_gap_review.json"
}
if (-not $PortManifestPath) {
    $PortManifestPath = Join-Path $reportRoot "port_manifest.json"
}
if (-not $OutputJson) {
    $OutputJson = Join-Path $reportRoot "item_block_parity_variant_gap_review.json"
}
if (-not $OutputMarkdown) {
    $OutputMarkdown = Join-Path $reportRoot "item_block_parity_variant_gap_review.md"
}

foreach ($path in @($RegistryReviewPath, $PortManifestPath)) {
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "Required input not found: $path"
    }
}

$registryReview = Get-Content -Raw -LiteralPath $RegistryReviewPath | ConvertFrom-Json
$portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json

function Get-PlainId([string]$Id) {
    if ([string]::IsNullOrWhiteSpace($Id)) { return "" }
    if ($Id.Contains(":")) { return $Id.Split(":", 2)[1] }
    return $Id
}

function Get-QuotedStrings([string]$Text) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return @() }
    return @([regex]::Matches($Text, '"(?<value>[^"]+)"') | ForEach-Object { $_.Groups["value"].Value })
}

function Get-CandidatePortIds([string]$BaseId, [string]$Variant) {
    $base = (Get-PlainId $BaseId).ToLowerInvariant()
    $variantId = (Get-PlainId $Variant).ToLowerInvariant()
    $candidates = [System.Collections.Generic.List[string]]::new()

    function Add-Candidate([string]$CandidateId) {
        if (-not [string]::IsNullOrWhiteSpace($CandidateId) -and -not $candidates.Contains($CandidateId)) {
            $candidates.Add($CandidateId)
        }
    }

    switch ($base) {
        "cluster" {
            Add-Candidate "cluster_$variantId"
        }
        "ingot" {
            Add-Candidate "${variantId}_ingot"
        }
        "nugget" {
            Add-Candidate "${variantId}_nugget"
            if ($variantId -eq "rareearth") {
                Add-Candidate "rare_earth"
            }
        }
        "plate" {
            Add-Candidate "${variantId}_plate"
        }
        "mind" {
            Add-Candidate "mind$variantId"
            Add-Candidate "mind_$variantId"
        }
        default {
            Add-Candidate "${base}_$variantId"
            Add-Candidate "${variantId}_$base"
        }
    }

    return @($candidates)
}

$portIdsByKind = @{}
foreach ($entry in @($portManifest.entries)) {
    if (-not $portIdsByKind.ContainsKey($entry.kind)) {
        $portIdsByKind[$entry.kind] = @{}
    }
    $portIdsByKind[$entry.kind][$entry.registryId] = $true
}

function Find-PortVariantMatches([string]$Kind, [string]$BaseId, [string]$Variant, $PortIdsByKind) {
    $matches = [System.Collections.Generic.List[string]]::new()
    if (-not $PortIdsByKind.ContainsKey($Kind)) {
        return @()
    }

    $portIds = $PortIdsByKind[$Kind]
    $candidateIds = Get-CandidatePortIds $BaseId $Variant
    foreach ($candidateId in $candidateIds) {
        if ($portIds.ContainsKey($candidateId) -and -not $matches.Contains($candidateId)) {
            $matches.Add($candidateId)
        }
    }

    return @($matches | Sort-Object)
}

$rows = [System.Collections.Generic.List[object]]::new()

foreach ($candidate in @($registryReview.candidates)) {
    $quoted = Get-QuotedStrings $candidate.evidence
    if ($quoted.Count -lt 2) {
        continue
    }

    $legacyBase = $quoted[0]
    $variants = @($quoted | Select-Object -Skip 1)
    if ($variants.Count -eq 0) {
        continue
    }

    $variantRows = [System.Collections.Generic.List[object]]::new()
    $matchedCount = 0
    foreach ($variant in $variants) {
        $matches = @(Find-PortVariantMatches $candidate.kind $legacyBase $variant $portIdsByKind)
        if ($matches.Count -gt 0) {
            $matchedCount++
        }
        $variantRows.Add([pscustomobject][ordered]@{
            legacyVariant = $variant
            expectedPortIdPatterns = @(Get-CandidatePortIds $legacyBase $variant | ForEach-Object { "thaumcraft:$_" })
            candidatePortIds = @($matches | ForEach-Object { "thaumcraft:$_" })
            candidateCount = $matches.Count
        })
    }

    $confidence = if ($matchedCount -eq $variants.Count) {
        "high"
    } elseif ($matchedCount -gt 0) {
        "medium"
    } else {
        "review"
    }

    $rows.Add([pscustomobject][ordered]@{
        kind = $candidate.kind
        legacyBaseId = "thaumcraft:$legacyBase"
        registryGapId = $candidate.id
        reviewCategory = $candidate.reviewCategory
        variantCount = $variants.Count
        matchedVariantCount = $matchedCount
        confidence = $confidence
        variants = @($variantRows)
        recommendedRuleFile = "variant-mapping.json"
        evidence = $candidate.evidence
    })
}

$orderedRows = @($rows | Sort-Object confidence, kind, legacyBaseId)
$summaryByConfidence = @(
    $orderedRows |
        Group-Object confidence |
        Sort-Object Name |
        ForEach-Object {
            [pscustomobject][ordered]@{
                confidence = $_.Name
                count = $_.Count
            }
        }
)

$output = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    registryReview = $RegistryReviewPath
    portManifest = $PortManifestPath
    matcher = "strict-base-patterns-v1"
    candidateCount = $orderedRows.Count
    summaryByConfidence = $summaryByConfidence
    candidates = $orderedRows
}

foreach ($path in @($OutputJson, $OutputMarkdown)) {
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
}

$output | ConvertTo-Json -Depth 20 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block parity variant gap review")
$lines.Add("")
$lines.Add("Generated: $($output.generatedAtUtc)")
$lines.Add("")
$lines.Add("Source registry review: ``$RegistryReviewPath``")
$lines.Add("")
$lines.Add("Matcher: strict-base-patterns-v1")
$lines.Add("")
$lines.Add("This report extracts legacy metadata-style base IDs and variants from source evidence and searches only for strict modern split-ID patterns. It does not edit ``variant-mapping.json``.")
$lines.Add("")
$lines.Add("## Summary by confidence")
$lines.Add("")
$lines.Add("| Confidence | Count |")
$lines.Add("|---|---:|")
foreach ($group in $summaryByConfidence) {
    $lines.Add("| $($group.confidence) | $($group.count) |")
}
$lines.Add("")
$lines.Add("## Variant candidates")
$lines.Add("")
$lines.Add("| Confidence | Kind | Legacy base | Matched variants | Variants | Evidence |")
$lines.Add("|---|---|---|---:|---|---|")
foreach ($row in $orderedRows) {
    $variantText = @(
        foreach ($variant in @($row.variants)) {
            $ports = if ($variant.candidatePortIds.Count -gt 0) { $variant.candidatePortIds -join ", " } else { "no port match" }
            "$($variant.legacyVariant) => $ports"
        }
    ) -join "<br>"
    $evidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
    $lines.Add("| $($row.confidence) | $($row.kind) | ``$($row.legacyBaseId)`` | $($row.matchedVariantCount)/$($row.variantCount) | $variantText | $evidence |")
}

$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Variant gap review: $OutputMarkdown"
Write-Output "Matcher=strict-base-patterns-v1"
Write-Output "VariantCandidates=$($orderedRows.Count)"
foreach ($group in $summaryByConfidence) {
    Write-Output "$($group.confidence)=$($group.count)"
}