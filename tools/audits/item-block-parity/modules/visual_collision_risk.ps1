[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [string]$PortRoot = "05_neoforge_port",
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_visual_collision_risk_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_visual_collision_risk_report.md" }

$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$assetsRoot = Join-Path $portPath "src/main/resources/assets/thaumcraft"
$javaRoot = Join-Path $portPath "src/main/java"
$blocksRegistryPath = Join-Path $portPath "src/main/java/thaumcraft/common/registry/TCBlocks.java"
$blocksRegistryText = if (Test-Path -LiteralPath $blocksRegistryPath -PathType Leaf) { Get-Content -Raw -LiteralPath $blocksRegistryPath } else { "" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}

function Read-JsonFileOrNull([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return $null }
    try { return Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json } catch { return $null }
}

function Get-JsonPropertyNames($Object) {
    if ($null -eq $Object) { return @() }
    return @($Object.PSObject.Properties | ForEach-Object { $_.Name } | Sort-Object -Unique)
}

function Format-List($Values) {
    $items = @($Values | Where-Object { $_ } | Select-Object -Unique)
    if ($items.Count -eq 0) { return "<none>" }
    return $items -join ", "
}

function Add-ResultRow($Rows, [string]$Kind, [string]$Id, [string]$Subcheck, [string]$Status, [string]$Evidence, [string]$Path = "") {
    $Rows.Add([pscustomobject][ordered]@{
        check = "visual_collision_risk"
        subcheck = $Subcheck
        kind = $Kind
        id = "thaumcraft:$Id"
        status = $Status
        evidence = $Evidence
        path = $Path
    })
}

function Get-ModelPathFromRef([string]$ModelRef) {
    if ([string]::IsNullOrWhiteSpace($ModelRef)) { return $null }
    $normalized = $ModelRef
    if ($normalized.StartsWith("thaumcraft:")) { $normalized = $normalized.Substring("thaumcraft:".Length) }
    return Join-Path $assetsRoot "models/$normalized.json"
}

function Get-NumberArray($Value) {
    return @($Value | ForEach-Object { [double]$_ })
}

function Test-FullCubeElement($Element) {
    if ($null -eq $Element -or $null -eq $Element.from -or $null -eq $Element.to) { return $false }
    $from = Get-NumberArray $Element.from
    $to = Get-NumberArray $Element.to
    if ($from.Count -ne 3 -or $to.Count -ne 3) { return $false }
    return ($from[0] -eq 0.0D -and $from[1] -eq 0.0D -and $from[2] -eq 0.0D -and
            $to[0] -eq 16.0D -and $to[1] -eq 16.0D -and $to[2] -eq 16.0D)
}

function Get-GeometryInfo($Json) {
    $elements = @($Json.elements)
    $hasElements = $elements.Count -gt 0
    $isSingleFullCube = $false
    if ($elements.Count -eq 1) { $isSingleFullCube = Test-FullCubeElement $elements[0] }
    $nonFull = $hasElements -and -not $isSingleFullCube
    $touchNorth = $false
    $touchSouth = $false
    $touchEast = $false
    $touchWest = $false
    $touchUp = $false
    $touchDown = $false
    foreach ($element in $elements) {
        if ($null -eq $element.from -or $null -eq $element.to) { continue }
        $from = Get-NumberArray $element.from
        $to = Get-NumberArray $element.to
        if ($from.Count -ne 3 -or $to.Count -ne 3) { continue }
        if ($from[2] -le 0.01D -and $to[2] -le 4.0D) { $touchNorth = $true }
        if ($to[2] -ge 15.99D -and $from[2] -ge 12.0D) { $touchSouth = $true }
        if ($to[0] -ge 15.99D -and $from[0] -ge 12.0D) { $touchEast = $true }
        if ($from[0] -le 0.01D -and $to[0] -le 4.0D) { $touchWest = $true }
        if ($to[1] -ge 15.99D -and $from[1] -ge 12.0D) { $touchUp = $true }
        if ($from[1] -le 0.01D -and $to[1] -le 4.0D) { $touchDown = $true }
    }
    $northDefaultProjection = $touchNorth -and -not $touchSouth -and -not $touchEast -and -not $touchWest -and -not $touchUp -and -not $touchDown
    return [pscustomobject][ordered]@{
        elementCount = $elements.Count
        hasElements = $hasElements
        nonFull = $nonFull
        isSingleFullCube = $isSingleFullCube
        northDefaultProjection = $northDefaultProjection
        touches = [ordered]@{
            north = $touchNorth
            south = $touchSouth
            east = $touchEast
            west = $touchWest
            up = $touchUp
            down = $touchDown
        }
    }
}

function Get-BlockDeclarationText([string]$Id) {
    if ([string]::IsNullOrWhiteSpace($blocksRegistryText)) { return "" }
    $escaped = [regex]::Escape($Id)
    $pattern = 'BLOCKS\.register\(\s*"' + $escaped + '"(?<chunk>.*?)(?=public\s+static\s+final\s+Supplier<Block>|private\s+TCBlocks|\z)'
    $match = [regex]::Match($blocksRegistryText, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)
    if ($match.Success) { return $match.Groups["chunk"].Value }
    return ""
}

function Get-JavaClassText($Entry) {
    if ($null -eq $Entry -or [string]::IsNullOrWhiteSpace([string]$Entry.portClassFile)) { return "" }
    $path = Join-Path $portPath ([string]$Entry.portClassFile)
    if (Test-Path -LiteralPath $path -PathType Leaf) { return Get-Content -Raw -LiteralPath $path }
    return ""
}

function Get-VariantObject($BlockstateJson, [string]$Facing) {
    if ($null -eq $BlockstateJson -or $null -eq $BlockstateJson.variants) { return $null }
    foreach ($property in @($BlockstateJson.variants.PSObject.Properties)) {
        if ($property.Name -match "(^|,)facing=$Facing(,|$)") { return $property.Value }
    }
    return $null
}

function Normalize-RotationValue($Value) {
    if ($null -eq $Value) { return 0 }
    try { return ([int]$Value + 360) % 360 } catch { return 0 }
}

function Test-RotationMatchesNorthDefault($Variant, [string]$Facing) {
    if ($null -eq $Variant) { return $false }
    $x = Normalize-RotationValue $Variant.x
    $y = Normalize-RotationValue $Variant.y
    switch ($Facing) {
        "north" { return ($x -eq 0 -and $y -eq 0) }
        "south" { return ($x -eq 0 -and $y -eq 180) }
        "east"  { return ($x -eq 0 -and $y -eq 90) }
        "west"  { return ($x -eq 0 -and $y -eq 270) }
        "down"  { return ($x -eq 90 -and $y -eq 0) }
        "up"    { return ($x -eq 270 -and $y -eq 0) }
        default { return $true }
    }
}

function Get-RotationSummary($Variant) {
    if ($null -eq $Variant) { return "<missing>" }
    return "x=$(Normalize-RotationValue $Variant.x),y=$(Normalize-RotationValue $Variant.y)"
}

$results = [System.Collections.Generic.List[object]]::new()
$modelSummaries = [System.Collections.Generic.List[object]]::new()

foreach ($entry in @($port.entries | Sort-Object kind, registryId)) {
    if ($entry.kind -ne "block") { continue }
    $id = [string]$entry.registryId
    $referencedModels = @($entry.resources.referencedBlockModels | Where-Object { $_ } | Sort-Object -Unique)
    if ($referencedModels.Count -eq 0) { continue }

    $classText = Get-JavaClassText $entry
    $blockDeclarationText = Get-BlockDeclarationText $id
    $hasShapeContract = $classText -match '\bgetShape\b|\bgetCollisionShape\b|\bVoxelShape\b|\bShapes\.'
    $hasOcclusionContract = ($classText -match '\bgetOcclusionShape\b|\buseShapeForLightOcclusion\b|\bskipRendering\b') -or ($blockDeclarationText -match '\.noOcclusion\s*\(')

    foreach ($modelRef in $referencedModels) {
        $modelPath = Get-ModelPathFromRef $modelRef
        $relativeModelPath = if ($modelPath -and (Test-Path -LiteralPath $modelPath -PathType Leaf)) { ConvertTo-RelativeRepoPath $modelPath } else { "assets/thaumcraft/models/$modelRef.json" }
        $modelJson = if ($modelPath) { Read-JsonFileOrNull $modelPath } else { $null }
        if ($null -eq $modelJson) {
            Add-ResultRow $results "block" $id "visual_collision_model_json" "VISUAL_MODEL_MISSING" "Block model missing or invalid for visual collision risk scan: $modelRef" $relativeModelPath
            continue
        }

        $geometry = Get-GeometryInfo $modelJson
        $modelSummaries.Add([pscustomobject][ordered]@{
            kind = "block"
            id = "thaumcraft:$id"
            modelRef = $modelRef
            path = $relativeModelPath
            elementCount = $geometry.elementCount
            nonFull = $geometry.nonFull
            northDefaultProjection = $geometry.northDefaultProjection
        })

        if ($geometry.nonFull) {
            if ($hasShapeContract) {
                Add-ResultRow $results "block" $id "non_full_model_shape_contract" "VISUAL_EVIDENCE" "Non-full model has Java shape/collision evidence; model=$modelRef; class=$($entry.portClassFile)" $relativeModelPath
            } else {
                Add-ResultRow $results "block" $id "non_full_model_shape_contract" "VISUAL_REVIEW_NEEDED" "Non-full block model has custom elements but no detected getShape/getCollisionShape/VoxelShape contract; may behave like full-cube collision" $relativeModelPath
            }

            if ($hasOcclusionContract) {
                Add-ResultRow $results "block" $id "non_full_model_occlusion_contract" "VISUAL_EVIDENCE" "Non-full model has occlusion evidence via Java method or noOcclusion() registration; model=$modelRef" $relativeModelPath
            } else {
                Add-ResultRow $results "block" $id "non_full_model_occlusion_contract" "VISUAL_REVIEW_NEEDED" "Non-full block model lacks detected noOcclusion/getOcclusionShape/useShapeForLightOcclusion evidence; may cause face-culling/x-ray artifacts" $relativeModelPath
            }
        }

        if ($geometry.northDefaultProjection) {
            $blockstatePath = Join-Path $assetsRoot "blockstates/$id.json"
            $blockstateJson = Read-JsonFileOrNull $blockstatePath
            $rotationEvidence = [System.Collections.Generic.List[string]]::new()
            $rotationOk = $true
            foreach ($face in @("north", "south", "east", "west", "down", "up")) {
                $variant = Get-VariantObject $blockstateJson $face
                $matches = Test-RotationMatchesNorthDefault $variant $face
                $rotationEvidence.Add("$face=$(Get-RotationSummary $variant)")
                if (-not $matches) { $rotationOk = $false }
            }
            $blockstateRelative = if (Test-Path -LiteralPath $blockstatePath -PathType Leaf) { ConvertTo-RelativeRepoPath $blockstatePath } else { "assets/thaumcraft/blockstates/$id.json" }
            if ($rotationOk) {
                Add-ResultRow $results "block" $id "directional_model_facing_rotation" "VISUAL_EVIDENCE" "North-default projection model has expected blockstate rotations: $(Format-List $rotationEvidence)" $blockstateRelative
            } else {
                Add-ResultRow $results "block" $id "directional_model_facing_rotation" "VISUAL_REVIEW_NEEDED" "North-default projection/nozzle model has suspicious facing rotations: $(Format-List $rotationEvidence)" $blockstateRelative
            }
        }
    }

    if ([bool]$entry.blockItem) {
        $itemModelPath = Join-Path $assetsRoot "models/item/$id.json"
        $itemRelative = if (Test-Path -LiteralPath $itemModelPath -PathType Leaf) { ConvertTo-RelativeRepoPath $itemModelPath } else { "assets/thaumcraft/models/item/$id.json" }
        $itemJson = Read-JsonFileOrNull $itemModelPath
        if ($null -eq $itemJson) { continue }
        $parent = if ($itemJson.parent) { [string]$itemJson.parent } else { "" }
        $displaySlots = @(Get-JsonPropertyNames $itemJson.display)
        $inheritsCustomBlockModel = $false
        foreach ($modelRef in $referencedModels) {
            if ($parent -eq "thaumcraft:$modelRef" -or $parent -eq $modelRef) { $inheritsCustomBlockModel = $true }
        }
        if ($inheritsCustomBlockModel -and $displaySlots.Count -eq 0) {
            Add-ResultRow $results "item" $id "block_item_custom_model_display" "VISUAL_REVIEW_NEEDED" "BlockItem inherits custom block model parent=$parent but has no explicit display transforms; GUI/hand item may render as incorrect flat/front view" $itemRelative
        } elseif ($inheritsCustomBlockModel) {
            Add-ResultRow $results "item" $id "block_item_custom_model_display" "VISUAL_EVIDENCE" "BlockItem inherits custom block model parent=$parent with display transforms present=$(Format-List $displaySlots)" $itemRelative
        }
    }
}

$orderedResults = @($results | Sort-Object status, kind, id, subcheck)
$summaryBySubcheck = @($orderedResults | Group-Object subcheck | Sort-Object Name | ForEach-Object {
    [pscustomobject][ordered]@{
        subcheck = $_.Name
        rows = $_.Count
        evidence = @($_.Group | Where-Object status -eq "VISUAL_EVIDENCE").Count
        reviewNeeded = @($_.Group | Where-Object status -eq "VISUAL_REVIEW_NEEDED").Count
        missing = @($_.Group | Where-Object status -eq "VISUAL_MODEL_MISSING").Count
    }
})

$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("visual_collision_risk")
    policy = "Report-only generic visual/collision risk scan. It catches non-full model shape/occlusion gaps, custom BlockItem display gaps, and north-default directional blockstate rotation risks like the Bellows issue. Review-needed rows are not hard failures by themselves."
    summary = [ordered]@{
        rows = $orderedResults.Count
        evidence = @($orderedResults | Where-Object status -eq "VISUAL_EVIDENCE").Count
        reviewNeeded = @($orderedResults | Where-Object status -eq "VISUAL_REVIEW_NEEDED").Count
        missing = @($orderedResults | Where-Object status -eq "VISUAL_MODEL_MISSING").Count
        models = @($modelSummaries).Count
        bySubcheck = @($summaryBySubcheck)
    }
    models = @($modelSummaries | Sort-Object kind, id, path)
    results = $orderedResults
}

New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 16 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block visual collision risk report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Rows | Evidence | Review needed | Missing models | Models inspected |")
$lines.Add("|---:|---:|---:|---:|---:|")
$lines.Add("| $($report.summary.rows) | $($report.summary.evidence) | $($report.summary.reviewNeeded) | $($report.summary.missing) | $($report.summary.models) |")
$lines.Add("")
$lines.Add("## By subcheck")
$lines.Add("")
$lines.Add("| Subcheck | Rows | Evidence | Review needed | Missing |")
$lines.Add("|---|---:|---:|---:|---:|")
foreach ($row in $summaryBySubcheck) { $lines.Add("| $($row.subcheck) | $($row.rows) | $($row.evidence) | $($row.reviewNeeded) | $($row.missing) |") }
$lines.Add("")
$lines.Add("## Review-needed and missing rows")
$lines.Add("")
$lines.Add("| Kind | ID | Subcheck | Status | Path | Evidence |")
$lines.Add("|---|---|---|---|---|---|")
foreach ($row in $orderedResults | Where-Object { $_.status -ne "VISUAL_EVIDENCE" }) {
    $safeEvidence = $row.evidence.Replace("|", "\|")
    $lines.Add("| $($row.kind) | ``$($row.id)`` | $($row.subcheck) | $($row.status) | ``$($row.path)`` | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Visual collision risk report: $OutputMarkdown"
Write-Output "Rows=$($report.summary.rows), evidence=$($report.summary.evidence), reviewNeeded=$($report.summary.reviewNeeded), missing=$($report.summary.missing), models=$($report.summary.models)"
