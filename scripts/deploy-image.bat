@echo off
setlocal enabledelayedexpansion

echo =====================================
echo AWS EKS Deployment Script
echo =====================================
echo.

set /p "AWS_REGION=Enter AWS Region (e.g., us-east-1): "
if "!AWS_REGION!"=="" (
    echo ERROR: AWS Region is required
    exit /b 1
)

set /p "CLUSTER_NAME=Enter EKS Cluster Name: "
if "!CLUSTER_NAME!"=="" (
    echo ERROR: EKS Cluster Name is required
    exit /b 1
)

set /p "IMAGE_URI=Enter Docker Image URI (full path with tag): "
if "!IMAGE_URI!"=="" (
    echo ERROR: Docker Image URI is required
    exit /b 1
)

echo.
echo --- Application Configuration ---
set /p "DB_HOST=Enter Database Host (e.g., mysql.example.com): "
if "!DB_HOST!"=="" set DB_HOST=localhost

set /p "DB_PORT=Enter Database Port (default: 3306): "
if "!DB_PORT!"=="" set DB_PORT=3306

set /p "DB_USERNAME=Enter Database Username: "
if "!DB_USERNAME!"=="" set DB_USERNAME=root

set /p "DB_PASSWORD=Enter Database Password: "
if "!DB_PASSWORD!"=="" set DB_PASSWORD=root

echo.
echo =====================================
echo Configuring kubectl for EKS
echo =====================================
echo.

aws eks update-kubeconfig --region !AWS_REGION! --name !CLUSTER_NAME!

if !ERRORLEVEL! neq 0 (
    echo ERROR: Failed to configure kubectl for EKS cluster
    exit /b 1
)

echo.
echo Verifying cluster connectivity...
kubectl cluster-info

if !ERRORLEVEL! neq 0 (
    echo ERROR: Unable to connect to EKS cluster
    exit /b 1
)

echo.
echo =====================================
echo Updating Kubernetes Manifests
echo =====================================
echo.

cd kubernetes

echo Updating deployment.yaml with image URI...
powershell -Command "(Get-Content deployment.yaml) -replace '{{IMAGE_URI}}', '%IMAGE_URI%' | Set-Content deployment.yaml"
powershell -Command "(Get-Content deployment.yaml) -replace '{{DB_HOST}}', '%DB_HOST%' | Set-Content deployment.yaml"
powershell -Command "(Get-Content deployment.yaml) -replace '{{DB_PORT}}', '%DB_PORT%' | Set-Content deployment.yaml"
powershell -Command "(Get-Content deployment.yaml) -replace '{{DB_USERNAME}}', '%DB_USERNAME%' | Set-Content deployment.yaml"
powershell -Command "(Get-Content deployment.yaml) -replace '{{DB_PASSWORD}}', '%DB_PASSWORD%' | Set-Content deployment.yaml"

cd ..

echo.
echo =====================================
echo Applying Kubernetes Manifests
echo =====================================
echo.

echo Creating namespace...
kubectl apply -f kubernetes\namespace.yaml

if !ERRORLEVEL! neq 0 (
    echo ERROR: Failed to create namespace
    exit /b 1
)

echo.
echo Creating deployment...
kubectl apply -f kubernetes\deployment.yaml

if !ERRORLEVEL! neq 0 (
    echo ERROR: Failed to create deployment
    exit /b 1
)

echo.
echo Creating service...
kubectl apply -f kubernetes\service.yaml

if !ERRORLEVEL! neq 0 (
    echo ERROR: Failed to create service
    exit /b 1
)

echo.
echo Creating ingress...
kubectl apply -f kubernetes\ingress.yaml

if !ERRORLEVEL! neq 0 (
    echo ERROR: Failed to create ingress
    exit /b 1
)

echo.
echo =====================================
echo Waiting for Deployment Rollout
echo =====================================
echo.

kubectl rollout status deployment/onlinebookstore -n onlinebookstore --timeout=5m

if !ERRORLEVEL! neq 0 (
    echo ERROR: Deployment rollout failed or timed out
    echo.
    echo Checking pod status...
    kubectl get pods -n onlinebookstore
    echo.
    echo Recent events:
    kubectl get events -n onlinebookstore --sort-by=.lastTimestamp
    exit /b 1
)

echo.
echo =====================================
echo Deployment Verification
echo =====================================
echo.

echo Pods:
kubectl get pods -n onlinebookstore

echo.
echo Services:
kubectl get svc -n onlinebookstore

echo.
echo Ingress:
kubectl get ingress -n onlinebookstore

echo.
echo =====================================
echo SUCCESS!
echo =====================================
echo.
echo Application deployed to AWS EKS cluster: !CLUSTER_NAME!
echo.
echo To get the application URL, run:
echo kubectl get ingress onlinebookstore-ingress -n onlinebookstore -o jsonpath="{.status.loadBalancer.ingress[0].hostname}"
echo.
echo To view logs:
echo kubectl logs -f deployment/onlinebookstore -n onlinebookstore
echo.
echo To rollback if needed:
echo kubectl rollout undo deployment/onlinebookstore -n onlinebookstore
echo.

endlocal