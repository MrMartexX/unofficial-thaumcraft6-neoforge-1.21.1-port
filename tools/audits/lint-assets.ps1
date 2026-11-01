# Run Asset & Texture Integrity Linter
$ErrorActionPreference = "Stop"
python tools/audits/lint-assets.py
if ($LASTEXITCODE -ne 0) {
    Write-Error "Asset linter failed with exit code $LASTEXITCODE"
    exit 1
}
