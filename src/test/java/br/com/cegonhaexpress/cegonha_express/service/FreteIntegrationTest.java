package br.com.cegonhaexpress.cegonha_express.service;

import static org.assertj.core.api.Assertions.*;

import br.com.cegonhaexpress.cegonha_express.dto.CalculoDeDistanciaResult;
import br.com.cegonhaexpress.cegonha_express.exception.GoogleMapsIntegrationException;
import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.entity.Frete;
import br.com.cegonhaexpress.cegonha_express.model.enums.TipoEntrega;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import com.google.maps.GeoApiContext;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Teste de integração completo para o sistema de cálculo de fretes.
 *
 * <p>Este teste integra todas as classes implementadas: - GoogleMapsConfig (configuração do
 * GeoApiContext) - GoogleMapsDistanceService (serviço de cálculo de distância) -
 * CalculoDeDistanciaResult (DTO de resultado) - FreteService (orquestração do cálculo de frete) -
 * GoogleMapsIntegrationException (tratamento de erros)
 *
 * @author Gabriel Coelho Soares
 */
@SpringBootTest
@ActiveProfiles({"test", "test-local"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Integração Completa - Sistema de Cálculo de Fretes")
class FreteIntegrationTest {

  @Autowired private GeoApiContext geoApiContext;

  @Autowired private GoogleMapsDistanceService googleMapsDistanceService;

  @Autowired private FreteService freteService;

  private Cliente clienteTeste;
  private Endereco enderecoOrigemSP;
  private Endereco enderecoDestinoRJ;
  private Encomenda encomendaTeste;

  @BeforeEach
  void setUp() {
    // Criação do cliente de teste
    clienteTeste = new Cliente("Maria da Silva", "maria.silva@email.com", "12345678901");

    // Endereço de origem - São Paulo
    enderecoOrigemSP =
        new Endereco("01310-100", "Avenida Paulista", "1000", "Bela Vista", "São Paulo", UF.SP);

    // Endereço de destino - Rio de Janeiro
    enderecoDestinoRJ =
        new Endereco("20040-020", "Avenida Rio Branco", "100", "Centro", "Rio de Janeiro", UF.RJ);

    // Encomenda de teste
    encomendaTeste =
        new Encomenda(
            clienteTeste,
            enderecoOrigemSP,
            enderecoDestinoRJ,
            TipoEntrega.PADRAO,
            "Bebê reborn colecionável",
            new BigDecimal("2.5"),
            new BigDecimal("45.0"),
            new BigDecimal("350.00"));
  }

  @Test
  @DisplayName("Deve configurar corretamente o GeoApiContext")
  void deveConfigurarGeoApiContext() {
    // Verifica se o bean foi criado corretamente
    assertThat(geoApiContext).isNotNull();

    // Não podemos acessar as configurações internas diretamente,
    // mas podemos verificar se está funcional
    assertThatCode(() -> geoApiContext.toString()).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Deve calcular distância real entre São Paulo e Rio de Janeiro")
  void deveCalcularDistanciaRealSPRJ() {
    // Given
    String enderecoOrigem = "Avenida Paulista, 1000, Bela Vista, São Paulo - SP, CEP: 01310-100";
    String enderecoDestino = "Avenida Rio Branco, 100, Centro, Rio de Janeiro - RJ, CEP: 20040-020";

    // When
    CalculoDeDistanciaResult resultado =
        googleMapsDistanceService.calcularDistancia(enderecoOrigem, enderecoDestino);

    // Then
    assertThat(resultado).isNotNull();
    assertThat(resultado.getDistanciaKm()).isGreaterThan(BigDecimal.valueOf(350)); // SP-RJ > 350km
    assertThat(resultado.getDistanciaKm()).isLessThan(BigDecimal.valueOf(600)); // SP-RJ < 600km
    assertThat(resultado.getDuracaoMinutos()).isGreaterThan(240L); // > 4 horas
    assertThat(resultado.getEnderecoOrigemFormatado()).isNotEmpty();
    assertThat(resultado.getEnderecoDestinoFormatado()).isNotEmpty();

    // Testa métodos de formatação
    assertThat(resultado.getDistanciaFormatada().toLowerCase()).contains("km");
    assertThat(resultado.getDuracaoFormatada()).matches(".*h.*|.*min.*"); // Deve conter h ou min
  }

  @Test
  @DisplayName("Deve calcular frete completo usando distância real do Google Maps")
  void deveCalcularFreteCompletoComDistanciaReal() {
    // When
    Frete freteCalculado = freteService.calcularFreteComDistanciaReal(encomendaTeste);

    // Then
    assertThat(freteCalculado).isNotNull();

    // Verifica associação com encomenda
    assertThat(freteCalculado.getEncomenda()).isEqualTo(encomendaTeste);
    assertThat(freteCalculado.getTipoEntrega()).isEqualTo(TipoEntrega.PADRAO);

    // Verifica distância real (SP-RJ)
    assertThat(freteCalculado.getDistanciaKm()).isGreaterThan(BigDecimal.valueOf(350));
    assertThat(freteCalculado.getDistanciaKm()).isLessThan(BigDecimal.valueOf(600));

    // Verifica valor do frete (baseado na distância real)
    assertThat(freteCalculado.getValor()).isGreaterThan(BigDecimal.ZERO);

    // Verifica prazo conforme tipo de entrega
    assertThat(freteCalculado.getPrazoDias()).isEqualTo(TipoEntrega.PADRAO.getDiasMinimosEntrega());

    // Verifica CEPs extraídos
    assertThat(freteCalculado.getCepOrigem()).isEqualTo("01310-100");
    assertThat(freteCalculado.getCepDestino()).isEqualTo("20040-020");

    System.out.println("=== RESULTADO DO TESTE ===");
    System.out.println("Distância: " + freteCalculado.getDistanciaFormatada());
    System.out.println("Valor: " + freteCalculado.getValorFormatado());
    System.out.println("Prazo: " + freteCalculado.getPrazoFormatado());
    System.out.println("Resumo: " + freteCalculado.getResumo());
  }

  @Test
  @DisplayName("Deve comparar preços entre diferentes tipos de entrega")
  void deveCompararPrecosDiferentesTiposEntrega() {
    // Given - Criar encomendas com diferentes tipos de entrega
    Encomenda encomendaExpressa =
        new Encomenda(
            clienteTeste,
            enderecoOrigemSP,
            enderecoDestinoRJ,
            TipoEntrega.EXPRESSA,
            "Bebê reborn urgente",
            new BigDecimal("2.5"),
            new BigDecimal("45.0"),
            new BigDecimal("350.00"));

    Encomenda encomendaEconomica =
        new Encomenda(
            clienteTeste,
            enderecoOrigemSP,
            enderecoDestinoRJ,
            TipoEntrega.ECONOMICA,
            "Bebê reborn econômico",
            new BigDecimal("2.5"),
            new BigDecimal("45.0"),
            new BigDecimal("350.00"));

    // When
    Frete freteExpressa = freteService.calcularFreteComDistanciaReal(encomendaExpressa);
    Frete fretePadrao = freteService.calcularFreteComDistanciaReal(encomendaTeste);
    Frete freteEconomica = freteService.calcularFreteComDistanciaReal(encomendaEconomica);

    // Then - Expressa deve ser mais cara que Padrão, que deve ser mais cara que Econômica
    assertThat(freteExpressa.getValor()).isGreaterThan(fretePadrao.getValor());
    assertThat(fretePadrao.getValor()).isGreaterThan(freteEconomica.getValor());

    // Prazos devem estar corretos
    assertThat(freteExpressa.getPrazoDias()).isEqualTo(1);
    assertThat(fretePadrao.getPrazoDias()).isEqualTo(3);
    assertThat(freteEconomica.getPrazoDias()).isEqualTo(7);

    System.out.println("=== COMPARAÇÃO DE PREÇOS ===");
    System.out.println(
        "Expressa: "
            + freteExpressa.getValorFormatado()
            + " ("
            + freteExpressa.getPrazoDias()
            + " dias)");
    System.out.println(
        "Padrão: "
            + fretePadrao.getValorFormatado()
            + " ("
            + fretePadrao.getPrazoDias()
            + " dias)");
    System.out.println(
        "Econômica: "
            + freteEconomica.getValorFormatado()
            + " ("
            + freteEconomica.getPrazoDias()
            + " dias)");
  }

  @Test
  @DisplayName("Deve lançar GoogleMapsIntegrationException para endereço inválido")
  void deveLancarExcecaoParaEnderecoInvalido() {
    // Given
    String enderecoInvalido = "Endereço Inexistente 999999, Cidade Fictícia - ZZ";
    String enderecoValido = "Avenida Paulista, 1000, São Paulo - SP";

    // When & Then
    assertThatThrownBy(
            () -> googleMapsDistanceService.calcularDistancia(enderecoInvalido, enderecoValido))
        .isInstanceOf(GoogleMapsIntegrationException.class)
        .hasMessageContaining("Endereço não encontrado");
  }

  @Test
  @DisplayName("Deve validar formatação de duração com dias, horas e minutos")
  void deveValidarFormatacaoDuracao() {
    // Given
    String origem = "São Paulo, SP";
    String destino = "Salvador, BA"; // Distância maior para ter duração longa

    // When
    CalculoDeDistanciaResult resultado =
        googleMapsDistanceService.calcularDistancia(origem, destino);

    // Then
    String duracaoFormatada = resultado.getDuracaoFormatada();

    // Para distância SP-Salvador, deve ter pelo menos horas
    assertThat(duracaoFormatada).containsAnyOf("h", "min");

    // Se duração > 24h, deve conter "dia"
    if (resultado.getDuracaoMinutos() > 1440) { // > 24 horas
      assertThat(duracaoFormatada).contains("dia");
    }

    System.out.println("=== TESTE DE FORMATAÇÃO ===");
    System.out.println("Origem: " + origem);
    System.out.println("Destino: " + destino);
    System.out.println("Distância: " + resultado.getDistanciaFormatada());
    System.out.println("Duração: " + resultado.getDuracaoFormatada());
    System.out.println("Duração em minutos: " + resultado.getDuracaoMinutos());
  }

  @Test
  @DisplayName("Deve verificar se frete é interestadual")
  void deveVerificarFreteInterestadual() {
    // When
    Frete freteCalculado = freteService.calcularFreteComDistanciaReal(encomendaTeste);

    // Then - SP para RJ deve ser interestadual
    assertThat(freteCalculado.isInterestadual()).isTrue();

    System.out.println("=== VERIFICAÇÃO INTERESTADUAL ===");
    System.out.println("Origem: " + enderecoOrigemSP.getUf());
    System.out.println("Destino: " + enderecoDestinoRJ.getUf());
    System.out.println("É interestadual: " + freteCalculado.isInterestadual());
  }
}
