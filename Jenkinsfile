pipeline {
    agent any

    environment {
        ImageRegistry = 'thormie'
        EC2_IP = '44.210.115.120'
        DockerComposeFile = 'docker-compose.yml'
        DotEnvFile = '.env'
        DockerImageTag = "${ImageRegistry}/${JOB_NAME}:${BUILD_NUMBER}"
    }

    stages {

        stage("buildImage") {
            steps {
                script {
                    echo "Building Docker Image..."
                    sh 'echo "Running on: $(hostname)"'
                    sh "docker build -t ${DockerImageTag} ."
                }
            }
        }

        stage("pushImage") {
            steps {
                script {
                    echo "Pushing Image to DockerHub..."
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh "echo $PASS | docker login -u $USER --password-stdin"
                        sh "docker push ${DockerImageTag}"
                    }
                }
            }
        }

        stage("deployCompose") {
            steps {
                script {
                    echo "Deploying with Docker Compose..."
                    sshagent(['ssh_key']) {
                        sh """
                        # Copy files to ec2 instance
                        scp -o StrictHostKeyChecking=no ${DotEnvFile} ${DockerComposeFile} ubuntu@${EC2_IP}:/home/ubuntu

                        # Pull the latest docker image and restart services
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} 
                            export DC_IMAGE_NAME=${DockerImageTag} && \
                            docker compose -f /home/ubuntu/${DockerComposeFile} --env-file /home/ubuntu/${DotEnvFile} down
                            docker compose -f /home/ubuntu/${DockerComposeFile} --env-file /home/ubuntu/${DotEnvFile} up -d
                        """
                    }
                }
            }
        }
    }
}