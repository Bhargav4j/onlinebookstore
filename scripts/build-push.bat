@echo off
setlocal enabledelayedexpansion

echo =====================================
echo Docker Build and Push Script
echo =====================================
echo.

set PROJECT_NAME=onlinebookstore

echo Select Docker Registry:
echo 1. AWS ECR
echo 2. Docker Hub
set /p "REGISTRY_CHOICE=Enter choice (1 or 2): "

if "!REGISTRY_CHOICE!"=="1" (
    echo.
    echo --- AWS ECR Configuration ---
    set /p "AWS_REGION=Enter AWS Region (e.g., us-east-1): "
    set /p "AWS_ACCOUNT_ID=Enter AWS Account ID: "
    set /p "ECR_REPO=Enter ECR Repository Name (default: %PROJECT_NAME%): "
    if "!ECR_REPO!"=="" set ECR_REPO=%PROJECT_NAME%
    
    set REGISTRY_URL=!AWS_ACCOUNT_ID!.dkr.ecr.!AWS_REGION!.amazonaws.com
    
    echo.
    echo Logging into AWS ECR...
    for /f "delims=" %%i in ('aws ecr get-login-password --region !AWS_REGION!') do set ECR_PASSWORD=%%i
    echo !ECR_PASSWORD! | docker login --username AWS --password-stdin !REGISTRY_URL!
    
    if !ERRORLEVEL! neq 0 (
        echo ERROR: AWS ECR login failed
        exit /b 1
    )
    
    echo Checking if ECR repository exists...
    aws ecr describe-repositories --repository-names !ECR_REPO! --region !AWS_REGION! >nul 2>&1
    if !ERRORLEVEL! neq 0 (
        echo Creating ECR repository: !ECR_REPO!
        aws ecr create-repository --repository-name !ECR_REPO! --region !AWS_REGION! --image-scanning-configuration scanOnPush=true
        if !ERRORLEVEL! neq 0 (
            echo ERROR: Failed to create ECR repository
            exit /b 1
        )
    )
    
    set IMAGE_NAME=%PROJECT_NAME%
    set /p "IMAGE_TAG=Enter image tag (default: latest): "
    if "!IMAGE_TAG!"=="" set IMAGE_TAG=latest
    
    set FULL_IMAGE_NAME=!REGISTRY_URL!/!ECR_REPO!:!IMAGE_TAG!
    
) else if "!REGISTRY_CHOICE!"=="2" (
    echo.
    echo --- Docker Hub Configuration ---
    set /p "DOCKER_USERNAME=Enter Docker Hub Username: "
    set /p "DOCKER_PASSWORD=Enter Docker Hub Password/Token: "
    
    echo.
    echo Logging into Docker Hub...
    echo !DOCKER_PASSWORD! | docker login --username !DOCKER_USERNAME! --password-stdin
    
    if !ERRORLEVEL! neq 0 (
        echo ERROR: Docker Hub login failed
        exit /b 1
    )
    
    set IMAGE_NAME=%PROJECT_NAME%
    set /p "IMAGE_TAG=Enter image tag (default: latest): "
    if "!IMAGE_TAG!"=="" set IMAGE_TAG=latest
    
    set FULL_IMAGE_NAME=!DOCKER_USERNAME!/!IMAGE_NAME!:!IMAGE_TAG!
    
) else (
    echo ERROR: Invalid choice. Please run the script again.
    exit /b 1
)

echo.
echo =====================================
echo Building Docker Image
echo Image: !FULL_IMAGE_NAME!
echo =====================================
echo.

docker build -t "!FULL_IMAGE_NAME!" .

if !ERRORLEVEL! neq 0 (
    echo ERROR: Docker build failed
    exit /b 1
)

echo.
echo =====================================
echo Pushing Docker Image
echo =====================================
echo.

docker push "!FULL_IMAGE_NAME!"

if !ERRORLEVEL! neq 0 (
    echo ERROR: Docker push failed
    exit /b 1
)

echo.
echo =====================================
echo SUCCESS!
echo =====================================
echo Image pushed: !FULL_IMAGE_NAME!
echo.
echo Next steps:
echo 1. Update Kubernetes manifests with this image URI
echo 2. Run deploy-image.bat to deploy to EKS
echo.

endlocal