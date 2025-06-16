# 🐣 CegonhaExpress Delivery

![Logo Cegonha Express](docs/images/logo-cegonha-express.jpeg)

> "Não espere 9 meses para ter o seu bebê, adquira agora e calcule em quanto tempo ele chegará em sua casa!"

**CegonhaExpress** é um sistema completo de entrega especializado em bebês reborn, desenvolvido como projeto acadêmico para demonstrar conceitos avançados de Programação Orientada a Objetos, arquitetura em camadas e integração de APIs.

## 📑 Índice

- [📦 Sobre o Projeto](#-sobre-o-projeto)
  - [✨ Funcionalidades Principais](#-funcionalidades-principais)
- [🏗️ Arquitetura Técnica](#️-arquitetura-técnica)
  - [Backend (Spring Boot)](#backend-spring-boot)
  - [Banco de Dados](#banco-de-dados)
- [🛠️ Tecnologias Utilizadas](#️-tecnologias-utilizadas)
- [🚀 Configuração e Instalação](#-configuração-e-instalação)
  - [Pré-requisitos](#pré-requisitos)
  - [1. Clone do Repositório](#1-clone-do-repositório)
  - [2. Configuração da API do Google Maps](#2-configuração-da-api-do-google-maps)
  - [3. Configuração Local](#3-configuração-local)
  - [4. Executar o Projeto](#4-executar-o-projeto)
  - [5. Acessos da Aplicação](#5-acessos-da-aplicação)
- [📚 Uso da API](#-uso-da-api)
  - [Endpoints Principais](#endpoints-principais)
  - [Códigos de Status HTTP](#códigos-de-status-http)
- [🗄️ Configuração de Banco de Dados](#️-configuração-de-banco-de-dados)
- [🧪 Testes](#-testes)
- [🎯 Conceitos Demonstrados](#-conceitos-demonstrados)
  - [Programação Orientada a Objetos](#programação-orientada-a-objetos)
  - [Design Patterns](#design-patterns)
  - [Arquitetura em Camadas](#arquitetura-em-camadas)
- [🚨 Troubleshooting](#-troubleshooting)
  - [Problemas Comuns](#problemas-comuns)
  - [Logs Úteis](#logs-úteis)
- [📊 Monitoramento](#-monitoramento)
- [🔒 Segurança](#-segurança)
- [🤝 Contribuição](#-contribuição)
- [📄 Licença](#-licença)
- [👨‍💻 Autores](#-autores)
- [📞 Suporte](#-suporte)

## 📦 Sobre o Projeto

Sistema de logística e entrega que simula o processo completo de pedido, cálculo de frete e acompanhamento de entregas de bebês reborn, combinando humor e funcionalidade técnica robusta.

### ✨ Funcionalidades Principais

- **Cálculo de Frete Inteligente**: Integração com Google Distance Matrix API para distâncias reais
- **Validação de Endereços**: Integração com API ViaCEP para validação automática de CEPs
- **Gestão de Pedidos**: Sistema completo de cadastro e acompanhamento de encomendas
- **Múltiplas Modalidades**: Entrega expressa (1 dia), padrão (3 dias) e econômica (7 dias)
- **API REST Completa**: Backend robusto com documentação Swagger/OpenAPI
- **Tratamento de Exceções**: Sistema global de tratamento de erros
- **Catálogo de Bebês**: Sistema de visualização de bebês reborn disponíveis

## 🏗️ Arquitetura Técnica

### Backend (Spring Boot)

- **API REST** com documentação Swagger/OpenAPI
- **JPA/Hibernate** para persistência de dados
- **Integração Google Maps** para cálculo de distância real
- **Integração ViaCEP** para validação de CEPs
- **Validações robustas** com Bean Validation
- **Tratamento de exceções** personalizado
- **CORS configurado** para frontend

### Banco de Dados

- **H2** para desenvolvimento e testes
- **MariaDB** preparado para produção
- **Modelagem otimizada** com relacionamentos JPA
- **Indices para performance** em consultas frequentes

## 🛠️ Tecnologias Utilizadas

**Backend:**

- Java 21+
- Spring Boot 3.5.0
- Spring Data JPA
- Spring Web
- Bean Validation
- H2/MariaDB
- Google Maps Services 2.2.0
- SpringDoc OpenAPI 2.8.8

**Ferramentas:**

- Maven 3.6+
- Lombok
- SLF4J

## 🚀 Configuração e Instalação

### Pré-requisitos

- **Java 21** ou superior
- **Maven 3.6+**
- **IDE** de sua preferência (IntelliJ IDEA, Eclipse, VS Code)
- **Conta Google Cloud** (para Google Maps API)

### 1. Clone do Repositório

```bash
git clone https://github.com/GabrielCoelho/cegonha-express-delivery.git
cd cegonha-express-delivery
```

### 2. Configuração da API do Google Maps

#### 2.1. Criar Projeto no Google Cloud Console

1. Acesse o [Google Cloud Console](https://console.cloud.google.com)
2. Crie um novo projeto ou selecione um existente
3. No menu de navegação, vá em **APIs e Serviços** → **Biblioteca**
4. Pesquise por "Distance Matrix API"
5. Clique em **Distance Matrix API** e depois em **ATIVAR**

#### 2.2. Criar Chave de API

1. No Google Cloud Console, vá em **APIs e Serviços** → **Credenciais**
2. Clique em **+ CRIAR CREDENCIAIS** → **Chave de API**
3. Copie a chave gerada
4. **[RECOMENDADO]** Clique em **RESTRINGIR CHAVE** e configure:
   - **Restrições de API**: Selecione apenas "Distance Matrix API"

#### 2.3. Configurar Cobrança (Obrigatório)

1. No Google Cloud Console, vá em **Faturamento**
2. Vincule um cartão de crédito ao projeto
3. **Nota**: O Google oferece $200 de créditos gratuitos mensais

### 3. Configuração Local

#### 3.1. Criar Arquivo de Configuração

```bash
# Copie o template de configuração
cp src/main/resources/application-template.yml src/main/resources/application-local.yml
```

#### 3.2. Configurar application-local.yml

Edite o arquivo `src/main/resources/application-local.yml`:

```yaml
spring:
  # Configurações de banco de dados MariaDB
  datasource:
    url: jdbc:mariadb://localhost:3306/cegonha_express
    driver-class-name: org.mariadb.jdbc.Driver
    username: seu_usuario_aqui # <- SUBSTITUA PELO USUARIO CRIADO
    password: sua_senha_aqui  # ← SUBSTITUA PELA SUA SENHA
{...}
# Google Maps API Configuration
google:
  maps:
    api:
      key: "SUA_API_KEY_AQUI" # ← SUBSTITUA PELA SUA API KEY
```

#### 3.3. Configurar MariaDB

Antes de executar a aplicação, certifique-se de que o MariaDB está rodando:

```bash
# No Ubuntu/Debian
sudo apt update
sudo apt install mariadb-server
sudo systemctl start mariadb
sudo systemctl enable mariadb

# No macOS (via Homebrew)
brew install mariadb
brew services start mariadb

# No Windows
# Baixe e instale o MariaDB do site oficial
```

Crie o banco de dados:

```sql
# Conecte ao MariaDB
mysql -u root -p

# Crie o banco de dados
CREATE DATABASE cegonha_express CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Crie um usuário específico (opcional, mas recomendado)
CREATE USER 'cegonhaex'@'localhost' IDENTIFIED BY 'cegonha';
GRANT ALL PRIVILEGES ON cegonhaexpress.* TO 'cegonhaex'@'localhost';
FLUSH PRIVILEGES;
```

**Alternativa com H2 (para desenvolvimento rápido):**

Se preferir usar H2 para desenvolvimento local, substitua a configuração do datasource por:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:cegonhadb
    driver-class-name: org.h2.Driver
    username: sa
    password: ""

  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect

  # Console H2 (apenas para H2)
  h2:
    console:
      enabled: true
      path: /h2-console
```

### 4. Executar o Projeto

#### 4.1. Via Maven

```bash
# Compilar e executar
./mvnw spring-boot:run

# Ou compilar e executar JAR
./mvnw clean package
java -jar target/cegonha-express-0.0.1-SNAPSHOT.jar
```

#### 4.2. Via IDE

1. Importe o projeto como projeto Maven
2. Execute a classe `CegonhaExpressApplication.java`

### 5. Acessos da Aplicação

- **API Documentation**: <http://localhost:8080/swagger-ui/index.html>
- **Aplicação**: <http://localhost:8080>

**Para H2 (se estiver usando):**

- **H2 Console**: <http://localhost:8080/h2-console>
  - **JDBC URL**: `jdbc:h2:mem:cegonhadb`
  - **Username**: `sa`
  - **Password**: (deixar em branco)

## 📚 Uso da API

### Endpoints Principais

Você pode utilizar a Swagger UI para verificar todos os endpoints criados, ou ler a nossa [documentação completa do Uso da API](docs/API_USAGE.md)

#### Códigos de Status HTTP

- **200**: Sucesso
- **201**: Criado com sucesso
- **204**: Sem conteúdo
- **400**: Dados inválidos
- **404**: Recurso não encontrado
- **409**: Conflito de estado
- **415**: Tipo de mídia não suportado
- **503**: Serviço indisponível

## 🗄️ Configuração de Banco de Dados

### Desenvolvimento (H2) vs. Produção (MariaDB)

**MariaDB** está configurado como padrão nesta fase do projeto pois estamos próximos da apresentação final do mesmo.

**Para usar H2** (desenvolvimento rápido), veja a seção "Alternativa com H2" na configuração do application-local.yml.

## 🧪 Testes

### Executar Testes

```bash
# Executar todos os testes
./mvnw test
```

**Observação**: Estamos com uma cobertura quase completa de testes que foram criados em cada implementação. Conforme o projeto foi ficando mais robusto, alguns testes podem retornar falhas e/ou erros por já não estarem atualizados. É algo que a equipe focará na semana da apresentação.

### Teste Manual da API

Use o Swagger UI ou ferramentas como Postman/Insomnia para testar os endpoints.

## 🎯 Conceitos Demonstrados

### Programação Orientada a Objetos

- **Herança**: `BaseEntity` como classe pai para todas as entidades
- **Polimorfismo**: Enum `TipoEntrega` com comportamentos diferentes
- **Encapsulamento**: Proteção de dados nas entidades
- **Abstração**: Interfaces para repositories e services

### Design Patterns

- **Strategy Pattern**: Cálculo de fretes por modalidade na classe `Frete`
- **Factory Pattern**: Criação de objetos via construtores especializados
- **DTO Pattern**: Transferência de dados entre camadas
- **Repository Pattern**: Abstração de acesso a dados

### Arquitetura em Camadas

- **Controller**: Endpoints REST
- **Service**: Lógica de negócio
- **Repository**: Acesso a dados
- **Entity**: Modelo de domínio
- **DTO**: Transferência de dados
- **Config**: Configurações da aplicação

## 🚨 Troubleshooting

### Problemas Comuns

#### Erro de API Key do Google Maps

```
GoogleMapsIntegrationException: API_KEY_INVALID
```

**Solução**: Verifique se a chave API está correta e se a Distance Matrix API está habilitada.

#### Erro de Conexão com Banco

```
org.h2.jdbc.JdbcSQLNonTransientConnectionException
```

**Solução**: Verifique se as configurações do banco no `application-local.yml` estão corretas.

#### Erro 415 - Unsupported Media Type

**Solução**: Certifique-se de enviar `Content-Type: application/json` nas requisições POST/PUT.

#### CEP Não Encontrado

```
404 - CEP não encontrado na base de dados dos Correios
```

**Solução**: Verifique se o CEP é válido. A API ViaCEP pode estar temporariamente indisponível.

### Logs Úteis

Para debugar problemas, habilite logs detalhados:

```yaml
logging:
  level:
    br.com.cegonhaexpress: DEBUG
    com.google.maps: DEBUG
    org.springframework.web: DEBUG
```

## 📊 Monitoramento

### Métricas Importantes

- **Performance de API**: Tempo de resposta das requisições
- **Taxa de Erro**: Percentual de requisições com erro
- **Uso da API Google**: Número de chamadas à Distance Matrix API
- **Uso da API ViaCEP**: Número de consultas de CEP

## 🔒 Segurança

### Recomendações

1. **API Key**: Nunca commite a chave do Google Maps no código
2. **Banco de Dados**: Use senhas fortes em produção
3. **CORS**: Configure origens permitidas adequadamente
4. **HTTPS**: Use HTTPS em produção
5. **Rate Limiting**: Implemente rate limiting para APIs públicas

## 🤝 Contribuição

### Padrões de Código

- **Nomenclatura**: CamelCase para Java, snake_case para banco
- **Documentação**: JavaDoc para métodos públicos
- **Testes**: Cobertura mínima de 80%
- **Commits**: Mensagens claras e descritivas

### Estrutura de Branches

- `main`: Código estável de produção
- `develop`: Código desenvolvido e em teste das duas equipes: Back e Frontend
  - `backend`: Código implementado pela equipe de *backend* testado somente dentro deste escopo
  - `frontend`: Código implementado pela equipe de *fronend* testado somente dentro deste escopo
- `feat/*`: Novas funcionalidades a serem implementadas
- `(bug/hot)fix/*`: Correções de bugs

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👨‍💻 Autores

Desenvolvedor | Responsabilidade Principal | GitHub
-- | -- | --
Gabriel Coelho Soares | Liderança, Arquitetura Backend, Integração APIs | [@GabrielCoelho](https://github.com/GabrielCoelho)
Brenda Gaudêncio | Frontend React, UI/UX | [@brendagaudencio](https://github.com/brendagaudencio)
Marcos Moreira | Dados, Configuração AWS | [@JamalShadowDev](https://github.com/JamalShadowDev)
Renan Mazzilli | DevOps, Build Tools | [@renan-mazzilli](https://github.com/renan-mazzilli]
Adryelle Calefi | Gestão, Documentação | [@DryCaleffi](https://github.com/DryCaleffi)
Guilherme Garcia | Code Review, Testes | [@HiroGarcia](https://github.com/HiroGarcia)
Mateus Nascimento | Gestão, Apresentação | [@M-Araujo26](https://github.com/M-Araujo26)
Tabata Etiéle | Code Review, Documentação | [@TabataEtiele](https://github.com/TabataEtiele)
Thaito Batalini | Code Review, Apresentação | [@thaitoGB](https://github.com/thaitoGB)

## 📞 Suporte

Para dúvidas e suporte:

1. **Issues**: Abra uma issue no GitHub
2. **Documentação**: Consulte a documentação da API via Swagger e nossos arquivos de Documentação

---

*Projeto desenvolvido com ☕ para fins acadêmicos - FATEC 2025*
