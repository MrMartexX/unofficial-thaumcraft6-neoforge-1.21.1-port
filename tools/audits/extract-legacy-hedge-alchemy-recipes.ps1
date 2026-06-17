param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$LegacySourceRoot = '',
    [string]$LegacyAlchemyAuditPath = '',
    [string]$OutputPath = ''
)

$ErrorActionPreference = 'Stop'

function Escape-Md {
    param([string]$Value)
    if ($null -eq $Value) {
        return ''
    }
    return ($Value -replace '\|', '\|' -replace "`r?`n", '<br>')
}

function Short-Line {
    param([string]$Value)
    if ($null -eq $Value) {
        return ''
    }
    $line = $Value.Trim()
    if ($line.Length -gt 260) {
        return $line.Substring(0, 260) + ' ...'
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

function Get-BalancedBlock {
    param(
        [string[]]$Lines,
        [int]$HitIndex
    )

    $start = $HitIndex
    while ($start -gt 0 -and $Lines[$start] -notmatch 'addCrucibleRecipe|new CrucibleRecipe') {
        $start--
        if (($HitIndex - $start) -gt 20) {
            $start = $HitIndex
            break
        }
    }

    $depth = 0
    $started = $false
    $block = New-Object System.Collections.Generic.List[string]

    for ($i = $start; $i -lt $Lines.Count; $i++) {
        $line = $Lines[$i]
        $block.Add($line.TrimEnd())

        foreach ($ch in $line.ToCharArray()) {
            if ($ch -eq '(') {
                $depth++
                $started = $true
            } elseif ($ch -eq ')') {
                $depth--
            }
        }

        if ($started -and $depth -le 0 -and $line -match ';\s*$') {
            break
        }

        if (($i - $start) -gt 40) {
            break
        }
    }

    return [pscustomobject]@{
        StartLine = $start + 1
        Text = ($block -join "`n")
    }
}

function Get-AspectsFromBlock {
    param([string]$Block)

    $matches = [regex]::Matches($Block, 'Aspect\.([A-Z_]+)\s*,\s*(\d+)')
    $parts = New-Object System.Collections.Generic.List[string]
    foreach ($match in $matches) {
        $aspect = $match.Groups[1].Value.ToLowerInvariant()
        $amount = $match.Groups[2].Value
        $parts.Add("$aspect=$amount")
    }
    return ($parts -join ', ')
}

function Get-ResearchFromBlock {
    param([string]$Block)

    $match = [regex]::Match($Block, 'new\s+CrucibleRecipe\s*\(\s*"([^"]+)"')
    if ($match.Success) {
        return $match.Groups[1].Value
    }
    return ''
}

function Get-ResourceIdFromBlock {
    param([string]$Block)

    $match = [regex]::Match($Block, 'new\s+ResourceLocation\s*\(\s*"([^"]+)"\s*\)')
    if ($match.Success) {
        return $match.Groups[1].Value
    }
    return ''
}

$repo = Resolve-Path -LiteralPath $RepoRoot

if ([string]::IsNullOrWhiteSpace($LegacySourceRoot)) {
    $LegacySourceRoot = Join-Path $repo '02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master'
}
if ([string]::IsNullOrWhiteSpace($LegacyAlchemyAuditPath)) {
    $LegacyAlchemyAuditPath = Join-Path $repo '06_docs/audits/legacy_alchemy_recipe_source_audit.md'
}
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $repo '06_docs/audits/hedge_alchemy_legacy_recipe_blocks.md'
}

$legacy = Resolve-Path -LiteralPath $LegacySourceRoot
$audit = Resolve-Path -LiteralPath $LegacyAlchemyAuditPath
$outputFullPath = [System.IO.Path]::GetFullPath($OutputPath)
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputFullPath) | Out-Null

$auditLines = Get-Content -LiteralPath $audit -Encoding UTF8
$refs = New-Object System.Collections.Generic.List[object]

foreach ($line in $auditLines) {
    if ($line -match '^\| HEDGE_ALCHEMY \| (?<ref>thaumcraft:[^| ]+) \| (?<hits>\d+) \| (?<path>[^|]+) \|') {
        $ref = $matches['ref'].Trim()
        if (-not ($refs | Where-Object { $_.Reference -eq $ref })) {
            $refs.Add([pscustomobject]@{
                Reference = $ref
                Key = ($ref -replace '^thaumcraft:', '')
                ResearchPath = $matches['path'].Trim()
            })
        }
    }
}

$javaFiles = @(Get-ChildItem -LiteralPath $legacy -Recurse -File -Filter '*.java')
$blocks = New-Object System.Collections.Generic.List[object]

foreach ($ref in $refs) {
    $found = $false
    $terms = @(
        "new ResourceLocation(`"$($ref.Reference)`")",
        $ref.Reference,
        $ref.Key
    )

    foreach ($file in $javaFiles) {
        $lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8 -ErrorAction SilentlyContinue
        for ($i = 0; $i -lt $lines.Count; $i++) {
            $line = $lines[$i]
            $matched = $false
            foreach ($term in $terms) {
                if ($line.IndexOf($term, [System.StringComparison]::OrdinalIgnoreCase) -ge 0) {
                    $matched = $true
                    break
                }
            }
            if (-not $matched) {
                continue
            }

            $block = Get-BalancedBlock -Lines $lines -HitIndex $i
            if ($block.Text -notmatch 'CrucibleRecipe|addCrucibleRecipe') {
                continue
            }

            $blocks.Add([pscustomobject]@{
                Reference = $ref.Reference
                Key = $ref.Key
                File = Get-RelativePathSafe -Base $legacy -Path $file.FullName
                StartLine = $block.StartLine
                Research = Get-ResearchFromBlock -Block $block.Text
                ResourceId = Get-ResourceIdFromBlock -Block $block.Text
                Aspects = Get-AspectsFromBlock -Block $block.Text
                Block = $block.Text
            })
            $found = $true
        }
    }

    if (-not $found) {
        $blocks.Add([pscustomobject]@{
            Reference = $ref.Reference
            Key = $ref.Key
            File = ''
            StartLine = 0
            Research = ''
            ResourceId = ''
            Aspects = ''
            Block = ''
        })
    }
}

$resolvedBlocks = @($blocks | Where-Object { -not [string]::IsNullOrWhiteSpace($_.Block) })
$unresolvedRefs = @($blocks | Where-Object { [string]::IsNullOrWhiteSpace($_.Block) })
$researchGroups = @($resolvedBlocks | Group-Object Research | Sort-Object -Property @{ Expression = 'Count'; Descending = $true }, Name)

$linesOut = New-Object System.Collections.Generic.List[string]
$linesOut.Add('# Hedge Alchemy Legacy Recipe Blocks')
$linesOut.Add('')
$linesOut.Add("Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')")
$linesOut.Add('')
$linesOut.Add('## Purpose')
$linesOut.Add('')
$linesOut.Add('This document extracts the exact legacy crucible recipe source blocks for the HEDGE_ALCHEMY family before any NeoForge data serializer or Thaumonomicon page implementation is written.')
$linesOut.Add('')
$linesOut.Add('## Summary')
$linesOut.Add('')
$linesOut.Add('| Metric | Count |')
$linesOut.Add('|---|---:|')
$linesOut.Add("| Unique hedge alchemy page references | $($refs.Count) |")
$linesOut.Add("| Extracted legacy crucible blocks | $($resolvedBlocks.Count) |")
$linesOut.Add("| References without extracted crucible block | $($unresolvedRefs.Count) |")
$linesOut.Add('')
$linesOut.Add('## Research key distribution')
$linesOut.Add('')
if ($researchGroups.Count -eq 0) {
    $linesOut.Add('No research keys extracted.')
} else {
    $linesOut.Add('| Research key | Count |')
    $linesOut.Add('|---|---:|')
    foreach ($group in $researchGroups) {
        $name = if ([string]::IsNullOrWhiteSpace($group.Name)) { '(missing)' } else { $group.Name }
        $linesOut.Add("| $(Escape-Md $name) | $($group.Count) |")
    }
}
$linesOut.Add('')
$linesOut.Add('## Extracted recipe overview')
$linesOut.Add('')
if ($blocks.Count -eq 0) {
    $linesOut.Add('No hedge alchemy references were found in the legacy alchemy recipe source audit.')
} else {
    $linesOut.Add('| Reference | Resource id | Research | Aspects | File | Line |')
    $linesOut.Add('|---|---|---|---|---|---:|')
    foreach ($block in ($blocks | Sort-Object Reference, File, StartLine)) {
        $linesOut.Add("| $(Escape-Md $block.Reference) | $(Escape-Md $block.ResourceId) | $(Escape-Md $block.Research) | $(Escape-Md $block.Aspects) | $(Escape-Md $block.File) | $($block.StartLine) |")
    }
}
$linesOut.Add('')
$linesOut.Add('## Extracted legacy source blocks')
$linesOut.Add('')
foreach ($block in ($resolvedBlocks | Sort-Object Reference, File, StartLine)) {
    $linesOut.Add("### $($block.Reference)")
    $linesOut.Add('')
    $linesOut.Add("- File: $(Escape-Md $block.File)")
    $linesOut.Add("- Start line: $($block.StartLine)")
    $linesOut.Add("- Research: $(Escape-Md $block.Research)")
    $linesOut.Add("- Aspects: $(Escape-Md $block.Aspects)")
    $linesOut.Add('')
    $linesOut.Add('```java')
    $linesOut.Add($block.Block)
    $linesOut.Add('```')
    $linesOut.Add('')
}
if ($unresolvedRefs.Count -gt 0) {
    $linesOut.Add('## Unresolved hedge references')
    $linesOut.Add('')
    $linesOut.Add('| Reference |')
    $linesOut.Add('|---|')
    foreach ($ref in $unresolvedRefs) {
        $linesOut.Add("| $(Escape-Md $ref.Reference) |")
    }
    $linesOut.Add('')
}
$linesOut.Add('## Next implementation guidance')
$linesOut.Add('')
$linesOut.Add('1. Use this extraction as the source of truth for the first crucible recipe data model and page snapshot batch.')
$linesOut.Add('2. Implement only the recipe serializer, datapack loading, audit export, and Thaumonomicon page snapshot for this family first.')
$linesOut.Add('3. Do not implement crucible block behavior, item transformation, essentia systems, particles, or in-world crafting in the same batch.')
$linesOut.Add('4. Preserve the legacy ResourceLocation ids from the extracted blocks so existing research page references resolve.')
$linesOut.Add('')

[System.IO.File]::WriteAllText($outputFullPath, ($linesOut -join "`n"), [System.Text.UTF8Encoding]::new($false))

Write-Host "Hedge alchemy legacy recipe blocks written to $outputFullPath"
Write-Host "Unique hedge refs: $($refs.Count)"
Write-Host "Extracted blocks: $($resolvedBlocks.Count)"
Write-Host "Unresolved refs: $($unresolvedRefs.Count)"
