#!groovy
/**
 * Jenkins pipeline to build the kotlin CorDapp template
 */

/**
 * Kill already started job.
 * Assume new commit takes precedence and results from previousunfinished builds are not required.
 * This feature doesn't play well with disableConcurrentBuilds() option
 */
@Library('corda-shared-build-pipeline-steps')
import static com.r3.build.BuildControl.killAllExistingBuildsForJob
killAllExistingBuildsForJob(env.JOB_NAME, env.BUILD_NUMBER.toInteger())

def isReleaseTag() {
    return (env.TAG_NAME =~ /^release-.*$/)
}

def isReleaseCandidate() {
    return (isReleaseTag()) && (env.TAG_NAME =~ /.*-(RC|HC)\d+(-.*)?/)
}

def isReleaseBranch() {
    return (env.BRANCH_NAME =~ /^release\/.*$/)
}

def isRelease = isReleaseTag() || isReleaseCandidate()
String publishOptions = (isReleaseBranch() || isRelease) ? "-s --info" : "--no-daemon -s"

pipeline {
    agent { label 'standard' }

    parameters {
        booleanParam name: 'DO_PUBLISH', defaultValue: (isRelease || isReleaseBranch()), description: 'Publish artifacts to Artifactory?'
    }

    options {
        ansiColor('xterm')
        timestamps()
        timeout(3*60) // 3 hours
        buildDiscarder(logRotator(daysToKeepStr: '7', artifactDaysToKeepStr: '7'))
    }

    environment {
        JAVA_HOME="/usr/lib/jvm/java-17-amazon-corretto"
    }

    stages {
        stage('Build contract-sdk') {
            steps {
                sh './gradlew clean build --no-daemon --refresh-dependencies'
            }
        }
        stage('Publish to Artifactory') {
            when {
                expression { params.DO_PUBLISH }
                beforeAgent true
            }
            steps {
                rtServer(
                        id: 'R3-Artifactory',
                        url: 'https://software.r3.com/artifactory',
                        credentialsId: 'artifactory-credentials'
                )
                rtGradleDeployer(
                        id: 'deployer',
                        serverId: 'R3-Artifactory',
                        repo: isRelease ? 'corda-lib' : 'corda-lib-dev'
                )
                rtGradleRun(
                        usesPlugin: true,
                        useWrapper: true,
                        switches: publishOptions,
                        tasks: 'artifactoryPublish',
                        deployerId: 'deployer',
                        buildName: env.ARTIFACTORY_BUILD_NAME
                )
                rtPublishBuildInfo(
                        serverId: 'R3-Artifactory',
                        buildName: env.ARTIFACTORY_BUILD_NAME
                )
            }
        }
    }
    post {
        always {
            junit '**/build/test-results/**/*.xml'
        }
        cleanup {
            deleteDir() /* clean up our workspace */
        }
    }
}