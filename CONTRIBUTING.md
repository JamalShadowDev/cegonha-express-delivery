# 🤝 Guia de Contribuição - CegonhaExpress Delivery

Obrigado por contribuir com o **CegonhaExpress Delivery**! Este guia irá ajudar você a entender como colaborar efetivamente com o projeto.

## 📋 Índice

- [Código de Conduta](#código-de-conduta)
- [Como Contribuir](#como-contribuir)
- [Padrões de Desenvolvimento](#padrões-de-desenvolvimento)
- [Estrutura de Branches](#estrutura-de-branches)
- [Padrões de Commit](#padrões-de-commit)
- [Pull Requests](#pull-requests)
- [Reportando Issues](#reportando-issues)
- [Configuração do Ambiente](#configuração-do-ambiente)

## 🌟 Código de Conduta

Este projeto segue um código de conduta baseado em respeito mútuo e colaboração acadêmica:

- **Respeite** diferentes níveis de conhecimento e experiência
- **Seja construtivo** em feedbacks e revisões de código
- **Mantenha** um ambiente de aprendizado positivo
- **Documente** suas contribuições de forma clara
- **Teste** seu código antes de submeter

## 🚀 Como Contribuir

### 1. Configuração Inicial

```bash
# Fork o repositório no GitHub
# Clone seu fork
git clone https://github.com/SEU_USUARIO/cegonha-express-delivery.git
cd cegonha-express-delivery

# Adicione o repositório original como upstream
git remote add upstream https://github.com/GabrielCoelho/cegonha-express-delivery.git

# Instale as dependências
./mvnw clean install
```

### 2. Mantenha seu Fork Atualizado

```bash
# Busque as últimas mudanças
git fetch upstream

# Atualize sua branch main
git checkout main
git merge upstream/main
git push origin main
```

## 🏗️ Padrões de Desenvolvimento

### Estrutura de Pacotes

```
src/main/java/com/cegonhaexpress/
├── controller/          # Controllers REST e MVC
├── service/            # Lógica de negócio
├── repository/         # Acesso a dados
├── model/             # Entidades JPA
├── dto/               # Data Transfer Objects
├── config/            # Configurações Spring
├── exception/         # Exceções customizadas
└── util/              # Classes utilitárias
```

### Convenções de Nomenclatura

**Classes:**
- **Controllers**: `NomeController` (ex: `EntregaController`)
- **Services**: `NomeService` (ex: `FreteService`)
- **Repositories**: `NomeRepository` (ex: `EncomendaRepository`)
- **DTOs**: `NomeDTO` (ex: `EnderecoDTO`)
- **Exceptions**: `NomeException` (ex: `CepInvalidoException`)

**Métodos:**
- **Controllers**: verbos HTTP + recurso (ex: `criarEncomenda()`, `buscarPorId()`)
- **Services**: ações de negócio (ex: `calcularFrete()`, `validarEndereco()`)
- **Repositories**: padrão Spring Data (ex: `findByStatus()`, `existsByCep()`)


## 🌳 Estrutura de Branches

### Branch Principal
- **`main`**: Branch de produção, sempre estável

### Branches de Desenvolvimento
- **`backend`** ou **`frontend`**: Branch de integração para desenvolvimento de cada parte
- **`feature/nome-da-funcionalidade`**: Novas funcionalidades
- **`bugfix/nome-do-bug`**: Correção de bugs
- **`hotfix/nome-do-hotfix`**: Correções urgentes
- **`docs/nome-da-documentacao`**: Atualizações de documentação

### Workflow de Branches

```bash
# Criar nova feature
git checkout backend
git pull upstream backend
git checkout -b feature/calculo-frete-expresso

# Desenvolver e commitar
git add .
git commit -m "feat: adiciona cálculo de frete expresso"

# Push e PR
git push origin feature/calculo-frete-expresso
# Abrir PR no GitHub: feature/calculo-frete-expresso -> backend
```

## 📝 Padrões de Commit

Utilizamos **Conventional Commits** para manter o histórico organizado:

### Formato
```
<tipo>(<escopo>): <descrição>

<corpo opcional>

<rodapé opcional>
```

### Tipos de Commit
- **`feat`**: Nova funcionalidade
- **`fix`**: Correção de bug
- **`docs`**: Documentação
- **`style`**: Formatação de código
- **`refactor`**: Refatoração sem mudança de funcionalidade
- **`test`**: Adição ou correção de testes
- **`chore`**: Tarefas de manutenção

### Exemplos
```bash
# Funcionalidade
git commit -m "feat(frete): adiciona cálculo de frete por peso"

# Correção
git commit -m "fix(api): corrige validação de CEP inválido"

# Documentação
git commit -m "docs(readme): atualiza instruções de instalação"

# Refatoração
git commit -m "refactor(service): extrai lógica de validação para classe utilitária"
```

## 🔄 Pull Requests

### Antes de Abrir um PR

1. **Certifique-se** que sua branch está atualizada com `backend` ou `frontend`
2. **Execute** todos os testes: `./mvnw test`
3. **Verifique** a cobertura de testes
4. **Documente** novas funcionalidades
5. **Teste** manualmente as mudanças

### Template de PR

```markdown
## 📋 Descrição
Descreva brevemente o que foi implementado/corrigido.

## 🔄 Tipo de Mudança
- [ ] Nova funcionalidade (feature)
- [ ] Correção de bug (bugfix)
- [ ] Documentação (docs)
- [ ] Refatoração (refactor)

## ✅ Checklist
- [ ] Código testado localmente
- [ ] Testes unitários adicionados/atualizados
- [ ] Documentação atualizada
- [ ] Segue os padrões de código do projeto
- [ ] PR tem título descritivo

## 🧪 Como Testar
1. Baixe a branch
2. Execute `./mvnw spring-boot:run`
3. Acesse `http://localhost:8080`
4. Teste o cenário: [descrever passos]

## 📸 Screenshots (se aplicável)
[Adicionar capturas de tela se houver mudanças na UI]
```

### Revisão de Código

**Para Revisores:**
- Verifique se o código segue os padrões estabelecidos
- Teste as funcionalidades localmente
- Deixe comentários construtivos
- Aprove apenas quando estiver satisfeito com a qualidade

**Para Autores:**
- Responda aos comentários de forma construtiva
- Faça as correções solicitadas
- Solicite nova revisão após mudanças significativas

## 🐛 Reportando Issues

### Template de Bug Report

```markdown
**Descrição do Bug**
Descrição clara e concisa do problema.

**Passos para Reproduzir**
1. Vá para '...'
2. Clique em '...'
3. Veja o erro

**Comportamento Esperado**
O que deveria acontecer.

**Screenshots**
Se aplicável, adicione screenshots.

**Ambiente:**
- OS: [ex: Windows 10]
- Browser: [ex: Chrome 91]
- Java Version: [ex: 17]
- Spring Boot Version: [ex: 3.1.0]
```

### Template de Feature Request

```markdown
**Funcionalidade Solicitada**
Descrição clara da funcionalidade desejada.

**Problema que Resolve**
Explique qual problema esta funcionalidade resolveria.

**Solução Proposta**
Descreva como você imagina que deveria funcionar.

**Alternativas Consideradas**
Outras soluções que você considerou.

**Contexto Adicional**
Qualquer outra informação relevante.
```

## ⚙️ Configuração do Ambiente

### Ferramentas Recomendadas

**IDEs:**
- IntelliJ IDEA
- Neovim
- Eclipse STS
- Visual Studio Code com extensões Java

**Extensões/Plugins:**
- Spring Boot Tools
- Lombok
- Git Integration
- Thymeleaf Support

### Configuração do IDE

**IntelliJ IDEA:**
1. Importe como projeto Maven
2. Configure o JDK 17+
3. Instale os plugins: Lombok, Spring Boot
4. Configure o estilo de código: `Settings > Editor > Code Style`

### Verificação da Configuração

```bash
# Verifique se tudo está funcionando
./mvnw clean test
./mvnw spring-boot:run

# Acesse: http://localhost:8080
# Verifique se a aplicação inicializa corretamente
```

## 📞 Suporte

**Dúvidas sobre contribuição:**
- Abra uma [Issue](https://github.com/GabrielCoelho/cegonha-express-delivery/issues) com a tag `question`
- Entre em contato com o maintainer: [@GabrielCoelho](https://github.com/GabrielCoelho)

**Problemas técnicos:**
- Verifique as [Issues existentes](https://github.com/GabrielCoelho/cegonha-express-delivery/issues)
- Consulte a [documentação do projeto](README.md)

---

**Obrigado por contribuir com o Cegonha Express! 🐣💙**
