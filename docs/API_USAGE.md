# 📚 DOCUMENTAÇÃO DA API - CEGONHAEXPRESS

## 🚀 Visão Geral

A **CegonhaExpress API** é uma REST API completa para gerenciamento de entregas de bebês reborn, oferecendo funcionalidades de criação, acompanhamento e cancelamento de encomendas, além de consulta de endereços via CEP.

### 📊 Informações da API

- **Versão**: 1.1.0
- **Base URL**: `http://localhost:8080/api`
- **Formato**: JSON
- **Autenticação**: Não requerida (projeto acadêmico)
- **Documentação Interativa**: `/swagger-ui.html`

---

## 📋 Índice

1. [Encomendas](#-encomendas)
2. [Endereços](#-endereços)
3. [Catálogo de Bebês](#-catálogo-de-bebês)
4. [Modelos de Dados](#-modelos-de-dados)
5. [Códigos de Status](#-códigos-de-status)
6. [Exemplos de Uso](#-exemplos-de-uso)

---

## 📦 Encomendas

### Criar Nova Encomenda

Cria uma nova encomenda de entrega com cálculo automático de frete.

```http
POST /api/encomendas
Content-Type: application/json
```

**Corpo da Requisição:**

```json
{
  "enderecoDestino": {
    "cep": "01001-000",
    "logradouro": "Praça da Sé",
    "numero": "123",
    "complemento": "Apto 45",
    "bairro": "Sé",
    "cidade": "São Paulo",
    "uf": "SP",
    "referencia": "Próximo à Catedral"
  },
  "tipoEntrega": "PADRAO",
  "descricaoBebe": "Bebê Alice, 50cm, cabelo loiro cacheado, olhos azuis",
  "pesoKg": 2.5,
  "alturaCm": 50.0,
  "valorDeclarado": 150.00
}
```

**Resposta (201 Created):**

```json
{
  "codigo": "CE1735834567123",
  "status": "Pendente",
  "valorFrete": "R$ 67,25",
  "tempoEstimadoEntrega": "5 dias úteis"
}
```

---

### Listar Todas as Encomendas

Retorna lista completa de encomendas cadastradas.

```http
GET /api/encomendas
```

**Resposta (200 OK):**

```json
[
  {
    "codigo": "CE1234567890123",
    "status": "Em Trânsito",
    "valorFrete": "R$ 45,50",
    "tempoEstimadoEntrega": "3 dias úteis"
  },
  {
    "codigo": "CE9876543210987",
    "status": "Entregue",
    "valorFrete": "R$ 32,75",
    "tempoEstimadoEntrega": "1 dia útil"
  }
]
```

---

### Listar Encomendas Ativas

Retorna apenas encomendas em andamento (exclui entregues e canceladas).

```http
GET /api/encomendas/ativas
```

**Resposta (200 OK):**

```json
[
  {
    "codigo": "CE1234567890123",
    "status": "Pendente",
    "valorFrete": "R$ 45,50",
    "tempoEstimadoEntrega": "3 dias úteis"
  }
]
```

---

### Buscar Encomenda por Código

Localiza encomenda específica pelo código de rastreamento.

```http
GET /api/encomendas/{codigo}
```

**Parâmetros:**

- `codigo` (path, required): Código único da encomenda (formato: CE + dígitos)

**Exemplo:**

```http
GET /api/encomendas/CE1234567890123
```

**Resposta (200 OK):**

```json
{
  "codigo": "CE1234567890123",
  "status": "Em Trânsito",
  "valorFrete": "R$ 45,50",
  "tempoEstimadoEntrega": "3 dias úteis"
}
```

---

### Avançar Status da Encomenda

Avança o status para o próximo estado válido na sequência.

```http
PUT /api/encomendas/{codigo}/status
```

**Sequência de Status:**
`PENDENTE` → `CONFIRMADA` → `EM_TRANSITO` → `ENTREGUE`

**Exemplo:**

```http
PUT /api/encomendas/CE1234567890123/status
```

**Resposta (200 OK):**

```json
"CONFIRMADA"
```

---

### Cancelar Encomenda

Cancela uma encomenda ativa com motivo obrigatório.

```http
PUT /api/encomendas/{codigo}/cancelar
Content-Type: application/json
```

**Corpo da Requisição:**

```json
{
  "motivo": "Cliente solicitou cancelamento devido a mudança de endereço"
}
```

**Resposta (200 OK):**

```json
"CANCELADA"
```

---

## 🏠 Endereços

### Consultar Endereço por CEP

Busca informações completas de endereço brasileiro via CEP.

```http
GET /api/enderecos/cep/{cep}
```

**Parâmetros:**

- `cep` (path, required): CEP brasileiro (formatos: 00000-000 ou 00000000)

**Exemplo:**

```http
GET /api/enderecos/cep/01001-000
```

**Resposta (200 OK):**

```json
{
  "cep": "01001-000",
  "logradouro": "Praça da Sé",
  "complemento": "lado ímpar",
  "bairro": "Sé",
  "localidade": "São Paulo",
  "uf": "SP",
  "ibge": "3550308",
  "gia": "1004",
  "ddd": "11",
  "siafi": "7107"
}
```

---

## 👶 Catálogo de Bebês

### Listar Bebês Disponíveis

Retorna catálogo completo de bebês reborn com especificações.

```http
GET /api/encomendas/bebes
```

**Resposta (200 OK):**

```json
[
  {
    "id": "BB001",
    "nome": "Alice",
    "linkImg": "https://exemplo.com/bebes/alice.jpg",
    "descricao": "Bebê reborn com cabelo loiro cacheado e olhos azuis",
    "acessorios": "Vestido rosa, sapatinhos, chupeta",
    "peso_kg": 2.5,
    "altura_cm": 50.0
  },
  {
    "id": "BB002",
    "nome": "Miguel",
    "linkImg": "https://exemplo.com/bebes/miguel.jpg",
    "descricao": "Bebê reborn com cabelo castanho e olhos verdes",
    "acessorios": "Macacão azul, boné, mamadeira",
    "peso_kg": 2.8,
    "altura_cm": 52.0
  }
]
```

---

## 📊 Modelos de Dados

### EncomendaRequestDTO

```json
{
  "enderecoDestino": "EnderecoDTO",
  "tipoEntrega": "TipoEntrega",
  "descricaoBebe": "string (max: 500)",
  "pesoKg": "number (0.1-15.0)",
  "alturaCm": "number (20.0-100.0)",
  "valorDeclarado": "number (≥0.0)"
}
```

### EnderecoDTO

```json
{
  "cep": "string (pattern: \\d{5}-?\\d{3})",
  "logradouro": "string (required)",
  "numero": "string",
  "complemento": "string",
  "bairro": "string (required)",
  "cidade": "string (required)",
  "uf": "string (required)",
  "referencia": "string"
}
```

### EncomendaResponseDTO

```json
{
  "codigo": "string",
  "status": "string",
  "valorFrete": "string (formatted)",
  "tempoEstimadoEntrega": "string (formatted)"
}
```

### BebeResponseDTO

```json
{
  "id": "string",
  "nome": "string",
  "linkImg": "string (URL)",
  "descricao": "string",
  "acessorios": "string",
  "peso_kg": "number",
  "altura_cm": "number"
}
```

### ViaCepResponseDto

```json
{
  "cep": "string",
  "logradouro": "string",
  "complemento": "string",
  "bairro": "string",
  "localidade": "string",
  "uf": "string",
  "ibge": "string",
  "gia": "string",
  "ddd": "string",
  "siafi": "string"
}
```

### CancelamentoRequestDTO

```json
{
  "motivo": "string (required, max: 500)"
}
```

---

## 🔢 Códigos de Status

### HTTP Status Codes

| Código | Descrição | Quando Ocorre |
|--------|-----------|---------------|
| **200** | OK | Operação realizada com sucesso |
| **201** | Created | Encomenda criada com sucesso |
| **204** | No Content | Lista vazia ou nenhuma ação possível |
| **400** | Bad Request | Dados inválidos ou formato incorreto |
| **404** | Not Found | Recurso não encontrado |
| **409** | Conflict | Conflito de estado de negócio |
| **415** | Unsupported Media Type | Content-Type incorreto |
| **503** | Service Unavailable | Serviço externo indisponível |

### Status da Encomenda

| Status | Descrição |
|--------|-----------|
| **PENDENTE** | Encomenda criada, aguardando confirmação |
| **CONFIRMADA** | Encomenda confirmada, preparando envio |
| **EM_TRANSITO** | Encomenda em trânsito para destino |
| **ENTREGUE** | Encomenda entregue com sucesso |
| **CANCELADA** | Encomenda cancelada |

### Tipos de Entrega

| Tipo | Descrição | Prazo Mínimo |
|------|-----------|--------------|
| **EXPRESSA** | Entrega expressa | 1 dia útil |
| **PADRAO** | Entrega padrão | 3 dias úteis |
| **ECONOMICA** | Entrega econômica | 7 dias úteis |

---

## 💡 Exemplos de Uso

### Fluxo Completo de Encomenda

#### 1. Validar CEP de Destino

```http
GET /api/enderecos/cep/01001-000
```

#### 2. Criar Encomenda

```http
POST /api/encomendas
{
  "enderecoDestino": {
    "cep": "01001-000",
    "logradouro": "Praça da Sé",
    "numero": "123",
    "bairro": "Sé",
    "cidade": "São Paulo",
    "uf": "SP"
  },
  "tipoEntrega": "PADRAO",
  "descricaoBebe": "Bebê Alice com vestido rosa"
}
```

#### 3. Acompanhar Status

```http
GET /api/encomendas/CE1735834567123
```

#### 4. Avançar Status (operação interna)

```http
PUT /api/encomendas/CE1735834567123/status
```

#### 5. Cancelar (se necessário)

```http
PUT /api/encomendas/CE1735834567123/cancelar
{
  "motivo": "Cliente desistiu da compra"
}
```

---

### Tratamento de Erros

#### CEP Inválido

```http
GET /api/enderecos/cep/abc123

400 Bad Request
{
  "timestamp": "2025-01-15 14:30:00",
  "status": 400,
  "error": "Parâmetro inválido",
  "message": "CEP precisa estar com formatação correta",
  "path": "/api/enderecos/cep/abc123"
}
```

#### Encomenda Não Encontrada

```http
GET /api/encomendas/CE9999999999999

404 Not Found
{
  "timestamp": "2025-01-15 14:30:00",
  "status": 404,
  "error": "Recurso não encontrado",
  "message": "Não existe uma encomenda com este Código",
  "path": "/api/encomendas/CE9999999999999"
}
```

#### Dados de Encomenda Inválidos

```http
POST /api/encomendas
{ "enderecoDestino": { "cep": "invalid" } }

400 Bad Request
{
  "timestamp": "2025-01-15 14:30:00",
  "status": 400,
  "error": "Erro de validação",
  "message": "Dados fornecidos são inválidos",
  "path": "/api/encomendas",
  "fieldErrors": {
    "enderecoDestino.cep": "CEP deve ter formato válido (00000-000)",
    "descricaoBebe": "Descrição do bebê é obrigatória"
  }
}
```

---

## 🛠️ Informações Técnicas

### Integrações Externas

- **ViaCEP**: Consulta de endereços brasileiros
- **Google Maps Distance Matrix**: Cálculo de distâncias reais para frete

### Tecnologias Utilizadas

- **Spring Boot 3.5.0**
- **Java 21**
- **JPA/Hibernate**
- **Bean Validation**
- **OpenAPI 3 / Swagger**

### Configuração de Desenvolvimento

```bash
# Iniciar aplicação
./mvnw spring-boot:run

# Acessar Swagger UI
http://localhost:8080/swagger-ui.html

# Console H2 (desenvolvimento)
http://localhost:8080/h2-console
```

---

## 📞 Suporte

Para dúvidas sobre a API:

- **Documentação Interativa**: `/swagger-ui.html`
- **Projeto**: CegonhaExpress
- **Versão**: 1.1.0

---

*Documentação gerada automaticamente para o projeto acadêmico CegonhaExpress - FATEC 2025* 🚀
