#!/bin/bash

# Configuration
EVOSUITE_JAR="lib/evosuite/evosuite-1.0.6.jar"
GSON_JAR="/home/gongas/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/2.13.1/853ce06c11316b33a8eae5e9095da096a9528b8f/gson-2.13.1.jar"
PROJECT_CP="build/classes/java/main:$GSON_JAR"
BUDGET=30 # seconds per class

echo "Compiling project..."
./gradlew compileJava

# Find all classes excluding Main and anonymous/inner classes
CLASSES=$(find build/classes/java/main -name "*.class" | grep -v "Main.class" | grep -v "\$" | sed 's|build/classes/java/main/||; s|/|.|g; s|.class||')

for CLASS in $CLASSES; do
    echo "--------------------------------------------------------"
    echo "Generating tests for: $CLASS"
    echo "--------------------------------------------------------"
    
    java -jar "$EVOSUITE_JAR" \
        -class "$CLASS" \
        -projectCP "$PROJECT_CP" \
        -Dsearch_budget="$BUDGET" \
        -Dstopping_condition=MaxTime \
        -Dshow_progress=false
done

echo "Done! Tests generated in evosuite-tests/"
