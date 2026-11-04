[CmdletBinding()]
param(
    [string]$RunPath,
    [string]$ResultsRoot,
    [string]$ConfigPath
)
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$core = Join-Path $PSScriptRoot "session.ps1"
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
    if ($item.capture.video) { Write-Host "Дополнительно требуется видео (не автоматизировано в этом цикле)." }
    if ($item.capture.logs) { Write-Host "Дополнительно требуется лог (не автоматизировано в этом цикле)." }
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
                    if ($null -ne $response) { Write-Host ($response.status + ": " + $response.relativePath) }
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
