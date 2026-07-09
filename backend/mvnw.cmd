@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup script (Windows)
@REM Downloads the correct Maven version defined in .mvn\wrapper\maven-wrapper.properties
@REM and runs it, so contributors don't need Maven installed globally.
@REM ----------------------------------------------------------------------------
@ECHO OFF

SET BASE_DIR=%~dp0
SET WRAPPER_JAR=%BASE_DIR%.mvn\wrapper\maven-wrapper.jar
SET WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Downloading Maven Wrapper jar...
  powershell -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%'"
)

IF "%JAVA_HOME%"=="" (
  SET JAVA_EXE=java
) ELSE (
  SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*
