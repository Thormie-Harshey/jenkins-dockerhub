pipeline {
    agent any


    stages{
        stage("buildImage") {
            steps{
                script {
                    echo "Build Docker Image"
                    sh 'docker build -t phpwebapp .'
                }
            }
        }
    }
}