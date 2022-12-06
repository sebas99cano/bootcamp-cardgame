mvn clean package -DskipTests
docker build -t raul-command .
docker tag raul-command:latest 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-command:latest
docker push 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-command:latest
