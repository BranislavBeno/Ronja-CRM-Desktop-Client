@echo off

rem jdeps --print-module-deps --ignore-missing-deps .\app\ronja-desktop.jar
rem jlink --verbose --strip-debug --no-man-pages --no-header-files --compress 2 --output runtime --module-path %JAVA_HOME%\jmods --add-modules java.base,java.desktop,java.instrument,java.management,java.naming,java.prefs,java.rmi,java.scripting,java.sql,jdk.httpserver,jdk.jfr,jdk.unsupported

start %CD%\runtime\bin\javaw -jar app\ronja-desktop.jar
