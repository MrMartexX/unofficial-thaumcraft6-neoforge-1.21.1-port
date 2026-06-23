[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RepoRoot,
    [string]$PortRoot = "05_neoforge_port",
    [Parameter(Mandatory = $true)][string]$AuditScript,
    [switch]$RunBuild,
    [switch]$RunSmoke,
    [switch]$RunRelatedAudits,
    [ValidateSet("off", "safe", "strict")][string]$FailMode = "safe",
    [string]$OutputJson,
    [string]$OutputMarkdown
)

$ErrorActionPreference = "Stop"
$RepoRoot = (Resolve-Path $RepoRoot).Path
$portPath = Join-Path $RepoRoot $PortRoot
if (-not (Test-Path -LiteralPath $portPath -PathType Container)) { throw "Port root not found: $portPath" }
if (-not (Test-Path -LiteralPath $AuditScript -PathType Leaf)) { throw "Audit script not found: $AuditScript" }
if (-not $OutputJson) { $OutputJson = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_runtime_report.json" }
if (-not $OutputMarkdown) { $OutputMarkdown = Join-Path $RepoRoot "tools/reports/local/item-block-parity/item_block_runtime_report.md" }
$logRoot = Join-Path $RepoRoot "tools/reports/local/item-block-parity/runtime-logs"
New-Item -ItemType Directory -Force -Path $logRoot | Out-Null

function Get-GradleExecutable([string]$PortPath) {
    $bat = Join-Path $PortPath "gradlew.bat"
    if (Test-Path -LiteralPath $bat -PathType Leaf) { return $bat }
    $sh = Join-Path $PortPath "gradlew"
    if (Test-Path -LiteralPath $sh -PathType Leaf) { return $sh }
    throw "Gradle wrapper not found under $PortPath"
}
function Invoke-LoggedCommand {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string]$WorkingDirectory,
        [Parameter(Mandatory = $true)][string]$Command,
        [string[]]$Arguments = @()
    )
    $logPath = Join-Path $logRoot "$Name.log"
    Push-Location $WorkingDirectory
    try {
        $startedAt = [DateTime]::UtcNow
        $output = @(& $Command @Arguments 2>&1 | ForEach-Object { [string]$_ })
        $exitCode = if ($null -ne $LASTEXITCODE) { $LASTEXITCODE } else { 0 }
        $finishedAt = [DateTime]::UtcNow
    } finally {
        Pop-Location
    }
    $output | Set-Content -LiteralPath $logPath -Encoding utf8NoBOM
    return [pscustomobject][ordered]@{
        name = $Name
        command = $Command
        arguments = @($Arguments)
        workingDirectory = $WorkingDirectory
        startedAtUtc = $startedAt.ToString("o")
        finishedAtUtc = $finishedAt.ToString("o")
        exitCode = $exitCode
        status = if ($exitCode -eq 0) { "PASS" } else { "FAIL" }
        log = $logPath
    }
}

$steps = [System.Collections.Generic.List[object]]::new()
$gradle = Get-GradleExecutable $portPath

if ($RunBuild) {
    $steps.Add((Invoke-LoggedCommand -Name "gradle_compileJava" -WorkingDirectory $portPath -Command $gradle -Arguments @("--no-daemon", "compileJava")))
}
if ($RunSmoke) {
    $steps.Add((Invoke-LoggedCommand -Name "gradle_classes_smoke" -WorkingDirectory $portPath -Command $gradle -Arguments @("--no-daemon", "classes")))
}
if ($RunRelatedAudits) {
    $pwsh = (Get-Command pwsh -ErrorAction Stop).Source
    $related = @(
        [pscustomobject]@{ name = "audit_registry"; args = @("-NoProfile", "-ExecutionPolicy", "Bypass", "-File", $AuditScript, "-RepoRoot", $RepoRoot, "-Preset", "registry", "-FailMode", "off") },
        [pscustomobject]@{ name = "audit_resources"; args = @("-NoProfile", "-ExecutionPolicy", "Bypass", "-File", $AuditScript, "-RepoRoot", $RepoRoot, "-Preset", "resources", "-FailMode", "off") },
        [pscustomobject]@{ name = "audit_data"; args = @("-NoProfile", "-ExecutionPolicy", "Bypass", "-File", $AuditScript, "-RepoRoot", $RepoRoot, "-Preset", "data", "-FailMode", "off") },
        [pscustomobject]@{ name = "audit_behavior_boundary"; args = @("-NoProfile", "-ExecutionPolicy", "Bypass", "-File", $AuditScript, "-RepoRoot", $RepoRoot, "-Preset", "behavior-boundary", "-FailMode", "off") },
        [pscustomobject]@{ name = "audit_source_quality"; args = @("-NoProfile", "-ExecutionPolicy", "Bypass", "-File", $AuditScript, "-RepoRoot", $RepoRoot, "-Preset", "source-quality", "-ProbeSecondaryLegacy", "-FailMode", "off") }
    )
    foreach ($item in $related) {
        $steps.Add((Invoke-LoggedCommand -Name $item.name -WorkingDirectory $RepoRoot -Command $pwsh -Arguments @($item.args)))
    }
}

$orderedSteps = @($steps)
$failed = @($orderedSteps | Where-Object status -eq "FAIL")
$report = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    policy = "Runtime integration report. Build/smoke/related audit commands are explicit opt-in switches and write local logs only. RunSmoke uses non-interactive Gradle classes, not a client/server process."
    requested = [ordered]@{
        runBuild = [bool]$RunBuild
        runSmoke = [bool]$RunSmoke
        runRelatedAudits = [bool]$RunRelatedAudits
    }
    summary = [ordered]@{
        steps = $orderedSteps.Count
        pass = @($orderedSteps | Where-Object status -eq "PASS").Count
        fail = $failed.Count
    }
    steps = $orderedSteps
}
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputJson) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path -Parent $OutputMarkdown) | Out-Null
$report | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $OutputJson -Encoding utf8NoBOM

$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add("# Item/block runtime integration report")
$lines.Add("")
$lines.Add("Generated: $($report.generatedAtUtc)")
$lines.Add("")
$lines.Add("Policy: $($report.policy)")
$lines.Add("")
$lines.Add("## Summary")
$lines.Add("")
$lines.Add("| Steps | PASS | FAIL |")
$lines.Add("|---:|---:|---:|")
$lines.Add("| $($report.summary.steps) | $($report.summary.pass) | $($report.summary.fail) |")
$lines.Add("")
$lines.Add("## Steps")
$lines.Add("")
$lines.Add("| Step | Status | Exit code | Log |")
$lines.Add("|---|---|---:|---|")
foreach ($step in $orderedSteps) {
    $relativeLog = [System.IO.Path]::GetRelativePath($RepoRoot, $step.log).Replace("\\", "/")
    $lines.Add("| $($step.name) | $($step.status) | $($step.exitCode) | ``$relativeLog`` |")
}
$lines | Set-Content -LiteralPath $OutputMarkdown -Encoding utf8NoBOM
Write-Output "Runtime integration report: $OutputMarkdown"
Write-Output "Runtime steps=$($report.summary.steps), pass=$($report.summary.pass), fail=$($report.summary.fail)"
if ($failed.Count -gt 0 -and $FailMode -ne "off") {
    throw "Runtime integration had failed steps: $($failed.name -join ', ')"
}