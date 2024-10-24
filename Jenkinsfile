pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'nivisha/ekart:latest' 
        SONARQUBE_SERVER = 'SonarQube'
        KUBE_TOKEN = credentials('kube-token') 
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/Nivisha01/Ekart.git' 
            }
        }

        stage('Maven Build') {
            steps {
                // Skip tests during the build
                sh 'mvn clean install -DskipTests' 
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(SONARQUBE_SERVER) {
                    sh 'mvn sonar:sonar'
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
                withCredentials([usernamePassword(credentialsId: 'DockerHub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'docker login -u $DOCKER_USER -p $DOCKER_PASS'
                    sh 'docker push $DOCKER_IMAGE'
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                kubectl apply -f kubernetes-deployment.yaml --token=$KUBE_TOKEN
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
