param(
    [string]$ModuleRoot = '05_neoforge_port',
    [string]$Namespace = 'thaumcraft',
    [switch]$FailOnErrors
)

$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..\..')
$moduleRootPath = Join-Path $repoRoot $ModuleRoot
$namespaceRoot = Join-Path $moduleRootPath "src\main\resources\assets\$Namespace"

$errors = New-Object System.Collections.Generic.List[string]
$warnings = New-Object System.Collections.Generic.List[string]
$modelsToScan = New-Object System.Collections.Generic.Queue[string]
$queuedModels = New-Object 'System.Collections.Generic.HashSet[string]'
$scannedModels = New-Object 'System.Collections.Generic.HashSet[string]'
$checkedItemModelEntrypoints = 0
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

function GetThaumcraftModelPath([string]$Value) {
    if (-not (IsExplicitThaumcraftReference $Value)) { return $null }
    $loc = ParseResourceLocation $Value
    if ($loc.Path.StartsWith('builtin/')) { return $null }
    return Join-Path (Join-Path $namespaceRoot 'models') ((NormalizePath $loc.Path) + '.json')
}

function QueueModelReference([string]$Value, [string]$Source, [string]$Context) {
    $target = GetThaumcraftModelPath $Value
    if ($null -eq $target) { return }

    $script:checkedModelRefs++
    if (-not (Test-Path -LiteralPath $target -PathType Leaf)) {
        AddError "Missing model reference: $Value from $Source ($Context). Expected $target"
        return
    }

    if ($queuedModels.Add($target)) {
        $modelsToScan.Enqueue($target)
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

function ReadJsonFile([string]$Path) {
    try {
        return Get-Content -LiteralPath $Path -Raw | ConvertFrom-Json -Depth 100
    }
    catch {
        AddError "Invalid JSON: ${Path}: $($_.Exception.Message)"
        return $null
    }
}

function VisitBlockstateJson($Node, [string]$Path, [string]$Source) {
    if ($null -eq $Node) { return }

    if ($Node -is [System.Array]) {
        for ($i = 0; $i -lt $Node.Count; $i++) {
            VisitBlockstateJson $Node[$i] "$Path[$i]" $Source
        }
        return
    }

    if ($Node -is [System.Management.Automation.PSCustomObject]) {
        foreach ($property in $Node.PSObject.Properties) {
            $name = $property.Name
            $value = $property.Value
            $childPath = "$Path.$name"

            if ($name -eq 'model' -and $value -is [string]) {
                QueueModelReference $value $Source $childPath
            }

            VisitBlockstateJson $value $childPath $Source
        }
    }
}

function ScanModelJson([string]$Path) {
    if (-not $scannedModels.Add($Path)) { return }

    $json = ReadJsonFile $Path
    if ($null -eq $json) { return }

    if ($json.PSObject.Properties.Name -contains 'parent') {
        QueueModelReference ([string]$json.parent) $Path 'parent'
    }

    if (($json.PSObject.Properties.Name -contains 'loader') -and ([string]$json.loader -eq 'neoforge:obj')) {
        if ($json.PSObject.Properties.Name -contains 'model') {
            CheckThaumcraftRawAsset ([string]$json.model) $Path 'neoforge:obj model'
        }
        if ($json.PSObject.Properties.Name -contains 'mtl_override') {
            CheckThaumcraftRawAsset ([string]$json.mtl_override) $Path 'neoforge:obj mtl_override'
        }
    }

    if ($json.PSObject.Properties.Name -contains 'textures') {
        foreach ($property in $json.textures.PSObject.Properties) {
            if ($property.Value -is [string]) {
                CheckThaumcraftTexture ([string]$property.Value) $Path "textures.$($property.Name)"
            }
        }
    }
}

function ScanBlockstateJson([string]$Path) {
    $script:checkedBlockstates++
    $json = ReadJsonFile $Path
    if ($null -eq $json) { return }
    VisitBlockstateJson $json '$' $Path
}

Write-Host 'Static asset reference audit'
Write-Host "Repository root: $repoRoot"
Write-Host "Module root: $moduleRootPath"
Write-Host "Namespace root: $namespaceRoot"
Write-Host "Mode: report-only by default. Use -FailOnErrors to make findings fail the step."

if (-not (Test-Path -LiteralPath $namespaceRoot -PathType Container)) {
    throw "Namespace asset root not found: $namespaceRoot"
}

$modelItemRoot = Join-Path (Join-Path $namespaceRoot 'models') 'item'
$blockstateRoot = Join-Path $namespaceRoot 'blockstates'

if (Test-Path -LiteralPath $modelItemRoot) {
    foreach ($file in Get-ChildItem -LiteralPath $modelItemRoot -Recurse -File -Filter '*.json') {
        $checkedItemModelEntrypoints++
        if ($queuedModels.Add($file.FullName)) {
            $modelsToScan.Enqueue($file.FullName)
        }
    }
}

if (Test-Path -LiteralPath $blockstateRoot) {
    foreach ($file in Get-ChildItem -LiteralPath $blockstateRoot -Recurse -File -Filter '*.json') {
        ScanBlockstateJson $file.FullName
    }
}

while ($modelsToScan.Count -gt 0) {
    ScanModelJson ($modelsToScan.Dequeue())
}

Write-Host "Checked item model entrypoints: $checkedItemModelEntrypoints"
Write-Host "Checked blockstate JSON files: $checkedBlockstates"
Write-Host "Checked reachable model JSON files: $($scannedModels.Count)"
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
    Write-Host "Findings: $($errors.Count)"
    foreach ($errorMessage in $errors) { Write-Host "FINDING: $errorMessage" }

    if ($FailOnErrors) {
        throw "Static asset reference audit failed with $($errors.Count) finding(s)."
    }

    Write-Host 'Static asset reference audit completed with findings, but report-only mode does not fail CI.'
    exit 0
}

Write-Host 'Static asset reference audit passed.'
