param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path,
    [switch]$IncludeKnownExact
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Get-JsonPropertyValue([object]$Object, [string]$Name) {
    if ($null -eq $Object) {
        return $null
    }

    $prop = $Object.PSObject.Properties[$Name]
    if ($null -eq $prop) {
        return $null
    }

    return $prop.Value
}

function Get-ArrayValue([object]$Value) {
    if ($null -eq $Value) {
        return @()
    }

    if ($Value -is [System.Array]) {
        return @($Value)
    }

    return @($Value)
}

$recipeRoot = Join-Path $RepoRoot '05_neoforge_port/src/main/resources/data/thaumcraft/recipe'
if (-not (Test-Path -LiteralPath $recipeRoot)) {
    throw "Recipe folder not found: $recipeRoot"
}

$knownExactLegacy = @(
    'caster_basic.json'
)

$placeholderPattern = @('III', 'LRL', 'LTL')
$placeholderAspects = @('aer', 'terra', 'aqua', 'ignis', 'ordo', 'perditio')

$matches = @()
$known = @()

foreach ($file in (Get-ChildItem -LiteralPath $recipeRoot -Filter '*.json' | Sort-Object Name)) {
    $raw = Get-Content -LiteralPath $file.FullName -Raw

    try {
        $json = $raw | ConvertFrom-Json
    }
    catch {
        Write-Warning "Invalid JSON: $($file.Name)"
        continue
    }

    $type = Get-JsonPropertyValue $json 'type'
    $research = Get-JsonPropertyValue $json 'research'
    $vis = Get-JsonPropertyValue $json 'vis'
    $result = Get-JsonPropertyValue $json 'result'
    $resultId = Get-JsonPropertyValue $result 'id'

    [array]$pattern = @(Get-ArrayValue (Get-JsonPropertyValue $json 'pattern'))
    [array]$crystals = @(Get-ArrayValue (Get-JsonPropertyValue $json 'crystals'))

    $samePattern = $pattern.Length -eq 3
    if ($samePattern) {
        for ($i = 0; $i -lt 3; $i++) {
            if ([string]$pattern[$i] -ne $placeholderPattern[$i]) {
                $samePattern = $false
                break
            }
        }
    }

    $sameCrystals = $crystals.Length -eq 6
    if ($sameCrystals) {
        [array]$actual = @($crystals | ForEach-Object {
            $aspect = Get-JsonPropertyValue $_ 'aspect'
            $amount = Get-JsonPropertyValue $_ 'amount'
            "$aspect`:$amount"
        })
        [array]$expected = @($placeholderAspects | ForEach-Object { "$_`:1" })

        foreach ($e in $expected) {
            if ($actual -notcontains $e) {
                $sameCrystals = $false
                break
            }
        }
    }

    $key = Get-JsonPropertyValue $json 'key'
    [array]$keyNames = @()
    if ($null -ne $key) {
        $keyNames = @($key.PSObject.Properties.Name | Sort-Object)
    }
    $hasCasterKeys = ($keyNames -join ',') -eq 'I,L,R,T'

    $isExactCasterPattern = (
        $type -eq 'thaumcraft:arcane_shaped' -and
        $research -eq 'UNLOCKAUROMANCY@2' -and
        $vis -eq 100 -and
        $samePattern -and
        $sameCrystals -and
        $hasCasterKeys
    )

    if (-not $isExactCasterPattern) {
        continue
    }

    $row = [pscustomobject]@{
        File = $file.Name
        Result = $resultId
    }

    if ($knownExactLegacy -contains $file.Name) {
        $known += $row
        if ($IncludeKnownExact) {
            $matches += $row
        }
    }
    else {
        $matches += $row
    }
}

if ($matches.Length -eq 0) {
    Write-Host 'No unresolved caster-basic arcane recipe placeholders found.'
}
else {
    Write-Host "Found $($matches.Length) unresolved caster-basic arcane recipe placeholder(s):"
    $matches | Format-Table -AutoSize
}

if ($known.Length -gt 0 -and -not $IncludeKnownExact) {
    Write-Host ''
    Write-Host "Ignored known exact legacy recipe(s): $($known.Length)"
    $known | Format-Table -AutoSize
}
