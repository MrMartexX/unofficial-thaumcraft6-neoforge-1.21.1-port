@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0..\..\tools\fixtures\restore-parity-worlds.ps1"
pause
