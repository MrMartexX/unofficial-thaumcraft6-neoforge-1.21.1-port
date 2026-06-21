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
    if ($relative -match '^(?<namespace>[^/]+)/(recipe|recipes)/(?<recipePath>.+)\.json$') { return ($matches['namespace'] + ':' + $matches['recipePath']) }
    return $null
}

$repo = Resolve-Path -LiteralPath $RepoRoot
$moduleRoot = Join-Path $repo '05_neoforge_port'
$dataRoot = Join-Path $moduleRoot 'src/main/resources/data'
if ([string]::IsNullOrWhiteSpace($OutputPath)) { $OutputPath = Join-Path $repo '06_docs/audits/crucible_recipe_data_audit.md' }
$outputFullPath = [System.IO.Path]::GetFullPath($OutputPath)
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $outputFullPath) | Out-Null

$recipeFiles = @(Get-ChildItem -LiteralPath $dataRoot -Recurse -File -Filter '*.json' | Where-Object {
    ($_.FullName.Replace('\', '/')) -match '/(recipe|recipes)/'
})
$rows = New-Object System.Collections.Generic.List[object]
$issues = New-Object System.Collections.Generic.List[object]

foreach ($file in $recipeFiles) {
    $json = Read-JsonFile $file
    if ($null -eq $json -or -not ($json.PSObject.Properties.Name -contains 'type') -or [string]$json.type -ne 'thaumcraft:crucible') { continue }
    $id = Get-RecipeIdFromFile $dataRoot $file
    $relative = [System.IO.Path]::GetRelativePath($repo, $file.FullName).Replace('\', '/')
    $hasResearch = $json.PSObject.Properties.Name -contains 'research'
    $hasCatalyst = $json.PSObject.Properties.Name -contains 'catalyst'
    $hasAspects = ($json.PSObject.Properties.Name -contains 'aspects') -and $null -ne $json.aspects -and @($json.aspects).Count -gt 0
    $hasResult = $json.PSObject.Properties.Name -contains 'result'
    $resultId = ''
    if ($hasResult -and $json.result.PSObject.Properties.Name -contains 'id') { $resultId = [string]$json.result.id }
    $aspectCount = if ($hasAspects) { @($json.aspects).Count } else { 0 }
    $ok = $hasCatalyst -and $hasAspects -and $hasResult -and -not [string]::IsNullOrWhiteSpace($resultId)

    $rows.Add([pscustomobject]@{ Id = $id; Result = $resultId; Aspects = $aspectCount; HasResearch = $hasResearch; Ok = $ok; File = $relative })
    if (-not $ok) {
        $problems = @()
        if (-not $hasCatalyst) { $problems += 'missing catalyst' }
        if (-not $hasAspects) { $problems += 'missing aspects' }
        if (-not $hasResult) { $problems += 'missing result' }
        if ($hasResult -and [string]::IsNullOrWhiteSpace($resultId)) { $problems += 'blank result id' }
        $issues.Add([pscustomobject]@{ Id = $id; Problems = ($problems -join ', '); File = $relative })
    }
}

$lines = New-Object System.Collections.Generic.List[string]
$lines.Add('# Crucible Recipe Data Audit')
$lines.Add('')
$lines.Add("Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')")
$lines.Add('')
$lines.Add('## Summary')
$lines.Add('')
$lines.Add('| Metric | Count |')
$lines.Add('|---|---:|')
$lines.Add("| Crucible recipe JSON files | $($rows.Count) |")
$lines.Add("| Valid page-data crucible recipes | $(@($rows | Where-Object { $_.Ok }).Count) |")
$lines.Add("| Invalid crucible recipes | $($issues.Count) |")
$lines.Add('')
$lines.Add('## Invalid crucible recipes')
$lines.Add('')
if ($issues.Count -eq 0) { $lines.Add('No invalid crucible recipe JSON files were found.') }
else {
    $lines.Add('| Recipe id | Problems | File |')
    $lines.Add('|---|---|---|')
    foreach ($issue in $issues) { $lines.Add("| $(Escape-Md $issue.Id) | $(Escape-Md $issue.Problems) | $(Escape-Md $issue.File) |") }
}
$lines.Add('')
$lines.Add('## Crucible recipes')
$lines.Add('')
$lines.Add('| Recipe id | Result | Aspect entries | Has research | File |')
$lines.Add('|---|---|---:|---:|---|')
foreach ($row in ($rows | Sort-Object Id)) { $lines.Add("| $(Escape-Md $row.Id) | $(Escape-Md $row.Result) | $($row.Aspects) | $($row.HasResearch) | $(Escape-Md $row.File) |") }
$lines.Add('')
$lines.Add('## Boundary note')
$lines.Add('')
$lines.Add('- This audit validates crucible recipe data shape only.')
$lines.Add('- It does not validate in-world heat, mutation, spill pollution, block-event FX or rendering.')
$lines.Add('- Use `tools/audits/audit-crucible-behavior.ps1` for the separate server-runtime boundary; the current expected result is `16/16`.')

[System.IO.File]::WriteAllText($outputFullPath, ($lines -join "`n"), [System.Text.UTF8Encoding]::new($false))
Write-Host "Crucible recipe data audit written to $outputFullPath"
Write-Host "Crucible recipe files: $($rows.Count)"
Write-Host "Invalid crucible recipe files: $($issues.Count)"
if ($issues.Count -gt 0) { exit 1 }
