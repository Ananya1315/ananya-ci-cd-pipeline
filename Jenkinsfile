```groovy
pipeline {
    agent any

    tools {
        maven 'Maven-3.10'
    }

    stages {
        stage('Build') {
            steps {
                bat 'mvn compile'
            }
        }

        stage('Test and Package') {
            steps {
                bat 'mvn package'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit 'course-registration-app/target/surefire-reports/TEST-*.xml'
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'course-registration-app/target/*.jar',
                                 fingerprint: true
            }
        }
    }
}
```