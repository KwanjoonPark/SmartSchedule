@echo off
setlocal
if exist build rmdir /s /q build
mkdir build\classes
mkdir build\javadoc
dir /s /b src\main\java\*.java > build\sources.txt
javac --release 17 -d build\classes @build\sources.txt
(
  echo Manifest-Version: 1.0
  echo Main-Class: com.smartschedule.SmartScheduleApp
  echo.
) > build\MANIFEST.MF
jar cfm build\SmartSchedule.jar build\MANIFEST.MF -C build\classes .
javadoc -quiet -d build\javadoc -sourcepath src\main\java -subpackages com.smartschedule
echo Built build\SmartSchedule.jar
endlocal
