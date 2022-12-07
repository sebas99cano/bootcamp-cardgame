mvn clean clean package -DskipTests
docker build -t raul-master .
docker tag raul-master:latest 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-master:latest
docker push 316078593388.dkr.ecr.us-east-1.amazonaws.com/raul-master:latest
kubectl rollout restart deployment/cardgame-core-master
