pipeline {
  agent any

  environment {
    IMAGE = "it217114/rentals-backend"
    GIT_SHORT = ""
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        script { GIT_SHORT = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim() }
      }
    }

    stage('Build JAR via docker run (Maven)') {
      steps {
        sh '''
          set -eux
          docker run --rm -v "$PWD":/workspace -w /workspace maven:3.9.6-eclipse-temurin-21-alpine \
            mvn -B -DskipTests package
          ls -la target || true
        '''
      }
    }

    stage('Docker Build') {
      steps {
        sh '''
          set -eux
          docker build -t '"${IMAGE}:latest"' -t '"${IMAGE}:${GIT_SHORT}"' .
        '''
      }
    }

    stage('Docker Push') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-it217114',
                       usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
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
    success { echo "✅ Pushed ${IMAGE}:latest and :${GIT_SHORT}" }
  }
}
