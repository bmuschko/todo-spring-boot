pipeline {
    agent any

    environment {
        SONAR_LOGIN = credentials('SONAR_LOGIN')
        HEROKU_API_KEY = credentials('HEROKU_API_KEY')
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
            steps {
                sh './gradlew deployHeroku'
            }
        }
    }
}