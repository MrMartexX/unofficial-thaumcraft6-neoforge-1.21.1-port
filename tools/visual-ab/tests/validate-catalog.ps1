[CmdletBinding()]
param()
$ErrorActionPreference = "Stop"
$toolRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "..")).Path
$scriptPath = Join-Path $toolRoot "ab.ps1"
$catalogPath = Join-Path $toolRoot "catalog/v0-groups.json"
if (-not (Test-Path -LiteralPath $scriptPath -PathType Leaf)) { throw "Missing ab.ps1" }
if (-not (Test-Path -LiteralPath $catalogPath -PathType Leaf)) { throw "Missing catalog" }
$result = & pwsh -NoProfile -File $scriptPath -Action catalog -CatalogPath $catalogPath | ConvertFrom-Json
if ($LASTEXITCODE -ne 0) { throw "Catalog CLI failed" }
if ($result.groupCount -ne 13) { throw "Unexpected group count" }
if ($result.findingCount -ne 37) { throw "Unexpected finding count" }
if ($result.status -ne "SOURCE_CATALOG_VALID") { throw "Unexpected catalog status" }
if ($result.runtimeState -ne "NOT_RUN") { throw "Source inventory misrepresented as runtime proof" }
Write-Host "PASS: 13 groups; all 37 findings covered; runtime remains NOT_RUN"
