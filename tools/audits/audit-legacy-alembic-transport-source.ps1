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
    $OutputPath = Join-Path $RepoRoot '06_docs/audits/alembic_legacy_transport_source_audit.md'
}

$legacyRoot = Join-Path $RepoRoot '02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master'
$portRoot = Join-Path $RepoRoot '05_neoforge_port'
if (-not (Test-Path $legacyRoot)) { throw "Legacy source folder not found: $legacyRoot" }
if (-not (Test-Path $portRoot)) { throw "NeoForge port folder not found: $portRoot" }

$legacyFiles = @(Get-ChildItem -Path $legacyRoot -Recurse -File -Include *.java,*.json,*.mcmeta,*.lang,*.txt | Where-Object { $_.FullName -notmatch '[\\/](build|bin|out|target)[\\/]' })
$portFiles = @(Get-ChildItem -Path $portRoot -Recurse -File -Include *.java,*.json,*.mcmeta,*.properties,*.toml | Where-Object { $_.FullName -notmatch '[\\/](build|bin|out|target)[\\/]' })

$alembicPattern = '(?i)alembic'
$transportPattern = '(?i)(IEssentiaTransport|TCEssentiaTransport|getEssentia|getSuction|canInputFrom|canOutputTo|addEssentia|takeEssentia|suction|amount|aspect|essentia|vent|smelter|furnace|jar|tube)'
$classPattern = '(?i)(class|interface|enum)\s+\w*Alembic\w*|\bTileAlembic\b|\bBlockAlembic\b|\bItemAlembic\b'
$recipePattern = '(?i)(recipe|json|thaumcraft:alembic|alembic)'

function Collect-Hits($files, [string]$SourceName) {
    $rows = New-Object System.Collections.Generic.List[object]
    foreach ($file in $files) {
        $rel = Get-RelativePath $RepoRoot $file.FullName
        $lines = @(Get-Content -LiteralPath $file.FullName -ErrorAction SilentlyContinue)
        $fileText = ($lines -join "`n")
        if ($fileText -notmatch $alembicPattern -and $rel -notmatch $alembicPattern) { continue }
        for ($i = 0; $i -lt $lines.Count; $i++) {
            $line = [string]$lines[$i]
            if ($line -match $alembicPattern -or $line -match $transportPattern -or $line -match $classPattern) {
                $category = @()
                if ($line -match $classPattern -or $rel -match $classPattern) { $category += 'class_identity' }
                if ($line -match $alembicPattern -or $rel -match $alembicPattern) { $category += 'alembic' }
                if ($line -match $transportPattern) { $category += 'transport_semantics' }
                if ($line -match $recipePattern -or $rel -match '(?i)recipe|models|blockstates|lang') { $category += 'data_or_asset' }
                $rows.Add([pscustomobject]@{
                    Source = $SourceName
                    File = $rel
                    Line = $i + 1
                    Category = (($category | Select-Object -Unique) -join ', ')
                    Text = $line.Trim()
                }) | Out-Null
            }
        }
    }
    return @($rows | Sort-Object File, Line)
}

$legacyHits = @(Collect-Hits $legacyFiles 'legacy')
$portHits = @(Collect-Hits $portFiles 'port')
$legacyFilesWithHits = @($legacyHits | Select-Object -ExpandProperty File -Unique)
$portFilesWithHits = @($portHits | Select-Object -ExpandProperty File -Unique)
$legacyTransportHits = @($legacyHits | Where-Object { $_.Category -match 'transport_semantics' })
$portTransportHits = @($portHits | Where-Object { $_.Category -match 'transport_semantics' })

function Add-HitTable([System.Collections.Generic.List[string]]$md, [string]$Title, $Rows, [int]$Limit, [string]$EmptyText) {
    $md.Add('') | Out-Null
    $md.Add("## $Title") | Out-Null
    $md.Add('') | Out-Null
    $selected = @($Rows | Select-Object -First $Limit)
    if ($selected.Count -eq 0) {
        $md.Add($EmptyText) | Out-Null
        return
    }
    $md.Add('| Source | File | Line | Category | Evidence |') | Out-Null
    $md.Add('|---|---|---:|---|---|') | Out-Null
    foreach ($row in $selected) {
        $md.Add('| ' + (Escape-Md $row.Source) + ' | ' + (Escape-Md $row.File) + ' | ' + $row.Line + ' | ' + (Escape-Md $row.Category) + ' | `' + (Escape-Md $row.Text) + '` |') | Out-Null
    }
}

$md = New-Object System.Collections.Generic.List[string]
$md.Add('# Alembic Legacy Transport Source Audit') | Out-Null
$md.Add('') | Out-Null
$md.Add('Generated: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')) | Out-Null
$md.Add('') | Out-Null
$md.Add('## Summary') | Out-Null
$md.Add('') | Out-Null
$md.Add('| Metric | Count |') | Out-Null
$md.Add('|---|---:|') | Out-Null
$md.Add('| Legacy files mentioning alembic | ' + $legacyFilesWithHits.Count + ' |') | Out-Null
$md.Add('| Legacy evidence rows | ' + $legacyHits.Count + ' |') | Out-Null
$md.Add('| Legacy transport evidence rows | ' + $legacyTransportHits.Count + ' |') | Out-Null
$md.Add('| Current port files mentioning alembic | ' + $portFilesWithHits.Count + ' |') | Out-Null
$md.Add('| Current port evidence rows | ' + $portHits.Count + ' |') | Out-Null
$md.Add('| Current port transport evidence rows | ' + $portTransportHits.Count + ' |') | Out-Null
$md.Add('') | Out-Null
$md.Add('## Interpretation') | Out-Null
$md.Add('') | Out-Null
$md.Add('- This audit owns the first Alembic discovery step after the legacy tube/jar transport slice.') | Out-Null
$md.Add('- It does not implement Alembic. It identifies legacy class/data evidence and current port gaps before a code batch.') | Out-Null
$md.Add('- The next implementation should be narrow: Alembic as the first real smelter output transport endpoint, not full smelter behavior.') | Out-Null
$md.Add('- Keep smelter inventory, fuel, efficiency, flux and bellows discovery in later batches unless the source evidence requires a minimal stub boundary.') | Out-Null

Add-HitTable $md 'Legacy Alembic evidence, first 180 rows' $legacyHits 180 'No Alembic evidence found in legacy source.'
Add-HitTable $md 'Legacy transport-relevant evidence, first 120 rows' $legacyTransportHits 120 'No transport-relevant Alembic evidence found in legacy source.'
Add-HitTable $md 'Current port Alembic mentions, first 120 rows' $portHits 120 'No current port Alembic mentions found.'
Add-HitTable $md 'Current port transport-relevant Alembic mentions, first 80 rows' $portTransportHits 80 'No current port transport-relevant Alembic mentions found.'

$md.Add('') | Out-Null
$md.Add('## Porting conclusion') | Out-Null
$md.Add('') | Out-Null
if ($legacyFilesWithHits.Count -eq 0) {
    $md.Add('- Legacy source evidence was not found automatically. Check source path assumptions before implementing Alembic.') | Out-Null
} elseif ($portFilesWithHits.Count -eq 0) {
    $md.Add('- Alembic appears present in legacy evidence but absent from the current port. Next code batch should add the Alembic block/entity/item/data boundary from the legacy class evidence.') | Out-Null
} else {
    $md.Add('- Alembic appears in both legacy and current port evidence. Next code batch should compare current implementation to the legacy transport methods and fill only the missing output endpoint behavior.') | Out-Null
}
$md.Add('- Do not expose Alembic through `TCEssentiaCapabilities.BLOCK` until its sided input/output and storage semantics are explicitly audited.') | Out-Null
$md.Add('- Do not claim smelter completion from Alembic alone; the design document treats smelter inventory/aspect/fuel/efficiency as a separate audited machine batch.') | Out-Null

New-Item -ItemType Directory -Force -Path (Split-Path $OutputPath) | Out-Null
Set-Content -LiteralPath $OutputPath -Value $md -Encoding UTF8
Write-Host ('[tc-port] Wrote ' + (Get-RelativePath $RepoRoot $OutputPath))
