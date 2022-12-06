mvn clean package -DskipTests
docker build -t raul-proxy .
docker tag raul-command:latest 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-proxy:latest
docker push 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-proxy:latest
