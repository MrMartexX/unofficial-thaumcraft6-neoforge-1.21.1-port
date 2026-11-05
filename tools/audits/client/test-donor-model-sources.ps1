$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
Add-Type -AssemblyName System.IO.Compression.FileSystem
$archive = [IO.Compression.ZipFile]::OpenRead((Join-Path $root '01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar'))
try {
    $textures = @('entity/crab.png', 'entity/firebat.png', 'entity/taintseed.png', 'entity/taintacle.png',
        'entity/pech_forage.png', 'entity/pech_thaum.png', 'entity/pech_stalker.png', 'models/chesthungry.png')
    foreach ($texture in $textures) {
        $name = "assets/thaumcraft/textures/$texture"
        $entry = $archive.GetEntry($name)
        if ($null -eq $entry) { throw "Missing original texture: $name" }
        $stream = $entry.Open()
        $sha = [Security.Cryptography.SHA256]::Create()
        try { $legacy = [BitConverter]::ToString($sha.ComputeHash($stream)).Replace('-', '') }
        finally { $stream.Dispose(); $sha.Dispose() }
        $modern = Get-FileHash -Algorithm SHA256 -LiteralPath (Join-Path $root "05_neoforge_port/src/main/resources/$name")
        if ($legacy -ne $modern.Hash) { throw "Original texture mismatch: $name" }
        Write-Output "PASS SHA256 $texture $legacy"
    }
    $reader = [IO.StreamReader]::new($archive.GetEntry('assets/thaumcraft/models/block/chest.json').Open())
    try { $legacyModel = $reader.ReadToEnd() | ConvertFrom-Json } finally { $reader.Dispose() }
    $modernModel = Get-Content -LiteralPath (Join-Path $root '05_neoforge_port/src/main/resources/assets/thaumcraft/models/block/hungry_chest.json') -Raw | ConvertFrom-Json
    $oldGeometry = ConvertTo-Json -InputObject $legacyModel.elements -Depth 50 -Compress
    $newGeometry = ConvertTo-Json -InputObject $modernModel.elements -Depth 50 -Compress
    if ($oldGeometry -cne $newGeometry) { throw 'Hungry Chest cuboids/UVs differ from original jar JSON' }
    if ($modernModel.textures.texture -ne $legacyModel.textures.texture) { throw 'Hungry Chest texture identity changed' }
    Write-Output 'PASS original Hungry Chest item cuboids and all face UVs (structured JSON comparison)'
    Write-Output 'PASS source assets: 8 textures, 1 inventory model; this does not certify rendered pixels'
} finally { $archive.Dispose() }
