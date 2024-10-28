pipeline {
    agent any

    environment {
        // Environment variables for SonarQube and DockerHub
        DOCKER_IMAGE = 'nivisha/ekart:latest'
        SONAR_HOST_URL = 'http://23.22.187.159:9000'
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Nivisha01/Ekart.git',
                    credentialsId: 'GitHub_Cred'
            }
        }

        stage('Maven Build - Skip Tests') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
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
                            docker build -t $DOCKER_IMAGE -f docker/Dockerfile .
                            docker push $DOCKER_IMAGE
                        '''
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                // Apply both deployment and service configurations
                sh '''
                    kubectl apply -f deployment.yaml
                    kubectl apply -f service.yaml
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
