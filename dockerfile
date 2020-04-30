#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
RUN mvn -f /home/app/src/pom.xml clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
USER root
RUN mkdir /home/workspace
COPY --from=build /home/app/src/biodwh2-main/target/BioDWH*.jar /usr/local/lib/BioDWH.jar
#RUN java -jar /usr/local/lib/BioDWH.jar -c /home/workspace
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/BioDWH.jar"]