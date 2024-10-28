pipeline {
    agent any

    environment {
        // Environment variables to store SonarQube details
        SONAR_HOST_URL = 'http://23.22.187.159:9000'
        SONAR_TOKEN = credentials('sonar-token')  // Use Jenkins credentials for the SonarQube token
    }

    stages {
        stage('Clone Repository') {
            steps {
                // Cloning the GitHub repository using Personal Access Token or SSH
                git branch: 'main',
                    url: 'https://github.com/your-repo/shopping-cart.git',
                    credentialsId: 'GitHub_Cred'  // Use PAT-based credentials stored in Jenkins
            }
        }

        stage('Maven Build - Skip Tests') {
            steps {
                // Build the project using Maven and skip tests
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {  // Ensure SonarQube is configured in Jenkins
                    sh '''
                        mvn sonar:sonar \
                          -Dmaven.test.skip=true \
                          -Dsonar.projectKey=shopping-cart \
                          -Dsonar.host.url=$SONAR_HOST_URL \
                          -Dsonar.login=$SONAR_TOKEN
                    '''
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'DockerHub_Cred') {
                        sh '''
                        docker build -t your-dockerhub-username/shopping-cart:latest .
                        docker push your-dockerhub-username/shopping-cart:latest
                        '''
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                // Apply Kubernetes deployment using kubectl
                sh 'kubectl apply -f k8s-deployment.yaml'
            }
        }
    }

    post {
        always {
            // Clean the workspace after each run
            cleanWs()
        }
    }
}
