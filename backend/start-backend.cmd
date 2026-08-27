@echo off
setlocal enabledelayedexpansion

:: Run PowerShell script with ExecutionPolicy Bypass so it loads .env and local Maven
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start-backend.ps1"
