@echo off
setlocal
echo === Iniciando Atualizacao do Git: %CD% ===

:: Garante que a branch seja main
git branch -m main 2>nul

:: Adiciona todas as alteracoes
git add .

:: Commita as alteracoes com timestamp
set datetime=%date% %time%
git commit -m "Backup Automatico: %datetime%"

:: Sincroniza com o remoto (prioriza local)
:: Nota: Pode falhar se o 'origin' nao estiver configurado
git pull origin main --rebase -X ours 2>nul

:: Envia para o GitHub
git push origin main --force 2>nul

echo === Atualizacao concluida em %CD% ===
timeout /t 2 >nul
endlocal
