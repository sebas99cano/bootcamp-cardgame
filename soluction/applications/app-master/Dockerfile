## mvn clean package -DskipTests
## docker build -t raul-master .
## docker tag raul-master:latest 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-master:latest
## docker push 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-master:latest

FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8085
