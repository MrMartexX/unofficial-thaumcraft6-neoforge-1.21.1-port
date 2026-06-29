param(
    [string]$RepoRoot = (Get-Location).Path
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$RepoRoot = (Resolve-Path $RepoRoot).Path
$ModulePath = Join-Path $RepoRoot 'tools/audits/item-block-parity/modules/item_visual_parity.ps1'
& $ModulePath -RepoRoot $RepoRoot
