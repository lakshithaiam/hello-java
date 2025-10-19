pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:3.9.6-eclipse-temurin-17
    command:
    - cat
    tty: true
  - name: sonar-scanner
    image: sonarsource/sonar-scanner-cli
    command:
    - cat
    tty: true
  - name: kubectl
    image: bitnami/kubectl:latest
    command:
    - cat
    tty: true
    securityContext:
      runAsUser: 0
      readOnlyRootFilesystem: false
    env:
    - name: KUBECONFIG
      value: /kube/config        
    volumeMounts:
    - name: kubeconfig-secret
      mountPath: /kube/config
      subPath: kubeconfig
  - name: dind
    image: docker:dind
    args: ["--registry-mirror=https://mirror.gcr.io", "--storage-driver=overlay2"]
    securityContext:
      privileged: true  # Needed to run Docker daemon
    env:
    - name: DOCKER_TLS_CERTDIR
      value: ""  # Disable TLS for simplicity
    volumeMounts:
    - name: docker-config
      mountPath: /etc/docker/daemon.json
      subPath: daemon.json  # Mount the file directly here
  volumes:
  - name: docker-config
    configMap:
      name: docker-daemon-config
  - name: kubeconfig-secret
    secret:
      secretName: kubeconfig-secret
'''
        }
    }
    
    
    stages {
        stage('Run Tests') {
            steps {
                container('python') {
                }
            }
            
        }
        stage('SonarQube Analysis') {
            steps {
                container('maven') {
                    sh '''
                        mvn clean verify sonar:sonar \
                        -Dsonar.projectKey=hello-java \
                        -Dsonar.projectName='hello-java' \
                        -Dsonar.host.url=http://my-sonarqube-sonarqube.sonarqube.svc.cluster.local:9000 \
                        -Dsonar.token=sqp_3acfb8be197be4c3f9b6f0d2660194014be8d78c
                    '''
                }
            }
        }
        stage('Login to Docker Registry') {
            steps {
                container('dind') {
                    sh 'docker --version'
                    sh 'sleep 10'
                    sh 'docker login nexus-service-for-docker-hosted-registry.nexus.svc.cluster.local:8085 -u admin -p Changeme@2025'
                }
            }
        }
        stage('Build - Tag - Push') {
            steps {
                container('dind') {
                    sh 'docker build -t nexus-service-for-docker-hosted-registry.nexus.svc.cluster.local:8085/my-repository/hello-java:v004 .'
                    sh 'docker push nexus-service-for-docker-hosted-registry.nexus.svc.cluster.local:8085/my-repository/hello-java:v004'
                    sh 'docker pull nexus-service-for-docker-hosted-registry.nexus.svc.cluster.local:8085/my-repository/hello-java:v004'
                    sh 'docker image ls'
                }
            }
        }
        stage('Deploy hello-java') {
            steps {
                container('kubectl') {
                    script {
                        dir('hello-java-deployment') {
                            sh 'kubectl get node'
                            sh 'kubectl apply -f hello-java-namespace.yaml'
                            sh 'kubectl apply -f hello-java-deployment.yaml'
                            sh 'sleep 99999'
                        }
                    }
                }
            }
        }
    }
}