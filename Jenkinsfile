pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'nivisha/ekart:latest'
        SONARQUBE_SERVER = 'SonarQube'
        PATH = "/opt/maven/bin:$PATH"  // Ensure Maven path is included
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/Nivisha01/Ekart.git'
            }
        }

        stage('Maven Build') {
            steps {
                sh '/opt/maven/bin/mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(SONARQUBE_SERVER) {
                    sh '/opt/maven/bin/mvn sonar:sonar'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE -f docker/Dockerfile .'
            }
        }

        stage('Push Docker Image to DockerHub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'DockerHub_Cred',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                    docker login -u $DOCKER_USER -p $DOCKER_PASS
                    docker push $DOCKER_IMAGE
                    '''
                }
            }
        }

        stage('Deploy to Minikube') {
            steps {
                sh 'kubectl apply -f deployment.yaml'
            }
        }

        stage('Expose Service') {
            steps {
                script {
                    sh '''
                    kubectl expose deployment ekart-deployment --type=NodePort --port=8080
                    SERVICE_URL=$(minikube service ekart-deployment --url)
                    echo "Application is available at: $SERVICE_URL"
                    '''
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
