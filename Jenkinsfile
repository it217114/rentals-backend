pipeline {
  agent any

  environment {
    IMAGE = "it217114/rentals-backend"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        script {
          GIT_SHORT = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
          echo "GIT_SHORT = ${GIT_SHORT}"
        }
      }
    }

    stage('Build JAR (Maven inside Docker)') {
      steps {
        script {
          // Αυτό ΧΡΗΣΙΜΟΠΟΙΕΙ το Docker Pipeline plugin.
          // Το plugin μοιράζεται αυτόματα το workspace με το κοντέινερ.
          docker.image('maven:3.9.6-eclipse-temurin-21-alpine').inside {
            sh '''
              set -eux
              ls -la
              mvn -B -DskipTests package
              ls -la target || true
            '''
          }
        }
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
            set -eux
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin docker.io
            docker push ${IMAGE}:latest
            docker push ${IMAGE}:${GIT_SHORT}
          '''
        }
      }
    }
  }

  post {
    always { sh 'docker logout || true' }
    success { echo "✅ Pushed ${IMAGE}:latest and :${GIT_SHORT}" }
  }
}
