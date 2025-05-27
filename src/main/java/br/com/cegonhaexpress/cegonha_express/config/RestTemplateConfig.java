package br.com.cegonhaexpress.cegonha_express.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração do RestTemplate para integração com APIs externas.
 *
 * <p>Configura timeouts apropriados para chamadas HTTP: - ConnectTimeout: 5 segundos - ReadTimeout:
 * 10 segundos
 *
 * @author Gabriel Coelho Soares
 */
@Configuration
public class RestTemplateConfig {

  /**
   * Cria instância configurada de RestTemplate como Bean Spring.
   *
   * @return RestTemplate com timeouts configurados
   */
  @Bean
  public RestTemplate createRestTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

    factory.setConnectTimeout(5000);
    factory.setReadTimeout(10000);
    RestTemplate novoRestTemplate = new RestTemplate(factory);
    return novoRestTemplate;
  }
}
