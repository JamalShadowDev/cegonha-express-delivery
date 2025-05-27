package br.com.cegonhaexpress.cegonha_express.config;

import com.google.maps.GeoApiContext;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuração do Google Maps para integração com APIs de distância.
 *
 * <p>Cria um contexto singleton thread-safe do Google Maps com timeouts otimizados (5s conexão, 10s
 * leitura/escrita) e retry automático (3 tentativas em 30s).
 *
 * <p>Requer configuração da API key via: google.maps.api.key=${GOOGLE_MAPS_API_KEY}
 *
 * @author Gabriel Coelho Soares
 * @see GeoApiContext
 * @see com.google.maps.DistanceMatrixApi
 * @see GoogleMapsDistanceService
 */
@Configuration
public class GoogleMapsConfig {

  @Value("${google.maps.api.key}")
  private String apiKey;

  /**
   * Cria o contexto principal para o uso do Google Maps - Distance Matrix Api. Este bean será
   * reutilizado em toda a aplicação (nos contextos corretos) evitando um overhead de criação
   * múltipla.
   */
  @Bean
  @Primary
  public GeoApiContext geoApiContext() {
    return new GeoApiContext.Builder()
        .apiKey(apiKey)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .maxRetries(3)
        .retryTimeout(30, TimeUnit.SECONDS)
        .build();
  }
}
