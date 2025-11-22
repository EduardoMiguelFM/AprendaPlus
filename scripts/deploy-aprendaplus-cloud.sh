#!/bin/bash
#
# Deploy completo Aprenda+ – Azure Cloud Shell
# 1. Cria PostgreSQL Flexible Server
# 2. Provisiona App Service (Java 21)
# 3. Configura variáveis e connection strings
# 4. Faz build e deploy do JAR

set -euo pipefail

# === Cores para saída ===
BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# === Configurações (edite se necessário) ===
RESOURCE_GROUP="AprendaPlusRG"
LOCATION="brazilsouth"
POSTGRES_SERVER="aprendaplus-pg-$(date +%s | tail -c 5)"
POSTGRES_DB="aprendaplusdb"
POSTGRES_USER="aprendaplusadmin"
POSTGRES_PASSWORD="Aprenda2025!Secure#DB"
APP_PLAN="AprendaPlusPlan"
APP_NAME="aprendaplus-web-$(date +%s | tail -c 5)"
SPRING_PROFILE="cloud"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  DEPLOY COMPLETO – APRNDA+${NC}"
echo -e "${BLUE}  Azure Cloud Shell${NC}"
echo -e "${BLUE}========================================${NC}\n"

# --- Verificar login ---
echo -e "${YELLOW}Verificando autenticação...${NC}"
if ! az account show >/dev/null 2>&1; then
  echo -e "${RED}❌ Rode 'az login' antes de executar.${NC}"
  exit 1
fi
ACCOUNT=$(az account show --query name -o tsv)
echo -e "${GREEN}✅ Conta: ${ACCOUNT}${NC}\n"

# --- Resumo ---
echo -e "${YELLOW}Recursos a serem criados:${NC}"
cat <<EOF
 Resource Group : $RESOURCE_GROUP
 Location       : $LOCATION
 Postgres       : $POSTGRES_SERVER
 Database       : $POSTGRES_DB
 App Service    : $APP_NAME
 App Plan       : $APP_PLAN
EOF
echo
read -p "Confirmar deploy? (s/N): " -n 1 -r
echo
[[ $REPLY =~ ^[Ss]$ ]] || { echo "Cancelado."; exit 0; }
echo

# --- Funções auxiliares ---
info_step() { echo -e "${YELLOW}$1${NC}"; }
ok_step()   { echo -e "${GREEN}✅ $1${NC}\n"; }

# 1. Resource Group
info_step "[1/9] Grupo de recursos"
az group create --name "$RESOURCE_GROUP" --location "$LOCATION" --output none
ok_step "Resource group pronto"

# 2. PostgreSQL
info_step "[2/9] PostgreSQL Flexible Server"
az postgres flexible-server create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$POSTGRES_SERVER" \
  --location "$LOCATION" \
  --admin-user "$POSTGRES_USER" \
  --admin-password "$POSTGRES_PASSWORD" \
  --sku-name Standard_B1ms \
  --tier Burstable \
  --storage-size 32 \
  --version 16 \
  --public-access 0.0.0.0 \
  --output none
ok_step "PostgreSQL criado"

# 3. Database
info_step "[3/9] Database"
az postgres flexible-server db create \
  --resource-group "$RESOURCE_GROUP" \
  --server-name "$POSTGRES_SERVER" \
  --database-name "$POSTGRES_DB" \
  --output none
ok_step "Database criada"

# 4. Firewall
info_step "[4/9] Firewall"
az postgres flexible-server firewall-rule create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$POSTGRES_SERVER" \
  --rule-name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 0.0.0.0 \
  --output none
ok_step "Firewall liberado para Azure"

# 5. App Service Plan
info_step "[5/9] App Service Plan"
az appservice plan create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$APP_PLAN" \
  --location "$LOCATION" \
  --sku B1 \
  --is-linux \
  --output none
ok_step "Plano criado"

# 6. Web App
info_step "[6/9] Web App (Java 21)"
az webapp create \
  --resource-group "$RESOURCE_GROUP" \
  --plan "$APP_PLAN" \
  --name "$APP_NAME" \
  --runtime "JAVA:21-java21" \
  --output none
ok_step "Web App pronta"

# 7. Connection string
info_step "[7/9] Connection String"
CONNECTION_STRING="jdbc:postgresql://${POSTGRES_SERVER}.postgres.database.azure.com:5432/${POSTGRES_DB}?sslmode=require"
az webapp config connection-string set \
  --resource-group "$RESOURCE_GROUP" \
  --name "$APP_NAME" \
  --connection-string-type PostgreSQL \
  --settings POSTGRESQLCONNSTR_DefaultConnection="$CONNECTION_STRING" \
  --output none
ok_step "Connection string definida"

# 8. App settings
info_step "[8/9] App Settings"
az webapp config appsettings set \
  --resource-group "$RESOURCE_GROUP" \
  --name "$APP_NAME" \
  --settings \
    SPRING_DATASOURCE_URL="$CONNECTION_STRING" \
    SPRING_DATASOURCE_USERNAME="$POSTGRES_USER" \
    SPRING_DATASOURCE_PASSWORD="$POSTGRES_PASSWORD" \
    SPRING_PROFILES_ACTIVE="$SPRING_PROFILE" \
    SPRING_AI_OPENAI_API_KEY="${SPRING_AI_OPENAI_API_KEY:-}" \
    JAVA_OPTS="-Xms512m -Xmx1024m" \
    WEBSITES_ENABLE_APP_SERVICE_STORAGE=false \
  --output none
ok_step "Variáveis configuradas"

# 9. Build & Deploy
info_step "[9/9] Build + Deploy"
if [ ! -f "gradlew" ]; then
  echo -e "${RED}❌ Rode este script na raiz do projeto (onde está gradlew).${NC}"
  exit 1
fi
chmod +x gradlew
./gradlew clean build -x test --no-daemon
JAR_FILE=$(ls build/libs/*.jar | head -n 1)
az webapp deploy \
  --resource-group "$RESOURCE_GROUP" \
  --name "$APP_NAME" \
  --src-path "$JAR_FILE" \
  --type jar \
  --output none
ok_step "Deploy concluído"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  ✅ DEPLOY FINALIZADO COM SUCESSO${NC}"
echo -e "${GREEN}========================================${NC}\n"
echo "Resource Group : $RESOURCE_GROUP"
echo "PostgreSQL     : $POSTGRES_SERVER.postgres.database.azure.com"
echo "Database       : $POSTGRES_DB"
echo "App Service    : https://$APP_NAME.azurewebsites.net"
echo "Swagger        : https://$APP_NAME.azurewebsites.net/swagger-ui.html"
echo "Login          : https://$APP_NAME.azurewebsites.net/login"
echo ""
echo "Logs  : az webapp log tail --resource-group $RESOURCE_GROUP --name $APP_NAME"
echo "Restart: az webapp restart --resource-group $RESOURCE_GROUP --name $APP_NAME"
echo ""
echo -e "${YELLOW}Obs.: defina SPRING_AI_OPENAI_API_KEY no Cloud Shell antes de rodar.${NC}"

