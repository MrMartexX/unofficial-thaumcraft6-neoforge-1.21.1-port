$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$python = Join-Path $env:USERPROFILE '.cache/codex-runtimes/codex-primary-runtime/dependencies/python/python.exe'
if (!(Test-Path -LiteralPath $python)) { throw 'Configured Python runtime is missing.' }
$packages = Join-Path $repo 'tools/reports/local/fixture-python'
$oldPythonPath = $env:PYTHONPATH
try {
    $env:PYTHONPATH = $packages
    & $python (Join-Path $PSScriptRoot 'restore-parity-worlds.py')
    if ($LASTEXITCODE -ne 0) { throw 'Fixture restoration failed; existing saves were not replaced.' }
} finally { $env:PYTHONPATH = $oldPythonPath }
