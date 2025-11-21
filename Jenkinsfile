pipeline {
	agent any
	tools {
		maven 'maven-3.9.9'
		jdk 'jdk-21'
	}
	stages {
		stage('Download source code') {
			steps {
				git branch: 'master', url: 'https://github.com/alpeerkaraca/karga'
			}
		}
		stage('Build') {
			steps {
				script {
					if (isUnix()) {
						sh 'mvn clean package -B -DskipTests'
					} else {
						bat 'mvn clean package -B -DskipTests'
					}
				}
			}
		}
	}
	post {
		success {
			archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
		}
		always {
			cleanWs()
		}
	}
}