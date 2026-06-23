[CmdletBinding()]
param(
    [string]$RepoRoot,

    [string]$LegacyRoot = "02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master",

    [string]$ResourceReviewPath,

    [string]$PortLangPath,

    [string]$OutputJson,

    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"

if (-not $RepoRoot) {
    $RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path
}
$RepoRoot = (Resolve-Path $RepoRoot).Path

$reportRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity"
if (-not $ResourceReviewPath) {
    $ResourceReviewPath = Join-Path $reportRoot "item_block_parity_resource_gap_review.json"
}
if (-not $PortLangPath) {
    $PortLangPath = Join-Path $RepoRoot "05_neoforge_port/src/main/resources/assets/thaumcraft/lang/en_us.json"
}
if (-not $OutputJson) {
    $OutputJson = Join-Path $reportRoot "item_block_parity_legacy_lang_review.json"
}
if (-not $OutputMarkdown) {
    $OutputMarkdown = Join-Path $reportRoot "item_block_parity_legacy_lang_review.md"
}

foreach ($path in @($ResourceReviewPath, $PortLangPath)) {
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "Required input not found: $path"
    }
}

$legacyPath = Join-Path $RepoRoot $LegacyRoot
if (-not (Test-Path -LiteralPath $legacyPath -PathType Container)) {
    throw "Legacy root not found: $legacyPath"
}

function Get-PlainId([string]$Id) {
    if ([string]::IsNullOrWhiteSpace($Id)) { return "" }
    if ($Id.Contains(":")) { return $Id.Split(":", 2)[1] }
    return $Id
}

function Convert-JsonObjectToHashtable($Object) {
    $table = @{}
    if ($null -eq $Object) { return $table }
    foreach ($property in $Object.PSObject.Properties) {
        $table[$property.Name] = [string]$property.Value
    }
    return $table
}

function Read-LegacyLangFile([string]$Path) {
    $map = [ordered]@{}
    foreach ($rawLine in Get-Content -LiteralPath $Path) {
        $line = $rawLine.Trim()
        if ([string]::IsNullOrWhiteSpace($line)) { continue }
        if ($line.StartsWith("#")) { continue }
        $equalsIndex = $line.IndexOf("=")
        if ($equalsIndex -lt 1) { continue }
        $key = $line.Substring(0, $equalsIndex).Trim()
        $value = $line.Substring($equalsIndex + 1).Trim()
        if (-not [string]::IsNullOrWhiteSpace($key) -and -not $map.Contains($key)) {
            $map[$key] = $value
        }
    }
    return $map
}

function Find-LegacyLangFiles([string]$LegacyPath) {
    $knownCandidates = @(
        "src/main/resources/assets/thaumcraft/lang/en_us.lang",
        "src/main/resources/assets/thaumcraft/lang/en_US.lang",
        "src/main/resources/assets/thaumcraft/lang/en_us.json",
        "src/main/resources/assets/thaumcraft/lang/en_US.json",
        "assets/thaumcraft/lang/en_us.lang",
        "assets/thaumcraft/lang/en_US.lang"
    )

    $found = [System.Collections.Generic.List[string]]::new()
    foreach ($relative in $knownCandidates) {
        $candidate = Join-Path $LegacyPath $relative
        if ((Test-Path -LiteralPath $candidate -PathType Leaf) -and -not $found.Contains($candidate)) {
            $found.Add($candidate)
        }
    }

    if ($found.Count -eq 0) {
        $recursive = @(Get-ChildItem -LiteralPath $LegacyPath -Recurse -File -ErrorAction SilentlyContinue |
            Where-Object {
                $_.Name -match 'en[_-]?us\.(lang|json)$' -and
                $_.FullName -match 'assets[\\/]thaumcraft[\\/]lang'
            } |
            Select-Object -ExpandProperty FullName)
        foreach ($path in $recursive) {
            if (-not $found.Contains($path)) { $found.Add($path) }
        }
    }

    return @($found)
}

function Get-LegacyKeyCandidates([string]$Kind, [string]$PlainId) {
    $keys = [System.Collections.Generic.List[string]]::new()
    function Add-Key([string]$Key) {
        if (-not [string]::IsNullOrWhiteSpace($Key) -and -not $keys.Contains($Key)) {
            $keys.Add($Key)
        }
    }

    if ($Kind -eq "block") {
        Add-Key "tile.$PlainId.name"
        Add-Key "tile.thaumcraft.$PlainId.name"
        Add-Key "tile.thaumcraft:$PlainId.name"
        Add-Key "block.thaumcraft.$PlainId"
        Add-Key "tile.$PlainId"
        Add-Key "tile.thaumcraft.$PlainId"
    } else {
        Add-Key "item.$PlainId.name"
        Add-Key "item.thaumcraft.$PlainId.name"
        Add-Key "item.thaumcraft:$PlainId.name"
        Add-Key "item.thaumcraft.$PlainId"
        Add-Key "item.$PlainId"
    }

    return @($keys)
}

function Normalize-KeyPart([string]$Value) {
    if ($null -eq $Value) { return "" }
    return (($Value -replace '[^a-zA-Z0-9]', '')).ToLowerInvariant()
}

function Find-LikelyLegacyHits([string]$Kind, [string]$PlainId, $LegacyLangMap) {
    $prefixes = if ($Kind -eq "block") { @("tile.", "block.") } else { @("item.") }
    $plainNorm = Normalize-KeyPart $PlainId
    $hits = [System.Collections.Generic.List[object]]::new()

    foreach ($key in $LegacyLangMap.Keys) {
        $startsWithExpectedPrefix = $false
        foreach ($prefix in $prefixes) {
            if ($key.StartsWith($prefix)) {
                $startsWithExpectedPrefix = $true
                break
            }
        }
        if (-not $startsWithExpectedPrefix) { continue }

        $keyNorm = Normalize-KeyPart $key
        if ($keyNorm.Contains($plainNorm)) {
            $hits.Add([pscustomobject][ordered]@{
                key = $key
                value = $LegacyLangMap[$key]
            })
        }
    }

    return @($hits | Sort-Object key | Select-Object -First 8)
}

$resourceReview = Get-Content -Raw -LiteralPath $ResourceReviewPath | ConvertFrom-Json
$portLangObject = Get-Content -Raw -LiteralPath $PortLangPath | ConvertFrom-Json
$portLang = Convert-JsonObjectToHashtable $portLangObject

$legacyLangFiles = @(Find-LegacyLangFiles $legacyPath)
if ($legacyLangFiles.Count -eq 0) {
    throw "No legacy en_us lang files found under: $legacyPath"
}

$legacyLang = [ordered]@{}
foreach ($legacyLangFile in $legacyLangFiles) {
    if ($legacyLangFile.ToLowerInvariant().EndsWith(".json")) {
        $jsonMap = Convert-JsonObjectToHashtable (Get-Content -Raw -LiteralPath $legacyLangFile | ConvertFrom-Json)
        foreach ($key in $jsonMap.Keys) {
            if (-not $legacyLang.Contains($key)) { $legacyLang[$key] = $jsonMap[$key] }
        }
        continue
    }

    $langMap = Read-LegacyLangFile $legacyLangFile
    foreach ($key in $langMap.Keys) {
        if (-not $legacyLang.Contains($key)) { $legacyLang[$key] = $langMap[$key] }
    }
}

$langRows = [System.Collections.Generic.List[object]]::new()
foreach ($candidate in @($resourceReview.candidates | Where-Object { $_.reviewCategory -eq "lang_generation_candidate" })) {
    $plain = Get-PlainId $candidate.id
    $portKey = if ($candidate.suggestedLangKey) { [string]$candidate.suggestedLangKey } elseif ($candidate.kind -eq "block") { "block.thaumcraft.$plain" } else { "item.thaumcraft.$plain" }
    $legacyKeyCandidates = @(Get-LegacyKeyCandidates $candidate.kind $plain)

    $exactHits = [System.Collections.Generic.List[object]]::new()
    foreach ($legacyKey in $legacyKeyCandidates) {
        if ($legacyLang.Contains($legacyKey)) {
            $exactHits.Add([pscustomobject][ordered]@{
                key = $legacyKey
                value = $legacyLang[$legacyKey]
            })
        }
    }

    $likelyHits = if ($exactHits.Count -eq 0) { @(Find-LikelyLegacyHits $candidate.kind $plain $legacyLang) } else { @() }
    $portAlreadyPresent = $portLang.ContainsKey($portKey)

    $reviewCategory = if ($portAlreadyPresent) {
        "port_lang_already_present"
    } elseif ($exactHits.Count -gt 0) {
        "exact_legacy_lang_hit"
    } elseif ($likelyHits.Count -gt 0) {
        "likely_legacy_lang_hit"
    } else {
        "no_legacy_lang_hit"
    }

    $recommendedAction = switch ($reviewCategory) {
        "port_lang_already_present" { "Re-run parity extraction; current port lang already contains the key." }
        "exact_legacy_lang_hit" { "Safe candidate for legacy-value backfill after reviewing wording in the report." }
        "likely_legacy_lang_hit" { "Manual review required before backfill; key matched by normalized/fuzzy search only." }
        default { "No legacy localization found; defer or use explicit modern naming decision, not automatic parity claim." }
    }

    $langRows.Add([pscustomobject][ordered]@{
        kind = $candidate.kind
        id = $candidate.id
        portLangKey = $portKey
        reviewCategory = $reviewCategory
        exactLegacyHits = @($exactHits)
        likelyLegacyHits = @($likelyHits)
        candidateLegacyKeys = $legacyKeyCandidates
        recommendedAction = $recommendedAction
        evidence = $candidate.evidence
    })
}

$orderedRows = @($langRows | Sort-Object reviewCategory, kind, id)
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
    legacyRoot = $LegacyRoot.Replace("\\", "/")
    legacyLangFiles = @(
        foreach ($legacyLangFile in @($legacyLangFiles)) {
            $resolvedLegacyLangFile = (Resolve-Path -LiteralPath $legacyLangFile).Path
            $relativeLegacyLangFile = $resolvedLegacyLangFile
            if ($relativeLegacyLangFile.StartsWith($RepoRoot)) {
                $relativeLegacyLangFile = $relativeLegacyLangFile.Substring($RepoRoot.Length)
            }
            ($relativeLegacyLangFile -replace '^[\/]+', '').Replace([char]92, [char]47)
        }
    )
    legacyLangEntryCount = $legacyLang.Count
    resourceReview = $ResourceReviewPath
    portLangPath = $PortLangPath
    langCandidateCount = $orderedRows.Count
    summaryByCategory = $summaryByCategory
    candidates = $orderedRows
}

foreach ($path in @($OutputJson, $OutputMarkdown)) {
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $path) | Out-Null
}

$output | ConvertTo-Json -Depth 20 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block parity legacy lang coverage review")
$lines.Add("")
$lines.Add("Generated: $($output.generatedAtUtc)")
$lines.Add("")
$lines.Add("This report reviews missing port lang keys against the legacy Thaumcraft localization source. It does not edit ``en_us.json``.")
$lines.Add("")
$lines.Add("Legacy lang files:")
foreach ($legacyLangFile in @($output.legacyLangFiles)) {
    $lines.Add("- ``$legacyLangFile``")
}
$lines.Add("")
$lines.Add("Legacy lang entries loaded: $($output.legacyLangEntryCount)")
$lines.Add("")
$lines.Add("## Summary by review category")
$lines.Add("")
$lines.Add("| Review category | Count |")
$lines.Add("|---|---:|")
foreach ($group in $summaryByCategory) {
    $lines.Add("| $($group.reviewCategory) | $($group.count) |")
}
$lines.Add("")
$lines.Add("## Lang candidates")
$lines.Add("")
$lines.Add("| Category | Kind | ID | Port lang key | Legacy hit | Action | Evidence |")
$lines.Add("|---|---|---|---|---|---|---|")
foreach ($row in $orderedRows) {
    $legacyHitText = ""
    if ($row.exactLegacyHits.Count -gt 0) {
        $legacyHitText = @($row.exactLegacyHits | ForEach-Object { "``$($_.key)`` = $($_.value)" }) -join "<br>"
    } elseif ($row.likelyLegacyHits.Count -gt 0) {
        $legacyHitText = @($row.likelyLegacyHits | ForEach-Object { "``$($_.key)`` = $($_.value)" }) -join "<br>"
    } else {
        $legacyHitText = ""
    }
    $evidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
    $legacyHitText = $legacyHitText.Replace("|", "\|")
    $lines.Add("| $($row.reviewCategory) | $($row.kind) | ``$($row.id)`` | ``$($row.portLangKey)`` | $legacyHitText | $($row.recommendedAction) | $evidence |")
}

$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output "Legacy lang coverage review: $OutputMarkdown"
Write-Output "LangCandidates=$($orderedRows.Count)"
foreach ($group in $summaryByCategory) {
    Write-Output "$($group.reviewCategory)=$($group.count)"
}
