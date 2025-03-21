// script.groovy

def buildImage() {
    echo "Building Docker Image..."
	sh 'echo "Running on: $(hostname)"'
    sh "docker build -t ${ImageRegistry}/${JOB_NAME}:${BUILD_NUMBER} ."
}

def pushImage() {
    echo "Pushing Image to DockerHub..."
    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh "docker push ${ImageRegistry}/${JOB_NAME}:${BUILD_NUMBER}"
    }
}

def deployCompose() {
    echo "Deploying with Docker Compose..."
    sshagent(['ssh_key']) {
        sh """
        scp -o StrictHostKeyChecking=no ${DotEnvFile} ${DockerComposeFile} ubuntu@${EC2_IP}:/home/ubuntu
        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} "docker compose -f /home/ubuntu/${DockerComposeFile} --env-file /home/ubuntu/${DotEnvFile} down"
        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} "docker compose -f /home/ubuntu/${DockerComposeFile} --env-file /home/ubuntu/${DotEnvFile} up -d"
        """
    }
}

return this