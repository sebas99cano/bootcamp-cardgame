mvn clean package -DskipTests
docker build -t raul-queries .
docker tag raul-queries:latest 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-queries:latest
docker push 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-queries:latest
kubectl rollout restart deployment/cardgame-core-queries