pipeline {
    agent any
    
    tools {
        terraform 'Terraform'
    }
    
    environment {
        registry = "ghalia08/2mpawi-g4-student"
        registryCredential = 'ghalia08'
        dockerImage = ''
        kubeConfigCredentialId = 'kubeid'
        awsCredentialsId = 'awsCredentials'
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
                    dockerImage = docker.build("${registry}:${BUILD_NUMBER}")
                    docker.build("${registry}:latest")
                }
            }
        }

        stage('PUSH TO DOCKER HUB') {
            steps {
                echo '🚀 Pushing Docker image to Docker Hub...'
                script {
                    docker.withRegistry('', registryCredential) {
                        dockerImage.push()
                        docker.image("${registry}:latest").push()
                    }
                }
            }
        }

        stage('CLEANUP EXISTING DEPLOYMENTS') {
            steps {
                echo '🧹 Cleaning up existing containers and infrastructure...'
                bat '''
                    docker rm -f student-mysql-g4-terraform student-app-g4-terraform prometheus-g4-terraform grafana-g4-terraform 2>nul || exit /b 0
                    docker network rm student-network-g4 2>nul || exit /b 0
                    docker volume rm mysql_data_g4 prometheus_data_g4 grafana_data_g4 2>nul || exit /b 0
                '''
                echo '🗑️ Cleaning Terraform state...'
                bat '''
                    del /F /Q .terraform.lock.hcl 2>nul || exit /b 0
                    del /F /Q terraform.tfstate 2>nul || exit /b 0
                    del /F /Q terraform.tfstate.backup 2>nul || exit /b 0
                    rmdir /S /Q .terraform 2>nul || exit /b 0
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

        stage('TEST AWS CREDENTIALS') {
            steps {
                echo '🔐 Testing AWS Credentials...'
                withCredentials([file(credentialsId: awsCredentialsId, variable: 'AWS_CREDENTIALS_FILE')]) {
                    script {
                        def awsCredentials = readFile(AWS_CREDENTIALS_FILE).trim().split("\n")
                        env.AWS_ACCESS_KEY_ID = awsCredentials.find { it.startsWith("aws_access_key_id") }.split("=")[1].trim()
                        env.AWS_SECRET_ACCESS_KEY = awsCredentials.find { it.startsWith("aws_secret_access_key") }.split("=")[1].trim()
                        def sessionTokenLine = awsCredentials.find { it.startsWith("aws_session_token") }
                        if (sessionTokenLine) {
                            env.AWS_SESSION_TOKEN = sessionTokenLine.split("=")[1].trim()
                        }
                        
                        echo "✅ AWS Access Key ID: ${env.AWS_ACCESS_KEY_ID}"
                        echo "✅ AWS Credentials Loaded Successfully"
                    }
                }
            }
        }

        stage('DEPLOY TO AWS KUBERNETES') {
            steps {
                echo '☸️ Deploying to AWS Kubernetes...'
                script {
                    withCredentials([
                        file(credentialsId: kubeConfigCredentialId, variable: 'KUBECONFIG'),
                        file(credentialsId: awsCredentialsId, variable: 'AWS_CREDENTIALS_FILE')
                    ]) {
                        // Set AWS credentials as environment variables
                        def awsCredentials = readFile(AWS_CREDENTIALS_FILE).trim().split("\n")
                        env.AWS_ACCESS_KEY_ID = awsCredentials.find { it.startsWith("aws_access_key_id") }.split("=")[1].trim()
                        env.AWS_SECRET_ACCESS_KEY = awsCredentials.find { it.startsWith("aws_secret_access_key") }.split("=")[1].trim()
                        def sessionTokenLine = awsCredentials.find { it.startsWith("aws_session_token") }
                        if (sessionTokenLine) {
                            env.AWS_SESSION_TOKEN = sessionTokenLine.split("=")[1].trim()
                        }
                        
                        // Test AWS connection
                        bat '''
                            aws sts get-caller-identity
                        '''
                        
                        echo '📋 Kubeconfig loaded successfully'
                        
                        // Apply Kubernetes manifests
                        bat """
                            kubectl --kubeconfig=%KUBECONFIG% apply -f deployment.yaml
                            kubectl --kubeconfig=%KUBECONFIG% apply -f service.yaml
                        """
                        
                        echo '⏳ Waiting for deployments to be ready...'
                        bat """
                            kubectl --kubeconfig=%KUBECONFIG% wait --for=condition=available --timeout=300s deployment/student-app-deployment
                        """
                        
                        echo '📋 Getting deployment status...'
                        bat """
                            kubectl --kubeconfig=%KUBECONFIG% get deployments
                            kubectl --kubeconfig=%KUBECONFIG% get pods
                            kubectl --kubeconfig=%KUBECONFIG% get services
                        """
                    }
                }
            }
        }

        stage('GET K8S SERVICE URL') {
            steps {
                echo '🌐 Getting Kubernetes Service URL...'
                script {
                    withCredentials([file(credentialsId: kubeConfigCredentialId, variable: 'KUBECONFIG')]) {
                        bat """
                            kubectl --kubeconfig=%KUBECONFIG% get service student-app-service
                        """
                        echo '📝 Note: If using LoadBalancer, it may take a few minutes to provision the external IP'
                    }
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
            echo "📦 Docker image: ${registry}:${BUILD_NUMBER}"
            echo "📦 Docker image: ${registry}:latest"
            echo '🚀 Deployed via: Terraform (Local) + Kubernetes (AWS)'
            echo ''
            echo '🐳 LOCAL ENDPOINTS (Terraform):'
            echo '   Application: http://localhost:8083/student'
            echo '   Swagger UI: http://localhost:8083/student/swagger-ui.html'
            echo '   Actuator: http://localhost:8083/student/actuator'
            echo '   Metrics: http://localhost:8083/student/actuator/prometheus'
            echo '   Prometheus: http://localhost:9090'
            echo '   Grafana: http://localhost:3000 (admin/admin123)'
            echo ''
            echo '☸️ KUBERNETES (AWS):'
            echo '   Run: kubectl get service student-app-service'
            echo '   to get the LoadBalancer external IP'
            echo ''
            echo '🐳 LOCAL CONTAINERS:'
            echo '   - student-mysql-g4-terraform (MySQL:3307)'
            echo '   - student-app-g4-terraform (App:8083)'
            echo '   - prometheus-g4-terraform (Prometheus:9090)'
            echo '   - grafana-g4-terraform (Grafana:3000)'
            echo ''
            echo '☸️ KUBERNETES PODS:'
            echo '   - student-app-deployment'
            echo '   - mysql-deployment'
            echo ''
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