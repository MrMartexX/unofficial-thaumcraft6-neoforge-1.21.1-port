param(
    [Parameter(Mandatory = $true)]
    [string]$RepoRoot
)

$expected = Join-Path $RepoRoot '06_docs/audits/smelter_runtime_boundary_audit.md'
if (!(Test-Path $expected)) {
    throw '[tc-port] Missing smelter_runtime_boundary_audit.md'
}
Write-Host '[tc-port] Smelter runtime boundary audit exists. Re-run the chat generator script to refresh evidence rows.'
