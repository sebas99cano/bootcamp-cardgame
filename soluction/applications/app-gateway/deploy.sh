mvn clean package -DskipTests
docker build -t raul-gateway .
docker tag raul-gateway:latest 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-gateway:latest
docker push 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-gateway:latest
kubectl rollout restart deployment/cardgame-core-gateway