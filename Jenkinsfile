pipeline {
  agent any

  environment {
    IMAGE     = "it217114/rentals-backend"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build JAR (Maven in Docker)') {
      steps {
        script {
          // Χρησιμοποιεί Docker Pipeline plugin – κάνει σωστά mount το workspace
          docker.image('maven:3.9.6-eclipse-temurin-21-alpine').inside('-v $HOME/.m2:/root/.m2') {
            sh 'mvn -B -DskipTests package'
          }
        }
      }
    }

    stage('Docker Build') {
      steps {
        script {
          // μικρό git short για tag
          def GIT_SHORT = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
          sh """
            set -eux
            docker build -t ${IMAGE}:latest -t ${IMAGE}:${GIT_SHORT} .
          """
          env.GIT_SHORT = GIT_SHORT
        }
      }
    }

    stage('Docker Push') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'dockerhub-it217114',
          usernameVariable: 'DOCKER_USER',
          passwordVariable: 'DOCKER_PASS'
        )]) {
          sh '''
            set -eux
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            docker push '"${IMAGE}:latest"'
            docker push '"${IMAGE}:${GIT_SHORT}"'
            docker logout || true
          '''
        }
      }
    }
  }

  post {
    success { echo "✅ Pushed ${IMAGE}:latest and :${env.GIT_SHORT}" }
  }
}
