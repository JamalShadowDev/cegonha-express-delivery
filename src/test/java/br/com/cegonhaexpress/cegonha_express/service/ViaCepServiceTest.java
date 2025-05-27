package br.com.cegonhaexpress.cegonha_express.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.cegonhaexpress.cegonha_express.dto.ViaCepResponseDto;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Testes unitários para ViaCepService.
 *
 * <p>Testa integração com API ViaCEP incluindo: - Busca de CEP válido - Tratamento de CEP inválido
 * - Conversão para entidade Endereco - Tratamento de erros de rede
 *
 * @author Gabriel Coelho Soares
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ViaCepService - Testes de Integração com API")
class ViaCepServiceTest {

  @Mock private RestTemplate restTemplate;

  @InjectMocks private ViaCepService viaCepService;

  private ViaCepResponseDto responseValida;
  private ViaCepResponseDto responseComErro;

  @BeforeEach
  void setUp() {
    System.out.println("\n=== CONFIGURANDO CENÁRIO DE TESTE ===");

    // Resposta válida simulada da API ViaCEP
    responseValida = new ViaCepResponseDto();
    responseValida.setCep("01001-000");
    responseValida.setLogradouro("Praça da Sé");
    responseValida.setComplemento("lado ímpar");
    responseValida.setBairro("Sé");
    responseValida.setLocalidade("São Paulo");
    responseValida.setUf("SP");
    responseValida.setIbge("3550308");
    responseValida.setDdd("11");
    responseValida.setErro(null);

    // Resposta de erro simulada
    responseComErro = new ViaCepResponseDto();
    responseComErro.setErro(true);

    System.out.println("✅ Mocks configurados com sucesso");
    System.out.println("📍 CEP teste válido: " + responseValida.getCep());
    System.out.println("📍 Localidade teste: " + responseValida.getLocalidade());
  }

  @Test
  @DisplayName("Deve buscar endereço com CEP válido")
  void deveBuscarEnderecoComCepValido() {
    System.out.println("\n🧪 TESTE: Busca de CEP válido");

    // Arrange
    String cepTeste = "01001000";
    String expectedUrl = "https://viacep.com.br/ws/01001000/json/";

    when(restTemplate.getForObject(expectedUrl, ViaCepResponseDto.class))
        .thenReturn(responseValida);

    System.out.println("📋 Configuração:");
    System.out.println("   CEP entrada: " + cepTeste);
    System.out.println("   URL esperada: " + expectedUrl);

    // Act
    ViaCepResponseDto resultado = viaCepService.buscarEnderecoPorCep(cepTeste);

    // Assert
    System.out.println("\n📊 Verificando resultados:");

    assertNotNull(resultado, "Resultado não deveria ser null");
    System.out.println("✅ Resultado não é null");

    assertEquals("01001-000", resultado.getCep());
    System.out.println("✅ CEP correto: " + resultado.getCep());

    assertEquals("Praça da Sé", resultado.getLogradouro());
    System.out.println("✅ Logradouro correto: " + resultado.getLogradouro());

    assertEquals("São Paulo", resultado.getLocalidade());
    System.out.println("✅ Localidade correta: " + resultado.getLocalidade());

    assertEquals("SP", resultado.getUf());
    System.out.println("✅ UF correto: " + resultado.getUf());

    assertFalse(resultado.isErro());
    System.out.println("✅ Não há erro na resposta");

    // Verify
    verify(restTemplate, times(1)).getForObject(expectedUrl, ViaCepResponseDto.class);
    System.out.println("✅ RestTemplate chamado exatamente 1 vez");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve retornar null para CEP inválido")
  void deveRetornarNullParaCepInvalido() {
    System.out.println("\n🧪 TESTE: CEP com formato inválido");

    // Arrange
    String[] cepsInvalidos = {"123", "12345678901", "abcd1234", "", null};

    for (String cepInvalido : cepsInvalidos) {
      System.out.println("\n📋 Testando CEP inválido: " + cepInvalido);

      // Act
      ViaCepResponseDto resultado = viaCepService.buscarEnderecoPorCep(cepInvalido);

      // Assert
      assertNull(resultado, "CEP inválido deveria retornar null");
      System.out.println("✅ CEP inválido retornou null corretamente");
    }

    // Verify - RestTemplate não deveria ser chamado para CEPs inválidos
    verify(restTemplate, never()).getForObject(anyString(), eq(ViaCepResponseDto.class));
    System.out.println("✅ RestTemplate não foi chamado para CEPs inválidos");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve tratar CEP não encontrado na API")
  void deveTratarCepNaoEncontrado() {
    System.out.println("\n🧪 TESTE: CEP não encontrado na base ViaCEP");

    // Arrange
    String cepInexistente = "99999999";
    String expectedUrl = "https://viacep.com.br/ws/99999999/json/";

    when(restTemplate.getForObject(expectedUrl, ViaCepResponseDto.class))
        .thenReturn(responseComErro);

    System.out.println("📋 Configuração:");
    System.out.println("   CEP inexistente: " + cepInexistente);
    System.out.println("   Resposta simulada: {\"erro\": true}");

    // Act
    ViaCepResponseDto resultado = viaCepService.buscarEnderecoPorCep(cepInexistente);

    // Assert
    System.out.println("\n📊 Verificando resultados:");

    assertNull(resultado, "CEP não encontrado deveria retornar null");
    System.out.println("✅ CEP não encontrado retornou null");

    // Verify
    verify(restTemplate, times(1)).getForObject(expectedUrl, ViaCepResponseDto.class);
    System.out.println("✅ RestTemplate foi chamado para verificar na API");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve tratar erro de rede")
  void deveTratarErroDeRede() {
    System.out.println("\n🧪 TESTE: Erro de comunicação com API");

    // Arrange
    String cepTeste = "01001000";
    String expectedUrl = "https://viacep.com.br/ws/01001000/json/";

    when(restTemplate.getForObject(expectedUrl, ViaCepResponseDto.class))
        .thenThrow(new RestClientException("Erro de conexão simulado"));

    System.out.println("📋 Configuração:");
    System.out.println("   CEP teste: " + cepTeste);
    System.out.println("   Erro simulado: RestClientException");

    // Act
    ViaCepResponseDto resultado = viaCepService.buscarEnderecoPorCep(cepTeste);

    // Assert
    System.out.println("\n📊 Verificando resultados:");

    assertNull(resultado, "Erro de rede deveria retornar null");
    System.out.println("✅ Erro de rede tratado corretamente (retornou null)");

    // Verify
    verify(restTemplate, times(1)).getForObject(expectedUrl, ViaCepResponseDto.class);
    System.out.println("✅ RestTemplate foi chamado apesar do erro");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve converter ViaCepResponse para Endereco")
  void deveConverterViaCepResponseParaEndereco() {
    System.out.println("\n🧪 TESTE: Conversão de DTO para Entidade");

    // Arrange
    String numero = "123";
    String complemento = "Apto 45";

    System.out.println("📋 Configuração:");
    System.out.println(
        "   DTO origem: " + responseValida.getLocalidade() + "/" + responseValida.getUf());
    System.out.println("   Número fornecido: " + numero);
    System.out.println("   Complemento fornecido: " + complemento);

    // Act
    Endereco endereco = viaCepService.converterParaEndereco(responseValida, numero, complemento);

    // Assert
    System.out.println("\n📊 Verificando conversão:");

    assertNotNull(endereco, "Endereço convertido não deveria ser null");
    System.out.println("✅ Endereço convertido com sucesso");

    assertEquals("01001-000", endereco.getCep());
    System.out.println("✅ CEP mapeado: " + endereco.getCep());

    assertEquals("Praça da Sé", endereco.getLogradouro());
    System.out.println("✅ Logradouro mapeado: " + endereco.getLogradouro());

    assertEquals(numero, endereco.getNumero());
    System.out.println("✅ Número definido: " + endereco.getNumero());

    assertEquals("Sé", endereco.getBairro());
    System.out.println("✅ Bairro mapeado: " + endereco.getBairro());

    assertEquals("São Paulo", endereco.getCidade());
    System.out.println("✅ Cidade mapeada (localidade -> cidade): " + endereco.getCidade());

    assertEquals(UF.SP, endereco.getUf());
    System.out.println("✅ UF convertida (String -> Enum): " + endereco.getUf());

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve buscar e converter endereço em uma operação")
  void deveBuscarEConverterEndereco() {
    System.out.println("\n🧪 TESTE: Busca + Conversão integrada");

    // Arrange
    String cepTeste = "01001-000";
    String numero = "100";
    String complemento = "Sala 1";
    String expectedUrl = "https://viacep.com.br/ws/01001000/json/";

    when(restTemplate.getForObject(expectedUrl, ViaCepResponseDto.class))
        .thenReturn(responseValida);

    System.out.println("📋 Configuração:");
    System.out.println("   CEP busca: " + cepTeste);
    System.out.println("   Número: " + numero);
    System.out.println("   Complemento: " + complemento);

    // Act
    Endereco endereco = viaCepService.buscarEConverterEndereco(cepTeste, numero, complemento);

    // Assert
    System.out.println("\n📊 Verificando operação integrada:");

    assertNotNull(endereco, "Endereço não deveria ser null");
    System.out.println("✅ Endereço criado com sucesso");

    assertEquals("01001-000", endereco.getCep());
    System.out.println("✅ CEP: " + endereco.getCep());

    assertEquals("São Paulo", endereco.getCidade());
    System.out.println("✅ Cidade: " + endereco.getCidade());

    assertEquals(UF.SP, endereco.getUf());
    System.out.println("✅ UF: " + endereco.getUf());

    assertEquals(numero, endereco.getNumero());
    System.out.println("✅ Número: " + endereco.getNumero());

    // Verify
    verify(restTemplate, times(1)).getForObject(expectedUrl, ViaCepResponseDto.class);
    System.out.println("✅ API foi consultada exatamente 1 vez");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve validar CEP corretamente")
  void deveValidarCep() {
    System.out.println("\n🧪 TESTE: Validação de CEPs");

    // ✅ CORREÇÃO: validarCep só valida formato, não chama API

    // CEPs válidos (8 dígitos)
    String[] cepsValidos = {"01001000", "01001-000", "12345678"};

    System.out.println("📋 Testando CEPs válidos:");
    for (String cep : cepsValidos) {
      boolean resultado = viaCepService.validarCep(cep);
      assertTrue(resultado, "CEP " + cep + " deveria ser válido");
      System.out.println("✅ CEP válido: " + cep);
    }

    // CEPs inválidos
    String[] cepsInvalidos = {"123", "abcd1234", "", null, "123456789"};

    System.out.println("\n📋 Testando CEPs inválidos:");
    for (String cep : cepsInvalidos) {
      boolean resultado = viaCepService.validarCep(cep);
      assertFalse(resultado, "CEP " + cep + " deveria ser inválido");
      System.out.println("✅ CEP inválido: " + cep);
    }

    // ✅ Verificar que RestTemplate NÃO foi chamado (só validação de formato)
    verify(restTemplate, never()).getForObject(anyString(), eq(ViaCepResponseDto.class));
    System.out.println("✅ RestTemplate não foi chamado (validação só de formato)");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }

  @Test
  @DisplayName("Deve validar CEP existente na API")
  void deveValidarCepExistenteNaApi() {
    System.out.println("\n🧪 TESTE: Validação de CEP com consulta à API");

    // Arrange
    String cepExistente = "01001000";
    String expectedUrl = "https://viacep.com.br/ws/01001000/json/";

    when(restTemplate.getForObject(expectedUrl, ViaCepResponseDto.class))
        .thenReturn(responseValida);

    System.out.println("📋 Configuração:");
    System.out.println("   CEP para validar: " + cepExistente);
    System.out.println("   Resposta mockada: Sucesso");

    // Act - Buscar para validar se existe
    ViaCepResponseDto response = viaCepService.buscarEnderecoPorCep(cepExistente);

    // Assert
    System.out.println("\n📊 Verificando validação via API:");

    assertNotNull(response, "CEP existente deveria retornar dados");
    System.out.println("✅ CEP existe na API: " + response.getLocalidade());

    assertFalse(response.isErro());
    System.out.println("✅ Sem erro na resposta");

    // Verify
    verify(restTemplate, times(1)).getForObject(expectedUrl, ViaCepResponseDto.class);
    System.out.println("✅ API foi consultada exatamente 1 vez");

    System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
  }
}
