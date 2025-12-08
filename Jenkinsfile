pipeline {
    agent {
        docker {
            // Use a Docker image with Flutter + Android SDK pre-installed
            // Example image (adjust to one that works or build your own):
            image 'jenkins/jenkins:lts'
            args '--user root'
        }
    }

    environment {
        // These paths depend on the Docker image
        ANDROID_HOME = '/opt/android-sdk-linux'
        FLUTTER_HOME = '/sdks/flutter'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
                sh 'git submodule update --init --recursive'
            }
        }

        stage('Flutter Dependencies') {
            steps {
                echo 'Installing Flutter dependencies...'
                dir('flutter_module') {
                    sh 'flutter pub get'
                }
            }
        }

        stage('Configure Flutter SDK Path') {
            steps {
                echo 'Configuring local.properties...'
                script {
                    def flutterSdk = sh(script: 'which flutter | xargs dirname | xargs dirname', returnStdout: true).trim()
                    sh """
                        mkdir -p flutter_module/.android
                        cat <<EOF > flutter_module/.android/local.properties
sdk.dir=${ANDROID_HOME}
flutter.sdk=${flutterSdk}
EOF
                    """
                }
            }
        }

        stage('Build Android Debug APK') {
            steps {
                echo 'Building Android APK...'
                dir('mobile-app') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean assembleDebug --no-daemon --stacktrace'
                }
            }
        }

        stage('Archive APK') {
            steps {
                echo 'Archiving APK artifact...'
                archiveArtifacts artifacts: 'mobile-app/app/build/outputs/apk/debug/*.apk', fingerprint: true
            }
        }
    }

    post {
        success {
            echo '✅ Build succeeded!'
        }
        failure {
            echo '❌ Build failed. Check console output.'
        }
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
    }
}
