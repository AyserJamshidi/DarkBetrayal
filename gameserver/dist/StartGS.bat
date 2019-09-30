@ECHO off
TITLE Neon-Dev - Game Server Console
:START
CLS
IF "%MODE%" == "" (
CALL PanelGS.bat
)
ECHO Starting Eleanora Game Server in %MODE% mode.
JAVA %JAVA_OPTS% -Dfile.encoding=UTF-8 -ea -cp ./lib/*;gameserver-3.0.1.jar com.ne.gs.GameServer
SET CLASSPATH=%OLDCLASSPATH%
IF ERRORLEVEL 2 GOTO START
IF ERRORLEVEL 1 GOTO ERROR
IF ERRORLEVEL 0 GOTO END
:ERROR
ECHO.
ECHO Game Server has terminated abnormaly!
ECHO.
PAUSE
EXIT
:END
ECHO.
ECHO Game Server is terminated!
ECHO.
PAUSE
EXIT