param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$LegacySourceRoot = '',
    [string]$PageGapAuditPath = '',
    [string]$OutputPath = ''
)

$ErrorActionPreference = 'Stop'

function Escape-Md {
    param([string]$Value)
    if ($null -eq $Value) { return '' }
    return ($Value -replace '\|', '\|' -replace "`r?`n", '<br>')
}

function Relative-Path {
    param([string]$Base, [string]$Path)
    try {
        return [System.IO.Path]::GetRelativePath($Base, $Path).Replace('\', '/')
    } catch {
        return $Path.Replace('\', '/')
    }
}

function Classify-Reference {
    param([string]$Reference)
    $key = $Reference -replace '^thaumcraft:', ''
    if ($key -like 'Seal*') { return 'SEAL_BOUNDARY' }
    if ($key -eq 'GolemPress') { return 'GOLEM_MACHINE_BOUNDARY' }
    if ($key -eq 'JarBrain') { return 'JAR_BRAIN_BOUNDARY' }
    if ($key -eq 'MindBiothaumic') { return 'MIND_COMPONENT_BOUNDARY' }
    return 'GOLEMANCY_BOUNDARY'
}

function Hit-Kind {
    param([string]$Text)
    if ($Text -match 'addArcaneCraftingRecipe|ShapedArcaneRecipe|ShapelessArcaneRecipe') { return 'ARCANE_RECIPE_SOURCE' }
    if ($Text -match 'addInfusionCraftingRecipe|InfusionRecipe') { return 'INFUSION_RECIPE_SOURCE' }
    if ($Text -match 'Seal[A-Z]|SealManager|SealHandler|new\s+Seal') { return 'SEAL_BEHAVIOR_SOURCE' }
    if ($Text -match 'Golem|golem|Brain|brain|Jar|jar|Press|press') { return 'GOLEM_BLOCK_OR_ITEM_SOURCE' }
    return 'TEXT_HIT'
}

function Context-Block {
    param([string[]]$Lines, [int]$Index, [int]$Radius = 4)

    $start = [Math]::Max(0, $Index - $Radius)
    $end = [Math]::Min($Lines.Count - 1, $Index + $Radius)
    $out = New-Object System.Collections.Generic.List[string]
    for ($i = $start; $i -le $end; $i++) {
        [void]$out.Add($Lines[$i].TrimEnd())
    }

    return [pscustomobject]@{
        StartLine = $start + 1
        Text = ($out -join "`n")
    }
}

$repo = Resolve-Path -LiteralPath $RepoRoot
if ([string]::IsNullOrWhiteSpace($LegacySourceRoot)) {
    $LegacySourceRoot = Join-Path $repo '02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master'
}
if ([string]::IsNullOrWhiteSpace($PageGapAuditPath)) {
    $PageGapAuditPath = Join-Path $repo '06_docs/audits/research_recipe_page_gap_audit.md'
}
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $repo '06_docs/audits/golemancy_page_boundary_source_audit.md'
}

$legacy = Resolve-Path -LiteralPath $LegacySourceRoot
$audit = Resolve-Path -LiteralPath $PageGapAuditPath
$outputFullPath = [System.IO.Path]::GetFullPath($OutputPath)

$auditLines = Get-Content -LiteralPath $audit -Encoding UTF8
$refs = New-Object System.Collections.Generic.List[object]

foreach ($line in $auditLines) {
    if (-not $line.Contains('| GOLEMANCY_PAGE_DEFERRED |')) { continue }

    $cells = $line.Trim().Trim('|').Split('|') | ForEach-Object { $_.Trim() }
    if ($cells.Count -lt 4) { continue }

    $reference = $cells[1]
    if (-not $reference.StartsWith('thaumcraft:')) { continue }

    if (-not ($refs | Where-Object { $_.Reference -eq $reference })) {
        [void]$refs.Add([pscustomobject]@{
            Reference = $reference
            Key = ($reference -replace '^thaumcraft:', '')
            Classification = Classify-Reference -Reference $reference
            ResearchFile = $cells[2]
            JsonPath = $cells[3]
        })
    }
}

if ($refs.Count -eq 0) {
    throw "No GOLEMANCY_PAGE_DEFERRED references found in $PageGapAuditPath"
}

$javaFiles = @(Get-ChildItem -LiteralPath $legacy -Recurse -File -Filter '*.java')
$hits = New-Object System.Collections.Generic.List[object]

foreach ($ref in $refs) {
    $terms = New-Object System.Collections.Generic.List[string]
    [void]$terms.Add($ref.Reference)
    [void]$terms.Add($ref.Key)
    [void]$terms.Add($ref.Key.ToLowerInvariant())
    if ($ref.Key -like 'Seal*') {
        [void]$terms.Add($ref.Key.Substring(4))
    }

    foreach ($file in $javaFiles) {
        $lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8 -ErrorAction SilentlyContinue
        for ($i = 0; $i -lt $lines.Count; $i++) {
            $line = $lines[$i]
            $matchedTerm = ''
            foreach ($term in $terms.ToArray()) {
                if ([string]::IsNullOrWhiteSpace($term)) { continue }
                if ($line.IndexOf($term, [System.StringComparison]::OrdinalIgnoreCase) -ge 0) {
                    $matchedTerm = $term
                    break
                }
            }

            if ([string]::IsNullOrWhiteSpace($matchedTerm)) { continue }

            $context = Context-Block -Lines $lines -Index $i
            [void]$hits.Add([pscustomobject]@{
                Reference = $ref.Reference
                Classification = $ref.Classification
                HitKind = Hit-Kind -Text $context.Text
                MatchedTerm = $matchedTerm
                File = Relative-Path -Base $legacy -Path $file.FullName
                Line = $i + 1
                ContextStartLine = $context.StartLine
                Context = $context.Text
            })
        }
    }
}

$uniqueHitRefs = @($hits | Select-Object -ExpandProperty Reference -Unique)
$unresolved = @($refs | Where-Object { $uniqueHitRefs -notcontains $_.Reference })

$classCounts = @{}
foreach ($ref in $refs) {
    if (-not $classCounts.ContainsKey($ref.Classification)) { $classCounts[$ref.Classification] = 0 }
    $classCounts[$ref.Classification]++
}

$hitKindCounts = @{}
foreach ($hit in $hits) {
    if (-not $hitKindCounts.ContainsKey($hit.HitKind)) { $hitKindCounts[$hit.HitKind] = 0 }
    $hitKindCounts[$hit.HitKind]++
}

$sb = New-Object System.Text.StringBuilder
[void]$sb.AppendLine('# Golemancy Page Boundary Source Audit')
[void]$sb.AppendLine()
[void]$sb.AppendLine('Generated: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz'))
[void]$sb.AppendLine()
[void]$sb.AppendLine('## Purpose')
[void]$sb.AppendLine()
[void]$sb.AppendLine('This document extracts legacy source evidence for current `GOLEMANCY_PAGE_DEFERRED` recipe-page references. It is an analysis artifact only; do not treat every hit as a recipe implementation target.')
[void]$sb.AppendLine()
[void]$sb.AppendLine('## Summary')
[void]$sb.AppendLine()
[void]$sb.AppendLine('| Metric | Count |')
[void]$sb.AppendLine('|---|---:|')
[void]$sb.AppendLine('| Golemancy deferred references | ' + $refs.Count + ' |')
[void]$sb.AppendLine('| References with at least one source hit | ' + $uniqueHitRefs.Count + ' |')
[void]$sb.AppendLine('| References without source hit | ' + $unresolved.Count + ' |')
[void]$sb.AppendLine('| Legacy Java files scanned | ' + $javaFiles.Count + ' |')
[void]$sb.AppendLine('| Source hits | ' + $hits.Count + ' |')
[void]$sb.AppendLine()
[void]$sb.AppendLine('## Boundary classification')
[void]$sb.AppendLine()
[void]$sb.AppendLine('| Classification | Count |')
[void]$sb.AppendLine('|---|---:|')
foreach ($key in ($classCounts.Keys | Sort-Object)) {
    [void]$sb.AppendLine('| ' + $key + ' | ' + $classCounts[$key] + ' |')
}
[void]$sb.AppendLine()
[void]$sb.AppendLine('## Source hit kind distribution')
[void]$sb.AppendLine()
[void]$sb.AppendLine('| Hit kind | Count |')
[void]$sb.AppendLine('|---|---:|')
foreach ($key in ($hitKindCounts.Keys | Sort-Object)) {
    [void]$sb.AppendLine('| ' + $key + ' | ' + $hitKindCounts[$key] + ' |')
}
[void]$sb.AppendLine()
[void]$sb.AppendLine('## Deferred reference overview')
[void]$sb.AppendLine()
[void]$sb.AppendLine('| Reference | Classification | Research file | JSON path | Source hits |')
[void]$sb.AppendLine('|---|---|---|---|---:|')
foreach ($ref in ($refs | Sort-Object Reference)) {
    $count = @($hits | Where-Object { $_.Reference -eq $ref.Reference }).Count
    [void]$sb.AppendLine('| ' + (Escape-Md $ref.Reference) + ' | ' + $ref.Classification + ' | ' + (Escape-Md $ref.ResearchFile) + ' | ' + (Escape-Md $ref.JsonPath) + ' | ' + $count + ' |')
}
[void]$sb.AppendLine()
[void]$sb.AppendLine('## Source hit overview')
[void]$sb.AppendLine()
[void]$sb.AppendLine('| Reference | Hit kind | File | Line | Matched term |')
[void]$sb.AppendLine('|---|---|---|---:|---|')
foreach ($hit in ($hits | Sort-Object Reference, File, Line)) {
    [void]$sb.AppendLine('| ' + (Escape-Md $hit.Reference) + ' | ' + $hit.HitKind + ' | ' + (Escape-Md $hit.File) + ' | ' + $hit.Line + ' | ' + (Escape-Md $hit.MatchedTerm) + ' |')
}
[void]$sb.AppendLine()
[void]$sb.AppendLine('## Extracted source contexts')
foreach ($hit in ($hits | Sort-Object Reference, File, Line)) {
    [void]$sb.AppendLine()
    [void]$sb.AppendLine('### ' + $hit.Reference + ' @ ' + $hit.File + ':' + $hit.Line)
    [void]$sb.AppendLine()
    [void]$sb.AppendLine('- Classification: ' + $hit.Classification)
    [void]$sb.AppendLine('- Hit kind: ' + $hit.HitKind)
    [void]$sb.AppendLine('- Context start line: ' + $hit.ContextStartLine)
    [void]$sb.AppendLine()
    [void]$sb.AppendLine('```java')
    [void]$sb.AppendLine($hit.Context)
    [void]$sb.AppendLine('```')
}
if ($unresolved.Count -gt 0) {
    [void]$sb.AppendLine()
    [void]$sb.AppendLine('## References without source hits')
    [void]$sb.AppendLine()
    [void]$sb.AppendLine('| Reference | Classification |')
    [void]$sb.AppendLine('|---|---|')
    foreach ($ref in $unresolved) {
        [void]$sb.AppendLine('| ' + (Escape-Md $ref.Reference) + ' | ' + $ref.Classification + ' |')
    }
}
[void]$sb.AppendLine()
[void]$sb.AppendLine('## Next implementation guidance')
[void]$sb.AppendLine()
[void]$sb.AppendLine('1. Do not implement all golemancy deferred references as recipes blindly.')
[void]$sb.AppendLine('2. Separate seal behavior placeholders from actual crafting/arcane recipes.')
[void]$sb.AppendLine('3. Treat `GolemPress`, `JarBrain`, and `MindBiothaumic` as block/item/machine boundaries until verified against exact source.')
[void]$sb.AppendLine('4. Choose one narrow family per batch, then re-run research recipe page gap audit.')

[System.IO.File]::WriteAllText($outputFullPath, $sb.ToString(), [System.Text.UTF8Encoding]::new($false))

if ((Get-Item -LiteralPath $outputFullPath).Length -lt 200) {
    throw "Generated golemancy audit is unexpectedly small: $outputFullPath"
}

Write-Host "Wrote $outputFullPath"
