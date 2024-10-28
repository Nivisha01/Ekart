pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://23.22.187.159:9000'
        SONAR_TOKEN = credentials('sonar-token')
        DOCKERHUB_CREDENTIALS = credentials('DockerHub_Cred')
        PROJECT_NAME = 'my-app'
        DOCKER_IMAGE = "nivisha/${PROJECT_NAME}:${env.BUILD_NUMBER}-${GIT_COMMIT.take(7)}"  // Tag with build number and short commit hash
        KUBECONFIG = '/var/lib/jenkins/.kube/config'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main',  // Ensure this matches your branch
                    url: 'https://github.com/Nivisha01/simple-java-maven-app.git',
                    credentialsId: 'GitHub_Cred'
            }
        }

        stage('Maven Build') {
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
                          -Dsonar.host.url=${SONAR_HOST_URL} \
                          -Dsonar.login=${SONAR_TOKEN}
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
                        sh """
                            kubectl config use-context minikube
                            kubectl create deployment ${PROJECT_NAME} --image=${DOCKER_IMAGE} --replicas=2
                            kubectl expose deployment ${PROJECT_NAME} --type=NodePort --port=80 --target-port=8070  // Use NodePort for Minikube
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs() // Clean up the workspace
            // Optionally, add notifications here
        }
    }
}

