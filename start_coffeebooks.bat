@echo off
title Coffee&Books - Inicializador do Sistema
echo ============================================
echo    INICIALIZANDO COFFEE & BOOKS ERP
echo ============================================
echo [1/2] Compilando o projeto com Maven...
call mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo Erro na compilacao. Verifique se o Maven esta instalado.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Iniciando o sistema Coffee&Books...
call mvn exec:java -Dexec.mainClass="view.MainFrame"

echo ============================================
echo Sistema encerrado.
pause
