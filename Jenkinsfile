pipeline {
    agent any

    stages {
        stage('Compile') {
            steps {
                sh './gradlew classes'
            }
        }
        stage('Unit Test') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }
    }
}