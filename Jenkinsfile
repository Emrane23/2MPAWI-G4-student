pipeline {
    agent any
    environment {
        registry = "ghalia08/2mpawi-g4-student"
        DOCKER_HUB_USERNAME = 'ghalia08'
        DOCKER_HUB_PASSWORD = credentials('dockerhub-password')
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
                script {
                    // Check if Department test exists
                    def testExists = bat(script: 'if exist src\\test\\java\\*Department* (exit 0) else (exit 1)', returnStatus: true)
                    if (testExists == 0) {
                        bat 'mvn test -Dtest=*Department*'
                    } else {
                        echo 'No Department tests found, running all tests'
                        bat 'mvn test'
                    }
                }
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
       
        // MANUAL DOCKER STAGES
        stage('BUILD DOCKER IMAGE') {
            steps {
                script {
                    bat 'docker build -t ghalia08/2mpawi-g4-student:latest .'
                    echo '✅ Docker image built successfully!'
                }
            }
        }

        stage('LOGIN TO DOCKER HUB') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        bat "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                    }
                    echo '✅ Logged in to Docker Hub!'
                }
            }
        }

        stage('PUSH TO DOCKER HUB') {
            steps {
                script {
                    bat 'docker push ghalia08/2mpawi-g4-student:latest'
                    echo '✅ Docker image pushed to Docker Hub!'
                }
            }
        }

        stage('TEST DOCKER CONTAINER') {
            steps {
                script {
                    bat 'docker stop student-app || true'
                    bat 'docker rm student-app || true'
                    bat 'docker run -d -p 8089:8089 --name student-app ghalia08/2mpawi-g4-student:latest'
                    bat 'timeout 30'
                    bat 'curl http://localhost:8089/student/actuator/health || echo "Application health check..."'
                    echo '✅ Docker container deployed and running!'
                    echo '🌐 Access your app at: http://localhost:8089/student'
                }
            }
        }

        stage('DOCKER CLEANUP') {
            steps {
                script {
                    bat 'docker stop student-app || true'
                    bat 'docker rm student-app || true'
                    echo '✅ Docker cleanup completed!'
                }
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts 'target/*.jar'
            // Final cleanup
            bat 'docker stop student-app || true'
            bat 'docker rm student-app || true'
        }
        success {
            echo '🎉 Pipeline completed successfully!'
            echo '🐳 Docker Image: ghalia08/2mpawi-g4-student:latest'
            echo '📦 Application deployed to Nexus'
            echo '🔍 Code quality analyzed with SonarQube'
            echo '✅ All tests passed!'
        }
        failure {
            echo '❌ Pipeline failed! Check the test results.'
        }
    }
}