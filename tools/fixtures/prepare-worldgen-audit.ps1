param(
    [Parameter(Mandatory = $true)][string]$Name,
    [long]$Seed = 61222121,
    [switch]$Ambient
)
$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$python = Join-Path $env:USERPROFILE '.cache/codex-runtimes/codex-primary-runtime/dependencies/python/python.exe'
if (!(Test-Path -LiteralPath $python)) { throw 'Configured fixture Python runtime is missing.' }
$oldPythonPath = $env:PYTHONPATH
try {
    $env:PYTHONPATH = Join-Path $repo 'tools/reports/local/fixture-python'
    $arguments = @((Join-Path $PSScriptRoot 'prepare_worldgen_audit.py'), $Name, '--seed', "$Seed")
    if ($Ambient) { $arguments += '--ambient' }
    & $python @arguments
    if ($LASTEXITCODE -ne 0) { throw 'Worldgen fixture creation failed; existing saves were not replaced.' }
} finally { $env:PYTHONPATH = $oldPythonPath }
