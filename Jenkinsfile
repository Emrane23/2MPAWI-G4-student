pipeline {
    agent any

    tools {
        maven 'Maven3.9' // le nom que tu as configuré dans "Global Tool Configuration"
    }

    environment {
        // Variables globales (optionnelles)
        MAVEN_OPTS = "-Dmaven.test.failure.ignore=true"
    }

    stages {
        stage('CHECKOUT GIT') {
            steps {
                echo '📥 Cloning repository...'
                git branch: 'ihebheni-2MPAWI-G4', url: 'https://github.com/Emrane23/2MPAWI-G4-student.git'
            }
        }

        stage('MVN CLEAN') {
            steps {
                echo '🧹 Cleaning Maven project...'
                bat 'mvn clean'
            }
        }

        stage('BUILD') {
            steps {
                echo '🏗️ Building the project...'
                bat 'mvn package -DskipTests'
            }
        }

        stage('UNIT TESTS') {
            steps {
                echo '🧪 Running tests...'
                bat 'mvn test'
            }
        }

        stage('DEPLOY TO NEXUS') {
            steps {
                echo '📦 Deploying artifact to Nexus...'
                bat 'mvn deploy'
            }
        }

        stage('BUILD DOCKER IMAGE') {
            steps {
                echo '🐳 Building Docker image...'
                bat 'docker build -t myapp:latest .'
            }
        }

        stage('PUSH TO DOCKER HUB') {
            steps {
                echo '🚀 Pushing Docker image to Docker Hub...'
                bat 'docker push myapp:latest'
            }
        }
    }

    post {
        success {
            echo '✅ Build completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed!'
        }
        always {
            echo '🧹 Cleaning workspace and Docker system...'
            bat 'docker system prune -f'
        }
    }
}
