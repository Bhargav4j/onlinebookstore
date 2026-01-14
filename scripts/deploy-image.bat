=== scripts/deploy-image.bat ===
@echo off
setlocal enabledelayedexpansion

echo === AWS ECS Fargate Deployment Script ===
echo.

REM Prompt for AWS configuration
set /p AWS_REGION="Enter AWS Region (e.g., us-east-1): "
set /p CLUSTER_NAME="Enter ECS Cluster Name: "
set /p VPC_ID="Enter VPC ID: "
set /p SUBNETS_INPUT="Enter Subnet IDs (comma-separated, at least 2): "
set /p SECURITY_GROUP="Enter Security Group ID: "
set /p IMAGE_URI="Enter ECR Image URI: "

REM Parse subnets
for /f "tokens=1,2 delims=," %%a in ("!SUBNETS_INPUT!") do (
    set SUBNET_1=%%a
    set SUBNET_2=%%b
)

REM Trim spaces
set SUBNET_1=!SUBNET_1: =!
set SUBNET_2=!SUBNET_2: =!

if "!SUBNET_1!"==" " (
    echo Error: At least 2 subnets are required
    exit /b 1
)

REM Get AWS Account ID
echo Getting AWS Account ID...
for /f "delims=" %%i in ('aws sts get-caller-identity --query Account --output text') do set ACCOUNT_ID=%%i
echo AWS Account ID: !ACCOUNT_ID!

REM Prompt for database configuration
echo Database Configuration:
set /p DB_HOST="Enter Database Host: "
set /p DB_PORT="Enter Database Port [3306]: "
if "!DB_PORT!"=="" set DB_PORT=3306
set /p DB_NAME="Enter Database Name [onlinebookstore]: "
if "!DB_NAME!"=="" set DB_NAME=onlinebookstore
set /p DB_USER_NAME="Enter Database Username: "
set /p DB_PASSWORD="Enter Database Password: "

REM Check if cluster exists
echo Checking if ECS cluster exists...
aws ecs describe-clusters --clusters "!CLUSTER_NAME!" --region "!AWS_REGION!" >nul 2>&1
if !ERRORLEVEL! neq 0 (
    echo Cluster does not exist. Creating ECS cluster...
    aws ecs create-cluster --cluster-name "!CLUSTER_NAME!" --region "!AWS_REGION!"
    echo ECS cluster created successfully
)

REM Create CloudWatch log group
echo Creating CloudWatch log group...
aws logs create-log-group --log-group-name "/ecs/onlinebookstore" --region "!AWS_REGION!" 2>nul

REM Prompt for load balancer
set /p NEED_LB="Do you need a load balancer for this service? (y/n): "

if /i "!NEED_LB!"=="y" (
    echo Creating Application Load Balancer...
    
    set ALB_NAME=onlinebookstore-alb
    for /f "delims=" %%i in ('aws elbv2 create-load-balancer --name !ALB_NAME! --subnets !SUBNET_1! !SUBNET_2! --security-groups !SECURITY_GROUP! --scheme internet-facing --type application --region !AWS_REGION! --query LoadBalancers[0].LoadBalancerArn --output text 2^>nul') do set ALB_ARN=%%i
    
    if "!ALB_ARN!"=="" (
        for /f "delims=" %%i in ('aws elbv2 describe-load-balancers --names !ALB_NAME! --region !AWS_REGION! --query LoadBalancers[0].LoadBalancerArn --output text') do set ALB_ARN=%%i
    )
    
    echo ALB ARN: !ALB_ARN!
    
    set TG_NAME=onlinebookstore-tg
    for /f "delims=" %%i in ('aws elbv2 create-target-group --name !TG_NAME! --protocol HTTP --port 8080 --vpc-id !VPC_ID! --target-type ip --health-check-path /health --region !AWS_REGION! --query TargetGroups[0].TargetGroupArn --output text 2^>nul') do set TARGET_GROUP_ARN=%%i
    
    if "!TARGET_GROUP_ARN!"=="" (
        for /f "delims=" %%i in ('aws elbv2 describe-target-groups --names !TG_NAME! --region !AWS_REGION! --query TargetGroups[0].TargetGroupArn --output text') do set TARGET_GROUP_ARN=%%i
    )
    
    echo Target Group ARN: !TARGET_GROUP_ARN!
    
    aws elbv2 create-listener --load-balancer-arn !ALB_ARN! --protocol HTTP --port 80 --default-actions Type=forward,TargetGroupArn=!TARGET_GROUP_ARN! --region !AWS_REGION! 2>nul
    
    for /f "delims=" %%i in ('aws elbv2 describe-load-balancers --load-balancer-arns !ALB_ARN! --region !AWS_REGION! --query LoadBalancers[0].DNSName --output text') do set ALB_DNS=%%i
    echo ALB DNS Name: !ALB_DNS!
    
    set USE_LB=true
) else (
    set USE_LB=false
)

REM Replace placeholders in task definition
echo Preparing ECS task definition...
copy ecs\task-definition.json ecs\task-definition-temp.json >nul

powershell -Command "(Get-Content ecs\task-definition-temp.json) -replace '{{IMAGE_URI}}', '!IMAGE_URI!' | Set-Content ecs\task-definition-temp.json"
powershell -Command "(Get-Content ecs\task-definition-temp.json) -replace '{{AWS_REGION}}', '!AWS_REGION!' | Set-Content ecs\task-definition-temp.json"
powershell -Command "(Get-Content ecs\task-definition-temp.json) -replace '{{ACCOUNT_ID}}', '!ACCOUNT_ID!' | Set-Content ecs\task-definition-temp.json"
powershell -Command "(Get-Content ecs\task-definition-temp.json) -replace '{{DB_HOST}}', '!DB_HOST!' | Set-Content ecs\task-definition-temp.json"
powershell -Command "(Get-Content ecs\task-definition-temp.json) -replace '{{DB_PORT}}', '!DB_PORT!' | Set-Content ecs\task-definition-temp.json"
powershell -Command "(Get-Content ecs\task-definition-temp.json) -replace '{{DB_NAME}}', '!DB_NAME!' | Set-Content ecs\task-definition-temp.json"
powershell -Command "(Get-Content ecs\task-definition-temp.json) -replace '{{DB_USER_NAME}}', '!DB_USER_NAME!' | Set-Content ecs\task-definition-temp.json"
powershell -Command "(Get-Content ecs\task-definition-temp.json) -replace '{{DB_PASSWORD}}', '!DB_PASSWORD!' | Set-Content ecs\task-definition-temp.json"

REM Register task definition
echo Registering ECS task definition...
for /f "delims=" %%i in ('aws ecs register-task-definition --cli-input-json file://ecs/task-definition-temp.json --region !AWS_REGION! --query taskDefinition.taskDefinitionArn --output text') do set TASK_DEF_ARN=%%i
echo Task Definition ARN: !TASK_DEF_ARN!

REM Prepare service definition
echo Preparing ECS service definition...
copy ecs\service-definition.json ecs\service-definition-temp.json >nul

powershell -Command "(Get-Content ecs\service-definition-temp.json) -replace '{{CLUSTER_NAME}}', '!CLUSTER_NAME!' | Set-Content ecs\service-definition-temp.json"
powershell -Command "(Get-Content ecs\service-definition-temp.json) -replace '{{SUBNET_1}}', '!SUBNET_1!' | Set-Content ecs\service-definition-temp.json"
powershell -Command "(Get-Content ecs\service-definition-temp.json) -replace '{{SUBNET_2}}', '!SUBNET_2!' | Set-Content ecs\service-definition-temp.json"
powershell -Command "(Get-Content ecs\service-definition-temp.json) -replace '{{SECURITY_GROUP}}', '!SECURITY_GROUP!' | Set-Content ecs\service-definition-temp.json"

if "!USE_LB!"=="true" (
    powershell -Command "(Get-Content ecs\service-definition-temp.json) -replace '{{TARGET_GROUP_ARN}}', '!TARGET_GROUP_ARN!' | Set-Content ecs\service-definition-temp.json"
) else (
    powershell -Command "$json = Get-Content ecs\service-definition-temp.json | ConvertFrom-Json; $json.PSObject.Properties.Remove('loadBalancers'); $json.PSObject.Properties.Remove('healthCheckGracePeriodSeconds'); $json | ConvertTo-Json -Depth 10 | Set-Content ecs\service-definition-temp.json"
)

REM Check if service exists
set SERVICE_NAME=onlinebookstore-service
for /f "delims=" %%i in ('aws ecs describe-services --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION! --query services[?status==`ACTIVE`].serviceName --output text') do set EXISTING_SERVICE=%%i

if "!EXISTING_SERVICE!"=="" (
    echo Creating new ECS service...
    aws ecs create-service --cli-input-json file://ecs/service-definition-temp.json --region !AWS_REGION!
    echo ECS service created successfully
) else (
    echo Updating existing ECS service...
    aws ecs update-service --cluster !CLUSTER_NAME! --service !SERVICE_NAME! --task-definition !TASK_DEF_ARN! --region !AWS_REGION!
    echo ECS service updated successfully
)

REM Wait for service stability
echo Waiting for service to become stable...
aws ecs wait services-stable --cluster !CLUSTER_NAME! --services !SERVICE_NAME! --region !AWS_REGION!

echo Service is stable
echo.
echo === Deployment Summary ===
echo Cluster: !CLUSTER_NAME!
echo Service: !SERVICE_NAME!
echo Task Definition: !TASK_DEF_ARN!
echo Region: !AWS_REGION!

if "!USE_LB!"=="true" (
    echo Load Balancer DNS: !ALB_DNS!
    echo Application URL: http://!ALB_DNS!
)

echo CloudWatch Logs: /ecs/onlinebookstore
echo.
echo To view logs:
echo aws logs tail /ecs/onlinebookstore --follow --region !AWS_REGION!
echo.
echo Deployment completed successfully!

REM Cleanup temp files
del ecs\task-definition-temp.json ecs\service-definition-temp.json 2>nul

endlocal
