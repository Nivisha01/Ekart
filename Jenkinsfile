pipeline {
    agent any

    environment {
        // Environment variables for SonarQube and DockerHub
        SONAR_HOST_URL = 'http://23.22.187.159:9000'
        SONAR_TOKEN = credentials('sonar-token')  // SonarQube token stored in Jenkins credentials
        DOCKER_IMAGE = 'nivisha/ekart:latest'  // Docker image name
    }

    stages {
        stage('Clone Repository') {
            steps {
                // Clone the GitHub repository using PAT credentials
                git branch: 'main',
                    url: 'https://github.com/Nivisha01/Ekart.git',
                    credentialsId: 'GitHub_Cred'
            }
        }

        stage('Maven Build - Skip Tests') {
            steps {
                // Build the project using Maven, skipping tests
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    // Perform SonarQube analysis, skipping tests
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
                        // Build Docker image from the correct path and push it to DockerHub
                        sh '''
                        docker build -t $DOCKER_IMAGE -f docker/Dockerfile .
                        docker push $DOCKER_IMAGE
                        '''
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                // Deploy the application using kubectl
                sh 'kubectl apply -f k8s-deployment.yaml'
            }
        }
    }

    post {
        always {
            // Clean the workspace after each run to free up space
            cleanWs()
        }
    }
}
