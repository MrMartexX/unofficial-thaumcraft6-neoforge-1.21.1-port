param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$OutputPath = '',
    [switch]$FailOnJsonError
)

$ErrorActionPreference = 'Stop'

function Escape-Md {
    param([string]$Value)
    if ($null -eq $Value) {
        return ''
    }
    return ($Value -replace '\|', '\|' -replace "`r?`n", ' ')
}

function Read-JsonFile {
    param([System.IO.FileInfo]$File)

    try {
        return Get-Content -LiteralPath $File.FullName -Raw -Encoding UTF8 | ConvertFrom-Json -Depth 100
    }
    catch {
        if ($FailOnJsonError) {
            throw
        }
        return $null
    }
}

function Get-RecipeIdFromFile {
    param(
        [string]$DataRoot,
        [System.IO.FileInfo]$File
    )

    $relative = [System.IO.Path]::GetRelativePath($DataRoot, $File.FullName).Replace('\', '/')
    if ($relative -match '^(?<namespace>[^/]+)/(recipe|recipes)/(?<recipePath>.+)\.json$') {
        return "$($matches['namespace']):$($matches['recipePath'])"
    }
    return $null
}

function Get-RecipeBoundaryClass {
    param([string]$Type)

    if ([string]::IsNullOrWhiteSpace($Type)) {
        return 'INVALID_OR_MISSING_TYPE'
    }

    if ($Type -match '^minecraft:') {
        return 'VANILLA_OR_STANDARD_READY'
    }

    if ($Type -match '^thaumcraft:.*arcane') {
        return 'ARCANE_READY'
    }

    if ($Type -eq 'thaumcraft:crucible') {
        return 'CRUCIBLE_PAGE_READY_NO_GAMEPLAY'
    }

    if ($Type -eq 'thaumcraft:infusion') {
        return 'INFUSION_PAGE_READY_NO_GAMEPLAY'
    }

    if ($Type -match '^thaumcraft:.*(infusion|blueprint|fake|special|compound|void|enchant)') {
        return 'CUSTOM_BLOCKED_REQUIRES_DESIGN'
    }

    if ($Type -match '^thaumcraft:') {
        return 'THAUMCRAFT_CUSTOM_REVIEW'
    }

    return 'UNKNOWN_NAMESPACE_REVIEW'
}

function Add-ResearchReference {
    param(
        [System.Collections.Generic.List[object]]$Refs,
        [string]$File,
        [string]$JsonPath,
        [string]$Value
    )

    if ([string]::IsNullOrWhiteSpace($Value)) {
        return
    }

    $normalized = $Value.Trim()
    if ($normalized -notmatch '^[a-z0-9_.-]+:[a-z0-9_./-]+$') {
        return
    }

    $pathLooksRelevant =
        $JsonPath -match '(?i)(recipe|recipes|required_craft|craft|page|pages)' -or
        $normalized -match '(?i)(crucible|infusion|blueprint|fake|thaumcraft:)'

    if (-not $pathLooksRelevant) {
        return
    }

    $Refs.Add([pscustomobject]@{
        File = $File
        Path = $JsonPath
        Value = $normalized
    })
}

function Walk-Json {
    param(
        [object]$Node,
        [string]$Path,
        [string]$File,
        [System.Collections.Generic.List[object]]$Refs
    )

    if ($null -eq $Node) {
        return
    }

    if ($Node -is [string]) {
        Add-ResearchReference -Refs $Refs -File $File -JsonPath $Path -Value $Node
        return
    }

    if ($Node -is [System.Management.Automation.PSCustomObject]) {
        foreach ($prop in $Node.PSObject.Properties) {
            Walk-Json -Node $prop.Value -Path "$Path.$($prop.Name)" -File $File -Refs $Refs
        }
        return
    }

    if ($Node -is [System.Collections.IDictionary]) {
        foreach ($key in $Node.Keys) {
            Walk-Json -Node $Node[$key] -Path "$Path.$key" -File $File -Refs $Refs
        }
        return
    }

    if ($Node -is [System.Collections.IEnumerable] -and -not ($Node -is [string])) {
        $index = 0
        foreach ($item in $Node) {
            Walk-Json -Node $item -Path "$Path[$index]" -File $File -Refs $Refs
            $index++
        }
        return
    }
}

$repo = Resolve-Path -LiteralPath $RepoRoot
$moduleRoot = Join-Path $repo '05_neoforge_port'
$dataRoot = Join-Path $moduleRoot 'src/main/resources/data'

if (-not (Test-Path -LiteralPath $dataRoot)) {
    throw "Missing data root: $dataRoot"
}

if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $repo '06_docs/audits/custom_recipe_boundary_audit.md'
}

$outputFullPath = [System.IO.Path]::GetFullPath($OutputPath)
$outputDir = Split-Path -Parent $outputFullPath
New-Item -ItemType Directory -Force -Path $outputDir | Out-Null

$recipeFiles = @(Get-ChildItem -LiteralPath $dataRoot -Recurse -File -Filter '*.json' |
    Where-Object { $_.FullName -match '[\\/](recipe|recipes)[\\/]' })

$recipes = New-Object System.Collections.Generic.List[object]
$jsonErrors = New-Object System.Collections.Generic.List[object]

foreach ($file in $recipeFiles) {
    $id = Get-RecipeIdFromFile -DataRoot $dataRoot -File $file
    if (-not $id) {
        continue
    }

    $json = Read-JsonFile -File $file
    if ($null -eq $json) {
        $jsonErrors.Add([pscustomobject]@{
            File = [System.IO.Path]::GetRelativePath($repo, $file.FullName).Replace('\', '/')
            Error = 'JSON parse failed'
        })
        continue
    }

    $type = ''
    if ($json.PSObject.Properties.Name -contains 'type') {
        $type = [string]$json.type
    }

    $recipes.Add([pscustomobject]@{
        Id = $id
        Type = $type
        Class = Get-RecipeBoundaryClass -Type $type
        File = [System.IO.Path]::GetRelativePath($repo, $file.FullName).Replace('\', '/')
    })
}

$recipeById = @{}
foreach ($recipe in $recipes) {
    $recipeById[$recipe.Id] = $recipe
}

$researchFiles = @(Get-ChildItem -LiteralPath (Join-Path $dataRoot 'thaumcraft/research') -Recurse -File -Filter '*.json' -ErrorAction SilentlyContinue)
$refs = New-Object System.Collections.Generic.List[object]

foreach ($file in $researchFiles) {
    $json = Read-JsonFile -File $file
    $relative = [System.IO.Path]::GetRelativePath($repo, $file.FullName).Replace('\', '/')
    if ($null -eq $json) {
        $jsonErrors.Add([pscustomobject]@{
            File = $relative
            Error = 'Research JSON parse failed'
        })
        continue
    }
    Walk-Json -Node $json -Path '$' -File $relative -Refs $refs
}

$resolvedRefs = New-Object System.Collections.Generic.List[object]
$missingRefs = New-Object System.Collections.Generic.List[object]
foreach ($ref in $refs) {
    if ($recipeById.ContainsKey($ref.Value)) {
        $recipe = $recipeById[$ref.Value]
        $resolvedRefs.Add([pscustomobject]@{
            Reference = $ref.Value
            RecipeType = $recipe.Type
            Class = $recipe.Class
            ResearchFile = $ref.File
            ResearchPath = $ref.Path
        })
    } else {
        $missingRefs.Add([pscustomobject]@{
            Reference = $ref.Value
            ResearchFile = $ref.File
            ResearchPath = $ref.Path
        })
    }
}

$customRecipeFiles = @($recipes | Where-Object {
    $_.Class -in @('CUSTOM_BLOCKED_REQUIRES_DESIGN', 'THAUMCRAFT_CUSTOM_REVIEW', 'UNKNOWN_NAMESPACE_REVIEW', 'INVALID_OR_MISSING_TYPE')
} | Sort-Object Class, Type, Id)

$blockedResearchRefs = @($resolvedRefs | Where-Object {
    $_.Class -in @('CUSTOM_BLOCKED_REQUIRES_DESIGN', 'THAUMCRAFT_CUSTOM_REVIEW', 'UNKNOWN_NAMESPACE_REVIEW')
} | Sort-Object Class, Reference, ResearchFile)

$keywordHits = New-Object System.Collections.Generic.List[object]
$keywords = @('crucible', 'infusion', 'blueprint', 'fake', 'compound', 'special', 'void jar', 'ShapedArcane', 'InfusionRecipe', 'CrucibleRecipe')
$resourceFiles = @(Get-ChildItem -LiteralPath $dataRoot -Recurse -File -Include '*.json')
foreach ($file in $resourceFiles) {
    $relative = [System.IO.Path]::GetRelativePath($repo, $file.FullName).Replace('\', '/')
    $content = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8
    foreach ($keyword in $keywords) {
        if ($content -match [regex]::Escape($keyword)) {
            $keywordHits.Add([pscustomobject]@{
                File = $relative
                Keyword = $keyword
            })
        }
    }
}

$typeDistribution = @($recipes | Group-Object Type | Sort-Object -Property @{ Expression = 'Count'; Descending = $true }, Name)
$classDistribution = @($recipes | Group-Object Class | Sort-Object -Property @{ Expression = 'Count'; Descending = $true }, Name)

$lines = New-Object System.Collections.Generic.List[string]
$lines.Add('# Custom Recipe Boundary Audit')
$lines.Add('')
$lines.Add("Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')")
$lines.Add('')
$lines.Add('## Summary')
$lines.Add('')
$lines.Add('| Metric | Count |')
$lines.Add('|---|---:|')
$lines.Add("| Recipe JSON files scanned | $($recipes.Count) |")
$lines.Add("| Research JSON files scanned | $($researchFiles.Count) |")
$lines.Add("| Research recipe-like references found | $($refs.Count) |")
$lines.Add("| Resolved recipe references | $($resolvedRefs.Count) |")
$lines.Add("| Missing recipe references | $($missingRefs.Count) |")
$lines.Add("| Custom/review recipe files | $($customRecipeFiles.Count) |")
$lines.Add("| Blocked/review research recipe references | $($blockedResearchRefs.Count) |")
$lines.Add("| JSON parse errors | $($jsonErrors.Count) |")
$lines.Add('')
$lines.Add('## Boundary rule')
$lines.Add('')
$lines.Add('- VANILLA_OR_STANDARD_READY means a normal Minecraft recipe type is already a data-only recipe.')
$lines.Add('- ARCANE_READY means the current Thaumcraft arcane recipe boundary is already implemented and audited.')
$lines.Add('- CRUCIBLE_PAGE_READY_NO_GAMEPLAY means the current Thaumcraft crucible recipe serializer/page snapshot boundary exists, but in-world crucible block behavior is still deferred.')
$lines.Add('- INFUSION_PAGE_READY_NO_GAMEPLAY means the current Thaumcraft infusion recipe serializer/page snapshot boundary exists, but in-world infusion altar behavior is still deferred.')
$lines.Add('- CUSTOM_BLOCKED_REQUIRES_DESIGN means fake, blueprint, special or similar custom behavior must not be copied directly from legacy classes. It needs a small serializer/page/behavior design slice first.')
$lines.Add('- THAUMCRAFT_CUSTOM_REVIEW means the recipe uses a Thaumcraft namespace type that is not recognized as the current arcane type and must be explicitly reviewed.')
$lines.Add('')
$lines.Add('## Recipe class distribution')
$lines.Add('')
$lines.Add('| Class | Count |')
$lines.Add('|---|---:|')
foreach ($group in $classDistribution) {
    $lines.Add("| $(Escape-Md $group.Name) | $($group.Count) |")
}
$lines.Add('')
$lines.Add('## Recipe type distribution')
$lines.Add('')
$lines.Add('| Type | Count |')
$lines.Add('|---|---:|')
foreach ($group in $typeDistribution) {
    $name = if ([string]::IsNullOrWhiteSpace($group.Name)) { '(missing)' } else { $group.Name }
    $lines.Add("| $(Escape-Md $name) | $($group.Count) |")
}
$lines.Add('')
$lines.Add('## Custom or review recipe files')
$lines.Add('')
if ($customRecipeFiles.Count -eq 0) {
    $lines.Add('No custom/review recipe JSON files were found in current data resources.')
} else {
    $lines.Add('| Class | Recipe id | Type | File |')
    $lines.Add('|---|---|---|---|')
    foreach ($recipe in $customRecipeFiles) {
        $lines.Add("| $(Escape-Md $recipe.Class) | $(Escape-Md $recipe.Id) | $(Escape-Md $recipe.Type) | $(Escape-Md $recipe.File) |")
    }
}
$lines.Add('')
$lines.Add('## Blocked or review research recipe references')
$lines.Add('')
if ($blockedResearchRefs.Count -eq 0) {
    $lines.Add('No research recipe references currently resolve to blocked/review custom recipe JSON files.')
} else {
    $lines.Add('| Class | Reference | Recipe type | Research file | JSON path |')
    $lines.Add('|---|---|---|---|---|')
    foreach ($ref in $blockedResearchRefs) {
        $lines.Add("| $(Escape-Md $ref.Class) | $(Escape-Md $ref.Reference) | $(Escape-Md $ref.RecipeType) | $(Escape-Md $ref.ResearchFile) | $(Escape-Md $ref.ResearchPath) |")
    }
}
$lines.Add('')
$lines.Add('## Missing recipe-like research references')
$lines.Add('')
if ($missingRefs.Count -eq 0) {
    $lines.Add('No missing recipe-like research references were found by this audit.')
} else {
    $lines.Add('| Reference | Research file | JSON path |')
    $lines.Add('|---|---|---|')
    foreach ($ref in ($missingRefs | Sort-Object Reference, ResearchFile, ResearchPath)) {
        $lines.Add("| $(Escape-Md $ref.Reference) | $(Escape-Md $ref.ResearchFile) | $(Escape-Md $ref.ResearchPath) |")
    }
}
$lines.Add('')
$lines.Add('## Custom recipe keyword hits in data resources')
$lines.Add('')
if ($keywordHits.Count -eq 0) {
    $lines.Add('No custom recipe keyword hits were found in data resources.')
} else {
    $lines.Add('| Keyword | File |')
    $lines.Add('|---|---|')
    foreach ($hit in ($keywordHits | Sort-Object Keyword, File)) {
        $lines.Add("| $(Escape-Md $hit.Keyword) | $(Escape-Md $hit.File) |")
    }
}
$lines.Add('')
$lines.Add('## Next implementation guidance')
$lines.Add('')
$lines.Add('1. Do not implement fake, blueprint, special or remaining custom recipe behavior by copying legacy recipe classes directly; crucible and infusion currently have serializer/page boundaries only.')
$lines.Add('2. Pick the most referenced blocked custom type from this audit as the next serializer/page snapshot slice.')
$lines.Add('3. Keep machine behavior, inventory behavior, essentia networks and rendering deferred until the serializer/page boundary has its own audit coverage.')
$lines.Add('4. Keep server smoke and build green after every expansion.')
$lines.Add('')

[System.IO.File]::WriteAllText($outputFullPath, ($lines -join "`n"), [System.Text.UTF8Encoding]::new($false))

Write-Host "Custom recipe boundary audit written to $outputFullPath"
Write-Host "Recipe files scanned: $($recipes.Count)"
Write-Host "Research files scanned: $($researchFiles.Count)"
Write-Host "Custom/review recipe files: $($customRecipeFiles.Count)"
Write-Host "Blocked/review research references: $($blockedResearchRefs.Count)"
Write-Host "Missing recipe-like research references: $($missingRefs.Count)"

if ($jsonErrors.Count -gt 0) {
    Write-Warning "JSON parse errors were found. Re-run with -FailOnJsonError for strict failure."
}
