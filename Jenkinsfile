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
                    url: 'https://github.com/veer127/chess-app',
                    credentialsId: 'veer127'
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
        stage('Build Release APK') {
            steps {
                sh './gradlew assembleRelease'
            }
        }

        stage('Archive Release APK') {
            steps {
                archiveArtifacts artifacts: 'app/build/outputs/apk/release/app-release-unsigned.apk', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅ Both Debug and Release APKs built successfully!'
        }

        failure {
            echo 'Build failed. Check logs.'
        }
    }
}
