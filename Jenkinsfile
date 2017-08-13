pipeline {
    agent any

    triggers {
        pollSCM('H 4/* 0 0 1-5')
    }

    stages {
        stage('Compile') {
            steps {
                sh './gradlew clean classes'
            }
        }
        stage('Unit Tests') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }
        stage('Integration Tests') {
            steps {
                sh './gradlew integrationTest'
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
                sh './gradlew sonarqube'
            }
        }
        stage('Assemble') {
            steps {
                sh './gradlew assemble'
            }
        }
        stage('Deploy') {
            environment {
                HEROKU_API_KEY = credentials('HEROKU_API_KEY')
            }
            steps {
                sh './gradlew deployHeroku'
            }
        }
    }
}