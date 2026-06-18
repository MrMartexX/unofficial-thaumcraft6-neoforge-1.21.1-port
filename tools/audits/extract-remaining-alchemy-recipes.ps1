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

function Get-RelativePathSafe {
    param([string]$Base, [string]$Path)
    try {
        return [System.IO.Path]::GetRelativePath($Base, $Path).Replace('\', '/')
    } catch {
        return $Path.Replace('\', '/')
    }
}

function Get-BalancedBlock {
    param([string[]]$Lines, [int]$HitIndex)

    $start = $HitIndex
    while ($start -gt 0 -and $Lines[$start] -notmatch 'addCrucibleRecipe|new CrucibleRecipe|addInfusionCraftingRecipe|new InfusionRecipe') {
        $start--
        if (($HitIndex - $start) -gt 25) {
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

        if (($i - $start) -gt 70) {
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
        $parts.Add($match.Groups[1].Value.ToLowerInvariant() + '=' + $match.Groups[2].Value)
    }
    return ($parts -join ', ')
}

function Get-ResearchFromBlock {
    param([string]$Block)

    foreach ($pattern in @(
        'new\s+CrucibleRecipe\s*\(\s*"([^"]+)"',
        'new\s+InfusionRecipe\s*\(\s*"([^"]+)"'
    )) {
        $match = [regex]::Match($Block, $pattern)
        if ($match.Success) {
            return $match.Groups[1].Value
        }
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

function Get-RecipeApiKind {
    param([string]$Block)

    if ($Block -match 'CrucibleRecipe|addCrucibleRecipe') {
        return 'CRUCIBLE'
    }
    if ($Block -match 'InfusionRecipe|addInfusionCraftingRecipe') {
        return 'INFUSION'
    }
    return 'UNKNOWN'
}

$repo = Resolve-Path -LiteralPath $RepoRoot
if ([string]::IsNullOrWhiteSpace($LegacySourceRoot)) {
    $LegacySourceRoot = Join-Path $repo '02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master'
}
if ([string]::IsNullOrWhiteSpace($LegacyAlchemyAuditPath)) {
    $LegacyAlchemyAuditPath = Join-Path $repo '06_docs/audits/legacy_alchemy_recipe_source_audit.md'
}
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $repo '06_docs/audits/remaining_alchemy_legacy_recipe_blocks.md'
}

$legacy = Resolve-Path -LiteralPath $LegacySourceRoot
$audit = Resolve-Path -LiteralPath $LegacyAlchemyAuditPath
$outputFullPath = [System.IO.Path]::GetFullPath($OutputPath)
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputFullPath) | Out-Null

$auditLines = Get-Content -LiteralPath $audit -Encoding UTF8
$refs = New-Object System.Collections.Generic.List[object]

foreach ($line in $auditLines) {
    if ($line -match '^\| (?<family>[A-Z_]+) \| (?<ref>thaumcraft:[^| ]+) \| (?<hits>\d+) \| (?<path>[^|]+) \|') {
        $family = $matches['family'].Trim()
        if ($family -eq 'HEDGE_ALCHEMY') {
            continue
        }
        $ref = $matches['ref'].Trim()
        if (-not ($refs | Where-Object { $_.Reference -eq $ref })) {
            $refs.Add([pscustomobject]@{
                Family = $family
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
            $matched = $false
            foreach ($term in $terms) {
                if ($lines[$i].IndexOf($term, [System.StringComparison]::OrdinalIgnoreCase) -ge 0) {
                    $matched = $true
                    break
                }
            }
            if (-not $matched) {
                continue
            }

            $block = Get-BalancedBlock -Lines $lines -HitIndex $i
            $apiKind = Get-RecipeApiKind -Block $block.Text
            if ($apiKind -eq 'UNKNOWN') {
                continue
            }

            $blocks.Add([pscustomobject]@{
                Family = $ref.Family
                Reference = $ref.Reference
                Key = $ref.Key
                ApiKind = $apiKind
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
            Family = $ref.Family
            Reference = $ref.Reference
            Key = $ref.Key
            ApiKind = 'UNRESOLVED'
            File = ''
            StartLine = 0
            Research = ''
            ResourceId = ''
            Aspects = ''
            Block = ''
        })
    }
}

$resolved = @($blocks | Where-Object { -not [string]::IsNullOrWhiteSpace($_.Block) })
$unresolved = @($blocks | Where-Object { [string]::IsNullOrWhiteSpace($_.Block) })
$familyGroups = @($refs | Group-Object Family | Sort-Object -Property @{ Expression = 'Count'; Descending = $true }, Name)
$apiGroups = @($resolved | Group-Object ApiKind | Sort-Object -Property @{ Expression = 'Count'; Descending = $true }, Name)

$linesOut = New-Object System.Collections.Generic.List[string]
$linesOut.Add('# Remaining Alchemy Legacy Recipe Blocks')
$linesOut.Add('')
$linesOut.Add("Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')")
$linesOut.Add('')
$linesOut.Add('## Purpose')
$linesOut.Add('')
$linesOut.Add('This document extracts exact legacy recipe source blocks for the remaining non-HEDGE alchemy recipe-page gaps after the first HEDGE_ALCHEMY crucible boundary batch.')
$linesOut.Add('')
$linesOut.Add('## Summary')
$linesOut.Add('')
$linesOut.Add('| Metric | Count |')
$linesOut.Add('|---|---:|')
$linesOut.Add("| Unique remaining alchemy references | $($refs.Count) |")
$linesOut.Add("| Extracted legacy blocks | $($resolved.Count) |")
$linesOut.Add("| References without extracted block | $($unresolved.Count) |")
$linesOut.Add('')
$linesOut.Add('## Remaining family distribution')
$linesOut.Add('')
$linesOut.Add('| Family | Count |')
$linesOut.Add('|---|---:|')
foreach ($group in $familyGroups) {
    $linesOut.Add("| $(Escape-Md $group.Name) | $($group.Count) |")
}
$linesOut.Add('')
$linesOut.Add('## Extracted API kind distribution')
$linesOut.Add('')
if ($apiGroups.Count -eq 0) {
    $linesOut.Add('No resolved legacy recipe API blocks found.')
} else {
    $linesOut.Add('| API kind | Count |')
    $linesOut.Add('|---|---:|')
    foreach ($group in $apiGroups) {
        $linesOut.Add("| $(Escape-Md $group.Name) | $($group.Count) |")
    }
}
$linesOut.Add('')
$linesOut.Add('## Extracted recipe overview')
$linesOut.Add('')
if ($blocks.Count -eq 0) {
    $linesOut.Add('No remaining alchemy references were found.')
} else {
    $linesOut.Add('| Family | Reference | API kind | Resource id | Research | Aspects | File | Line |')
    $linesOut.Add('|---|---|---|---|---|---|---|---:|')
    foreach ($block in ($blocks | Sort-Object Family, Reference, ApiKind, File, StartLine)) {
        $linesOut.Add("| $(Escape-Md $block.Family) | $(Escape-Md $block.Reference) | $(Escape-Md $block.ApiKind) | $(Escape-Md $block.ResourceId) | $(Escape-Md $block.Research) | $(Escape-Md $block.Aspects) | $(Escape-Md $block.File) | $($block.StartLine) |")
    }
}
$linesOut.Add('')
$linesOut.Add('## Extracted legacy source blocks')
$linesOut.Add('')
foreach ($block in ($resolved | Sort-Object Family, Reference, File, StartLine)) {
    $linesOut.Add("### $($block.Reference)")
    $linesOut.Add('')
    $linesOut.Add("- Family: $(Escape-Md $block.Family)")
    $linesOut.Add("- API kind: $(Escape-Md $block.ApiKind)")
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
if ($unresolved.Count -gt 0) {
    $linesOut.Add('## Unresolved remaining alchemy references')
    $linesOut.Add('')
    $linesOut.Add('| Family | Reference |')
    $linesOut.Add('|---|---|')
    foreach ($ref in $unresolved) {
        $linesOut.Add("| $(Escape-Md $ref.Family) | $(Escape-Md $ref.Reference) |")
    }
    $linesOut.Add('')
}
$linesOut.Add('## Next implementation guidance')
$linesOut.Add('')
$linesOut.Add('1. Prefer the largest resolved family whose API kind is CRUCIBLE and whose output/catalyst ids already exist in the modern registry.')
$linesOut.Add('2. Keep infusion, entity/block behavior, and machine behavior out of alchemy recipe-page batches unless the audit shows the family is not a crucible recipe.')
$linesOut.Add('3. Re-run research recipe page gap audit after each family-level batch and do not patch individual ids unless a family has mixed API kind or missing registry identities.')
$linesOut.Add('')

[System.IO.File]::WriteAllText($outputFullPath, ($linesOut -join "`n"), [System.Text.UTF8Encoding]::new($false))

Write-Host "Remaining alchemy legacy recipe blocks written to $outputFullPath"
Write-Host "Remaining refs: $($refs.Count)"
Write-Host "Extracted blocks: $($resolved.Count)"
Write-Host "Unresolved refs: $($unresolved.Count)"
