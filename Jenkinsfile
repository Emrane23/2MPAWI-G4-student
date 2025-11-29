pipeline {
    agent any

    stages {
        stages {
        stage('Increment Version') {
            steps {
                script {
                    // Read current version and increment
                    def currentVersion = readMavenPom().getVersion()
                    def newVersion = incrementVersion(currentVersion)
                    sh "mvn versions:set -DnewVersion=${newVersion}"
                }
            }
        }
        stage('CHECKOUT GIT') {
            steps {
                git branch: 'ghaliamannai-2MPAWI-G4', 
                url: 'https://github.com/Emrane23/2MPAWI-G4-student.git'
            }
        }

        stage('MVN CLEAN') {
            steps {
                bat 'mvn clean'
            }
        }

        stage('COMPILE CODE') {
            steps {
                bat 'mvn compile'
            }
        }

        stage('RUN DEPARTMENT TESTS') {
            steps {
                bat 'mvn test -Dtest=DepartmentServiceTest'
            }
        }

        stage('RUN ALL TESTS') {
            steps {
                bat 'mvn test'
            }
        }

        // SONARQUBE ANALYSIS WITH TOKEN
        stage('SONARQUBE ANALYSIS') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    bat "mvn sonar:sonar -Dsonar.token=${SONAR_TOKEN}"
                }
            }
        }

         // NEXUS ARTIFACT PUBLISHING
        stage('PUBLISH TO NEXUS') {
            steps {
                bat 'mvn deploy -DskipTests -Djacoco.skip=true'
            }
        }

        stage('PACKAGE APPLICATION') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts 'target/*.jar'
        }
        success {
            echo '🎉 Pipeline completed successfully! Department tests passed!'
        }
        failure {
            echo '❌ Pipeline failed! Check the test results.'
        }
    }
}