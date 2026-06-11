param(
    [string]$PortDir = "05_neoforge_port"
)

$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$port = Resolve-Path (Join-Path $root $PortDir)
$source = Join-Path $port "src\main\resources"
$target = Join-Path $port "build\resources\main"

if (-not (Test-Path -LiteralPath $source)) {
    throw "Source resources folder not found: $source"
}

if (-not (Test-Path -LiteralPath $target)) {
    New-Item -ItemType Directory -Force -Path $target | Out-Null
}

Get-ChildItem -LiteralPath $source -Force | ForEach-Object {
    Copy-Item -LiteralPath $_.FullName -Destination $target -Recurse -Force
}

$thaumometerSource = Join-Path $source "assets\thaumcraft\models\item\thaumometer.json"
$thaumometerTarget = Join-Path $target "assets\thaumcraft\models\item\thaumometer.json"

Write-Host "Synced resources:"
Write-Host "  from: $source"
Write-Host "    to: $target"

if ((Test-Path -LiteralPath $thaumometerSource) -and (Test-Path -LiteralPath $thaumometerTarget)) {
    $sourceHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $thaumometerSource).Hash.Substring(0, 16)
    $targetHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $thaumometerTarget).Hash.Substring(0, 16)
    Write-Host "Thaumometer model hash:"
    Write-Host "  src:   $sourceHash"
    Write-Host "  build: $targetHash"
}

Write-Host "Now press F3+T in the already running client."
