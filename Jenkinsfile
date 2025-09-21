pipeline {
  agent any

  environment {
    IMAGE = 'it217114/rentals-backend'   // ΠΡΕΠΕΙ να είναι πεζά
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Compute git short SHA') {
      steps {
        script {
          env.GIT_SHORT = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
          echo "GIT_SHORT=${env.GIT_SHORT}"
        }
      }
    }

    stage('Build JAR (Maven in Docker)') {
      steps {
        sh '''
          set -eux
          docker run --rm \
            -v "$PWD":/ws -w /ws \
            -v "$HOME/.m2":/root/.m2 \
            maven:3.9.6-eclipse-temurin-21-alpine \
            sh -lc 'mvn -B -DskipTests package'
        '''
      }
    }

    stage('Docker Build') {
      steps {
        sh '''
          set -eux
          : "${IMAGE:?IMAGE env not set}"
          : "${GIT_SHORT:?GIT_SHORT env not set}"
          echo "Building IMAGE=${IMAGE}"
          docker build -t "${IMAGE}:latest" -t "${IMAGE}:${GIT_SHORT}" .
        '''
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
            : "${IMAGE:?IMAGE env not set}"
            : "${GIT_SHORT:?GIT_SHORT env not set}"
            echo "Pushing IMAGE=${IMAGE}"
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            docker push "${IMAGE}:latest"
            docker push "${IMAGE}:${GIT_SHORT}"
            docker logout || true
          '''
        }
      }
    }
  }
}
