param([string]$EvidenceDirectory = (Join-Path $PSScriptRoot '../../reports/local/worldgen-20261103'))
$ErrorActionPreference = 'Stop'
$evidence = (Resolve-Path -LiteralPath $EvidenceDirectory).Path
$contracts = Get-Content -LiteralPath (Join-Path $evidence 'contracts/contracts.json') -Raw | ConvertFrom-Json
if (!$contracts.allPass -or $contracts.passed -lt 30) { throw 'Worldgen data contracts did not pass.' }
if ([string]::IsNullOrWhiteSpace($contracts.run_id) -or [string]::IsNullOrWhiteSpace($contracts.manifest_fingerprint)) {
    throw 'contracts.json is missing required run_id or manifest_fingerprint.'
}

$repo = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
foreach ($entry in $contracts.sha256.PSObject.Properties) {
    $hash = (Get-FileHash -LiteralPath (Join-Path $repo $entry.Name) -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($hash -ne $entry.Value) { throw "Source changed since contracts snapshot: $($entry.Name)" }
}

$expectedSeeds = @{
    'runtime-reload-final.md' = '61222121';
    'runtime-seednegative-final.md' = '-987654321';
    'gate11-diagnostics-seed612.md' = '61222121';
    'gate11-diagnostics-seednegative.md' = '-987654321'
}

foreach ($name in @('runtime-reload-final.md', 'runtime-seednegative-final.md')) {
    $report = Get-Content -LiteralPath (Join-Path $evidence $name) -Raw
    $passMatches = [regex]::Matches($report, '(?m)^- PASS (.*)$')
    $count = $passMatches.Count
    $uniqueCount = ($passMatches | ForEach-Object { $_.Groups[1].Value.Trim() } | Sort-Object -Unique).Count

    if ($report -notmatch "Run ID: ``$($contracts.run_id)``") {
        throw "Report $name missing or mismatched Run ID: expected $($contracts.run_id)"
    }
    if ($report -notmatch "Manifest Fingerprint: ``$($contracts.manifest_fingerprint)``") {
        throw "Report $name missing or mismatched Manifest Fingerprint: expected $($contracts.manifest_fingerprint)"
    }
    if ($report -notmatch "Seed: ``$($expectedSeeds[$name])``") {
        throw "Report $name missing or mismatched Seed: expected $($expectedSeeds[$name])"
    }
    if ($report -notmatch 'Result: \*\*PASS\*\*' -or $report -match '(?m)^- FAIL ' -or $count -lt 28) {
        throw "Incomplete or failed runtime report: $name ($count passed)"
    }
    if ($uniqueCount -ne $count) {
        throw "Report $name contains duplicate checks: $uniqueCount unique out of $count total"
    }
    Write-Output "$name : $count passed (all unique, run_id and fingerprint verified)"
}

foreach ($name in @('gate11-diagnostics-seed612.md', 'gate11-diagnostics-seednegative.md')) {
    $report = Get-Content -LiteralPath (Join-Path $evidence $name) -Raw
    $passMatches = [regex]::Matches($report, '(?m)^- PASS (.*)$')
    $count = $passMatches.Count
    $uniqueCount = ($passMatches | ForEach-Object { $_.Groups[1].Value.Trim() } | Sort-Object -Unique).Count

    if ($report -notmatch "Run ID: ``$($contracts.run_id)``") {
        throw "Report $name missing or mismatched Run ID: expected $($contracts.run_id)"
    }
    if ($report -notmatch "Manifest Fingerprint: ``$($contracts.manifest_fingerprint)``") {
        throw "Report $name missing or mismatched Manifest Fingerprint: expected $($contracts.manifest_fingerprint)"
    }
    if ($report -notmatch "Seed: ``$($expectedSeeds[$name])``") {
        throw "Report $name missing or mismatched Seed: expected $($expectedSeeds[$name])"
    }
    if ($report -notmatch 'Result: \*\*PASS\*\*' -or $report -match '(?m)^- FAIL ' -or $count -ne 32) {
        throw "Incomplete or failed Gate 11 diagnostic report ($count passed, expected 32): $name"
    }
    if ($uniqueCount -ne $count) {
        throw "Report $name contains duplicate checks: $uniqueCount unique out of $count total"
    }
    Write-Output "$name : $count passed (all unique, run_id and fingerprint verified)"
}

$client = Get-Content -LiteralPath (Join-Path $evidence 'client-ambient-final.log') -Raw
if ($client -notmatch 'TC_FOREST_AMBIENT_AUDIT_PASS nightSpawned=[1-9]\d* peakRenderable=[1-9]\d* daySpawned=0') {
    throw 'Missing actual night/day client evidence.'
}
if ($client -match 'TC_FOREST_AMBIENT_AUDIT_FAIL|Preparing crash report|Game crashed!') { throw 'Client evidence contains a crash/failure.' }
if (!(Test-Path -LiteralPath (Join-Path $evidence 'night-motes.png'))) { throw 'Missing night capture.' }

# Post-run verification that no sources were tampered with or modified during/after run
foreach ($entry in $contracts.sha256.PSObject.Properties) {
    $postHash = (Get-FileHash -LiteralPath (Join-Path $repo $entry.Name) -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($postHash -ne $entry.Value) { throw "Source changed post-run: $($entry.Name)" }
}

Write-Output 'WORLDGEN_EVIDENCE_PASS: data contracts, fresh/reloaded natural mounds, night/day client, run_id and manifest fingerprint verified.'
