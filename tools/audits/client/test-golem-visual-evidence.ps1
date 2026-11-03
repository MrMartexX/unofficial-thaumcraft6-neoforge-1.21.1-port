param([string]$EvidenceDirectory = '', [string]$ClientLog = '')
$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
if (!$EvidenceDirectory) { $EvidenceDirectory = Join-Path $repo 'tools/reports/local/review-20261102/visual-seals-golems' }
if (!$ClientLog) { $ClientLog = Join-Path $repo 'tools/reports/local/review-20261102/visual-client.log' }
if ((Get-Content -LiteralPath $ClientLog -Raw) -notmatch 'TC_GOLEM_VISUAL_AUDIT_CAPTURED phases=10 serverMenus=true ghostClicks=true cacheRemoval=true reconnect=true watchCycle=true') {
    throw 'Missing real server-menu and removal acknowledgement marker'
}
Add-Type -AssemblyName System.Drawing
if ((Get-Content -LiteralPath $ClientLog -Raw) -notmatch 'TC_EXCHANGE_RENDER_PASS samples=\d+ vertices=\d+ finite=true atlas=true expires=true' -or
    (Get-Content -LiteralPath $ClientLog -Raw) -notmatch 'TC_EXCHANGE_PACKET_CLIENT_PASS particles=\d+ dimension=true') {
    throw 'Missing Exchange actual geometry or clientbound payload acceptance evidence'
}
if ((Get-Content -LiteralPath $ClientLog -Raw) -notmatch 'TC_GOLEM_RENDER_CONTRACT_PASS revealVertices=\d+ leashVertices=100 noLeak=true depthState=true dartVertices=24') {
    throw 'Missing actual reveal/leash geometry and reveal-state isolation contract'
}
for ($phase = 0; $phase -lt 10; $phase++) {
    $path = Join-Path $EvidenceDirectory "phase-$phase.png"
    if (!(Test-Path -LiteralPath $path)) { throw "Missing capture: $path" }
    $bitmap = [Drawing.Bitmap]::new($path)
    try {
        if ($bitmap.Width -lt 640 -or $bitmap.Height -lt 480) { throw "Capture too small: $path" }
        if ($phase -lt 2) {
            $cellWidth = [int][Math]::Floor($bitmap.Width / 3)
            $cellHeight = [int][Math]::Floor($bitmap.Height / 2)
            for ($cell = 0; $cell -lt 6; $cell++) {
                $left = ($cell % 3) * $cellWidth
                $top = [int][Math]::Floor($cell / 3) * $cellHeight
                $visible = 0
                $missing = 0
                for ($y = $top + 8; $y -lt $top + $cellHeight * .85; $y += 3) {
                    for ($x = $left + 8; $x -lt $left + $cellWidth - 8; $x += 3) {
                        $pixel = $bitmap.GetPixel($x, $y)
                        if ([Math]::Abs($pixel.R - 53) + [Math]::Abs($pixel.G - 59) + [Math]::Abs($pixel.B - 64) -gt 20) { $visible++ }
                        if ($pixel.R -gt 220 -and $pixel.B -gt 220 -and $pixel.G -lt 40) { $missing++ }
                    }
                }
                if ($visible -lt 100) { throw "Blank golem: phase=$phase cell=$cell visible=$visible" }
                if ($missing -gt 10) { throw "Missing-texture pixels: phase=$phase cell=$cell count=$missing" }
            }
        }
    } finally { $bitmap.Dispose() }
}
Write-Output 'PASS: ten captures, twelve nonblank golem views, reveal/leash GPU contracts, real menu/ghost clicks, chunk unwatch/rewatch and persisted reconnect.'
Write-Output 'This checks rendered evidence, not pixel equality against a legacy capture.'
