docker run --name zipkin -d -p 9411:9411 openzipkin/zipkin-slim
docker run -t -i --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
docker run --name mongodb -v /var/data:/etc/mongo  -p 27017:27017 -d mongo:3
Copy the following contents to a file named cluster-trust-policy.json.


{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "eks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
Create the role. You can replace eksClusterRole with any name that you choose.


aws iam create-role \
  --role-name eksClusterRole \
  --assume-role-policy-document file://"cluster-trust-policy.json"
Attach the required IAM policy to the role.


aws iam attach-role-policy \
  --policy-arn arn:aws:iam::aws:policy/AmazonEKSClusterPolicy \
  --role-name eksClusterRole

  eksctl create cluster --name demo --region region-code --version 1.24 --vpc-private-subnets subnet-ExampleID1,subnet-ExampleID2 --without-nodegroup