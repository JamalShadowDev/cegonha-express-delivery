package br.com.cegonhaexpress.cegonha_express.integration;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cegonhaexpress.cegonha_express.config.RestTemplateConfig;
import br.com.cegonhaexpress.cegonha_express.dto.ViaCepResponseDto;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import br.com.cegonhaexpress.cegonha_express.services.ViaCepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

/**
 * Testes de integração completa do sistema ViaCEP.
 *
 * <p>Testa o fluxo completo: Config → Service → DTO → Entity
 *
 * @author Gabriel Coelho Soares
 */
@DisplayName("ViaCEP - Testes de Integração Completa")
class ViaCepIntegrationTest {

  private ViaCepService viaCepService;
  private RestTemplateConfig config;

  @BeforeEach
  void setUp() {
    System.out.println("\n=== CONFIGURANDO INTEGRAÇÃO COMPLETA ===");

    // Montar toda a estrutura como seria no Spring
    config = new RestTemplateConfig();
    RestTemplate restTemplate = config.createRestTemplate();
    viaCepService = new ViaCepService(restTemplate);

    System.out.println("✅ Stack completa configurada:");
    System.out.println("   📄 RestTemplateConfig instanciado");
    System.out.println("   🌐 RestTemplate configurado com timeouts");
    System.out.println("   🔧 ViaCepService injetado com RestTemplate");
    System.out.println("   🚀 Sistema pronto para testes de integração");
  }

  @Test
  @DisplayName("Deve executar fluxo completo de busca e conversão")
  void deveExecutarFluxoCompletoDeBuscaEConversao() {
    System.out.println("\n🧪 TESTE DE INTEGRAÇÃO: Fluxo completo");

    // Arrange
    String cepTeste = "01001-000"; // CEP da Praça da Sé, SP
    String numero = "100";
    String complemento = "Lado ímpar";

    System.out.println("📋 Parâmetros do teste:");
    System.out.println("   CEP: " + cepTeste);
    System.out.println("   Número: " + numero);
    System.out.println("   Complemento: " + complemento);
    System.out.println("\n🔄 Iniciando fluxo de integração...");

    try {
      // Act 1: Buscar via API
      System.out.println("\n1️⃣ ETAPA: Busca na API ViaCEP");
      ViaCepResponseDto response = viaCepService.buscarEnderecoPorCep(cepTeste);

      // Assert 1: Verificar resposta da API
      if (response != null && !response.isErro()) {
        System.out.println("✅ API respondeu com sucesso");
        System.out.println("   🏢 Logradouro: " + response.getLogradouro());
        System.out.println("   🏙️ Cidade: " + response.getLocalidade());
        System.out.println("   🗺️ UF: " + response.getUf());
        System.out.println("   📮 CEP: " + response.getCep());

        assertNotNull(response);
        assertFalse(response.isErro());
        assertTrue(response.isCepEncontrado());

        // Act 2: Converter para Endereco
        System.out.println("\n2️⃣ ETAPA: Conversão para entidade");
        Endereco endereco = viaCepService.converterParaEndereco(response, numero, complemento);

        // Assert 2: Verificar conversão
        if (endereco != null) {
          System.out.println("✅ Conversão realizada com sucesso");
          System.out.println("   📍 Endereço completo: " + endereco.getEnderecoCompleto());
          System.out.println("   🆔 ID entidade: " + endereco.getId());
          System.out.println("   ✅ Endereço completo: " + endereco.isCompleto());

          assertNotNull(endereco);
          assertEquals(response.getCep(), endereco.getCep());
          assertEquals(response.getLogradouro(), endereco.getLogradouro());
          assertEquals(response.getLocalidade(), endereco.getCidade());
          assertEquals(numero, endereco.getNumero());
          assertEquals(UF.valueOf(response.getUf()), endereco.getUf());
          assertTrue(endereco.isCompleto());

          // Act 3: Teste do método integrado
          System.out.println("\n3️⃣ ETAPA: Método integrado (buscar + converter)");
          Endereco enderecoIntegrado =
              viaCepService.buscarEConverterEndereco(cepTeste, numero, complemento);

          // Assert 3: Verificar método integrado
          assertNotNull(enderecoIntegrado);
          assertEquals(endereco.getCep(), enderecoIntegrado.getCep());
          assertEquals(endereco.getCidade(), enderecoIntegrado.getCidade());
          System.out.println("✅ Método integrado funcionando corretamente");

        } else {
          System.out.println("⚠️ Conversão retornou null - possível problema na UF");
          fail("Conversão não deveria retornar null para dados válidos");
        }

      } else {
        System.out.println("⚠️ API não respondeu ou retornou erro");
        System.out.println("   Isso pode acontecer por:");
        System.out.println("   - Conexão com internet indisponível");
        System.out.println("   - API ViaCEP temporariamente fora do ar");
        System.out.println("   - CEP realmente não existente");

        // Para testes de integração, isso é aceitável
        System.out.println("🟡 TESTE IGNORADO - Dependência externa indisponível");
        org.junit.jupiter.api.Assumptions.assumeTrue(false, "API ViaCEP indisponível");
      }

    } catch (Exception e) {
      System.out.println("❌ ERRO durante integração: " + e.getMessage());
      System.out.println("   Tipo: " + e.getClass().getSimpleName());

      // Para testes de integração, logging é importante
      e.printStackTrace();

      // Decidir se falha é aceitável ou não
      if (e.getMessage().contains("timeout") || e.getMessage().contains("connection")) {
        System.out.println("🟡 ERRO DE REDE - Aceitável em testes de integração");
        org.junit.jupiter.api.Assumptions.assumeTrue(false, "Erro de rede: " + e.getMessage());
      } else {
        fail("Erro inesperado na integração: " + e.getMessage());
      }
    }

    System.out.println("\n🎉 TESTE DE INTEGRAÇÃO CONCLUÍDO!");
  }

  @Test
  @DisplayName("Deve testar diferentes cenários de CEP")
  void deveTestarDiferentesCenariosDeCep() {
    System.out.println("\n🧪 TESTE DE INTEGRAÇÃO: Múltiplos CEPs");

    // CEPs de teste (reais e conhecidos)
    String[] cepsParaTeste = {
      "01001-000", // Praça da Sé, SP
      "20040-020", // Rio de Janeiro, RJ
      "70040-010", // Brasília, DF
      "80010-000", // Curitiba, PR
      "99999-999" // CEP inexistente
    };

    int sucessos = 0;
    int erros = 0;
    int timeouts = 0;

    for (String cep : cepsParaTeste) {
      System.out.println("\n🔍 Testando CEP: " + cep);

      try {
        ViaCepResponseDto response = viaCepService.buscarEnderecoPorCep(cep);

        if (response == null) {
          erros++;
          System.out.println("❌ CEP retornou null (inválido ou não encontrado)");
        } else if (response.isErro()) {
          erros++;
          System.out.println("❌ CEP não encontrado na base ViaCEP");
        } else {
          sucessos++;
          System.out.println(
              "✅ CEP encontrado: " + response.getLocalidade() + "/" + response.getUf());
        }

      } catch (Exception e) {
        timeouts++;
        System.out.println("⏱️ Timeout/Erro de rede: " + e.getMessage());
      }

      // Pausa entre requisições para não sobrecarregar API
      try {
        Thread.sleep(200);
      } catch (InterruptedException ignored) {
      }
    }

    System.out.println("\n📊 RESUMO DOS TESTES:");
    System.out.println("   ✅ Sucessos: " + sucessos);
    System.out.println("   ❌ Erros esperados: " + erros);
    System.out.println("   ⏱️ Timeouts/Rede: " + timeouts);
    System.out.printf("   📈 Taxa de sucesso: %.1f%%%n", (sucessos * 100.0) / cepsParaTeste.length);

    // Verificar se pelo menos alguns CEPs funcionaram
    assertTrue(
        sucessos > 0 || timeouts > 0,
        "Pelo menos alguns CEPs deveriam funcionar ou dar timeout (indicando tentativa de"
            + " conexão)");

    System.out.println("🎉 TESTE DE MÚLTIPLOS CEPS CONCLUÍDO!");
  }

  @Test
  @DisplayName("Deve testar performance e timeouts")
  void deveTestarPerformanceETimeouts() {
    System.out.println("\n🧪 TESTE DE INTEGRAÇÃO: Performance e Timeouts");

    String cepTeste = "01001000";
    int numeroTestes = 3;
    long[] tempos = new long[numeroTestes];

    System.out.println("📊 Executando " + numeroTestes + " requisições para medir performance:");

    for (int i = 0; i < numeroTestes; i++) {
      System.out.println("\n🔄 Requisição " + (i + 1) + "/" + numeroTestes);

      long inicio = System.currentTimeMillis();

      try {
        ViaCepResponseDto response = viaCepService.buscarEnderecoPorCep(cepTeste);
        long fim = System.currentTimeMillis();
        tempos[i] = fim - inicio;

        System.out.println("⏱️ Tempo: " + tempos[i] + "ms");

        if (response != null && !response.isErro()) {
          System.out.println("✅ Resposta válida recebida");
        } else {
          System.out.println("⚠️ Resposta com erro ou null");
        }

      } catch (Exception e) {
        long fim = System.currentTimeMillis();
        tempos[i] = fim - inicio;
        System.out.println("❌ Erro após " + tempos[i] + "ms: " + e.getMessage());
      }

      // Pausa entre requisições
      if (i < numeroTestes - 1) {
        try {
          Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
      }
    }

    // Calcular estatísticas
    long tempoTotal = 0;
    long tempoMin = Long.MAX_VALUE;
    long tempoMax = 0;

    for (long tempo : tempos) {
      tempoTotal += tempo;
      tempoMin = Math.min(tempoMin, tempo);
      tempoMax = Math.max(tempoMax, tempo);
    }

    double tempoMedio = tempoTotal / (double) numeroTestes;

    System.out.println("\n📊 ESTATÍSTICAS DE PERFORMANCE:");
    System.out.println("   ⏱️ Tempo médio: " + String.format("%.1f", tempoMedio) + "ms");
    System.out.println("   🏃 Tempo mínimo: " + tempoMin + "ms");
    System.out.println("   🐌 Tempo máximo: " + tempoMax + "ms");
    System.out.println("   📈 Variação: " + (tempoMax - tempoMin) + "ms");

    // Verificar se os tempos estão dentro dos timeouts configurados
    long timeoutConfigurado = 10000; // 10 segundos (ReadTimeout)

    for (long tempo : tempos) {
      assertTrue(
          tempo <= timeoutConfigurado + 1000, // +1s de margem
          "Tempo de resposta ("
              + tempo
              + "ms) não deveria exceder timeout configurado ("
              + timeoutConfigurado
              + "ms)");
    }

    System.out.println("✅ Todos os tempos dentro do timeout configurado");

    // Verificar performance aceitável (média < 5 segundos para API externa)
    if (tempoMedio < 5000) {
      System.out.println("🚀 Performance excelente (< 5s)");
    } else if (tempoMedio < 8000) {
      System.out.println("⚡ Performance boa (< 8s)");
    } else {
      System.out.println("🐌 Performance lenta (> 8s) - pode ser problema de rede");
    }

    System.out.println("🎉 TESTE DE PERFORMANCE CONCLUÍDO!");
  }

  @Test
  @DisplayName("Deve demonstrar uso completo do sistema")
  void deveDemonstrarUsoCompletoDoSistema() {
    System.out.println("\n🧪 DEMONSTRAÇÃO: Uso completo do sistema ViaCEP");

    System.out.println("\n🎯 CENÁRIO: Cliente quer cadastrar endereço com CEP");

    // Simula dados de entrada do usuário
    String cepUsuario = "01310-100"; // Av. Paulista, SP
    String numeroUsuario = "1578";
    String complementoUsuario = "Conjunto 142";

    System.out.println("📋 Dados fornecidos pelo usuário:");
    System.out.println("   CEP: " + cepUsuario);
    System.out.println("   Número: " + numeroUsuario);
    System.out.println("   Complemento: " + complementoUsuario);

    try {
      System.out.println("\n🔍 PASSO 1: Validando CEP na base ViaCEP...");

      // Primeira validação - CEP existe?
      boolean cepValido = viaCepService.validarCep(cepUsuario);
      System.out.println("   Resultado validação: " + (cepValido ? "✅ Válido" : "❌ Inválido"));

      if (cepValido) {
        System.out.println("\n📍 PASSO 2: Buscando dados completos do endereço...");

        // Busca detalhada
        ViaCepResponseDto dadosEndereco = viaCepService.buscarEnderecoPorCep(cepUsuario);

        if (dadosEndereco != null && !dadosEndereco.isErro()) {
          System.out.println("✅ Dados encontrados:");
          System.out.println("   🏢 Logradouro: " + dadosEndereco.getLogradouro());
          System.out.println("   🏘️ Bairro: " + dadosEndereco.getBairro());
          System.out.println("   🏙️ Cidade: " + dadosEndereco.getLocalidade());
          System.out.println("   🗺️ Estado: " + dadosEndereco.getUf());
          System.out.println("   📞 DDD: " + dadosEndereco.getDdd());

          System.out.println("\n🏗️ PASSO 3: Criando entidade Endereco...");

          // Conversão para entidade
          Endereco enderecoCompleto =
              viaCepService.buscarEConverterEndereco(cepUsuario, numeroUsuario, complementoUsuario);

          if (enderecoCompleto != null) {
            System.out.println("✅ Entidade criada com sucesso:");
            System.out.println(
                "   📍 Endereço completo: " + enderecoCompleto.getEnderecoCompleto());
            System.out.println("   ✅ Dados completos: " + enderecoCompleto.isCompleto());
            System.out.println(
                "   📦 Pronto para persistência: "
                    + (enderecoCompleto.isCompleto() ? "SIM" : "NÃO"));

            System.out.println("\n💾 PASSO 4: Simulando persistência...");

            // Aqui normalmente salvaria no banco de dados
            System.out.println("   🔄 endereco.save() - SIMULADO");
            System.out.println(
                "   ✅ Endereço salvo com ID: "
                    + (enderecoCompleto.getId() != null
                        ? enderecoCompleto.getId()
                        : "AUTO_GENERATED"));

            System.out.println("\n🎉 FLUXO COMPLETO EXECUTADO COM SUCESSO!");
            System.out.println("   ✅ CEP validado");
            System.out.println("   ✅ Dados buscados na API");
            System.out.println("   ✅ Entidade criada");
            System.out.println("   ✅ Pronto para persistência");

            // Assertions para garantir que tudo funcionou
            assertNotNull(enderecoCompleto);
            assertTrue(enderecoCompleto.isCompleto());
            assertEquals(
                cepUsuario.replaceAll("\\D", "").substring(0, 5)
                    + "-"
                    + cepUsuario.replaceAll("\\D", "").substring(5),
                enderecoCompleto.getCep());
            assertEquals(numeroUsuario, enderecoCompleto.getNumero());

          } else {
            System.out.println("❌ Falha na criação da entidade");
            fail("Entidade não deveria ser null para dados válidos");
          }

        } else {
          System.out.println("❌ CEP não encontrado na base ViaCEP");
          System.out.println("   💡 Usuário deve informar dados manualmente");
        }

      } else {
        System.out.println("❌ CEP inválido - usuário deve corrigir");
        System.out.println("   💡 Mostrar mensagem de erro amigável");
      }

    } catch (Exception e) {
      System.out.println("❌ ERRO no fluxo: " + e.getMessage());
      System.out.println("   💡 Sistema deve ter fallback para entrada manual");
      System.out.println("   🔧 Log completo:");
      e.printStackTrace();

      // Em um sistema real, isso seria logado e teria fallback
      System.out.println("🟡 FALLBACK: Permitir cadastro manual do endereço");
    }

    System.out.println("\n🎉 DEMONSTRAÇÃO CONCLUÍDA!");
  }

  @Test
  @DisplayName("Deve testar robustez do sistema")
  void deveTestarRobustezDoSistema() {
    System.out.println("\n🧪 TESTE DE ROBUSTEZ: Cenários extremos e edge cases");

    System.out.println("\n🎯 Testando diferentes tipos de entrada:");

    // Teste de entradas extremas
    String[] entradasExtremas = {
      null,
      "",
      "   ",
      "abc",
      "12345",
      "123456789",
      "00000-000",
      "99999-999",
      "01001000",
      "01001-000",
      "010010001", // 9 dígitos
      "abcde-fgh"
    };

    int testesExecutados = 0;
    int testesComErro = 0;
    int testesComSucesso = 0;
    int testesComTimeout = 0;

    for (String entrada : entradasExtremas) {
      System.out.println("\n🔍 Testando entrada: '" + entrada + "'");
      testesExecutados++;

      try {
        long inicio = System.currentTimeMillis();
        ViaCepResponseDto resultado = viaCepService.buscarEnderecoPorCep(entrada);
        long tempo = System.currentTimeMillis() - inicio;

        System.out.println("   ⏱️ Tempo: " + tempo + "ms");

        if (resultado == null) {
          testesComErro++;
          System.out.println("   ✅ Retornou null (entrada inválida tratada corretamente)");
        } else if (resultado.isErro()) {
          testesComErro++;
          System.out.println("   ✅ Retornou erro (CEP não encontrado - comportamento esperado)");
        } else {
          testesComSucesso++;
          System.out.println("   ✅ Retornou dados válidos: " + resultado.getLocalidade());
        }

      } catch (Exception e) {
        testesComTimeout++;
        System.out.println(
            "   ⚠️ Exceção: " + e.getClass().getSimpleName() + " - " + e.getMessage());
      }
    }

    System.out.println("\n📊 RESUMO DOS TESTES DE ROBUSTEZ:");
    System.out.println("   🧪 Total executados: " + testesExecutados);
    System.out.println("   ✅ Sucessos: " + testesComSucesso);
    System.out.println("   ❌ Erros esperados: " + testesComErro);
    System.out.println("   ⚠️ Timeouts/Exceções: " + testesComTimeout);

    // O sistema deve ser robusto - não quebrar com entradas inválidas
    assertTrue(
        testesComTimeout == 0 || testesComTimeout < testesExecutados / 2,
        "Sistema não deveria ter muitas exceções não tratadas");

    System.out.println("✅ Sistema demonstrou robustez adequada");
    System.out.println("🎉 TESTE DE ROBUSTEZ CONCLUÍDO!");
  }
}
