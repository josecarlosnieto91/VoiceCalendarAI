#!/bin/sh
# Gradle wrapper start script

# Add default JVM options here.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")
# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD=maximum

die() {
    echo
    echo "$*"
    echo
    exit 1
}

# Determine the Java command to use
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

if [ ! -x "$JAVACMD" ] ; then
    die "ERROR: JAVA_HOME is not set and no 'java' command could be found."
fi

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the JVM command to use to start the wrapper
exec "$JAVACMD" \
    $DEFAULT_JVM_OPTS \
    $JAVA_OPTS \
    $GRADLE_OPTS \
    "-Dorg.gradle.appname=$APP_BASE_NAME" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
