=== scripts/deploy-image.sh ===
#!/bin/bash

# Enable strict error handling
set -e
set -o pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}=== AWS ECS Fargate Deployment Script ===${NC}"
echo ""

# Prompt for AWS configuration
read -p "Enter AWS Region (e.g., us-east-1): " AWS_REGION
export AWS_DEFAULT_REGION="$AWS_REGION"

read -p "Enter ECS Cluster Name: " CLUSTER_NAME
read -p "Enter VPC ID: " VPC_ID
read -p "Enter Subnet IDs (comma-separated, at least 2): " SUBNETS_INPUT
read -p "Enter Security Group ID: " SECURITY_GROUP
read -p "Enter ECR Image URI (e.g., 123456789.dkr.ecr.us-east-1.amazonaws.com/app:latest): " IMAGE_URI

# Parse subnets
IFS=',' read -ra SUBNETS_ARRAY <<< "$SUBNETS_INPUT"
SUBNET_1=$(echo "${SUBNETS_ARRAY[0]}" | xargs)
SUBNET_2=$(echo "${SUBNETS_ARRAY[1]}" | xargs)

if [ -z "$SUBNET_1" ] || [ -z "$SUBNET_2" ]; then
    echo -e "${RED}Error: At least 2 subnets are required for high availability${NC}"
    exit 1
fi

# Get AWS Account ID
echo -e "${YELLOW}Getting AWS Account ID...${NC}"
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo -e "${GREEN}AWS Account ID: $ACCOUNT_ID${NC}"

# Prompt for database configuration
echo -e "${YELLOW}Database Configuration:${NC}"
read -p "Enter Database Host: " DB_HOST
read -p "Enter Database Port [3306]: " DB_PORT
DB_PORT=${DB_PORT:-3306}
read -p "Enter Database Name [onlinebookstore]: " DB_NAME
DB_NAME=${DB_NAME:-onlinebookstore}
read -p "Enter Database Username: " DB_USER_NAME
read -sp "Enter Database Password: " DB_PASSWORD
echo ""

# Check if cluster exists
echo -e "${YELLOW}Checking if ECS cluster exists...${NC}"
aws ecs describe-clusters --clusters "$CLUSTER_NAME" --region "$AWS_REGION" >/dev/null 2>&1 || {
    echo -e "${YELLOW}Cluster does not exist. Creating ECS cluster...${NC}"
    aws ecs create-cluster --cluster-name "$CLUSTER_NAME" --region "$AWS_REGION"
    echo -e "${GREEN}ECS cluster created successfully${NC}"
}

# Create CloudWatch log group
echo -e "${YELLOW}Creating CloudWatch log group...${NC}"
aws logs create-log-group --log-group-name "/ecs/onlinebookstore" --region "$AWS_REGION" 2>/dev/null || echo "Log group already exists"

# Prompt for load balancer
read -p "Do you need a load balancer for this service? (y/n): " NEED_LB

if [ "$NEED_LB" = "y" ] || [ "$NEED_LB" = "Y" ]; then
    echo -e "${YELLOW}Creating Application Load Balancer...${NC}"
    
    # Create ALB
    ALB_NAME="onlinebookstore-alb"
    ALB_ARN=$(aws elbv2 create-load-balancer \
        --name "$ALB_NAME" \
        --subnets "$SUBNET_1" "$SUBNET_2" \
        --security-groups "$SECURITY_GROUP" \
        --scheme internet-facing \
        --type application \
        --ip-address-type ipv4 \
        --region "$AWS_REGION" \
        --query 'LoadBalancers[0].LoadBalancerArn' \
        --output text 2>/dev/null || aws elbv2 describe-load-balancers --names "$ALB_NAME" --region "$AWS_REGION" --query 'LoadBalancers[0].LoadBalancerArn' --output text)
    
    echo -e "${GREEN}ALB ARN: $ALB_ARN${NC}"
    
    # Create Target Group with target-type ip (required for Fargate)
    TG_NAME="onlinebookstore-tg"
    TARGET_GROUP_ARN=$(aws elbv2 create-target-group \
        --name "$TG_NAME" \
        --protocol HTTP \
        --port 8080 \
        --vpc-id "$VPC_ID" \
        --target-type ip \
        --health-check-enabled \
        --health-check-protocol HTTP \
        --health-check-path /health \
        --health-check-interval-seconds 30 \
        --health-check-timeout-seconds 5 \
        --healthy-threshold-count 2 \
        --unhealthy-threshold-count 3 \
        --region "$AWS_REGION" \
        --query 'TargetGroups[0].TargetGroupArn' \
        --output text 2>/dev/null || aws elbv2 describe-target-groups --names "$TG_NAME" --region "$AWS_REGION" --query 'TargetGroups[0].TargetGroupArn' --output text)
    
    echo -e "${GREEN}Target Group ARN: $TARGET_GROUP_ARN${NC}"
    
    # Create ALB Listener
    aws elbv2 create-listener \
        --load-balancer-arn "$ALB_ARN" \
        --protocol HTTP \
        --port 80 \
        --default-actions Type=forward,TargetGroupArn="$TARGET_GROUP_ARN" \
        --region "$AWS_REGION" 2>/dev/null || echo "Listener already exists"
    
    # Get ALB DNS name
    ALB_DNS=$(aws elbv2 describe-load-balancers --load-balancer-arns "$ALB_ARN" --region "$AWS_REGION" --query 'LoadBalancers[0].DNSName' --output text)
    echo -e "${GREEN}ALB DNS Name: $ALB_DNS${NC}"
    
    USE_LB=true
else
    USE_LB=false
fi

# Replace placeholders in task definition
echo -e "${YELLOW}Preparing ECS task definition...${NC}"
cp ecs/task-definition.json ecs/task-definition-temp.json

sed -i "s|{{IMAGE_URI}}|$IMAGE_URI|g" ecs/task-definition-temp.json
sed -i "s|{{AWS_REGION}}|$AWS_REGION|g" ecs/task-definition-temp.json
sed -i "s|{{ACCOUNT_ID}}|$ACCOUNT_ID|g" ecs/task-definition-temp.json
sed -i "s|{{DB_HOST}}|$DB_HOST|g" ecs/task-definition-temp.json
sed -i "s|{{DB_PORT}}|$DB_PORT|g" ecs/task-definition-temp.json
sed -i "s|{{DB_NAME}}|$DB_NAME|g" ecs/task-definition-temp.json
sed -i "s|{{DB_USER_NAME}}|$DB_USER_NAME|g" ecs/task-definition-temp.json
sed -i "s|{{DB_PASSWORD}}|$DB_PASSWORD|g" ecs/task-definition-temp.json

# Register task definition
echo -e "${YELLOW}Registering ECS task definition...${NC}"
TASK_DEF_ARN=$(aws ecs register-task-definition \
    --cli-input-json file://ecs/task-definition-temp.json \
    --region "$AWS_REGION" \
    --query 'taskDefinition.taskDefinitionArn' \
    --output text)

echo -e "${GREEN}Task Definition ARN: $TASK_DEF_ARN${NC}"

# Prepare service definition
echo -e "${YELLOW}Preparing ECS service definition...${NC}"
cp ecs/service-definition.json ecs/service-definition-temp.json

sed -i "s|{{CLUSTER_NAME}}|$CLUSTER_NAME|g" ecs/service-definition-temp.json
sed -i "s|{{SUBNET_1}}|$SUBNET_1|g" ecs/service-definition-temp.json
sed -i "s|{{SUBNET_2}}|$SUBNET_2|g" ecs/service-definition-temp.json
sed -i "s|{{SECURITY_GROUP}}|$SECURITY_GROUP|g" ecs/service-definition-temp.json

if [ "$USE_LB" = false ]; then
    # Remove loadBalancers and healthCheckGracePeriodSeconds from service definition
    jq 'del(.loadBalancers, .healthCheckGracePeriodSeconds)' ecs/service-definition-temp.json > ecs/service-definition-temp2.json
    mv ecs/service-definition-temp2.json ecs/service-definition-temp.json
else
    sed -i "s|{{TARGET_GROUP_ARN}}|$TARGET_GROUP_ARN|g" ecs/service-definition-temp.json
fi

# Check if service exists
SERVICE_NAME="onlinebookstore-service"
EXISTING_SERVICE=$(aws ecs describe-services \
    --cluster "$CLUSTER_NAME" \
    --services "$SERVICE_NAME" \
    --region "$AWS_REGION" \
    --query 'services[?status==`ACTIVE`].serviceName' \
    --output text)

if [ -z "$EXISTING_SERVICE" ] || [ "$EXISTING_SERVICE" = "None" ]; then
    echo -e "${YELLOW}Creating new ECS service...${NC}"
    aws ecs create-service \
        --cli-input-json file://ecs/service-definition-temp.json \
        --region "$AWS_REGION"
    echo -e "${GREEN}ECS service created successfully${NC}"
else
    echo -e "${YELLOW}Updating existing ECS service...${NC}"
    aws ecs update-service \
        --cluster "$CLUSTER_NAME" \
        --service "$SERVICE_NAME" \
        --task-definition "$TASK_DEF_ARN" \
        --region "$AWS_REGION"
    echo -e "${GREEN}ECS service updated successfully${NC}"
fi

# Wait for service stability
echo -e "${YELLOW}Waiting for service to become stable (this may take several minutes)...${NC}"
aws ecs wait services-stable \
    --cluster "$CLUSTER_NAME" \
    --services "$SERVICE_NAME" \
    --region "$AWS_REGION"

echo -e "${GREEN}Service is stable${NC}"

# Display service information
echo ""
echo -e "${GREEN}=== Deployment Summary ===${NC}"
echo -e "Cluster: ${CLUSTER_NAME}"
echo -e "Service: ${SERVICE_NAME}"
echo -e "Task Definition: ${TASK_DEF_ARN}"
echo -e "Region: ${AWS_REGION}"

if [ "$USE_LB" = true ]; then
    echo -e "Load Balancer DNS: ${ALB_DNS}"
    echo -e "Application URL: http://${ALB_DNS}"
fi

echo -e "CloudWatch Logs: /ecs/onlinebookstore"
echo ""
echo -e "${YELLOW}To view logs:${NC}"
echo -e "aws logs tail /ecs/onlinebookstore --follow --region ${AWS_REGION}"
echo ""
echo -e "${GREEN}Deployment completed successfully!${NC}"

# Cleanup temp files
rm -f ecs/task-definition-temp.json ecs/service-definition-temp.json
