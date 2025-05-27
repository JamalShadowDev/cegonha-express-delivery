# 🐣 CegonhaExpress Delivery

![Logo Cegonha Express](docs/images/logo-cegonha-express.jpeg)

> "Não espere 9 meses para ter o seu bebê, adquira agora e calcule em quanto tempo ele chegará em sua casa!"

**CegonhaExpress** é um sistema completo de entrega especializado em bebês reborn, desenvolvido como projeto acadêmico para demonstrar conceitos avançados de Programação Orientada a Objetos, arquitetura em camadas e integração de APIs.

## 📦 Sobre o Projeto

Sistema de logística e entrega que simula o processo completo de pedido, cálculo de frete e acompanhamento de entregas de bebês reborn, combinando humor e funcionalidade técnica robusta.

### ✨ Funcionalidades Principais

- **Cálculo de Frete Inteligente**: Integração com API ViaCEP para validação automática de endereços
- **Gestão de Pedidos**: Sistema completo de cadastro e acompanhamento de encomendas
- **Múltiplas Modalidades**: Entrega expressa, standard e econômica
- **Interface Responsiva**: Frontend moderno com Thymeleaf e Bootstrap
- **API REST Completa**: Backend robusto com Spring Boot

## 🏗️ Arquitetura Técnica

### Backend (Spring Boot)

- **API REST** com documentação Swagger/OpenAPI
- **JPA/Hibernate** para persistência de dados
- **Integração ViaCEP** para validação de CEPs
- **Validações robustas** com Bean Validation
- **Tratamento de exceções** personalizado

### Frontend (MVC + Thymeleaf)

- **Templates responsivos** com Bootstrap 5
- **Formulários dinâmicos** com validação client/server-side
- **Interface intuitiva** para consulta de fretes
- **Feedback visual** em tempo real

### Banco de Dados

- **H2** para desenvolvimento e testes
- **PostgreSQL/MySQL** preparado para produção
- **Modelagem otimizada** com relacionamentos JPA

## 🛠️ Tecnologias Utilizadas

**Backend:**

- Java 21+
- Spring Boot 3.x
- Spring Data JPA
- Spring Web
- Bean Validation
- H2/PostgreSQL

**Frontend:**

- Thymeleaf
- Bootstrap 5
- JavaScript ES6+
- HTML5/CSS3

**Ferramentas:**

- Maven
- Swagger/OpenAPI
- Git

## 🎯 Conceitos Demonstrados

### Programação Orientada a Objetos

- ✅ **Herança**: Hierarquia de classes para tipos de entrega
- ✅ **Polimorfismo**: Diferentes estratégias de cálculo de frete
- ✅ **Encapsulamento**: Proteção de dados e métodos privados
- ✅ **Abstração**: Interfaces para serviços e repositórios

### Design Patterns

- 🎯 **Strategy Pattern**: Cálculo de fretes por modalidade
- 🎯 **Factory Pattern**: Criação de objetos de entrega
- 🎯 **DTO Pattern**: Transferência de dados entre camadas
- 🎯 **Repository Pattern**: Abstração de acesso a dados

## 🚀 Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- IDE de sua preferência

### Executando o Projeto

```bash
# Clone o repositório
git clone https://github.com/GabrielCoelho/cegonha-express-delivery.git

# Entre no diretório
cd cegonha-express-delivery

# Execute com Maven
./mvnw spring-boot:run

# Ou compile e execute
./mvnw clean package
java -jar target/cegonha-express-delivery-1.0.0.jar
```

## 🔧 Configuração Local

### 1. Configuração Inicial

```bash
# 1. Copie o template de configuração
cp src/main/resources/application-template.yml src/main/resources/application-local.yml

# 2. Configure sua API key no arquivo application-local.yml
# (O arquivo application-local.yml NÃO será commitado)

### Acessos

- **Aplicação Web**: <http://localhost:8080>
- **API Documentation**: <http://localhost:8080/swagger-ui.html>
- **H2 Console**: <http://localhost:8080/h2-console>

## 📚 Documentação Acadêmica

Este projeto foi desenvolvido como trabalho acadêmico para o curso de **Análise e Desenvolvimento de Sistemas**, demonstrando:

- Aplicação prática de conceitos de POO
- Arquitetura em camadas bem definida
- Integração com APIs externas
- Boas práticas de desenvolvimento
- Documentação técnica completa

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👨‍💻 Autores

- [Adryelle Calefi](https://github.com/DryCaleffi)
- [Brenda Gaudêncio](https://github.com/brendagaudencio)
- [Gabriel Coelho Soares](https://github.com/GabrielCoelho)
- [Guilherme Garcia](https://github.com/HiroGarcia)
- [Marcos Moreira](https://github.com/JamalShadowDev)
- [Mateus Nascimento de Araújo](https://github.com/M-Araujo26)
- [Renan Mazzilli Dias](https://github.com/renan-mazzilli)
- [Tabata Etiéle](https://github.com/TabataEtiele)
- [Thaito Batalini](https://github.com/tahitoGB)

---

*Projeto desenvolvido com ☕ para fins acadêmicos - FATEC 2025*
