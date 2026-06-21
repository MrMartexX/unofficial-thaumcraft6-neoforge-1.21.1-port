param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$OutputPath = ''
)
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
function Rel([string]$Root, [string]$Path) {
    $r = (Resolve-Path $Root).Path.TrimEnd('\','/')
    $p = (Resolve-Path $Path).Path
    if ($p.StartsWith($r, [System.StringComparison]::OrdinalIgnoreCase)) { return $p.Substring($r.Length).TrimStart('\','/') -replace '\\','/' }
    return $p -replace '\\','/'
}
function Esc([string]$Text) {
    if ($null -eq $Text) { return '' }
    return ($Text -replace '\|','\|' -replace "`r",'' -replace "`n",' ')
}
$RepoRoot = (Resolve-Path $RepoRoot).Path
if ([string]::IsNullOrWhiteSpace($OutputPath)) { $OutputPath = Join-Path $RepoRoot '06_docs/audits/smelter_flux_aura_bridge_audit.md' }
$portRoot = Join-Path $RepoRoot '05_neoforge_port'
$files = @(Get-ChildItem -Path (Join-Path $portRoot 'src/main/java') -Recurse -File -Include *.java | Where-Object { $_.FullName -notmatch '[\\/](build|bin|out|target)[\\/]' })
$patterns = '(?i)(AuraHelper|aura|flux|pollut|addFlux|pollute|drain|chunk|SavedData|pendingFlux|Smelter|Vent)'
$rows = New-Object System.Collections.Generic.List[object]
foreach ($file in $files) {
    $rel = Rel $RepoRoot $file.FullName
    $lines = @(Get-Content -LiteralPath $file.FullName)
    $text = $lines -join "`n"
    if ($rel -notmatch '(?i)(aura|smelter|vent|flux)' -and $text -notmatch '(?i)(AuraHelper|pendingFlux|pollute|addFlux|flux)') { continue }
    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = [string]$lines[$i]
        if ($line -match $patterns) {
            $category = @()
            if ($line -match '(?i)class\s+\w*Aura|AuraHelper|package\s+thaumcraft\.(common|api)\.aura') { $category += 'aura_api_identity' }
            if ($line -match '(?i)flux|pollut|addFlux|pollute') { $category += 'flux_mutation' }
            if ($line -match '(?i)pendingFlux|TCSmelterBlockEntity|smelter') { $category += 'smelter_pending_flux' }
            if ($line -match '(?i)vent|SmelterVent') { $category += 'vent_dependency' }
            if ($line -match '(?i)SavedData|chunk|Level|ServerLevel') { $category += 'runtime_storage' }
            $rows.Add([pscustomobject]@{ File=$rel; Line=$i+1; Category=(($category | Select-Object -Unique) -join ', '); Text=$line.Trim() }) | Out-Null
        }
    }
}
$rows = @($rows | Sort-Object File, Line)
$auraRows = @($rows | Where-Object { $_.Category -match 'aura_api_identity|flux_mutation|runtime_storage' })
$smelterRows = @($rows | Where-Object { $_.Category -match 'smelter_pending_flux' })
$ventRows = @($rows | Where-Object { $_.Category -match 'vent_dependency' })
$md = New-Object System.Collections.Generic.List[string]
$md.Add('# Smelter Pending Flux Aura Bridge Audit') | Out-Null
$md.Add('') | Out-Null
$md.Add('Generated: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')) | Out-Null
$md.Add('') | Out-Null
$md.Add('## Summary') | Out-Null
$md.Add('') | Out-Null
$md.Add('| Metric | Count |') | Out-Null
$md.Add('|---|---:|') | Out-Null
$md.Add('| Aura/flux/runtime evidence rows | ' + $auraRows.Count + ' |') | Out-Null
$md.Add('| Smelter pending-flux rows | ' + $smelterRows.Count + ' |') | Out-Null
$md.Add('| Vent dependency rows | ' + $ventRows.Count + ' |') | Out-Null
$md.Add('| Total evidence rows | ' + $rows.Count + ' |') | Out-Null
$md.Add('') | Out-Null
$md.Add('## Interpretation') | Out-Null
$md.Add('') | Out-Null
$md.Add('- This audit prepares the boundary between `TCSmelterBlockEntity.pendingFlux` and the current aura/flux API.') | Out-Null
$md.Add('- The next code slice should use only an existing current-port aura mutation method proven below. If no direct flux/pollution API appears, add a small aura-side boundary first instead of guessing.') | Out-Null
$md.Add('- Smelter vent mitigation should remain separate from direct aura pollution because legacy vents intercept pending flux before pollution.') | Out-Null
function Table([System.Collections.Generic.List[string]]$md, [string]$title, $data, [int]$limit) {
    $md.Add('') | Out-Null
    $md.Add('## ' + $title) | Out-Null
    $md.Add('') | Out-Null
    $sel = @($data | Select-Object -First $limit)
    if ($sel.Count -eq 0) { $md.Add('No evidence rows found.') | Out-Null; return }
    $md.Add('| File | Line | Category | Evidence |') | Out-Null
    $md.Add('|---|---:|---|---|') | Out-Null
    foreach ($row in $sel) { $md.Add('| ' + (Esc $row.File) + ' | ' + $row.Line + ' | ' + (Esc $row.Category) + ' | `' + (Esc $row.Text) + '` |') | Out-Null }
}
Table $md 'Aura and flux API evidence, first 220 rows' $auraRows 220
Table $md 'Smelter pending flux evidence, first 120 rows' $smelterRows 120
Table $md 'Vent dependency evidence, first 120 rows' $ventRows 120
$md.Add('') | Out-Null
$md.Add('## Porting conclusion') | Out-Null
$md.Add('') | Out-Null
$md.Add('- If the aura table exposes a stable direct flux/pollution method, use it in a follow-up smelter pending-flux drain slice.') | Out-Null
$md.Add('- If the aura table does not expose such a method, add a minimal aura mutation boundary before wiring smelter pollution.') | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path $OutputPath) | Out-Null
Set-Content -LiteralPath $OutputPath -Value $md -Encoding UTF8
Write-Host ('[tc-port] Wrote ' + (Rel $RepoRoot $OutputPath))
