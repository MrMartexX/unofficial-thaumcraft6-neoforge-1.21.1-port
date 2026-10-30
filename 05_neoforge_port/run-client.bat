@echo off
setlocal
pushd "%~dp0"
title Thaumic Research - NeoForge 1.21.1 Client

if not exist "gradlew.bat" (
    echo [ERROR] gradlew.bat was not found in %CD%
    popd
    pause
    exit /b 1
)

echo Starting the existing development client without clean or full build...
call gradlew.bat runClient --no-daemon %*
set "TC_CLIENT_EXIT=%ERRORLEVEL%"

popd
if not "%TC_CLIENT_EXIT%"=="0" (
    echo.
    echo Client launch failed with exit code %TC_CLIENT_EXIT%.
    pause
)
exit /b %TC_CLIENT_EXIT%
