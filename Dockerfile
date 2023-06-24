FROM  amazoncorretto:11-alpine-jdk
MAINTAINER LMA
COPY target/abmlcontroller-0.0.1-SNAPSHOT.jar  abmlcontroller-app.jar
ENTRYPOINT ["java","-jar","/abmlcontroller-app.jar"]