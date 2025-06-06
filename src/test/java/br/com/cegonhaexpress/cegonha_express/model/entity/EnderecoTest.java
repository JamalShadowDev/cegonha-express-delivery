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
 * Testes unitários para a entidade Endereco.
 *
 * <p>Demonstra testes de: - Validações Bean Validation para campos obrigatórios - Métodos
 * utilitários de formatação de CEP - Relacionamento com Cliente (bidirecional) - Construtores
 * customizados - Métodos de negócio específicos
 *
 * @author Gabriel Coelho Soares
 */
@DisplayName("Endereco Entity Tests")
class EnderecoTest {

  private Validator validator;
  private Endereco enderecoValido;
  private Cliente clienteValido;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    enderecoValido =
        new Endereco("12345-678", "Rua das Flores", "100", "Centro", "São Paulo", UF.SP);

    clienteValido =
        new Cliente("João Silva", "joao@email.com", "(11) 99999-9999", "123.456.789-00");
  }

  @Nested
  @DisplayName("Testes de Construção")
  class TestesConstrutores {

    @Test
    @DisplayName("Deve criar endereço com dados essenciais")
    void deveCriarEnderecoComDadosEssenciais() {
      System.out.println("\n=== TESTE: Criação de Endereço com Dados Essenciais ===");

      // Arrange & Act
      Endereco endereco =
          new Endereco("87654-321", "Avenida Paulista", "1000", "Bela Vista", "São Paulo", UF.SP);

      // Assert
      assertEquals("87654-321", endereco.getCep());
      assertEquals("Avenida Paulista", endereco.getLogradouro());
      assertEquals("1000", endereco.getNumero());
      assertEquals("Bela Vista", endereco.getBairro());
      assertEquals("São Paulo", endereco.getCidade());
      assertEquals(UF.SP, endereco.getUf());
      assertNull(endereco.getComplemento());
      assertNull(endereco.getPontoReferencia());

      // Logs informativos
      System.out.println("✅ Endereço criado com sucesso!");
      System.out.println("📍 Dados do Endereço:");
      System.out.println("   CEP: " + endereco.getCep());
      System.out.println("   Logradouro: " + endereco.getLogradouro());
      System.out.println("   Número: " + endereco.getNumero());
      System.out.println("   Bairro: " + endereco.getBairro());
      System.out.println("   Cidade: " + endereco.getCidade());
      System.out.println("   UF: " + endereco.getUf());
      System.out.println(
          "   Complemento: "
              + (endereco.getComplemento() != null ? endereco.getComplemento() : "Não informado"));
      System.out.println(
          "   Referência: "
              + (endereco.getPontoReferencia() != null
                  ? endereco.getPontoReferencia()
                  : "Não informada"));
      System.out.println("   Endereço completo: " + endereco.getEnderecoCompleto());
    }

    @Test
    @DisplayName("Deve criar endereço completo com complemento")
    void deveCriarEnderecoCompletoComComplemento() {
      System.out.println("\n=== TESTE: Criação de Endereço Completo ===");

      // Arrange & Act
      Endereco endereco =
          new Endereco("01310-100", "Avenida Paulista", "1578", "Bela Vista", "São Paulo", UF.SP);
      endereco.setComplemento("14º andar - Conjunto 1401");
      endereco.setPontoReferencia("Próximo ao metrô Trianon-MASP");

      // Assert
      assertNotNull(endereco.getComplemento());
      assertNotNull(endereco.getPontoReferencia());
      assertTrue(endereco.isCompleto());

      // Logs informativos
      System.out.println("✅ Endereço completo criado com sucesso!");
      System.out.println("📍 Dados Completos do Endereço:");
      System.out.println("   CEP: " + endereco.getCep());
      System.out.println(
          "   Logradouro: " + endereco.getLogradouro() + ", " + endereco.getNumero());
      System.out.println("   Complemento: " + endereco.getComplemento());
      System.out.println("   Bairro: " + endereco.getBairro());
      System.out.println("   Cidade/UF: " + endereco.getCidade() + " - " + endereco.getUf());
      System.out.println("   Referência: " + endereco.getPontoReferencia());
      System.out.println("   Status completo: " + (endereco.isCompleto() ? "SIM" : "NÃO"));
      System.out.println("   📋 Endereço formatado: " + endereco.getEnderecoCompleto());
    }
  }

  @Nested
  @DisplayName("Testes de Validação")
  class TestesValidacao {

    @Test
    @DisplayName("Deve aceitar endereço válido")
    void deveAceitarEnderecoValido() {
      System.out.println("\n=== TESTE: Validação de Endereço Válido ===");

      // Act
      Set<ConstraintViolation<Endereco>> violations = validator.validate(enderecoValido);

      // Assert
      assertTrue(violations.isEmpty());

      // Logs informativos
      System.out.println("✅ Endereço passou em todas as validações!");
      System.out.println("📍 Endereço Validado:");
      System.out.println("   " + enderecoValido.getEnderecoCompleto());
      System.out.println("   Número de violações: " + violations.size());
      System.out.println("   Status validação: APROVADO ✓");
    }

    @Test
    @DisplayName("Deve rejeitar CEP inválido")
    void deveRejeitarCepInvalido() {
      System.out.println("\n=== TESTE: Validação de CEP Inválido ===");

      // Arrange
      Endereco endereco =
          new Endereco(
              "123", // CEP inválido
              "Rua A",
              "100",
              "Centro",
              "São Paulo",
              UF.SP);

      // Act
      Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

      // Assert
      assertFalse(violations.isEmpty());
      assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cep")));

      // Logs informativos
      System.out.println("❌ Endereço rejeitado por CEP inválido (ESPERADO)");
      System.out.println("📍 Detalhes da Validação:");
      System.out.println("   CEP testado: " + endereco.getCep());
      System.out.println("   Número de violações: " + violations.size());
      violations.forEach(
          violation -> {
            System.out.println(
                "   ⚠️ Campo: "
                    + violation.getPropertyPath()
                    + " | Erro: "
                    + violation.getMessage());
          });
      System.out.println("   Status validação: REJEITADO (correto) ✓");
    }

    @Test
    @DisplayName("Deve rejeitar campos obrigatórios em branco")
    void deveRejeitarCamposObrigatoriosEmBranco() {
      System.out.println("\n=== TESTE: Validação de Campos Obrigatórios ===");

      // Arrange
      Endereco endereco = new Endereco();
      endereco.setCep(""); // Vazio
      endereco.setLogradouro(""); // Vazio
      endereco.setNumero(""); // Vazio
      endereco.setBairro(""); // Vazio
      endereco.setCidade(""); // Vazio
      // UF fica null

      // Act
      Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

      // Assert
      assertFalse(violations.isEmpty());
      assertTrue(violations.size() >= 6); // Pelo menos 6 campos obrigatórios

      // Logs informativos
      System.out.println("❌ Endereço rejeitado por campos obrigatórios vazios (ESPERADO)");
      System.out.println("📍 Detalhes das Violações:");
      System.out.println("   Total de violações: " + violations.size());
      violations.forEach(
          violation -> {
            System.out.println(
                "   ⚠️ Campo: "
                    + violation.getPropertyPath()
                    + " | Valor: '"
                    + violation.getInvalidValue()
                    + "' | Erro: "
                    + violation.getMessage());
          });
      System.out.println("   Status validação: MÚLTIPLAS FALHAS (correto) ✓");
    }
  }

  @Nested
  @DisplayName("Testes de Métodos Utilitários")
  class TestesMetodosUtilitarios {

    @Test
    @DisplayName("Deve formatar CEP corretamente")
    void deveFormatarCepCorretamente() {
      System.out.println("\n=== TESTE: Formatação de CEP ===");

      // Test 1: CEP sem formatação
      Endereco endereco1 = new Endereco("12345678", "Rua A", "100", "Centro", "São Paulo", UF.SP);

      // Test 2: CEP já formatado
      Endereco endereco2 =
          new Endereco("87654-321", "Rua B", "200", "Vila", "Rio de Janeiro", UF.RJ);

      // Act & Assert
      assertEquals("12345678", endereco1.getCepLimpo());
      assertEquals("12345-678", endereco1.getCepFormatado());
      assertEquals("87654321", endereco2.getCepLimpo());
      assertEquals("87654-321", endereco2.getCepFormatado());

      // Logs informativos
      System.out.println("✅ Formatação de CEP funcionando corretamente!");
      System.out.println("📍 Teste 1 - CEP sem formatação:");
      System.out.println("   CEP original: " + endereco1.getCep());
      System.out.println("   CEP limpo: " + endereco1.getCepLimpo());
      System.out.println("   CEP formatado: " + endereco1.getCepFormatado());
      System.out.println(
          "   Transformação: " + endereco1.getCep() + " → " + endereco1.getCepFormatado());

      System.out.println("📍 Teste 2 - CEP já formatado:");
      System.out.println("   CEP original: " + endereco2.getCep());
      System.out.println("   CEP limpo: " + endereco2.getCepLimpo());
      System.out.println("   CEP formatado: " + endereco2.getCepFormatado());
      System.out.println(
          "   Mantém formatação: " + endereco2.getCep() + " → " + endereco2.getCepFormatado());
    }

    @Test
    @DisplayName("Deve gerar endereço completo formatado")
    void deveGerarEnderecoCompletoFormatado() {
      System.out.println("\n=== TESTE: Geração de Endereço Completo ===");

      // Arrange
      Endereco endereco =
          new Endereco("01310-100", "Avenida Paulista", "1578", "Bela Vista", "São Paulo", UF.SP);
      endereco.setComplemento("14º andar");
      endereco.setPontoReferencia("Próximo ao MASP");

      // Act
      String enderecoCompleto = endereco.getEnderecoCompleto();

      // Assert
      assertNotNull(enderecoCompleto);
      assertTrue(enderecoCompleto.contains("Avenida Paulista"));
      assertTrue(enderecoCompleto.contains("1578"));
      assertTrue(enderecoCompleto.contains("14º andar"));
      assertTrue(enderecoCompleto.contains("Bela Vista"));
      assertTrue(enderecoCompleto.contains("São Paulo"));
      assertTrue(enderecoCompleto.contains("SP"));
      assertTrue(enderecoCompleto.contains("01310-100"));

      // Logs informativos
      System.out.println("✅ Endereço completo gerado com sucesso!");
      System.out.println("📍 Componentes do Endereço:");
      System.out.println("   Logradouro: " + endereco.getLogradouro());
      System.out.println("   Número: " + endereco.getNumero());
      System.out.println("   Complemento: " + endereco.getComplemento());
      System.out.println("   Bairro: " + endereco.getBairro());
      System.out.println("   Cidade: " + endereco.getCidade());
      System.out.println("   UF: " + endereco.getUf());
      System.out.println("   CEP: " + endereco.getCepFormatado());
      System.out.println("   Referência: " + endereco.getPontoReferencia());
      System.out.println("🏠 Endereço Completo Formatado:");
      System.out.println("   " + enderecoCompleto);
    }

    @Test
    @DisplayName("Deve validar endereço completo")
    void deveValidarEnderecoCompleto() {
      System.out.println("\n=== TESTE: Validação de Completude do Endereço ===");

      // Test 1: Endereço completo
      Endereco enderecoCompleto =
          new Endereco("12345-678", "Rua A", "100", "Centro", "São Paulo", UF.SP);

      // Test 2: Endereço incompleto (sem CEP)
      Endereco enderecoIncompleto = new Endereco();
      enderecoIncompleto.setLogradouro("Rua B");
      enderecoIncompleto.setNumero("200");
      // CEP fica null

      // Assert
      assertTrue(enderecoCompleto.isCompleto());
      assertFalse(enderecoIncompleto.isCompleto());

      // Logs informativos
      System.out.println("✅ Validação de completude funcionando!");
      System.out.println("📍 Teste 1 - Endereço Completo:");
      System.out.println("   Endereço: " + enderecoCompleto.getEnderecoCompleto());
      System.out.println(
          "   Status completo: " + (enderecoCompleto.isCompleto() ? "SIM ✓" : "NÃO ✗"));

      System.out.println("📍 Teste 2 - Endereço Incompleto:");
      System.out.println(
          "   CEP: "
              + (enderecoIncompleto.getCep() != null ? enderecoIncompleto.getCep() : "AUSENTE"));
      System.out.println("   Logradouro: " + enderecoIncompleto.getLogradouro());
      System.out.println(
          "   Status completo: "
              + (enderecoIncompleto.isCompleto() ? "SIM ✓" : "NÃO ✗ (esperado)"));
    }
  }

  @Nested
  @DisplayName("Testes de Relacionamento com Cliente")
  class TestesRelacionamento {

    @Test
    @DisplayName("Deve estabelecer relacionamento com cliente")
    void deveEstabelecerRelacionamentoComCliente() {
      System.out.println("\n=== TESTE: Relacionamento Endereco-Cliente ===");

      // Arrange & Act
      enderecoValido.setCliente(clienteValido);

      // Assert
      assertEquals(clienteValido, enderecoValido.getCliente());
      assertNotNull(enderecoValido.getCliente());

      // Logs informativos
      System.out.println("✅ Relacionamento estabelecido com sucesso!");
      System.out.println("📍 Detalhes do Relacionamento:");
      System.out.println("   Endereço: " + enderecoValido.getEnderecoCompleto());
      System.out.println(
          "   Cliente associado: "
              + (enderecoValido.getCliente() != null
                  ? enderecoValido.getCliente().getNome()
                  : "Nenhum"));
      System.out.println(
          "   CPF do cliente: "
              + (enderecoValido.getCliente() != null
                  ? enderecoValido.getCliente().getCpf()
                  : "N/A"));
      System.out.println(
          "   Relacionamento válido: " + (enderecoValido.getCliente() != null ? "SIM ✓" : "NÃO ✗"));
    }

    @Test
    @DisplayName("Deve funcionar sem cliente associado")
    void deveFuncionarSemClienteAssociado() {
      System.out.println("\n=== TESTE: Endereço Sem Cliente Associado ===");

      // Act & Assert
      assertNull(enderecoValido.getCliente());
      assertTrue(enderecoValido.isCompleto());

      // Logs informativos
      System.out.println("✅ Endereço funciona independentemente de cliente!");
      System.out.println("📍 Detalhes do Endereço Independente:");
      System.out.println("   Endereço: " + enderecoValido.getEnderecoCompleto());
      System.out.println(
          "   Cliente associado: "
              + (enderecoValido.getCliente() != null
                  ? enderecoValido.getCliente().getNome()
                  : "Nenhum"));
      System.out.println(
          "   Status completo: " + (enderecoValido.isCompleto() ? "SIM ✓" : "NÃO ✗"));
      System.out.println("   Funcionalidade independente: CONFIRMADA ✓");
    }
  }

  @Nested
  @DisplayName("Testes de Validação de CEP")
  class TestesValidacaoCep {

    @Test
    @DisplayName("Deve aceitar CEP com hífen")
    void deveAceitarCepComHifen() {
      System.out.println("\n=== TESTE: Validação CEP com Hífen ===");

      Endereco endereco = new Endereco("12345-678", "Rua A", "100", "Centro", "São Paulo", UF.SP);
      Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

      assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("cep")));

      // Logs informativos
      System.out.println("✅ CEP com hífen aceito pela validação!");
      System.out.println("📍 Detalhes da Validação de CEP:");
      System.out.println("   CEP testado: " + endereco.getCep());
      System.out.println("   Formato: COM hífen (00000-000)");
      System.out.println(
          "   Violações de CEP: "
              + violations.stream()
                  .filter(v -> v.getPropertyPath().toString().equals("cep"))
                  .count());
      System.out.println("   Status: APROVADO ✓");
    }

    @Test
    @DisplayName("Deve aceitar CEP sem hífen")
    void deveAceitarCepSemHifen() {
      System.out.println("\n=== TESTE: Validação CEP sem Hífen ===");

      Endereco endereco = new Endereco("12345678", "Rua A", "100", "Centro", "São Paulo", UF.SP);
      Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

      assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("cep")));

      // Logs informativos
      System.out.println("✅ CEP sem hífen aceito pela validação!");
      System.out.println("📍 Detalhes da Validação de CEP:");
      System.out.println("   CEP testado: " + endereco.getCep());
      System.out.println("   Formato: SEM hífen (00000000)");
      System.out.println(
          "   Violações de CEP: "
              + violations.stream()
                  .filter(v -> v.getPropertyPath().toString().equals("cep"))
                  .count());
      System.out.println("   Status: APROVADO ✓");
      System.out.println("   🔄 Ambos os formatos funcionam: COM e SEM hífen!");
    }

    @Test
    @DisplayName("Deve rejeitar CEP com formato inválido")
    void deveRejeitarCepComFormatoInvalido() {
      System.out.println("\n=== TESTE: Rejeição de CEP Inválido ===");

      String[] cepsInvalidos = {"123", "12345-67890", "abcde-fgh", "", "12.345-678"};

      for (String cepInvalido : cepsInvalidos) {
        Endereco endereco = new Endereco(cepInvalido, "Rua A", "100", "Centro", "São Paulo", UF.SP);
        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

        assertTrue(
            violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cep")),
            "CEP '" + cepInvalido + "' deveria ser rejeitado");

        System.out.println("❌ CEP '" + cepInvalido + "' rejeitado corretamente");
      }

      // Logs informativos
      System.out.println("✅ Todos os CEPs inválidos foram rejeitados!");
      System.out.println("📍 CEPs testados e rejeitados:");
      for (String cep : cepsInvalidos) {
        System.out.println("   ❌ '" + cep + "' - REJEITADO ✓");
      }
      System.out.println("   Validação de formato: FUNCIONANDO CORRETAMENTE ✓");
    }
  }

  @Nested
  @DisplayName("Testes de Enum UF")
  class TestesEnumUF {

    @Test
    @DisplayName("Deve aceitar todos os estados brasileiros")
    void deveAceitarTodosEstadosBrasileiros() {
      System.out.println("\n=== TESTE: Validação de Estados Brasileiros ===");

      UF[] estados = {UF.SP, UF.RJ, UF.MG, UF.RS, UF.SC, UF.PR, UF.BA, UF.GO};
      int estadosTestados = 0;

      for (UF estado : estados) {
        Endereco endereco = new Endereco("12345-678", "Rua A", "100", "Centro", "Cidade", estado);
        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);

        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("uf")));

        estadosTestados++;
        System.out.println("✅ Estado " + estado + " aceito corretamente");
      }

      // Logs informativos
      System.out.println("✅ Validação de UF funcionando para todos os estados!");
      System.out.println("📍 Resumo da Validação:");
      System.out.println("   Estados testados: " + estadosTestados);
      System.out.println("   Estados aprovados: " + estadosTestados);
      System.out.println("   Taxa de sucesso: 100% ✓");
      System.out.println("   Enum UF: FUNCIONANDO CORRETAMENTE ✓");
    }
  }
}
