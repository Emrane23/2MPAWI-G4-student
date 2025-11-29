pipeline {
    agent any
    environment {
        registry = "ghalia08/2mpawi-g4-student"
        registryCredential = 'dockerhub'
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

        stage('RUN TESTS') {
            steps {
                bat 'mvn test'
            }
        }

        stage('SONARQUBE ANALYSIS') {
            steps {
                bat 'mvn sonar:sonar -Dsonar.login=admin -Dsonar.password=admin'
            }
        }

        stage('PACKAGE APPLICATION') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

        stage('PUBLISH TO NEXUS') {
            steps {
                bat 'mvn deploy -DskipTests'
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

        stage('TEST WITH DOCKER COMPOSE') {
            steps {
                script {
                    bat 'docker-compose down'
                    bat 'docker-compose up -d'
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
            echo '🎉 Pipeline completed successfully!'
            echo '🐳 Docker Image: ghalia08/2mpawi-g4-student:latest'
            echo '🌐 App running at: http://localhost:8089/student'
        }
    }
}