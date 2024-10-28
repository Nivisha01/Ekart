pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://23.22.187.159:9000'
        SONAR_TOKEN = credentials('sonar-token')
        DOCKERHUB_CREDENTIALS = credentials('DockerHub_Cred')
        PROJECT_NAME = 'ekart'
        DOCKER_IMAGE = "nivisha/${PROJECT_NAME}:${env.BUILD_NUMBER}"  // Tag with build number
        KUBECONFIG = '/var/lib/jenkins/.kube/config'
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
                        withCredentials([string(credentialsId: 'k8s-jenkins-token', variable: 'K8S_TOKEN')]) {
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
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            mail to: 'your-email@example.com', 
                subject: "Build failed in Jenkins: ${env.JOB_NAME} #${env.BUILD_NUMBER}", 
                body: "Something is wrong with ${env.JOB_NAME} build. Please check it out!"
        }
        always {
            cleanWs()
        }
    }
}
