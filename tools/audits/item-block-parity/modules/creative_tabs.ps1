[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [Parameter(Mandatory = $true)][string]$PortRoot,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = 'Stop'
$RepoRoot = (Resolve-Path -LiteralPath $RepoRoot).Path
$PortRoot = (Resolve-Path -LiteralPath $PortRoot).Path
if (-not $RulesRoot) { $RulesRoot = Join-Path $RepoRoot 'tools/audits/item-block-parity/rules' }
$reportRoot = Join-Path $RepoRoot 'tools/reports/local/item-block-parity'
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_creative_tabs_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_creative_tabs_report.md' }

function ConvertTo-RelativeRepoPath([string]$FullPath) { if ([string]::IsNullOrWhiteSpace($FullPath)) { return '' }; return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace('\', '/') }
function Normalize-Id([object]$Value) { if ($null -eq $Value) { return '' }; $s = ([string]$Value).Trim().ToLowerInvariant(); if ($s.StartsWith('thaumcraft:')) { $s = $s.Substring('thaumcraft:'.Length) }; return $s }
function Get-EntryId($Entry) { foreach ($name in @('registryId','id','name','registryName','legacyId')) { $prop = $Entry.PSObject.Properties[$name]; if ($null -ne $prop -and -not [string]::IsNullOrWhiteSpace([string]$prop.Value)) { return Normalize-Id $prop.Value } }; return '' }
function Get-Entries($Manifest) { foreach ($name in @('entries','items','blocks')) { $prop = $Manifest.PSObject.Properties[$name]; if ($null -ne $prop -and $null -ne $prop.Value) { return @($prop.Value) } }; return @() }
function Get-EntryKind($Entry) { foreach ($name in @('kind','type','declaredType')) { $prop = $Entry.PSObject.Properties[$name]; if ($null -ne $prop -and -not [string]::IsNullOrWhiteSpace([string]$prop.Value)) { return ([string]$prop.Value).ToLowerInvariant() } }; return '' }
function Test-IsPlayerFacingEntry($Entry) { $id = Get-EntryId $Entry; if ([string]::IsNullOrWhiteSpace($id)) { return $false }; $kind = Get-EntryKind $Entry; if ($kind -match 'block|item') { return $true }; return $true }
function Get-CreativeSourceFiles { param([string[]]$SourceRoots,[string[]]$Extensions,[string[]]$Terms); $files=@(); foreach($root in @($SourceRoots)){ if([string]::IsNullOrWhiteSpace($root)){continue}; $fullRoot=Join-Path $PortRoot $root; if(-not(Test-Path -LiteralPath $fullRoot -PathType Container)){continue}; foreach($ext in @($Extensions)){ if([string]::IsNullOrWhiteSpace($ext)){continue}; foreach($file in @(Get-ChildItem -LiteralPath $fullRoot -Recurse -File -Filter ('*' + $ext) -ErrorAction SilentlyContinue)){ $text=Get-Content -Raw -LiteralPath $file.FullName; foreach($term in @($Terms)){ if(-not [string]::IsNullOrWhiteSpace($term) -and $text.IndexOf($term,[System.StringComparison]::OrdinalIgnoreCase) -ge 0){ $files += $file; break } } } } }; return @($files | Sort-Object FullName -Unique) }
function Test-IdMentioned { param([string]$Text,[string]$Id); if([string]::IsNullOrWhiteSpace($Text) -or [string]::IsNullOrWhiteSpace($Id)){return $false}; $id=Normalize-Id $Id; $upper=$id.ToUpperInvariant(); if($Text.IndexOf('thaumcraft:' + $id,[System.StringComparison]::OrdinalIgnoreCase) -ge 0){return $true}; if($Text.IndexOf('"' + $id + '"',[System.StringComparison]::OrdinalIgnoreCase) -ge 0){return $true}; if($Text.IndexOf("'" + $id + "'",[System.StringComparison]::OrdinalIgnoreCase) -ge 0){return $true}; if($Text.IndexOf($upper,[System.StringComparison]::Ordinal) -ge 0){return $true}; return $false }
function Add-Row { param([ref]$Rows,[string]$Status,[string]$Severity,[string]$Id,[string]$Classification,[string]$Evidence,[string[]]$SourceFiles=@()); $Rows.Value += [pscustomobject][ordered]@{ check='creative_tabs'; status=$Status; severity=$Severity; id=$Id; classification=$Classification; sourceFiles=@($SourceFiles); evidence=$Evidence } }

$rulesPath = Join-Path $RulesRoot 'creative-tabs-rules.json'
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) { $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json } else { $rules = [pscustomobject]@{ sourceRoots=@('src/main/java'); sourceFileExtensions=@('.java'); creativeSearchTerms=@('CreativeModeTab','BuildCreativeModeTabContentsEvent','displayItems','CREATIVE_TAB','creative tab','.accept(','event.accept'); idReferencePattern='thaumcraft:([a-z0-9_./-]+)'; maxMissingSamples=200; status=[pscustomobject]@{pass='CREATIVE_TABS_PASS';review='CREATIVE_TABS_REVIEW_NEEDED';error='CREATIVE_TABS_ERROR'} } }
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { 'CREATIVE_TABS_PASS' }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { 'CREATIVE_TABS_REVIEW_NEEDED' }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { 'CREATIVE_TABS_ERROR' }
$pattern = if ($rules.idReferencePattern) { [string]$rules.idReferencePattern } else { 'thaumcraft:([a-z0-9_./-]+)' }
$rows = @()
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { Add-Row -Rows ([ref]$rows) -Status $errorStatus -Severity 'error' -Id '' -Classification 'missing_port_manifest' -Evidence ('Port manifest not found: ' + $PortManifestPath) } else {
    $portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
    $portEntries = @(Get-Entries $portManifest)
    $manifestIds = @($portEntries | Where-Object { Test-IsPlayerFacingEntry $_ } | ForEach-Object { Get-EntryId $_ } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Sort-Object -Unique)
    $creativeFiles = @(Get-CreativeSourceFiles -SourceRoots @($rules.sourceRoots) -Extensions @($rules.sourceFileExtensions) -Terms @($rules.creativeSearchTerms))
    $sourceTextParts = @(); foreach($file in $creativeFiles){ $sourceTextParts += Get-Content -Raw -LiteralPath $file.FullName }
    $combinedSource = $sourceTextParts -join "`n"
    $creativeFileRel = @($creativeFiles | ForEach-Object { ConvertTo-RelativeRepoPath $_.FullName })
    if($creativeFiles.Count -eq 0){ Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Id '' -Classification 'creative_source_not_found' -Evidence 'No source files containing creative tab terms were found.' } else { Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -Id '' -Classification 'creative_source_present' -Evidence ('Creative tab source files found: ' + $creativeFiles.Count) -SourceFiles $creativeFileRel }
    $sourceRefs = @([regex]::Matches($combinedSource.ToLowerInvariant(), $pattern) | ForEach-Object { Normalize-Id $_.Groups[1].Value } | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Sort-Object -Unique)
    foreach($id in $manifestIds){ if(Test-IdMentioned -Text $combinedSource -Id $id){ Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -Id $id -Classification 'manifest_id_referenced_by_creative_source' -Evidence 'Port manifest ID is mentioned by creative tab source text.' -SourceFiles $creativeFileRel } else { Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Id $id -Classification 'manifest_id_not_observed_in_creative_source' -Evidence 'Port manifest ID is not directly observed in creative tab source text.' } }
    foreach($refId in $sourceRefs){ if($refId -notin $manifestIds){ Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Id $refId -Classification 'creative_source_reference_not_in_manifest' -Evidence 'Creative source references a Thaumcraft ID not present in the port manifest.' -SourceFiles $creativeFileRel } }
}
$orderedRows = @($rows | Sort-Object severity, classification, id)
$summary = [ordered]@{ rows=@($orderedRows).Count; pass=@($orderedRows|Where-Object{$_.status -eq $passStatus}).Count; reviewNeeded=@($orderedRows|Where-Object{$_.status -eq $reviewStatus}).Count; errors=@($orderedRows|Where-Object{$_.status -eq $errorStatus}).Count; creativeSourceFiles=@($orderedRows|Where-Object{$_.classification -eq 'creative_source_present'}).Count; exposedManifestIds=@($orderedRows|Where-Object{$_.classification -eq 'manifest_id_referenced_by_creative_source'}).Count; unobservedManifestIds=@($orderedRows|Where-Object{$_.classification -eq 'manifest_id_not_observed_in_creative_source'}).Count; sourceOnlyRefs=@($orderedRows|Where-Object{$_.classification -eq 'creative_source_reference_not_in_manifest'}).Count }
$report = [ordered]@{ schemaVersion=1; generatedAtUtc=[DateTime]::UtcNow.ToString('o'); policy='Report-only creative tab audit. Review rows identify player-facing IDs not directly observed in creative tab source or source-only references; they are not gameplay parity failures.'; inputs=[ordered]@{ portManifest=ConvertTo-RelativeRepoPath $PortManifestPath; portRoot=ConvertTo-RelativeRepoPath $PortRoot; rules=ConvertTo-RelativeRepoPath $rulesPath; checks=@($Checks) }; summary=$summary; results=@($orderedRows) }
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 18 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM
$lines=@(); $lines+='# Item/block creative tab audit'; $lines+=''; $lines+=('Generated: '+$report.generatedAtUtc); $lines+=''; $lines+='Policy: report-only creative tab audit. Review rows are player-facing grouping work, not gameplay parity failures.'; $lines+=''; $lines+='## Summary'; $lines+=''; $lines+=('- Rows: '+$summary.rows); $lines+=('- Pass: '+$summary.pass); $lines+=('- Review needed: '+$summary.reviewNeeded); $lines+=('- Errors: '+$summary.errors); $lines+=('- Exposed manifest IDs: '+$summary.exposedManifestIds); $lines+=('- Unobserved manifest IDs: '+$summary.unobservedManifestIds); $lines+=('- Source-only refs: '+$summary.sourceOnlyRefs); $lines+=''; $lines+='## Non-pass sample'; $lines+=''; $lines+='| Status | Classification | ID | Evidence |'; $lines+='|---|---|---|---|'
$nonPass=@($orderedRows|Where-Object{$_.status -ne $passStatus}|Select-Object -First 100); foreach($row in $nonPass){ $evidence=if($row.evidence){$row.evidence.Replace('|','\|')}else{''}; $lines+=('| '+$row.status+' | '+$row.classification+' | '+$row.id+' | '+$evidence+' |') }; if($nonPass.Count -eq 0){$lines+='| CREATIVE_TABS_PASS | <all> | <all> | All player-facing manifest IDs were observed in creative tab source. |'}; $lines+=''; $lines+='Only the first 100 non-pass rows are shown in Markdown; JSON contains all rows.'; $lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output ('Creative tab audit report: ' + $OutputMarkdown)
Write-Output ('Rows={0}, pass={1}, reviewNeeded={2}, errors={3}, exposedManifestIds={4}, unobservedManifestIds={5}, sourceOnlyRefs={6}' -f $summary.rows,$summary.pass,$summary.reviewNeeded,$summary.errors,$summary.exposedManifestIds,$summary.unobservedManifestIds,$summary.sourceOnlyRefs)
