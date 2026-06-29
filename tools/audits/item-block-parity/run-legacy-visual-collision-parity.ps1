[CmdletBinding()]
param(
    [Parameter(Mandatory = $false)][string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path,
    [string]$LegacySourceRoot = "02_existing_decompiled_repo/Thaumcraft-6-Source-Code-master",
    [string]$LegacyJarPath = "01_original_jar/Thaumcraft-1.12.2-6.1.BETA26.jar",
    [string]$PortManifestPath = "tools/reports/local/item-block-parity/port_manifest.json"
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$module = Join-Path $RepoRoot "tools/audits/item-block-parity/modules/legacy_visual_collision_parity.ps1"
$manifest = if ([System.IO.Path]::IsPathRooted($PortManifestPath)) { $PortManifestPath } else { Join-Path $RepoRoot $PortManifestPath }
$legacySource = if ([System.IO.Path]::IsPathRooted($LegacySourceRoot)) { $LegacySourceRoot } else { Join-Path $RepoRoot $LegacySourceRoot }
$legacyJar = if ([System.IO.Path]::IsPathRooted($LegacyJarPath)) { $LegacyJarPath } else { Join-Path $RepoRoot $LegacyJarPath }

& pwsh -NoProfile -File $module `
    -RepoRoot $RepoRoot `
    -PortManifestPath $manifest `
    -LegacySourceRoot $legacySource `
    -LegacyJarPath $legacyJar
exit $LASTEXITCODE