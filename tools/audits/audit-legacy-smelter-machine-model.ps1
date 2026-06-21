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
    $OutputPath = Join-Path $RepoRoot '06_docs/audits/smelter_legacy_machine_model_audit.md'
}

$legacyRoot = Join-Path $RepoRoot '02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master'
$portRoot = Join-Path $RepoRoot '05_neoforge_port'
if (-not (Test-Path $legacyRoot)) { throw "Legacy source folder not found: $legacyRoot" }
if (-not (Test-Path $portRoot)) { throw "NeoForge port folder not found: $portRoot" }

$legacyFiles = @(Get-ChildItem -Path $legacyRoot -Recurse -File -Include *.java,*.json,*.lang,*.txt | Where-Object { $_.FullName -notmatch '[\\/](build|bin|out|target)[\\/]' })
$portFiles = @(Get-ChildItem -Path $portRoot -Recurse -File -Include *.java,*.json,*.properties,*.toml | Where-Object { $_.FullName -notmatch '[\\/](build|bin|out|target)[\\/]' })

$smelterFilePattern = '(?i)(smelter|furnace)'
$classPattern = '(?i)(class|interface|enum)\s+\w*(Smelter|Furnace)\w*|\bTileSmelter\w*\b|\bBlockSmelter\w*\b|\bTileThaumatorium\b|\bTileAlembic\b'
$machinePattern = '(?i)(inventory|IInventory|ISidedInventory|ItemStack|NonNullList|slots|slot|fuel|burn|cook|smelt|furnace|recipe|input|output|efficiency|vis|flux|aura|aspect|essentia|alembic|processAlembics|addToContainer|crucible|vent|bellows|pedestal)'
$transportPattern = '(?i)(IEssentiaTransport|TCEssentiaTransport|getEssentia|getSuction|addEssentia|takeEssentia|canInputFrom|canOutputTo|isConnectable)'

function Collect-Hits($files, [string]$SourceName) {
    $rows = New-Object System.Collections.Generic.List[object]
    foreach ($file in $files) {
        $rel = Get-RelativePath $RepoRoot $file.FullName
        $lines = @(Get-Content -LiteralPath $file.FullName -ErrorAction SilentlyContinue)
        $fileText = ($lines -join "`n")
        if ($rel -notmatch $smelterFilePattern -and $fileText -notmatch '(?i)smelter|essentia smelter|furnace|processAlembics|Thaumatorium') {
            continue
        }
        for ($i = 0; $i -lt $lines.Count; $i++) {
            $line = [string]$lines[$i]
            if ($line -match $classPattern -or $line -match $machinePattern -or $line -match $transportPattern) {
                $category = @()
                if ($line -match $classPattern -or $rel -match $classPattern) { $category += 'class_identity' }
                if ($line -match $machinePattern) { $category += 'machine_model' }
                if ($line -match $transportPattern) { $category += 'transport_contract' }
                if ($line -match '(?i)fuel|burn|cook|smelt|recipe') { $category += 'processing' }
                if ($line -match '(?i)efficiency|vis|flux|aura|bellows') { $category += 'efficiency_or_flux' }
                if ($line -match '(?i)alembic|processAlembics') { $category += 'alembic_output' }
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
$legacyClassHits = @($legacyHits | Where-Object { $_.Category -match 'class_identity' })
$legacyProcessingHits = @($legacyHits | Where-Object { $_.Category -match 'processing' })
$legacyEfficiencyHits = @($legacyHits | Where-Object { $_.Category -match 'efficiency_or_flux' })
$legacyAlembicOutputHits = @($legacyHits | Where-Object { $_.Category -match 'alembic_output' })
$portClassHits = @($portHits | Where-Object { $_.Category -match 'class_identity' })
$portProcessingHits = @($portHits | Where-Object { $_.Category -match 'processing' })

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
$md.Add('# Smelter Legacy Machine Model Audit') | Out-Null
$md.Add('') | Out-Null
$md.Add('Generated: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')) | Out-Null
$md.Add('') | Out-Null
$md.Add('## Summary') | Out-Null
$md.Add('') | Out-Null
$md.Add('| Metric | Count |') | Out-Null
$md.Add('|---|---:|') | Out-Null
$md.Add('| Legacy files with smelter/furnace evidence | ' + $legacyFilesWithHits.Count + ' |') | Out-Null
$md.Add('| Legacy evidence rows | ' + $legacyHits.Count + ' |') | Out-Null
$md.Add('| Legacy class identity rows | ' + $legacyClassHits.Count + ' |') | Out-Null
$md.Add('| Legacy processing/fuel/recipe rows | ' + $legacyProcessingHits.Count + ' |') | Out-Null
$md.Add('| Legacy efficiency/flux/bellows rows | ' + $legacyEfficiencyHits.Count + ' |') | Out-Null
$md.Add('| Legacy alembic-output rows | ' + $legacyAlembicOutputHits.Count + ' |') | Out-Null
$md.Add('| Current port files with smelter/furnace evidence | ' + $portFilesWithHits.Count + ' |') | Out-Null
$md.Add('| Current port evidence rows | ' + $portHits.Count + ' |') | Out-Null
$md.Add('| Current port class identity rows | ' + $portClassHits.Count + ' |') | Out-Null
$md.Add('| Current port processing/fuel/recipe rows | ' + $portProcessingHits.Count + ' |') | Out-Null
$md.Add('') | Out-Null
$md.Add('## Interpretation') | Out-Null
$md.Add('') | Out-Null
$md.Add('- This audit owns the first smelter machine-model discovery step after the tube/jar/Alembic transport endpoint slices.') | Out-Null
$md.Add('- It does not implement smelting. It extracts legacy evidence needed before replacing the current smelter skeleton with real inventory/aspect/fuel/efficiency behavior.') | Out-Null
$md.Add('- The next implementation should be one audited machine batch and should not introduce Bellows discovery, vent rendering, essentia mirrors, thaumatorium, or broad automation at the same time.') | Out-Null

Add-HitTable $md 'Legacy class identity evidence, first 160 rows' $legacyClassHits 160 'No legacy smelter/furnace class identity evidence found.'
Add-HitTable $md 'Legacy processing/fuel/recipe evidence, first 220 rows' $legacyProcessingHits 220 'No legacy processing/fuel/recipe evidence found.'
Add-HitTable $md 'Legacy efficiency/flux/bellows evidence, first 160 rows' $legacyEfficiencyHits 160 'No legacy efficiency/flux/bellows evidence found.'
Add-HitTable $md 'Legacy Alembic output evidence, first 120 rows' $legacyAlembicOutputHits 120 'No legacy Alembic output evidence found.'
Add-HitTable $md 'Current port smelter/furnace evidence, first 180 rows' $portHits 180 'No current port smelter evidence found.'

$md.Add('') | Out-Null
$md.Add('## Porting conclusion') | Out-Null
$md.Add('') | Out-Null
if ($legacyClassHits.Count -eq 0) {
    $md.Add('- Legacy source class evidence was not found automatically. Check source path assumptions before implementing smelter.') | Out-Null
} else {
    $md.Add('- Legacy class evidence exists. Use the class identity and processing tables above to pick the exact smelter class/methods before a code batch.') | Out-Null
}
$md.Add('- Current Alembic endpoint work should be treated as dependency complete only for output storage; it does not implement the smelter production machine.') | Out-Null
$md.Add('- Do not expose smelter endpoint skeletons as transport capabilities until the machine model and output contract are implemented and audited.') | Out-Null

New-Item -ItemType Directory -Force -Path (Split-Path $OutputPath) | Out-Null
Set-Content -LiteralPath $OutputPath -Value $md -Encoding UTF8
Write-Host ('[tc-port] Wrote ' + (Get-RelativePath $RepoRoot $OutputPath))
