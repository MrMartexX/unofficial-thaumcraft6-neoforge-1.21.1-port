[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [Parameter(Mandatory = $true)][string]$PortManifestPath,
    [Parameter(Mandatory = $true)][string]$LegacySourceRoot,
    [Parameter(Mandatory = $true)][string]$LegacyJarPath,
    [string]$RulesRoot,
    [string[]]$Checks,
    [string]$PortRoot = "05_neoforge_port",
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$LegacySourceRoot = (Resolve-Path $LegacySourceRoot).Path
$LegacyJarPath = (Resolve-Path $LegacyJarPath).Path
$PortManifestPath = (Resolve-Path $PortManifestPath).Path
$portPath = if ([System.IO.Path]::IsPathRooted($PortRoot)) { $PortRoot } else { Join-Path $RepoRoot $PortRoot }
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_legacy_visual_collision_parity_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_legacy_visual_collision_parity_report.md" }

Add-Type -AssemblyName System.IO.Compression.FileSystem
$legacyZip = [System.IO.Compression.ZipFile]::OpenRead($LegacyJarPath)
try {
    $port = Get-Content -Raw -LiteralPath $PortManifestPath | ConvertFrom-Json
    $assetsRoot = Join-Path $portPath "src/main/resources/assets/thaumcraft"
    $portJavaRoot = Join-Path $portPath "src/main/java"
    $tcBlocksPath = Join-Path $portPath "src/main/java/thaumcraft/common/registry/TCBlocks.java"
    $tcBlocksText = if (Test-Path -LiteralPath $tcBlocksPath -PathType Leaf) { Get-Content -Raw -LiteralPath $tcBlocksPath } else { "" }

    function ConvertTo-RelativeRepoPath([string]$FullPath) {
        if ([string]::IsNullOrWhiteSpace($FullPath)) { return "" }
        return [System.IO.Path]::GetRelativePath($RepoRoot, $FullPath).Replace("\", "/")
    }
    function Normalize-Id([object]$Raw) {
        $id = [string]$Raw
        if ([string]::IsNullOrWhiteSpace($id)) { return "" }
        $id = $id.Trim()
        if ($id.Contains(":")) { $id = $id.Split(":", 2)[1] }
        return $id
    }
    function Get-EntryId($Entry) {
        foreach ($name in @("registryId", "id", "name")) {
            $prop = $Entry.PSObject.Properties[$name]
            if ($null -ne $prop -and -not [string]::IsNullOrWhiteSpace([string]$prop.Value)) { return Normalize-Id $prop.Value }
        }
        return ""
    }
    function Get-Array($Value) {
        if ($null -eq $Value) { return @() }
        if ($Value -is [System.Array]) { return @($Value) }
        return @($Value)
    }
    function Get-JsonPropertyNames($Object) {
        if ($null -eq $Object) { return @() }
        return @($Object.PSObject.Properties | ForEach-Object { $_.Name } | Sort-Object -Unique)
    }
    function Normalize-ZipPath([string]$Path) { return $Path.Replace("\", "/").TrimStart("/") }
    function Get-ZipEntry([string]$Path) {
        return $legacyZip.GetEntry((Normalize-ZipPath $Path))
    }
    function Read-ZipTextOrNull([string]$Path) {
        $entry = Get-ZipEntry $Path
        if ($null -eq $entry) { return $null }
        $stream = $entry.Open()
        try {
            $reader = [System.IO.StreamReader]::new($stream, [System.Text.Encoding]::UTF8, $true)
            try { return $reader.ReadToEnd() } finally { $reader.Dispose() }
        } finally { $stream.Dispose() }
    }
    function Read-JsonFileOrNull([string]$Path) {
        if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { return $null }
        try { return Get-Content -Raw -LiteralPath $Path | ConvertFrom-Json } catch { return $null }
    }
    function Read-LegacyJsonOrNull([string]$ZipPath) {
        $text = Read-ZipTextOrNull $ZipPath
        if ($null -eq $text) { return $null }
        try { return $text | ConvertFrom-Json } catch { return $null }
    }
    function Normalize-ModelRef([string]$ModelRef, [string]$DefaultNamespace) {
        if ([string]::IsNullOrWhiteSpace($ModelRef)) { return "" }
        $ref = $ModelRef.Replace("\", "/")
        if ($ref -match '^[a-z0-9_.-]+:') { return $ref }
        return "${DefaultNamespace}:$ref"
    }
    function Split-ModelRef([string]$ModelRef, [string]$DefaultNamespace) {
        $normalized = Normalize-ModelRef $ModelRef $DefaultNamespace
        if (-not $normalized.Contains(":")) { return [pscustomobject]@{ Namespace = $DefaultNamespace; Path = $normalized } }
        $parts = $normalized.Split(":", 2)
        return [pscustomobject]@{ Namespace = $parts[0]; Path = $parts[1] }
    }
    function Test-ArrayEquals([object[]]$Value, [double[]]$Expected) {
        if ($Value.Count -ne $Expected.Count) { return $false }
        for ($i = 0; $i -lt $Expected.Count; $i++) {
            if ([math]::Abs(([double]$Value[$i]) - $Expected[$i]) -gt 0.001) { return $false }
        }
        return $true
    }
    function Format-Bounds($Bounds) {
        if ($null -eq $Bounds) { return "<none>" }
        return "[$($Bounds.minX),$($Bounds.minY),$($Bounds.minZ)]-[$($Bounds.maxX),$($Bounds.maxY),$($Bounds.maxZ)]"
    }
    function Test-BoundsClose($A, $B, [double]$Tolerance = 0.25) {
        if ($null -eq $A -or $null -eq $B) { return $false }
        foreach ($name in @("minX", "minY", "minZ", "maxX", "maxY", "maxZ")) {
            if ([math]::Abs(([double]$A.$name) - ([double]$B.$name)) -gt $Tolerance) { return $false }
        }
        return $true
    }

    function Get-BlockDeclarationChunk([string]$Id) {
        if ([string]::IsNullOrWhiteSpace($tcBlocksText) -or [string]::IsNullOrWhiteSpace($Id)) { return "" }
        $pattern = 'public\s+static\s+final\s+Supplier<Block>\s+[A-Z0-9_]+\s*=\s*BLOCKS\.register\(\s*"' + [regex]::Escape($Id) + '"(?<chunk>.*?)(?=^\s*public\s+static\s+final\s+Supplier<Block>|^\s*private\s+TCBlocks|\z)'
        $match = [regex]::Match($tcBlocksText, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline -bor [System.Text.RegularExpressions.RegexOptions]::Multiline)
        if ($match.Success) { return $match.Groups["chunk"].Value }
        return ""
    }
    function Get-PrivateBlockHelperChunk([string]$HelperName) {
        if ([string]::IsNullOrWhiteSpace($tcBlocksText) -or [string]::IsNullOrWhiteSpace($HelperName)) { return "" }
        $pattern = 'private\s+static\s+Block(?:Behaviour\.Properties)?\s+' + [regex]::Escape($HelperName) + '\s*\([^)]*\)\s*\{'
        $match = [regex]::Match($tcBlocksText, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)
        if (-not $match.Success) { return "" }
        $braceStart = $tcBlocksText.IndexOf("{", $match.Index)
        if ($braceStart -lt 0) { return "" }
        $depth = 0
        for ($i = $braceStart; $i -lt $tcBlocksText.Length; $i++) {
            $ch = $tcBlocksText[$i]
            if ($ch -eq "{") { $depth++ }
            elseif ($ch -eq "}") {
                $depth--
                if ($depth -eq 0) { return $tcBlocksText.Substring($match.Index, ($i - $match.Index + 1)) }
            }
        }
        return ""
    }
    function Get-FactoryText([string]$Id) {
        $chunk = Get-BlockDeclarationChunk $Id
        $builder = [System.Text.StringBuilder]::new()
        [void]$builder.AppendLine($chunk)
        $helperNames = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::Ordinal)
        foreach ($match in [regex]::Matches($chunk, '=>\s*(?<helper>[a-z][A-Za-z0-9_]*)\s*\(')) { [void]$helperNames.Add($match.Groups["helper"].Value) }
        foreach ($helperName in @($helperNames)) {
            $helperChunk = Get-PrivateBlockHelperChunk $helperName
            if (-not [string]::IsNullOrWhiteSpace($helperChunk)) { [void]$builder.AppendLine($helperChunk) }
        }
        return $builder.ToString()
    }
    function Find-PortClassPath([string]$ClassName) {
        if ([string]::IsNullOrWhiteSpace($ClassName) -or -not (Test-Path -LiteralPath $portJavaRoot -PathType Container)) { return "" }
        $hit = Get-ChildItem -LiteralPath $portJavaRoot -Recurse -File -Filter "$ClassName.java" -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($null -eq $hit) { return "" }
        return $hit.FullName
    }
    function Add-PortClassAndParents([System.Collections.Generic.HashSet[string]]$Paths, [string]$ClassPath, [int]$Depth = 0) {
        if ([string]::IsNullOrWhiteSpace($ClassPath) -or -not (Test-Path -LiteralPath $ClassPath -PathType Leaf) -or $Depth -gt 3) { return }
        if (-not $Paths.Add($ClassPath)) { return }
        $text = Get-Content -Raw -LiteralPath $ClassPath
        foreach ($m in [regex]::Matches($text, 'extends\s+(?<class>TC[A-Za-z0-9_]+Block)\b')) {
            $parent = Find-PortClassPath $m.Groups["class"].Value
            Add-PortClassAndParents $Paths $parent ($Depth + 1)
        }
    }
    function Get-ManifestJavaPaths($Entry) {
        $paths = [System.Collections.Generic.List[string]]::new()
        function Visit($Value) {
            if ($null -eq $Value) { return }
            if ($Value -is [string]) {
                $s = [string]$Value
                if ($s -match '\.java$') { $paths.Add($s) }
                return
            }
            if ($Value -is [System.Collections.IEnumerable] -and -not ($Value -is [string])) {
                foreach ($item in $Value) { Visit $item }
                return
            }
            if ($Value.PSObject -and $Value.PSObject.Properties) {
                foreach ($p in $Value.PSObject.Properties) { Visit $p.Value }
            }
        }
        Visit $Entry
        return @($paths | Sort-Object -Unique)
    }
    function Get-PortEvidence($Entry, [string]$Id) {
        $factory = Get-FactoryText $Id
        $pathSet = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
        foreach ($manifestPath in Get-ManifestJavaPaths $Entry) {
            $candidate = if ([System.IO.Path]::IsPathRooted($manifestPath)) { $manifestPath } else { Join-Path $portPath $manifestPath }
            if (Test-Path -LiteralPath $candidate -PathType Leaf) { [void]$pathSet.Add($candidate) }
        }
        foreach ($m in [regex]::Matches($factory, 'new\s+(?<class>[A-Z][A-Za-z0-9_]+)\s*\(')) {
            $className = $m.Groups["class"].Value
            $classPath = Find-PortClassPath $className
            Add-PortClassAndParents $pathSet $classPath 0
        }
        $sourceBuilder = [System.Text.StringBuilder]::new()
        foreach ($path in @($pathSet | Sort-Object)) { [void]$sourceBuilder.AppendLine((Get-Content -Raw -LiteralPath $path)) }
        $all = "$factory`n$($sourceBuilder.ToString())"
        $builtinNoCollisionCopy = $all -match 'ofFullCopy\(Blocks\.(CANDLE|OAK_SAPLING|SHORT_GRASS|WATER)\)'
        $builtinShapeClass = $all -match '\bnew\s+(StairBlock|SlabBlock|FenceBlock|WallBlock|DoorBlock|TrapDoorBlock|PaneBlock)\b|\bextends\s+(StairBlock|SlabBlock|FenceBlock|WallBlock|DoorBlock|TrapDoorBlock|PaneBlock)\b'
        return [pscustomobject]@{
            sourceFiles = @($pathSet | Sort-Object | ForEach-Object { ConvertTo-RelativeRepoPath $_ })
            factoryResolved = -not [string]::IsNullOrWhiteSpace($factory)
            sourceResolved = $pathSet.Count -gt 0
            hasSelectionShapeContract = ($all -match '\bgetShape\s*\(|\bVoxelShape\b|\bShapes\.or\b|\bBlock\.box\s*\(|(?<![A-Za-z0-9_.])box\s*\(') -or $builtinShapeClass -or $builtinNoCollisionCopy
            hasCollisionShapeContract = ($all -match '\bgetCollisionShape\s*\(|\bVoxelShape\b|\bShapes\.or\b|\.noCollission\s*\(|\bBlock\.box\s*\(|(?<![A-Za-z0-9_.])box\s*\(') -or $builtinShapeClass -or $builtinNoCollisionCopy
            hasShapeContract = ($all -match '\bgetShape\s*\(|\bgetCollisionShape\s*\(|\bVoxelShape\b|\bShapes\.or\b|\bBlock\.box\s*\(|(?<![A-Za-z0-9_.])box\s*\(|\.noCollission\s*\(') -or $builtinShapeClass -or $builtinNoCollisionCopy
            hasOcclusionContract = ($all -match '\bgetOcclusionShape\s*\(|\buseShapeForLightOcclusion\s*\(|\.noOcclusion\s*\(|\.noCollission\s*\(') -or $builtinShapeClass -or $builtinNoCollisionCopy
            hasBoxConstants = $all -match '\bBlock\.box\s*\(|(?<![A-Za-z0-9_.])box\s*\('
            noCollision = ($all -match '\.noCollission\s*\(') -or $builtinNoCollisionCopy
            resolvedText = -not [string]::IsNullOrWhiteSpace($all)
        }
    }

    $modelCache = @{}
    function Read-ModelJson([string]$Side, [string]$ModelRef) {
        $split = Split-ModelRef $ModelRef "thaumcraft"
        if ($split.Namespace -eq "minecraft") { return $null }
        $relative = "models/$($split.Path).json"
        if ($Side -eq "port") {
            $path = Join-Path $assetsRoot $relative
            $displayPath = if (Test-Path -LiteralPath $path -PathType Leaf) { ConvertTo-RelativeRepoPath $path } else { "05_neoforge_port/src/main/resources/assets/thaumcraft/$relative" }
            return [pscustomobject]@{ Json = (Read-JsonFileOrNull $path); Path = $displayPath }
        }
        $zipPath = "assets/thaumcraft/$relative"
        return [pscustomobject]@{ Json = (Read-LegacyJsonOrNull $zipPath); Path = $zipPath }
    }
    function Get-ModelAnalysis([string]$Side, [string]$ModelRef, [int]$Depth = 0) {
        if ([string]::IsNullOrWhiteSpace($ModelRef)) { return $null }
        $cacheKey = "$Side|$ModelRef"
        if ($modelCache.ContainsKey($cacheKey)) { return $modelCache[$cacheKey] }
        if ($Depth -gt 8) { return $null }
        $split = Split-ModelRef $ModelRef "thaumcraft"
        $normalized = "$($split.Namespace):$($split.Path)"
        $jsonRecord = Read-ModelJson $Side $normalized
        $json = if ($null -ne $jsonRecord) { $jsonRecord.Json } else { $null }
        $path = if ($null -ne $jsonRecord) { $jsonRecord.Path } else { $normalized }
        $parentLower = ""
        if ($null -eq $json -and $split.Namespace -eq "minecraft") { $parentLower = $split.Path.ToLowerInvariant() }
        elseif ($null -ne $json -and $json.parent) { $parentLower = ([string]$json.parent).ToLowerInvariant() }
        $elements = if ($null -ne $json) { @(Get-Array $json.elements) } else { @() }
        if ($elements.Count -eq 0 -and $null -ne $json -and $json.parent -and ([string]$json.parent) -match '^thaumcraft:') {
            $parentAnalysis = Get-ModelAnalysis $Side ([string]$json.parent) ($Depth + 1)
            if ($null -ne $parentAnalysis) {
                $analysis = [pscustomobject]@{
                    side = $Side; modelRef = $normalized; path = $path; exists = $true; parent = [string]$json.parent
                    hasElements = $parentAnalysis.hasElements; elementCount = $parentAnalysis.elementCount
                    hasFullCubeElement = $parentAnalysis.hasFullCubeElement; likelyNonFull = $parentAnalysis.likelyNonFull
                    hasNorthProjection = $parentAnalysis.hasNorthProjection; bounds = $parentAnalysis.bounds; inheritedFrom = $parentAnalysis.modelRef
                }
                $modelCache[$cacheKey] = $analysis
                return $analysis
            }
        }
        $hasFullCubeElement = $false
        $hasNorthProjection = $false
        $bounds = $null
        foreach ($element in $elements) {
            $from = @(Get-Array $element.from)
            $to = @(Get-Array $element.to)
            if ($from.Count -ne 3 -or $to.Count -ne 3) { continue }
            $minX = [double]$from[0]; $minY = [double]$from[1]; $minZ = [double]$from[2]
            $maxX = [double]$to[0]; $maxY = [double]$to[1]; $maxZ = [double]$to[2]
            $isFullCube = (Test-ArrayEquals $from @(0, 0, 0)) -and (Test-ArrayEquals $to @(16, 16, 16))
            if ($isFullCube) { $hasFullCubeElement = $true }
            if ($null -eq $bounds) { $bounds = [pscustomobject]@{ minX = $minX; minY = $minY; minZ = $minZ; maxX = $maxX; maxY = $maxY; maxZ = $maxZ } }
            else {
                $bounds.minX = [math]::Min($bounds.minX, $minX); $bounds.minY = [math]::Min($bounds.minY, $minY); $bounds.minZ = [math]::Min($bounds.minZ, $minZ)
                $bounds.maxX = [math]::Max($bounds.maxX, $maxX); $bounds.maxY = [math]::Max($bounds.maxY, $maxY); $bounds.maxZ = [math]::Max($bounds.maxZ, $maxZ)
            }
            $widthX = $maxX - $minX; $heightY = $maxY - $minY
            if ($minZ -le 0.001 -and $maxZ -le 4.001 -and $widthX -lt 14.0 -and $heightY -lt 14.0 -and -not $isFullCube) { $hasNorthProjection = $true }
        }
        $parentImpliesFullCube = $parentLower -match '(^|:)block/(cube|cube_all|cube_column|orientable|log|column)$|^block/(cube|cube_all|cube_column|orientable|log|column)$'
        $parentImpliesBuiltInNonFull = $parentLower -match 'stairs|slab|cross|crop|torch|fence|wall|door|trapdoor|pane'
        $likelyNonFull = $false
        if ($elements.Count -gt 0) { $likelyNonFull = -not $hasFullCubeElement }
        elseif ($parentImpliesBuiltInNonFull) { $likelyNonFull = $true }
        elseif ($parentImpliesFullCube) { $likelyNonFull = $false }
        $exists = $null -ne $json -or $split.Namespace -eq "minecraft"
        $analysis = [pscustomobject]@{
            side = $Side; modelRef = $normalized; path = $path; exists = $exists
            parent = if ($null -ne $json -and $json.parent) { [string]$json.parent } elseif ($split.Namespace -eq "minecraft") { $normalized } else { "<missing>" }
            hasElements = $elements.Count -gt 0; elementCount = $elements.Count; hasFullCubeElement = $hasFullCubeElement
            likelyNonFull = $likelyNonFull; hasNorthProjection = $hasNorthProjection; bounds = $bounds; inheritedFrom = ""
        }
        $modelCache[$cacheKey] = $analysis
        return $analysis
    }

    function Get-ModelRefsFromBlockstateJson($Json) {
        $refs = [System.Collections.Generic.List[string]]::new()
        if ($null -eq $Json) { return @() }
        if ($Json.variants) {
            foreach ($prop in $Json.variants.PSObject.Properties) {
                foreach ($variant in @(Get-Array $prop.Value)) { if ($variant.model) { $refs.Add([string]$variant.model) } }
            }
        }
        if ($Json.multipart) {
            foreach ($part in @(Get-Array $Json.multipart)) {
                foreach ($apply in @(Get-Array $part.apply)) { if ($apply.model) { $refs.Add([string]$apply.model) } }
            }
        }
        return @($refs | Sort-Object -Unique)
    }
    function Get-VariantMap($Json) {
        $map = @{}
        if ($null -eq $Json -or $null -eq $Json.variants) { return $map }
        foreach ($prop in $Json.variants.PSObject.Properties) { $map[[string]$prop.Name] = @(Get-Array $prop.Value) }
        return $map
    }
    function Get-FacingDomain($VariantMap) {
        $domain = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
        foreach ($key in $VariantMap.Keys) {
            foreach ($m in [regex]::Matches($key, '(^|,)facing=(?<facing>north|south|east|west|up|down)(,|$)')) { [void]$domain.Add($m.Groups["facing"].Value.ToLowerInvariant()) }
        }
        return @($domain | Sort-Object)
    }
    function Get-PortBlockstateJson([string]$Id) {
        $path = Join-Path $assetsRoot "blockstates/$Id.json"
        $displayPath = if (Test-Path -LiteralPath $path -PathType Leaf) { ConvertTo-RelativeRepoPath $path } else { "05_neoforge_port/src/main/resources/assets/thaumcraft/blockstates/$Id.json" }
        return [pscustomobject]@{ Json = (Read-JsonFileOrNull $path); Path = $displayPath }
    }
    function Get-LegacyIdAliases([string]$Id) {
        $aliases = [System.Collections.Generic.List[string]]::new()
        $aliases.Add($Id)
        switch ($Id) {
            "arcane_pedestal" { $aliases.Add("pedestal_normal"); $aliases.Add("pedestal_arcane") }
            "ancient_pedestal" { $aliases.Add("pedestal_ancient") }
            "eldritch_pedestal" { $aliases.Add("pedestal_eldritch") }
            "essentiatransportin" { $aliases.Add("essentia_input") }
            "essentiatransportout" { $aliases.Add("essentia_output") }
            "jar_normal" { $aliases.Add("jar") }
        }
        return @($aliases | Sort-Object -Unique)
    }
    function Get-LegacyBlockstateJson([string]$Id) {
        foreach ($candidate in Get-LegacyIdAliases $Id) {
            $path = "assets/thaumcraft/blockstates/$candidate.json"
            $json = Read-LegacyJsonOrNull $path
            if ($null -ne $json) { return [pscustomobject]@{ Json = $json; Path = $path; Id = $candidate } }
        }
        return [pscustomobject]@{ Json = $null; Path = "assets/thaumcraft/blockstates/$Id.json"; Id = $Id }
    }
    function Get-PortModelRefs($Entry, [string]$Id) {
        $refs = @()
        if ($Entry.resources -and $Entry.resources.referencedBlockModels) { $refs = @($Entry.resources.referencedBlockModels | Where-Object { $_ } | Sort-Object -Unique) }
        if ($refs.Count -eq 0) {
            $bs = Get-PortBlockstateJson $Id
            $refs = Get-ModelRefsFromBlockstateJson $bs.Json
        }
        return @($refs | ForEach-Object { Normalize-ModelRef ([string]$_) "thaumcraft" } | Sort-Object -Unique)
    }
    function Get-LegacyModelRefs([string]$Id) {
        $bs = Get-LegacyBlockstateJson $Id
        $refs = Get-ModelRefsFromBlockstateJson $bs.Json
        if ($refs.Count -eq 0) {
            foreach ($candidate in Get-LegacyIdAliases $Id) {
                if ($null -ne (Read-LegacyJsonOrNull "assets/thaumcraft/models/block/$candidate.json")) { $refs += "thaumcraft:block/$candidate" }
            }
        }
        return [pscustomobject]@{ blockstate = $bs; refs = @($refs | ForEach-Object { Normalize-ModelRef ([string]$_) "thaumcraft" } | Sort-Object -Unique) }
    }

    function Add-LegacySourcePath([System.Collections.Generic.List[string]]$List, [string]$RelativePath) {
        $full = Join-Path $LegacySourceRoot $RelativePath
        if (Test-Path -LiteralPath $full -PathType Leaf) { $List.Add($full) }
    }
    function Get-LegacySourcePathsStrict([string]$Id) {
        $paths = [System.Collections.Generic.List[string]]::new()
        if ($Id -like "candle_*") { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/basic/BlockCandle.java" }
        elseif ($Id -like "crystal_*") { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/world/ore/BlockCrystal.java" }
        elseif ($Id -like "nitor_*") { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/misc/BlockNitor.java" }
        elseif ($Id -like "sapling_*") { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/world/plants/BlockSaplingTC.java" }
        elseif ($Id -like "tube*") { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/essentia/BlockTube.java" }
        elseif ($Id -like "stairs_*") { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/basic/BlockStairsTC.java" }
        elseif ($Id -like "slab_*") { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/basic/BlockSlabTC.java" }
        else {
            switch ($Id) {
                "alembic" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/essentia/BlockAlembic.java" }
                "arcane_pedestal" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/devices/BlockPedestal.java" }
                "ancient_pedestal" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/devices/BlockPedestal.java" }
                "eldritch_pedestal" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/devices/BlockPedestal.java" }
                "arcane_workbench" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/crafting/BlockArcaneWorkbench.java" }
                "arcane_workbench_charger" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/crafting/BlockArcaneWorkbenchCharger.java" }
                "bellows" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/devices/BlockBellows.java" }
                "crucible" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/crafting/BlockCrucible.java" }
                "essentiatransportin" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/essentia/BlockEssentiaTransport.java" }
                "essentiatransportout" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/essentia/BlockEssentiaTransport.java" }
                "golem_builder" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/crafting/BlockGolemBuilder.java" }
                "infusion_matrix" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/crafting/BlockInfusionMatrix.java" }
                "matrix_cost" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/crafting/BlockInfusionMatrix.java" }
                "matrix_speed" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/crafting/BlockInfusionMatrix.java" }
                "inlay" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/devices/BlockInlay.java" }
                "jar_normal" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/essentia/BlockJar.java" }
                "research_table" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/crafting/BlockResearchTable.java" }
                "table_wood" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/basic/BlockTable.java" }
                "table_stone" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/basic/BlockTable.java" }
                "smelter_aux" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/essentia/BlockSmelterAux.java" }
                "smelter_vent" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/essentia/BlockSmelterVent.java" }
                "smelter_basic" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/essentia/BlockSmelter.java" }
                "smelter_thaumium" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/essentia/BlockSmelter.java" }
                "smelter_void" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/essentia/BlockSmelter.java" }
                "stabilizer" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/devices/BlockStabilizer.java" }
                "thaumatorium" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java" }
                "thaumatorium_top" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/crafting/BlockThaumatorium.java" }
                "wand_workbench" { Add-LegacySourcePath $paths "src/main/java/thaumcraft/common/blocks/crafting/BlockArcaneWorkbench.java" }
            }
        }
        return @($paths | Sort-Object -Unique)
    }
    function Get-LegacySourceEvidence([string]$Id) {
        $paths = @(Get-LegacySourcePathsStrict $Id)
        $builder = [System.Text.StringBuilder]::new()
        foreach ($path in $paths) { [void]$builder.AppendLine((Get-Content -Raw -LiteralPath $path)) }
        $all = $builder.ToString()
        return [pscustomobject]@{
            sourceFiles = @($paths | ForEach-Object { ConvertTo-RelativeRepoPath $_ })
            hasExactSource = $paths.Count -gt 0
            hasExplicitNonOpaque = $all -match 'isOpaqueCube\s*\([^)]*\)\s*\{[^}]*return\s+false\s*;' -or $all -match 'isFullBlock\s*\([^)]*\)\s*\{[^}]*return\s+false\s*;' -or $all -match 'isFullCube\s*\([^)]*\)\s*\{[^}]*return\s+false\s*;'
            hasExplicitNonFullCube = $all -match 'isFullCube\s*\([^)]*\)\s*\{[^}]*return\s+false\s*;' -or $all -match 'isFullBlock\s*\([^)]*\)\s*\{[^}]*return\s+false\s*;'
            hasExplicitBounds = $all -match 'getBoundingBox\s*\(|getCollisionBoundingBox\s*\(|AxisAlignedBB\s+|setBlockBounds\s*\('
            hasExplicitOutlineBounds = $all -match 'getBoundingBox\s*\(|getSelectedBoundingBox\s*\(|AxisAlignedBB\s+|setBlockBounds\s*\('
            hasExplicitNoCollision = $all -match 'getCollisionBoundingBox\s*\([^)]*\)\s*\{[^}]*return\s+NULL_AABB\s*;' -or $all -match 'getCollisionBoundingBox\s*\([^)]*\)\s*\{[^}]*return\s+null\s*;'
            facingContract = if ($all -match '\bimplements\s+[^{]*\bIBlockFacingHorizontal\b') { "horizontal" } elseif ($all -match '\bimplements\s+[^{]*\bIBlockFacing\b') { "all" } else { "unspecified" }
        }
    }

    function Add-ResultRow($Rows, [string]$Kind, [string]$Id, [string]$Subcheck, [string]$Status, [string]$Evidence, [string]$LegacyPath = "", [string]$PortPath = "", $Details = $null) {
        $Rows.Add([pscustomobject][ordered]@{
            check = "legacy_visual_collision_parity"; subcheck = $Subcheck; kind = $Kind; id = "thaumcraft:$Id"; status = $Status
            legacyPath = $LegacyPath; portPath = $PortPath; evidence = $Evidence; details = $Details
        })
    }

    $results = [System.Collections.Generic.List[object]]::new()
    foreach ($entry in @($port.entries | Where-Object { $_.kind -eq "block" } | Sort-Object { Get-EntryId $_ })) {
        $id = Get-EntryId $entry
        if ([string]::IsNullOrWhiteSpace($id)) { continue }
        $portEvidence = Get-PortEvidence $entry $id
        $legacySource = Get-LegacySourceEvidence $id
        $legacyModels = Get-LegacyModelRefs $id
        $portRefs = Get-PortModelRefs $entry $id
        $legacyRefs = @($legacyModels.refs)

        if ($legacyRefs.Count -eq 0) {
            Add-ResultRow $results "block" $id "visual_model_geometry" "LEGACY_PARITY_UNKNOWN" "No exact legacy blockstate/model resource found for this registry id or known aliases; no mismatch asserted." $legacyModels.blockstate.Path "" @{ legacyAliases = @(Get-LegacyIdAliases $id) }
        } elseif ($portRefs.Count -eq 0) {
            Add-ResultRow $results "block" $id "visual_model_geometry" "LEGACY_PARITY_MISSING" "Legacy model exists but no port block model references were found." ($legacyRefs -join ", ") "" @{ legacyModels = $legacyRefs; portModels = $portRefs }
        } else {
            $legacyAnalyses = @($legacyRefs | ForEach-Object { Get-ModelAnalysis "legacy" $_ } | Where-Object { $_ })
            $portAnalyses = @($portRefs | ForEach-Object { Get-ModelAnalysis "port" $_ } | Where-Object { $_ })
            $legacyNonFull = @($legacyAnalyses | Where-Object { $_.likelyNonFull }).Count -gt 0
            $portNonFull = @($portAnalyses | Where-Object { $_.likelyNonFull }).Count -gt 0
            $legacyMissing = @($legacyAnalyses | Where-Object { -not $_.exists }).Count -gt 0
            $portMissing = @($portAnalyses | Where-Object { -not $_.exists }).Count -gt 0
            $legacyBounds = @($legacyAnalyses | Where-Object { $_.bounds } | Select-Object -First 1).bounds
            $portBounds = @($portAnalyses | Where-Object { $_.bounds } | Select-Object -First 1).bounds
            if ($legacyMissing) {
                Add-ResultRow $results "block" $id "visual_model_geometry" "LEGACY_PARITY_UNKNOWN" "Legacy blockstate references models, but at least one legacy model could not be read; no mismatch asserted." ($legacyRefs -join ", ") ($portRefs -join ", ") @{ legacyModels = $legacyAnalyses; portModels = $portAnalyses }
            } elseif ($portMissing) {
                Add-ResultRow $results "block" $id "visual_model_geometry" "LEGACY_PARITY_MISSING" "Port blockstate references models, but at least one port model could not be read." ($legacyRefs -join ", ") ($portRefs -join ", ") @{ legacyModels = $legacyAnalyses; portModels = $portAnalyses }
            } elseif ($legacyNonFull -ne $portNonFull) {
                Add-ResultRow $results "block" $id "visual_model_geometry" "LEGACY_PARITY_MISMATCH" "Legacy non-full visual geometry flag differs from port. legacyNonFull=$legacyNonFull, portNonFull=$portNonFull." ($legacyRefs -join ", ") ($portRefs -join ", ") @{ legacyModels = $legacyAnalyses; portModels = $portAnalyses }
            } elseif ($null -ne $legacyBounds -and $null -ne $portBounds -and -not (Test-BoundsClose $legacyBounds $portBounds)) {
                Add-ResultRow $results "block" $id "visual_model_geometry" "LEGACY_PARITY_MISMATCH" "Legacy/port model union bounds differ. legacy=$(Format-Bounds $legacyBounds), port=$(Format-Bounds $portBounds)." ($legacyRefs -join ", ") ($portRefs -join ", ") @{ legacyModels = $legacyAnalyses; portModels = $portAnalyses }
            } else {
                Add-ResultRow $results "block" $id "visual_model_geometry" "LEGACY_PARITY_MATCH" "Legacy and port model geometry class/bounds match within tolerance." ($legacyRefs -join ", ") ($portRefs -join ", ") @{ legacyModels = $legacyAnalyses; portModels = $portAnalyses }
            }
        }

        $portPathSummary = @($portEvidence.sourceFiles) -join ", "
        if (-not $legacySource.hasExactSource) {
            Add-ResultRow $results "block" $id "occlusion_contract" "LEGACY_PARITY_UNKNOWN" "No strict identity legacy source mapping for this block; no mismatch asserted." "" $portPathSummary
            Add-ResultRow $results "block" $id "outline_contract" "LEGACY_PARITY_UNKNOWN" "No strict identity legacy source mapping for this block; no mismatch asserted." "" $portPathSummary
            Add-ResultRow $results "block" $id "collision_contract" "LEGACY_PARITY_UNKNOWN" "No strict identity legacy source mapping for this block; no mismatch asserted." "" $portPathSummary
        } else {
            $legacyPathSummary = @($legacySource.sourceFiles) -join ", "
            if ($legacySource.hasExplicitNonOpaque) {
                if ($portEvidence.hasOcclusionContract) { Add-ResultRow $results "block" $id "occlusion_contract" "LEGACY_PARITY_MATCH" "Legacy exact source marks block non-opaque/non-full and port has mechanical occlusion evidence." $legacyPathSummary $portPathSummary }
                elseif (-not $portEvidence.factoryResolved -and -not $portEvidence.sourceResolved) { Add-ResultRow $results "block" $id "occlusion_contract" "LEGACY_PARITY_UNKNOWN" "Legacy exact source marks non-opaque/non-full, but port source/factory could not be resolved; no mismatch asserted." $legacyPathSummary $portPathSummary }
                else { Add-ResultRow $results "block" $id "occlusion_contract" "LEGACY_PARITY_MISMATCH" "Legacy exact source marks block non-opaque/non-full, but port lacks noOcclusion/getOcclusionShape/useShapeForLightOcclusion/no-collision/built-in evidence." $legacyPathSummary $portPathSummary }
            } else {
                Add-ResultRow $results "block" $id "occlusion_contract" "LEGACY_PARITY_UNKNOWN" "Exact legacy source was found, but it has no explicit non-opaque evidence; no mismatch asserted." $legacyPathSummary $portPathSummary
            }
            if ($legacySource.hasExplicitOutlineBounds -or $legacySource.hasExplicitNonFullCube) {
                if ($portEvidence.hasSelectionShapeContract) { Add-ResultRow $results "block" $id "outline_contract" "LEGACY_PARITY_MATCH" "Legacy exact source has outline/bounds/non-full evidence and port has getShape/VoxelShape/Block.box/built-in shape evidence." $legacyPathSummary $portPathSummary }
                elseif (-not $portEvidence.factoryResolved -and -not $portEvidence.sourceResolved) { Add-ResultRow $results "block" $id "outline_contract" "LEGACY_PARITY_UNKNOWN" "Legacy exact source has outline/bounds/non-full evidence, but port source/factory could not be resolved; no mismatch asserted." $legacyPathSummary $portPathSummary }
                else { Add-ResultRow $results "block" $id "outline_contract" "LEGACY_PARITY_MISMATCH" "Legacy exact source has outline/bounds/non-full evidence, but port lacks getShape/VoxelShape/Block.box/built-in shape evidence; in-game selection outline may be a full cube/default shape." $legacyPathSummary $portPathSummary }
            } else {
                Add-ResultRow $results "block" $id "outline_contract" "LEGACY_PARITY_UNKNOWN" "Exact legacy source was found, but it has no explicit outline/bounds evidence; no mismatch asserted." $legacyPathSummary $portPathSummary
            }
            if ($legacySource.hasExplicitNoCollision) {
                if ($portEvidence.noCollision) { Add-ResultRow $results "block" $id "collision_contract" "LEGACY_PARITY_MATCH" "Legacy exact source has no collision and port has no-collision evidence." $legacyPathSummary $portPathSummary }
                elseif (-not $portEvidence.factoryResolved -and -not $portEvidence.sourceResolved) { Add-ResultRow $results "block" $id "collision_contract" "LEGACY_PARITY_UNKNOWN" "Legacy exact source has no collision, but port source/factory could not be resolved; no mismatch asserted." $legacyPathSummary $portPathSummary }
                else { Add-ResultRow $results "block" $id "collision_contract" "LEGACY_PARITY_MISMATCH" "Legacy exact source has no collision, but port lacks noCollission/known no-collision inherited property evidence." $legacyPathSummary $portPathSummary }
            } elseif ($legacySource.hasExplicitBounds -or $legacySource.hasExplicitNonFullCube) {
                if ($portEvidence.hasCollisionShapeContract) { Add-ResultRow $results "block" $id "collision_contract" "LEGACY_PARITY_MATCH" "Legacy exact source has bounds/non-full evidence and port has collision shape/no-collision/built-in evidence." $legacyPathSummary $portPathSummary }
                elseif (-not $portEvidence.factoryResolved -and -not $portEvidence.sourceResolved) { Add-ResultRow $results "block" $id "collision_contract" "LEGACY_PARITY_UNKNOWN" "Legacy exact source has bounds/non-full evidence, but port source/factory could not be resolved; no mismatch asserted." $legacyPathSummary $portPathSummary }
                else { Add-ResultRow $results "block" $id "collision_contract" "LEGACY_PARITY_MISMATCH" "Legacy exact source has bounds/non-full evidence, but port lacks getShape/getCollisionShape/VoxelShape/no-collision/built-in evidence." $legacyPathSummary $portPathSummary }
            } else {
                Add-ResultRow $results "block" $id "collision_contract" "LEGACY_PARITY_UNKNOWN" "Exact legacy source was found, but it has no explicit collision/bounds evidence; no mismatch asserted." $legacyPathSummary $portPathSummary
            }
        }

        $legacyBs = $legacyModels.blockstate
        $portBs = Get-PortBlockstateJson $id
        $legacyVariantMap = Get-VariantMap $legacyBs.Json
        $portVariantMap = Get-VariantMap $portBs.Json
        $legacyFacingDomain = Get-FacingDomain $legacyVariantMap
        if ($legacySource.facingContract -eq "horizontal") {
            $legacyFacingDomain = @($legacyFacingDomain | Where-Object { $_ -in @("north", "south", "east", "west") })
        }
        $portFacingDomain = Get-FacingDomain $portVariantMap
        if ($legacyFacingDomain.Count -gt 0 -or $portFacingDomain.Count -gt 0) {
            $legacyDomainString = $legacyFacingDomain -join ","
            $portDomainString = $portFacingDomain -join ","
            if ($legacyDomainString -ne $portDomainString) { Add-ResultRow $results "block" $id "facing_domain" "LEGACY_PARITY_MISMATCH" "Legacy/port facing domains differ. legacy=[$legacyDomainString], port=[$portDomainString]." $legacyBs.Path $portBs.Path @{ legacyFacing = $legacyFacingDomain; portFacing = $portFacingDomain; legacyFacingContract = $legacySource.facingContract } }
            else { Add-ResultRow $results "block" $id "facing_domain" "LEGACY_PARITY_MATCH" "Legacy/port facing domains match: [$legacyDomainString]." $legacyBs.Path $portBs.Path @{ legacyFacing = $legacyFacingDomain; portFacing = $portFacingDomain; legacyFacingContract = $legacySource.facingContract } }
        }
    }

    foreach ($entry in @($port.entries | Where-Object { $_.kind -eq "item" -and $_.blockItem } | Sort-Object { Get-EntryId $_ })) {
        $id = Get-EntryId $entry
        if ([string]::IsNullOrWhiteSpace($id)) { continue }
        $portItemPath = Join-Path $assetsRoot "models/item/$id.json"
        $portItem = Read-JsonFileOrNull $portItemPath
        $legacyItem = $null
        $legacyItemPath = ""
        foreach ($candidate in Get-LegacyIdAliases $id) {
            $candidatePath = "assets/thaumcraft/models/item/$candidate.json"
            $candidateJson = Read-LegacyJsonOrNull $candidatePath
            if ($null -ne $candidateJson) { $legacyItem = $candidateJson; $legacyItemPath = $candidatePath; break }
        }
        if ($null -eq $legacyItem) { Add-ResultRow $results "item" $id "item_display_transform" "LEGACY_PARITY_UNKNOWN" "No legacy item model found for registry id or aliases; no mismatch asserted." "assets/thaumcraft/models/item/$id.json" ""; continue }
        if ($null -eq $portItem) { Add-ResultRow $results "item" $id "item_display_transform" "LEGACY_PARITY_MISSING" "Legacy item model exists, but port item model is missing or invalid." $legacyItemPath (ConvertTo-RelativeRepoPath $portItemPath); continue }
        $legacyDisplay = @(Get-JsonPropertyNames $legacyItem.display)
        $portDisplay = @(Get-JsonPropertyNames $portItem.display)
        $legacyDisplayString = $legacyDisplay -join ","
        $portDisplayString = $portDisplay -join ","
        if ($legacyDisplay.Count -gt 0 -and $portDisplay.Count -eq 0) { Add-ResultRow $results "item" $id "item_display_transform" "LEGACY_PARITY_MISMATCH" "Legacy item model has explicit display transforms [$legacyDisplayString], but port item model has none." $legacyItemPath (ConvertTo-RelativeRepoPath $portItemPath) }
        elseif ($legacyDisplay.Count -gt 0 -and $legacyDisplayString -ne $portDisplayString) { Add-ResultRow $results "item" $id "item_display_transform" "LEGACY_PARITY_MISMATCH" "Legacy/port item display transform slot sets differ. legacy=[$legacyDisplayString], port=[$portDisplayString]." $legacyItemPath (ConvertTo-RelativeRepoPath $portItemPath) }
        else { Add-ResultRow $results "item" $id "item_display_transform" "LEGACY_PARITY_MATCH" "Legacy/port item display transform requirement matches. legacy=[$legacyDisplayString], port=[$portDisplayString]." $legacyItemPath (ConvertTo-RelativeRepoPath $portItemPath) }
    }

    $orderedResults = @($results | Sort-Object status, kind, id, subcheck)
    $summaryBySubcheck = @($orderedResults | Group-Object subcheck | Sort-Object Name | ForEach-Object {
        [pscustomobject][ordered]@{
            subcheck = $_.Name; rows = $_.Count
            match = @($_.Group | Where-Object status -eq "LEGACY_PARITY_MATCH").Count
            mismatch = @($_.Group | Where-Object status -eq "LEGACY_PARITY_MISMATCH").Count
            missing = @($_.Group | Where-Object status -eq "LEGACY_PARITY_MISSING").Count
            unknown = @($_.Group | Where-Object status -eq "LEGACY_PARITY_UNKNOWN").Count
        }
    })
    $report = [ordered]@{
        schemaVersion = 2
        generatedAtUtc = [DateTime]::UtcNow.ToString("o")
        selectedChecks = @("legacy_visual_collision_parity")
        policy = "Strict identity-based legacy-vs-port parity audit. Source-based MISMATCH requires an exact curated/manifest identity mapping. UNKNOWN is not a failure and means this audit refused to infer parity from broad token matches."
        inputs = [ordered]@{ repoRoot = $RepoRoot; portManifestPath = $PortManifestPath; legacySourceRoot = $LegacySourceRoot; legacyJarPath = $LegacyJarPath }
        summary = [ordered]@{
            rows = $orderedResults.Count
            match = @($orderedResults | Where-Object status -eq "LEGACY_PARITY_MATCH").Count
            mismatch = @($orderedResults | Where-Object status -eq "LEGACY_PARITY_MISMATCH").Count
            missing = @($orderedResults | Where-Object status -eq "LEGACY_PARITY_MISSING").Count
            unknown = @($orderedResults | Where-Object status -eq "LEGACY_PARITY_UNKNOWN").Count
            bySubcheck = @($summaryBySubcheck)
        }
        results = $orderedResults
    }

    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
    $report | ConvertTo-Json -Depth 32 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

    $lines = [System.Collections.Generic.List[string]]::new()
    $lines.Add("# Legacy visual/collision parity report")
    $lines.Add("")
    $lines.Add("Generated: $($report.generatedAtUtc)")
    $lines.Add("")
    $lines.Add("Policy: $($report.policy)")
    $lines.Add("")
    $lines.Add("## Summary")
    $lines.Add("")
    $lines.Add("| Rows | Match | Mismatch | Missing | Unknown |")
    $lines.Add("|---:|---:|---:|---:|---:|")
    $lines.Add("| $($report.summary.rows) | $($report.summary.match) | $($report.summary.mismatch) | $($report.summary.missing) | $($report.summary.unknown) |")
    $lines.Add("")
    $lines.Add("## By subcheck")
    $lines.Add("")
    $lines.Add("| Subcheck | Rows | Match | Mismatch | Missing | Unknown |")
    $lines.Add("|---|---:|---:|---:|---:|---:|")
    foreach ($row in $summaryBySubcheck) { $lines.Add("| $($row.subcheck) | $($row.rows) | $($row.match) | $($row.mismatch) | $($row.missing) | $($row.unknown) |") }
    $lines.Add("")
    $lines.Add("## Mismatches and missing rows")
    $lines.Add("")
    $lines.Add("| Kind | ID | Subcheck | Status | Legacy path | Port path | Evidence |")
    $lines.Add("|---|---|---|---|---|---|---|")
    foreach ($row in $orderedResults | Where-Object { $_.status -in @("LEGACY_PARITY_MISMATCH", "LEGACY_PARITY_MISSING") }) {
        $safeEvidence = if ($row.evidence) { $row.evidence.Replace("|", "\|") } else { "" }
        $lines.Add("| $($row.kind) | ``$($row.id)`` | $($row.subcheck) | $($row.status) | ``$($row.legacyPath)`` | ``$($row.portPath)`` | $safeEvidence |")
    }
    $lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
    Write-Output "Legacy visual/collision parity report: $OutputMarkdown"
    Write-Output "Rows=$($report.summary.rows), match=$($report.summary.match), mismatch=$($report.summary.mismatch), missing=$($report.summary.missing), unknown=$($report.summary.unknown)"
}
finally {
    if ($null -ne $legacyZip) { $legacyZip.Dispose() }
}
