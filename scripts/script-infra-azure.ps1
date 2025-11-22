# Script de provisionamento de infraestrutura no Azure (PowerShell)
# Cria Resource Group, PostgreSQL, Web App e outros recursos necessários

# Variáveis de ambiente (configure conforme necessário)
$RESOURCE_GROUP = "rg-aprenda-plus"
$LOCATION = "brazilsouth"
$APP_NAME = "aprenda-plus"
$DB_SERVER_NAME = "aprenda-db-server"
$DB_NAME = "aprenda_db"
$DB_ADMIN_USER = "aprenda_admin"
$SKU_NAME = "B_Gen5_1"  # Basic tier, Gen5, 1 vCore
$STORAGE_SIZE = "51200"  # 50GB

Write-Host "=== Provisionando infraestrutura no Azure ===" -ForegroundColor Green

# Login no Azure (descomente se necessário)
# az login

# Criar Resource Group
Write-Host "Criando Resource Group..." -ForegroundColor Yellow
az group create `
    --name $RESOURCE_GROUP `
    --location $LOCATION

# Criar PostgreSQL Server
Write-Host "Criando PostgreSQL Server..." -ForegroundColor Yellow
$DB_PASSWORD = -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 32 | ForEach-Object { [char]$_ })

az postgres flexible-server create `
    --resource-group $RESOURCE_GROUP `
    --name $DB_SERVER_NAME `
    --location $LOCATION `
    --admin-user $DB_ADMIN_USER `
    --admin-password $DB_PASSWORD `
    --sku-name $SKU_NAME `
    --tier Burstable `
    --storage-size $STORAGE_SIZE `
    --version 14 `
    --public-access 0.0.0.0

# Criar Database
Write-Host "Criando Database..." -ForegroundColor Yellow
az postgres flexible-server db create `
    --resource-group $RESOURCE_GROUP `
    --server-name $DB_SERVER_NAME `
    --database-name $DB_NAME

# Criar App Service Plan
Write-Host "Criando App Service Plan..." -ForegroundColor Yellow
az appservice plan create `
    --name "${APP_NAME}-plan" `
    --resource-group $RESOURCE_GROUP `
    --location $LOCATION `
    --sku B1 `
    --is-linux

# Criar Web App
Write-Host "Criando Web App..." -ForegroundColor Yellow
az webapp create `
    --resource-group $RESOURCE_GROUP `
    --plan "${APP_NAME}-plan" `
    --name $APP_NAME `
    --runtime "JAVA:17-java17"

# Configurar variáveis de ambiente na Web App
Write-Host "Configurando variáveis de ambiente..." -ForegroundColor Yellow
az webapp config appsettings set `
    --resource-group $RESOURCE_GROUP `
    --name $APP_NAME `
    --settings `
    SPRING_PROFILES_ACTIVE=prod `
    DB_URL="jdbc:postgresql://${DB_SERVER_NAME}.postgres.database.azure.com:5432/${DB_NAME}?sslmode=require" `
    DB_USERNAME="${DB_ADMIN_USER}@${DB_SERVER_NAME}" `
    DB_PASSWORD="$DB_PASSWORD"

Write-Host "=== Infraestrutura provisionada com sucesso! ===" -ForegroundColor Green
Write-Host "Resource Group: $RESOURCE_GROUP"
Write-Host "Web App: $APP_NAME"
Write-Host "Database: $DB_NAME"









