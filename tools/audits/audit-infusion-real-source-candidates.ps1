param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$OutputPath = ''
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Get-RelativePath([string]$Root, [string]$Path) {
    $rootFull = (Resolve-Path $Root).Path.TrimEnd('\','/')
    $pathFull = (Resolve-Path $Path).Path
    if ($pathFull.StartsWith($rootFull, [System.StringComparison]::OrdinalIgnoreCase)) {
        return $pathFull.Substring($rootFull.Length).TrimStart('\','/') -replace '\\','/'
    }
    return $pathFull -replace '\\','/'
}

function Escape-Md([string]$Text) {
    if ($null -eq $Text) { return '' }
    return ($Text -replace '\|','\|' -replace "`r",'' -replace "`n",' ')
}

$RepoRoot = (Resolve-Path $RepoRoot).Path
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $RepoRoot '06_docs/audits/infusion_real_source_candidate_audit.md'
}

$javaRoot = Join-Path $RepoRoot '05_neoforge_port/src/main/java'
$resRoot = Join-Path $RepoRoot '05_neoforge_port/src/main/resources'
if (-not (Test-Path $javaRoot)) { throw "Java source root missing: $javaRoot" }
if (-not (Test-Path $resRoot)) { throw "Resource root missing: $resRoot" }

$allFiles = @()
$allFiles += Get-ChildItem -Path $javaRoot -Recurse -File -Include *.java | Where-Object { $_.FullName -notmatch '[\\/]build[\\/]' }
$allFiles += Get-ChildItem -Path $resRoot -Recurse -File -Include *.json,*.mcmeta,*.properties,*.toml | Where-Object { $_.FullName -notmatch '[\\/]build[\\/]' }

$termPattern = '(?i)(warded.?jar|jarbrain|\bjar\b|essentia|alembic|tube|aspect.?source|aspect.?container|IAspectContainer|AspectList|importer|exporter|phial|vis\b|aura|flux|source)'
$registryPattern = '(?i)(DeferredRegister|register\(|RegistryObject|BlockEntityType|BLOCK_ENTITY|BlockEntity|TCBlocks|TCBlockEntities|TCItems)'
$storagePattern = '(?i)(AspectList|IAspectContainer|availableAspects|drain\(|remove\(|add\(Aspect|visSize|getAmount|essentia|aspect)'
$blockEntityPattern = '(?i)(extends\s+.*BlockEntity|new\s+.*BlockEntity|BlockEntityType|BlockEntity\b|blockEntity)'
$placeholderPattern = '(?i)(bridge|placeholder|non-gameplay|deferred|fake|synthetic)'

$hits = New-Object System.Collections.Generic.List[object]
$registryHits = New-Object System.Collections.Generic.List[object]
$blockEntityHits = New-Object System.Collections.Generic.List[object]
$storageHits = New-Object System.Collections.Generic.List[object]
$placeholderHits = New-Object System.Collections.Generic.List[object]

foreach ($file in $allFiles) {
    $rel = Get-RelativePath $RepoRoot $file.FullName
    $lines = @(Get-Content -LiteralPath $file.FullName)
    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = [string]$lines[$i]
        if ($line -notmatch $termPattern) { continue }
        $entry = [pscustomobject]@{
            File = $rel
            Line = $i + 1
            Text = $line.Trim()
        }
        $hits.Add($entry) | Out-Null
        if ($line -match $registryPattern -or $rel -match '(?i)(TCBlocks|TCBlockEntities|TCItems|registry)') { $registryHits.Add($entry) | Out-Null }
        if ($line -match $blockEntityPattern -or $rel -match '(?i)(BlockEntity|tile)') { $blockEntityHits.Add($entry) | Out-Null }
        if ($line -match $storagePattern) { $storageHits.Add($entry) | Out-Null }
        if ($line -match $placeholderPattern -or $rel -match '(?i)(placeholder|bridge)') { $placeholderHits.Add($entry) | Out-Null }
    }
}

function TopRows($items, [int]$Limit) {
    return @($items | Sort-Object File, Line | Select-Object -First $Limit)
}

function Add-Table([System.Collections.Generic.List[string]]$md, [string]$Title, $Rows, [string]$EmptyText) {
    $md.Add('') | Out-Null
    $md.Add("## $Title") | Out-Null
    $md.Add('') | Out-Null
    if (-not $Rows -or @($Rows).Count -eq 0) {
        $md.Add($EmptyText) | Out-Null
        return
    }
    $md.Add('| File | Line | Evidence |') | Out-Null
    $md.Add('|---|---:|---|') | Out-Null
    foreach ($row in $Rows) {
        $md.Add('| ' + (Escape-Md $row.File) + ' | ' + $row.Line + ' | `' + (Escape-Md $row.Text) + '` |') | Out-Null
    }
}

$uniqueFiles = @($hits | Select-Object -ExpandProperty File -Unique)
$registryFiles = @($registryHits | Select-Object -ExpandProperty File -Unique)
$blockEntityFiles = @($blockEntityHits | Select-Object -ExpandProperty File -Unique)
$storageFiles = @($storageHits | Select-Object -ExpandProperty File -Unique)
$placeholderFiles = @($placeholderHits | Select-Object -ExpandProperty File -Unique)

$md = New-Object System.Collections.Generic.List[string]
$md.Add('# Infusion Real Source Candidate Audit') | Out-Null
$md.Add('') | Out-Null
$md.Add('Generated: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')) | Out-Null
$md.Add('') | Out-Null
$md.Add('## Summary') | Out-Null
$md.Add('') | Out-Null
$md.Add('| Metric | Count |') | Out-Null
$md.Add('|---|---:|') | Out-Null
$md.Add('| Candidate-term hits | ' + $hits.Count + ' |') | Out-Null
$md.Add('| Files with candidate hits | ' + $uniqueFiles.Count + ' |') | Out-Null
$md.Add('| Registry-like candidate files | ' + $registryFiles.Count + ' |') | Out-Null
$md.Add('| BlockEntity-like candidate files | ' + $blockEntityFiles.Count + ' |') | Out-Null
$md.Add('| Aspect/storage-like candidate files | ' + $storageFiles.Count + ' |') | Out-Null
$md.Add('| Placeholder/bridge-like candidate files | ' + $placeholderFiles.Count + ' |') | Out-Null
$md.Add('') | Out-Null
$md.Add('## Interpretation') | Out-Null
$md.Add('') | Out-Null
$md.Add('- This audit is a discovery pass for the first real infusion aspect/essentia source policy.') | Out-Null
$md.Add('- It does not select a source implementation by itself.') | Out-Null
$md.Add('- A source should be selected only if there is a stable block or block entity with explicit storage semantics in the current port.') | Out-Null
$md.Add('- Placeholder or bridge-only identities must not be used as real source implementations.') | Out-Null
$md.Add('- If no stable local source exists, the resolver must remain fail-closed and player-facing infusion completion must stay disabled.') | Out-Null

Add-Table $md 'Registry-like source candidates' (TopRows $registryHits 60) 'No registry-like source candidates were found.'
Add-Table $md 'BlockEntity-like source candidates' (TopRows $blockEntityHits 80) 'No block-entity-like source candidates were found.'
Add-Table $md 'Aspect/storage-like candidates' (TopRows $storageHits 80) 'No aspect/storage-like candidates were found.'
Add-Table $md 'Placeholder or bridge warning candidates' (TopRows $placeholderHits 80) 'No placeholder/bridge warning candidates were found.'
Add-Table $md 'All candidate hits, first 120' (TopRows $hits 120) 'No candidate hits were found.'

$md.Add('') | Out-Null
$md.Add('## Porting conclusion') | Out-Null
$md.Add('') | Out-Null
$md.Add('- Do not implement a real infusion source adapter until one candidate is manually reviewed and confirmed to be a real storage-bearing block/entity, not just a recipe/page placeholder.') | Out-Null
$md.Add('- The first implementation slice should support exactly one reviewed source type and keep all unknown sources fail-closed.') | Out-Null
$md.Add('- Re-run this audit after adding jar, tube, alembic, aura, essentia storage, or related block/entity implementations.') | Out-Null

New-Item -ItemType Directory -Force -Path (Split-Path $OutputPath) | Out-Null
Set-Content -LiteralPath $OutputPath -Value $md -Encoding UTF8
Write-Host ('[tc-port] Wrote ' + (Get-RelativePath $RepoRoot $OutputPath))
