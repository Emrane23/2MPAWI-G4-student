pipeline {
    agent any
    environment {
        registry = "ghalia08/2mpawi-g4-student"
        registryCredential = 'dockerhub'
    }

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

        // DOCKER STAGES ADDED HERE
        stage('BUILD DOCKER IMAGE') {
            steps {
                script {
                    dockerImage = docker.build registry + ":latest"
                }
            }
        }

        stage('PUSH TO DOCKER HUB') {
            steps {
                script {
                    docker.withRegistry('', registryCredential) {
                        dockerImage.push()
                    }
                }
            }
        }

        stage('TEST DOCKER CONTAINER') {
            steps {
                script {
                    bat 'docker stop student-app || true'
                    bat 'docker rm student-app || true'
                    bat "docker run -d -p 8089:8089 --name student-app ${registry}:latest"
                    bat 'timeout 30'
                    bat 'curl http://localhost:8089/student/actuator/health || echo "Application starting..."'
                }
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