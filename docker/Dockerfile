FROM tomcat:7-jre8
# FROM tomcat:7.0.105-jdk8-openjdk-slim * Fails with some security error

LABEL maintainer="ioexcept@gmail.com"
LABEL version="1.1"

RUN rm -rf /usr/local/tomcat/webapps/*

ADD tethysui.war /usr/local/tomcat/webapps/
ADD https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.20/mysql-connector-java-8.0.20.jar /usr/local/tomcat/lib/
ADD server.xml /usr/local/tomcat/conf/server.xml
ADD context.xml /usr/local/tomcat/conf/context.xml
