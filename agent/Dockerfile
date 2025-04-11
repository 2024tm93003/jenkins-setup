FROM jenkins/inbound-agent:latest

# Install curl, Node.js 18, npm, Angular CLI and http-server
USER root

RUN apt-get update && \
    apt-get install -y curl gnupg && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs && \
    npm install -g @angular/cli http-server pm2 && \
    apt-get clean

WORKDIR /home/jenkins

ENV PATH=$PATH:/home/jenkins/.npm-global/bin
USER jenkins
