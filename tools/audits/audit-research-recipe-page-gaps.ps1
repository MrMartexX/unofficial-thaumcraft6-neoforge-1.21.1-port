param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
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

function Read-JsonFile {
    param([System.IO.FileInfo]$File)

    try {
        return Get-Content -LiteralPath $File.FullName -Raw -Encoding UTF8 | ConvertFrom-Json -Depth 100
    }
    catch {
        throw "Failed to parse JSON '$($File.FullName)': $($_.Exception.Message)"
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

function Get-RecipeTypeClass {
    param([string]$Type)

    if ([string]::IsNullOrWhiteSpace($Type)) {
        return 'MISSING_TYPE'
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
    if ($Type -match '^thaumcraft:') {
        return 'THAUMCRAFT_CUSTOM_OR_REVIEW'
    }
    return 'UNKNOWN_TYPE'
}

function Get-ReferenceKind {
    param([string]$Path)

    if ($Path -match '\.(stages|addenda)\[\d+\]\.recipes\[\d+\]') {
        return 'RECIPE_PAGE'
    }
    if ($Path -match '\.stages\[\d+\]\.required_craft\[\d+\]') {
        return 'REQUIRED_CRAFT'
    }
    if ($Path -match '\.stages\[\d+\]\.required_item\[\d+\]') {
        return 'REQUIRED_ITEM'
    }
    if ($Path -match '\.icons\[\d+\]') {
        return 'ICON'
    }
    return 'OTHER'
}

function Get-MissingRecipePageClass {
    param(
        [string]$Reference,
        [string]$ResearchFile
    )

    $ref = $Reference

    if ($ref -match '(?i)fake|RunicArmorFake|IE[A-Z]+FAKE|salismundusfake') {
        return 'FAKE_OR_SYNTHETIC_PAGE'
    }

    if ($ResearchFile -match '/infusion\.json$') {
        if ($ref -match '(?i)infusion|matrix|runic|elemental|mask|cloud|charm|void|primordial|boots|girdle|ring|fake') {
            return 'INFUSION_PAGE_DEFERRED'
        }
        return 'INFUSION_RESEARCH_LEGACY_PAGE_KEY'
    }

    if ($ResearchFile -match '/alchemy\.json$') {
        if ($ref -match '(?i)hedge_|metal_purification|nitor|Bath|Bottle|Liquid|Sane|tallow|soap|salismundus|brass|alumentum') {
            return 'ALCHEMY_CRUCIBLE_OR_SPECIAL_PAGE'
        }
        return 'ALCHEMY_RESEARCH_LEGACY_PAGE_KEY'
    }

    if ($ResearchFile -match '/golemancy\.json$') {
        if ($ref -match '(?i)^thaumcraft:Seal|Golem|Mind|Module|Press|brain|jar') {
            return 'GOLEMANCY_PAGE_DEFERRED'
        }
        return 'GOLEMANCY_RESEARCH_LEGACY_PAGE_KEY'
    }

    if ($ResearchFile -match '/auromancy\.json$') {
        if ($ref -match '(?i)focus|caster|recharge|vis|gauntlet') {
            return 'AUROMANCY_FOCUS_OR_CASTER_PAGE_DEFERRED'
        }
        return 'AUROMANCY_RESEARCH_LEGACY_PAGE_KEY'
    }

    if ($ResearchFile -match '/artifice\.json$') {
        if ($ref -match '(?i)Mirror|Bore|Lamp|Grapple|Infernal|Pattern|Hand|Essentia') {
            return 'ARTIFICE_BEHAVIOR_PAGE_DEFERRED'
        }
        return 'ARTIFICE_RESEARCH_LEGACY_PAGE_KEY'
    }

    if ($ResearchFile -match '/eldritch\.json$') {
        return 'ELDRITCH_PAGE_DEFERRED'
    }

    return 'LEGACY_PAGE_KEY_OR_MISSING_RECIPE'
}

function Add-StringReference {
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
    if ($normalized -notmatch '^[A-Za-z0-9_.-]+:[A-Za-z0-9_./-]+$') {
        return
    }

    $kind = Get-ReferenceKind -Path $JsonPath
    if ($kind -eq 'OTHER') {
        return
    }

    $Refs.Add([pscustomobject]@{
        Kind = $kind
        File = $File
        Path = $JsonPath
        Reference = $normalized
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
        Add-StringReference -Refs $Refs -File $File -JsonPath $Path -Value $Node
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
    $OutputPath = Join-Path $repo '06_docs/audits/research_recipe_page_gap_audit.md'
}

$outputFullPath = [System.IO.Path]::GetFullPath($OutputPath)
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputFullPath) | Out-Null

$recipeFiles = @(Get-ChildItem -LiteralPath $dataRoot -Recurse -File -Filter '*.json' |
    Where-Object { $_.FullName -match '[\\/](recipe|recipes)[\\/]' })

$recipes = @{}
$recipeRows = New-Object System.Collections.Generic.List[object]

foreach ($file in $recipeFiles) {
    $id = Get-RecipeIdFromFile -DataRoot $dataRoot -File $file
    if (-not $id) {
        continue
    }

    $json = Read-JsonFile -File $file
    $type = ''
    if ($json.PSObject.Properties.Name -contains 'type') {
        $type = [string]$json.type
    }

    $row = [pscustomobject]@{
        Id = $id
        Type = $type
        Class = Get-RecipeTypeClass -Type $type
        File = [System.IO.Path]::GetRelativePath($repo, $file.FullName).Replace('\', '/')
    }
    $recipes[$id] = $row
    $recipeRows.Add($row)
}

$catalogEntries = @{}
$catalogRoot = Join-Path $dataRoot 'thaumcraft/research_page_catalog'
$catalogFiles = @(Get-ChildItem -LiteralPath $catalogRoot -Recurse -File -Filter '*.json' -ErrorAction SilentlyContinue)
foreach ($file in $catalogFiles) {
    $json = Read-JsonFile -File $file
    if ($null -eq $json -or -not ($json.PSObject.Properties.Name -contains 'entries')) {
        continue
    }
    foreach ($entry in $json.entries) {
        if ($null -eq $entry -or -not ($entry.PSObject.Properties.Name -contains 'id')) {
            continue
        }
        $targets = @()
        if ($entry.PSObject.Properties.Name -contains 'targets' -and $null -ne $entry.targets) {
            $targets = @($entry.targets)
        }
        $catalogEntries[[string]$entry.id] = [pscustomobject]@{
            Kind = [string]$entry.kind
            Targets = $targets
        }
    }
}

$researchFiles = @(Get-ChildItem -LiteralPath (Join-Path $dataRoot 'thaumcraft/research') -Recurse -File -Filter '*.json' -ErrorAction SilentlyContinue)
$refs = New-Object System.Collections.Generic.List[object]

foreach ($file in $researchFiles) {
    $relative = [System.IO.Path]::GetRelativePath($repo, $file.FullName).Replace('\', '/')
    $json = Read-JsonFile -File $file
    Walk-Json -Node $json -Path '$' -File $relative -Refs $refs
}

$classified = New-Object System.Collections.Generic.List[object]

foreach ($ref in $refs) {
    $recipe = $null
    $resolved = $false
    $recipeType = ''
    $recipeClass = ''

    if ($recipes.ContainsKey($ref.Reference)) {
        $recipe = $recipes[$ref.Reference]
        $resolved = $true
        $recipeType = $recipe.Type
        $recipeClass = $recipe.Class
    }

    $missingClass = ''
    if (-not $resolved -and $ref.Kind -eq 'RECIPE_PAGE' -and $catalogEntries.ContainsKey($ref.Reference)) {
        $catalogEntry = $catalogEntries[$ref.Reference]
        if ($catalogEntry.Kind -eq 'group' -and $catalogEntry.Targets.Count -gt 0) {
            $missingTargets = @($catalogEntry.Targets | Where-Object { -not $recipes.ContainsKey([string]$_) })
            if ($missingTargets.Count -eq 0) {
                $resolved = $true
                $recipeType = 'research_page_catalog:group'
                $recipeClass = 'CATALOG_GROUP_READY'
            } else {
                $missingClass = 'CATALOG_GROUP_INCOMPLETE'
            }
        } elseif (-not [string]::IsNullOrWhiteSpace($catalogEntry.Kind)) {
            $resolved = $true
            $recipeType = 'research_page_catalog:' + ([string]$catalogEntry.Kind).ToLowerInvariant()
            $recipeClass = 'CATALOG_' + ([string]$catalogEntry.Kind).ToUpperInvariant() + '_READY'
        }
    }
    if (-not $resolved -and $ref.Kind -eq 'RECIPE_PAGE') {
        if ([string]::IsNullOrWhiteSpace($missingClass)) {
            $missingClass = Get-MissingRecipePageClass -Reference $ref.Reference -ResearchFile $ref.File
        }
    }

    $classified.Add([pscustomobject]@{
        Kind = $ref.Kind
        Reference = $ref.Reference
        ResolvedRecipe = $resolved
        RecipeType = $recipeType
        RecipeClass = $recipeClass
        MissingRecipePageClass = $missingClass
        ResearchFile = $ref.File
        JsonPath = $ref.Path
    })
}

$recipePageRefs = @($classified | Where-Object { $_.Kind -eq 'RECIPE_PAGE' })
$requiredCraftRefs = @($classified | Where-Object { $_.Kind -eq 'REQUIRED_CRAFT' })
$iconRefs = @($classified | Where-Object { $_.Kind -eq 'ICON' })
$requiredItemRefs = @($classified | Where-Object { $_.Kind -eq 'REQUIRED_ITEM' })

$rawMissingRecipePageRefs = @($recipePageRefs | Where-Object { -not $_.ResolvedRecipe } | Sort-Object MissingRecipePageClass, ResearchFile, Reference)
$intentionalSyntheticPageRefs = @($rawMissingRecipePageRefs | Where-Object { $_.MissingRecipePageClass -eq 'FAKE_OR_SYNTHETIC_PAGE' } | Sort-Object ResearchFile, Reference, JsonPath)
$missingRecipePageRefs = @($rawMissingRecipePageRefs | Where-Object { $_.MissingRecipePageClass -ne 'FAKE_OR_SYNTHETIC_PAGE' } | Sort-Object MissingRecipePageClass, ResearchFile, Reference)
$resolvedRecipePageRefs = @($recipePageRefs | Where-Object { $_.ResolvedRecipe } | Sort-Object RecipeClass, Reference)
$missingPageGroups = @($missingRecipePageRefs | Group-Object MissingRecipePageClass | Sort-Object -Property @{ Expression = 'Count'; Descending = $true }, Name)
$fileGroups = @($missingRecipePageRefs | Group-Object ResearchFile | Sort-Object -Property @{ Expression = 'Count'; Descending = $true }, Name)

$lines = New-Object System.Collections.Generic.List[string]
$lines.Add('# Research Recipe Page Gap Audit')
$lines.Add('')
$lines.Add("Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')")
$lines.Add('')
$lines.Add('## Summary')
$lines.Add('')
$lines.Add('| Metric | Count |')
$lines.Add('|---|---:|')
$lines.Add("| Recipe JSON files scanned | $($recipeRows.Count) |")
$lines.Add("| Research JSON files scanned | $($researchFiles.Count) |")
$lines.Add("| Stage/addendum recipe page references | $($recipePageRefs.Count) |")
$lines.Add("| Resolved recipe page references | $($resolvedRecipePageRefs.Count) |")
$lines.Add("| Raw unresolved recipe page references | $($rawMissingRecipePageRefs.Count) |")
$lines.Add("| Missing recipe page references | $($missingRecipePageRefs.Count) |")
$lines.Add("| Intentional fake/synthetic page references | $($intentionalSyntheticPageRefs.Count) |")
$lines.Add("| Required craft references | $($requiredCraftRefs.Count) |")
$lines.Add("| Required item references | $($requiredItemRefs.Count) |")
$lines.Add("| Icon references | $($iconRefs.Count) |")
$lines.Add('')
$lines.Add('## Missing recipe page references by class')
$lines.Add('')
if ($missingPageGroups.Count -eq 0) {
    $lines.Add('No missing stage/addendum recipe page references were found.')
} else {
    $lines.Add('| Class | Count |')
    $lines.Add('|---|---:|')
    foreach ($group in $missingPageGroups) {
        $lines.Add("| $(Escape-Md $group.Name) | $($group.Count) |")
    }
}
$lines.Add('')
$lines.Add('## Missing recipe page references by research file')
$lines.Add('')
if ($fileGroups.Count -eq 0) {
    $lines.Add('No missing stage/addendum recipe page references were found.')
} else {
    $lines.Add('| Research file | Count |')
    $lines.Add('|---|---:|')
    foreach ($group in $fileGroups) {
        $lines.Add("| $(Escape-Md $group.Name) | $($group.Count) |")
    }
}
$lines.Add('')
$lines.Add('## Resolved recipe page references')
$lines.Add('')
if ($resolvedRecipePageRefs.Count -eq 0) {
    $lines.Add('No stage/addendum recipe page references currently resolve to recipe JSON.')
} else {
    $lines.Add('| Reference | Recipe type | Class | Research file | JSON path |')
    $lines.Add('|---|---|---|---|---|')
    foreach ($ref in $resolvedRecipePageRefs) {
        $lines.Add("| $(Escape-Md $ref.Reference) | $(Escape-Md $ref.RecipeType) | $(Escape-Md $ref.RecipeClass) | $(Escape-Md $ref.ResearchFile) | $(Escape-Md $ref.JsonPath) |")
    }
}
$lines.Add('')
$lines.Add('## Missing recipe page references')
$lines.Add('')
if ($missingRecipePageRefs.Count -eq 0) {
    $lines.Add('No missing stage/addendum recipe page references were found.')
} else {
    $lines.Add('| Class | Reference | Research file | JSON path |')
    $lines.Add('|---|---|---|---|')
    foreach ($ref in $missingRecipePageRefs) {
        $lines.Add("| $(Escape-Md $ref.MissingRecipePageClass) | $(Escape-Md $ref.Reference) | $(Escape-Md $ref.ResearchFile) | $(Escape-Md $ref.JsonPath) |")
    }
}
$lines.Add('')
$lines.Add('## Intentional fake/synthetic page references')
$lines.Add('')
if ($intentionalSyntheticPageRefs.Count -eq 0) {
    $lines.Add('No intentional fake/synthetic page references were found.')
} else {
    $lines.Add('| Reference | Research file | JSON path |')
    $lines.Add('|---|---|---|')
    foreach ($ref in $intentionalSyntheticPageRefs) {
        $lines.Add("| $(Escape-Md $ref.Reference) | $(Escape-Md $ref.ResearchFile) | $(Escape-Md $ref.JsonPath) |")
    }
}
$lines.Add('')
$lines.Add('## Required craft references')
$lines.Add('')
if ($requiredCraftRefs.Count -eq 0) {
    $lines.Add('No required_craft references found.')
} else {
    $lines.Add('| Reference | Resolves to recipe JSON | Recipe type | Research file | JSON path |')
    $lines.Add('|---|---:|---|---|---|')
    foreach ($ref in ($requiredCraftRefs | Sort-Object ResearchFile, Reference, JsonPath)) {
        $lines.Add("| $(Escape-Md $ref.Reference) | $($ref.ResolvedRecipe) | $(Escape-Md $ref.RecipeType) | $(Escape-Md $ref.ResearchFile) | $(Escape-Md $ref.JsonPath) |")
    }
}
$lines.Add('')
$lines.Add('## Next implementation guidance')
$lines.Add('')
$lines.Add('1. Use Missing recipe page references by class as the decision source for the next large implementation slice.')
$lines.Add('2. If ALCHEMY_CRUCIBLE_OR_SPECIAL_PAGE dominates, design a crucible/special alchemy recipe serializer and page snapshot before machine behavior.')
$lines.Add('3. FAKE_OR_SYNTHETIC_PAGE references are intentional non-recipe teaching/UI placeholders and are reported separately from actionable gaps.')
$lines.Add('4. Do not treat ICON, REQUIRED_ITEM, or REQUIRED_CRAFT references as missing recipe pages unless their own requirement audit says they are unresolved.')
$lines.Add('5. Keep build and dedicated server smoke green after every page/serializer expansion.')
$lines.Add('')

[System.IO.File]::WriteAllText($outputFullPath, ($lines -join "`n"), [System.Text.UTF8Encoding]::new($false))

Write-Host "Research recipe page gap audit written to $outputFullPath"
Write-Host "Recipe page refs: $($recipePageRefs.Count)"
Write-Host "Resolved recipe page refs: $($resolvedRecipePageRefs.Count)"
Write-Host "Missing recipe page refs: $($missingRecipePageRefs.Count)"
Write-Host "Required craft refs: $($requiredCraftRefs.Count)"
