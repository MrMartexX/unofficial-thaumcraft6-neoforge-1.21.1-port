param(
    [string]$ModuleRoot = '05_neoforge_port',
    [string]$Namespace = 'thaumcraft'
)

$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..\..')
$moduleRootPath = Join-Path $repoRoot $ModuleRoot
$assetsRoot = Join-Path $moduleRootPath 'src\main\resources\assets'
$namespaceRoot = Join-Path $assetsRoot $Namespace

$script:errors = New-Object System.Collections.Generic.List[string]
$script:warnings = New-Object System.Collections.Generic.List[string]
$script:checkedModels = 0
$script:checkedBlockstates = 0
$script:checkedTextureRefs = 0
$script:checkedModelRefs = 0
$script:checkedObjRefs = 0

function Add-AuditError {
    param([string]$Message)
    $script:errors.Add($Message) | Out-Null
}

function Add-AuditWarning {
    param([string]$Message)
    $script:warnings.Add($Message) | Out-Null
}

function Get-JsonFiles {
    param([string]$Path)
    if (-not (Test-Path -LiteralPath $Path)) {
        return @()
    }
    return @(Get-ChildItem -LiteralPath $Path -Recurse -File -Filter '*.json')
}

function Split-ResourceLocation {
    param([string]$Value)

    if ([string]::IsNullOrWhiteSpace($Value)) {
        return $null
    }

    if ($Value.StartsWith('#')) {
        return $null
    }

    $parts = $Value.Split(':', 2)
    if ($parts.Count -eq 2) {
        return [pscustomobject]@{ Namespace = $parts[0]; Path = $parts[1] }
    }

    return [pscustomobject]@{ Namespace = $Namespace; Path = $Value }
}

function Normalize-ResourcePath {
    param([string]$Path)

    $p = $Path -replace '/', [IO.Path]::DirectorySeparatorChar
    $p = $p.TrimStart([IO.Path]::DirectorySeparatorChar)
    return $p
}

function Resolve-ThaumcraftModelPath {
    param([string]$Resource)

    $loc = Split-ResourceLocation $Resource
    if ($null -eq $loc) { return $null }
    if ($loc.Namespace -ne $Namespace) { return $null }
    if ($loc.Path.StartsWith('builtin/')) { return $null }

    $relative = Normalize-ResourcePath $loc.Path
    return Join-Path (Join-Path $namespaceRoot 'models') ($relative + '.json')
}

function Resolve-ThaumcraftTexturePath {
    param([string]$Resource)

    $loc = Split-ResourceLocation $Resource
    if ($null -eq $loc) { return $null }
    if ($loc.Namespace -ne $Namespace) { return $null }

    $relative = Normalize-ResourcePath $loc.Path
    if ($relative.EndsWith('.png')) {
        return Join-Path (Join-Path $namespaceRoot 'textures') $relative
    }
    return Join-Path (Join-Path $namespaceRoot 'textures') ($relative + '.png')
}

function Resolve-ThaumcraftRawAssetPath {
    param([string]$Resource)

    $loc = Split-ResourceLocation $Resource
    if ($null -eq $loc) { return $null }
    if ($loc.Namespace -ne $Namespace) { return $null }

    return Join-Path $namespaceRoot (Normalize-ResourcePath $loc.Path)
}

function Test-ModelReference {
    param(
        [string]$Resource,
        [string]$SourceFile,
        [string]$Context
    )

    $target = Resolve-ThaumcraftModelPath $Resource
    if ($null -eq $target) { return }

    $script:checkedModelRefs++
    if (-not (Test-Path -LiteralPath $target -PathType Leaf)) {
        Add-AuditError "Missing model reference: $Resource from $SourceFile ($Context). Expected $target"
    }
}

function Test-TextureReference {
    param(
        [string]$Resource,
        [string]$SourceFile,
        [string]$Context
    )

    $target = Resolve-ThaumcraftTexturePath $Resource
    if ($null -eq $target) { return }

    $script:checkedTextureRefs++
    $loc = Split-ResourceLocation $Resource
    if ($loc.Path.StartsWith('items/') -or $loc.Path.StartsWith('blocks/')) {
        Add-AuditWarning "Legacy plural texture path still referenced: $Resource from $SourceFile ($Context). Prefer item/ or block/ when adapting resources."
    }

    if (-not (Test-Path -LiteralPath $target -PathType Leaf)) {
        Add-AuditError "Missing texture reference: $Resource from $SourceFile ($Context). Expected $target"
    }
}

function Test-RawAssetReference {
    param(
        [string]$Resource,
        [string]$SourceFile,
        [string]$Context
    )

    $target = Resolve-ThaumcraftRawAssetPath $Resource
    if ($null -eq $target) { return }

    $script:checkedObjRefs++
    if (-not (Test-Path -LiteralPath $target -PathType Leaf)) {
        Add-AuditError "Missing raw asset reference: $Resource from $SourceFile ($Context). Expected $target"
    }
}

function ConvertFrom-JsonFile {
    param([System.IO.FileInfo]$File)

    try {
        return Get-Content -LiteralPath $File.FullName -Raw | ConvertFrom-Json -Depth 100
    }
    catch {
        Add-AuditError "Invalid JSON: $($File.FullName): $($_.Exception.Message)"
        return $null
    }
}

function Visit-JsonNode {
    param(
        [object]$Node,
        [scriptblock]$Visitor,
        [string]$Path = '$'
    )

    if ($null -eq $Node) { return }

    if ($Node -is [System.Array]) {
        for ($i = 0; $i -lt $Node.Count; $i++) {
            Visit-JsonNode -Node $Node[$i] -Visitor $Visitor -Path "$Path[$i]"
        }
        return
    }

    if ($Node -is [System.Management.Automation.PSCustomObject]) {
        foreach ($property in $Node.PSObject.Properties) {
            & $Visitor $property.Name $property.Value "$Path.$($property.Name)"
            Visit-JsonNode -Node $property.Value -Visitor $Visitor -Path "$Path.$($property.Name)"
        }
    }
}

function Scan-ModelJson {
    param([System.IO.FileInfo]$File)

    $script:checkedModels++
    $json = ConvertFrom-JsonFile -File $File
    if ($null -eq $json) { return }

    if ($json.PSObject.Properties.Name -contains 'parent') {
        Test-ModelReference -Resource ([string]$json.parent) -SourceFile $File.FullName -Context 'parent'
    }

    if ($json.PSObject.Properties.Name -contains 'loader' -and [string]$json.loader -eq 'neoforge:obj') {
        if ($json.PSObject.Properties.Name -contains 'model') {
            Test-RawAssetReference -Resource ([string]$json.model) -SourceFile $File.FullName -Context 'neoforge:obj model'
        }
        if ($json.PSObject.Properties.Name -contains 'mtl_override') {
            Test-RawAssetReference -Resource ([string]$json.mtl_override) -SourceFile $File.FullName -Context 'neoforge:obj mtl_override'
        }
    }

    if ($json.PSObject.Properties.Name -contains 'textures') {
        Visit-JsonNode -Node $json.textures -Path '$.textures' -Visitor {
            param($Name, $Value, $Path)
            if ($Value -is [string]) {
                Test-TextureReference -Resource $Value -SourceFile $File.FullName -Context $Path
            }
        }
    }
}

function Scan-BlockstateJson {
    param([System.IO.FileInfo]$File)

    $script:checkedBlockstates++
    $json = ConvertFrom-JsonFile -File $File
    if ($null -eq $json) { return }

    Visit-JsonNode -Node $json -Visitor {
        param($Name, $Value, $Path)
        if ($Name -eq 'model' -and $Value -is [string]) {
            Test-ModelReference -Resource $Value -SourceFile $File.FullName -Context $Path
        }
    }
}

Write-Host "Static asset reference audit"
Write-Host "Repository root: $repoRoot"
Write-Host "Module root: $moduleRootPath"
Write-Host "Namespace root: $namespaceRoot"

if (-not (Test-Path -LiteralPath $namespaceRoot -PathType Container)) {
    throw "Namespace asset root not found: $namespaceRoot"
}

$modelRoot = Join-Path $namespaceRoot 'models'
$blockstateRoot = Join-Path $namespaceRoot 'blockstates'

foreach ($file in Get-JsonFiles -Path $modelRoot) {
    Scan-ModelJson -File $file
}

foreach ($file in Get-JsonFiles -Path $blockstateRoot) {
    Scan-BlockstateJson -File $file
}

Write-Host "Checked model JSON files: $script:checkedModels"
Write-Host "Checked blockstate JSON files: $script:checkedBlockstates"
Write-Host "Checked model references: $script:checkedModelRefs"
Write-Host "Checked texture references: $script:checkedTextureRefs"
Write-Host "Checked OBJ/MTL raw references: $script:checkedObjRefs"

if ($script:warnings.Count -gt 0) {
    Write-Host ''
    Write-Host "Warnings: $($script:warnings.Count)"
    foreach ($warning in $script:warnings) {
        Write-Host "WARNING: $warning"
    }
}

if ($script:errors.Count -gt 0) {
    Write-Host ''
    Write-Host "Errors: $($script:errors.Count)"
    foreach ($errorMessage in $script:errors) {
        Write-Host "ERROR: $errorMessage"
    }
    exit 1
}

Write-Host 'Static asset reference audit passed.'
