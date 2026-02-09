#!/bin/bash

# Script de deploy para Google Cloud Run
# Uso: ./deploy-cloudrun.sh [PROJECT_ID] [REGION] [SERVICE_NAME]

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configurações padrão
PROJECT_ID=${1:-${GOOGLE_CLOUD_PROJECT}}
REGION=${2:-us-central1}
SERVICE_NAME=${3:-autoflex-backend}
IMAGE_NAME="gcr.io/${PROJECT_ID}/${SERVICE_NAME}"

# Verificar se o gcloud está instalado
if ! command -v gcloud &> /dev/null; then
    echo -e "${RED}Erro: gcloud CLI não está instalado.${NC}"
    echo "Instale em: https://cloud.google.com/sdk/docs/install"
    exit 1
fi

# Verificar se o Docker está instalado
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Erro: Docker não está instalado.${NC}"
    exit 1
fi

# Verificar se está autenticado
if ! gcloud auth list --filter=status:ACTIVE --format="value(account)" | grep -q .; then
    echo -e "${YELLOW}Aviso: Não há contas ativas. Fazendo login...${NC}"
    gcloud auth login
fi

# Verificar se o projeto está configurado
if [ -z "$PROJECT_ID" ]; then
    echo -e "${RED}Erro: PROJECT_ID não especificado.${NC}"
    echo "Uso: $0 [PROJECT_ID] [REGION] [SERVICE_NAME]"
    echo "Ou defina a variável GOOGLE_CLOUD_PROJECT"
    exit 1
fi

echo -e "${GREEN}=== Deploy do Autoflex Backend para Cloud Run ===${NC}"
echo "Projeto: $PROJECT_ID"
echo "Região: $REGION"
echo "Serviço: $SERVICE_NAME"
echo ""

# Configurar projeto
echo -e "${YELLOW}Configurando projeto...${NC}"
gcloud config set project $PROJECT_ID

# Habilitar APIs necessárias
echo -e "${YELLOW}Habilitando APIs necessárias...${NC}"
gcloud services enable cloudbuild.googleapis.com
gcloud services enable run.googleapis.com
gcloud services enable containerregistry.googleapis.com
gcloud services enable sqladmin.googleapis.com

# Build da imagem
echo -e "${YELLOW}Fazendo build da imagem Docker...${NC}"
docker build -f Dockerfile.cloudrun -t ${IMAGE_NAME}:latest .

# Autenticar Docker no GCR
echo -e "${YELLOW}Autenticando Docker no Google Container Registry...${NC}"
gcloud auth configure-docker

# Push da imagem
echo -e "${YELLOW}Enviando imagem para Container Registry...${NC}"
docker push ${IMAGE_NAME}:latest

# Verificar se o serviço já existe
if gcloud run services describe ${SERVICE_NAME} --region=${REGION} --format="value(name)" &> /dev/null; then
    echo -e "${YELLOW}Serviço já existe. Atualizando...${NC}"
else
    echo -e "${YELLOW}Criando novo serviço...${NC}"
fi

# Deploy no Cloud Run
echo -e "${YELLOW}Fazendo deploy no Cloud Run...${NC}"

# Criar arquivo temporário para variáveis de ambiente (formato YAML)
ENV_FILE=$(mktemp)
trap "rm -f $ENV_FILE" EXIT

# Adicionar variável padrão
echo "QUARKUS_PROFILE: prod" > $ENV_FILE

# Ler variáveis de ambiente do arquivo .env.cloudrun se existir
if [ -f .env.cloudrun ]; then
    echo -e "${GREEN}Carregando variáveis de ambiente de .env.cloudrun...${NC}"
    while IFS= read -r line || [ -n "$line" ]; do
        # Ignora comentários e linhas vazias
        [[ "$line" =~ ^[[:space:]]*# ]] && continue
        [[ -z "${line// }" ]] && continue
        
        # Remove espaços do início/fim
        line=$(echo "$line" | xargs)
        
        # Verifica se a linha contém =
        if [[ "$line" =~ = ]]; then
            # Separa chave e valor (usando o primeiro = como separador)
            key="${line%%=*}"
            value="${line#*=}"
            
            # Remove espaços da chave
            key=$(echo "$key" | xargs)
            
            # Remove aspas do valor se existirem (simples ou duplas)
            value=$(echo "$value" | sed 's/^["'\'']//;s/["'\'']$//' | xargs)
            
            # Ignora se a chave for QUARKUS_PROFILE (já adicionamos)
            [[ "$key" == "QUARKUS_PROFILE" ]] && continue
            
            # Ignora se chave ou valor estiverem vazios
            [[ -z "$key" ]] && continue
            [[ -z "$value" ]] && continue
            
            # Escapar caracteres especiais no valor para YAML
            # Sempre usar aspas para valores que podem ter problemas
            # Escapar aspas duplas e backslashes
            value_escaped=$(echo "$value" | sed 's/\\/\\\\/g' | sed 's/"/\\"/g')
            echo "${key}: \"${value_escaped}\"" >> $ENV_FILE
        fi
    done < .env.cloudrun
fi

# Deploy
# Aumentando memória e CPU para startup mais rápido
# Timeout de request aumentado para 300s
# Nota: Cloud Run tem timeout de startup padrão de 240s, mas não pode ser configurado via flag
# O timeout de startup é controlado automaticamente pelo Cloud Run
gcloud run deploy ${SERVICE_NAME} \
    --image ${IMAGE_NAME}:latest \
    --region ${REGION} \
    --platform managed \
    --allow-unauthenticated \
    --port 8080 \
    --memory 1Gi \
    --cpu 2 \
    --min-instances 0 \
    --max-instances 10 \
    --timeout 300 \
    --env-vars-file ${ENV_FILE}

# Garantir que o serviço está público (IAM policy)
echo -e "${YELLOW}Garantindo que o serviço está público...${NC}"
gcloud run services add-iam-policy-binding ${SERVICE_NAME} \
    --member="allUsers" \
    --role="roles/run.invoker" \
    --region=${REGION} \
    --quiet || echo -e "${YELLOW}Aviso: IAM policy pode já estar configurada.${NC}"

# Verificar IAM policy
echo -e "${YELLOW}Verificando IAM policy...${NC}"
IAM_CHECK=$(gcloud run services get-iam-policy ${SERVICE_NAME} --region=${REGION} --format="value(bindings[0].members[0])" 2>/dev/null || echo "")
if [[ "$IAM_CHECK" == "allUsers" ]]; then
    echo -e "${GREEN}✓ Serviço está público (allUsers tem acesso)${NC}"
else
    echo -e "${YELLOW}⚠ Verifique manualmente a IAM policy do serviço${NC}"
fi

# Obter URL do serviço
SERVICE_URL=$(gcloud run services describe ${SERVICE_NAME} --region=${REGION} --format="value(status.url)")

echo ""
echo -e "${GREEN}=== Deploy concluído com sucesso! ===${NC}"
echo -e "URL do serviço: ${GREEN}${SERVICE_URL}${NC}"
echo -e "Health check: ${GREEN}${SERVICE_URL}/health${NC}"
echo -e "Swagger UI: ${GREEN}${SERVICE_URL}/swagger-ui${NC}"
echo ""

# Testar health check
echo -e "${YELLOW}Testando health check...${NC}"
sleep 5
if curl -f -s "${SERVICE_URL}/health" > /dev/null; then
    echo -e "${GREEN}✓ Health check passou!${NC}"
else
    echo -e "${RED}✗ Health check falhou. Verifique os logs:${NC}"
    echo "gcloud run services logs read ${SERVICE_NAME} --region=${REGION}"
fi
