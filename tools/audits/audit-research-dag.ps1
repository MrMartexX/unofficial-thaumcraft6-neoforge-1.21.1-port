# Run Thaumonomicon Research & Aspect DAG Validator
$ErrorActionPreference = "Stop"
python tools/audits/audit-research-dag.py
if ($LASTEXITCODE -ne 0) {
    Write-Error "Research DAG validator failed with exit code $LASTEXITCODE"
    exit 1
}
