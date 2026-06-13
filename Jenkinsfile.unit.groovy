pipeline {
    agent {
        label 'docker'
    }

    stages {

        stage('Source') {
            steps {
                git 'https://github.com/srayuso/unir-cicd.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }

        stage('Unit tests') {
            steps {
                sh 'make test-unit'

                archiveArtifacts artifacts: 'results/unit_result.xml'
            }
        }

        stage('API tests') {
            steps {
                sh 'make test-api'

                archiveArtifacts artifacts: 'results/api_result.xml'
            }
        }

        stage('E2E tests') {
            steps {
                sh 'make test-e2e'

                archiveArtifacts artifacts: 'results/e2e_result.xml'
            }
        }
    }

    post {

        always {

            junit 'results/unit_result.xml'
            junit 'results/api_result.xml'
            junit 'results/e2e_result.xml'

            cleanWs()
        }

        failure {

            echo """
            Asunto: Fallo en pipeline Jenkins

            El trabajo ${env.JOB_NAME}
            ha fallado en la ejecución número ${env.BUILD_NUMBER}.
            """

            /*
            mail(
                to: 'equipo@empresa.com',
                subject: "Fallo en ${env.JOB_NAME}",
                body: """El trabajo ${env.JOB_NAME}
ha fallado en la ejecución número ${env.BUILD_NUMBER}."""
            )
            */
        }
    }
}