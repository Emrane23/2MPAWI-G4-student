pipeline {
    agent any
    
    tools {
        terraform 'Terraform'
    }
    
    environment {
        registry = "ghalia08/2mpawi-g4-student"
        registryCredential = 'ghalia08'
    }

    stages {
        stage('CHECKOUT GIT') {
            steps {
                echo '📥 Checking out code from Git...'
                git branch: 'ghaliamannai-2MPAWI-G4', 
                    url: 'https://github.com/Emrane23/2MPAWI-G4-student.git'
            }
        }

        stage('MVN CLEAN') {
            steps {
                echo '🧹 Cleaning Maven project...'
                bat 'mvn clean'
            }
        }

        stage('COMPILE CODE') {
            steps {
                echo '⚙️ Compiling source code...'
                bat 'mvn compile'
            }
        }

        stage('RUN DEPARTMENT TESTS') {
            steps {
                echo '🧪 Running Department Service Tests...'
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
                echo '🧪 Running all unit tests...'
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SONARQUBE ANALYSIS') {
            steps {
                echo '🔍 Running SonarQube analysis...'
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    bat "mvn sonar:sonar -Dsonar.token=${SONAR_TOKEN}"
                }
            }
        }

        stage('PUBLISH TO NEXUS') {
            steps {
                echo '📦 Publishing artifact to Nexus...'
                bat 'mvn deploy -DskipTests -Djacoco.skip=true'
            }
        }

        stage('PACKAGE APPLICATION') {
            steps {
                echo '📦 Packaging application...'
                bat 'mvn package -DskipTests'
            }
        }
       
        stage('BUILD DOCKER IMAGE') {
            steps {
                echo '🐳 Building Docker image...'
                script {
                    docker.build("${registry}:latest")
                }
            }
        }

        stage('PUSH TO DOCKER HUB') {
            steps {
                echo '🚀 Pushing Docker image to Docker Hub...'
                script {
                    docker.withRegistry('', registryCredential) {
                        docker.image("${registry}:latest").push()
                    }
                }
            }
        }

        stage('CLEANUP EXISTING DEPLOYMENTS') {
            steps {
                echo '🧹 Cleaning up existing containers and infrastructure...'
                script {
                    // Remove containers (ignore errors)
                    bat '''
                        docker rm -f student-mysql-g4-terraform student-app-g4-terraform 2>nul || echo Containers removed
                        docker network rm student-network-g4 2>nul || echo Network removed
                        docker volume rm mysql_data_g4 2>nul || echo Volume removed
                    '''
                    echo '✅ Cleanup completed'
                }
            }
        }

        stage('TERRAFORM INIT') {
            steps {
                echo '🔧 Initializing Terraform...'
                bat 'terraform init -upgrade'
            }
        }

        stage('TERRAFORM APPLY') {
            steps {
                echo '🚀 Deploying infrastructure with Terraform...'
                bat 'terraform apply -auto-approve'
            }
        }

        stage('VERIFY DEPLOYMENT') {
            steps {
                echo '🔍 Verifying deployment...'
                script {
                    // Wait 30 seconds
                    bat 'timeout /t 30 /nobreak'
                    
                    // Show containers
                    echo '📋 Running containers:'
                    bat 'docker ps --filter "name=student-"'
                    
                    // Show logs
                    echo '📝 Application logs:'
                    bat 'docker logs student-app-g4-terraform --tail 30 || echo Waiting for app...'
                    
                    echo '✅ Deployment completed!'
                }
            }
        }
    }

    post {
        always {
            echo '📦 Archiving artifacts...'
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
        }
        success {
            echo '✅ ========================================='
            echo '✅ PIPELINE COMPLETED SUCCESSFULLY!'
            echo '✅ ========================================='
            echo "📦 Docker image: ${registry}:latest"
            echo '🌐 Application: http://localhost:8083/student'
            echo '📚 Swagger UI: http://localhost:8083/student/swagger-ui.html'
            echo '✅ ========================================='
        }
        failure {
            echo '❌ ========================================='
            echo '❌ PIPELINE FAILED!'
            echo '❌ ========================================='
            bat 'docker ps -a || echo Cannot list containers'
            bat 'docker logs student-app-g4-terraform --tail 50 || echo No app logs'
        }
        cleanup {
            echo '🧹 Final cleanup...'
            bat 'docker system prune -f || echo Cleanup skipped'
        }
    }
}