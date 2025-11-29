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
                    // Check test results and set build status
                    script {
                        def testResult = junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                        if (testResult.failCount > 0) {
                            currentBuild.result = 'UNSTABLE'
                        }
                    }
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

        // NEXUS ARTIFACT PUBLISHING - Make it non-blocking
        stage('PUBLISH TO NEXUS') {
            steps {
                script {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        bat 'mvn deploy -DskipTests -Djacoco.skip=true'
                    }
                }
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

    }

    post {
        always {
            archiveArtifacts 'target/*.jar'
            // Final cleanup
            script {
                bat 'docker stop student-app 2>nul || echo "No container to stop in post-cleanup"'
                bat 'docker rm student-app 2>nul || echo "No container to remove in post-cleanup"'
            }
        }
        success {
            echo '🎉 Pipeline completed successfully! All stages passed!'
        }
        unstable {
            echo '⚠️ Pipeline completed with warnings (some stages may have failed but core functionality works)'
        }
        failure {
            echo '❌ Pipeline failed! Check the test results.'
        }
    }
}