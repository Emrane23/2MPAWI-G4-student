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
                    docker.build("${registry}:latest")
                }
            }
        }

        stage('PUSH TO DOCKER HUB') {
            steps {
                script {
                    docker.withRegistry('', registryCredential) {
                        docker.image("${registry}:latest").push()
                    }
                }
            }
        }

        stage('TEST DOCKER CONTAINER') {
            steps {
                script {
                    echo "Stopping and removing everything..."
                    bat 'docker-compose down -v --remove-orphans --rmi local'
                    
                    echo "Pulling latest image..."
                    bat 'docker pull ghalia08/2mpawi-g4-student:latest'
                    
                    echo "Building and starting fresh..."
                    bat 'docker-compose up -d --build --force-recreate'
                    
                    echo "Waiting for startup..."
                    bat 'timeout /t 60 /nobreak'
                    
                    echo "Health check..."
                    bat 'curl -f http://localhost:8089/actuator/health || exit 1'
                }
            }
        }

    post {
        always {
            archiveArtifacts 'target/*.jar'
        }
        success {
            echo '🎉 PIPELINE COMPLETED SUCCESSFULLY! All tests passed, Docker image pushed to Docker Hub!'
        }
        failure {
            echo '❌ Pipeline failed! Check the test results.'
        }
    }
}