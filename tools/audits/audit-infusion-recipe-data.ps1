param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$OutputPath = ''
)

$ErrorActionPreference = 'Stop'

function Escape-Md([string]$Value) {
    if ($null -eq $Value) { return '' }
    return ($Value -replace '\|', '\|' -replace "`r?`n", ' ')
}

function Read-JsonFile([System.IO.FileInfo]$File) {
    try { return Get-Content -LiteralPath $File.FullName -Raw -Encoding UTF8 | ConvertFrom-Json -Depth 100 }
    catch { throw ('Failed to parse JSON ' + $File.FullName + ': ' + $_.Exception.Message) }
}

function Get-RecipeIdFromFile([string]$DataRoot, [System.IO.FileInfo]$File) {
    $relative = [System.IO.Path]::GetRelativePath($DataRoot, $File.FullName).Replace('\', '/')
    if ($relative -match '^(?<namespace>[^/]+)/(recipe|recipes)/(?<recipePath>.+)\.json$') {
        return ($matches['namespace'] + ':' + $matches['recipePath'])
    }
    return $null
}

function Has-NonBlankId([object]$Node) {
    if ($null -eq $Node) { return $false }
    $names = @($Node.PSObject.Properties.Name)
    if ($names -contains 'id' -and -not [string]::IsNullOrWhiteSpace([string]$Node.id)) { return $true }
    if ($names -contains 'item' -and -not [string]::IsNullOrWhiteSpace([string]$Node.item)) { return $true }
    if ($names -contains 'tag' -and -not [string]::IsNullOrWhiteSpace([string]$Node.tag)) { return $true }
    return $false
}

$repo = Resolve-Path -LiteralPath $RepoRoot
$moduleRoot = Join-Path $repo '05_neoforge_port'
$dataRoot = Join-Path $moduleRoot 'src/main/resources/data'
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $repo '06_docs/audits/infusion_recipe_data_audit.md'
}

$outputFullPath = [System.IO.Path]::GetFullPath($OutputPath)
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputFullPath) | Out-Null

$recipeFiles = @(Get-ChildItem -LiteralPath $dataRoot -Recurse -File -Filter '*.json' | Where-Object { $_.FullName -match '[\\/](recipe|recipes)[\\/]' })
$rows = New-Object System.Collections.Generic.List[object]
$issues = New-Object System.Collections.Generic.List[object]

foreach ($file in $recipeFiles) {
    $json = Read-JsonFile $file
    $names = @($json.PSObject.Properties.Name)
    if ($null -eq $json -or -not ($names -contains 'type') -or [string]$json.type -ne 'thaumcraft:infusion') { continue }

    $id = Get-RecipeIdFromFile $dataRoot $file
    $relative = [System.IO.Path]::GetRelativePath($repo, $file.FullName).Replace('\', '/')

    $hasResearch = ($names -contains 'research') -and -not [string]::IsNullOrWhiteSpace([string]$json.research)
    $hasInstability = $names -contains 'instability'
    $hasCatalyst = ($names -contains 'catalyst') -and (Has-NonBlankId $json.catalyst)
    $componentCount = if ($names -contains 'components' -and $null -ne $json.components) { @($json.components).Count } else { 0 }
    $aspectCount = if ($names -contains 'aspects' -and $null -ne $json.aspects) { @($json.aspects).Count } else { 0 }
    $hasResult = ($names -contains 'result') -and (Has-NonBlankId $json.result)

    $badComponents = 0
    if ($componentCount -gt 0) {
        foreach ($component in @($json.components)) { if (-not (Has-NonBlankId $component)) { $badComponents++ } }
    }

    $badAspects = 0
    if ($aspectCount -gt 0) {
        foreach ($aspect in @($json.aspects)) {
            $aspectNames = @($aspect.PSObject.Properties.Name)
            $aspectName = if ($aspectNames -contains 'aspect') { [string]$aspect.aspect } else { '' }
            $amount = if ($aspectNames -contains 'amount') { [int]$aspect.amount } else { 0 }
            if ([string]::IsNullOrWhiteSpace($aspectName) -or $amount -le 0) { $badAspects++ }
        }
    }

    $resultId = ''
    if ($hasResult) {
        $resultNames = @($json.result.PSObject.Properties.Name)
        if ($resultNames -contains 'id') { $resultId = [string]$json.result.id }
        elseif ($resultNames -contains 'item') { $resultId = [string]$json.result.item }
    }

    $ok = $hasResearch -and $hasInstability -and $hasCatalyst -and $componentCount -gt 0 -and $badComponents -eq 0 -and $aspectCount -gt 0 -and $badAspects -eq 0 -and $hasResult

    $rows.Add([pscustomobject]@{ Id = $id; Result = $resultId; Components = $componentCount; Aspects = $aspectCount; Instability = if ($hasInstability) { [string]$json.instability } else { '' }; HasResearch = $hasResearch; Ok = $ok; File = $relative })

    if (-not $ok) {
        $problems = @()
        if (-not $hasResearch) { $problems += 'missing research' }
        if (-not $hasInstability) { $problems += 'missing instability' }
        if (-not $hasCatalyst) { $problems += 'missing catalyst' }
        if ($componentCount -le 0) { $problems += 'missing components' }
        if ($badComponents -gt 0) { $problems += "blank component entries=$badComponents" }
        if ($aspectCount -le 0) { $problems += 'missing aspects' }
        if ($badAspects -gt 0) { $problems += "bad aspect entries=$badAspects" }
        if (-not $hasResult) { $problems += 'missing result id' }
        $issues.Add([pscustomobject]@{ Id = $id; Problems = ($problems -join ', '); File = $relative })
    }
}

$lines = New-Object System.Collections.Generic.List[string]
$lines.Add('# Infusion Recipe Data Audit')
$lines.Add('')
$lines.Add("Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')")
$lines.Add('')
$lines.Add('## Summary')
$lines.Add('')
$lines.Add('| Metric | Count |')
$lines.Add('|---|---:|')
$lines.Add("| Infusion recipe JSON files | $($rows.Count) |")
$lines.Add("| Valid page-data infusion recipes | $(@($rows | Where-Object { $_.Ok }).Count) |")
$lines.Add("| Invalid infusion recipes | $($issues.Count) |")
$lines.Add('')
$lines.Add('## Invalid infusion recipes')
$lines.Add('')
if ($issues.Count -eq 0) { $lines.Add('No invalid infusion recipe JSON files were found.') }
else {
    $lines.Add('| Recipe id | Problems | File |')
    $lines.Add('|---|---|---|')
    foreach ($issue in ($issues | Sort-Object Id)) { $lines.Add("| $(Escape-Md $issue.Id) | $(Escape-Md $issue.Problems) | $(Escape-Md $issue.File) |") }
}
$lines.Add('')
$lines.Add('## Infusion recipes')
$lines.Add('')
$lines.Add('| Recipe id | Result | Components | Aspects | Instability | Has research | File |')
$lines.Add('|---|---|---:|---:|---:|---:|---|')
foreach ($row in ($rows | Sort-Object Id)) { $lines.Add("| $(Escape-Md $row.Id) | $(Escape-Md $row.Result) | $($row.Components) | $($row.Aspects) | $(Escape-Md $row.Instability) | $($row.HasResearch) | $(Escape-Md $row.File) |") }
$lines.Add('')
$lines.Add('## Boundary note')
$lines.Add('')
$lines.Add('- This audit validates infusion recipe data shape only.')
$lines.Add('- It does not mean in-world infusion altar gameplay is implemented.')
$lines.Add('- Use it before activating any server-side infusion matrix behavior.')

[System.IO.File]::WriteAllText($outputFullPath, ($lines -join "`n"), [System.Text.UTF8Encoding]::new($false))
Write-Host "Infusion recipe data audit written to $outputFullPath"
Write-Host "Infusion recipe files: $($rows.Count)"
Write-Host "Invalid infusion recipe files: $($issues.Count)"
if ($issues.Count -gt 0) { exit 1 }