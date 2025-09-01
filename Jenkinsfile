pipeline {
    agent any

    environment {
        JAVA_HOME = "/opt/homebrew/opt/openjdk@17"
        ANDROID_HOME = "/Users/veerkumar/Library/Android/sdk"
        PATH = "$JAVA_HOME/bin:$ANDROID_HOME/emulator:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/your-username/ChessApp.git',
                    credentialsId: 'your-github-credentials-id'
            }
        }

        stage('Build Debug APK') {
            steps {
                sh './gradlew clean assembleDebug'
            }
        }

        stage('Archive APK') {
            steps {
                archiveArtifacts artifacts: 'app/build/outputs/apk/debug/app-debug.apk', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Build finished successfully!'
        }
        failure {
            echo 'Build failed. Check logs.'
        }
    }
}
