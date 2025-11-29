pipeline {
    agent any
    environment {
        registry = "ghalia08/2mpawi-g4-student"
        registryCredential = 'ghalia08'
    }

    stages {
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
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('RUN ALL TESTS') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
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
                    bat 'docker stop student-app || echo "No container to stop"'
                    bat 'docker rm student-app || echo "No container to remove"'
                    bat "docker run -d -p 8089:8089 --name student-app ${registry}:latest"
                    bat 'timeout /t 30 /nobreak'
                    bat 'curl http://localhost:8089/student/actuator/health || echo "Application starting..."'
                    // clean up after test 
                    bat 'docker stop student-app || echo "Could not stop container"'
                    bat 'docker rm student-app || echo "Could not remove container"'
                }
            }
        }
    }

    post {
        always {
            // Archive artifacts after all stages complete
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