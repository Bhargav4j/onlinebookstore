=== scripts/build-push.bat ===
@echo off
setlocal enabledelayedexpansion

echo === Docker Build and Push Script ===
echo.

REM Project configuration
set PROJECT_NAME=onlinebookstore

REM Sanitize image name: lowercase, replace non-alphanumeric with hyphens
set IMAGE_NAME=!PROJECT_NAME!
for %%i in (A B C D E F G H I J K L M N O P Q R S T U V W X Y Z) do (
    set IMAGE_NAME=!IMAGE_NAME:%%i=%%i!
)
set IMAGE_NAME=!IMAGE_NAME: =-!
set IMAGE_NAME=!IMAGE_NAME:A=a!
set IMAGE_NAME=!IMAGE_NAME:B=b!
set IMAGE_NAME=!IMAGE_NAME:C=c!
set IMAGE_NAME=!IMAGE_NAME:D=d!
set IMAGE_NAME=!IMAGE_NAME:E=e!
set IMAGE_NAME=!IMAGE_NAME:F=f!
set IMAGE_NAME=!IMAGE_NAME:G=g!
set IMAGE_NAME=!IMAGE_NAME:H=h!
set IMAGE_NAME=!IMAGE_NAME:I=i!
set IMAGE_NAME=!IMAGE_NAME:J=j!
set IMAGE_NAME=!IMAGE_NAME:K=k!
set IMAGE_NAME=!IMAGE_NAME:L=l!
set IMAGE_NAME=!IMAGE_NAME:M=m!
set IMAGE_NAME=!IMAGE_NAME:N=n!
set IMAGE_NAME=!IMAGE_NAME:O=o!
set IMAGE_NAME=!IMAGE_NAME:P=p!
set IMAGE_NAME=!IMAGE_NAME:Q=q!
set IMAGE_NAME=!IMAGE_NAME:R=r!
set IMAGE_NAME=!IMAGE_NAME:S=s!
set IMAGE_NAME=!IMAGE_NAME:T=t!
set IMAGE_NAME=!IMAGE_NAME:U=u!
set IMAGE_NAME=!IMAGE_NAME:V=v!
set IMAGE_NAME=!IMAGE_NAME:W=w!
set IMAGE_NAME=!IMAGE_NAME:X=x!
set IMAGE_NAME=!IMAGE_NAME:Y=y!
set IMAGE_NAME=!IMAGE_NAME:Z=z!

echo Select registry type:
echo 1. AWS ECR (Elastic Container Registry)
echo 2. Docker Hub
set /p REGISTRY_CHOICE="Enter choice (1 or 2): "

if "!REGISTRY_CHOICE!"=="1" (
    echo Selected: AWS ECR
    
    REM Prompt for AWS details
    set /p AWS_REGION="Enter AWS Region (e.g., us-east-1): "
    set /p AWS_ACCOUNT_ID="Enter AWS Account ID: "
    set /p ECR_REPO="Enter ECR Repository Name [!IMAGE_NAME!]: "
    if "!ECR_REPO!"=="" set ECR_REPO=!IMAGE_NAME!
    
    REM Prompt for image tag
    set /p IMAGE_TAG="Enter image tag [latest]: "
    if "!IMAGE_TAG!"=="" set IMAGE_TAG=latest
    
    set REGISTRY_URL=!AWS_ACCOUNT_ID!.dkr.ecr.!AWS_REGION!.amazonaws.com
    set FULL_IMAGE_NAME=!REGISTRY_URL!/!ECR_REPO!:!IMAGE_TAG!
    
    echo Authenticating with AWS ECR...
    aws ecr get-login-password --region !AWS_REGION! | docker login --username AWS --password-stdin !REGISTRY_URL!
    
    if !ERRORLEVEL! neq 0 (
        echo Failed to authenticate with AWS ECR
        exit /b 1
    )
    
    echo Checking if ECR repository exists...
    aws ecr describe-repositories --repository-names !ECR_REPO! --region !AWS_REGION! >nul 2>&1
    if !ERRORLEVEL! neq 0 (
        echo Repository does not exist. Creating ECR repository...
        aws ecr create-repository --repository-name !ECR_REPO! --region !AWS_REGION!
        echo ECR repository created successfully
    )
    
) else if "!REGISTRY_CHOICE!"=="2" (
    echo Selected: Docker Hub
    
    REM Prompt for Docker Hub details
    set /p DOCKER_USERNAME="Enter Docker Hub username: "
    set /p DOCKER_PASSWORD="Enter Docker Hub password or access token: "
    
    REM Prompt for image tag
    set /p IMAGE_TAG="Enter image tag [latest]: "
    if "!IMAGE_TAG!"=="" set IMAGE_TAG=latest
    
    set FULL_IMAGE_NAME=!DOCKER_USERNAME!/!IMAGE_NAME!:!IMAGE_TAG!
    
    echo Authenticating with Docker Hub...
    echo !DOCKER_PASSWORD! | docker login --username !DOCKER_USERNAME! --password-stdin
    
    if !ERRORLEVEL! neq 0 (
        echo Failed to authenticate with Docker Hub
        exit /b 1
    )
    
) else (
    echo Invalid choice. Exiting.
    exit /b 1
)

echo.
echo Building Docker image: !FULL_IMAGE_NAME!
docker build -t "!FULL_IMAGE_NAME!" .

if !ERRORLEVEL! neq 0 (
    echo Docker build failed
    exit /b 1
)

echo Docker build completed successfully
echo.

echo Pushing image to registry: !FULL_IMAGE_NAME!
docker push "!FULL_IMAGE_NAME!"

if !ERRORLEVEL! neq 0 (
    echo Docker push failed
    exit /b 1
)

echo Image pushed successfully!
echo Image URI: !FULL_IMAGE_NAME!
echo.
echo You can now use this image URI in your ECS deployment.

endlocal
