@echo off
pushd "%~dp005_neoforge_port"
call gradlew.bat %*
popd
