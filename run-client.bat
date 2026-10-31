@echo off
setlocal
pushd "%~dp005_neoforge_port"
call run-client.bat %*
set "TC_CLIENT_EXIT=%ERRORLEVEL%"
popd
exit /b %TC_CLIENT_EXIT%
