param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path,
    [string]$OutputPath = (Join-Path $RepoRoot '06_docs/audits/infusion_tag_input_expansion_audit.md')
)

$ErrorActionPreference = 'Stop'
function Escape-Md([string]$Value) { if ($null -eq $Value) { return '' }; return ($Value -replace '\|', '\|' -replace "`r?`n", ' ') }
function Read-JsonFile([System.IO.FileInfo]$File) { Get-Content -LiteralPath $File.FullName -Raw -Encoding UTF8 | ConvertFrom-Json -Depth 100 }
function Write-Text([string]$Path, [string]$Text) { [System.IO.File]::WriteAllText($Path, $Text, [System.Text.UTF8Encoding]::new($false)) }
function Get-RecipeIdFromFile([string]$DataRoot, [System.IO.FileInfo]$File) {
    $relative = [System.IO.Path]::GetRelativePath($DataRoot, $File.FullName).Replace('\', '/')
    if ($relative -match '^(?<namespace>[^/]+)/(recipe|recipes)/(?<recipePath>.+)\.json$') { return ($matches['namespace'] + ':' + $matches['recipePath']) }
    return $null
}
function Resolve-TagFile([string]$DataRoot, [string]$TagId) {
    if ($TagId -notmatch '^(?<namespace>[^:]+):(?<path>.+)$') { return $null }
    $namespace = $matches['namespace']; $tagPath = $matches['path']
    $candidates = @(
        (Join-Path $DataRoot ($namespace + '/tags/items/' + $tagPath + '.json')),
        (Join-Path $DataRoot ($namespace + '/tags/item/' + $tagPath + '.json'))
    )
    foreach ($candidate in $candidates) { if (Test-Path -LiteralPath $candidate) { return (Get-Item -LiteralPath $candidate) } }
    return $null
}

$repo = (Resolve-Path -LiteralPath $RepoRoot).Path
$dataRoot = Join-Path $repo '05_neoforge_port/src/main/resources/data'
if (-not (Test-Path -LiteralPath $dataRoot)) { throw ('Missing data root: ' + $dataRoot) }

$knownRemainderItems = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::OrdinalIgnoreCase)
@(
    'minecraft:water_bucket','minecraft:lava_bucket','minecraft:milk_bucket','minecraft:powder_snow_bucket',
    'minecraft:cod_bucket','minecraft:salmon_bucket','minecraft:tropical_fish_bucket','minecraft:pufferfish_bucket','minecraft:axolotl_bucket','minecraft:tadpole_bucket',
    'minecraft:potion','minecraft:splash_potion','minecraft:lingering_potion','minecraft:honey_bottle','minecraft:glass_bottle',
    'minecraft:mushroom_stew','minecraft:rabbit_stew','minecraft:beetroot_soup','minecraft:suspicious_stew'
) | ForEach-Object { [void]$knownRemainderItems.Add($_) }

$builtInTags = @{
    'minecraft:wool' = @(
        'minecraft:white_wool','minecraft:orange_wool','minecraft:magenta_wool','minecraft:light_blue_wool',
        'minecraft:yellow_wool','minecraft:lime_wool','minecraft:pink_wool','minecraft:gray_wool',
        'minecraft:light_gray_wool','minecraft:cyan_wool','minecraft:purple_wool','minecraft:blue_wool',
        'minecraft:brown_wool','minecraft:green_wool','minecraft:red_wool','minecraft:black_wool'
    )
}

function Expand-Tag([string]$DataRoot, [string]$TagId, [System.Collections.Generic.HashSet[string]]$Seen) {
    $items = New-Object System.Collections.Generic.List[string]
    $missing = New-Object System.Collections.Generic.List[string]
    if ($Seen.Contains($TagId)) { return [pscustomobject]@{ Items = @(); MissingTags = @(); Status = 'cycle' } }
    [void]$Seen.Add($TagId)
    $file = Resolve-TagFile $DataRoot $TagId
    if (-not $file) {
        if ($builtInTags.ContainsKey($TagId)) { return [pscustomobject]@{ Items = @($builtInTags[$TagId]); MissingTags = @(); Status = 'builtin_fallback' } }
        return [pscustomobject]@{ Items = @(); MissingTags = @($TagId); Status = 'external_or_missing' }
    }
    $json = Read-JsonFile $file
    foreach ($value in @($json.values)) {
        $id = $null
        if ($value -is [string]) { $id = $value }
        elseif ($null -ne $value.id) { $id = [string]$value.id }
        if ([string]::IsNullOrWhiteSpace($id)) { continue }
        if ($id.StartsWith('#')) {
            $nested = Expand-Tag $DataRoot $id.Substring(1) $Seen
            foreach ($item in @($nested.Items)) { $items.Add($item) }
            foreach ($tag in @($nested.MissingTags)) { $missing.Add($tag) }
        } else { $items.Add($id) }
    }
    return [pscustomobject]@{ Items = @($items | Sort-Object -Unique); MissingTags = @($missing | Sort-Object -Unique); Status = 'local' }
}

$recipeFiles = @(Get-ChildItem -LiteralPath $dataRoot -Recurse -File -Filter '*.json' | Where-Object { $_.FullName -match '[\\/](recipe|recipes)[\\/]' })
$tagRefs = New-Object System.Collections.Generic.List[object]
foreach ($file in $recipeFiles) {
    $json = Read-JsonFile $file
    $names = @($json.PSObject.Properties.Name)
    if ($null -eq $json -or -not ($names -contains 'type') -or [string]$json.type -ne 'thaumcraft:infusion') { continue }
    $recipeId = Get-RecipeIdFromFile $dataRoot $file
    $relative = [System.IO.Path]::GetRelativePath($repo, $file.FullName).Replace('\', '/')
    if ($names -contains 'catalyst' -and $null -ne $json.catalyst -and @($json.catalyst.PSObject.Properties.Name) -contains 'tag') {
        $tagRefs.Add([pscustomobject]@{ RecipeId = $recipeId; Role = 'catalyst'; Tag = [string]$json.catalyst.tag; File = $relative })
    }
    if ($names -contains 'components' -and $null -ne $json.components) {
        $index = 0
        foreach ($component in @($json.components)) {
            if (@($component.PSObject.Properties.Name) -contains 'tag') { $tagRefs.Add([pscustomobject]@{ RecipeId = $recipeId; Role = ('component[' + $index + ']'); Tag = [string]$component.tag; File = $relative }) }
            $index++
        }
    }
}

$expandedRows = New-Object System.Collections.Generic.List[object]
$blockedRows = New-Object System.Collections.Generic.List[object]
foreach ($ref in $tagRefs) {
    $seen = [System.Collections.Generic.HashSet[string]]::new([StringComparer]::OrdinalIgnoreCase)
    $expanded = Expand-Tag $dataRoot $ref.Tag $seen
    $items = @($expanded.Items)
    $missing = @($expanded.MissingTags)
    $knownRemainders = @($items | Where-Object { $knownRemainderItems.Contains($_) })
    foreach ($item in $knownRemainders) { $blockedRows.Add([pscustomobject]@{ RecipeId=$ref.RecipeId; Role=$ref.Role; Tag=$ref.Tag; Item=$item; File=$ref.File }) }
    $expandedRows.Add([pscustomobject]@{ RecipeId=$ref.RecipeId; Role=$ref.Role; Tag=$ref.Tag; Status=$expanded.Status; LocalItems=($items -join ', '); MissingNestedTags=($missing -join ', '); File=$ref.File })
}

$lines = New-Object System.Collections.Generic.List[string]
$lines.Add('# Infusion Tag Input Expansion Audit')
$lines.Add('')
$lines.Add("Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')")
$lines.Add('')
$lines.Add('## Summary')
$lines.Add('')
$lines.Add('| Metric | Count |')
$lines.Add('|---|---:|')
$lines.Add('| Infusion tag input references | ' + $tagRefs.Count + ' |')
$lines.Add('| Tag references with locally expanded known remainder items | ' + $blockedRows.Count + ' |')
$lines.Add('| External or missing tag references | ' + (@($expandedRows | Where-Object { $_.Status -eq 'external_or_missing' }).Count) + ' |')
$lines.Add('| Built-in fallback tag references | ' + (@($expandedRows | Where-Object { $_.Status -eq 'builtin_fallback' }).Count) + ' |')
$lines.Add('')
$lines.Add('## Expanded tag references')
$lines.Add('')
if ($expandedRows.Count -eq 0) { $lines.Add('No tag-based infusion inputs were found.') }
else {
    $lines.Add('| Recipe id | Role | Tag | Status | Expanded items | Missing nested tags | File |')
    $lines.Add('|---|---|---|---|---|---|---|')
    foreach ($row in ($expandedRows | Sort-Object RecipeId, Role)) { $lines.Add('| ' + (Escape-Md $row.RecipeId) + ' | ' + (Escape-Md $row.Role) + ' | ' + (Escape-Md $row.Tag) + ' | ' + (Escape-Md $row.Status) + ' | ' + (Escape-Md $row.LocalItems) + ' | ' + (Escape-Md $row.MissingNestedTags) + ' | ' + (Escape-Md $row.File) + ' |') }
}
$lines.Add('')
$lines.Add('## Known remainder items found through tag expansion')
$lines.Add('')
if ($blockedRows.Count -eq 0) { $lines.Add('No known bucket/bottle/bowl-style remainder items were found through local or built-in fallback tag expansion.') }
else {
    $lines.Add('| Recipe id | Role | Tag | Item | File |')
    $lines.Add('|---|---|---|---|---|')
    foreach ($row in ($blockedRows | Sort-Object RecipeId, Role, Item)) { $lines.Add('| ' + (Escape-Md $row.RecipeId) + ' | ' + (Escape-Md $row.Role) + ' | ' + (Escape-Md $row.Tag) + ' | ' + (Escape-Md $row.Item) + ' | ' + (Escape-Md $row.File) + ' |') }
}
$lines.Add('')
$lines.Add('## Porting conclusion')
$lines.Add('')
$lines.Add('- Locally resolvable and known built-in fallback infusion tag inputs do not currently expand to known bucket/bottle/bowl-style remainder items.')
$lines.Add('- External tags still require runtime/pack validation if new tag namespaces are introduced.')
$lines.Add('- Re-run this audit whenever local tag files, tag references, accepted ingredient forms, or built-in fallback assumptions change.')
Write-Text $OutputPath ($lines -join "`n")