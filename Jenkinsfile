pipeline {
  agent any

  environment {
    IMAGE = "it217114/rentals-backend"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build JAR (Maven in Docker)') {
      steps {
        script {
          docker.image('maven:3.9.6-eclipse-temurin-21-alpine')
                .inside('-v $HOME/.m2:/root/.m2') {
            sh 'mvn -B -DskipTests package'
          }
        }
      }
    }

    stage('Docker Build') {
      steps {
        script {
          // Υπολογίζουμε short SHA για tagging
          def gitShort = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
          sh """
            docker build -t ${IMAGE}:latest -t ${IMAGE}:${gitShort} .
          """
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
          script {
            def gitShort = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
            sh """
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
              docker push ${IMAGE}:latest
              docker push ${IMAGE}:${gitShort}
              docker logout || true
            """
          }
        }
      }
    }
  }

  post {
    success { echo "✅ Pushed ${IMAGE}:latest and commit tag" }
  }
}
