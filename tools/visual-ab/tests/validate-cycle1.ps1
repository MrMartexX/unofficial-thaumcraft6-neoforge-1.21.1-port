[CmdletBinding()]
param([string]$ConfigPath)
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$cli = Join-Path $PSScriptRoot "../ab.ps1"
if (-not $ConfigPath) {
    $root = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "../../..")).Path
    $ConfigPath = Join-Path $root "tools/reports/local/visual-ab/config.local.json"
}

$catalogOutput = & pwsh -NoProfile -File $cli -Action catalog
if ($LASTEXITCODE -ne 0) { throw "Catalog CLI failed" }
$catalog = $catalogOutput | ConvertFrom-Json
if ($catalog.status -ne "SOURCE_CATALOG_VALID" -or $catalog.findingCount -ne 37 -or $catalog.groupCount -ne 13) {
    throw "Catalog is incomplete"
}
$ledgerOutput = & pwsh -NoProfile -File $cli -Action findings
if ($LASTEXITCODE -ne 0) { throw "Finding ledger CLI failed" }
$ledger = $ledgerOutput | ConvertFrom-Json
if ($ledger.status -ne "SOURCE_LEDGER_VALID" -or $ledger.runtimeState -ne "NOT_RUN") {
    throw "Ledger failed integrity or incorrectly claimed runtime success"
}
if ($ledger.watchlistCount -ne 6 -or @($ledger.unmappedWatchlistIds).Count -ne 1 -or $ledger.unmappedWatchlistIds[0] -ne "W-02") {
    throw "Watchlist provenance unexpectedly changed"
}
Write-Host "PASS: 13 source groups, 37 preserved findings, 6 watchlist entries; W-02 explicitly unmapped"

if (Test-Path -LiteralPath $ConfigPath -PathType Leaf) {
    $fixtureOutput = & pwsh -NoProfile -File $cli -Action fixtures -ConfigPath $ConfigPath
    if ($LASTEXITCODE -ne 0) { throw "Fixture inspection CLI failed" }
    $fixture = $fixtureOutput | ConvertFrom-Json
    if ($fixture.schema -ne "tc6-visual-ab-fixture-inventory/1") {
        throw "Unexpected fixture report schema"
    }
    foreach ($world in @($fixture.worlds)) {
        if ($world.clientVerification -ne "NOT_RUN") {
            throw "Filesystem-only world inspection claimed client validation"
        }
    }
    Write-Host "PASS: read-only fixture inventory; $($fixture.worldCount) worlds; $($fixture.pairedCount) candidate pair(s)"
} else {
    Write-Host "SKIP: local config absent; fixture inventory requires host-specific paths"
}
Write-Host "NOTE: no Minecraft client was launched and no gameplay PASS was assigned"
