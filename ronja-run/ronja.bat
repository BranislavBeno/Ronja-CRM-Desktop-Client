@echo off

rem jdeps --print-module-deps --ignore-missing-deps .\ronja-desktop.jar
rem jlink --verbose --strip-debug --no-man-pages --no-header-files --compress 2 --output runtime --module-path %JAVA_HOME%\jmods --add-modules java.base,java.desktop,java.instrument,java.management.rmi,java.naming,java.prefs,java.scripting,java.security.jgss,java.sql,jdk.httpserver,jdk.jfr,jdk.unsupported

set JAVA_HOME=%CD%\runtime
set PATH=%JAVA_HOME%\bin;%PATH%
echo %JAVA_HOME%
start javaw -jar "%CD%\app\ronja-desktop.jar"