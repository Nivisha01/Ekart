pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://23.22.187.159:9000'  // SonarQube URL
        SONAR_TOKEN = credentials('sonar-token')  // SonarQube token credential
        DOCKERHUB_CREDENTIALS = credentials('DockerHub_Cred')  // DockerHub credentials
        PROJECT_NAME = 'ekart'  // Project name for Docker
        DOCKER_IMAGE = "nivisha/${PROJECT_NAME}:latest"  // Docker image name
        KUBECONFIG = '/var/lib/jenkins/.kube/config'  // Kubernetes config path for Jenkins
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Nivisha01/Ekart.git',
                    credentialsId: 'GitHub_Cred'  // GitHub credentials for cloning
            }
        }

        stage('Maven Build - Skip Tests') {
            steps {
                sh 'mvn clean install -DskipTests'  // Build the application
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {  // Use the SonarQube environment
                    sh """
                        mvn sonar:sonar \
                          -Dmaven.test.skip=true \
                          -Dsonar.projectKey=${PROJECT_NAME} \
                          -Dsonar.host.url=${SONAR_HOST_URL} \
                          -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    // Log in to DockerHub and push the Docker image
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
                    // Set KUBECONFIG
                    withEnv(["KUBECONFIG=${KUBECONFIG}"]) {
                        // Use the Kubernetes token securely
                        withCredentials([string(credentialsId: 'k8s-jenkins-token', variable: 'K8S_TOKEN')]) {
                            // Write the token to the kubeconfig for authentication
                            sh """
                                kubectl config set-credentials jenkins-sa --token=${K8S_TOKEN}
                                kubectl config use-context minikube
                                kubectl apply -f deployment.yaml --validate=false
                                kubectl apply -f service.yaml
                                kubectl rollout restart deployment ekart-deployment --context=minikube
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()  // Clean up the workspace
        }
    }
}
