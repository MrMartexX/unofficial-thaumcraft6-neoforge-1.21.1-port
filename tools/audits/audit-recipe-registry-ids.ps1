param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path,
    [switch]$IncludeExternalTags,
    [switch]$FailOnMissing
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Get-JsonPropertyValue([object]$Object, [string]$Name) {
    if ($null -eq $Object) { return $null }
    $prop = $Object.PSObject.Properties[$Name]
    if ($null -eq $prop) { return $null }
    return $prop.Value
}

function Get-ArrayValue([object]$Value) {
    if ($null -eq $Value) { return @() }
    if ($Value -is [System.Array]) { return @($Value) }
    return @($Value)
}

function Add-JsonRefs([object]$Node, [string]$RecipeFile, [string]$JsonPath, [System.Collections.Generic.List[object]]$Refs) {
    if ($null -eq $Node) { return }

    if ($Node -is [System.Array]) {
        $index = 0
        foreach ($child in $Node) {
            Add-JsonRefs $child $RecipeFile "$JsonPath[$index]" $Refs
            $index++
        }
        return
    }

    if ($Node -is [pscustomobject]) {
        foreach ($prop in $Node.PSObject.Properties) {
            $name = $prop.Name
            $value = $prop.Value

            if (($name -eq 'item' -or $name -eq 'id' -or $name -eq 'tag') -and ($value -is [string])) {
                $kind = if ($name -eq 'tag') { 'tag' } elseif ($JsonPath -like '*.result*' -or $JsonPath -eq '$.result') { 'result' } else { 'item' }
                $Refs.Add([pscustomobject]@{
                    Recipe = $RecipeFile
                    Kind = $kind
                    JsonPath = "$JsonPath.$name"
                    Id = $value
                })
            }

            Add-JsonRefs $value $RecipeFile "$JsonPath.$name" $Refs
        }
    }
}

function Get-RegisteredThaumcraftItemIds([string]$ModuleRoot) {
    $ids = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)

    $tcItemsPath = Join-Path $ModuleRoot 'src/main/java/thaumcraft/common/registry/TCItems.java'
    $tcBlocksPath = Join-Path $ModuleRoot 'src/main/java/thaumcraft/common/registry/TCBlocks.java'

    foreach ($path in @($tcItemsPath, $tcBlocksPath)) {
        if (-not (Test-Path -LiteralPath $path)) {
            throw "Registry source not found: $path"
        }
    }

    $itemsText = Get-Content -LiteralPath $tcItemsPath -Raw
    $blocksText = Get-Content -LiteralPath $tcBlocksPath -Raw

    foreach ($match in [regex]::Matches($itemsText, 'ITEMS\.register\("([^"]+)"')) {
        [void]$ids.Add("thaumcraft:$($match.Groups[1].Value)")
    }

    foreach ($match in [regex]::Matches($itemsText, 'blockItem\("([^"]+)"')) {
        [void]$ids.Add("thaumcraft:$($match.Groups[1].Value)")
    }

    foreach ($match in [regex]::Matches($blocksText, 'BLOCKS\.register\("([^"]+)"')) {
        [void]$ids.Add("thaumcraft:$($match.Groups[1].Value)")
    }

    return $ids
}

function Test-LocalTagExists([string]$ModuleRoot, [string]$TagId) {
    $parts = $TagId.Split(':', 2)
    if ($parts.Length -ne 2) { return $false }

    $namespace = $parts[0]
    $path = $parts[1]

    $candidatePaths = @(
        (Join-Path $ModuleRoot "src/main/resources/data/$namespace/tags/item/$path.json"),
        (Join-Path $ModuleRoot "src/main/resources/data/$namespace/tags/items/$path.json"),
        (Join-Path $ModuleRoot "src/main/resources/data/$namespace/tags/block/$path.json"),
        (Join-Path $ModuleRoot "src/main/resources/data/$namespace/tags/blocks/$path.json")
    )

    foreach ($candidate in $candidatePaths) {
        if (Test-Path -LiteralPath $candidate) {
            return $true
        }
    }

    return $false
}

$module = Join-Path $RepoRoot '05_neoforge_port'
$recipeRoot = Join-Path $module 'src/main/resources/data/thaumcraft/recipe'

if (-not (Test-Path -LiteralPath $recipeRoot)) {
    throw "Recipe folder not found: $recipeRoot"
}

$registeredThaumcraftIds = Get-RegisteredThaumcraftItemIds $module
$refs = [System.Collections.Generic.List[object]]::new()

foreach ($file in (Get-ChildItem -LiteralPath $recipeRoot -Filter '*.json' | Sort-Object Name)) {
    $raw = Get-Content -LiteralPath $file.FullName -Raw

    try {
        $json = $raw | ConvertFrom-Json
    }
    catch {
        Write-Warning "Invalid JSON: $($file.Name)"
        continue
    }

    Add-JsonRefs $json $file.Name '$' $refs
}

$missingThaumcraft = @()
$externalTags = @()
$localTagMissing = @()

foreach ($ref in $refs) {
    $id = [string]$ref.Id

    if ($ref.Kind -eq 'tag') {
        if ($id.StartsWith('thaumcraft:')) {
            if (-not (Test-LocalTagExists $module $id)) {
                $localTagMissing += $ref
            }
        }
        elseif ($IncludeExternalTags -and -not ($id.StartsWith('minecraft:'))) {
            $externalTags += $ref
        }
        continue
    }

    if ($id.StartsWith('thaumcraft:') -and -not $registeredThaumcraftIds.Contains($id)) {
        $missingThaumcraft += $ref
    }
}

if ($missingThaumcraft.Count -eq 0) {
    Write-Host 'No missing thaumcraft:item/block registry IDs found in recipe item/result references.'
}
else {
    Write-Host "Missing thaumcraft:item/block registry IDs: $($missingThaumcraft.Count)"
    $missingThaumcraft |
        Sort-Object Id, Recipe, JsonPath |
        Format-Table Recipe, Kind, JsonPath, Id -AutoSize
}

if ($localTagMissing.Count -gt 0) {
    Write-Host ''
    Write-Host "Missing local thaumcraft tag files: $($localTagMissing.Count)"
    $localTagMissing |
        Sort-Object Id, Recipe, JsonPath |
        Format-Table Recipe, Kind, JsonPath, Id -AutoSize
}

if ($IncludeExternalTags -and $externalTags.Count -gt 0) {
    Write-Host ''
    Write-Host "External/non-local tags referenced. These may be valid if provided by NeoForge/common tags or another data source: $($externalTags.Count)"
    $externalTags |
        Sort-Object Id, Recipe, JsonPath |
        Format-Table Recipe, Kind, JsonPath, Id -AutoSize
}

if ($FailOnMissing -and ($missingThaumcraft.Count -gt 0 -or $localTagMissing.Count -gt 0)) {
    exit 1
}
