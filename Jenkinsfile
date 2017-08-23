pipeline {
    agent any

    triggers {
        pollSCM('*/5 * * * *')
    }

    stages {
        stage('Compile') {
            steps {
                gradlew('clean', 'classes')
            }
        }
        stage('Unit Tests') {
            steps {
                gradlew('test')
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }
        stage('Long-running Verification') {
            environment {
                SONAR_LOGIN = credentials('SONAR_LOGIN')
            }
            steps {
                parallel("Integration Tests": {
                    gradlew('integrationTest')
                },
                "Code Analysis": {
                    gradlew('sonarqube')
                })
            }
            post {
                always {
                    junit '**/build/test-results/integrationTest/TEST-*.xml'
                }
            }
        }
        stage('Assemble') {
            steps {
                gradlew('assemble')
                stash includes: '**/build/libs/*.war', name: 'app'
            }
        }
        stage('Promotion') {
            steps {
                input 'Deploy to Production?'
            }
        }
        stage('Deploy to Production') {
            environment {
                HEROKU_API_KEY = credentials('HEROKU_API_KEY')
            }
            steps {
                unstash 'app'
                gradlew('deployHeroku')
            }
        }
    }
}

def gradlew(String... args) {
    sh "./gradlew ${args.join(' ')} -s"
}