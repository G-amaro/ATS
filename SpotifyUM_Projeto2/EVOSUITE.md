# EvoSuite Integration in SpotifUM

This project has been configured to use EvoSuite 1.0.6 for automated test generation.

## 🚀 Quick Reference

| Action | Command |
| :--- | :--- |
| **Generate tests for all classes** | `./gradlew evosuiteGenerateAll` |
| **Run only EvoSuite tests** | `./gradlew testEvoSuite` |
| **Run all tests (Standard + EvoSuite)** | `./gradlew test testEvoSuite` |

## Prerequisites
- Java 8 (refactoring completed)
- EvoSuite jars (located in `lib/evosuite/`)

## How to generate tests

### For all classes
A Gradle task has been created to automate this process. It identifies all compiled classes and runs EvoSuite for each (with a budget of 30s per class).

Run:
```bash
./gradlew evosuiteGenerateAll
```

### For a specific class manually
Run the following command (replace `CLASS_NAME` with the full class name):

```bash
java -jar lib/evosuite/evosuite-1.0.6.jar \
  -class CLASS_NAME \
  -projectCP build/classes/java/main:/home/gongas/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/2.13.1/853ce06c11316b33a8eae5e9095da096a9528b8f/gson-2.13.1.jar \
  -Dsearch_budget=60 \
  -Dstopping_condition=MaxTime \
  -Dtest_dir=src/evosuite-tests
```

Example for `Album`:
```bash
java -jar lib/evosuite/evosuite-1.0.6.jar -class org.spotifumtp37.model.album.Album -projectCP build/classes/java/main:/home/gongas/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/2.13.1/853ce06c11316b33a8eae5e9095da096a9528b8f/gson-2.13.1.jar -Dsearch_budget=60 -Dstopping_condition=MaxTime -Dtest_dir=src/evosuite-tests
```

## How to run EvoSuite tests

The generated tests are saved in `src/evosuite-tests/` and integrated into the Gradle build.

To run them:
```bash
./gradlew testEvoSuite
```

To run all tests (standard + EvoSuite):
```bash
./gradlew test testEvoSuite
```

## Configuration Details
- **Dependencies**: `evosuite-standalone-runtime-1.0.6.jar` and `junit:junit:4.12` added to `testImplementation`.
- **Source Sets**: `evosuite-tests` directory added to the test source set.
- **Gradle Tasks**: Custom `testEvoSuite` task registered to run JUnit 4 tests with `_ESTest` suffix.
