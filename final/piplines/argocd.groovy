import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.json.JsonSlurperClassic
import groovy.transform.Field

@Field JOB = [:]

JOB.trigg_by = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause').userName

PROP = [:]

PROP['git_cred'] = 'github_ssh2'
PROP['branch'] = 'main'
PROP['proj_url'] = 'https://github.com/bz-22/final-project'
PROP['project_folder_name'] = 'final'


pipeline {

    agent { label 'jenkins_ec2' } // Use the label of your EC2 agent


    stages {


    stage('Git clone') {
        steps {
            script {
                println("=====================================${STAGE_NAME}=====================================")
                println("Cloning from branch ${PROP.branch} and using credentials ${PROP.git_cred}")

                // Enable sparse checkout
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${PROP.branch}"]],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [
                        [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [[path: PROP.project_folder_name]]]
                    ],
                    submoduleCfg: [],
                    userRemoteConfigs: [[
                        credentialsId: PROP.git_cred,
                        url: PROP.proj_url
                    ]]
                ])
            }
        }
        }




        stage('delete .git golder'){
            steps {
                script {
                    println("=====================================${STAGE_NAME}=====================================")
                    println("Deleting.git folder")
                    sh 'rm -rf .git'
                }
            }
        }


        stage('Commit and Push'){
            steps {
                script {
                    println("=====================================${STAGE_NAME}=====================================")
                    println("Committing and pushing changes")
                    sshagent([PROP.git_cred]){
                        sh """
                            git init
                            git config user.name "bz-22"
                            git config user.email "bsharzein@gmail.com"
                            git branch -M main
                            git remote add origin git@github.com:bz-22/chartNew.git
                            git add .
                            git commit -m "Commit at ${System.currentTimeMillis()}"

                            git pull origin main --rebase
                            git push --set-upstream origin main
                        """
                    }
                }
            }
        }




    }


}