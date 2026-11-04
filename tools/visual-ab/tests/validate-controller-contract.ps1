[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$root = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "..")).Path
$targets = @(
    (Join-Path $root "session/session.ps1"),
    (Join-Path $root "session/controller.ps1"),
    (Join-Path $root "preflight/inspect-fixtures.ps1"),
    (Join-Path $root "ab.ps1")
)
foreach ($target in $targets) {
    if (-not (Test-Path -LiteralPath $target -PathType Leaf)) { throw "Missing script: $target" }
    $tokens = $null
    $parseErrors = $null
    [void][System.Management.Automation.Language.Parser]::ParseFile($target, [ref]$tokens, [ref]$parseErrors)
    if (@($parseErrors).Count -gt 0) {
        $messages = @($parseErrors | ForEach-Object { $_.Message + " at " + $_.Extent.StartLineNumber })
        throw "PowerShell syntax errors in $target : $($messages -join '; ')"
    }
}
Write-Host "PASS: PowerShell AST parsing of controller, session core and preflight scripts"
Write-Host "NOTE: menu usability and live F2 collection still need the later Windows client pilot"
