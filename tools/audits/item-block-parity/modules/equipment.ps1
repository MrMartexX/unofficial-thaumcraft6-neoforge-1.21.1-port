[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$LegacyManifestPath,
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
if (-not $OutputJson) { $OutputJson = Join-Path $reportRoot 'item_block_equipment_report.json' }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $reportRoot 'item_block_equipment_report.md' }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    if ([string]::IsNullOrWhiteSpace($FullPath)) { return '' }
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace('\', '/')
}
function Normalize-Id([object]$Value) {
    if ($null -eq $Value) { return '' }
    $s = ([string]$Value).Trim().ToLowerInvariant()
    if ($s.StartsWith('thaumcraft:')) { $s = $s.Substring('thaumcraft:'.Length) }
    return $s
}
function Get-EntryId($Entry) {
    foreach ($name in @('registryId','id','name','registryName','legacyId')) {
        $prop = $Entry.PSObject.Properties[$name]
        if ($null -ne $prop -and -not [string]::IsNullOrWhiteSpace([string]$prop.Value)) { return Normalize-Id $prop.Value }
    }
    return ''
}
function Get-Entries($Manifest) {
    foreach ($name in @('entries','items','blocks')) {
        $prop = $Manifest.PSObject.Properties[$name]
        if ($null -ne $prop -and $null -ne $prop.Value) { return @($prop.Value) }
    }
    return @()
}
function Get-EntrySearchText($Entry) {
    $values = @()
    foreach ($prop in @($Entry.PSObject.Properties)) {
        if ($null -eq $prop.Value) { continue }
        if ($prop.Value -is [System.Array]) {
            foreach ($v in @($prop.Value)) { if ($null -ne $v) { $values += [string]$v } }
        } else {
            $values += [string]$prop.Value
        }
    }
    return (($values -join ' ') -replace '[^a-zA-Z0-9_:/.-]', ' ').ToLowerInvariant()
}
function Get-MatchedTerms {
    param([string]$Text, [string[]]$Terms)
    $hits = @()
    $lower = $Text.ToLowerInvariant()
    foreach ($term in @($Terms)) {
        if ([string]::IsNullOrWhiteSpace($term)) { continue }
        if ($lower.Contains(([string]$term).ToLowerInvariant())) { $hits += [string]$term }
    }
    return @($hits | Select-Object -Unique)
}
function Add-Row {
    param(
        [ref]$Rows,
        [string]$Status,
        [string]$Severity,
        [string]$Classification,
        [string]$Subject,
        [string]$Path,
        [string[]]$Evidence = @(),
        [string]$Recommendation = ''
    )
    $Rows.Value += [pscustomobject][ordered]@{
        check = 'equipment'
        status = $Status
        severity = $Severity
        classification = $Classification
        subject = $Subject
        path = $Path
        evidence = @($Evidence)
        recommendation = $Recommendation
    }
}
function Get-FilesFromRoots {
    param([string[]]$Roots, [string[]]$Extensions)
    $files = @()
    foreach ($root in @($Roots)) {
        if ([string]::IsNullOrWhiteSpace($root)) { continue }
        $fullRoot = Join-Path $PortRoot $root
        if (-not (Test-Path -LiteralPath $fullRoot -PathType Container)) { continue }
        $all = @(Get-ChildItem -LiteralPath $fullRoot -Recurse -File -ErrorAction SilentlyContinue)
        foreach ($file in $all) {
            if (@($Extensions) -contains $file.Extension) { $files += $file }
        }
    }
    return @($files | Sort-Object FullName -Unique)
}

$rulesPath = Join-Path $RulesRoot 'equipment-rules.json'
if (Test-Path -LiteralPath $rulesPath -PathType Leaf) {
    $rules = Get-Content -Raw -LiteralPath $rulesPath | ConvertFrom-Json
} else {
    $rules = [pscustomobject]@{
        sourceRoots = @('src/main/java')
        sourceExtensions = @('.java')
        resourceRoots = @('src/main/resources/assets', 'src/main/resources/data')
        resourceExtensions = @('.json')
        equipmentTerms = @('ArmorItem','TieredItem','EquipmentSlot','ArmorMaterial','ToolMaterial')
        armorTerms = @('ArmorItem','helmet','chestplate','leggings','boots','armor')
        toolTerms = @('TieredItem','SwordItem','PickaxeItem','AxeItem','ShovelItem','HoeItem','tool')
        accessoryTerms = @('Curios','Baubles','accessory','trinket','wearable')
        materialTerms = @('thaumium','void','material','durability','defense','toughness')
        manifestCandidateTerms = @('sword','pickaxe','axe','shovel','hoe','helmet','chestplate','leggings','boots','robe','goggles','armor','tool')
        resourceEquipmentTerms = @('sword','pickaxe','axe','shovel','hoe','helmet','chestplate','leggings','boots','armor')
        status = [pscustomobject]@{ pass = 'EQUIPMENT_PASS'; review = 'EQUIPMENT_REVIEW_NEEDED'; error = 'EQUIPMENT_ERROR' }
        markdownSampleLimit = 120
    }
}
$passStatus = if ($rules.status.pass) { [string]$rules.status.pass } else { 'EQUIPMENT_PASS' }
$reviewStatus = if ($rules.status.review) { [string]$rules.status.review } else { 'EQUIPMENT_REVIEW_NEEDED' }
$errorStatus = if ($rules.status.error) { [string]$rules.status.error } else { 'EQUIPMENT_ERROR' }
$sampleLimit = if ($rules.markdownSampleLimit) { [int]$rules.markdownSampleLimit } else { 120 }

if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw ('Port manifest not found: ' + $PortManifestPath) }
$portManifest = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$portEntries = Get-Entries $portManifest
$sourceFiles = Get-FilesFromRoots -Roots @($rules.sourceRoots) -Extensions @($rules.sourceExtensions)
$resourceFiles = Get-FilesFromRoots -Roots @($rules.resourceRoots) -Extensions @($rules.resourceExtensions)
$rows = @()
$equipmentSourceFiles = 0
$armorSourceFiles = 0
$toolSourceFiles = 0
$accessorySourceFiles = 0
$materialEvidenceFiles = 0
$resourceEquipmentFiles = 0
$manifestCandidateIds = 0

foreach ($file in @($sourceFiles)) {
    $text = Get-Content -Raw -LiteralPath $file.FullName
    $equipmentHits = Get-MatchedTerms -Text $text -Terms @($rules.equipmentTerms)
    $armorHits = Get-MatchedTerms -Text $text -Terms @($rules.armorTerms)
    $toolHits = Get-MatchedTerms -Text $text -Terms @($rules.toolTerms)
    $accessoryHits = Get-MatchedTerms -Text $text -Terms @($rules.accessoryTerms)
    $materialHits = Get-MatchedTerms -Text $text -Terms @($rules.materialTerms)
    if ($equipmentHits.Count -eq 0 -and $armorHits.Count -eq 0 -and $toolHits.Count -eq 0 -and $accessoryHits.Count -eq 0) { continue }
    $rel = ConvertTo-RelativeRepoPath $file.FullName
    $evidence = @()
    if ($equipmentHits.Count -gt 0) { $evidence += ('equipment terms: ' + ($equipmentHits -join ', ')); $equipmentSourceFiles++ }
    if ($armorHits.Count -gt 0) { $evidence += ('armor terms: ' + ($armorHits -join ', ')); $armorSourceFiles++ }
    if ($toolHits.Count -gt 0) { $evidence += ('tool terms: ' + ($toolHits -join ', ')); $toolSourceFiles++ }
    if ($accessoryHits.Count -gt 0) { $evidence += ('accessory terms: ' + ($accessoryHits -join ', ')); $accessorySourceFiles++ }
    if ($materialHits.Count -gt 0) { $evidence += ('material terms: ' + ($materialHits -join ', ')); $materialEvidenceFiles++ }
    $classification = 'equipment_source_review'
    if ($armorHits.Count -gt 0 -and $toolHits.Count -gt 0) { $classification = 'tool_and_armor_source_review' }
    elseif ($armorHits.Count -gt 0) { $classification = 'armor_source_review' }
    elseif ($toolHits.Count -gt 0) { $classification = 'tool_source_review' }
    elseif ($accessoryHits.Count -gt 0) { $classification = 'accessory_source_review' }
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification $classification -Subject $file.Name -Path $rel -Evidence $evidence -Recommendation 'Confirm tool/armor/accessory material, slot, durability, damage, defense, discount and reveal/warp policy against legacy behavior or mark intentional differences.'
}

foreach ($file in @($resourceFiles)) {
    $rel = ConvertTo-RelativeRepoPath $file.FullName
    $text = (Get-Content -Raw -LiteralPath $file.FullName) + ' ' + $rel
    $resourceHits = Get-MatchedTerms -Text $text -Terms @($rules.resourceEquipmentTerms)
    if ($resourceHits.Count -eq 0) { continue }
    $resourceEquipmentFiles++
    Add-Row -Rows ([ref]$rows) -Status $passStatus -Severity 'info' -Classification 'equipment_resource_evidence' -Subject $file.Name -Path $rel -Evidence @('resource terms: ' + ($resourceHits -join ', ')) -Recommendation 'Resource contains tool/armor/equipment-facing clues.'
}

foreach ($entry in @($portEntries)) {
    $id = Get-EntryId $entry
    if ([string]::IsNullOrWhiteSpace($id)) { continue }
    $entryText = (Get-EntrySearchText $entry) + ' ' + $id
    $hits = Get-MatchedTerms -Text $entryText -Terms @($rules.manifestCandidateTerms)
    if ($hits.Count -gt 0) {
        $manifestCandidateIds++
        Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'manifest_equipment_candidate' -Subject $id -Path '' -Evidence @('manifest terms: ' + ($hits -join ', ')) -Recommendation 'Confirm whether this ID needs equipment material, slot, durability, combat stat, Curios/accessory, discount/reveal or explicit intentional non-equipment policy.'
    }
}

if ($rows.Count -eq 0) {
    Add-Row -Rows ([ref]$rows) -Status $reviewStatus -Severity 'review' -Classification 'no_equipment_evidence' -Subject 'source/resource scan' -Path (ConvertTo-RelativeRepoPath $PortRoot) -Evidence @('No configured equipment evidence was found.') -Recommendation 'Confirm whether equipment support is not started or rules need additional terms.'
}

$orderedRows = @($rows | Sort-Object severity, classification, path, subject)
$summary = [ordered]@{
    rows = @($orderedRows).Count
    pass = @($orderedRows | Where-Object { $_.status -eq $passStatus }).Count
    reviewNeeded = @($orderedRows | Where-Object { $_.status -eq $reviewStatus }).Count
    errors = @($orderedRows | Where-Object { $_.status -eq $errorStatus }).Count
    sourceFilesScanned = @($sourceFiles).Count
    resourceFilesScanned = @($resourceFiles).Count
    equipmentSourceFiles = $equipmentSourceFiles
    armorSourceFiles = $armorSourceFiles
    toolSourceFiles = $toolSourceFiles
    accessorySourceFiles = $accessorySourceFiles
    materialEvidenceFiles = $materialEvidenceFiles
    resourceEquipmentFiles = $resourceEquipmentFiles
    manifestCandidateIds = $manifestCandidateIds
}
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString('o')
    policy = 'Report-only equipment audit. Review rows identify tool, armor, material, accessory and manifest candidates; they are not gameplay parity failures.'
    inputs = [ordered]@{
        portManifest = ConvertTo-RelativeRepoPath $PortManifestPath
        portRoot = ConvertTo-RelativeRepoPath $PortRoot
        rules = ConvertTo-RelativeRepoPath $rulesPath
        checks = @($Checks)
    }
    summary = $summary
    results = @($orderedRows)
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
$report | ConvertTo-Json -Depth 18 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = @()
$lines += '# Item/block equipment audit'
$lines += ''
$lines += ('Generated: ' + $report.generatedAtUtc)
$lines += ''
$lines += 'Policy: report-only tool/armor/equipment review. Review rows are material, slot, stat and accessory policy work, not gameplay parity failures.'
$lines += ''
$lines += '## Summary'
$lines += ''
$lines += ('- Rows: ' + $summary.rows)
$lines += ('- Pass: ' + $summary.pass)
$lines += ('- Review needed: ' + $summary.reviewNeeded)
$lines += ('- Errors: ' + $summary.errors)
$lines += ('- Source files scanned: ' + $summary.sourceFilesScanned)
$lines += ('- Resource files scanned: ' + $summary.resourceFilesScanned)
$lines += ('- Equipment source files: ' + $summary.equipmentSourceFiles)
$lines += ('- Armor source files: ' + $summary.armorSourceFiles)
$lines += ('- Tool source files: ' + $summary.toolSourceFiles)
$lines += ('- Accessory source files: ' + $summary.accessorySourceFiles)
$lines += ('- Material evidence files: ' + $summary.materialEvidenceFiles)
$lines += ('- Resource equipment files: ' + $summary.resourceEquipmentFiles)
$lines += ('- Manifest candidate IDs: ' + $summary.manifestCandidateIds)
$lines += ''
$lines += '## Non-pass sample'
$lines += ''
$lines += '| Status | Classification | Subject | Path | Evidence | Recommendation |'
$lines += '|---|---|---|---|---|---|'
$nonPass = @($orderedRows | Where-Object { $_.status -ne $passStatus } | Select-Object -First $sampleLimit)
foreach ($row in $nonPass) {
    $evidence = if ($row.evidence) { (@($row.evidence) -join '; ').Replace('|', '\|') } else { '' }
    $recommendation = if ($row.recommendation) { $row.recommendation.Replace('|', '\|') } else { '' }
    $lines += ('| ' + $row.status + ' | ' + $row.classification + ' | ' + $row.subject + ' | ' + $row.path + ' | ' + $evidence + ' | ' + $recommendation + ' |')
}
if ($nonPass.Count -eq 0) {
    $lines += '| EQUIPMENT_PASS | <all> | <all> | <all> | Source/resource scan found no review-only rows. | None. |'
}
$lines += ''
$lines += ('Only the first ' + $sampleLimit + ' non-pass rows are shown in Markdown; JSON contains all rows.')
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM

Write-Output ('Equipment audit report: ' + $OutputMarkdown)
Write-Output ('Rows={0}, pass={1}, reviewNeeded={2}, errors={3}, sourceFilesScanned={4}, resourceFilesScanned={5}, equipmentSourceFiles={6}, armorSourceFiles={7}, toolSourceFiles={8}, accessorySourceFiles={9}, materialEvidenceFiles={10}, resourceEquipmentFiles={11}, manifestCandidateIds={12}' -f $summary.rows, $summary.pass, $summary.reviewNeeded, $summary.errors, $summary.sourceFilesScanned, $summary.resourceFilesScanned, $summary.equipmentSourceFiles, $summary.armorSourceFiles, $summary.toolSourceFiles, $summary.accessorySourceFiles, $summary.materialEvidenceFiles, $summary.resourceEquipmentFiles, $summary.manifestCandidateIds)
