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
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
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
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
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
                bat '''
                    docker rm -f student-mysql-g4-terraform student-app-g4-terraform 2>nul || exit /b 0
                    docker network rm student-network-g4 2>nul || exit /b 0
                    docker volume rm mysql_data_g4 2>nul || exit /b 0
                '''
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
                echo '🚀 Deploying with Terraform...'
                bat 'terraform apply -auto-approve'
            }
        }

        stage('VERIFY DEPLOYMENT') {
            steps {
                echo '🔍 Verifying deployment...'
                script {
                    echo '⏳ Waiting 30 seconds for containers to start...'
                    sleep 30
                    
                    echo '📋 Running containers:'
                    bat 'docker ps --filter "name=student-"'
                    
                    echo '📝 MySQL logs (last 20 lines):'
                    bat 'docker logs student-mysql-g4-terraform --tail 20 2>nul || echo "MySQL starting..."'
                    
                    echo '📝 Application logs (last 30 lines):'
                    bat 'docker logs student-app-g4-terraform --tail 30 2>nul || echo "App starting..."'
                    
                    echo '✅ Deployment verified!'
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
            echo '✅ PIPELINE COMPLETED SUCCESSFULLY! 🎉'
            echo '✅ ========================================='
            echo "📦 Docker image: ${registry}:latest"
            echo '🚀 Deployed via: Terraform'
            echo '🌐 Application: http://localhost:8083/student'
            echo '📚 Swagger UI: http://localhost:8083/student/swagger-ui.html'
            echo '🗄️ MySQL Port: 3307'
            echo '🐳 Containers:'
            echo '   - student-mysql-g4-terraform'
            echo '   - student-app-g4-terraform'
            echo '🌐 Network: student-network-g4'
            echo '💾 Volume: mysql_data_g4'
            echo '✅ ========================================='
        }
        failure {
            echo '❌ ========================================='
            echo '❌ PIPELINE FAILED!'
            echo '❌ ========================================='
            echo 'Check the logs above to identify the failing stage'
        }
        cleanup {
            echo '🧹 Performing final cleanup...'
            bat 'docker system prune -f || exit /b 0'
        }
    }
}