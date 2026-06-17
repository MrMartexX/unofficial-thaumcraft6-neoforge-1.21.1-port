param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$LegacySourceRoot = '',
    [string]$GapAuditPath = '',
    [string]$OutputPath = ''
)

$ErrorActionPreference = 'Stop'

function Escape-Md {
    param([string]$Value)
    if ($null -eq $Value) {
        return ''
    }
    return ($Value -replace '\|', '\|' -replace "`r?`n", ' ')
}

function Short-Line {
    param([string]$Value)
    if ($null -eq $Value) {
        return ''
    }
    $line = $Value.Trim()
    if ($line.Length -gt 220) {
        return $line.Substring(0, 220) + ' ...'
    }
    return $line
}

function Get-RelativePathSafe {
    param(
        [string]$Base,
        [string]$Path
    )

    try {
        return [System.IO.Path]::GetRelativePath($Base, $Path).Replace('\', '/')
    }
    catch {
        return $Path.Replace('\', '/')
    }
}

function Get-RecipeFamily {
    param([string]$Reference)

    $key = ($Reference -replace '^thaumcraft:', '')

    if ($key -match '^hedge_') {
        return 'HEDGE_ALCHEMY'
    }
    if ($key -match '^metal_purification_') {
        return 'METAL_PURIFICATION'
    }
    if ($key -match '(?i)nitor') {
        return 'NITOR'
    }
    if ($key -match '(?i)brass') {
        return 'BRASS_ALCHEMY'
    }
    if ($key -match '(?i)alumentum') {
        return 'ALUMENTUM'
    }
    if ($key -match '(?i)Bath|Bottle|Liquid|Sane|Soap|Taint') {
        return 'SPECIAL_ALCHEMY'
    }
    if ($key -match '(?i)tallow|soap|salt|salismundus|salis') {
        return 'BASIC_ALCHEMY'
    }
    return 'ALCHEMY_OTHER'
}

$repo = Resolve-Path -LiteralPath $RepoRoot

if ([string]::IsNullOrWhiteSpace($LegacySourceRoot)) {
    $LegacySourceRoot = Join-Path $repo '02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master'
}
if ([string]::IsNullOrWhiteSpace($GapAuditPath)) {
    $GapAuditPath = Join-Path $repo '06_docs/audits/research_recipe_page_gap_audit.md'
}
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $repo '06_docs/audits/legacy_alchemy_recipe_source_audit.md'
}

$legacy = Resolve-Path -LiteralPath $LegacySourceRoot
$gapAudit = Resolve-Path -LiteralPath $GapAuditPath
$outputFullPath = [System.IO.Path]::GetFullPath($OutputPath)
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputFullPath) | Out-Null

$gapLines = Get-Content -LiteralPath $gapAudit -Encoding UTF8
$alchemyRefs = New-Object System.Collections.Generic.List[object]

foreach ($line in $gapLines) {
    if ($line -match '^\| (?<class>ALCHEMY_[^|]+) \| (?<ref>[^|]+) \| (?<file>[^|]+) \| (?<path>[^|]+) \|') {
        $ref = $matches['ref'].Trim()
        $alchemyRefs.Add([pscustomobject]@{
            Class = $matches['class'].Trim()
            Reference = $ref
            Key = ($ref -replace '^thaumcraft:', '')
            Family = Get-RecipeFamily -Reference $ref
            ResearchFile = $matches['file'].Trim()
            JsonPath = $matches['path'].Trim()
        })
    }
}

$javaFiles = @(Get-ChildItem -LiteralPath $legacy -Recurse -File -Filter '*.java')
$legacyTextFiles = @(Get-ChildItem -LiteralPath $legacy -Recurse -File -Include '*.java', '*.json', '*.lang', '*.txt' -ErrorAction SilentlyContinue)

$hits = New-Object System.Collections.Generic.List[object]

foreach ($ref in $alchemyRefs) {
    $terms = New-Object System.Collections.Generic.HashSet[string]([System.StringComparer]::OrdinalIgnoreCase)
    [void]$terms.Add($ref.Reference)
    [void]$terms.Add($ref.Key)
    [void]$terms.Add($ref.Key.Replace('_', ''))
    [void]$terms.Add($ref.Key.Replace('_', ' '))
    [void]$terms.Add($ref.Key.ToUpperInvariant())
    [void]$terms.Add($ref.Key.ToLowerInvariant())

    foreach ($file in $legacyTextFiles) {
        $lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8 -ErrorAction SilentlyContinue
        for ($i = 0; $i -lt $lines.Count; $i++) {
            foreach ($term in $terms) {
                if ([string]::IsNullOrWhiteSpace($term)) {
                    continue
                }
                if ($lines[$i].IndexOf($term, [System.StringComparison]::OrdinalIgnoreCase) -ge 0) {
                    $hits.Add([pscustomobject]@{
                        Reference = $ref.Reference
                        Family = $ref.Family
                        Term = $term
                        File = Get-RelativePathSafe -Base $legacy -Path $file.FullName
                        Line = $i + 1
                        Snippet = Short-Line -Value $lines[$i]
                    })
                    break
                }
            }
        }
    }
}

$apiHits = New-Object System.Collections.Generic.List[object]
$apiPatterns = @(
    'addCrucibleRecipe',
    'CrucibleRecipe',
    'addInfusionCraftingRecipe',
    'InfusionRecipe',
    'ShapedArcane',
    'ShapelessArcane',
    'RecipesAlchemy',
    'ConfigRecipes'
)

foreach ($file in $javaFiles) {
    $lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8 -ErrorAction SilentlyContinue
    for ($i = 0; $i -lt $lines.Count; $i++) {
        foreach ($pattern in $apiPatterns) {
            if ($lines[$i].IndexOf($pattern, [System.StringComparison]::OrdinalIgnoreCase) -ge 0) {
                $apiHits.Add([pscustomobject]@{
                    Pattern = $pattern
                    File = Get-RelativePathSafe -Base $legacy -Path $file.FullName
                    Line = $i + 1
                    Snippet = Short-Line -Value $lines[$i]
                })
                break
            }
        }
    }
}

$hitByReference = @{}
foreach ($hit in $hits) {
    if (-not $hitByReference.ContainsKey($hit.Reference)) {
        $hitByReference[$hit.Reference] = New-Object System.Collections.Generic.List[object]
    }
    $hitByReference[$hit.Reference].Add($hit)
}

$refsWithHits = @($alchemyRefs | Where-Object { $hitByReference.ContainsKey($_.Reference) })
$refsWithoutHits = @($alchemyRefs | Where-Object { -not $hitByReference.ContainsKey($_.Reference) })
$familyGroups = @($alchemyRefs | Group-Object Family | Sort-Object -Property @{ Expression = 'Count'; Descending = $true }, Name)
$apiPatternGroups = @($apiHits | Group-Object Pattern | Sort-Object -Property @{ Expression = 'Count'; Descending = $true }, Name)

$linesOut = New-Object System.Collections.Generic.List[string]
$linesOut.Add('# Legacy Alchemy Recipe Source Audit')
$linesOut.Add('')
$linesOut.Add("Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')")
$linesOut.Add('')
$linesOut.Add('## Summary')
$linesOut.Add('')
$linesOut.Add('| Metric | Count |')
$linesOut.Add('|---|---:|')
$linesOut.Add("| Alchemy missing recipe-page references from current audit | $($alchemyRefs.Count) |")
$linesOut.Add("| References with at least one legacy source hit | $($refsWithHits.Count) |")
$linesOut.Add("| References without direct source hit | $($refsWithoutHits.Count) |")
$linesOut.Add("| Legacy Java files scanned | $($javaFiles.Count) |")
$linesOut.Add("| Legacy API/pattern source hits | $($apiHits.Count) |")
$linesOut.Add('')
$linesOut.Add('## Alchemy reference family distribution')
$linesOut.Add('')
$linesOut.Add('| Family | Count |')
$linesOut.Add('|---|---:|')
foreach ($group in $familyGroups) {
    $linesOut.Add("| $(Escape-Md $group.Name) | $($group.Count) |")
}
$linesOut.Add('')
$linesOut.Add('## Legacy recipe API/pattern hit distribution')
$linesOut.Add('')
if ($apiPatternGroups.Count -eq 0) {
    $linesOut.Add('No legacy recipe API/pattern hits found.')
} else {
    $linesOut.Add('| Pattern | Count |')
    $linesOut.Add('|---|---:|')
    foreach ($group in $apiPatternGroups) {
        $linesOut.Add("| $(Escape-Md $group.Name) | $($group.Count) |")
    }
}
$linesOut.Add('')
$linesOut.Add('## Alchemy page references and legacy source hits')
$linesOut.Add('')
if ($alchemyRefs.Count -eq 0) {
    $linesOut.Add('No alchemy missing recipe-page references were found in the current research page gap audit.')
} else {
    $linesOut.Add('| Family | Reference | Legacy hits | Research path |')
    $linesOut.Add('|---|---|---:|---|')
    foreach ($ref in ($alchemyRefs | Sort-Object Family, Reference, JsonPath)) {
        $count = 0
        if ($hitByReference.ContainsKey($ref.Reference)) {
            $count = $hitByReference[$ref.Reference].Count
        }
        $linesOut.Add("| $(Escape-Md $ref.Family) | $(Escape-Md $ref.Reference) | $count | $(Escape-Md $ref.JsonPath) |")
    }
}
$linesOut.Add('')
$linesOut.Add('## Representative legacy source hits')
$linesOut.Add('')
if ($hits.Count -eq 0) {
    $linesOut.Add('No direct legacy source hits were found for the alchemy page references.')
} else {
    $linesOut.Add('| Reference | Family | File | Line | Snippet |')
    $linesOut.Add('|---|---|---|---:|---|')
    foreach ($hit in ($hits | Sort-Object Family, Reference, File, Line | Select-Object -First 250)) {
        $linesOut.Add("| $(Escape-Md $hit.Reference) | $(Escape-Md $hit.Family) | $(Escape-Md $hit.File) | $($hit.Line) | $(Escape-Md $hit.Snippet) |")
    }
    if ($hits.Count -gt 250) {
        $linesOut.Add('')
        $linesOut.Add("Representative table truncated to first 250 source hits out of $($hits.Count).")
    }
}
$linesOut.Add('')
$linesOut.Add('## Legacy recipe API/pattern hit samples')
$linesOut.Add('')
if ($apiHits.Count -eq 0) {
    $linesOut.Add('No legacy recipe API/pattern hit samples found.')
} else {
    $linesOut.Add('| Pattern | File | Line | Snippet |')
    $linesOut.Add('|---|---|---:|---|')
    foreach ($hit in ($apiHits | Sort-Object Pattern, File, Line | Select-Object -First 250)) {
        $linesOut.Add("| $(Escape-Md $hit.Pattern) | $(Escape-Md $hit.File) | $($hit.Line) | $(Escape-Md $hit.Snippet) |")
    }
    if ($apiHits.Count -gt 250) {
        $linesOut.Add('')
        $linesOut.Add("API hit table truncated to first 250 hits out of $($apiHits.Count).")
    }
}
$linesOut.Add('')
$linesOut.Add('## References without direct legacy source hit')
$linesOut.Add('')
if ($refsWithoutHits.Count -eq 0) {
    $linesOut.Add('All alchemy page references had at least one direct legacy source hit.')
} else {
    $linesOut.Add('| Family | Reference | Research file | JSON path |')
    $linesOut.Add('|---|---|---|---|')
    foreach ($ref in ($refsWithoutHits | Sort-Object Family, Reference, JsonPath)) {
        $linesOut.Add("| $(Escape-Md $ref.Family) | $(Escape-Md $ref.Reference) | $(Escape-Md $ref.ResearchFile) | $(Escape-Md $ref.JsonPath) |")
    }
}
$linesOut.Add('')
$linesOut.Add('## Next implementation guidance')
$linesOut.Add('')
$linesOut.Add('1. The next large safe slice should be chosen from the highest-count alchemy family with direct legacy source hits.')
$linesOut.Add('2. Implement only recipe data model, serializer, loader audit, and Thaumonomicon page snapshot for the first selected family.')
$linesOut.Add('3. Do not implement crucible block behavior, essentia handling, alchemy machines, particles, or item transformations in the same slice.')
$linesOut.Add('4. Preserve legacy recipe IDs and research page references first; behavior can follow only after page/data parity is testable.')
$linesOut.Add('')

[System.IO.File]::WriteAllText($outputFullPath, ($linesOut -join "`n"), [System.Text.UTF8Encoding]::new($false))

Write-Host "Legacy alchemy recipe source audit written to $outputFullPath"
Write-Host "Alchemy refs: $($alchemyRefs.Count)"
Write-Host "Refs with legacy source hits: $($refsWithHits.Count)"
Write-Host "Refs without direct hits: $($refsWithoutHits.Count)"
Write-Host "Legacy API/pattern hits: $($apiHits.Count)"
