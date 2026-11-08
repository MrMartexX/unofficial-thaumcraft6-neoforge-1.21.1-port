$ErrorActionPreference = 'Stop'
$repo = 'D:\Thaumcraft_6_port_to_1.21.1'
$runDir = Join-Path $repo '05_neoforge_port\run'
$propsFile = Join-Path $runDir 'server.properties'
$origProps = Get-Content $propsFile -Raw

# 1. Generate contracts manifest with unique run_id and manifest fingerprint
Write-Host "Generating contracts manifest..."
& python (Join-Path $repo 'tools/audits/worldgen/audit_worldgen.py')
if ($LASTEXITCODE -ne 0) { throw "Contracts generation failed" }

$contractsPath = Join-Path $repo 'tools/reports/local/worldgen-20261103/contracts/contracts.json'
$contracts = Get-Content -LiteralPath $contractsPath -Raw | ConvertFrom-Json
$runId = $contracts.run_id
$manifestHash = $contracts.manifest_fingerprint
Write-Host "Audit Run ID: $runId"
Write-Host "Manifest Fingerprint: $manifestHash"

# Alphanumeric run tag for world naming (disposable world naming contract)
$runTag = ($runId -replace '[^a-zA-Z0-9]', '_').Trim('_')
$createdWorlds = [System.Collections.Generic.List[string]]::new()
$resolvedRunDir = [System.IO.Path]::GetFullPath($runDir)

function Setup-WorldFixture([string]$baseName, [string]$seed) {
    $name = "tc_worldgen_audit_${runTag}_${baseName}"
    $w = Join-Path $runDir $name
    $fullPath = [System.IO.Path]::GetFullPath($w)

    # Pre-condition checks: strictly within run directory and never overwrite
    if (-not $fullPath.StartsWith($resolvedRunDir + [System.IO.Path]::DirectorySeparatorChar)) {
        throw "Security violation: world path '$fullPath' is outside run directory '$resolvedRunDir'"
    }
    if (-not $name.StartsWith('tc_worldgen_audit_')) {
        throw "Invalid disposable world name prefix: '$name'"
    }
    if (Test-Path -LiteralPath $fullPath) {
        throw "Refusing to reuse or overwrite existing world directory: $fullPath"
    }

    $python = Join-Path $env:USERPROFILE '.cache/codex-runtimes/codex-primary-runtime/dependencies/python/python.exe'
    if (!(Test-Path -LiteralPath $python)) { $python = 'python' }

    $env:PYTHONPATH = Join-Path $repo 'tools/reports/local/fixture-python'
    & $python (Join-Path $repo 'tools/fixtures/prepare_worldgen_audit.py') $name --seed "$seed"
    if ($LASTEXITCODE -ne 0) { throw "Fixture generation failed for $name" }

    $createdWorlds.Add($fullPath)

    $text = $origProps
    if ($text -match '(?m)^level-name=.*$') {
        $text = [regex]::Replace($text, '(?m)^level-name=.*$', "level-name=$name")
    } else {
        $text = $text.TrimEnd() + "`r`nlevel-name=$name`r`n"
    }
    if ($text -match '(?m)^level-seed=.*$') {
        $text = [regex]::Replace($text, '(?m)^level-seed=.*$', "level-seed=$seed")
    } else {
        $text = $text.TrimEnd() + "`r`nlevel-seed=$seed`r`n"
    }
    Set-Content -Path $propsFile -Value $text -Encoding ASCII
}

$outDir = Join-Path $repo 'tools\reports\local\worldgen-20261103'
$refJson = Join-Path $repo 'tools\reports\local\worldgen-20261103\legacy_flower_grid_reference.json'

try {
    # 2. Run TCWorldgenAudit on 61222121
    Write-Host "Starting audit 1: runtime-reload on seed 61222121..."
    Setup-WorldFixture 'stage2d_612' '61222121'
    & "$repo\05_neoforge_port\gradlew.bat" -p "$repo\05_neoforge_port" runServer -PtcWorldgenAudit=true "-PtcWorldgenAuditPath=$outDir\runtime-reload-final.md" "-PtcWorldgenAuditRunId=$runId" "-PtcWorldgenAuditManifest=$manifestHash" --no-daemon
    if ($LASTEXITCODE -ne 0) { throw "runtime reload failed" }

    # 3. Run TCWorldgenAudit on -987654321
    Write-Host "Starting audit 2: runtime-seednegative on seed -987654321..."
    Setup-WorldFixture 'stage2d_neg' '-987654321'
    & "$repo\05_neoforge_port\gradlew.bat" -p "$repo\05_neoforge_port" runServer -PtcWorldgenAudit=true "-PtcWorldgenAuditPath=$outDir\runtime-seednegative-final.md" "-PtcWorldgenAuditRunId=$runId" "-PtcWorldgenAuditManifest=$manifestHash" --no-daemon
    if ($LASTEXITCODE -ne 0) { throw "runtime seednegative failed" }

    # 4. Run Gate 11 diagnostics on 61222121
    Write-Host "Starting audit 3: gate11 on seed 61222121..."
    Setup-WorldFixture 'gate11e_612' '61222121'
    & "$repo\05_neoforge_port\gradlew.bat" -p "$repo\05_neoforge_port" runServer -PtcWorldgenGate11Audit=true "-PtcWorldgenGate11AuditPath=$outDir\gate11-diagnostics-seed612.md" "-PtcWorldgenAuditRunId=$runId" "-PtcWorldgenAuditManifest=$manifestHash" "-PtcLegacyFlowerGridReference=$refJson" --no-daemon
    if ($LASTEXITCODE -ne 0) { throw "gate11 612 failed" }

    # 5. Run Gate 11 diagnostics on -987654321
    Write-Host "Starting audit 4: gate11 on seed -987654321..."
    Setup-WorldFixture 'gate11e_neg' '-987654321'
    & "$repo\05_neoforge_port\gradlew.bat" -p "$repo\05_neoforge_port" runServer -PtcWorldgenGate11Audit=true "-PtcWorldgenGate11AuditPath=$outDir\gate11-diagnostics-seednegative.md" "-PtcWorldgenAuditRunId=$runId" "-PtcWorldgenAuditManifest=$manifestHash" "-PtcLegacyFlowerGridReference=$refJson" --no-daemon
    if ($LASTEXITCODE -ne 0) { throw "gate11 neg failed" }
} finally {
    Set-Content -Path $propsFile -Value $origProps -Encoding ASCII
    foreach ($worldPath in $createdWorlds) {
        $normalized = [System.IO.Path]::GetFullPath($worldPath)
        $folderName = [System.IO.Path]::GetFileName($normalized)
        if ($normalized.StartsWith($resolvedRunDir + [System.IO.Path]::DirectorySeparatorChar) -and
            $folderName.StartsWith('tc_worldgen_audit_') -and
            (Test-Path -LiteralPath $normalized)) {
            Write-Host "Safely cleaning up created world: $folderName"
            Remove-Item -LiteralPath $normalized -Recurse -Force -ErrorAction SilentlyContinue
        }
    }
}

# 6. Verify full evidence chain
Write-Host "Verifying evidence chain with test-worldgen-evidence.ps1..."
& powershell -ExecutionPolicy Bypass -File (Join-Path $repo 'tools/audits/worldgen/test-worldgen-evidence.ps1')
if ($LASTEXITCODE -ne 0) { throw "Evidence verification failed" }

Write-Host "ALL_SERVER_AUDITS_AND_EVIDENCE_COMPLETED_SUCCESSFULLY"
