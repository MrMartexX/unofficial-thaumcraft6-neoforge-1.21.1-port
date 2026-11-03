param([string]$EvidenceDirectory = (Join-Path $PSScriptRoot '../../reports/local/worldgen-20261103'))
$ErrorActionPreference = 'Stop'
$evidence = (Resolve-Path -LiteralPath $EvidenceDirectory).Path
$contracts = Get-Content -LiteralPath (Join-Path $evidence 'contracts/contracts.json') -Raw | ConvertFrom-Json
if (!$contracts.allPass -or $contracts.passed -lt 30) { throw 'Worldgen data contracts did not pass.' }
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
foreach ($entry in $contracts.sha256.PSObject.Properties) {
    $hash = (Get-FileHash -LiteralPath (Join-Path $repo $entry.Name) -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($hash -ne $entry.Value) { throw "Source changed since contracts snapshot: $($entry.Name)" }
}
foreach ($name in @('runtime-reload-final.md', 'runtime-seednegative-final.md')) {
    $report = Get-Content -LiteralPath (Join-Path $evidence $name) -Raw
    $count = [regex]::Matches($report, '(?m)^- PASS ').Count
    if ($report -notmatch 'Result: \*\*PASS\*\*' -or $report -match '(?m)^- FAIL ' -or $count -lt 28) {
        throw "Incomplete or failed runtime report: $name"
    }
    Write-Output "$name : $count passed"
}
$client = Get-Content -LiteralPath (Join-Path $evidence 'client-ambient-final.log') -Raw
if ($client -notmatch 'TC_FOREST_AMBIENT_AUDIT_PASS nightSpawned=[1-9]\d* peakRenderable=[1-9]\d* daySpawned=0') {
    throw 'Missing actual night/day client evidence.'
}
if ($client -match 'TC_FOREST_AMBIENT_AUDIT_FAIL|Preparing crash report|Game crashed!') { throw 'Client evidence contains a crash/failure.' }
if (!(Test-Path -LiteralPath (Join-Path $evidence 'night-motes.png'))) { throw 'Missing night capture.' }
Write-Output 'WORLDGEN_EVIDENCE_PASS: data contracts, fresh/reloaded natural mounds, night/day client.'
