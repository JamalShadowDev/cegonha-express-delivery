package br.com.cegonhaexpress.cegonha_express.dto.request;

import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferência de dados de endereços entre camadas.
 *
 * <p>Utilizado tanto para receber endereços de clientes via API/formulários quanto para integração
 * com serviços externos como ViaCEP. Contém validações de formato e métodos utilitários para
 * formatação e conversão.
 *
 * <p><strong>Casos de Uso:</strong>
 *
 * <ul>
 *   <li>Recebimento de endereços de destino em EncomendaRequestDTO
 *   <li>Integração com ViaCepService para autocompletar dados
 *   <li>Validação e formatação de endereços no frontend
 * </ul>
 *
 * @author Gabriel Coelho Soares
 * @see Endereco
 * @see br.com.cegonhaexpress.cegonha_express.service.ViaCepService
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoDTO {

  /**
   * CEP do endereço no formato brasileiro. Aceita tanto "12345678" quanto "12345-678". Usado para
   * integração com ViaCEP.
   */
  @NotBlank(message = "CEP é Obrigatório")
  @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve ter formato válido (00000-000)")
  private String cep;

  /**
   * Nome da rua, avenida ou logradouro. Preenchido automaticamente via ViaCEP ou informado
   * manualmente pelo usuário.
   */
  @NotBlank(message = "Logradouro é Obrigatório")
  private String logradouro;

  /**
   * Número do endereço. Campo opcional. Se não informado, será definido como "s/n" na conversão
   * para entidade.
   */
  private String numero;

  /**
   * Complemento adicional do endereço (apartamento, sala, bloco). Campo completamente opcional para
   * detalhamento da localização.
   */
  private String complemento;

  /** Nome do bairro ou distrito. Preenchido automaticamente via ViaCEP ou informado manualmente. */
  @NotBlank(message = "Bairro é Obrigatório")
  private String bairro;

  /** Nome da cidade/município. Usado para cálculos de distância e validações geográficas. */
  @NotBlank(message = "Cidade é Obrigatório")
  private String cidade;

  /**
   * Sigla da Unidade Federativa (estado) como String. Será convertido para enum UF na transformação
   * para entidade.
   */
  @NotBlank(message = "UF é Obrigatório")
  private String uf;

  /**
   * Ponto de referência opcional para facilitar localização. Ex: "Próximo ao shopping", "Em frente
   * à padaria São João"
   */
  private String referencia;

  /**
   * Converte DTO para entidade Endereco do domínio.
   *
   * <p>Aplica transformações necessárias:
   *
   * <ul>
   *   <li>String "uf" → enum UF
   *   <li>Número null → "s/n"
   *   <li>Normalização automática via @PrePersist na entidade
   * </ul>
   *
   * @return Endereco com dados convertidos e normalizados
   * @throws IllegalArgumentException se UF inválida
   */
  public Endereco toEntity() {
    return new Endereco(
        cep,
        logradouro,
        this.numero != null ? this.numero : "s/n",
        bairro,
        cidade,
        UF.valueOf(uf.toUpperCase()));
  }

  /**
   * Verifica se o endereço possui dados mínimos necessários.
   *
   * <p>Valida apenas campos essenciais para funcionamento do sistema, ignorando campos opcionais
   * como número, complemento e referência.
   *
   * @return true se possui CEP, logradouro, cidade e UF preenchidos
   */
  public boolean isCompleto() {
    return cep != null
        && !cep.trim().isEmpty()
        && logradouro != null
        && !logradouro.trim().isEmpty()
        && cidade != null
        && !cidade.trim().isEmpty()
        && uf != null
        && !uf.trim().isEmpty();
  }

  /**
   * Gera representação textual completa do endereço.
   *
   * <p>Formato de saída otimizado para exibição ao usuário: <br>
   * <em>"Rua das Flores, 123 - Apto 45, Centro, São Paulo/SP - CEP: 01001-000"</em>
   *
   * <p>Campos opcionais (número, complemento) são incluídos apenas se preenchidos.
   *
   * @return String formatada do endereço completo
   */
  public String getEnderecoFormatado() {
    StringBuilder sb = new StringBuilder();
    sb.append(logradouro);

    if (numero != null && !numero.trim().isEmpty()) {
      sb.append(", ").append(numero);
    }

    if (complemento != null && !complemento.trim().isEmpty()) {
      sb.append(" - ").append(complemento);
    }

    sb.append(", ").append(bairro);
    sb.append(", ").append(cidade).append("/").append(uf);
    sb.append(" - CEP: ").append(cep);

    return sb.toString();
  }

  /**
   * Factory method para criar DTO a partir de entidade Endereco.
   *
   * <p>Útil para conversões no sentido inverso (entidade → DTO) em cenários como:
   *
   * <ul>
   *   <li>Retorno de endereços existentes via API
   *   <li>Preenchimento de formulários com dados salvos
   *   <li>Integração com serviços que consomem endereços
   * </ul>
   *
   * @param endereco Entidade Endereco do domínio
   * @return DTO com dados da entidade
   */
  public static EnderecoDTO fromEntity(Endereco endereco) {
    return new EnderecoDTO(
        endereco.getCep(),
        endereco.getLogradouro(),
        endereco.getNumero(),
        endereco.getComplemento(),
        endereco.getBairro(),
        endereco.getCidade(),
        endereco.getUf().name(),
        endereco.getPontoReferencia());
  }
}
