param(
    [string]$ModuleRoot = '05_neoforge_port',
    [string]$Namespace = 'thaumcraft'
)

$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..\..')
$moduleRootPath = Join-Path $repoRoot $ModuleRoot
$namespaceRoot = Join-Path $moduleRootPath "src\main\resources\assets\$Namespace"

$errors = New-Object System.Collections.Generic.List[string]
$warnings = New-Object System.Collections.Generic.List[string]
$checkedModels = 0
$checkedBlockstates = 0
$checkedModelRefs = 0
$checkedTextureRefs = 0
$checkedRawRefs = 0

function AddError([string]$Message) { $errors.Add($Message) | Out-Null }
function AddWarning([string]$Message) { $warnings.Add($Message) | Out-Null }

function ParseResourceLocation([string]$Value) {
    if ([string]::IsNullOrWhiteSpace($Value)) { return $null }
    if ($Value.StartsWith('#')) { return $null }

    $index = $Value.IndexOf(':')
    if ($index -lt 0) {
        return [pscustomobject]@{ Namespace = $null; Path = $Value }
    }

    return [pscustomobject]@{
        Namespace = $Value.Substring(0, $index)
        Path = $Value.Substring($index + 1)
    }
}

function NormalizePath([string]$Path) {
    return ($Path -replace '/', [IO.Path]::DirectorySeparatorChar).TrimStart([IO.Path]::DirectorySeparatorChar)
}

function IsExplicitThaumcraftReference([string]$Value) {
    $loc = ParseResourceLocation $Value
    return ($null -ne $loc -and $loc.Namespace -eq $Namespace)
}

function CheckThaumcraftModel([string]$Value, [string]$Source, [string]$Context) {
    if (-not (IsExplicitThaumcraftReference $Value)) { return }
    $loc = ParseResourceLocation $Value
    if ($loc.Path.StartsWith('builtin/')) { return }

    $script:checkedModelRefs++
    $target = Join-Path (Join-Path $namespaceRoot 'models') ((NormalizePath $loc.Path) + '.json')
    if (-not (Test-Path -LiteralPath $target -PathType Leaf)) {
        AddError "Missing model reference: $Value from $Source ($Context). Expected $target"
    }
}

function CheckThaumcraftTexture([string]$Value, [string]$Source, [string]$Context) {
    if (-not (IsExplicitThaumcraftReference $Value)) { return }
    $loc = ParseResourceLocation $Value

    $script:checkedTextureRefs++
    if ($loc.Path.StartsWith('items/') -or $loc.Path.StartsWith('blocks/')) {
        AddWarning "Legacy plural texture path: $Value from $Source ($Context). Prefer item/ or block/."
    }

    $relative = NormalizePath $loc.Path
    if (-not $relative.EndsWith('.png')) { $relative = $relative + '.png' }
    $target = Join-Path (Join-Path $namespaceRoot 'textures') $relative
    if (-not (Test-Path -LiteralPath $target -PathType Leaf)) {
        AddError "Missing texture reference: $Value from $Source ($Context). Expected $target"
    }
}

function CheckThaumcraftRawAsset([string]$Value, [string]$Source, [string]$Context) {
    if (-not (IsExplicitThaumcraftReference $Value)) { return }
    $loc = ParseResourceLocation $Value

    $script:checkedRawRefs++
    $target = Join-Path $namespaceRoot (NormalizePath $loc.Path)
    if (-not (Test-Path -LiteralPath $target -PathType Leaf)) {
        AddError "Missing raw asset reference: $Value from $Source ($Context). Expected $target"
    }
}

function ReadJsonFile([System.IO.FileInfo]$File) {
    try {
        return Get-Content -LiteralPath $File.FullName -Raw | ConvertFrom-Json -Depth 100
    }
    catch {
        AddError "Invalid JSON: $($File.FullName): $($_.Exception.Message)"
        return $null
    }
}

function VisitJson($Node, [string]$Path, [string]$Source) {
    if ($null -eq $Node) { return }

    if ($Node -is [System.Array]) {
        for ($i = 0; $i -lt $Node.Count; $i++) {
            VisitJson $Node[$i] "$Path[$i]" $Source
        }
        return
    }

    if ($Node -is [System.Management.Automation.PSCustomObject]) {
        foreach ($property in $Node.PSObject.Properties) {
            $name = $property.Name
            $value = $property.Value
            $childPath = "$Path.$name"

            if ($name -eq 'model' -and $value -is [string]) {
                CheckThaumcraftModel $value $Source $childPath
            }

            VisitJson $value $childPath $Source
        }
    }
}

function ScanModelJson([System.IO.FileInfo]$File) {
    $script:checkedModels++
    $json = ReadJsonFile $File
    if ($null -eq $json) { return }

    if ($json.PSObject.Properties.Name -contains 'parent') {
        CheckThaumcraftModel ([string]$json.parent) $File.FullName 'parent'
    }

    if (($json.PSObject.Properties.Name -contains 'loader') -and ([string]$json.loader -eq 'neoforge:obj')) {
        if ($json.PSObject.Properties.Name -contains 'model') {
            CheckThaumcraftRawAsset ([string]$json.model) $File.FullName 'neoforge:obj model'
        }
        if ($json.PSObject.Properties.Name -contains 'mtl_override') {
            CheckThaumcraftRawAsset ([string]$json.mtl_override) $File.FullName 'neoforge:obj mtl_override'
        }
    }

    if ($json.PSObject.Properties.Name -contains 'textures') {
        foreach ($property in $json.textures.PSObject.Properties) {
            if ($property.Value -is [string]) {
                CheckThaumcraftTexture ([string]$property.Value) $File.FullName "textures.$($property.Name)"
            }
        }
    }
}

function ScanBlockstateJson([System.IO.FileInfo]$File) {
    $script:checkedBlockstates++
    $json = ReadJsonFile $File
    if ($null -eq $json) { return }
    VisitJson $json '$' $File.FullName
}

Write-Host 'Static asset reference audit'
Write-Host "Repository root: $repoRoot"
Write-Host "Module root: $moduleRootPath"
Write-Host "Namespace root: $namespaceRoot"

if (-not (Test-Path -LiteralPath $namespaceRoot -PathType Container)) {
    throw "Namespace asset root not found: $namespaceRoot"
}

$modelRoot = Join-Path $namespaceRoot 'models'
$blockstateRoot = Join-Path $namespaceRoot 'blockstates'

if (Test-Path -LiteralPath $modelRoot) {
    foreach ($file in Get-ChildItem -LiteralPath $modelRoot -Recurse -File -Filter '*.json') {
        ScanModelJson $file
    }
}

if (Test-Path -LiteralPath $blockstateRoot) {
    foreach ($file in Get-ChildItem -LiteralPath $blockstateRoot -Recurse -File -Filter '*.json') {
        ScanBlockstateJson $file
    }
}

Write-Host "Checked model JSON files: $checkedModels"
Write-Host "Checked blockstate JSON files: $checkedBlockstates"
Write-Host "Checked model references: $checkedModelRefs"
Write-Host "Checked texture references: $checkedTextureRefs"
Write-Host "Checked OBJ/MTL raw references: $checkedRawRefs"

if ($warnings.Count -gt 0) {
    Write-Host ''
    Write-Host "Warnings: $($warnings.Count)"
    foreach ($warning in $warnings) { Write-Host "WARNING: $warning" }
}

if ($errors.Count -gt 0) {
    Write-Host ''
    Write-Host "Errors: $($errors.Count)"
    foreach ($errorMessage in $errors) { Write-Host "ERROR: $errorMessage" }
    throw "Static asset reference audit failed with $($errors.Count) error(s)."
}

Write-Host 'Static asset reference audit passed.'
