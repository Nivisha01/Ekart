pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://23.22.187.159:9000'
        SONAR_TOKEN = credentials('sonar-token')
        DOCKERHUB_CREDENTIALS = credentials('DockerHub_Cred')
        PROJECT_NAME = 'ekart'
        DOCKER_IMAGE = "nivisha/${PROJECT_NAME}:latest"
        KUBECONFIG = '/var/lib/jenkins/.kube/config' // Kubernetes config path for Jenkins
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
                    sh """
                        mvn sonar:sonar \
                          -Dmaven.test.skip=true \
                          -Dsonar.projectKey=${PROJECT_NAME} \
                          -Dsonar.host.url=$SONAR_HOST_URL \
                          -Dsonar.login=$SONAR_TOKEN
                    """
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'DockerHub_Cred') {
                        sh """
                            docker build -t ${DOCKER_IMAGE} -f docker/Dockerfile .
                            docker push ${DOCKER_IMAGE}
                        """
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    withEnv(["KUBECONFIG=${KUBECONFIG}"]) {
                        sh 'kubectl config use-context minikube'
                        sh 'kubectl apply -f deployment.yaml --validate=false'
                        sh 'kubectl apply -f service.yaml'
                        sh 'kubectl rollout restart deployment ekart-deployment --context=minikube'
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs() // Clean up the workspace
        }
    }
}
