#!/bin/bash
set -e
set -o pipefail

echo "====================================="
echo "AWS EKS Deployment Script"
echo "====================================="
echo ""

# Prompt for AWS EKS configuration
read -p "Enter AWS Region (e.g., us-east-1): " AWS_REGION
if [ -z "$AWS_REGION" ]; then
    echo "ERROR: AWS Region is required"
    exit 1
fi

read -p "Enter EKS Cluster Name: " CLUSTER_NAME
if [ -z "$CLUSTER_NAME" ]; then
    echo "ERROR: EKS Cluster Name is required"
    exit 1
fi

read -p "Enter Docker Image URI (full path with tag): " IMAGE_URI
if [ -z "$IMAGE_URI" ]; then
    echo "ERROR: Docker Image URI is required"
    exit 1
fi

# Prompt for environment variables
echo ""
echo "--- Application Configuration ---"
read -p "Enter Database Host (e.g., mysql.example.com): " DB_HOST
DB_HOST=${DB_HOST:-localhost}

read -p "Enter Database Port (default: 3306): " DB_PORT
DB_PORT=${DB_PORT:-3306}

read -p "Enter Database Username: " DB_USERNAME
DB_USERNAME=${DB_USERNAME:-root}

read -sp "Enter Database Password: " DB_PASSWORD
DB_PASSWORD=${DB_PASSWORD:-root}
echo ""

echo ""
echo "====================================="
echo "Configuring kubectl for EKS"
echo "====================================="
echo ""

# Configure kubectl
aws eks update-kubeconfig --region "$AWS_REGION" --name "$CLUSTER_NAME"

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to configure kubectl for EKS cluster"
    exit 1
fi

# Verify cluster connectivity
echo ""
echo "Verifying cluster connectivity..."
kubectl cluster-info

if [ $? -ne 0 ]; then
    echo "ERROR: Unable to connect to EKS cluster"
    exit 1
fi

echo ""
echo "====================================="
echo "Updating Kubernetes Manifests"
echo "====================================="
echo ""

# Update manifests with provided values
cd kubernetes

echo "Updating deployment.yaml with image URI..."
sed -i 's|{{IMAGE_URI}}|'"$IMAGE_URI"'|g' deployment.yaml
sed -i 's|{{DB_HOST}}|'"$DB_HOST"'|g' deployment.yaml
sed -i 's|{{DB_PORT}}|'"$DB_PORT"'|g' deployment.yaml
sed -i 's|{{DB_USERNAME}}|'"$DB_USERNAME"'|g' deployment.yaml
sed -i 's|{{DB_PASSWORD}}|'"$DB_PASSWORD"'|g' deployment.yaml

cd ..

echo ""
echo "====================================="
echo "Applying Kubernetes Manifests"
echo "====================================="
echo ""

# Apply manifests in order
echo "Creating namespace..."
kubectl apply -f kubernetes/namespace.yaml

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to create namespace"
    exit 1
fi

echo ""
echo "Creating deployment..."
kubectl apply -f kubernetes/deployment.yaml

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to create deployment"
    exit 1
fi

echo ""
echo "Creating service..."
kubectl apply -f kubernetes/service.yaml

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to create service"
    exit 1
fi

echo ""
echo "Creating ingress..."
kubectl apply -f kubernetes/ingress.yaml

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to create ingress"
    exit 1
fi

echo ""
echo "====================================="
echo "Waiting for Deployment Rollout"
echo "====================================="
echo ""

kubectl rollout status deployment/onlinebookstore -n onlinebookstore --timeout=5m

if [ $? -ne 0 ]; then
    echo "ERROR: Deployment rollout failed or timed out"
    echo ""
    echo "Checking pod status..."
    kubectl get pods -n onlinebookstore
    echo ""
    echo "Recent events:"
    kubectl get events -n onlinebookstore --sort-by='.lastTimestamp' | tail -20
    exit 1
fi

echo ""
echo "====================================="
echo "Deployment Verification"
echo "====================================="
echo ""

echo "Pods:"
kubectl get pods -n onlinebookstore

echo ""
echo "Services:"
kubectl get svc -n onlinebookstore

echo ""
echo "Ingress:"
kubectl get ingress -n onlinebookstore

echo ""
echo "====================================="
echo "SUCCESS!"
echo "====================================="
echo ""
echo "Application deployed to AWS EKS cluster: $CLUSTER_NAME"
echo ""
echo "To get the application URL, run:"
echo "kubectl get ingress onlinebookstore-ingress -n onlinebookstore -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'"
echo ""
echo "To view logs:"
echo "kubectl logs -f deployment/onlinebookstore -n onlinebookstore"
echo ""
echo "To rollback if needed:"
echo "kubectl rollout undo deployment/onlinebookstore -n onlinebookstore"
echo ""