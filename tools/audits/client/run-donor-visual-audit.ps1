param(
    [string]$FixtureSource = 'tc_visual_ground_seals_20261103',
    [ValidateRange(30, 900)][int]$TimeoutSeconds = 360
)

$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$module = Join-Path $root '05_neoforge_port'
$saves = Join-Path $module 'run/saves'
if ($FixtureSource -notmatch '^tc_visual_[A-Za-z0-9_]+$') { throw 'Use a prepared disposable fixture, not a player save.' }
$source = Join-Path $saves $FixtureSource
if (-not (Test-Path -LiteralPath (Join-Path $source 'level.dat'))) { throw 'Missing prepared fixture level.dat' }
$id = [guid]::NewGuid().ToString('N')
$world = "tc_visual_donor_$id"
$target = Join-Path $saves $world
if (Test-Path -LiteralPath $target) { throw 'Refusing to overwrite a fixture' }
Copy-Item -LiteralPath $source -Destination $target -Recurse
$output = Join-Path $root "tools/reports/local/donor-visual-$id"
New-Item -ItemType Directory -Path $output | Out-Null
$stdout = Join-Path $output 'client.stdout.log'
$stderr = Join-Path $output 'client.stderr.log'
$old = $env:JAVA_TOOL_OPTIONS
$process = $null
try {
    $env:JAVA_TOOL_OPTIONS = "$old -Dtc.donorVisualAuditWorld=$world -Dtc.donorVisualAuditOutput=$output".Trim()
    $gradle = Join-Path $module 'gradlew.bat'
    $process = Start-Process -FilePath $env:ComSpec -ArgumentList @('/d', '/c', "`"$gradle`" runClient --no-daemon") `
        -WorkingDirectory $module -WindowStyle Hidden -PassThru -RedirectStandardOutput $stdout -RedirectStandardError $stderr
    $env:JAVA_TOOL_OPTIONS = $old
    if (-not $process.WaitForExit($TimeoutSeconds * 1000)) { throw "Donor audit timed out. Evidence: $output" }
    $text = (Get-Content -LiteralPath $stdout -Raw) + (Get-Content -LiteralPath $stderr -Raw)
    if ($process.ExitCode -ne 0 -or $text -notmatch 'TC_DONOR_ENTITY_CONTRACT_PASS variants=12' `
            -or $text -notmatch 'TC_DONOR_ENTITY_AUDIT_CAPTURED variants=12 views=3' `
            -or $text -match 'Exception in thread|Crash report|BUILD FAILED') {
        throw "Donor client audit failed. Evidence: $output"
    }
    foreach ($file in @('model-contracts.txt', 'models-0.png', 'models-1.png', 'models-2.png')) {
        if (-not (Test-Path -LiteralPath (Join-Path $output $file))) { throw "Missing evidence: $file" }
    }
    Write-Output "PASS contracts and three captures: $output"
    Write-Output "Disposable world retained for reproduction: $target"
} finally {
    $env:JAVA_TOOL_OPTIONS = $old
    if ($null -ne $process -and -not $process.HasExited) { $process.Kill($true); $process.WaitForExit() }
}
