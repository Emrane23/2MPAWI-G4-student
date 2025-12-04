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

        stage('BUILD & PACKAGE') {
            steps {
                echo '🏗️ Building application...'
                bat 'mvn clean package -DskipTests'
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

        stage('CLEANUP') {
            steps {
                echo '🧹 Cleaning up...'
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

        stage('VERIFY') {
            steps {
                echo '🔍 Verifying deployment...'
                script {
                    echo '⏳ Waiting 30 seconds for containers to start...'
                    sleep 30
                    
                    echo '📋 Running containers:'
                    bat 'docker ps --filter "name=student-"'
                    
                    echo '✅ Deployment verified!'
                }
            }
        }
    }

    post {
        success {
            echo '✅ ========================================='
            echo '✅ PIPELINE SUCCESS! 🎉'
            echo '✅ ========================================='
            echo "📦 Docker image: ${registry}:latest"
            echo '🌐 App: http://localhost:8083/student'
            echo '📚 Swagger: http://localhost:8083/student/swagger-ui.html'
            echo '🗄️ MySQL: localhost:3307'
            echo '✅ ========================================='
        }
        failure {
            echo '❌ PIPELINE FAILED!'
        }
    }
}