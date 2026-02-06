# Autoflex - Sistema de Controle de Estoque e Produção

Sistema completo de gerenciamento de produtos, matérias-primas e sugestões de produção baseado em estoque disponível.

## 📋 Sobre o Projeto

O Autoflex é uma aplicação full-stack desenvolvida para gerenciar produtos, matérias-primas e calcular sugestões de produção otimizadas baseadas no estoque disponível. O sistema prioriza a produção de produtos com maior valor unitário, considerando as matérias-primas necessárias e o estoque atual.

## 🏗️ Arquitetura

O projeto é dividido em duas partes principais:

- **Frontend**: Aplicação React com TypeScript, Material-UI e Redux
- **Backend**: API REST desenvolvida com Kotlin, Quarkus e Oracle Database

```
autoflex/
├── front-end/          # Aplicação React (Vite + TypeScript)
├── backend/           # API REST (Quarkus + Kotlin)
└── README.md          # Este arquivo
```

## 🚀 Tecnologias

### Frontend
- **React 18.2** - Biblioteca UI
- **TypeScript** - Tipagem estática
- **Vite** - Build tool e dev server
- **Material-UI (MUI)** - Componentes de interface
- **Redux Toolkit** - Gerenciamento de estado
- **React Router** - Roteamento
- **Jest + Testing Library** - Testes unitários
- **Cypress** - Testes E2E

### Backend
- **Kotlin 1.9+** - Linguagem de programação
- **Quarkus 3.6.4** - Framework Java/Kotlin reativo
- **Hibernate ORM Panache** - ORM simplificado
- **Oracle Database** - Banco de dados relacional
- **RESTEasy Reactive** - REST API
- **Hibernate Validator** - Validação de dados
- **SmallRye Health** - Health checks
- **SmallRye OpenAPI** - Documentação Swagger
- **Maven** - Gerenciamento de dependências

### Infraestrutura
- **Docker & Docker Compose** - Containerização
- **Oracle Database Express Edition 21c** - Banco de dados

## 📦 Pré-requisitos

### Para desenvolvimento local:
- **Node.js 18+** e **npm**
- **Java 17+** (JDK)
- **Maven 3.9+**
- **Docker** e **Docker Compose**

### Para produção:
- **Docker** e **Docker Compose** (recomendado)
- Ou servidor com Java 17+ e Oracle Database

## 🛠️ Instalação e Execução

### Opção 1: Docker Compose (Recomendado)

Esta é a forma mais simples de executar todo o sistema:

```bash
# 1. Iniciar Backend e Oracle Database
cd backend
docker compose up -d

# Aguardar os serviços iniciarem (pode levar alguns minutos na primeira vez)
# Verificar status:
docker compose ps

# Ver logs:
docker compose logs -f backend
```

O backend estará disponível em: `http://localhost:8081`

```bash
# 2. Iniciar Frontend (em outro terminal)
cd front-end
npm install
npm run dev
```

O frontend estará disponível em: `http://localhost:5173`

### Opção 2: Desenvolvimento Local

#### Backend:

```bash
cd backend

# 1. Iniciar Oracle Database via Docker
docker compose up -d oracle

# 2. Executar backend localmente
mvn quarkus:dev
```

#### Frontend:

```bash
cd front-end
npm install
npm run dev
```

## 🔐 Segurança

### Proteção de Rotas Administrativas

As seguintes rotas estão protegidas e requerem autenticação:

- `/swagger-ui` - Interface Swagger (requer senha)
- `/health` - Health check (requer senha)
- `/q/health` - Health check SmallRye (requer senha)
- `/q/openapi.json` - OpenAPI JSON (bloqueado para acesso direto, apenas via Swagger UI)

**Senha padrão**: `projedata`

**Métodos de autenticação**:
- Header HTTP: `X-API-Key: projedata`
- Query parameter: `?apiKey=projedata`

**Exemplo**:
```bash
# Acessar Swagger UI
curl -H "X-API-Key: projedata" http://localhost:8081/swagger-ui

# Ou via browser
http://localhost:8081/swagger-ui?apiKey=projedata
```

**Nota**: O arquivo `/q/openapi.json` não pode ser baixado diretamente por motivos de segurança. Ele só é acessível através do Swagger UI.

## 📡 API Endpoints

Base URL: `http://localhost:8081/api`

### Produtos
- `GET /api/products` - Listar todos os produtos
- `GET /api/products/{code}` - Buscar produto por código
- `POST /api/products` - Criar produto
- `PUT /api/products/{code}` - Atualizar produto
- `DELETE /api/products/{code}` - Deletar produto

### Matérias-Primas
- `GET /api/raw-materials` - Listar todas as matérias-primas
- `GET /api/raw-materials/{code}` - Buscar matéria-prima por código
- `POST /api/raw-materials` - Criar matéria-prima
- `PUT /api/raw-materials/{code}` - Atualizar matéria-prima
- `DELETE /api/raw-materials/{code}` - Deletar matéria-prima

### Associações Produto-Matéria-Prima
- `GET /api/products/{productCode}/raw-materials` - Listar associações de um produto
- `POST /api/products/{productCode}/raw-materials` - Criar associação
- `PUT /api/products/{productCode}/raw-materials/{rawMaterialCode}` - Atualizar quantidade
- `DELETE /api/products/{productCode}/raw-materials/{rawMaterialCode}` - Remover associação

### Produção
- `GET /api/production/suggestions` - Obter sugestões de produção
- `POST /api/production/confirm` - Confirmar produção (atualiza estoque)

## 🎯 Funcionalidades Principais

### 1. Gerenciamento de Produtos
- CRUD completo de produtos
- Cada produto possui código, nome e valor unitário

### 2. Gerenciamento de Matérias-Primas
- CRUD completo de matérias-primas
- Controle de estoque (quantidade disponível)
- Cada matéria-prima possui código, nome e quantidade em estoque

### 3. Associação Produto-Matéria-Prima
- Definir quais matérias-primas são necessárias para cada produto
- Especificar a quantidade de cada matéria-prima por produto

### 4. Sugestões de Produção
- Algoritmo inteligente que calcula quantos produtos podem ser produzidos
- Prioriza produtos com maior valor unitário
- Considera o estoque disponível de matérias-primas
- Evita sobreposição de uso de estoque

### 5. Confirmação de Produção
- Atualiza o estoque de matérias-primas após confirmação
- Suporte a múltiplos produtos em uma única confirmação
- Controle de concorrência (otimistic locking)
- Transações atômicas para garantir integridade

## 🧪 Testes

### Frontend

```bash
cd front-end

# Testes unitários
npm test

# Testes em modo watch
npm run test:watch

# Cobertura de testes
npm run test:coverage

# Testes E2E com Cypress
npm run cypress:open
npm run cypress:run
```

### Backend

```bash
cd backend

# Executar todos os testes
mvn test

# Testes unitários
mvn test -Dtest=*ServiceTest

# Testes de integração
mvn test -Dtest=*ResourceTest
```

## 📚 Documentação da API

A documentação interativa da API está disponível via Swagger UI:

1. Acesse: `http://localhost:8081/swagger-ui?apiKey=projedata`
2. Ou use o header: `X-API-Key: projedata`

No Swagger UI você pode:
- Visualizar todos os endpoints
- Testar os endpoints diretamente
- Ver exemplos de requisições e respostas
- Entender os modelos de dados

## 🗄️ Estrutura do Banco de Dados

### Tabela: PRODUCTS
- `id` NUMBER(19,0) PRIMARY KEY
- `code` VARCHAR2(50) UNIQUE NOT NULL
- `name` VARCHAR2(255) NOT NULL
- `value` NUMBER(10,2) NOT NULL
- `version` NUMBER(19,0) NOT NULL DEFAULT 0 (controle de concorrência)

### Tabela: RAW_MATERIALS
- `id` NUMBER(19,0) PRIMARY KEY
- `code` VARCHAR2(50) UNIQUE NOT NULL
- `name` VARCHAR2(255) NOT NULL
- `stock_quantity` NUMBER(10,2) NOT NULL
- `version` NUMBER(19,0) NOT NULL DEFAULT 0 (controle de concorrência)

### Tabela: PRODUCT_RAW_MATERIALS
- `product_id` NUMBER(19,0) FK -> PRODUCTS
- `raw_material_id` NUMBER(19,0) FK -> RAW_MATERIALS
- `quantity` NUMBER(10,2) NOT NULL
- PRIMARY KEY (product_id, raw_material_id)

## 🔄 Controle de Concorrência

O sistema implementa controle de concorrência para evitar problemas em ambientes multiusuário:

- **Optimistic Locking**: Usa `@Version` nas entidades
- **Pessimistic Locking**: Usa locks no banco durante operações críticas
- **Transações Atômicas**: Garante que operações de produção sejam atômicas
- **Tratamento de Conflitos**: Retorna erros apropriados quando detecta conflitos

## 🌐 Internacionalização (i18n)

O frontend suporta múltiplos idiomas:
- Português (pt-BR) - padrão
- Inglês (en)

O idioma pode ser alterado através do menu na sidebar.

## 📦 Build e Deploy

### Build Local

#### Frontend

```bash
cd front-end
npm run build
```

Os arquivos de produção serão gerados em `front-end/dist/`

#### Backend

```bash
cd backend

# Build
mvn clean package

# O JAR será gerado em: target/quarkus-app/quarkus-run.jar
```

### Docker

```bash
# Build das imagens
cd backend
docker compose build

# Iniciar serviços
docker compose up -d

# Parar serviços
docker compose down

# Ver logs
docker compose logs -f
```

## 🚀 Deploy em Produção

### Deploy do Backend no Google Cloud Run

O backend está preparado para deploy no Google Cloud Run. Siga os passos abaixo:

#### Pré-requisitos

1. **Google Cloud SDK (gcloud CLI)** instalado
   ```bash
   # Instalar: https://cloud.google.com/sdk/docs/install
   ```

2. **Docker** instalado e rodando

3. **Conta Google Cloud** com projeto criado

#### Passo a Passo

1. **Autenticar no Google Cloud**:
   ```bash
   gcloud auth login
   gcloud auth configure-docker
   ```

2. **Configurar variáveis de ambiente** (opcional):
   
   Crie um arquivo `.env.cloudrun` na pasta `backend/` com as variáveis necessárias:
   ```bash
   DB_USERNAME=AUTOFLEX
   DB_PASSWORD=sua_senha_segura
   DB_URL=jdbc:oracle:thin:@seu-oracle-host:1521:XE
   ```

3. **Executar o script de deploy**:
   ```bash
   cd backend
   chmod +x deploy-cloudrun.sh
   ./deploy-cloudrun.sh [PROJECT_ID] [REGION] [SERVICE_NAME]
   ```

   **Exemplo**:
   ```bash
   ./deploy-cloudrun.sh meu-projeto-gcp us-central1 autoflex-backend
   ```

   Ou usando variável de ambiente:
   ```bash
   export GOOGLE_CLOUD_PROJECT=meu-projeto-gcp
   ./deploy-cloudrun.sh
   ```

4. **O script irá**:
   - Habilitar APIs necessárias no Google Cloud
   - Fazer build da imagem Docker
   - Enviar imagem para Google Container Registry
   - Fazer deploy no Cloud Run
   - Configurar variáveis de ambiente
   - Testar o health check

5. **Após o deploy**, você receberá a URL do serviço:
   ```
   URL do serviço: https://autoflex-backend-xxxxx.run.app
   Health check: https://autoflex-backend-xxxxx.run.app/health
   Swagger UI: https://autoflex-backend-xxxxx.run.app/swagger-ui
   ```

#### Configurações do Cloud Run

O deploy usa as seguintes configurações padrão:
- **Porta**: 8080
- **Memória**: 512Mi
- **CPU**: 1
- **Instâncias mínimas**: 0 (scale to zero)
- **Instâncias máximas**: 10
- **Timeout**: 300 segundos
- **Acesso**: Público (allow-unauthenticated)

Para alterar, edite o script `deploy-cloudrun.sh`.

#### Verificar Logs

```bash
gcloud run services logs read autoflex-backend --region=us-central1
```

#### Atualizar Deploy

Para atualizar o serviço após mudanças no código:

```bash
cd backend
./deploy-cloudrun.sh [PROJECT_ID] [REGION] [SERVICE_NAME]
```

O script detecta se o serviço já existe e faz update automaticamente.

### Deploy do Frontend na Vercel

O frontend pode ser facilmente deployado na Vercel. Siga os passos abaixo:

#### Opção 1: Deploy via Vercel CLI (Recomendado)

1. **Instalar Vercel CLI**:
   ```bash
   npm install -g vercel
   ```

2. **Autenticar na Vercel**:
   ```bash
   vercel login
   ```

3. **Configurar variáveis de ambiente**:
   
   Crie um arquivo `.env.production` na pasta `front-end/`:
   ```bash
   VITE_API_URL=https://seu-backend-url.run.app/api
   ```

4. **Fazer deploy**:
   ```bash
   cd front-end
   vercel --prod
   ```

5. **Seguir as instruções**:
   - Escolher o projeto (ou criar novo)
   - Confirmar configurações
   - Aguardar o build e deploy

#### Opção 2: Deploy via GitHub (Integração Contínua)

1. **Conectar repositório GitHub à Vercel**:
   - Acesse [vercel.com](https://vercel.com)
   - Faça login com sua conta GitHub
   - Clique em "Add New Project"
   - Selecione o repositório do projeto

2. **Configurar o projeto**:
   - **Framework Preset**: Vite
   - **Root Directory**: `front-end`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
   - **Install Command**: `npm install`

3. **Configurar variáveis de ambiente**:
   - Na seção "Environment Variables", adicione:
     - `VITE_API_URL`: URL do seu backend (ex: `https://autoflex-backend-xxxxx.run.app/api`)

4. **Deploy automático**:
   - A cada push na branch `main` (ou `master`), a Vercel fará deploy automaticamente
   - Pull requests geram preview deployments

#### Opção 3: Deploy via Dashboard Vercel

1. **Acesse o dashboard**: [vercel.com/dashboard](https://vercel.com/dashboard)

2. **Clique em "Add New Project"**

3. **Importe o repositório** do GitHub/GitLab/Bitbucket

4. **Configure o projeto**:
   - Framework: Vite
   - Root Directory: `front-end`
   - Build Command: `npm run build`
   - Output Directory: `dist`

5. **Adicione variáveis de ambiente**:
   ```
   VITE_API_URL=https://seu-backend-url.run.app/api
   ```

6. **Clique em "Deploy"**

#### Configuração de CORS no Backend

Após fazer deploy do frontend, você precisa atualizar as origens permitidas no backend:

1. **No Google Cloud Run**, adicione a URL da Vercel nas origens CORS:
   
   Edite `backend/src/main/resources/application.properties`:
   ```properties
   quarkus.http.cors.origins=http://localhost:5173,http://localhost:3000,https://seu-projeto.vercel.app
   ```

2. **Ou via variável de ambiente no Cloud Run**:
   ```bash
   gcloud run services update autoflex-backend \
     --update-env-vars="QUARKUS_HTTP_CORS_ORIGINS=https://seu-projeto.vercel.app" \
     --region=us-central1
   ```

#### Verificar Deploy

Após o deploy, acesse a URL fornecida pela Vercel (ex: `https://autoflex.vercel.app`).

#### Atualizar Deploy

- **Via CLI**: Execute `vercel --prod` novamente
- **Via GitHub**: Faça push para a branch principal
- **Via Dashboard**: Clique em "Redeploy" no dashboard da Vercel

#### Domínio Customizado

1. No dashboard da Vercel, vá em "Settings" > "Domains"
2. Adicione seu domínio customizado
3. Siga as instruções para configurar DNS

### Deploy Completo (Backend + Frontend)

Para fazer deploy completo do sistema:

1. **Deploy do Backend** (Google Cloud Run):
   ```bash
   cd backend
   ./deploy-cloudrun.sh [PROJECT_ID] [REGION] [SERVICE_NAME]
   ```

2. **Anotar URL do Backend**:
   ```
   Backend URL: https://autoflex-backend-xxxxx.run.app
   ```

3. **Configurar Frontend**:
   ```bash
   cd front-end
   # Criar .env.production
   echo "VITE_API_URL=https://autoflex-backend-xxxxx.run.app/api" > .env.production
   ```

4. **Deploy do Frontend** (Vercel):
   ```bash
   vercel --prod
   ```

5. **Atualizar CORS no Backend**:
   - Adicione a URL da Vercel nas origens permitidas

6. **Testar**:
   - Acesse a URL do frontend na Vercel
   - Verifique se consegue fazer requisições ao backend

## 🔧 Configuração

### Variáveis de Ambiente

#### Frontend
Crie um arquivo `.env` em `front-end/`:
```
VITE_API_URL=http://localhost:8081/api
```

#### Backend
As configurações estão em `backend/src/main/resources/application.properties`

**Desenvolvimento (Docker)**:
- Perfil: `docker`
- URL do banco: `jdbc:oracle:thin:@oracle:1521:XE`

**Produção**:
- Perfil: `prod`
- Variáveis de ambiente: `DB_USERNAME`, `DB_PASSWORD`, `DB_URL`

## 🐛 Troubleshooting

### Backend não inicia
- Verifique se o Oracle está rodando: `docker compose ps`
- Verifique os logs: `docker compose logs backend`
- Verifique se a porta 8081 está livre

### Frontend não conecta ao backend
- Verifique se o backend está rodando
- Verifique a URL da API em `.env`
- Verifique CORS no backend

### Erro de conexão com Oracle
- Aguarde o Oracle inicializar completamente (pode levar alguns minutos)
- Verifique os logs: `docker compose logs oracle`
- Teste a conexão: `docker compose exec oracle sqlplus AUTOFLEX/autoflex123@XE`

### Porta já em uso
- Backend: Altere `quarkus.http.port` em `application.properties`
- Frontend: Altere a porta no `vite.config.ts`

## 📝 Licença

Este projeto foi desenvolvido como teste prático para Autoflex.

## 👥 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📞 Suporte

Para questões ou problemas, abra uma issue no repositório do projeto.

---

**Desenvolvido com ❤️ usando Kotlin, Quarkus, React e TypeScript**
