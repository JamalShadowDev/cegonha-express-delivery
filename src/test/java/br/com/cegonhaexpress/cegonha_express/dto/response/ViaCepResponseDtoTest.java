package br.com.cegonhaexpress.cegonha_express.dto.response;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para ViaCepResponseDto.
 *
 * <p>Testa mapeamento JSON, validações e métodos utilitários do DTO.
 *
 * @author Gabriel Coelho Soares
 */
@DisplayName("ViaCepResponseDto - Testes de Mapeamento e Validação")
class ViaCepResponseDtoTest {

  private ObjectMapper objectMapper;
  private ViaCepResponseDto dtoValido;
  private ViaCepResponseDto dtoComErro;

  @BeforeEach
  void setUp() {
    System.out.println("\n=== CONFIGURANDO CENÁRIO DE TESTE DTO ===");

    objectMapper = new ObjectMapper();

    // DTO com dados válidos
    dtoValido = new ViaCepResponseDto();
    dtoValido.setCep("01001-000");
    dtoValido.setLogradouro("Praça da Sé");
    dtoValido.setComplemento("lado ímpar");
    dtoValido.setBairro("Sé");
    dtoValido.setLocalidade("São Paulo");
    dtoValido.setUf("SP");
    dtoValido.setIbge("3550308");
    dtoValido.setDdd("11");
    dtoValido.setErro(null);

    // DTO com erro
    dtoComErro = new ViaCepResponseDto();
    dtoComErro.setErro(true);

    System.out.println("✅ DTOs de teste configurados");
    System.out.println("📍 DTO válido: " + dtoValido.getLocalidade());
    System.out.println("📍 DTO erro: erro = " + dtoComErro.getErro());
  }

  @Test
  @DisplayName("Deve mapear JSON de sucesso corretamente")
  void deveMappearJsonDeSucessoCorretamente() throws Exception {
    System.out.println("\n🧪 TESTE: Mapeamento JSON de sucesso");

    // Arrange
    String jsonSucesso =
        """
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
        """;

    System.out.println("📋 JSON de entrada:");
    System.out.println(jsonSucesso);

    // Act
    ViaCepResponseDto resultado = objectMapper.readValue(jsonSucesso, ViaCepResponseDto.class);

    // Assert
    System.out.println("\n📊 Verificando mapeamento:");

    assertNotNull(resultado);
    System.out.println("✅ DTO criado com sucesso");

    assertEquals("01001-000", resultado.getCep());
    System.out.println("✅ CEP mapeado: " + resultado.getCep());

    assertEquals("Praça da Sé", resultado.getLogradouro());
    System.out.println("✅ Logradouro mapeado: " + resultado.getLogradouro());

    assertEquals("São Paulo", resultado.getLocalidade());
    System.out.println("✅ Localidade mapeada: " + resultado.getLocalidade());

    assertEquals("SP", resultado.getUf());
    System.out.println("✅ UF mapeada: " + resultado.getUf());

    assertEquals("Sé", resultado.getBairro());
    System.out.println("✅ Bairro mapeado: " + resultado.getBairro());

    assertEquals("3550308", resultado.getIbge());
    System.out.println("✅ IBGE mapeado: " + resultado.getIbge());

    assertEquals("11", resultado.getDdd());
    System.out.println("✅ DDD mapeado: " + resultado.getDdd());

    assertFalse(resultado.isErro());
    System.out.println("✅ Sem erro detectado");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve mapear JSON de erro corretamente")
  void deveMappearJsonDeErroCorretamente() throws Exception {
    System.out.println("\n🧪 TESTE: Mapeamento JSON de erro");

    // Arrange
    String jsonErro =
        """
        {
          "erro": true
        }
        """;

    System.out.println("📋 JSON de entrada:");
    System.out.println(jsonErro);

    // Act
    ViaCepResponseDto resultado = objectMapper.readValue(jsonErro, ViaCepResponseDto.class);

    // Assert
    System.out.println("\n📊 Verificando mapeamento de erro:");

    assertNotNull(resultado);
    System.out.println("✅ DTO criado com sucesso");

    assertTrue(resultado.isErro());
    System.out.println("✅ Erro detectado corretamente");

    assertNull(resultado.getCep());
    System.out.println("✅ CEP é null (esperado para erro)");

    assertNull(resultado.getLocalidade());
    System.out.println("✅ Localidade é null (esperado para erro)");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve detectar erro corretamente")
  void deveDetectarErroCorretamente() {
    System.out.println("\n🧪 TESTE: Detecção de erro");

    System.out.println("📋 Testando diferentes cenários de erro:");

    // Cenário 1: Sem erro
    ViaCepResponseDto semErro = new ViaCepResponseDto();
    semErro.setErro(null);
    assertFalse(semErro.isErro());
    System.out.println("✅ Erro null = false (sem erro)");

    // Cenário 2: Erro false explícito
    ViaCepResponseDto erroFalse = new ViaCepResponseDto();
    erroFalse.setErro(false);
    assertFalse(erroFalse.isErro());
    System.out.println("✅ Erro false = false (sem erro)");

    // Cenário 3: Erro true
    ViaCepResponseDto erroTrue = new ViaCepResponseDto();
    erroTrue.setErro(true);
    assertTrue(erroTrue.isErro());
    System.out.println("✅ Erro true = true (com erro)");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve validar resposta corretamente")
  void deveValidarRespostaCorretamente() {
    System.out.println("\n🧪 TESTE: Validação de resposta");

    System.out.println("📋 Testando diferentes cenários de validação:");

    // Cenário 1: Resposta válida completa
    assertTrue(dtoValido.isValidResponse());
    System.out.println("✅ Resposta completa é válida");

    // Cenário 2: Resposta com erro
    assertFalse(dtoComErro.isValidResponse());
    System.out.println("✅ Resposta com erro é inválida");

    // Cenário 3: Resposta sem CEP
    ViaCepResponseDto semCep = new ViaCepResponseDto();
    semCep.setLocalidade("São Paulo");
    semCep.setUf("SP");
    assertFalse(semCep.isValidResponse());
    System.out.println("✅ Resposta sem CEP é inválida");

    // Cenário 4: Resposta sem localidade
    ViaCepResponseDto semLocalidade = new ViaCepResponseDto();
    semLocalidade.setCep("01001-000");
    semLocalidade.setUf("SP");
    assertFalse(semLocalidade.isValidResponse());
    System.out.println("✅ Resposta sem localidade é inválida");

    // Cenário 5: Resposta sem UF
    ViaCepResponseDto semUf = new ViaCepResponseDto();
    semUf.setCep("01001-000");
    semUf.setLocalidade("São Paulo");
    assertFalse(semUf.isValidResponse());
    System.out.println("✅ Resposta sem UF é inválida");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve identificar CEP encontrado")
  void deveIdentificarCepEncontrado() {
    System.out.println("\n🧪 TESTE: Identificação de CEP encontrado");

    System.out.println("📋 Testando diferentes cenários:");

    // Cenário 1: CEP encontrado (resposta válida)
    assertTrue(dtoValido.isCepEncontrado());
    System.out.println("✅ CEP válido identificado como encontrado");

    // Cenário 2: CEP não encontrado (erro)
    assertFalse(dtoComErro.isCepEncontrado());
    System.out.println("✅ CEP com erro identificado como não encontrado");

    // Cenário 3: Resposta inválida
    ViaCepResponseDto respostaInvalida = new ViaCepResponseDto();
    assertFalse(respostaInvalida.isCepEncontrado());
    System.out.println("✅ Resposta inválida identificada como CEP não encontrado");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve verificar endereço completo")
  void deveVerificarEnderecoCompleto() {
    System.out.println("\n🧪 TESTE: Verificação de endereço completo");

    System.out.println("📋 Testando completude de endereços:");

    // Cenário 1: Endereço completo
    assertTrue(dtoValido.isEnderecoCompleto());
    System.out.println("✅ Endereço completo identificado corretamente");

    // Cenário 2: Endereço sem logradouro
    ViaCepResponseDto semLogradouro = new ViaCepResponseDto();
    semLogradouro.setCep("01001-000");
    semLogradouro.setLocalidade("São Paulo");
    semLogradouro.setUf("SP");
    semLogradouro.setBairro("Centro");
    // logradouro fica null

    assertFalse(semLogradouro.isEnderecoCompleto());
    System.out.println("✅ Endereço sem logradouro identificado como incompleto");

    // Cenário 3: Endereço sem bairro
    ViaCepResponseDto semBairro = new ViaCepResponseDto();
    semBairro.setCep("01001-000");
    semBairro.setLocalidade("São Paulo");
    semBairro.setUf("SP");
    semBairro.setLogradouro("Praça da Sé");
    // bairro fica null

    assertFalse(semBairro.isEnderecoCompleto());
    System.out.println("✅ Endereço sem bairro identificado como incompleto");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve retornar cidade corretamente")
  void deveRetornarCidadeCorretamente() {
    System.out.println("\n🧪 TESTE: Método getCidade()");

    System.out.println("📋 Testando alias getCidade():");

    // Test
    assertEquals("São Paulo", dtoValido.getCidade());
    System.out.println("✅ getCidade() retorna localidade: " + dtoValido.getCidade());

    assertEquals(dtoValido.getLocalidade(), dtoValido.getCidade());
    System.out.println("✅ getCidade() é igual a getLocalidade()");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve limpar CEP corretamente")
  void deveLimparCepCorretamente() {
    System.out.println("\n🧪 TESTE: Limpeza de CEP");

    System.out.println("📋 Testando limpeza de CEP:");

    // CEP formatado
    assertEquals("01001000", dtoValido.getCepLimpo());
    System.out.println("✅ CEP '01001-000' limpo para: " + dtoValido.getCepLimpo());

    // CEP null
    ViaCepResponseDto dtoSemCep = new ViaCepResponseDto();
    assertNull(dtoSemCep.getCepLimpo());
    System.out.println("✅ CEP null retorna null");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve converter DDD para numérico")
  void deveConverterDddParaNumerico() {
    System.out.println("\n🧪 TESTE: Conversão DDD numérico");

    System.out.println("📋 Testando conversão de DDD:");

    // DDD válido
    assertEquals(11, dtoValido.getDddNumerico());
    System.out.println("✅ DDD '11' convertido para: " + dtoValido.getDddNumerico());

    // DDD inválido
    ViaCepResponseDto dtoComDddInvalido = new ViaCepResponseDto();
    dtoComDddInvalido.setDdd("XX");
    assertNull(dtoComDddInvalido.getDddNumerico());
    System.out.println("✅ DDD inválido 'XX' retorna null");

    // DDD null
    ViaCepResponseDto dtoSemDdd = new ViaCepResponseDto();
    assertNull(dtoSemDdd.getDddNumerico());
    System.out.println("✅ DDD null retorna null");

    // DDD vazio
    ViaCepResponseDto dtoComDddVazio = new ViaCepResponseDto();
    dtoComDddVazio.setDdd("");
    assertNull(dtoComDddVazio.getDddNumerico());
    System.out.println("✅ DDD vazio retorna null");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve normalizar campos corretamente")
  void deveNormalizarCamposCorretamente() {
    System.out.println("\n🧪 TESTE: Normalização de campos");

    // Arrange
    ViaCepResponseDto dtoComCamposVazios = new ViaCepResponseDto();
    dtoComCamposVazios.setCep("01001-000");
    dtoComCamposVazios.setLogradouro(""); // String vazia
    dtoComCamposVazios.setComplemento("   "); // Só espaços
    dtoComCamposVazios.setBairro("Centro");
    dtoComCamposVazios.setLocalidade("São Paulo");
    dtoComCamposVazios.setUf("SP");
    dtoComCamposVazios.setDdd("");

    System.out.println("📋 Antes da normalização:");
    System.out.println("   Logradouro: '" + dtoComCamposVazios.getLogradouro() + "'");
    System.out.println("   Complemento: '" + dtoComCamposVazios.getComplemento() + "'");
    System.out.println("   DDD: '" + dtoComCamposVazios.getDdd() + "'");

    // Act
    dtoComCamposVazios.normalizeFields();

    // Assert
    System.out.println("\n📊 Após normalização:");

    assertNull(dtoComCamposVazios.getLogradouro());
    System.out.println("✅ Logradouro vazio normalizado para null");

    assertNull(dtoComCamposVazios.getComplemento());
    System.out.println("✅ Complemento com espaços normalizado para null");

    assertNull(dtoComCamposVazios.getDdd());
    System.out.println("✅ DDD vazio normalizado para null");

    assertEquals("Centro", dtoComCamposVazios.getBairro());
    System.out.println("✅ Bairro válido mantido: " + dtoComCamposVazios.getBairro());

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve gerar toString personalizado")
  void deveGerarToStringPersonalizado() {
    System.out.println("\n🧪 TESTE: Método toString()");

    System.out.println("📋 Testando toString() personalizado:");

    // DTO válido
    String toStringValido = dtoValido.toString();
    assertTrue(toStringValido.contains("01001-000"));
    assertTrue(toStringValido.contains("São Paulo"));
    assertTrue(toStringValido.contains("SP"));
    System.out.println("✅ toString() DTO válido: " + toStringValido);

    // DTO com erro
    String toStringErro = dtoComErro.toString();
    assertTrue(toStringErro.contains("erro=true"));
    System.out.println("✅ toString() DTO erro: " + toStringErro);

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve gerar resumo corretamente")
  void deveGerarResumoCorretamente() {
    System.out.println("\n🧪 TESTE: Método getResumo()");

    System.out.println("📋 Testando geração de resumo:");

    // DTO válido
    String resumoValido = dtoValido.getResumo();
    assertEquals("São Paulo, SP, 01001-000", resumoValido);
    System.out.println("✅ Resumo DTO válido: " + resumoValido);

    // DTO com erro
    String resumoErro = dtoComErro.getResumo();
    assertEquals("CEP não encontrado", resumoErro);
    System.out.println("✅ Resumo DTO erro: " + resumoErro);

    // DTO inválido
    ViaCepResponseDto dtoInvalido = new ViaCepResponseDto();
    String resumoInvalido = dtoInvalido.getResumo();
    assertEquals("Resposta inválida da API", resumoInvalido);
    System.out.println("✅ Resumo DTO inválido: " + resumoInvalido);

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve serializar para JSON corretamente")
  void deveSerializarParaJsonCorretamente() throws Exception {
    System.out.println("\n🧪 TESTE: Serialização para JSON");

    // Act
    String json = objectMapper.writeValueAsString(dtoValido);

    System.out.println("📋 JSON serializado:");
    System.out.println(json);

    // Assert
    assertTrue(json.contains("\"cep\":\"01001-000\""));
    System.out.println("✅ CEP presente no JSON");

    assertTrue(json.contains("\"localidade\":\"São Paulo\""));
    System.out.println("✅ Localidade presente no JSON");

    assertTrue(json.contains("\"uf\":\"SP\""));
    System.out.println("✅ UF presente no JSON");

    assertFalse(json.contains("\"erro\""));
    System.out.println("✅ Campo erro null não serializado (JsonInclude.NON_NULL)");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }
}
