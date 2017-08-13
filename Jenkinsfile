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
        stage('Integration Tests') {
            steps {
                gradlew('integrationTest')
            }
            post {
                always {
                    junit '**/build/test-results/integrationTest/TEST-*.xml'
                }
            }
        }
        stage('Code Analysis') {
            environment {
                SONAR_LOGIN = credentials('SONAR_LOGIN')
            }
            steps {
                gradlew('sonarqube')
            }
        }
        stage('Assemble') {
            steps {
                gradlew('assemble')
            }
        }
        stage('Deploy') {
            input message: Deploy to Heroku?'
            environment {
                HEROKU_API_KEY = credentials('HEROKU_API_KEY')
            }
            steps {
                gradlew('deployHeroku')
            }
        }
    }
}

def gradlew(String... args) {
    sh "./gradlew ${args.join(' ')} -s"
}