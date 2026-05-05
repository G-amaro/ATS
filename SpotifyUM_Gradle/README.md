# SpotifUM Testing and Coverage Guide

This project is a Java-based music management system with integrated testing using JUnit 5, EvoSuite for automated test generation, and JaCoCo for code coverage.

## 🚀 Quick Reference

| Action | Command |
| :--- | :--- |
| **Run Standard Tests** | `./gradlew test` |
| **Generate EvoSuite Tests** | `./gradlew evosuiteGenerateAll` |
| **Run EvoSuite Tests** | `./gradlew testEvoSuite` |
| **Run All Tests** | `./gradlew test testEvoSuite` |
| **Generate Coverage Report** | `./gradlew jacocoTestReport` |

---

## 🧪 Standard Tests (JUnit 5)

The standard manual tests are located in `src/test/java/`. These tests use JUnit 5.

To run the standard tests:
```bash
./gradlew test
```
The reports can be found in `build/reports/tests/test/index.html`.

---

## 🤖 EvoSuite (Automated Testing)

EvoSuite 1.0.6 is used for automated test generation. Generated tests are stored in `src/evosuite-tests/`.

### Generate tests for all classes
This task identifies all compiled classes and runs EvoSuite for each (30s budget per class):
```bash
./gradlew evosuiteGenerateAll
```

### Run EvoSuite tests
To run only the generated tests:
```bash
./gradlew testEvoSuite
```

---

## 📊 JaCoCo (Code Coverage)

JaCoCo is configured to measure the coverage of both standard and EvoSuite tests.

### Generate Coverage Report
Run the following command to execute the tests and generate the HTML report:
```bash
./gradlew jacocoTestReport
```

*Note: This task is configured to depend on both `test` and `testEvoSuite`, ensuring that the report reflects the combined coverage.*

### Viewing the Report
After running the command, you can find the coverage report at:
`build/reports/jacoco/index.html`

---

## Prerequisites
- **Java 8**: The project is configured for Java 8 compatibility.
- **Gradle**: Uses the Gradle wrapper (`./gradlew`).
- **EvoSuite Jars**: Located in `lib/evosuite/`.
