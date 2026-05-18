@echo off
title Coffee^&Books - Inicializador do Sistema
echo ============================================
echo    INICIALIZANDO COFFEE ^& BOOKS ERP
echo ============================================

:: Auto-detect Maven executable path
set "MVN_CMD=mvn"
if exist "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" (
    set "MVN_CMD=C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"
)

echo [1/2] Compilando o projeto com Maven...
call "%MVN_CMD%" clean compile

if %ERRORLEVEL% NEQ 0 (
    echo Erro na compilacao. Verifique se o Maven esta instalado.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Iniciando o sistema Coffee^&Books...
call "%MVN_CMD%" exec:java -Dexec.mainClass="view.LoginFrame"

echo ============================================
echo Sistema encerrado.
pause
