#!/bin/sh

# jdeps --print-module-deps --ignore-missing-deps ./app/ronja-desktop.jar
# jlink --verbose --strip-java-debug-attributes --no-man-pages --no-header-files --compress 2 --output runtime --module-path $JAVA_HOME/jmods --add-modules java.base,java.desktop,java.instrument,java.management.rmi,java.naming,java.prefs,java.scripting,java.security.jgss,java.sql,jdk.httpserver,jdk.jfr,jdk.unsupported

JAVA_CURRENT=./runtime/bin
echo $JAVA_CURRENT
$JAVA_CURRENT/java -jar ./app/ronja-desktop.jar
