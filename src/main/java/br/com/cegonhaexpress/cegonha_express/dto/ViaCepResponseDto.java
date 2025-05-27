package br.com.cegonhaexpress.cegonha_express.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mapear resposta da API ViaCEP.
 *
 * <p>Representa exatamente a estrutura JSON retornada pela API: - Sucesso: retorna dados completos
 * do endereço - Erro: retorna {"erro": true} quando CEP não existe
 *
 * @author Gabriel Coelho Soares
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViaCepResponseDto {

  private String cep;
  private String logradouro;
  private String complemento;
  private String bairro;

  @JsonProperty("localidade")
  private String localidade;

  private String uf;
  private String ibge;
  private String gia;
  private String ddd;
  private String siafi;

  /** Campo retornado pela API quando CEP não é encontrado Presente apenas em respostas de erro */
  @JsonProperty("erro")
  private Boolean erro;

  /**
   * Verifica se houve erro na consulta do CEP.
   *
   * @return true se CEP não foi encontrado, false se foi encontrado
   */
  public boolean isErro() {
    return erro != null && erro;
  }

  /**
   * Verifica se o CEP foi encontrado com sucesso.
   *
   * @return true se CEP foi encontrado, false se houve erro
   */
  public boolean isCepEncontrado() {
    return !isErro() && cep != null && !cep.trim().isEmpty();
  }

  /**
   * Retorna o nome da cidade (alias para localidade). Facilita o uso no service layer.
   *
   * @return nome da cidade
   */
  public String getCidade() {
    return localidade;
  }

  /**
   * Verifica se a resposta contém dados válidos mínimos.
   *
   * @return true se tem pelo menos CEP e localidade
   */
  public boolean isValidResponse() {
    return cep != null
        && !cep.trim().isEmpty()
        && localidade != null
        && !localidade.trim().isEmpty()
        && uf != null
        && !uf.trim().isEmpty();
  }

  /**
   * Verifica se o endereço retornado está completo.
   *
   * @return true se todos os campos principais estão preenchidos
   */
  public boolean isEnderecoCompleto() {
    return isValidResponse()
        && logradouro != null
        && !logradouro.trim().isEmpty()
        && bairro != null
        && !bairro.trim().isEmpty();
  }

  /**
   * Retorna CEP sem formatação (apenas números).
   *
   * @return CEP limpo ou null se não disponível
   */
  public String getCepLimpo() {
    return cep != null ? cep.replaceAll("\\D", "") : null;
  }

  /**
   * Retorna DDD como número inteiro.
   *
   * @return DDD numérico ou null se não disponível/inválido
   */
  public Integer getDddNumerico() {
    if (ddd == null || ddd.trim().isEmpty()) {
      return null;
    }
    try {
      return Integer.parseInt(ddd.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /** Normaliza strings vazias para null. Útil para manter consistência nos dados. */
  public void normalizeFields() {
    cep = normalizeString(cep);
    logradouro = normalizeString(logradouro);
    complemento = normalizeString(complemento);
    bairro = normalizeString(bairro);
    localidade = normalizeString(localidade);
    uf = normalizeString(uf);
    ibge = normalizeString(ibge);
    gia = normalizeString(gia);
    ddd = normalizeString(ddd);
    siafi = normalizeString(siafi);
  }

  /** Converte string vazia para null. */
  private String normalizeString(String value) {
    return (value != null && value.trim().isEmpty()) ? null : value;
  }

  // ==================== MÉTODOS DE APRESENTAÇÃO ====================

  /**
   * Retorna representação resumida para debug.
   *
   * @return string no formato "CEP: 01001-000, São Paulo/SP"
   */
  @Override
  public String toString() {
    if (isErro()) {
      return "ViaCepResponseDto{erro=true}";
    }

    return String.format(
        "ViaCepResponseDto{cep='%s', localidade='%s', uf='%s', logradouro='%s'}",
        cep, localidade, uf, logradouro);
  }

  /**
   * Retorna resumo do endereço encontrado.
   *
   * @return endereço formatado ou mensagem de erro
   */
  public String getResumo() {
    if (isErro()) {
      return "CEP não encontrado";
    }

    if (!isValidResponse()) {
      return "Resposta inválida da API";
    }

    return String.format("%s, %s, %s", localidade, uf, cep);
  }
}
