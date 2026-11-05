[CmdletBinding()]
param(
    [string]$RunPath,
    [string]$ResultsRoot,
    [string]$ConfigPath
)
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$core = Join-Path $PSScriptRoot "session.ps1"
$comparisonTool = Join-Path $PSScriptRoot "../comparison/compare.ps1"
$reportTool = Join-Path $PSScriptRoot "../reporting/report.ps1"
$mediaTool = Join-Path $PSScriptRoot "../media/media.ps1"
$environmentTool = Join-Path $PSScriptRoot "../environment/inspect.ps1"
$videoTool = Join-Path $PSScriptRoot "../video/plan.ps1"
$videoProbeTool = Join-Path $PSScriptRoot "../video/probe.ps1"
$videoRenderTool = Join-Path $PSScriptRoot "../video/render.ps1"
$catalogPath = Join-Path $PSScriptRoot "../catalog/v0-state-candidates.json"
if (-not (Test-Path -LiteralPath $core -PathType Leaf)) { throw "Missing session.ps1" }
$catalog = Get-Content -LiteralPath $catalogPath -Raw -Encoding UTF8 | ConvertFrom-Json
if ($catalog.schema -ne "tc6-visual-ab-scene-candidates/1") { throw "Unsupported state catalog" }
$script:activeRun = if ($RunPath) { $RunPath } else { $null }

function Invoke-Core([string[]]$Arguments) {
    $raw = & pwsh -NoProfile -File $script:core @Arguments
    $code = $LASTEXITCODE
    if ($code -ne 0) {
        Write-Warning "Controller command failed with exit code $code"
        return $null
    }
    $text = @($raw) -join "`n"
    if (-not $text) { return $null }
    try {
        return ($text | ConvertFrom-Json -ErrorAction Stop)
    } catch {
        Write-Warning "Unexpected controller response: $text"
        return $null
    }
}

function Invoke-Cycle3([string]$Script, [string[]]$Arguments) {
    $raw = & pwsh -NoProfile -File $Script @Arguments
    if ($LASTEXITCODE -ne 0) {
        Write-Warning "Cycle 3 action failed (exit code $LASTEXITCODE)"
        return $null
    }
    try { return (@($raw) -join "`n" | ConvertFrom-Json -ErrorAction Stop) }
    catch { Write-Warning "Unexpected Cycle 3 response: $(@($raw) -join ' ')"; return $null }
}

function Need-Run {
    if (-not $script:activeRun) {
        Write-Host "Сначала создай сессию или укажи существующий RunPath." -ForegroundColor Yellow
        return $false
    }
    return $true
}

function Show-Pilots {
    Write-Host ""
    Write-Host "Предварительные карточки с инструкциями (в клиенте пока НЕ проверены):"
    foreach ($scene in @($catalog.scenes | Where-Object { $_.stateStatus -eq "PILOT_RECIPE_DRAFT" })) {
        Write-Host ("  " + $scene.id)
        Write-Host ("      " + $scene.title)
    }
    Write-Host "Остальные 43 карточки пока требуют конкретных игровых рецептов."
}

function Show-SceneInstructions([string]$Id) {
    $scene = @($catalog.scenes | Where-Object { $_.id -ceq $Id })
    if ($scene.Count -ne 1) {
        Write-Warning "Неизвестный ID сцены: $Id"
        return
    }
    $item = $scene[0]
    Write-Host ""
    Write-Host ("Сцена: " + $item.id)
    Write-Host ("Цель: " + $item.objective)
    Write-Host ("Статус подготовки: " + $item.stateStatus)
    Write-Host ("Ожидается F2 на каждую сторону: " + $item.capture.screenshots)
    if ($item.capture.video) { Write-Host "Дополнительно требуется видео: можно индексировать внешний файл (V), без анализа содержимого." }
    if ($item.capture.logs) { Write-Host "Дополнительно требуется лог: можно сохранить локальную копию (L), требуется проверка приватности." }
    if ($null -ne $item.PSObject.Properties["steps"]) {
        $i = 1
        foreach ($step in @($item.steps)) {
            Write-Host ("  " + $i + ". " + $step)
            $i++
        }
    } else {
        Write-Host ("Подготовка не подтверждена: " + $item.prerequisites)
        Write-Host "Не создавай сцену предположительными командами." -ForegroundColor Yellow
    }
}

Write-Host "Thaumcraft Visual A/B Harness: контроллер сессий (предварительная версия)"
Write-Host "Только работа с доказательствами; игровые миры и настройки не изменяются."
while ($true) {
    Write-Host ""
    Write-Host ("Текущая сессия: " + $(if ($script:activeRun) { $script:activeRun } else { "(не выбрана)" }))
    Write-Host "1  Создать сессию"
    Write-Host "2  Открыть существующую сессию"
    Write-Host "3  Показать 5 пилотных карточек"
    Write-Host "4  Выбрать сцену и сторону"
    Write-Host "5  Смотреть новые F2 (без удаления исходников)"
    Write-Host "6  Импортировать уже сделанный F2"
    Write-Host "7  Добавить заметку к сцене"
    Write-Host "8  Показать полноту доказательств сцены"
    Write-Host "9  Проверить SHA-256 всех снимков"
    Write-Host "C  A/B просмотр выбранной сцены (HTML)"
    Write-Host "R  Сформировать отчёт и проверенный ZIP"
    Write-Host "I  Проверить и при желании импортировать ZIP"
    Write-Host "L  Снять локальную копию latest.log (не входит в ZIP)"
    Write-Host "V  Зарегистрировать внешний видеофайл (без копирования)"
    Write-Host "M  Состояние и SHA-256 логов/видео"
    Write-Host "E  Снимок и сверка условий обоих клиентов (без их запуска)"
    Write-Host "T  План сегментов двух видео (ручные смещения, без нарезки)"
    Write-Host "B  ffprobe и предпросмотр безопасных аргументов ffmpeg"
    Write-Host "F  Нарезка двух видео через ffmpeg (только по явному подтверждению)"
    Write-Host "S  Показать состояние сессии"
    Write-Host "0  Выход"
    $choice = (Read-Host "Выбери действие").Trim().ToUpperInvariant()
    try {
        switch ($choice) {
            "1" {
                $root = $ResultsRoot
                if (-not $root) { $root = Read-Host "Каталог результатов (желательно вне Git-репозитория)" }
                if (-not $root) { Write-Warning "Каталог не указан"; break }
                $pair = Read-Host "Идентификатор пары миров (Enter = пока неизвестен)"
                $argsList = @("-Action", "start", "-ResultsRoot", $root)
                if ($pair) { $argsList += @("-PairKey", $pair) }
                $response = Invoke-Core $argsList
                if ($null -ne $response -and $response.status -eq "SESSION_CREATED") {
                    $script:activeRun = [string]$response.runPath
                    Write-Host ("Создано: " + $response.runId) -ForegroundColor Green
                }
            }
            "2" {
                $path = Read-Host "Полный путь к папке RUN_..."
                if (-not $path) { break }
                $response = Invoke-Core @("-Action", "status", "-RunPath", $path)
                if ($null -ne $response) {
                    $script:activeRun = $path
                    Write-Host ("Продолжение сессии " + $response.runId) -ForegroundColor Green
                }
            }
            "3" { Show-Pilots }
            "4" {
                if (-not (Need-Run)) { break }
                $id = Read-Host "SceneId (точный ID из каталога)"
                Show-SceneInstructions $id
                $side = (Read-Host "Сторона: legacy или modern").Trim().ToLowerInvariant()
                if ($side -notin @("legacy", "modern")) {
                    Write-Warning "Ожидалось legacy или modern"; break
                }
                $response = Invoke-Core @("-Action", "select", "-RunPath", $script:activeRun, "-SceneId", $id, "-Side", $side)
                if ($null -ne $response -and $response.status -eq "SCENE_SELECTED") {
                    Write-Host ("Активно: " + $id + " / " + $side) -ForegroundColor Green
                }
            }
            "5" {
                if (-not (Need-Run)) { break }
                $status = Invoke-Core @("-Action", "status", "-RunPath", $script:activeRun)
                if ($null -eq $status -or -not $status.activeSceneId) {
                    Write-Warning "Сначала выбери сцену и сторону"; break
                }
                Show-SceneInstructions ([string]$status.activeSceneId)
                $numText = Read-Host "Сколько НОВЫХ F2 зарегистрировать (Enter = 1)"
                $maxFiles = 1
                if ($numText) {
                    if (-not [int]::TryParse($numText, [ref]$maxFiles) -or $maxFiles -lt 1 -or $maxFiles -gt 100) {
                        Write-Warning "Количество должно быть от 1 до 100"; break
                    }
                }
                $argsList = @("-Action", "watch", "-RunPath", $script:activeRun, "-MaxSeconds", "120", "-MaxFiles", [string]$maxFiles)
                if ($ConfigPath) { $argsList += @("-ConfigPath", $ConfigPath) }
                Write-Host "Запускаю наблюдение. Переключись в Minecraft и нажми F2 после появления сообщения Watching." -ForegroundColor Cyan
                & pwsh -NoProfile -File $core @argsList
                if ($LASTEXITCODE -ne 0) { Write-Warning "Сборщик завершился с ошибкой" }
            }
            "6" {
                if (-not (Need-Run)) { break }
                $path = Read-Host "Полный путь к существующему F2 PNG"
                if ($path) {
                    $response = Invoke-Core @("-Action", "capture", "-RunPath", $script:activeRun, "-FilePath", $path)
                    if ($null -ne $response) {
                        if ($response.status -eq "CAPTURED") {
                            Write-Host ("CAPTURED: " + $response.relativePath) -ForegroundColor Green
                        } else {
                            Write-Host ([string]$response.status)
                        }
                    }
                }
            }
            "7" {
                if (-not (Need-Run)) { break }
                $note = Read-Host "Замечание (до 4000 символов)"
                if ($note) {
                    $response = Invoke-Core @("-Action", "note", "-RunPath", $script:activeRun, "-NoteText", $note)
                    if ($null -ne $response) { Write-Host "Заметка сохранена" -ForegroundColor Green }
                }
            }
            "8" {
                if (-not (Need-Run)) { break }
                $response = Invoke-Core @("-Action", "completeness", "-RunPath", $script:activeRun)
                if ($null -ne $response) {
                    Write-Host ("Сцена: " + $response.sceneId)
                    Write-Host ("F2 оригинал / порт: " + $response.legacyScreenshots + " / " + $response.modernScreenshots)
                    Write-Host ("Минимум для каждой стороны: " + $response.requiredScreenshotsPerSide)
                    Write-Host ("Полнота: " + $response.processing)
                    Write-Host "Это подсчёт файлов, а не оценка соответствия игры."
                }
            }
            "9" {
                if (-not (Need-Run)) { break }
                $response = Invoke-Core @("-Action", "verify", "-RunPath", $script:activeRun)
                if ($null -ne $response) {
                    Write-Host ("Контрольные суммы: корректно " + $response.valid + ", повреждено/отсутствует " + $response.invalid)
                    foreach ($issue in @($response.issues)) { Write-Warning ($issue.id + ": " + $issue.issue) }
                }
            }
            "C" {
                if (-not (Need-Run)) { break }
                $scene = Read-Host "SceneId (Enter = активная сцена)"
                $argsList = @("-RunPath", $script:activeRun)
                if ($scene) { $argsList += @("-SceneId", $scene) }
                $result = Invoke-Cycle3 $comparisonTool $argsList
                if ($null -ne $result -and $result.status -eq "REVIEW_VIEWER_CREATED") {
                    Write-Host ("HTML: " + $result.htmlPath) -ForegroundColor Green
                    Write-Host "Difference рассчитывается только в браузере; это не игровой PASS."
                }
            }
            "R" {
                if (-not (Need-Run)) { break }
                $result = Invoke-Cycle3 $reportTool @("-Action", "export", "-RunPath", $script:activeRun)
                if ($null -ne $result -and $result.status -eq "EXPORT_VERIFIED") {
                    Write-Host ("Архив: " + $result.path) -ForegroundColor Green
                    Write-Host ("SHA-256: " + $result.sha256)
                    Write-Host "Логи, видео и частные заметки не включены. Игровой результат: NOT_RUN."
                }
            }
            "I" {
                $package = Read-Host "Полный путь к ZIP-пакету"
                if (-not $package) { break }
                $result = Invoke-Cycle3 $reportTool @("-Action", "verify-archive", "-ArchivePath", $package)
                if ($null -eq $result -or $result.status -ne "ARCHIVE_VERIFIED") { break }
                Write-Host ("ZIP проверен: " + $result.files + " файлов; SHA-256: " + $result.sha256) -ForegroundColor Green
                $target = Read-Host "Каталог для импорта (Enter = только проверить)"
                if ($target) {
                    $importResult = Invoke-Cycle3 $reportTool @("-Action", "import", "-ArchivePath", $package, "-ImportRoot", $target)
                    if ($null -ne $importResult) { Write-Host ("Импорт: " + $importResult.status + "; " + $importResult.path) -ForegroundColor Green }
                }
            }
            "L" {
                if (-not (Need-Run)) { break }
                $path = Read-Host "Полный путь к .log (Enter = latest.log из локального конфига)"
                $argsList = @("-Action", "snapshot-log", "-RunPath", $script:activeRun)
                if ($path) { $argsList += @("-LogPath", $path) }
                if ($ConfigPath) { $argsList += @("-ConfigPath", $ConfigPath) }
                $result = Invoke-Cycle3 $mediaTool $argsList
                if ($null -ne $result) {
                    Write-Host ("Лог: " + $result.status) -ForegroundColor Green
                    if ($null -ne $result.PSObject.Properties['sha256']) { Write-Host ("SHA-256: " + $result.sha256) }
                    if ($null -ne $result.PSObject.Properties['privacyFlags'] -and @($result.privacyFlags).Count -gt 0) {
                        Write-Host ("Сигналы приватности: " + (@($result.privacyFlags) -join ", "))
                    }
                    Write-Host "Ручная проверка приватности обязательна. В ZIP лог не включён."
                }
            }
            "V" {
                if (-not (Need-Run)) { break }
                $path = Read-Host "Полный путь к существующему MP4/MKV/WebM/MOV"
                if (-not $path) { break }
                $result = Invoke-Cycle3 $mediaTool @("-Action", "index-video", "-RunPath", $script:activeRun, "-VideoPath", $path)
                if ($null -ne $result) {
                    Write-Host ("Видео: " + $result.status) -ForegroundColor Green
                    if ($null -ne $result.PSObject.Properties['sha256']) { Write-Host ("SHA-256: " + $result.sha256) }
                    Write-Host "Видео не скопировано и не декодировано; привязка к сцене требует проверки."
                }
            }
            "M" {
                if (-not (Need-Run)) { break }
                $status = Invoke-Cycle3 $mediaTool @("-Action", "status", "-RunPath", $script:activeRun)
                $verification = Invoke-Cycle3 $mediaTool @("-Action", "verify", "-RunPath", $script:activeRun)
                if ($null -ne $status -and $null -ne $verification) {
                    Write-Host ("Логи: " + $status.logsCaptured + "; видео: " + $status.videosIndexed)
                    Write-Host ("Ожидают проверки приватности: " + $status.pendingPrivacyReview)
                    Write-Host ("SHA-256 корректны: " + $verification.valid + "; ошибки: " + $verification.invalid)
                    foreach ($entry in @($status.videoIdsForPlanning)) {
                        Write-Host ("Видео для плана: " + $entry.id + " / " + $entry.side + " / " + $entry.sceneId)
                    }
                    foreach ($issue in @($verification.issues)) { Write-Warning ($issue.id + ": " + $issue.issue) }
                    Write-Host "Это локальный учёт файлов; игровой результат остаётся NOT_RUN."
                }
            }
            "E" {
                if (-not (Need-Run)) { break }
                $mode = (Read-Host "Окружение: C = снять, V = проверить изменения на диске, Enter = состояние").Trim().ToUpperInvariant()
                $action = switch ($mode) { 'C' { 'capture' }; 'V' { 'verify' }; default { 'status' } }
                $argsList = @("-Action", $action, "-RunPath", $script:activeRun)
                if ($ConfigPath) { $argsList += @("-ConfigPath", $ConfigPath) }
                $result = Invoke-Cycle3 $environmentTool $argsList
                if ($null -ne $result) {
                    Write-Host ("Окружение: " + $result.status) -ForegroundColor Green
                    if ($null -ne $result.PSObject.Properties['rawMismatchKeys']) {
                        Write-Host ("Разные сырые параметры: " + (@($result.rawMismatchKeys) -join ', '))
                    }
                    if ($null -ne $result.PSObject.Properties['driftFields']) {
                        Write-Host ("Изменения после снимка: " + (@($result.driftFields) -join ', '))
                    }
                    Write-Host "Сравнение файлов не подтверждает Java, камеру и игровую калибровку."
                }
            }
            "T" {
                if (-not (Need-Run)) { break }
                $mode = (Read-Host "Видео: P = новый план, V = проверить хеши, Enter = состояние").Trim().ToUpperInvariant()
                if ($mode -eq 'P') {
                    Write-Host "Посмотри ID файлов через M. Нужны видео ОДНОЙ сцены: legacy и modern." -ForegroundColor Yellow
                    $leftId = Read-Host 'ID видео legacy'
                    $rightId = Read-Host 'ID видео modern'
                    $leftTime = Read-Host 'Начало legacy, секунды (например 5.25)'
                    $rightTime = Read-Host 'Начало modern, секунды (например 7.25)'
                    $duration = Read-Host 'Длительность общего отрезка, секунды (например 10)'
                    if (-not $leftId -or -not $rightId -or -not $leftTime -or -not $rightTime -or -not $duration) {
                        Write-Warning 'Все поля плана обязательны'; break
                    }
                    $argsList = @('-Action', 'plan', '-RunPath', $script:activeRun,
                        '-LegacyVideoId', $leftId, '-ModernVideoId', $rightId,
                        '-LegacyStartSeconds', $leftTime, '-ModernStartSeconds', $rightTime,
                        '-DurationSeconds', $duration)
                } else {
                    $action = if ($mode -eq 'V') { 'verify' } else { 'status' }
                    $argsList = @('-Action', $action, '-RunPath', $script:activeRun)
                }
                $result = Invoke-Cycle3 $videoTool $argsList
                if ($null -ne $result) {
                    Write-Host ("Видео: " + $result.status) -ForegroundColor Green
                    if ($null -ne $result.PSObject.Properties['planId']) { Write-Host ("План: " + $result.planId) }
                    if ($null -ne $result.PSObject.Properties['planIdsForProbe']) {
                        foreach ($entry in @($result.planIdsForProbe)) {
                            Write-Host ("План для ffprobe: " + $entry.id + " / " + $entry.sceneId)
                        }
                    }
                    if ($null -ne $result.PSObject.Properties['invalid']) {
                        Write-Host ("Хеши: корректно " + $result.valid + ", ошибки " + $result.invalid)
                    }
                    Write-Host "Нарезка и синхронизация камеры не выполнены. Игровой результат: NOT_RUN."
                }
            }
            "B" {
                if (-not (Need-Run)) { break }
                $mode = (Read-Host "ffprobe: I = проверить видео, P = предпросмотр команд, V = перепроверить SHA, Enter = состояние").Trim().ToUpperInvariant()
                $action = switch ($mode) { 'I' { 'inspect' }; 'P' { 'preview' }; 'V' { 'verify' }; default { 'status' } }
                $argsList = @('-Action', $action, '-RunPath', $script:activeRun)
                if ($action -in @('inspect', 'preview')) {
                    $id = Read-Host 'PlanId (получить через пункт T, режим состояния)'
                    if (-not $id) { Write-Warning 'Нужен PlanId'; break }
                    $argsList += @('-PlanId', $id)
                    if ($action -eq 'inspect') {
                        $toolPath = Read-Host 'Путь к ffprobe.exe (Enter = ffprobe из PATH)'
                        if ($toolPath) { $argsList += @('-FfprobePath', $toolPath) }
                    }
                }
                $result = Invoke-Cycle3 $videoProbeTool $argsList
                if ($null -ne $result) {
                    Write-Host ("ffprobe/ffmpeg: " + $result.status) -ForegroundColor Green
                    if ($null -ne $result.PSObject.Properties['probeId']) { Write-Host ("Probe: " + $result.probeId) }
                    if ($null -ne $result.PSObject.Properties['previewId']) { Write-Host ("Preview: " + $result.previewId) }
                    if ($null -ne $result.PSObject.Properties['invalid']) {
                        Write-Host ("Сверка источников: " + $result.valid + " корректно, " + $result.invalid + " ошибок")
                    }
                    Write-Host 'Команды не выполнялись, видео не обрезалось. Совпадение кадров НЕ подтверждено.'
                }
            }
            "F" {
                if (-not (Need-Run)) { break }
                $mode = (Read-Host 'Клипы: R = нарезать, V = проверить SHA-256, Enter = состояние').Trim().ToUpperInvariant()
                $action = switch ($mode) { 'R' { 'render' }; 'V' { 'verify' }; default { 'status' } }
                $argsList = @('-Action', $action, '-RunPath', $script:activeRun)
                if ($action -eq 'render') {
                    $preview = Read-Host 'PreviewId (из пункта B, режим P)'
                    if (-not $preview) { Write-Warning 'Нужен PreviewId'; break }
                    Write-Host 'Будут созданы два новых MP4 в текущем RUN. Это перекодирование без проверки визуального совпадения.' -ForegroundColor Yellow
                    $approval = Read-Host 'Для запуска ffmpeg напиши RENDER (Enter = отмена)'
                    if ($approval -cne 'RENDER') { Write-Host 'Нарезка отменена'; break }
                    $argsList += @('-PreviewId', $preview)
                    $ffmpeg = Read-Host 'Путь к ffmpeg.exe (Enter = ffmpeg из PATH)'
                    if ($ffmpeg) { $argsList += @('-FfmpegPath', $ffmpeg) }
                }
                $result = Invoke-Cycle3 $videoRenderTool $argsList
                if ($null -ne $result) {
                    Write-Host ('Клипы: ' + $result.status) -ForegroundColor Green
                    if ($null -ne $result.PSObject.Properties['legacyPath']) {
                        Write-Host ('Legacy: ' + $result.legacyPath)
                        Write-Host ('Modern: ' + $result.modernPath)
                    }
                    if ($null -ne $result.PSObject.Properties['successfulPairs']) {
                        Write-Host ('Успешно закодированных пар: ' + $result.successfulPairs)
                    }
                    if ($null -ne $result.PSObject.Properties['invalid']) {
                        Write-Host ('SHA-256: корректно ' + $result.valid + '; ошибки ' + $result.invalid)
                    }
                    Write-Host 'Исходные видео не перезаписываются. Клипы не включаются в ZIP и требуют ручной проверки.'
                    Write-Host 'Синхронизация камеры и игровой результат: NOT_RUN.'
                }
            }
            "S" {
                if (-not (Need-Run)) { break }
                $response = Invoke-Core @("-Action", "status", "-RunPath", $script:activeRun)
                if ($null -ne $response) {
                    Write-Host ("RUN: " + $response.runId)
                    Write-Host ("Сцена: " + $response.activeSceneId + " / " + $response.activeSide)
                    Write-Host ("F2: " + $response.evidenceCount + "; заметки: " + $response.noteCount)
                    Write-Host ("Калибровка: " + $response.calibrationStatus + "; игровой результат: " + $response.observation)
                }
            }
            "0" { Write-Host "Сессия сохранена. Выход."; return }
            default { Write-Warning "Неизвестный пункт меню" }
        }
    } catch {
        Write-Warning $_.Exception.Message
        Write-Host "Сохранённые результаты не удалены. Можно повторить действие." -ForegroundColor Yellow
    }
}
