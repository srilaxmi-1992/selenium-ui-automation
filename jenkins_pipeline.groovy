pipeline {
    agent any

    tools {
        allure 'allure'
    }
    parameters {
        choice(
                name: 'BROWSER',
                choices: ['chrome', 'firefox', 'edge'],
                description: '''Select the browser for this run.
• chrome  — Google Chrome (default)
• firefox — Mozilla Firefox
• edge    — Microsoft Edge'''
        )
        string(
                name: 'URL',
                defaultValue: 'https://rahulshettyacademy.com/client',
                description: '''Target application URL.
Override to point at dev / staging / prod as needed.
Example: https://staging.yourapp.com'''
        )
        booleanParam(
                name: 'HEADLESS',
                defaultValue: true,
                description: '''Run browser in headless mode (no display server required).
Recommended: true for CI agents, false for local debugging.'''
        )
        booleanParam(
                name: 'REMOTE',
                defaultValue: false,
                description: '''Set to true to route tests through a Selenium Grid hub.
When enabled, GRID_URL must also be provided.'''
        )
        string(
                name: 'GRID_URL',
                defaultValue: 'http://localhost:4444/wd/hub',
                description: '''Selenium Grid hub URL. Only used when REMOTE = true.
Examples:
  Local Docker Grid : http://localhost:4444/wd/hub
  Remote Grid       : http://selenium-hub.internal:4444/wd/hub
  Selenium Cloud    : https://<user>:<key>@hub.provider.com/wd/hub'''
        )
        // ── TestNG Suite File ─────────────────────────────────────────────────
        choice(
                name: 'SUITE',
                choices: ['testng', 'testng-grid'],
                description: '''TestNG suite XML to execute.
• testng       — sequential run, single browser
• testng-grid  — parallel run (3 threads), multi-browser, includes RetryListener'''
        )
    }

    environment {
        MVN_OPTS = "-Dbrowser=${params.BROWSER} \
-Durl=${params.URL} \
-Dheadless=${params.HEADLESS} \
-Dremote=${params.REMOTE} \
-Dgrid.url=${params.GRID_URL} \
-Dsuite=${params.SUITE}"
    }

    stages {

        // ── 1. Print Run Configuration ────────────────────────────────────────
        stage('Print Configuration') {
            steps {
                echo """
╔══════════════════════════════════════════════════╗
║          SELENIUM TEST RUN CONFIGURATION         ║
╠══════════════════════════════════════════════════╣
  Browser   : ${params.BROWSER}
  URL       : ${params.URL}
  Headless  : ${params.HEADLESS}
  Remote    : ${params.REMOTE}
  Grid URL  : ${params.GRID_URL}
  Suite     : ${params.SUITE}
╚══════════════════════════════════════════════════╝"""
            }
        }
        stage('Checkout') {
            steps {
                cleanWs()
                git branch: 'main',
                        url: 'https://github.com/srilaxmi-1992/selenium-ui-automation.git'
                echo "✅ Checkout complete — workspace: ${env.WORKSPACE}"
            }
        }
        stage('Build') {
            steps {
                bat 'mvn clean install -DskipTests'
                echo '✅ Build successful'
            }
        }
        stage('Run Tests') {
            steps {
                script {
                    echo "🚀 Launching test suite [${params.SUITE}] on [${params.BROWSER}]"
                    echo "   Remote: ${params.REMOTE}  |  Headless: ${params.HEADLESS}"
                    bat "mvn test ${env.MVN_OPTS}"
                }
            }
        }
        stage('Publish Test Results') {
            steps {
                junit(
                        testResults: '**/target/surefire-reports/*.xml',
                        allowEmptyResults: true
                )
                echo '✅ TestNG results published'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts(
                        artifacts: 'target/*.jar',
                        fingerprint: true,
                        allowEmptyArchive: true
                )
                echo '✅ Artifacts archived'
            }
        }
    }
    post {

        always {
            // Generate Allure HTML report from target/allure-results
            allure([
                    includeProperties: false,
                    jdk              : '',
                    results          : [[path: 'target/allure-results']]
            ])
            echo '📊 Allure report generated'
        }

        success {
            echo '''
✅ ══════════════════════════════════════
   ALL TESTS PASSED
   Check the Allure report for details.
══════════════════════════════════════'''
        }

        failure {
            echo '''
❌ ══════════════════════════════════════
   ONE OR MORE TESTS FAILED
   Review the Allure report and logs.
══════════════════════════════════════'''
        }

        unstable {
            echo '''
⚠️  ══════════════════════════════════════
   TESTS UNSTABLE (some failures present)
   Retry analyzer may have recovered some.
══════════════════════════════════════'''
        }
    }
}