pipeline {
    agent any

    triggers {
        cron('H H(20-23) * * 1-5')
	pollSCM('H * * * 1-5')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timeout time: 60, unit: 'MINUTES'
    }

    environment {
	ARTIFACTORY_DEPLOY=credentials('artifactory-deploy')
    }
    stages {
        stage('Build') {
            agent {
                docker {
                    image "maven:3-jdk-8"
		    reuseNode true
                }
            }
            steps {
		sh 'mvn -B clean package -Dmaven.test.failure.ignore'
            }
	    post {
		always {
		    recordIssues(tools: [mavenConsole(), java()])
		    junit "**/target/surefire-reports/*.xml"
		    jacoco()
		}
	    }
        }
        stage('Deploy') {
            agent {
                docker {
                    image "maven:3-jdk-8"
		    reuseNode true
                }
            }
	    when { branch 'master' }
            steps {
		sh 'mvn -B deploy -s deploy-settings.xml -DskipTests -DskipITs'
            }
        }
    }

    post {
        unstable {
            emailext (
                subject: "Build unstable in Jenkins: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Unstable job '${env.JOB_NAME}' #${env.BUILD_NUMBER}: check console output at ${env.BUILD_URL}.",
                recipientProviders:  [developers()]
            )
        }

        failure {
            emailext (
                subject: "Build failed in Jenkins: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Failed job '${env.JOB_NAME}' #${env.BUILD_NUMBER}: check console output at ${env.BUILD_URL}.",
                recipientProviders:  [developers()]
            )
        }
    }
}
