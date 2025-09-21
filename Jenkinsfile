pipeline {
  agent any

  environment {
    IMAGE = "it217114/rentals-backend"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Compute GIT_SHORT') {
      steps {
        script {
          env.GIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        }
      }
    }

    stage('Build JAR (Maven inside Docker)') {
      steps {
        sh '''
          set -eux
          docker run --rm \
            -v "$PWD":/ws \
            -w /ws \
            -v "$HOME/.m2":/ws/.m2 \
            maven:3.9.6-eclipse-temurin-21-alpine \
            sh -lc 'ls -la; mvn -B -Dmaven.repo.local=/ws/.m2 -DskipTests package'
        '''
      }
    }

    stage('Docker Build') {
      steps {
        sh '''
          set -eux
          docker build \
            -t ${IMAGE}:latest \
            -t ${IMAGE}:${GIT_SHORT} \
            .
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
            set -eu
            echo "Login to Docker Hub as $DOCKER_USER"
            test -n "$DOCKER_PASS"  # ensure not empty
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            docker push ${IMAGE}:latest
            docker push ${IMAGE}:${GIT_SHORT}
            docker logout || true
          '''
        }
      }
    }
  }

  post {
    success { echo "✅ Pushed ${IMAGE}:latest and :${GIT_SHORT}" }
    always  { sh 'docker logout || true' }
  }
}
