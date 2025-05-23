package br.com.cegonhaexpress.cegonha_express.model.entity;

import br.com.cegonhaexpress.cegonha_express.model.base.BaseEntity;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidade de endereços do sistema CegonhaExpress
 *
 * @author Gabriel Coelho Soares
 */
@Entity
@Table(
    name = "enderecos",
    indexes = {
      @Index(name = "idx_endereco_cep", columnList = "cep"),
      @Index(name = "idx_endereco_cidade", columnList = "cidade"),
      @Index(name = "idx_endereco_uf", columnList = "uf")
    })
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Endereco extends BaseEntity {

  @NotBlank(message = "CEP é Obrigatório")
  @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve ter formato válido (00000-000)")
  @Column(name = "cep", nullable = false, length = 9)
  private String cep;

  @NotBlank(message = "Logradouro é Obrigatório")
  @Size(max = 255, message = "Logradouro deve ter no máximo 255 caracteres")
  @Column(name = "logradouro", nullable = false)
  private String logradouro;

  @NotBlank(message = "Número é Obrigatório")
  @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
  @Column(name = "numero", nullable = false, length = 10)
  private String numero;

  @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
  @Column(name = "complemento", length = 100)
  private String complemento;

  @NotBlank(message = "Bairro é Obrigatório")
  @Size(max = 200, message = "Bairro deve ter no máximo 200 caracteres")
  @Column(name = "bairro", nullable = false, length = 200)
  private String bairro;

  @NotBlank(message = "Cidade é Obrigatório")
  @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
  @Column(name = "cidade", nullable = false, length = 100)
  private String cidade;

  @NotBlank(message = "UF é Obrigatório")
  @Enumerated(EnumType.STRING)
  @Column(name = "idx_endereco_uf", nullable = false, length = 2)
  private UF uf;

  @Size(max = 255, message = "Ponto de Referência deve ter no máximo 255 caracteres")
  @Column(name = "pontoReferencia")
  private String pontoReferencia;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cliente_id")
  private Cliente cliente;

  /**
   * Construtor para criação rápida de endereço com campos essenciais. Útil para testes e integração
   * com ViaCEP.
   *
   * @param cep CEP do endereço
   * @param logradouro Logradouro/rua
   * @param numero Número do endereço
   * @param bairro Bairro
   * @param cidade Cidade
   * @param uf Unidade Federativa
   */
  public Endereco(
      String cep, String logradouro, String numero, String bairro, String cidade, UF uf) {
    this.cep = cep;
    this.logradouro = logradouro;
    this.numero = numero;
    this.bairro = bairro;
    this.cidade = cidade;
    this.uf = uf;
  }

  /**
   * Formata o CEP removendo caracteres especiais. Útil para integração com APIs externas.
   *
   * @return CEP apenas com números
   */
  public String getCepLimpo() {
    return cep != null ? cep.replaceAll("\\D", "") : null;
  }

  /**
   * Formata o CEP com máscara (00000-000).
   *
   * @return CEP formatado
   */
  public String getCepFormatado() {
    String cepLimpo = getCepLimpo();
    if (cepLimpo != null && cepLimpo.length() == 8) {
      return cepLimpo.substring(0, 5) + "-" + cepLimpo.substring(5);
    }
    return cep;
  }

  /**
   * Retorna o endereço completo formatado em uma linha. Útil para exibição em relatórios e
   * interfaces.
   *
   * @return Endereço completo formatado
   */
  public String getEnderecoCompleto() {
    StringBuilder endereco = new StringBuilder();

    endereco.append(logradouro);
    endereco.append(", ").append(numero);

    if (complemento != null && !complemento.trim().isEmpty()) {
      endereco.append(" - ").append(complemento);
    }

    endereco.append(", ").append(bairro);
    endereco.append(", ").append(cidade);
    endereco.append(" - ").append(uf.name());
    endereco.append(", CEP: ").append(getCepFormatado());

    return endereco.toString();
  }

  /**
   * Verifica se o endereço está completo (todos os campos obrigatórios preenchidos).
   *
   * @return true se o endereço está completo
   */
  public boolean isCompleto() {
    return cep != null
        && !cep.trim().isEmpty()
        && logradouro != null
        && !logradouro.trim().isEmpty()
        && numero != null
        && !numero.trim().isEmpty()
        && bairro != null
        && !bairro.trim().isEmpty()
        && cidade != null
        && !cidade.trim().isEmpty()
        && uf != null;
  }

  /** Callback executado antes da persistência. Normaliza dados do endereço. */
  @PrePersist
  @PreUpdate
  private void normalizeData() {
    // Remove espaços extras e converte para maiúsculo campos relevantes
    if (cep != null) {
      cep = cep.trim().replaceAll("\\s+", "");
    }
    if (logradouro != null) {
      logradouro = logradouro.trim();
    }
    if (numero != null) {
      numero = numero.trim();
    }
    if (complemento != null) {
      complemento = complemento.trim();
      if (complemento.isEmpty()) {
        complemento = null;
      }
    }
    if (bairro != null) {
      bairro = bairro.trim();
    }
    if (cidade != null) {
      cidade = cidade.trim();
    }
    if (pontoReferencia != null) {
      pontoReferencia = pontoReferencia.trim();
      if (pontoReferencia.isEmpty()) {
        pontoReferencia = null;
      }
    }
  }
}
