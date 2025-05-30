package br.com.cegonhaexpress.cegonha_express.service;

import br.com.cegonhaexpress.cegonha_express.dto.result.CalculoDeDistanciaResult;
import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.entity.Frete;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsável por calcular fretes utilizando distâncias reais do Google Maps.
 *
 * <p>Integra GoogleMapsDistanceService com a lógica de negócio da entidade Frete, substituindo
 * estimativas por dados precisos de rota. Constrói endereços completos para maior precisão nas
 * consultas de distância.
 *
 * @author Gabriel Coelho Soares
 * @see Frete @See GoogleMapsDistanceService
 */
@Service
@Slf4j
public class FreteService {

  private final GoogleMapsDistanceService distanceService;

  public FreteService(GoogleMapsDistanceService distanceService) {
    this.distanceService = distanceService;
  }

  /**
   * Calcula o frete usando distância real obtida do Google Maps. Substitui estimativas imprecisas
   * por dados reais de rota.
   */
  public Frete calcularFreteComDistanciaReal(Encomenda encomenda) {

    // Constrói endereços completos para maior precisão
    String enderecoOrigem = construirEnderecoCompleto(encomenda.getEnderecoOrigem());
    String enderecoDestino = construirEnderecoCompleto(encomenda.getEnderecoDestino());

    // Obtém distância real via Google Maps
    CalculoDeDistanciaResult distanceResult =
        distanceService.calcularDistancia(enderecoOrigem, enderecoDestino);

    // Usa a distância real para calcular o frete (método estático da entidade Frete)
    BigDecimal valorFrete =
        Frete.calcularFrete(
            encomenda.getTipoEntrega(), distanceResult.getDistanciaKm(), encomenda.getPesoKg());

    // Cria o frete com dados precisos
    return new Frete(
        encomenda,
        encomenda.getTipoEntrega(),
        valorFrete,
        distanceResult.getDistanciaKm(),
        encomenda.getTipoEntrega().getDiasMinimosEntrega());
  }

  /**
   * Constrói endereço completo para melhor precisão na consulta. O Google Maps funciona melhor com
   * endereços detalhados.
   */
  private String construirEnderecoCompleto(Endereco endereco) {
    return String.format(
        "%s, %s, %s, %s - %s, %s",
        endereco.getLogradouro(),
        endereco.getNumero(),
        endereco.getBairro(),
        endereco.getCidade(),
        endereco.getUf().name(),
        endereco.getCepFormatado());
  }
}
