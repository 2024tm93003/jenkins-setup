pipeline {
    agent { label 'agent-1' }

    parameters {
        choice(name: 'BRANCH', choices: ['development', 'staging', 'main'], description: 'Branch to deploy')
    }

    environment {
        PATH = "/usr/local/bin:$PATH"
    }

    stages {
        stage('Clean Workspace') {
            steps {
                sh 'rm -rf * .[^.]* || true'
            }
        }
        stage('Clone Repo') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'github-creds', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    sh "git clone -b ${params.BRANCH} https://${GIT_USER}:${GIT_PASS}@github.com/2024tm93003/angular-app.git"
                }
            }
        }

        stage('Install Dependencies') {
            steps {
                dir('angular-app') {
                    sh 'npm install @angular/cli --unsafe-perm=true'
                    sh 'npm install'
                }
            }
        }

        stage('Get Current Working Directory') {
            steps {
                echo "Current working directory: ${pwd()}"
            }
        }

        stage('Build Angular App') {
            steps {
                dir('angular-app') {
                    sh "ng build --configuration=${getEnvironment(params.BRANCH)}"
                }
            }
        }

        stage('Deploy') {
            steps {
                dir('angular-app') {
                    sh 'pwd'
                    sh 'npm install http-server'
                    sh "nohup npx http-server dist/angular-app/browser -p ${getPort(params.BRANCH)} -a 0.0.0.0 &"
                    sh "sleep 20"
                    echo "Angular app deployed to ${getEnvironment(params.BRANCH)} environment"
                }
            }
        }
    }
    
    post {
        failure {
            echo "Pipeline failed!"
        }
        success {
            echo "Pipeline completed successfully!"
        }
    }
}

// Helper function to get the environment based on the branch
def getEnvironment(branch) {
    def environment = 'development'
    if (branch == 'staging') {
        environment = 'staging'
    } else if (branch == 'main') {
        environment = 'production'
    }
    return environment
}

// Helper function to get the port based on the branch
def getPort(branch) {
    def port = 1000
    if (branch == 'staging') {
        port = 2000
    } else if (branch == 'main') {
        port = 3000
    }
    return port
}
