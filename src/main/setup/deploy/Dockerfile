############################################################
# Dockerfile for Dastrax deployment
# Version 1.0.3
############################################################

FROM        ubuntu:14.04
MAINTAINER  Tarka L'Herpiniere <tarka@solid.com>

RUN         apt-get update

# Prepare for Java download
RUN         apt-get install -y python-software-properties
RUN         apt-get install -y software-properties-common

# Add the Java repository
RUN         add-apt-repository -y ppa:webupd8team/java

# Update the repository sources list
RUN         apt-get update

# Install Oracle Java 8
RUN         echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN         apt-get install oracle-java8-installer -y --force-yes
RUN         apt-get install oracle-java8-set-default -y --force-yes

# Set glassfish home as enviroment property
ENV         GLASSFISH_HOME    /usr/local/glassfish4
ENV         PATH              $PATH:$GLASSFISH_HOME/bin

# Install Glassfish
RUN         apt-get update && \
            apt-get install -y curl unzip zip inotify-tools && \
            rm -rf /var/lib/apt/lists/*

RUN         curl -L -o /tmp/glassfish-4.1.zip http://download.java.net/glassfish/4.1/release/glassfish-4.1.zip && \
            unzip /tmp/glassfish-4.1.zip -d /usr/local && \
            rm -f /tmp/glassfish-4.1.zip

COPY         glassfish-start.sh /
COPY         *.war /
COPY         mysql-connector-java-5.1.35-bin.jar /usr/local/glassfish4/glassfish/lib/

RUN         chmod +x /glassfish-start.sh
RUN         apt-get update

EXPOSE      8080 4848

ENTRYPOINT  ["/glassfish-start.sh"]
