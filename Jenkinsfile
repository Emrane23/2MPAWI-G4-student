pipeline {
    agent any

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
        }

        stage('RUN ALL TESTS') {
            steps {
                bat 'mvn test'
            }
        }

        // NEW: SONARQUBE CODE ANALYSIS
        stage('SONARQUBE ANALYSIS') {
            steps {
                bat 'mvn sonar:sonar -Dsonar.login=admin -Dsonar.password=Ghourella21#'
            }
        }

        stage('PACKAGE APPLICATION') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts 'target/*.jar'
        }
        success {
            echo '🎉 Pipeline completed successfully! Department tests passed!'
        }
        failure {
            echo '❌ Pipeline failed! Check the test results.'
        }
    }
}