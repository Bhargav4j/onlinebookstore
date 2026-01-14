=== docs/DEPLOYMENT.md ===
# Deployment Guide: Online Bookstore Application

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Building Docker Images](#building-docker-images)
4. [AWS ECS Fargate Prerequisites](#aws-ecs-fargate-prerequisites)
5. [ECS Fargate Setup](#ecs-fargate-setup)
6. [Deployment Walkthrough](#deployment-walkthrough)
7. [Configuration Management](#configuration-management)
8. [Monitoring and Logging](#monitoring-and-logging)
9. [Troubleshooting](#troubleshooting)
10. [Scaling and Management](#scaling-and-management)
11. [Security Considerations](#security-considerations)

---

## Prerequisites

### Required Tools
- **Docker** (version 20.10 or later)
- **Docker Compose** (version 2.0 or later)
- **AWS CLI** (version 2.x)
- **Java 8 JDK** (for local development)
- **Maven 3.6+** (for local builds)

### AWS Account Requirements
- Active AWS account with appropriate permissions
- IAM user with ECS, ECR, VPC, and CloudWatch permissions
- AWS credentials configured (`aws configure`)

### Installation

**Docker:**
- Linux: `sudo apt-get install docker.io docker-compose`
- macOS: Download Docker Desktop from docker.com
- Windows: Download Docker Desktop from docker.com

**AWS CLI:**
```bash
# Linux/macOS
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Windows
msiexec.exe /i https://awscli.amazonaws.com/AWSCLIV2.msi

# Configure AWS CLI
aws configure
```

---

## Local Development Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd "Online check test"
```

### 2. Configure Application Properties
Edit `src/main/resources/application.properties`:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.host=jdbc:mysql://localhost
db.port=3306
db.name=onlinebookstore
db.username=root
db.password=root
```

### 3. Set Up Local Database
The application requires a MySQL database. You can run MySQL locally or use Docker:

```bash
# Run MySQL in Docker
docker run -d \
  --name mysql-db \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=onlinebookstore \
  -p 3306:3306 \
  mysql:8.0
```

### 4. Build and Run with Docker Compose
```bash
# Build and start the application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down
```

### 5. Access the Application
Open your browser and navigate to:
- **Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/health

---

## Building Docker Images

### Using Build Scripts

The project includes automated build scripts for both Linux/macOS and Windows:

**Linux/macOS:**
```bash
chmod +x scripts/build-push.sh
./scripts/build-push.sh
```

**Windows:**
```cmd
scripts\build-push.bat
```

### Script Features
- Interactive registry selection (AWS ECR or Docker Hub)
- Automatic ECR repository creation
- Image tag sanitization
- Authentication handling
- Build progress indicators

### Manual Docker Build
```bash
# Build the image
docker build -t onlinebookstore:latest .

# Tag for ECR
docker tag onlinebookstore:latest \  123456789.dkr.ecr.us-east-1.amazonaws.com/onlinebookstore:latest

# Push to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  123456789.dkr.ecr.us-east-1.amazonaws.com

docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/onlinebookstore:latest
```

---

## AWS ECS Fargate Prerequisites

### 1. VPC and Networking Setup

You need a VPC with at least 2 subnets in different availability zones:

```bash
# Create VPC (if needed)
VPC_ID=$(aws ec2 create-vpc \
  --cidr-block 10.0.0.0/16 \
  --query 'Vpc.VpcId' \
  --output text)

# Create subnets
SUBNET_1=$(aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.1.0/24 \
  --availability-zone us-east-1a \
  --query 'Subnet.SubnetId' \
  --output text)

SUBNET_2=$(aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.2.0/24 \
  --availability-zone us-east-1b \
  --query 'Subnet.SubnetId' \
  --output text)

# Enable auto-assign public IP
aws ec2 modify-subnet-attribute \
  --subnet-id $SUBNET_1 \
  --map-public-ip-on-launch

aws ec2 modify-subnet-attribute \
  --subnet-id $SUBNET_2 \
  --map-public-ip-on-launch

# Create Internet Gateway
IGW_ID=$(aws ec2 create-internet-gateway \
  --query 'InternetGateway.InternetGatewayId' \
  --output text)

aws ec2 attach-internet-gateway \
  --vpc-id $VPC_ID \
  --internet-gateway-id $IGW_ID

# Update route table
RTB_ID=$(aws ec2 describe-route-tables \
  --filters Name=vpc-id,Values=$VPC_ID \
  --query 'RouteTables[0].RouteTableId' \
  --output text)

aws ec2 create-route \
  --route-table-id $RTB_ID \
  --destination-cidr-block 0.0.0.0/0 \
  --gateway-id $IGW_ID
```

### 2. Security Group Configuration

```bash
# Create security group
SG_ID=$(aws ec2 create-security-group \
  --group-name onlinebookstore-sg \
  --description "Security group for Online Bookstore ECS tasks" \
  --vpc-id $VPC_ID \
  --query 'GroupId' \
  --output text)

# Allow inbound HTTP traffic on port 8080
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 8080 \
  --cidr 0.0.0.0/0

# Allow inbound HTTP traffic on port 80 (for ALB)
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0
```

### 3. IAM Roles Setup

**ECS Task Execution Role:**
```bash
# Create trust policy
cat > ecs-trust-policy.json <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

# Create role
aws iam create-role \
  --role-name ecsTaskExecutionRole \
  --assume-role-policy-document file://ecs-trust-policy.json

# Attach managed policy
aws iam attach-role-policy \
  --role-name ecsTaskExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy
```

**ECS Task Role (for application permissions):**
```bash
aws iam create-role \
  --role-name ecsTaskRole \
  --assume-role-policy-document file://ecs-trust-policy.json

# Attach policies as needed (e.g., S3, RDS, Secrets Manager)
```

### 4. CloudWatch Log Group
```bash
aws logs create-log-group \
  --log-group-name /ecs/onlinebookstore \
  --region us-east-1
```

---

## ECS Fargate Setup

### Understanding ECS Task Definitions

The task definition (`ecs/task-definition.json`) specifies:
- **Launch Type**: FARGATE (serverless containers)
- **Network Mode**: awsvpc (each task gets its own ENI)
- **CPU and Memory**: Valid combinations for Fargate:
  - CPU: 512 (.5 vCPU) → Memory: 1024 MB (1 GB)
  - CPU: 1024 (1 vCPU) → Memory: 2048-8192 MB
  - CPU: 2048 (2 vCPU) → Memory: 4096-16384 MB
- **Container Definitions**: Application container specification
- **Logging**: CloudWatch Logs configuration

### Understanding ECS Service Configuration

The service definition (`ecs/service-definition.json`) specifies:
- **Desired Count**: Number of tasks to run (2 for high availability)
- **Launch Type**: FARGATE
- **Network Configuration**: VPC, subnets, security groups
- **Load Balancer**: Optional ALB integration
- **Deployment Configuration**: Rolling update strategy
- **Auto Scaling**: (Can be configured post-deployment)

---

## Deployment Walkthrough

### Step 1: Build and Push Docker Image

```bash
# Linux/macOS
chmod +x scripts/build-push.sh
./scripts/build-push.sh

# Windows
scripts\build-push.bat
```

Follow the prompts:
1. Select registry type (1 for ECR, 2 for Docker Hub)
2. Enter AWS region and account ID (for ECR)
3. Enter repository name
4. Enter image tag (default: latest)
5. Script will build and push automatically

**Save the Image URI** displayed at the end (e.g., `123456789.dkr.ecr.us-east-1.amazonaws.com/onlinebookstore:latest`)

### Step 2: Deploy to ECS Fargate

```bash
# Linux/macOS
chmod +x scripts/deploy-image.sh
./scripts/deploy-image.sh

# Windows
scripts\deploy-image.bat
```

Follow the prompts:
1. **AWS Region**: us-east-1
2. **ECS Cluster Name**: onlinebookstore-cluster
3. **VPC ID**: vpc-xxxxx (from prerequisites)
4. **Subnet IDs**: subnet-xxxxx,subnet-yyyyy (comma-separated)
5. **Security Group ID**: sg-xxxxx
6. **Image URI**: (paste from Step 1)
7. **Database Configuration**:
   - DB Host: your-rds-endpoint.amazonaws.com
   - DB Port: 3306
   - DB Name: onlinebookstore
   - DB Username: admin
   - DB Password: (enter securely)
8. **Load Balancer**: y (recommended for production)

### Step 3: Verify Deployment

```bash
# Check service status
aws ecs describe-services \
  --cluster onlinebookstore-cluster \
  --services onlinebookstore-service \
  --region us-east-1

# Check running tasks
aws ecs list-tasks \
  --cluster onlinebookstore-cluster \
  --service-name onlinebookstore-service \
  --region us-east-1

# View logs
aws logs tail /ecs/onlinebookstore --follow --region us-east-1
```

### Step 4: Access the Application

If you configured a load balancer:
```bash
# Get ALB DNS name
ALB_DNS=$(aws elbv2 describe-load-balancers \
  --names onlinebookstore-alb \
  --query 'LoadBalancers[0].DNSName' \
  --output text)

echo "Application URL: http://$ALB_DNS"
```

Open the URL in your browser:
- **Application**: http://your-alb-dns
- **Health Check**: http://your-alb-dns/health

---

## Configuration Management

### Environment Variables

The application uses environment variables for configuration:

| Variable | Description | Default |
|----------|-------------|----------|
| `JAVA_OPTS` | JVM options | `-Xmx512m -Xms256m` |
| `TZ` | Timezone | `UTC` |
| `DB_HOST` | Database host | `localhost` |
| `DB_PORT` | Database port | `3306` |
| `DB_NAME` | Database name | `onlinebookstore` |
| `DB_USER_NAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `root` |

### Updating Configuration

**Option 1: Update Task Definition**
1. Edit `ecs/task-definition.json`
2. Update environment variables
3. Re-run deployment script

**Option 2: Use AWS Secrets Manager**
```bash
# Store secret
aws secretsmanager create-secret \
  --name onlinebookstore/db-password \
  --secret-string "your-secure-password"

# Reference in task definition
"secrets": [
  {
    "name": "DB_PASSWORD",
    "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789:secret:onlinebookstore/db-password"
  }
]
```

---

## Monitoring and Logging

### CloudWatch Logs

```bash
# View real-time logs
aws logs tail /ecs/onlinebookstore --follow --region us-east-1

# View logs for specific time range
aws logs filter-log-events \
  --log-group-name /ecs/onlinebookstore \
  --start-time $(date -d '1 hour ago' +%s)000 \
  --region us-east-1

# Search logs for errors
aws logs filter-log-events \
  --log-group-name /ecs/onlinebookstore \
  --filter-pattern "ERROR" \
  --region us-east-1
```

### CloudWatch Metrics

Key metrics to monitor:
- **CPUUtilization**: Target < 70%
- **MemoryUtilization**: Target < 80%
- **TargetResponseTime**: Target < 1s
- **HealthyHostCount**: Should equal desired count

```bash
# View CPU utilization
aws cloudwatch get-metric-statistics \
  --namespace AWS/ECS \
  --metric-name CPUUtilization \
  --dimensions Name=ServiceName,Value=onlinebookstore-service \
  --statistics Average \
  --start-time $(date -u -d '1 hour ago' +%Y-%m-%dT%H:%M:%S) \
  --end-time $(date -u +%Y-%m-%dT%H:%M:%S) \
  --period 300 \
  --region us-east-1
```

### Application Health Checks

```bash
# Manual health check
curl http://your-alb-dns/health

# Expected response:
# {"status": "UP", "timestamp": "2024-01-14T10:30:00Z"}
```

---

## Troubleshooting

### Common Issues

#### 1. Task Fails to Start

**Symptoms**: Tasks stop immediately after starting

**Causes**:
- Invalid CPU/memory combination
- Image pull errors (ECR permissions)
- Application startup failures

**Solutions**:
```bash
# Check stopped tasks
aws ecs describe-tasks \
  --cluster onlinebookstore-cluster \
  --tasks $(aws ecs list-tasks \
    --cluster onlinebookstore-cluster \
    --desired-status STOPPED \
    --query 'taskArns[0]' \
    --output text) \
  --region us-east-1

# Check task execution role permissions
aws iam get-role --role-name ecsTaskExecutionRole

# Verify ECR permissions
aws ecr get-repository-policy \
  --repository-name onlinebookstore \
  --region us-east-1
```

#### 2. Network Connectivity Issues

**Symptoms**: Cannot reach application via ALB

**Solutions**:
```bash
# Verify security group rules
aws ec2 describe-security-groups \
  --group-ids sg-xxxxx \
  --region us-east-1

# Check target group health
aws elbv2 describe-target-health \
  --target-group-arn arn:aws:elasticloadbalancing:... \
  --region us-east-1

# Ensure subnets have internet gateway route
aws ec2 describe-route-tables \
  --filters Name=association.subnet-id,Values=subnet-xxxxx \
  --region us-east-1
```

#### 3. Database Connection Errors

**Symptoms**: Application logs show DB connection failures

**Solutions**:
```bash
# Verify DB endpoint and credentials
aws rds describe-db-instances \
  --db-instance-identifier your-db-instance \
  --query 'DBInstances[0].Endpoint' \
  --region us-east-1

# Check RDS security group
# Ensure it allows inbound traffic from ECS security group

# Test connectivity from ECS task
aws ecs execute-command \
  --cluster onlinebookstore-cluster \
  --task task-id \
  --container onlinebookstore \
  --interactive \
  --command "/bin/sh"

# Inside container:
telnet your-rds-endpoint 3306
```

#### 4. High Memory Usage

**Symptoms**: Tasks killed due to OOM

**Solutions**:
- Increase task memory in `ecs/task-definition.json`
- Tune JVM heap size: `JAVA_OPTS="-Xmx768m -Xms384m"`
- Review application for memory leaks

```bash
# Update task definition with more memory
# CPU: 1024, Memory: 2048
aws ecs register-task-definition \
  --cli-input-json file://ecs/task-definition.json

# Update service
aws ecs update-service \
  --cluster onlinebookstore-cluster \
  --service onlinebookstore-service \
  --task-definition onlinebookstore-task:NEW_REVISION
```

---

## Scaling and Management

### Manual Scaling

```bash
# Scale up to 5 tasks
aws ecs update-service \
  --cluster onlinebookstore-cluster \
  --service onlinebookstore-service \
  --desired-count 5 \
  --region us-east-1

# Scale down to 1 task
aws ecs update-service \
  --cluster onlinebookstore-cluster \
  --service onlinebookstore-service \
  --desired-count 1 \
  --region us-east-1
```

### Auto Scaling

```bash
# Register scalable target
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --resource-id service/onlinebookstore-cluster/onlinebookstore-service \
  --scalable-dimension ecs:service:DesiredCount \
  --min-capacity 2 \
  --max-capacity 10 \
  --region us-east-1

# Create scaling policy (target tracking)
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --resource-id service/onlinebookstore-cluster/onlinebookstore-service \
  --scalable-dimension ecs:service:DesiredCount \
  --policy-name cpu-scaling-policy \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration file://scaling-policy.json \
  --region us-east-1
```

**scaling-policy.json**:
```json
{
  "TargetValue": 70.0,
  "PredefinedMetricSpecification": {
    "PredefinedMetricType": "ECSServiceAverageCPUUtilization"
  },
  "ScaleInCooldown": 300,
  "ScaleOutCooldown": 60
}
```

### Blue/Green Deployments

```bash
# Create CodeDeploy application
aws deploy create-application \
  --application-name onlinebookstore-app \
  --compute-platform ECS

# Create deployment group
aws deploy create-deployment-group \
  --application-name onlinebookstore-app \
  --deployment-group-name onlinebookstore-dg \
  --deployment-config-name CodeDeployDefault.ECSAllAtOnce \
  --service-role-arn arn:aws:iam::123456789:role/CodeDeployServiceRole \
  --ecs-services clusterName=onlinebookstore-cluster,serviceName=onlinebookstore-service \
  --load-balancer-info targetGroupPairInfoList=[...]
```

### Rolling Updates

```bash
# Update to new image
aws ecs update-service \
  --cluster onlinebookstore-cluster \
  --service onlinebookstore-service \
  --force-new-deployment \
  --region us-east-1

# Monitor deployment
aws ecs describe-services \
  --cluster onlinebookstore-cluster \
  --services onlinebookstore-service \
  --query 'services[0].deployments' \
  --region us-east-1
```

---

## Security Considerations

### 1. Use Secrets Manager for Sensitive Data
```bash
# Store database credentials
aws secretsmanager create-secret \
  --name onlinebookstore/db-credentials \
  --secret-string '{"username":"admin","password":"secure-password"}'

# Update task definition to reference secrets
"secrets": [
  {
    "name": "DB_USER_NAME",
    "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789:secret:onlinebookstore/db-credentials:username::"
  },
  {
    "name": "DB_PASSWORD",
    "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789:secret:onlinebookstore/db-credentials:password::"
  }
]
```

### 2. Restrict Security Group Rules
- Only allow necessary inbound ports
- Use security group chaining (ECS SG → RDS SG)
- Regularly audit and remove unused rules

### 3. Enable VPC Flow Logs
```bash
aws ec2 create-flow-logs \
  --resource-type VPC \
  --resource-ids vpc-xxxxx \
  --traffic-type ALL \
  --log-destination-type cloud-watch-logs \
  --log-group-name /aws/vpc/flowlogs
```

### 4. Use IAM Roles (Never Credentials)
- Task execution role for ECS operations
- Task role for application permissions
- Principle of least privilege

### 5. Enable Container Insights
```bash
aws ecs update-cluster-settings \
  --cluster onlinebookstore-cluster \
  --settings name=containerInsights,value=enabled
```

### 6. Regular Security Updates
- Keep base images updated
- Scan images for vulnerabilities
- Monitor AWS Security Bulletins

```bash
# Enable ECR image scanning
aws ecr put-image-scanning-configuration \
  --repository-name onlinebookstore \
  --image-scanning-configuration scanOnPush=true

# View scan results
aws ecr describe-image-scan-findings \
  --repository-name onlinebookstore \
  --image-id imageTag=latest
```

---

## Technology-Specific Notes

### Java 8 Servlet Application

**JVM Tuning:**
- Heap size should be ~75% of container memory
- Use container-aware flags: `-XX:+UseContainerSupport`
- Enable GC logging: `-Xlog:gc*:file=/app/logs/gc.log`

**Webapp-Runner:**
- Embedded Tomcat server for running WAR files
- Supports session management and JNDI
- Configure via command-line arguments

**Health Checks:**
- Custom servlet at `/health` endpoint
- Returns JSON status
- Can be extended for database connectivity checks

**Monitoring:**
- Add JMX monitoring: `-Dcom.sun.management.jmxremote`
- Use CloudWatch agent for custom metrics
- Consider adding APM tools (New Relic, Datadog)

---

## Support and Resources

### AWS Documentation
- [ECS Fargate Documentation](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/AWS_Fargate.html)
- [ECR User Guide](https://docs.aws.amazon.com/AmazonECR/latest/userguide/)
- [CloudWatch Logs](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/)

### Useful Commands Reference

```bash
# List all ECS clusters
aws ecs list-clusters --region us-east-1

# Describe service
aws ecs describe-services \
  --cluster onlinebookstore-cluster \
  --services onlinebookstore-service

# View task details
aws ecs describe-tasks \
  --cluster onlinebookstore-cluster \
  --tasks task-id

# Force new deployment
aws ecs update-service \
  --cluster onlinebookstore-cluster \
  --service onlinebookstore-service \
  --force-new-deployment

# Delete service
aws ecs delete-service \
  --cluster onlinebookstore-cluster \
  --service onlinebookstore-service \
  --force

# Delete cluster
aws ecs delete-cluster \
  --cluster onlinebookstore-cluster
```

---

## Conclusion

This deployment guide covers the complete lifecycle of deploying the Online Bookstore application to AWS ECS Fargate. For additional assistance:

1. Review AWS ECS documentation
2. Check CloudWatch logs for errors
3. Consult AWS Support for infrastructure issues
4. Review application logs for application-specific problems

**Best Practices:**
- Always test in non-production environments first
- Use infrastructure as code (Terraform/CloudFormation)
- Implement CI/CD pipelines for automated deployments
- Monitor costs with AWS Cost Explorer
- Set up billing alerts

---

**Document Version**: 1.0  
**Last Updated**: 2024-01-14  
**Deployment Platform**: AWS ECS Fargate  
**Application**: Online Bookstore (Java 8 Servlet)  
