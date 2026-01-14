# Deployment Guide for Online Bookstore Application

This guide provides comprehensive instructions for deploying the Online Bookstore Java web application using Docker and AWS EKS (Elastic Kubernetes Service).

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Project Overview](#project-overview)
3. [Local Development Setup](#local-development-setup)
4. [Building Docker Images](#building-docker-images)
5. [AWS EKS Deployment](#aws-eks-deployment)
6. [Configuration Management](#configuration-management)
7. [Monitoring and Logging](#monitoring-and-logging)
8. [Troubleshooting](#troubleshooting)
9. [Security Considerations](#security-considerations)
10. [Rollback Procedures](#rollback-procedures)

## Prerequisites

### Required Tools

- **Docker**: Version 20.10 or higher
  - Download: https://docs.docker.com/get-docker/
  - Verify: `docker --version`

- **Docker Compose**: Version 2.0 or higher
  - Included with Docker Desktop
  - Verify: `docker-compose --version`

- **AWS CLI**: Version 2.x
  - Download: https://aws.amazon.com/cli/
  - Verify: `aws --version`
  - Configure: `aws configure`

- **kubectl**: Version 1.24 or higher
  - Download: https://kubernetes.io/docs/tasks/tools/
  - Verify: `kubectl version --client`

- **Java Development Kit**: JDK 8 (for local development)
  - Download: https://adoptium.net/

- **Maven**: Version 3.6 or higher (for local builds)
  - Download: https://maven.apache.org/download.cgi

### AWS Requirements

- AWS Account with appropriate permissions
- IAM user with policies:
  - `AmazonEKSClusterPolicy`
  - `AmazonEKSWorkerNodePolicy`
  - `AmazonEC2ContainerRegistryFullAccess`
  - `AmazonEKS_CNI_Policy`

- EKS Cluster (pre-created or create using eksctl)
- AWS Load Balancer Controller installed on EKS cluster

## Project Overview

### Application Details

- **Name**: Online Bookstore
- **Type**: Java Web Application (Servlet-based)
- **Build Tool**: Maven
- **Java Version**: 8
- **Package Type**: WAR
- **Web Server**: Tomcat (via webapp-runner)
- **Default Port**: 8080
- **Health Check**: `/health`

### Technology Stack

- Java 8
- Servlet API 3.1.0
- PostgreSQL Driver 42.3.7
- MySQL Connector 8.0.28
- Webapp Runner 8.0.30.2

### Database Configuration

The application requires a MySQL database with the following configuration:

- **Database Name**: onlinebookstore
- **Default Host**: localhost
- **Default Port**: 3306
- **Default Username**: root
- **Default Password**: root

Environment variables can override these defaults (see Configuration Management section).

## Local Development Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd "test DB check app tes"
```

### 2. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.host=jdbc:mysql://localhost
db.port=3306
db.name=onlinebookstore
db.username=root
db.password=root
```

### 3. Build the Application

```bash
mvn clean package
```

The WAR file will be created at `target/onlinebookstore.war`.

### 4. Run with Docker Compose

```bash
# Update docker-compose.yml with your database configuration
# Then start the application
docker-compose up --build
```

Access the application at: `http://localhost:8080`

Health check endpoint: `http://localhost:8080/health`

### 5. Stop the Application

```bash
docker-compose down
```

## Building Docker Images

### Option 1: Using build-push.sh (Linux/macOS)

```bash
chmod +x scripts/build-push.sh
./scripts/build-push.sh
```

### Option 2: Using build-push.bat (Windows)

```cmd
scripts\build-push.bat
```

### Script Features

The build scripts support two registry types:

1. **AWS ECR (Elastic Container Registry)**
   - Automatically creates repository if it doesn't exist
   - Configures authentication via AWS CLI
   - Requires AWS Account ID and Region

2. **Docker Hub**
   - Uses Docker Hub credentials
   - Requires Docker Hub username and password/token

### Manual Docker Build

If you prefer to build manually:

```bash
# Build the image
docker build -t onlinebookstore:latest .

# Tag for ECR
docker tag onlinebookstore:latest <account-id>.dkr.ecr.<region>.amazonaws.com/onlinebookstore:latest

# Login to ECR
aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account-id>.dkr.ecr.<region>.amazonaws.com

# Push to ECR
docker push <account-id>.dkr.ecr.<region>.amazonaws.com/onlinebookstore:latest
```

## AWS EKS Deployment

### Prerequisites

1. **Create EKS Cluster** (if not already created):

```bash
eksctl create cluster \
  --name onlinebookstore-cluster \
  --region us-east-1 \
  --nodegroup-name standard-workers \
  --node-type t3.medium \
  --nodes 2 \
  --nodes-min 1 \
  --nodes-max 3 \
  --managed
```

2. **Install AWS Load Balancer Controller**:

```bash
# Download IAM policy
curl -o iam_policy.json https://raw.githubusercontent.com/kubernetes-sigs/aws-load-balancer-controller/v2.4.7/docs/install/iam_policy.json

# Create IAM policy
aws iam create-policy \
  --policy-name AWSLoadBalancerControllerIAMPolicy \
  --policy-document file://iam_policy.json

# Create IAM service account
eksctl create iamserviceaccount \
  --cluster=onlinebookstore-cluster \
  --namespace=kube-system \
  --name=aws-load-balancer-controller \
  --attach-policy-arn=arn:aws:iam::<AWS_ACCOUNT_ID>:policy/AWSLoadBalancerControllerIAMPolicy \
  --override-existing-serviceaccounts \
  --approve

# Install using Helm
helm repo add eks https://aws.github.io/eks-charts
helm repo update
helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=onlinebookstore-cluster \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller
```

### Deployment Steps

#### Step 1: Prepare Database

Ensure you have a MySQL database accessible from EKS:

- AWS RDS MySQL instance
- Self-hosted MySQL on EC2
- External database with network connectivity

Note the following details:
- Database host (hostname or IP)
- Database port (default: 3306)
- Database username
- Database password

#### Step 2: Deploy Application

**Linux/macOS:**

```bash
chmod +x scripts/deploy-image.sh
./scripts/deploy-image.sh
```

**Windows:**

```cmd
scripts\deploy-image.bat
```

#### Step 3: Verify Deployment

```bash
# Check namespace
kubectl get namespace onlinebookstore

# Check pods
kubectl get pods -n onlinebookstore

# Check service
kubectl get svc -n onlinebookstore

# Check ingress
kubectl get ingress -n onlinebookstore

# Get application URL
kubectl get ingress onlinebookstore-ingress -n onlinebookstore -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'
```

#### Step 4: View Logs

```bash
# View application logs
kubectl logs -f deployment/onlinebookstore -n onlinebookstore

# View all pods logs
kubectl logs -l app=onlinebookstore -n onlinebookstore --all-containers=true
```

### Manual Kubernetes Deployment

If you prefer manual deployment:

```bash
# Update manifests with your image URI and configuration
# Edit kubernetes/deployment.yaml and replace {{IMAGE_URI}} with your image

# Apply manifests
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
kubectl apply -f kubernetes/ingress.yaml

# Wait for rollout
kubectl rollout status deployment/onlinebookstore -n onlinebookstore
```

## Configuration Management

### Environment Variables

The application uses the following environment variables:

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_HOST` | Database host | localhost | Yes |
| `DB_PORT` | Database port | 3306 | Yes |
| `DB_USERNAME` | Database username | root | Yes |
| `DB_PASSWORD` | Database password | root | Yes |
| `JAVA_OPTS` | JVM options | `-Xmx512m -Xms256m` | No |

### Kubernetes Secrets

Database credentials are stored as Kubernetes secrets:

```bash
# Create secret manually
kubectl create secret generic onlinebookstore-db-secret \
  --from-literal=username=myuser \
  --from-literal=password=mypassword \
  -n onlinebookstore

# View secret
kubectl get secret onlinebookstore-db-secret -n onlinebookstore -o yaml
```

### ConfigMaps (Optional)

For non-sensitive configuration:

```bash
kubectl create configmap onlinebookstore-config \
  --from-literal=db.host=mysql.example.com \
  --from-literal=db.port=3306 \
  -n onlinebookstore
```

## Monitoring and Logging

### Application Logs

```bash
# Stream logs from all pods
kubectl logs -f deployment/onlinebookstore -n onlinebookstore

# View logs from specific pod
kubectl logs <pod-name> -n onlinebookstore

# View previous container logs (after crash)
kubectl logs <pod-name> -n onlinebookstore --previous
```

### Health Checks

The application exposes a health check endpoint:

```bash
# From within cluster
kubectl exec -it <pod-name> -n onlinebookstore -- wget -O- http://localhost:8080/health

# From ingress (replace with actual hostname)
curl http://<ingress-hostname>/health
```

### Resource Monitoring

```bash
# Pod resource usage
kubectl top pods -n onlinebookstore

# Node resource usage
kubectl top nodes

# Describe pod for detailed info
kubectl describe pod <pod-name> -n onlinebookstore
```

## Troubleshooting

### Common Issues

#### 1. Pods Not Starting

**Symptoms**: Pods stuck in `Pending`, `CrashLoopBackOff`, or `ImagePullBackOff`

**Diagnosis**:

```bash
# Check pod status
kubectl get pods -n onlinebookstore

# Describe pod for events
kubectl describe pod <pod-name> -n onlinebookstore

# Check logs
kubectl logs <pod-name> -n onlinebookstore
```

**Solutions**:

- **ImagePullBackOff**: Verify image URI is correct and ECR permissions are configured
- **CrashLoopBackOff**: Check application logs for startup errors
- **Pending**: Check node resources and ensure nodes are available

#### 2. Database Connection Failures

**Symptoms**: Application logs show database connection errors

**Diagnosis**:

```bash
# Check database environment variables
kubectl get deployment onlinebookstore -n onlinebookstore -o yaml | grep -A 10 env:

# Check secret exists
kubectl get secret onlinebookstore-db-secret -n onlinebookstore
```

**Solutions**:

- Verify database host is accessible from EKS
- Check security groups allow traffic on database port
- Verify database credentials are correct
- Test connectivity from pod:

```bash
kubectl exec -it <pod-name> -n onlinebookstore -- /bin/sh
# Inside pod
telnet <db-host> 3306
```

#### 3. Ingress Not Working

**Symptoms**: Cannot access application via ingress hostname

**Diagnosis**:

```bash
# Check ingress status
kubectl get ingress -n onlinebookstore

# Describe ingress for events
kubectl describe ingress onlinebookstore-ingress -n onlinebookstore

# Check AWS Load Balancer Controller logs
kubectl logs -n kube-system deployment/aws-load-balancer-controller
```

**Solutions**:

- Ensure AWS Load Balancer Controller is installed
- Verify IAM permissions for load balancer creation
- Check ingress annotations are correct
- Verify security groups allow inbound traffic on port 80

#### 4. High Memory Usage

**Symptoms**: Pods being OOMKilled or high memory consumption

**Diagnosis**:

```bash
# Check resource usage
kubectl top pods -n onlinebookstore

# Check pod events
kubectl get events -n onlinebookstore --sort-by='.lastTimestamp'
```

**Solutions**:

- Adjust `JAVA_OPTS` heap settings in deployment.yaml
- Increase memory limits in resource section
- Review application for memory leaks

### Debug Commands

```bash
# Get all resources in namespace
kubectl get all -n onlinebookstore

# Get events sorted by time
kubectl get events -n onlinebookstore --sort-by='.lastTimestamp'

# Execute shell in pod
kubectl exec -it <pod-name> -n onlinebookstore -- /bin/sh

# Port forward to local machine
kubectl port-forward deployment/onlinebookstore 8080:8080 -n onlinebookstore

# View deployment rollout history
kubectl rollout history deployment/onlinebookstore -n onlinebookstore
```

## Security Considerations

### Application Security

1. **Non-Root User**: Application runs as non-root user (UID 1001)
2. **Read-Only Filesystem**: Consider adding read-only root filesystem
3. **Security Context**: Pod security context configured with restricted permissions

### Database Security

1. **Use Secrets**: Always store database credentials in Kubernetes secrets
2. **Encrypted Connections**: Enable SSL/TLS for database connections
3. **Network Policies**: Restrict database access to application pods only

### Network Security

1. **Security Groups**: Configure AWS security groups to allow only necessary traffic
2. **Network Policies**: Implement Kubernetes network policies for pod-to-pod communication
3. **TLS Termination**: Configure HTTPS on ingress/load balancer

### Image Security

1. **Scan Images**: Use ECR image scanning for vulnerabilities
2. **Minimal Base Images**: Use minimal base images (Alpine-based)
3. **Regular Updates**: Keep base images and dependencies updated

### Best Practices

```bash
# Enable ECR image scanning
aws ecr put-image-scanning-configuration \
  --repository-name onlinebookstore \
  --image-scanning-configuration scanOnPush=true \
  --region <region>

# Create network policy (example)
kubectl apply -f - <<EOF
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: onlinebookstore-netpol
  namespace: onlinebookstore
spec:
  podSelector:
    matchLabels:
      app: onlinebookstore
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          name: kube-system
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - podSelector: {}
    ports:
    - protocol: TCP
      port: 3306
EOF
```

## Rollback Procedures

### Kubernetes Rollback

```bash
# View rollout history
kubectl rollout history deployment/onlinebookstore -n onlinebookstore

# Rollback to previous version
kubectl rollout undo deployment/onlinebookstore -n onlinebookstore

# Rollback to specific revision
kubectl rollout undo deployment/onlinebookstore -n onlinebookstore --to-revision=2

# Check rollback status
kubectl rollout status deployment/onlinebookstore -n onlinebookstore
```

### Manual Rollback

```bash
# Update deployment with previous image
kubectl set image deployment/onlinebookstore \
  onlinebookstore=<previous-image-uri> \
  -n onlinebookstore

# Wait for rollout
kubectl rollout status deployment/onlinebookstore -n onlinebookstore
```

## Scaling

### Manual Scaling

```bash
# Scale to 3 replicas
kubectl scale deployment/onlinebookstore --replicas=3 -n onlinebookstore

# Verify scaling
kubectl get pods -n onlinebookstore
```

### Horizontal Pod Autoscaling (HPA)

```bash
# Create HPA
kubectl autoscale deployment onlinebookstore \
  --cpu-percent=70 \
  --min=2 \
  --max=10 \
  -n onlinebookstore

# Check HPA status
kubectl get hpa -n onlinebookstore

# Describe HPA
kubectl describe hpa onlinebookstore -n onlinebookstore
```

## Additional Resources

- [AWS EKS Documentation](https://docs.aws.amazon.com/eks/)
- [Kubernetes Documentation](https://kubernetes.io/docs/home/)
- [Docker Documentation](https://docs.docker.com/)
- [AWS Load Balancer Controller](https://kubernetes-sigs.github.io/aws-load-balancer-controller/)

## Support

For issues and questions:

1. Check application logs
2. Review Kubernetes events
3. Consult troubleshooting section
4. Contact DevOps team

---

**Document Version**: 1.0  
**Last Updated**: 2025-01-14  
**Maintained By**: DevOps Team