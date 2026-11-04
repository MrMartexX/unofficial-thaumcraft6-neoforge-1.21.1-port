[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$RunPath,
    [string]$SceneId
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$toolRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "..")).Path
$catalog = Get-Content -LiteralPath (Join-Path $toolRoot "catalog/v0-state-candidates.json") -Raw -Encoding UTF8 | ConvertFrom-Json

function Resolve-RunFile([string]$Root, [string]$Relative) {
    if ([string]::IsNullOrWhiteSpace($Relative) -or [IO.Path]::IsPathRooted($Relative) -or
        $Relative -match '(^|[/\\])\.\.([/\\]|$)' -or $Relative.Contains(":")) {
        throw "Unsafe evidence path: $Relative"
    }
    $fullRoot = [IO.Path]::GetFullPath($Root).TrimEnd([char[]]@('/', '\'))
    $full = [IO.Path]::GetFullPath((Join-Path $fullRoot $Relative))
    if (-not $full.StartsWith(($fullRoot + [IO.Path]::DirectorySeparatorChar), [StringComparison]::OrdinalIgnoreCase)) {
        throw "Evidence path escapes run directory"
    }
    if (-not (Test-Path -LiteralPath $full -PathType Leaf)) { throw "Evidence missing: $Relative" }
    $segment = $fullRoot
    foreach ($part in ($Relative -split '[\\/]')) {
        $segment = Join-Path $segment $part
        if ((Get-Item -LiteralPath $segment).Attributes -band [IO.FileAttributes]::ReparsePoint) {
            throw "Symlink/reparse-point evidence is not accepted"
        }
    }
    return $full
}

$resolvedRun = (Resolve-Path -LiteralPath $RunPath -ErrorAction Stop).Path
$session = Get-Content -LiteralPath (Join-Path $resolvedRun "session.json") -Raw -Encoding UTF8 | ConvertFrom-Json
if ($session.schema -ne "tc6-visual-ab-session/1" -or $session.catalogVersion -ne $catalog.version) {
    throw "Unsupported or unpinned session"
}
if (-not $SceneId -and $null -ne $session.selection) { $SceneId = [string]$session.selection.sceneId }
if ([string]::IsNullOrWhiteSpace($SceneId) -or @($catalog.scenes | Where-Object { $_.id -ceq $SceneId }).Count -ne 1) {
    throw "Specify a SceneId from the pinned catalog"
}

$selected = @{}
foreach ($side in @("legacy", "modern")) {
    $matching = @($session.evidence | Where-Object { $_.sceneId -ceq $SceneId -and $_.side -ceq $side } |
        Sort-Object importedUtc -Descending)
    if ($matching.Count -eq 0) { throw "Missing $side screenshot for $SceneId" }
    $evidence = $matching[0]
    $source = Resolve-RunFile $resolvedRun ([string]$evidence.relativePath)
    if ([IO.Path]::GetExtension($source).ToLowerInvariant() -ne '.png') { throw "Expected PNG evidence" }
    if ((Get-Item -LiteralPath $source).Length -gt 32MB) { throw "Image exceeds 32 MiB viewer limit" }
    $actualHash = (Get-FileHash -LiteralPath $source -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actualHash -cne [string]$evidence.sha256) { throw "Evidence integrity failed ($side)" }
    $selected[$side] = [pscustomobject]@{
        id = $evidence.id
        sha256 = $actualHash
        relativePath = $evidence.relativePath
        dataUri = 'data:image/png;base64,' + [Convert]::ToBase64String([IO.File]::ReadAllBytes($source))
    }
}

$safeScene = [Net.WebUtility]::HtmlEncode($SceneId)
$safeRun = [Net.WebUtility]::HtmlEncode([string]$session.runId)
$html = @'
<!doctype html>
<html lang="ru">
<head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Thaumcraft A/B image review</title>
<style>
body{font:15px system-ui,Arial,sans-serif;background:#151820;color:#f0f0f0;margin:24px}
h1{font-size:22px}p{color:#c9ced9}.pair{display:grid;grid-template-columns:1fr 1fr;gap:14px}
.panel{background:#222733;padding:12px;border-radius:8px;min-width:0}.panel img{display:block;max-width:100%;height:auto}
.overlay{position:relative;width:100%;overflow:hidden}.overlay img{width:100%;height:auto}
.overlay img:last-child{position:absolute;inset:0;opacity:.5}
canvas{max-width:100%;height:auto;background:#080808}label{display:inline-flex;align-items:center;gap:12px}
button{padding:8px 14px}#warning{color:#ffcf83}code{word-break:break-all}
</style></head><body>
<h1>Thaumcraft 6 — A/B review</h1>
<p>Run: <code>__RUN__</code> · Scene: <code>__SCENE__</code></p>
<p id="warning">Предварительное сравнение. Автоматическая разница пикселей не устанавливает игровой PASS; состояние калибровки и подготовки проверяется отдельно.</p>
<h2>Side-by-side</h2><div class="pair"><div class="panel">Minecraft 1.12.2<img id="legacy" src="__LEGACY__" alt="Legacy"></div><div class="panel">Minecraft 1.21.1<img id="modern" src="__MODERN__" alt="Modern"></div></div>
<h2>Overlay</h2><p><label>Прозрачность порта <input id="opacity" type="range" min="0" max="100" value="50"> <output id="opacityText">50%</output></label></p>
<div class="panel"><div class="overlay"><img id="overlayLegacy" src="__LEGACY__" alt="Legacy overlay"><img id="overlayModern" src="__MODERN__" alt="Modern overlay"></div></div>
<h2>Difference Image</h2><p>Изображение вычисляется в браузере только для кадров одинакового разрешения. Выравнивание камеры этим инструментом не выполняется.</p>
<p><label>Порог канала (0–255): <input id="threshold" type="range" min="0" max="255" value="32"><output id="thresholdText">32</output></label></p>
<p id="metric">Загрузка кадров…</p><canvas id="diff"></canvas>
<p><button id="download" disabled>Скачать Difference PNG</button></p>
<script>
"use strict";
const legacy=document.getElementById('legacy'),modern=document.getElementById('modern');
const opacity=document.getElementById('opacity'),threshold=document.getElementById('threshold');
const canvas=document.getElementById('diff'),metric=document.getElementById('metric'),download=document.getElementById('download');
opacity.addEventListener('input',()=>{document.getElementById('overlayModern').style.opacity=opacity.value/100;document.getElementById('opacityText').textContent=opacity.value+'%'});
let pixelsA=null,pixelsB=null;
function loadPixels(img,w,h){const c=document.createElement('canvas');c.width=w;c.height=h;const x=c.getContext('2d',{willReadFrequently:true});x.drawImage(img,0,0);return x.getImageData(0,0,w,h).data}
function renderDiff(){
  if(!pixelsA||!pixelsB)return;
  const limit=Number(threshold.value);document.getElementById('thresholdText').textContent=String(limit);
  const ctx=canvas.getContext('2d');const out=ctx.createImageData(canvas.width,canvas.height);let changed=0;
  for(let i=0;i<pixelsA.length;i+=4){
    const delta=Math.max(Math.abs(pixelsA[i]-pixelsB[i]),Math.abs(pixelsA[i+1]-pixelsB[i+1]),Math.abs(pixelsA[i+2]-pixelsB[i+2]));
    if(delta>limit){changed++;out.data[i]=255;out.data[i+1]=Math.min(255,delta);out.data[i+2]=35;out.data[i+3]=255}
    else{out.data[i]=28;out.data[i+1]=35;out.data[i+2]=45;out.data[i+3]=255}
  }
  ctx.putImageData(out,0,0);metric.textContent='Порог: '+limit+'; выделено пикселей: '+changed+' из '+(canvas.width*canvas.height)+' (диагностика, не оценка соответствия)';
}
function prepare(){
  if(!legacy.complete||!modern.complete||!legacy.naturalWidth||!modern.naturalWidth)return;
  if(legacy.naturalWidth!==modern.naturalWidth||legacy.naturalHeight!==modern.naturalHeight){
    metric.textContent='UNALIGNED_FRAMES: разрешения различаются ('+legacy.naturalWidth+'×'+legacy.naturalHeight+' / '+modern.naturalWidth+'×'+modern.naturalHeight+'). Difference отключён.';return;
  }
  canvas.width=legacy.naturalWidth;canvas.height=legacy.naturalHeight;
  try{pixelsA=loadPixels(legacy,canvas.width,canvas.height);pixelsB=loadPixels(modern,canvas.width,canvas.height);renderDiff();download.disabled=false}
  catch(e){metric.textContent='Ошибка декодирования или доступа к пикселям: '+e.message}
}
legacy.addEventListener('load',prepare);modern.addEventListener('load',prepare);
legacy.addEventListener('error',()=>metric.textContent='Не удалось загрузить оригинал');
modern.addEventListener('error',()=>metric.textContent='Не удалось загрузить порт');
threshold.addEventListener('input',renderDiff);
download.addEventListener('click',()=>canvas.toBlob(blob=>{if(!blob)return;const u=URL.createObjectURL(blob);const a=document.createElement('a');a.href=u;a.download='thaumcraft-difference.png';a.click();setTimeout(()=>URL.revokeObjectURL(u),1000)},'image/png'));
prepare();
</script></body></html>
'@
$html = $html.Replace('__RUN__', $safeRun).Replace('__SCENE__', $safeScene)
$html = $html.Replace('__LEGACY__', $selected.legacy.dataUri).Replace('__MODERN__', $selected.modern.dataUri)

$comparisonDir = Join-Path $resolvedRun 'comparisons'
New-Item -ItemType Directory -Path $comparisonDir -Force | Out-Null
$comparisonId = 'CMP_' + [DateTime]::UtcNow.ToString('yyyyMMddTHHmmssfffZ') + '_' + [guid]::NewGuid().ToString('N').Substring(0, 8)
$htmlPath = Join-Path $comparisonDir ($comparisonId + '.html')
[IO.File]::WriteAllText($htmlPath, $html, [Text.UTF8Encoding]::new($false))
[pscustomobject][ordered]@{
    schema = 'tc6-visual-ab-comparison/1'
    status = 'REVIEW_VIEWER_CREATED'
    runId = $session.runId
    sceneId = $SceneId
    htmlPath = $htmlPath
    legacySha256 = $selected.legacy.sha256
    modernSha256 = $selected.modern.sha256
    processing = 'REVIEW_REQUIRED'
    observation = 'NOT_RUN'
    warning = 'Frames are not automatically registered as aligned, calibrated, or visually equivalent.'
} | ConvertTo-Json -Depth 5
