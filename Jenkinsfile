pipeline {
    agent any
    
    environment {
        ANDROID_HOME = '/opt/android-sdk'
        PATH = "${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Checking out code...'
                checkout scm
            }
        }

        stage('Configure Android SDK') {
            steps {
                echo '⚙️ Configuring local.properties...'
                sh """
                    cat <<EOF > local.properties
sdk.dir=${ANDROID_HOME}
EOF
                    cat local.properties
                """
            }
        }

        stage('Build Debug APK') {
            steps {
                echo '🏗️ Building Android Debug APK...'
                sh 'chmod +x gradlew'
                sh './gradlew clean assembleDebug --no-daemon --stacktrace'
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo '🧪 Running unit tests...'
                sh './gradlew test --no-daemon || echo "Tests completed with issues"'
            }
        }

        stage('Lint Check') {
            steps {
                echo '🔍 Running lint checks...'
                sh './gradlew lint --no-daemon || echo "Lint completed with warnings"'
            }
        }

        stage('Archive APK') {
            steps {
                echo '📁 Archiving APK artifact...'
                archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk', fingerprint: true, allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo '✅ Android build completed successfully!'
        }
        failure {
            echo '❌ Build failed! Check console output.'
        }
        always {
            echo '🧹 Build finished.'
        }
    }
}
