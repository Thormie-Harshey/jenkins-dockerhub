// Load the external script.groovy file
def gv

pipeline {
    agent any

    environment {
        ImageRegistry = 'thormie'
        EC2_IP = '18.212.17.152'
        DockerComposeFile = 'docker-compose.yml'
        DotEnvFile = '.env'
    }

    stages {

        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }

        stage("buildImage") {
            steps {
                script {
                    gv.buildImage()
                }
            }
        }

        stage("pushImage") {
            steps {
                script {
                    gv.pushImage()                   
                    }
                }
            }
    

        stage("deployCompose") {
            steps {
                script {
                    gv.deployCompose()                   
                    }
                }
            }
        }
    }