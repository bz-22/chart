node {
  stage('SCM') {
    checkout scm
  }
  stage('SonarQube Analysis') {
    def scannerHome = tool 'sonarScanner';
    withSonarQubeEnv() {
      sh "${scannerHome}/bin/sonarScanner"
    }
  }
}