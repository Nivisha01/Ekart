pipeline {
    agent any

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/your-repo/shopping-cart.git',
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
                        mvn clean verify sonar:sonar \
                          -Dmaven.test.skip=true \
                          -Dsonar.projectKey=shopping-cart \
                          -Dsonar.host.url=$SONAR_HOST_URL \
                          -Dsonar.login=$SONAR_TOKEN
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
