# Selenium UI Automation Framework

A production-grade **Java + Selenium 4 + TestNG** UI test automation framework built on the **Page Object Model (POM)** pattern. Designed for scalability, parallel execution, and CI/CD integration — with support for local, headless, and remote Selenium Grid runs.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Test Data](#test-data)
- [Parallel Execution & Selenium Grid](#parallel-execution--selenium-grid)
- [Reporting](#reporting)
- [CI/CD — Jenkins Pipeline](#cicd--jenkins-pipeline)

---

## Overview

This framework automates end-to-end UI tests for a sample e-commerce application. It covers the full user journey — Login → Products Dashboard → Cart → Place Order → Order Confirmation — with **20+ test scenarios**.

The framework was built with the following goals in mind:

- **Thread-safe parallel execution** using `ThreadLocal<WebDriver>`
- **Data-driven testing** via JSON test data files and TestNG `@DataProvider`
- **Flexible browser support** — Chrome, Firefox, Edge (local or remote)
- **Clean separation of concerns** — page actions, test logic, utilities, and configuration are fully decoupled
- **Allure reporting** with screenshots auto-attached on failure

---

## Tech Stack

| Layer              | Technology                     |
|--------------------|--------------------------------|
| Language           | Java 23                        |
| Build Tool         | Maven                          |
| Test Framework     | TestNG 7.12.0                  |
| Browser Automation | Selenium 4.43.0                |
| Reporting          | Allure 2.29.0                  |
| Logging            | Apache Log4j2 2.24.3           |
| JSON Parsing       | Jackson Databind 2.21.0        |
| CI/CD              | Jenkins (Declarative Pipeline) |
| Design Pattern     | Page Object Model (POM)        |

---

## Project Structure

```
selenium-ui-automation-framework/
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── pages/                    # Page Object classes
│   │       └── utils/                    # Utility/helper classes
│   └── test/
│       ├── java/
│       │   ├── tests/                    # Test classes
│       │   └── listeners/               # TestNG event listeners
│       └── resources/
│           └── testdata/
├── jenkins_pipeline.groovy               # Declarative Jenkins pipeline
└── pom.xml
```

---

## Features

### Page Object Model (POM)
Each application page is represented by a dedicated Java class that encapsulates all locators and page actions. Test classes interact only with page methods — never with raw Selenium calls — keeping tests readable and maintainable.

### ThreadLocal WebDriver
`BaseTest` stores each `WebDriver` instance in a `ThreadLocal`, ensuring complete thread isolation during parallel test runs. The `teardown()` method explicitly calls `driverThread.remove()` to prevent memory leaks in thread pools.

### Multi-Browser Support
Supports **Chrome**, **Firefox**, and **Edge** out of the box. Browser selection is driven by:
1. TestNG `@Parameters("browser")` in the XML suite (takes priority)
2. `browser` key in `config.properties` (fallback)

### Headless Execution
Set `headless=true` in `config.properties` to run all browsers in headless mode — no display server required, ideal for CI environments.

### Selenium Grid / Remote Execution
Set `remote=true` and configure `grid.url` in `config.properties` to route all tests through a Selenium Grid hub. `initRemoteDriver()` in `BaseTest` handles `RemoteWebDriver` setup automatically.

### Data-Driven Testing with JSON
Test data lives in JSON files under `src/test/resources/testdata/`. The `JSONReader` utility reads test cases by ID, and TestNG `@DataProvider` feeds them into parameterized tests:

### Wait Mechanisms
`SeleniumUtils` provides a layered waiting strategy:

| Mechanism            | Usage                                                                                                 |
|----------------------|-------------------------------------------------------------------------------------------------------|
| `WebDriverWait`      | `visibilityOfElementLocated`, `elementToBeClickable`, `urlContains`                                   |
| `FluentWait`         | Custom polling (500 ms interval), ignores `NoSuchElementException` & `StaleElementReferenceException` |
| `ImplicitWait`       | Global timeout configured from `config.properties`                                                    |
| `ExpectedConditions` | `invisibilityOfElementLocated` for spinner/overlay detection                                          |

### Allure Reporting with `@Step` Annotations
Page Object methods are annotated with `@Step`, `@Feature`, `@Story`, `@Description`, and `@Severity` to produce rich, human-readable Allure reports. Screenshots are automatically attached to failing test results by `ScreenshotListener`.

### Retry Mechanism
`RetryAnalyzerImpl` retries failed tests up to **2 times** before marking them as failed. `RetryListener` applies the retry analyzer globally via the `testng-grid.xml` suite, eliminating the need to annotate every `@Test`.

### Screenshot on Failure
`ScreenshotListener` implements `ITestListener`. On `onTestFailure`, it grabs the driver from `BaseTest`, captures a PNG screenshot, and attaches it to the Allure report as a named artifact (`FAILED_<methodName>`).

### Structured Logging
Log4j2 is configured with rolling file appenders and console output. Every page action, wait, assertion, and thread event is logged at the appropriate level (`DEBUG`, `INFO`, `WARN`, `ERROR`) for full test traceability.

### TestNG Groups
Tests that require an authenticated session are tagged with `groups = {"requiresLogin"}`. `BaseTest` has a dedicated `@BeforeMethod(onlyForGroups = "requiresLogin")` that performs the login step automatically — keeping individual test methods focused on their own assertions.

---

## Prerequisites

- **Java 23+** installed and `JAVA_HOME` configured
- **Maven 3.8+** on the `PATH`
- **Google Chrome / Firefox / Edge** installed (for local runs)
- **Allure CLI 2.29.0** (bundled under `.allure/`) for generating reports locally

---

## Setup & Installation

```bash
# 1. Clone the repository
git clone https://github.com/your-username/selenium-ui-automation-framework.git
cd selenium-ui-automation-framework

# 2. Install dependencies
mvn clean install -DskipTests
```

---

## Configuration

All runtime settings live in `src/test/resources/config.properties`:

```properties
url=https://rahulshettyacademy.com/client
browser=chrome          # chrome | firefox | edge
timeout=5               # implicit wait in seconds
headless=false          # true for CI / no-display environments
remote=false            # true to run against Selenium Grid
grid.url=http://localhost:4444/wd/hub
```

Any property can be overridden at runtime via Maven system properties, which takes precedence over the file (Jenkins-friendly):

```bash
mvn test -Dbrowser=firefox -Dheadless=true -Durl=https://staging.yourapp.com
```

---

## Running Tests

### Default suite (sequential)
```bash
mvn test
```

### Parallel suite with Grid
```bash
mvn test -Dsuite=testng-grid
```

### Specific browser
```bash
mvn test -Dbrowser=firefox
```

### Headless mode
```bash
mvn test -Dheadless=true
```

### Against a custom URL
```bash
mvn test -Durl=https://staging.yourapp.com
```

---

## Test Data

Test data is stored as structured JSON under `src/test/resources/testdata/`.

---

## Parallel Execution & Selenium Grid

### `testng-grid.xml` — Parallel Configuration

Five test groups run concurrently across Chrome, Firefox, and Edge. `ThreadLocal<WebDriver>` guarantees each thread gets its own isolated driver instance.

### Starting Selenium Grid

```bash
# Hub
java -jar selenium-server-4.x.x.jar standalone

# Node
java -jar selenium-server-4.x.x.jar node \
  --publish-events tcp://localhost:4442 \
  --subscribe-events tcp://localhost:4443 \
  --detect-drivers true \
  --hub http://localhost:4444

```
Then run:
```bash
mvn test -Dsuite=testng-grid -Dremote=true -Dgrid.url=http://localhost:4444/wd/hub
```

---

## Reporting

### Allure Report

After a test run, generate and open the Allure report:

```bash
# Using the bundled Allure CLI
.allure/allure-2.29.0/bin/allure serve target/allure-results

# Or if Allure is on your PATH
allure serve target/allure-results
```

### TestNG / Surefire Report

Standard XML reports are generated at `target/surefire-reports/` and are consumed by Jenkins' built-in JUnit report publisher.

---

## CI/CD — Jenkins Pipeline

The `jenkins_pipeline.groovy` file defines a **declarative Jenkins pipeline** with the following stages:

| Stage                 | Action                                                           |
|-----------------------|------------------------------------------------------------------|
| **Checkout**          | Cleans workspace, pulls `main` branch from GitHub                |
| **Build**             | `mvn clean install -DskipTests`                                  |
| **Test**              | Runs `mvn test` with `browser` and `url` as parameterized inputs |
| **Publish Reports**   | Publishes TestNG XML results via JUnit plugin                    |
| **Archive Artifacts** | Archives built JARs with fingerprinting                          |
| **Post — Allure**     | Always generates Allure report from `target/allure-results`      |


**Triggering a run:**
```bash
mvn test \
  -Dbrowser=${params.browser} \
  -Durl=${params.url} \
  -Dsuite=${params.suite} \
  -Dremote=${params.remote}

```
---
## About Me

QA Automation Engineer specializing in Selenium, Java, and scalable test automation frameworks.

- 🔗 [LinkedIn](https://www.linkedin.com/in/navya-sivakoti)
- 💻 [GitHub](https://github.com/NavyaSivakoti/javascript)

---