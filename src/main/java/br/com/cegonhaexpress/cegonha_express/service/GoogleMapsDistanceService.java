package br.com.cegonhaexpress.cegonha_express.service;

import br.com.cegonhaexpress.cegonha_express.dto.CalculoDeDistanciaResult;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import java.io.IOException;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoogleMapsDistanceService {

  private final GeoApiContext geoApiContext;

  public GoogleMapsDistanceService(GeoApiContext geoApiContext) {
    this.geoApiContext = geoApiContext;
  }

  /**
   * Calcula a distância entre dois endereços usando a API Distance Matrix.
   *
   * <p>A grande vantagem desta abordagem é que ela considera: - Rotas reais (não distância em linha
   * reta) - Condições de trânsito - Restrições de trânsito (pedágios, estradas fechadas)
   */
  public CalculoDeDistanciaResult calcularDistancia(String enderecoOrigem, String enderecoDestino) {
    try {
      DistanceMatrixApiRequest request =
          DistanceMatrixApi.newRequest(geoApiContext)
              .origins(enderecoOrigem)
              .destinations(enderecoDestino)
              .units(Unit.METRIC)
              .mode(TravelMode.DRIVING)
              .language("pt-BR");

      DistanceMatrix result = request.await();

      return processarResposta(result, enderecoOrigem, enderecoDestino);

    } catch (ApiException e) {
      log.error("Erro na API do Google Maps: {}", e.getMessage());
      // throw new Exeção de projeto
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      // throw new Exeção de projeto
    } catch (IOException e) {
      log.error("Erro de conectividade com a API do Google Maps: {}", e.getMessage());
      // throw new Exeção de projeto
    }
  }

  /**
   * Processa a resposta da API de forma inteligente. A API pode retornar múltiplas rotas, então
   * precisamos extrair a informação mais relevante.
   */
  public CalculoDeDistanciaResult processarResposta(
      DistanceMatrix matrix, String origem, String destino) {
    if (matrix.rows == null || matrix.rows.length == 0) {
      // throw new Exeção de projeto
    }

    DistanceMatrixRow row = matrix.rows[0];

    if (row.elements == null || row.elements.length == 0) {
      // throw new Exeção de projeto
    }

    DistanceMatrixElement element = row.elements[0];

    if (element.status != DistanceMatrixElementStatus.OK) {
      String mensagemErro =
          switch (element.status) {
            case NOT_FOUND -> "Endereço não encontrado";
            case ZERO_RESULTS -> "Não há rota disponível entre os endereços";
            default -> "Erro desconhecido: " + element.status;
          };
      // throw new Exeção de projeto
    }

    Distance distance = element.distance;
    Duration duration = element.duration;

    return CalculoDeDistanciaResult.builder()
        .distanciaKm(BigDecimal.valueOf(distance.inMeters / 1000.0))
        .duracaoMinutos(duration.inSeconds / 60)
        .enderecoOrigemFormatado(matrix.originAddresses[0])
        .enderecoDestinoFormatado(matrix.destinationAddresses[0])
        .build();
  }
}
