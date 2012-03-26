call srcs.bat
javac -cp "./dist/lib/systray4j.jar;./dist/lib/log4j.jar;./dist/lib/jdom.jar" -d cls -sourcepath src @sources.txt -Xlint:unchecked
pause
::src\ua\group42\taskmanager\StartWrapper.java src\ua\group42\taskmanager\server\ServerSideWrapper.java  src\ua\group42\taskmanager\clientside\ClientSideWrapper.java
