[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [string]$PortRoot = "05_neoforge_port",
    [string]$LegacyRoot = "02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master",
    [string]$OriginalJar = "01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar",
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$portPath = if ([System.IO.Path]::IsPathRooted($PortRoot)) { $PortRoot } else { Join-Path $RepoRoot $PortRoot }
$legacyPath = if ([System.IO.Path]::IsPathRooted($LegacyRoot)) { $LegacyRoot } else { Join-Path $RepoRoot $LegacyRoot }
$originalJarPath = if ([System.IO.Path]::IsPathRooted($OriginalJar)) { $OriginalJar } else { Join-Path $RepoRoot $OriginalJar }
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not (Test-Path -LiteralPath $PortManifestPath -PathType Leaf)) { throw "Port manifest not found: $PortManifestPath" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_texture_color_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_texture_color_report.md" }

function ConvertTo-RelativeRepoPath([string]$FullPath) {
    return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
}
function Read-JsonFileOrNull([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return $null }
    try { return Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json } catch { return $null }
}
function Read-RuleDocument([string]$Root, [string]$FileName) {
    if ([string]::IsNullOrWhiteSpace($Root)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    $path = Join-Path $Root $FileName
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { return [pscustomobject]@{ schemaVersion = 1; entries = @() } }
    return Get-Content -Raw -LiteralPath $path | ConvertFrom-Json
}
function New-TextureRuleLookup($Document) {
    $lookup = @{}
    foreach ($entry in @($Document.entries)) {
        $textureRef = if ($entry.textureRef) { [string]$entry.textureRef } elseif ($entry.ref) { [string]$entry.ref } else { "" }
        if ([string]::IsNullOrWhiteSpace($textureRef)) { continue }
        $lookup[$textureRef] = $entry
        if ($textureRef.StartsWith("thaumcraft:")) {
            $lookup[$textureRef.Substring("thaumcraft:".Length)] = $entry
        }
    }
    return $lookup
}
function Get-TextureRule($Lookup, [string]$TextureRef, [string]$TexturePath) {
    foreach ($key in @($TextureRef, $TexturePath, $TexturePath.Replace("textures/", ""), ("thaumcraft:" + $TexturePath.Replace("textures/", "").Replace(".png", "")))) {
        if ($Lookup.ContainsKey($key)) { return $Lookup[$key] }
    }
    return $null
}
function Get-ModelTextureRefs([string]$ModelPath) {
    $json = Read-JsonFileOrNull $ModelPath
    if ($null -eq $json -or $null -eq $json.textures) { return @() }
    return @($json.textures.PSObject.Properties | ForEach-Object { [string]$_.Value } | Where-Object { $_ -and $_ -notlike "#*" -and $_.StartsWith("thaumcraft:") } | Sort-Object -Unique)
}
function Resolve-PortTexture([string]$TextureRef, [string]$AssetsRoot) {
    $pathPart = $TextureRef.Substring("thaumcraft:".Length)
    $texturePath = "textures/$pathPart.png"
    $fullPath = Join-Path $AssetsRoot $texturePath
    return [pscustomobject][ordered]@{
        textureRef = $TextureRef
        texturePath = $texturePath.Replace("\", "/")
        fullPath = $fullPath
        exists = Test-Path -LiteralPath $fullPath -PathType Leaf
    }
}
function Get-LegacyTexturePathVariants([string]$TexturePath) {
    $variants = [System.Collections.Generic.List[string]]::new()
    $normalized = $TexturePath.Replace("\", "/")
    $variants.Add($normalized)
    if ($normalized.StartsWith("textures/block/")) {
        $variants.Add($normalized.Replace("textures/block/", "textures/blocks/"))
    }
    if ($normalized.StartsWith("textures/item/")) {
        $variants.Add($normalized.Replace("textures/item/", "textures/items/"))
    }
    if ($normalized.StartsWith("textures/blocks/")) {
        $variants.Add($normalized.Replace("textures/blocks/", "textures/block/"))
    }
    if ($normalized.StartsWith("textures/items/")) {
        $variants.Add($normalized.Replace("textures/items/", "textures/item/"))
    }
    switch ($normalized) {
        "textures/block/stone_arcane_brick.png" { $variants.Add("textures/blocks/arcane_brick_stone.png") }
        "textures/block/stone_porous.png" { $variants.Add("textures/blocks/porous_stone.png") }
        "textures/item/brass_ingot.png" { $variants.Add("textures/items/ingot_brass.png") }
        "textures/item/thaumium_ingot.png" { $variants.Add("textures/items/ingot_thaumium.png") }
        "textures/item/brass_plate.png" { $variants.Add("textures/items/plate_brass.png") }
        "textures/item/thaumium_plate.png" { $variants.Add("textures/items/plate_thaumium.png") }
        "textures/item/void_plate.png" { $variants.Add("textures/items/plate_void.png") }
        "textures/item/thaumometer.png" { $variants.Add("textures/items/scanner.png") }
    }
    return @($variants | Select-Object -Unique)
}
function Resolve-LegacyTexture([string]$TexturePath) {
    $pathVariants = @(Get-LegacyTexturePathVariants $TexturePath)
    $candidates = foreach ($variant in $pathVariants) {
        Join-Path $legacyPath "src/main/resources/assets/thaumcraft/$variant"
        Join-Path $legacyPath "assets/thaumcraft/$variant"
        Join-Path $legacyPath "resources/assets/thaumcraft/$variant"
    }
    foreach ($candidate in $candidates | Select-Object -Unique) {
        if (Test-Path -LiteralPath $candidate -PathType Leaf) {
            return [pscustomobject][ordered]@{ source = "legacy_root"; fullPath = $candidate; exists = $true; archiveEntry = $null }
        }
    }
    if (Test-Path -LiteralPath $originalJarPath -PathType Leaf) {
        try {
            Add-Type -AssemblyName System.IO.Compression.FileSystem -ErrorAction SilentlyContinue
            $zip = [System.IO.Compression.ZipFile]::OpenRead($originalJarPath)
            try {
                foreach ($variant in $pathVariants) {
                    $entryName = "assets/thaumcraft/$($variant.Replace('\\','/'))"
                    $entry = $zip.Entries | Where-Object { $_.FullName -eq $entryName } | Select-Object -First 1
                    if (-not $entry) { continue }
                    $tempRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity/legacy-texture-cache"
                    New-Item -ItemType Directory -Force -Path $tempRoot | Out-Null
                    $safeName = ($entryName -replace '[^A-Za-z0-9_.-]', '_')
                    $tempPath = Join-Path $tempRoot $safeName
                    $entry.ExtractToFile($tempPath, $true)
                    return [pscustomobject][ordered]@{ source = "original_jar"; fullPath = $tempPath; exists = $true; archiveEntry = $entryName }
                }
            } finally {
                $zip.Dispose()
            }
        } catch {
            return [pscustomobject][ordered]@{ source = "original_jar_error"; fullPath = $null; exists = $false; archiveEntry = $null; error = $_.Exception.Message }
        }
    }
    return [pscustomobject][ordered]@{ source = "missing"; fullPath = $null; exists = $false; archiveEntry = $null }
}
function Get-FileSha256([string]$Path) {
    return (Get-FileHash -Algorithm SHA256 -LiteralPath $Path).Hash.ToLowerInvariant()
}
function Get-BitmapMetrics([string]$Path) {
    try {
        Add-Type -AssemblyName System.Drawing -ErrorAction SilentlyContinue
        $bitmap = [System.Drawing.Bitmap]::new($Path)
        try {
            $width = $bitmap.Width
            $height = $bitmap.Height
            $pixelCount = [Math]::Max(1, $width * $height)
            $maxSamples = 4096
            $step = [Math]::Max(1, [Math]::Floor([Math]::Sqrt($pixelCount / $maxSamples)))
            $sampleCount = 0
            [double]$sumR = 0
            [double]$sumG = 0
            [double]$sumB = 0
            [double]$sumA = 0
            [int]$transparent = 0
            for ($y = 0; $y -lt $height; $y += $step) {
                for ($x = 0; $x -lt $width; $x += $step) {
                    $pixel = $bitmap.GetPixel($x, $y)
                    $sampleCount++
                    $sumR += $pixel.R
                    $sumG += $pixel.G
                    $sumB += $pixel.B
                    $sumA += $pixel.A
                    if ($pixel.A -lt 16) { $transparent++ }
                }
            }
            return [pscustomobject][ordered]@{
                readable = $true
                width = $width
                height = $height
                samples = $sampleCount
                avgR = [Math]::Round($sumR / $sampleCount, 3)
                avgG = [Math]::Round($sumG / $sampleCount, 3)
                avgB = [Math]::Round($sumB / $sampleCount, 3)
                avgA = [Math]::Round($sumA / $sampleCount, 3)
                transparentRatio = [Math]::Round($transparent / [double]$sampleCount, 5)
                error = $null
            }
        } finally {
            $bitmap.Dispose()
        }
    } catch {
        return [pscustomobject][ordered]@{ readable = $false; width = 0; height = 0; samples = 0; avgR = 0; avgG = 0; avgB = 0; avgA = 0; transparentRatio = 0; error = $_.Exception.Message }
    }
}
function Get-ColorDistance($A, $B) {
    $dr = [double]$A.avgR - [double]$B.avgR
    $dg = [double]$A.avgG - [double]$B.avgG
    $db = [double]$A.avgB - [double]$B.avgB
    return [Math]::Round([Math]::Sqrt(($dr * $dr) + ($dg * $dg) + ($db * $db)), 3)
}

$port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
$assetsRoot = Join-Path $portPath "src/main/resources/assets/thaumcraft"
$intentionalRecolorRules = New-TextureRuleLookup (Read-RuleDocument $RulesRoot "intentional-recolor.json")
$textureEquivalenceRules = New-TextureRuleLookup (Read-RuleDocument $RulesRoot "texture-equivalence.json")

$textureRefs = [System.Collections.Generic.Dictionary[string, object]]::new()
foreach ($entry in @($port.entries)) {
    if ($entry.kind -eq "item") {
        $modelPath = Join-Path $assetsRoot "models/item/$($entry.registryId).json"
        foreach ($textureRef in Get-ModelTextureRefs $modelPath) {
            if (-not $textureRefs.ContainsKey($textureRef)) { $textureRefs[$textureRef] = [System.Collections.Generic.List[string]]::new() }
            $textureRefs[$textureRef].Add("item:thaumcraft:$($entry.registryId)")
        }
    } elseif ($entry.kind -eq "block") {
        foreach ($modelRef in @($entry.resources.referencedBlockModels | Where-Object { $_ })) {
            $modelPath = Join-Path $assetsRoot "models/$modelRef.json"
            foreach ($textureRef in Get-ModelTextureRefs $modelPath) {
                if (-not $textureRefs.ContainsKey($textureRef)) { $textureRefs[$textureRef] = [System.Collections.Generic.List[string]]::new() }
                $textureRefs[$textureRef].Add("block:thaumcraft:$($entry.registryId)")
            }
        }
    }
}

$results = [System.Collections.Generic.List[object]]::new()
foreach ($textureRef in @($textureRefs.Keys | Sort-Object)) {
    $portTexture = Resolve-PortTexture $textureRef $assetsRoot
    $usedBy = @($textureRefs[$textureRef] | Sort-Object -Unique)
    if (-not $portTexture.exists) {
        $results.Add([pscustomobject][ordered]@{
            textureRef = $textureRef
            texturePath = $portTexture.texturePath
            status = "PORT_TEXTURE_MISSING"
            source = "port"
            usedBy = @($usedBy)
            evidence = "Referenced texture file missing in port: $($portTexture.texturePath)"
            port = $null
            legacy = $null
            metrics = $null
        })
        continue
    }
    $legacyTexture = Resolve-LegacyTexture $portTexture.texturePath
    if (-not $legacyTexture.exists) {
        $results.Add([pscustomobject][ordered]@{
            textureRef = $textureRef
            texturePath = $portTexture.texturePath
            status = "LEGACY_TEXTURE_MISSING"
            source = $legacyTexture.source
            usedBy = @($usedBy)
            evidence = "Legacy texture not found in primary legacy resources or original jar fallback"
            port = [pscustomobject][ordered]@{ path = ConvertTo-RelativeRepoPath $portTexture.fullPath; sha256 = Get-FileSha256 $portTexture.fullPath }
            legacy = $null
            metrics = $null
        })
        continue
    }

    $portSha = Get-FileSha256 $portTexture.fullPath
    $legacySha = Get-FileSha256 $legacyTexture.fullPath
    $portMetrics = Get-BitmapMetrics $portTexture.fullPath
    $legacyMetrics = Get-BitmapMetrics $legacyTexture.fullPath
    $status = "TEXTURE_REVIEW_NEEDED"
    $evidence = "Texture comparison needs manual review"
    $distance = $null
    $alphaDiff = $null

    if (-not $portMetrics.readable -or -not $legacyMetrics.readable) {
        $status = "TEXTURE_READ_ERROR"
        $evidence = "Could not read texture metrics; portError=$($portMetrics.error); legacyError=$($legacyMetrics.error)"
    } elseif ($portSha -eq $legacySha) {
        $status = "TEXTURE_EXACT_MATCH"
        $evidence = "SHA-256 exact match"
    } elseif ($portMetrics.width -ne $legacyMetrics.width -or $portMetrics.height -ne $legacyMetrics.height) {
        $status = "TEXTURE_SIZE_MISMATCH"
        $evidence = "size port=$($portMetrics.width)x$($portMetrics.height), legacy=$($legacyMetrics.width)x$($legacyMetrics.height)"
    } else {
        $distance = Get-ColorDistance $portMetrics $legacyMetrics
        $alphaDiff = [Math]::Round([Math]::Abs([double]$portMetrics.transparentRatio - [double]$legacyMetrics.transparentRatio), 5)
        if ($alphaDiff -gt 0.03) {
            $status = "TEXTURE_ALPHA_MISMATCH"
            $evidence = "transparentRatioDiff=$alphaDiff; colorDistance=$distance"
        } elseif ($distance -le 6.0) {
            $status = "TEXTURE_SIMILAR_MATCH"
            $evidence = "same size and similar average color; colorDistance=$distance; transparentRatioDiff=$alphaDiff"
        } else {
            $status = "TEXTURE_COLOR_MISMATCH"
            $evidence = "same size but average color differs; colorDistance=$distance; transparentRatioDiff=$alphaDiff"
        }
    }

    $equivalenceRule = Get-TextureRule $textureEquivalenceRules $textureRef $portTexture.texturePath
    $recolorRule = Get-TextureRule $intentionalRecolorRules $textureRef $portTexture.texturePath
    if ($null -ne $equivalenceRule -and $status -in @("TEXTURE_COLOR_MISMATCH", "TEXTURE_ALPHA_MISMATCH", "TEXTURE_SIZE_MISMATCH")) {
        $status = "TEXTURE_EQUIVALENT_BY_RULE"
        $evidence = "$evidence; equivalence rule: $($equivalenceRule.reason)"
    } elseif ($null -ne $recolorRule -and $status -eq "TEXTURE_COLOR_MISMATCH") {
        $status = "INTENTIONAL_VISUAL_DIFFERENCE"
        $evidence = "$evidence; intentional recolor: $($recolorRule.reason)"
    }

    $results.Add([pscustomobject][ordered]@{
        textureRef = $textureRef
        texturePath = $portTexture.texturePath
        status = $status
        source = $legacyTexture.source
        usedBy = @($usedBy)
        evidence = $evidence
        port = [pscustomobject][ordered]@{ path = ConvertTo-RelativeRepoPath $portTexture.fullPath; sha256 = $portSha; metrics = $portMetrics }
        legacy = [pscustomobject][ordered]@{ path = if ($legacyTexture.source -eq "original_jar") { $legacyTexture.archiveEntry } else { ConvertTo-RelativeRepoPath $legacyTexture.fullPath }; sha256 = $legacySha; metrics = $legacyMetrics }
        metrics = [pscustomobject][ordered]@{ colorDistance = $distance; transparentRatioDiff = $alphaDiff }
    })
}

$orderedResults = @($results | Sort-Object status, textureRef)
$summaryByStatus = @($orderedResults | Group-Object status | Sort-Object Name | ForEach-Object { [pscustomobject][ordered]@{ status = $_.Name; count = $_.Count } })
$reviewStatuses = @("PORT_TEXTURE_MISSING", "LEGACY_TEXTURE_MISSING", "TEXTURE_READ_ERROR", "TEXTURE_SIZE_MISMATCH", "TEXTURE_ALPHA_MISMATCH", "TEXTURE_COLOR_MISMATCH", "TEXTURE_REVIEW_NEEDED")
$reviewRows = @($orderedResults | Where-Object { $_.status -in $reviewStatuses })
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    selectedChecks = @("texture_color")
    policy = "Report-only texture/color scan. Exact SHA, dimensions, alpha ratio and sampled average color are compared against primary legacy resources or original jar fallback when available. This does not replace manual screenshot review."
    thresholds = [ordered]@{ similarColorDistanceMax = 6.0; transparentRatioDiffMax = 0.03; maxSamplesPerTexture = 4096 }
    summary = [ordered]@{
        textures = $orderedResults.Count
        reviewNeeded = $reviewRows.Count
        byStatus = @($summaryByStatus)
        legacySourceAvailable = (Test-Path -LiteralPath $legacyPath -PathType Container)
        originalJarAvailable = (Test-Path -LiteralPath $originalJarPath -PathType Leaf)
    }
    results = $orderedResults
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 18 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block texture color parity report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Textures | Review needed | Legacy root available | Original jar available |")
$lines.Add("|---:|---:|---|---|")
$lines.Add("| $($report.summary.textures) | $($report.summary.reviewNeeded) | $($report.summary.legacySourceAvailable) | $($report.summary.originalJarAvailable) |")
$lines.Add("")
$lines.Add("## By status")
$lines.Add("")
$lines.Add("| Status | Count |")
$lines.Add("|---|---:|")
foreach ($row in $summaryByStatus) { $lines.Add("| $($row.status) | $($row.count) |") }
$lines.Add("")
$lines.Add("## Review-needed texture rows")
$lines.Add("")
$lines.Add("| Texture | Status | Used by | Evidence |")
$lines.Add("|---|---|---|---|")
foreach ($row in $reviewRows) {
    $safeEvidence = $row.evidence.Replace("|", "\|")
    $usedBy = (@($row.usedBy) -join ", ").Replace("|", "\|")
    $lines.Add("| ``$($row.textureRef)`` | $($row.status) | $usedBy | $safeEvidence |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Texture color parity report: $OutputMarkdown"
Write-Output "Textures=$($report.summary.textures), reviewNeeded=$($report.summary.reviewNeeded), statuses=$((@($summaryByStatus | ForEach-Object { "$($_.status)=$($_.count)" }) -join ', '))"
