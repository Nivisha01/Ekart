pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://54.84.52.102:9000' // Updated SonarQube URL
        SONAR_TOKEN = credentials('sonar-token')
        DOCKERHUB_CREDENTIALS = credentials('DockerHub_Cred')
        PROJECT_NAME = 'ekart'  // Update to match your app name
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
                // Ensure that the Maven goal includes packaging for a .jar file
                sh 'mvn clean install -DskipTests'
                sh 'mvn clean package -DskipTests'
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
                        // Apply the deployment and service YAML files
                        sh """
                            kubectl apply -f deployment.yaml
                            kubectl apply -f service.yaml
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
