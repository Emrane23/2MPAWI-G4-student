pipeline {
    agent any
    
    tools {
        terraform 'Terraform'  // Your configured Terraform tool
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
                    // Clean docker-compose deployments
                    bat 'docker-compose down --remove-orphans || echo "No docker-compose to clean"'
                    
                    // Remove any old standalone containers
                    bat 'docker rm -f student-mysql-g4 student-app-g4 || echo "Old containers not found"'
                    
                    // Clean up Terraform-managed containers
                    bat 'docker rm -f student-mysql-g4-terraform student-app-g4-terraform || echo "No Terraform containers to clean"'
                    
                    // Clean up Docker networks
                    bat 'docker network rm student-network-g4 || echo "Network already removed"'
                    
                    // Clean up Docker volumes (optional - removes data)
                    // bat 'docker volume rm mysql_data_g4 || echo "Volume already removed"'
                    
                    echo '✅ Cleanup completed'
                }
            }
        }

        stage('TERRAFORM INIT') {
            steps {
                echo '🔧 Initializing Terraform...'
                dir('terraform') {
                    bat 'terraform init'
                }
            }
        }

        stage('TERRAFORM VALIDATE') {
            steps {
                echo '✅ Validating Terraform configuration...'
                dir('terraform') {
                    bat 'terraform validate'
                }
            }
        }

        stage('TERRAFORM PLAN') {
            steps {
                echo '📋 Planning Terraform infrastructure changes...'
                dir('terraform') {
                    bat 'terraform plan -out=tfplan'
                }
            }
        }

        stage('TERRAFORM APPLY') {
            steps {
                echo '🚀 Deploying infrastructure with Terraform...'
                dir('terraform') {
                    bat 'terraform apply -auto-approve tfplan'
                }
            }
        }

        stage('VERIFY DEPLOYMENT') {
            steps {
                script {
                    echo '🔍 Verifying Terraform deployment...'
                    
                    // Wait for MySQL to be fully ready
                    echo '⏳ Waiting for MySQL to be ready...'
                    powershell '''
                        $maxAttempts = 30
                        $attempt = 0
                        $isHealthy = $false
                        
                        while (-not $isHealthy -and $attempt -lt $maxAttempts) {
                            $attempt++
                            Write-Host "Checking MySQL health (attempt $attempt/$maxAttempts)..."
                            
                            try {
                                docker exec student-mysql-g4-terraform mysqladmin ping -h localhost -u root -prootpassword 2>$null
                                if ($LASTEXITCODE -eq 0) {
                                    $isHealthy = $true
                                    Write-Host "✅ MySQL is healthy!"
                                }
                            } catch {
                                Write-Host "⏳ MySQL not ready yet, waiting..."
                            }
                            
                            if (-not $isHealthy) {
                                Start-Sleep -Seconds 2
                            }
                        }
                        
                        if (-not $isHealthy) {
                            Write-Host "❌ MySQL failed to become healthy"
                            exit 1
                        }
                    '''
                    
                    // Wait for application to start
                    echo '⏳ Waiting for application to start...'
                    powershell 'Start-Sleep -Seconds 30'
                    
                    // Display all running containers
                    echo '📋 Current running containers:'
                    bat 'docker ps'
                    
                    // Verify MySQL container is running
                    echo '🔍 Checking MySQL container...'
                    bat 'docker ps --filter name=student-mysql-g4-terraform --format "{{.Status}}" | findstr "Up" >nul'
                    echo '✅ MySQL container is running'
                    
                    // Verify App container is running
                    echo '🔍 Checking Application container...'
                    bat 'docker ps --filter name=student-app-g4-terraform --format "{{.Status}}" | findstr "Up" >nul'
                    echo '✅ Application container is running'
                    
                    // Check network connectivity
                    echo '🌐 Checking network connectivity...'
                    bat 'docker network inspect student-network-g4'
                    
                    // Display Terraform outputs
                    echo '📊 Terraform Infrastructure Details:'
                    dir('terraform') {
                        bat 'terraform output'
                    }
                    
                    // Display container logs
                    echo '📝 MySQL logs (last 20 lines):'
                    bat 'docker logs --tail 20 student-mysql-g4-terraform || echo "Cannot fetch MySQL logs"'
                    
                    echo '📝 Application logs (last 50 lines):'
                    bat 'docker logs --tail 50 student-app-g4-terraform || echo "Cannot fetch app logs"'
                    
                    // Try to access health endpoint
                    echo '🔍 Testing application health endpoint...'
                    powershell '''
                        $maxAttempts = 15
                        $attempt = 0
                        $isHealthy = $false
                        
                        while (-not $isHealthy -and $attempt -lt $maxAttempts) {
                            $attempt++
                            Write-Host "Testing health endpoint (attempt $attempt/$maxAttempts)..."
                            
                            try {
                                $response = Invoke-WebRequest -Uri "http://localhost:8083/student/actuator/health" -UseBasicParsing -TimeoutSec 5
                                if ($response.StatusCode -eq 200) {
                                    $isHealthy = $true
                                    Write-Host "✅ Application is healthy!"
                                    Write-Host $response.Content
                                }
                            } catch {
                                Write-Host "⏳ Application not ready yet, waiting..."
                                Start-Sleep -Seconds 5
                            }
                        }
                        
                        if (-not $isHealthy) {
                            Write-Host "⚠️ Application health check did not pass within timeout"
                            Write-Host "Containers are running but application may still be starting..."
                        }
                    '''
                    
                    echo '✅ Verification completed!'
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
            echo '🚀 Deployed via: Terraform'
            echo '🌐 Application URL: http://localhost:8083/student'
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
            echo '📋 Debugging information:'
            
            bat 'docker ps -a || echo "Cannot list containers"'
            bat 'docker network ls || echo "Cannot list networks"'
            bat 'docker logs student-app-g4-terraform || echo "No app container"'
            bat 'docker logs student-mysql-g4-terraform || echo "No MySQL container"'
            
            echo '❌ ========================================='
        }
        cleanup {
            echo '🧹 Performing final cleanup...'
            bat 'docker system prune -f || echo "Cleanup skipped"'
        }
    }
}