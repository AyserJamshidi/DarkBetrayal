@ECHO off
TITLE Neon-Dev - Eleanora Server Panel
:MENU
CLS
ECHO.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                         Game Server Panel 	                           ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Development                                       4 - Quit        ^|
ECHO   ^|    2 - Production                                                        ^|
ECHO   ^|    3 - Production X2                                                     ^|
ECHO   ^|                                                                          ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.

SET JAVA_OPTS=-Xms1000m -Xmx8000m -server -Dfile.encoding=UTF-8 -ea -cp ./lib/*;gameserver-3.0.1.jar com.ne.gs.GameServer