package br.com.cegonhaexpress.cegonha_express.service;

import br.com.cegonhaexpress.cegonha_express.dto.response.ViaCepResponseDto;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Serviço responsável por integrar com a API ViaCEP para validação e busca de endereços.
 *
 * @author Gabriel Coelho Soares
 */
@Service
public class ViaCepService {

  private static final Logger logger = LoggerFactory.getLogger(ViaCepService.class);
  private static final String VIA_CEP_API_URL = "https://viacep.com.br/ws/%s/json/";

  private final RestTemplate restTemplate;

  public ViaCepService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Busca informações de endereço através do CEP na API ViaCEP.
   *
   * @param cep CEP a ser consultado (formato: 00000000 ou 00000-000)
   * @return ViaCepResponseDto com dados do endereço ou null se não encontrado
   */
  public ViaCepResponseDto buscarEnderecoPorCep(String cep) {
    try {
      // Limpa o CEP removendo caracteres especiais
      String cepLimpo = limparCep(cep);

      // Valida formato do CEP
      if (!isValidCepFormat(cepLimpo)) {
        logger.warn("CEP inválido fornecido: {}", cep);
        return null;
      }

      // Monta URL da API
      String url = String.format(VIA_CEP_API_URL, cepLimpo);
      logger.info("Consultando ViaCEP para CEP: {}", cepLimpo);

      // Faz a requisição - ✅ USA O RESTTEMPLATE INJETADO
      ViaCepResponseDto response = restTemplate.getForObject(url, ViaCepResponseDto.class);

      // Verifica se houve erro na resposta
      if (response != null && response.isErro()) {
        logger.warn("CEP não encontrado na base ViaCEP: {}", cep);
        return null;
      }

      logger.info(
          "Endereço encontrado para CEP {}: {}, {}/{}",
          cepLimpo,
          response != null ? response.getLogradouro() : "null",
          response != null ? response.getLocalidade() : "null",
          response != null ? response.getUf() : "null");

      return response;

    } catch (RestClientException e) {
      logger.error("Erro ao consultar ViaCEP para CEP {}: {}", cep, e.getMessage());
      return null;
    } catch (Exception e) {
      logger.error("Erro inesperado ao consultar ViaCEP para CEP {}: {}", cep, e.getMessage(), e);
      return null;
    }
  }

  /**
   * Converte ViaCepResponseDto para entidade Endereco do sistema.
   *
   * @param viaCepResponse Resposta da API ViaCEP
   * @param numero Número do endereço (fornecido pelo usuário)
   * @param complemento Complemento do endereço (opcional)
   * @return Endereco populado com dados da API
   */
  public Endereco converterParaEndereco(
      ViaCepResponseDto viaCepResponse, String numero, String complemento) {
    if (viaCepResponse == null) {
      return null;
    }

    try {
      UF uf = UF.valueOf(viaCepResponse.getUf().toUpperCase());

      return new Endereco(
          formatarCep(viaCepResponse.getCep()),
          viaCepResponse.getLogradouro(),
          numero,
          viaCepResponse.getBairro(),
          viaCepResponse.getLocalidade(),
          uf);

    } catch (IllegalArgumentException e) {
      logger.error("UF inválida retornada pela ViaCEP: {}", viaCepResponse.getUf());
      return null;
    }
  }

  /**
   * Busca e converte diretamente para entidade Endereco.
   *
   * @param cep CEP a ser consultado
   * @param numero Número do endereço
   * @param complemento Complemento (opcional)
   * @return Endereco populado ou null se não encontrado
   */
  public Endereco buscarEConverterEndereco(String cep, String numero, String complemento) {
    ViaCepResponseDto response = buscarEnderecoPorCep(cep);
    return converterParaEndereco(response, numero, complemento);
  }

  /**
   * Valida se um CEP está em formato válido.
   *
   * @param cep CEP a ser validado
   * @return true se válido, false caso contrário
   */
  public boolean validarCep(String cep) {
    if (cep == null || cep.trim().isEmpty()) {
      return false;
    }

    String cepLimpo = limparCep(cep);

    // ✅ CORREÇÃO: Só valida formato, não faz chamada à API
    return isValidCepFormat(cepLimpo);
  }

  // ==================== MÉTODOS UTILITÁRIOS ====================

  /** Remove caracteres não numéricos do CEP. */
  private String limparCep(String cep) {
    return cep != null ? cep.replaceAll("\\D", "") : "";
  }

  /** Formata CEP com hífen (00000-000). */
  private String formatarCep(String cep) {
    String cepLimpo = limparCep(cep);
    if (cepLimpo.length() == 8) {
      return cepLimpo.substring(0, 5) + "-" + cepLimpo.substring(5);
    }
    return cep;
  }

  /** Valida formato numérico do CEP (8 dígitos). */
  private boolean isValidCepFormat(String cepLimpo) {
    return cepLimpo != null && cepLimpo.matches("\\d{8}");
  }
}
