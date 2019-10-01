@ECHO off
TITLE Lmfaoown - Game Server Console
:START
CLS
ECHO Starting Lmfaoown Game Server
JAVA -Xms1000m -Xmx8000m -server -Dfile.encoding=UTF-8 -ea -cp ./lib/*;gameserver-3.0.1.jar com.ne.gs.GameServer

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