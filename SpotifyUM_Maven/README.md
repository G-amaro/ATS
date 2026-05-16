# SpotifUM Testing and Coverage Guide (Maven)

This project is a Java-based music management system with integrated testing using JUnit 5, EvoSuite for automated test generation, Jqwik for property-based testing (QuickCheck), Pitest for mutation testing, and JaCoCo for code coverage.

## 🚀 Quick Reference

| Action | Command |
| :--- | :--- |
| **Run All Unit Tests** | `mvn test` |
| **Run Standard Tests Only** | `mvn test -Dtest=org.spotifumtp37.*` |
| **Run QuickCheck Tests Only** | `mvn test -Dtest=QuickcheckTests` |
| **Generate EvoSuite Tests** | `mvn evosuite:generate` |
| **Run EvoSuite Tests Only** | `mvn test -Dtest=*_ESTest` |
| **Run Mutation Testing** | `mvn pitest:mutationCoverage` |
| **Generate Coverage Report** | `mvn jacoco:report` |

---

## 🧪 Standard Tests (JUnit 5)

The standard manual tests are located in `src/test/java/`. These tests use JUnit 5.

To run the standard tests:
```bash
mvn test
```
*(Note: This command runs all tests included in the test source paths, including QuickCheck and EvoSuite if not explicitly excluded).*

The reports can be found in `target/surefire-reports/`.

---

## 🔍 QuickCheck (Property-Based Testing)

QuickCheck-style tests using **Jqwik** are located in `src/quickcheckTests/`. These tests use property-based testing and CSV data sources for validation.

To run these tests specifically:
```bash
mvn test -Dtest=QuickcheckTests
```

---

## 🤖 EvoSuite (Automated Testing)

EvoSuite 1.0.6 is used for automated test generation. Generated tests are stored in `src/evosuiteTests/java/`.

### Generate tests for a specific class
```bash
mvn evosuite:generate -Dclass=org.example.Model.Song
```

### Run EvoSuite tests
To run specifically the generated tests:
```bash
mvn test -Dtest=*_ESTest
```

---

## 👾 Pitest (Mutation Testing)

Pitest is used to evaluate the quality of the tests by injecting faults (mutations) into the code.

To run mutation testing:
```bash
mvn pitest:mutationCoverage
```
The report will be available at: `target/pit-reports/index.html`

---

## 📊 JaCoCo (Code Coverage)

JaCoCo is configured to measure the coverage of the tests.

### Generate Coverage Report
Run the following command to execute the tests and generate the HTML report:
```bash
mvn jacoco:prepare-agent test jacoco:report
```

### Viewing the Report
After running the command, you can find the coverage report at:
`target/site/jacoco/index.html`

---

## Prerequisites
- **Java 8**: The project is configured for Java 8 compatibility (`maven.compiler.source=1.8`).
- **Maven**: Ensure you have Maven installed and configured.
- **Mockito 4.11.0**: Used for mocking in JUnit 5 tests.
