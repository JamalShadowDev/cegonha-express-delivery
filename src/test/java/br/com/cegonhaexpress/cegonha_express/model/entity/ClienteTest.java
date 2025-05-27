package br.com.cegonhaexpress.cegonha_express.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para a entidade Cliente.
 *
 * <p>Demonstra testes de: - Validações Bean Validation - Métodos utilitários - Relacionamentos
 * bidirecionais - Construtores customizados
 *
 * @author Gabriel Coelho Soares
 */
@DisplayName("Cliente Entity Tests")
class ClienteTest {

  private Validator validator;
  private Cliente clienteValido;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    clienteValido =
        new Cliente("João Silva", "joao@email.com", "(11) 99999-9999", "123.456.789-00");
  }

  @Nested
  @DisplayName("Testes de Construção")
  class TestesConstrutores {

    @Test
    @DisplayName("Deve criar cliente com dados essenciais")
    void deveCriarClienteComDadosEssenciais() {
      System.out.println("\n=== TESTE: Criação de Cliente com Dados Essenciais ===");

      // Arrange & Act
      Cliente cliente = new Cliente("Ana Silva", "ana@email.com", "111.111.111-11");

      // Assert
      assertEquals("Ana Silva", cliente.getNome());
      assertEquals("ana@email.com", cliente.getEmail());
      assertEquals("111.111.111-11", cliente.getCpf());
      assertNull(cliente.getTelefone());
      assertNotNull(cliente.getEnderecos());
      assertTrue(cliente.getEnderecos().isEmpty());

      // Logs informativos
      System.out.println("✅ Cliente criado com sucesso!");
      System.out.println("📋 Dados do Cliente:");
      System.out.println("   Nome: " + cliente.getNome());
      System.out.println("   Email: " + cliente.getEmail());
      System.out.println("   CPF: " + cliente.getCpf());
      System.out.println(
          "   Telefone: "
              + (cliente.getTelefone() != null ? cliente.getTelefone() : "Não informado"));
      System.out.println("   Endereços cadastrados: " + cliente.getEnderecos().size());
      System.out.println(
          "   Lista de endereços inicializada: "
              + (cliente.getEnderecos() != null ? "SIM" : "NÃO"));
    }

    @Test
    @DisplayName("Deve criar cliente com dados completos")
    void deveCriarClienteComDadosCompletos() {
      System.out.println("\n=== TESTE: Criação de Cliente com Dados Completos ===");

      // Arrange & Act
      Cliente cliente =
          new Cliente("Carlos Silva", "carlos@email.com", "(11) 8888-8888", "222.222.222-22");

      // Assert
      assertEquals("Carlos Silva", cliente.getNome());
      assertEquals("carlos@email.com", cliente.getEmail());
      assertEquals("(11) 8888-8888", cliente.getTelefone());
      assertEquals("222.222.222-22", cliente.getCpf());
      assertNotNull(cliente.getEnderecos());

      // Logs informativos
      System.out.println("✅ Cliente completo criado com sucesso!");
      System.out.println("📋 Dados Completos do Cliente:");
      System.out.println("   Nome: " + cliente.getNome());
      System.out.println("   Email: " + cliente.getEmail());
      System.out.println("   Telefone: " + cliente.getTelefone());
      System.out.println("   CPF: " + cliente.getCpf());
      System.out.println("   CPF Formatado: " + cliente.getCpfFormatado());
      System.out.println("   Telefone Formatado: " + cliente.getTelefoneFormatado());
      System.out.println("   Dados completos: " + (cliente.isDadosCompletos() ? "SIM" : "NÃO"));
    }

    @Test
    @DisplayName("Deve inicializar lista de endereços vazia")
    void deveInicializarListaEnderecosVazia() {
      // Arrange & Act
      Cliente cliente = new Cliente("Pedro", "pedro@email.com", "333.333.333-33");

      // Assert
      assertNotNull(cliente.getEnderecos());
      assertTrue(cliente.getEnderecos().isEmpty());
      assertEquals(0, cliente.getEnderecos().size());
    }
  }

  @Nested
  @DisplayName("Testes de Validação")
  class TestesValidacao {

    @Test
    @DisplayName("Deve aceitar cliente válido")
    void deveAceitarClienteValido() {
      System.out.println("\n=== TESTE: Validação de Cliente Válido ===");

      // Act
      Set<ConstraintViolation<Cliente>> violations = validator.validate(clienteValido);

      // Assert
      assertTrue(violations.isEmpty());

      // Logs informativos
      System.out.println("✅ Cliente passou em todas as validações!");
      System.out.println("📋 Cliente Validado:");
      System.out.println("   Nome: " + clienteValido.getNome());
      System.out.println("   Email: " + clienteValido.getEmail());
      System.out.println("   Telefone: " + clienteValido.getTelefone());
      System.out.println("   CPF: " + clienteValido.getCpf());
      System.out.println("   Número de violações: " + violations.size());
      System.out.println("   Status validação: APROVADO ✓");
    }

    @Test
    @DisplayName("Deve rejeitar nome em branco")
    void deveRejeitarNomeEmBranco() {
      // Arrange
      Cliente cliente = new Cliente("", "joao@email.com", "123.456.789-00");

      // Act
      Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

      // Assert
      assertFalse(violations.isEmpty());
      assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    @DisplayName("Deve rejeitar email inválido")
    void deveRejeitarEmailInvalido() {
      // Arrange
      Cliente cliente = new Cliente("João", "email-invalido", "123.456.789-00");

      // Act
      Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

      // Assert
      assertFalse(violations.isEmpty());
      assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Deve rejeitar CPF com formato inválido")
    void deveRejeitarCpfComFormatoInvalido() {
      // Arrange
      Cliente cliente = new Cliente("João", "joao@email.com", "123.456");

      // Act
      Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

      // Assert
      assertFalse(violations.isEmpty());
      assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    @DisplayName("Deve rejeitar telefone com formato inválido")
    void deveRejeitarTelefoneComFormatoInvalido() {
      // Arrange
      Cliente cliente = new Cliente("João", "joao@email.com", "123", "123.456.789-00");

      // Act
      Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

      // Assert
      assertFalse(violations.isEmpty());
      assertTrue(
          violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("telefone")));
    }
  }

  @Nested
  @DisplayName("Testes de Métodos Utilitários")
  class TestesMetodosUtilitarios {

    @Test
    @DisplayName("Deve formatar CPF corretamente")
    void deveFormatarCpfCorretamente() {
      System.out.println("\n=== TESTE: Formatação de CPF ===");

      // Arrange
      Cliente cliente = new Cliente("João", "joao@email.com", "12345678900");

      // Act & Assert
      assertEquals("123.456.789-00", cliente.getCpfFormatado());

      // Logs informativos
      System.out.println("✅ Formatação de CPF funcionando corretamente!");
      System.out.println("📋 Detalhes da Formatação:");
      System.out.println("   CPF original: " + cliente.getCpf());
      System.out.println("   CPF limpo: " + cliente.getCpfLimpo());
      System.out.println("   CPF formatado: " + cliente.getCpfFormatado());
      System.out.println(
          "   Transformação: " + cliente.getCpf() + " → " + cliente.getCpfFormatado());
    }

    @Test
    @DisplayName("Deve retornar CPF limpo")
    void deveRetornarCpfLimpo() {
      // Arrange
      Cliente cliente = new Cliente("João", "joao@email.com", "123.456.789-00");

      // Act & Assert
      assertEquals("12345678900", cliente.getCpfLimpo());
    }

    @Test
    @DisplayName("Deve formatar telefone celular")
    void deveFormatarTelefoneCelular() {
      System.out.println("\n=== TESTE: Formatação de Telefone Celular ===");

      // Arrange
      Cliente cliente = new Cliente("João", "joao@email.com", "11999998888", "123.456.789-00");

      // Act & Assert
      assertEquals("(11) 99999-8888", cliente.getTelefoneFormatado());

      // Logs informativos
      System.out.println("✅ Formatação de telefone celular funcionando!");
      System.out.println("📋 Detalhes da Formatação:");
      System.out.println("   Telefone original: " + cliente.getTelefone());
      System.out.println("   Telefone limpo: " + cliente.getTelefoneLimpo());
      System.out.println("   Telefone formatado: " + cliente.getTelefoneFormatado());
      System.out.println("   Tipo detectado: CELULAR (11 dígitos)");
      System.out.println(
          "   Transformação: " + cliente.getTelefone() + " → " + cliente.getTelefoneFormatado());
    }

    @Test
    @DisplayName("Deve formatar telefone fixo")
    void deveFormatarTelefoneFixo() {
      // Arrange
      Cliente cliente = new Cliente("João", "joao@email.com", "1133334444", "123.456.789-00");

      // Act & Assert
      assertEquals("(11) 3333-4444", cliente.getTelefoneFormatado());
    }

    @Test
    @DisplayName("Deve validar dados completos")
    void deveValidarDadosCompletos() {
      // Act & Assert
      assertTrue(clienteValido.isDadosCompletos());

      // Teste com dados incompletos
      Cliente clienteIncompleto = new Cliente("", "joao@email.com", "123.456.789-00");
      assertFalse(clienteIncompleto.isDadosCompletos());
    }
  }

  @Nested
  @DisplayName("Testes de Relacionamento com Endereço")
  class TestesRelacionamento {

    private Endereco criarEnderecoValido() {
      return new Endereco("12345-678", "Rua das Flores", "100", "Centro", "São Paulo", UF.SP);
    }

    @Test
    @DisplayName("Deve adicionar endereço corretamente")
    void deveAdicionarEnderecoCorretamente() {
      System.out.println("\n=== TESTE: Adição de Endereço ao Cliente ===");

      // Arrange
      Endereco endereco = criarEnderecoValido();

      // Act
      clienteValido.adicionarEndereco(endereco);

      // Assert
      assertEquals(1, clienteValido.getEnderecos().size());
      assertEquals(clienteValido, endereco.getCliente());
      assertTrue(clienteValido.possuiEnderecos());

      // Logs informativos
      System.out.println("✅ Endereço adicionado com sucesso!");
      System.out.println("📋 Detalhes do Relacionamento:");
      System.out.println("   Cliente: " + clienteValido.getNome());
      System.out.println("   Endereços do cliente: " + clienteValido.getEnderecos().size());
      System.out.println("   Endereço adicionado: " + endereco.getEnderecoCompleto());
      System.out.println(
          "   Cliente do endereço: "
              + (endereco.getCliente() != null ? endereco.getCliente().getNome() : "Nenhum"));
      System.out.println(
          "   Relacionamento bidirecional: "
              + (clienteValido.equals(endereco.getCliente()) ? "CONFIRMADO ✓" : "FALHOU ✗"));
      System.out.println(
          "   Possui endereços: " + (clienteValido.possuiEnderecos() ? "SIM" : "NÃO"));
    }

    @Test
    @DisplayName("Deve remover endereço corretamente")
    void deveRemoverEnderecoCorretamente() {
      // Arrange
      Endereco endereco = criarEnderecoValido();
      clienteValido.adicionarEndereco(endereco);

      // Act
      clienteValido.removerEndereco(endereco);

      // Assert
      assertTrue(clienteValido.getEnderecos().isEmpty());
      assertNull(endereco.getCliente());
      assertFalse(clienteValido.possuiEnderecos());
    }

    @Test
    @DisplayName("Deve retornar endereço principal")
    void deveRetornarEnderecoPrincipal() {
      // Arrange
      Endereco endereco1 = criarEnderecoValido();
      Endereco endereco2 =
          new Endereco("87654-321", "Rua B", "200", "Vila", "Rio de Janeiro", UF.RJ);

      // Act
      clienteValido.adicionarEndereco(endereco1);
      clienteValido.adicionarEndereco(endereco2);

      // Assert
      assertEquals(endereco1, clienteValido.getEnderecoPrincipal());
    }

    @Test
    @DisplayName("Deve retornar null para endereço principal quando não há endereços")
    void deveRetornarNullParaEnderecoPrincipalQuandoNaoHaEnderecos() {
      // Act & Assert
      assertNull(clienteValido.getEnderecoPrincipal());
    }

    @Test
    @DisplayName("Não deve adicionar endereço nulo")
    void naoDeveAdicionarEnderecoNulo() {
      // Arrange
      int tamanhoInicial = clienteValido.getEnderecos().size();

      // Act
      clienteValido.adicionarEndereco(null);

      // Assert
      assertEquals(tamanhoInicial, clienteValido.getEnderecos().size());
    }
  }

  @Nested
  @DisplayName("Testes de Validação de Regex")
  class TestesRegex {

    @Test
    @DisplayName("Deve aceitar CPF formatado")
    void deveAceitarCpfFormatado() {
      System.out.println("\n=== TESTE: Validação de CPF Formatado ===");

      Cliente cliente = new Cliente("João", "joao@email.com", "123.456.789-00");
      Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

      assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("cpf")));

      // Logs informativos
      System.out.println("✅ CPF formatado aceito pela validação!");
      System.out.println("📋 Detalhes da Validação de CPF:");
      System.out.println("   CPF testado: " + cliente.getCpf());
      System.out.println("   Formato: COM máscara (000.000.000-00)");
      System.out.println(
          "   Violações de CPF: "
              + violations.stream()
                  .filter(v -> v.getPropertyPath().toString().equals("cpf"))
                  .count());
      System.out.println("   Status: APROVADO ✓");
    }

    @Test
    @DisplayName("Deve aceitar CPF sem formatação")
    void deveAceitarCpfSemFormatacao() {
      System.out.println("\n=== TESTE: Validação de CPF Sem Formatação ===");

      Cliente cliente = new Cliente("João", "joao@email.com", "12345678900");
      Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

      assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("cpf")));

      // Logs informativos
      System.out.println("✅ CPF sem formatação aceito pela validação!");
      System.out.println("📋 Detalhes da Validação de CPF:");
      System.out.println("   CPF testado: " + cliente.getCpf());
      System.out.println("   Formato: SEM máscara (00000000000)");
      System.out.println(
          "   Violações de CPF: "
              + violations.stream()
                  .filter(v -> v.getPropertyPath().toString().equals("cpf"))
                  .count());
      System.out.println("   Status: APROVADO ✓");
      System.out.println("   🔄 Ambos os formatos funcionam: COM e SEM máscara!");
    }

    @Test
    @DisplayName("Deve aceitar telefone celular formatado")
    void deveAceitarTelefoneCelularFormatado() {
      Cliente cliente = new Cliente("João", "joao@email.com", "(11) 99999-9999", "123.456.789-00");
      Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

      assertTrue(
          violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("telefone")));
    }

    @Test
    @DisplayName("Deve aceitar telefone fixo formatado")
    void deveAceitarTelefoneFixoFormatado() {
      Cliente cliente = new Cliente("João", "joao@email.com", "(11) 3333-4444", "123.456.789-00");
      Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

      assertTrue(
          violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("telefone")));
    }
  }
}
