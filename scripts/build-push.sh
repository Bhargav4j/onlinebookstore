=== scripts/build-push.sh ===
#!/bin/bash

# Enable strict error handling
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Docker Build and Push Script ===${NC}"
echo ""

# Project configuration
PROJECT_NAME="onlinebookstore"

# Sanitize image name: lowercase, replace non-alphanumeric with hyphens, trim hyphens
IMAGE_NAME=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9' '-' | sed 's/^-*//;s/-*$//')

echo -e "${YELLOW}Select registry type:${NC}"
echo "1. AWS ECR (Elastic Container Registry)"
echo "2. Docker Hub"
read -p "Enter choice (1 or 2): " REGISTRY_CHOICE

if [ "$REGISTRY_CHOICE" = "1" ]; then
    echo -e "${GREEN}Selected: AWS ECR${NC}"
    
    # Prompt for AWS details
    read -p "Enter AWS Region (e.g., us-east-1): " AWS_REGION
    read -p "Enter AWS Account ID: " AWS_ACCOUNT_ID
    read -p "Enter ECR Repository Name [$IMAGE_NAME]: " ECR_REPO
    ECR_REPO=${ECR_REPO:-$IMAGE_NAME}
    
    # Prompt for image tag
    read -p "Enter image tag [latest]: " IMAGE_TAG
    IMAGE_TAG=${IMAGE_TAG:-latest}
    
    # Sanitize tag
    IMAGE_TAG=$(echo "$IMAGE_TAG" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9.-' '-' | sed 's/^-*//;s/-*$//')
    
    REGISTRY_URL="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
    FULL_IMAGE_NAME="${REGISTRY_URL}/${ECR_REPO}:${IMAGE_TAG}"
    
    echo -e "${YELLOW}Authenticating with AWS ECR...${NC}"
    aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$REGISTRY_URL"
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}Failed to authenticate with AWS ECR${NC}"
        exit 1
    fi
    
    echo -e "${YELLOW}Checking if ECR repository exists...${NC}"
    aws ecr describe-repositories --repository-names "$ECR_REPO" --region "$AWS_REGION" >/dev/null 2>&1 || {
        echo -e "${YELLOW}Repository does not exist. Creating ECR repository...${NC}"
        aws ecr create-repository --repository-name "$ECR_REPO" --region "$AWS_REGION"
        echo -e "${GREEN}ECR repository created successfully${NC}"
    }
    
elif [ "$REGISTRY_CHOICE" = "2" ]; then
    echo -e "${GREEN}Selected: Docker Hub${NC}"
    
    # Prompt for Docker Hub details
    read -p "Enter Docker Hub username: " DOCKER_USERNAME
    read -sp "Enter Docker Hub password or access token: " DOCKER_PASSWORD
    echo ""
    
    # Prompt for image tag
    read -p "Enter image tag [latest]: " IMAGE_TAG
    IMAGE_TAG=${IMAGE_TAG:-latest}
    
    # Sanitize tag
    IMAGE_TAG=$(echo "$IMAGE_TAG" | tr '[:upper:]' '[:lower:]' | tr -cs 'a-z0-9.-' '-' | sed 's/^-*//;s/-*$//')
    
    FULL_IMAGE_NAME="${DOCKER_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
    
    echo -e "${YELLOW}Authenticating with Docker Hub...${NC}"
    echo "$DOCKER_PASSWORD" | docker login --username "$DOCKER_USERNAME" --password-stdin
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}Failed to authenticate with Docker Hub${NC}"
        exit 1
    fi
    
else
    echo -e "${RED}Invalid choice. Exiting.${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}Building Docker image: ${FULL_IMAGE_NAME}${NC}"
docker build -t "$FULL_IMAGE_NAME" .

if [ $? -ne 0 ]; then
    echo -e "${RED}Docker build failed${NC}"
    exit 1
fi

echo -e "${GREEN}Docker build completed successfully${NC}"
echo ""

echo -e "${YELLOW}Pushing image to registry: ${FULL_IMAGE_NAME}${NC}"
docker push "$FULL_IMAGE_NAME"

if [ $? -ne 0 ]; then
    echo -e "${RED}Docker push failed${NC}"
    exit 1
fi

echo -e "${GREEN}Image pushed successfully!${NC}"
echo -e "${GREEN}Image URI: ${FULL_IMAGE_NAME}${NC}"
echo ""
echo -e "${YELLOW}You can now use this image URI in your ECS deployment.${NC}"
