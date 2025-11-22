#!/bin/bash

# Script de provisionamento de infraestrutura no Azure
# Cria Resource Group, PostgreSQL, Web App e outros recursos necessários

# Variáveis de ambiente (configure conforme necessário)
RESOURCE_GROUP="rg-aprenda-plus"
LOCATION="brazilsouth"
APP_NAME="aprenda-plus"
DB_SERVER_NAME="aprenda-db-server"
DB_NAME="aprenda_db"
DB_ADMIN_USER="aprenda_admin"
SKU_NAME="B_Gen5_1"  # Basic tier, Gen5, 1 vCore
STORAGE_SIZE="51200"  # 50GB

echo "=== Provisionando infraestrutura no Azure ==="

# Login no Azure (descomente se necessário)
# az login

# Criar Resource Group
echo "Criando Resource Group..."
az group create \
  --name $RESOURCE_GROUP \
  --location $LOCATION

# Criar PostgreSQL Server
echo "Criando PostgreSQL Server..."
az postgres flexible-server create \
  --resource-group $RESOURCE_GROUP \
  --name $DB_SERVER_NAME \
  --location $LOCATION \
  --admin-user $DB_ADMIN_USER \
  --admin-password $(openssl rand -base64 32) \
  --sku-name $SKU_NAME \
  --tier Burstable \
  --storage-size $STORAGE_SIZE \
  --version 14 \
  --public-access 0.0.0.0

# Criar Database
echo "Criando Database..."
az postgres flexible-server db create \
  --resource-group $RESOURCE_GROUP \
  --server-name $DB_SERVER_NAME \
  --database-name $DB_NAME

# Criar App Service Plan
echo "Criando App Service Plan..."
az appservice plan create \
  --name "${APP_NAME}-plan" \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --sku B1 \
  --is-linux

# Criar Web App
echo "Criando Web App..."
az webapp create \
  --resource-group $RESOURCE_GROUP \
  --plan "${APP_NAME}-plan" \
  --name $APP_NAME \
  --runtime "JAVA:17-java17"

# Configurar variáveis de ambiente na Web App
echo "Configurando variáveis de ambiente..."
DB_PASSWORD=$(az postgres flexible-server show \
  --resource-group $RESOURCE_GROUP \
  --name $DB_SERVER_NAME \
  --query administratorLoginPassword -o tsv)

az webapp config appsettings set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --settings \
    SPRING_PROFILES_ACTIVE=prod \
    DB_URL="jdbc:postgresql://${DB_SERVER_NAME}.postgres.database.azure.com:5432/${DB_NAME}?sslmode=require" \
    DB_USERNAME="${DB_ADMIN_USER}@${DB_SERVER_NAME}" \
    DB_PASSWORD="$DB_PASSWORD"

echo "=== Infraestrutura provisionada com sucesso! ==="
echo "Resource Group: $RESOURCE_GROUP"
echo "Web App: $APP_NAME"
echo "Database: $DB_NAME"








