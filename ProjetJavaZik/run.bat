@echo off
REM Lance JavaZic en mode graphique (Swing).
REM Se place dans le dossier du .bat pour que "data/" soit trouve.
cd /d "%~dp0"
start "" javaw -jar JavaZic.jar
