package br.com.cegonhaexpress.cegonha_express.model.entity;

import br.com.cegonhaexpress.cegonha_express.model.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Table(
    name = "cliente",
    indexes = {
      @Index(name = "idx_cliente_cpf", columnList = "cpf", unique = true),
      @Index(name = "idx_cliente_email", columnList = "email", unique = true)
    })
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    exclude = {"enderecos"})
public class Cliente extends BaseEntity {

  @NotBlank(message = "Nome do cliente é obrigatório")
  @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
  @Column(name = "nome", nullable = false, length = 150)
  private String nome;

  @NotBlank(message = "E-mail do cliente é obrigatório")
  @Email(message = "E-mail deve ter formato válido")
  @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;

  @Pattern(
      regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$|^\\(?\\d{2}\\)?\\s?\\d{4}-?\\d{4}$",
      message = "Telefone deve ter formato válido (11) 99999-9999 ou (11) 3333-4444")
  @Size(max = 16, message = "Telefone deve ter no máximo 16 caracteres")
  @Column(name = "telefone")
  private String telefone;

  @NotBlank(message = "CPF do cliente é obrigatório")
  @Pattern(
      regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$",
      message = "CPF deve ter formato válido (000.000.000-00 ou 00000000000)")
  @Size(max = 14, message = "CPF deve ter no máximo 14 caracteres")
  @Column(name = "cpf", nullable = false, length = 14, unique = true)
  private String cpf;

  /**
   * Relacionamento com Endereços Um cliente pode ter diversos endereços
   *
   * <p>Importante:
   *
   * <p>mapeado por cliente (endereço é o lado proprietário) operações em cliente são cascadeadas
   * para endereço Endereços são carregados só quando acessados (Lazy) Remove endereços órfãos do
   * banco de dados
   */
  @OneToMany(
      mappedBy = "cliente",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<Endereco> enderecos = new ArrayList<>();

  /**
   * Construtor para criação rápida de cliente com dados essenciais.
   *
   * @param nome Nome completo do cliente
   * @param email Email válido
   * @param cpf CPF no formato válido
   */
  public Cliente(String nome, String email, String cpf) {
    this.nome = nome;
    this.email = email;
    this.cpf = cpf;
    this.enderecos = new ArrayList<>();
  }

  /**
   * Construtor completo sem endereços.
   *
   * @param nome Nome completo do cliente
   * @param email Email válido
   * @param telefone Telefone no formato válido
   * @param cpf CPF no formato válido
   */
  public Cliente(String nome, String email, String telefone, String cpf) {
    this(nome, email, cpf);
    this.telefone = telefone;
  }

  /**
   * Retorna o CPF apenas com números (limpo). Útil para integrações que não aceitam formatação.
   *
   * @return CPF sem pontos e hífen
   */
  public String getCpfLimpo() {
    return cpf != null ? cpf.replaceAll("\\D", "") : null;
  }

  /**
   * Formata o CPF com máscara (000.000.000-00).
   *
   * @return CPF formatado
   */
  public String getCpfFormatado() {
    String cpfLimpo = getCpfLimpo();
    if (cpfLimpo != null && cpfLimpo.length() == 11) {
      return cpfLimpo.substring(0, 3)
          + "."
          + cpfLimpo.substring(3, 6)
          + "."
          + cpfLimpo.substring(6, 9)
          + "-"
          + cpfLimpo.substring(9);
    }
    return cpf;
  }

  /**
   * Retorna o telefone apenas com números (limpo).
   *
   * @return Telefone sem formatação
   */
  public String getTelefoneLimpo() {
    return telefone != null ? telefone.replaceAll("\\D", "") : null;
  }

  /**
   * Formata o telefone com máscara brasileira.
   *
   * @return Telefone formatado (11) 99999-9999 ou (11) 3333-4444
   */
  public String getTelefoneFormatado() {
    String telLimpo = getTelefoneLimpo();
    if (telLimpo != null) {
      if (telLimpo.length() == 11) {
        // Celular: (11) 99999-9999
        return "("
            + telLimpo.substring(0, 2)
            + ") "
            + telLimpo.substring(2, 7)
            + "-"
            + telLimpo.substring(7);
      } else if (telLimpo.length() == 10) {
        // Fixo: (11) 3333-4444
        return "("
            + telLimpo.substring(0, 2)
            + ") "
            + telLimpo.substring(2, 6)
            + "-"
            + telLimpo.substring(6);
      }
    }
    return telefone;
  }

  /**
   * Verifica se o cliente possui dados completos.
   *
   * @return true se todos os dados obrigatórios estão preenchidos
   */
  public boolean isDadosCompletos() {
    return nome != null
        && !nome.trim().isEmpty()
        && email != null
        && !email.trim().isEmpty()
        && cpf != null
        && !cpf.trim().isEmpty();
  }

  /** Callback executado antes da persistência. Normaliza os dados do cliente. */
  @PrePersist
  @PreUpdate
  private void normalizeData() {
    // Normaliza nome
    if (nome != null) {
      nome = nome.trim();
    }

    // Normaliza email para lowercase
    if (email != null) {
      email = email.trim().toLowerCase();
    }

    // Remove formatação de CPF e telefone se necessário
    if (cpf != null) {
      cpf = cpf.trim();
    }

    if (telefone != null) {
      telefone = telefone.trim();
      if (telefone.isEmpty()) {
        telefone = null;
      }
    }
  }

  /**
   * Adiciona um endereço à lista do cliente. Mantém a consistência bidirecional do relacionamento.
   *
   * @param endereco Endereço a ser adicionado
   */
  public void adicionarEndereco(Endereco endereco) {
    if (endereco != null) {
      this.enderecos.add(endereco);
      endereco.setCliente(this); // Mantém consistência bidirecional
    }
  }

  /**
   * Remove um endereço da lista do cliente. Mantém a consistência bidirecional do relacionamento.
   *
   * @param endereco Endereço a ser removido
   */
  public void removerEndereco(Endereco endereco) {
    if (endereco != null && this.enderecos.contains(endereco)) {
      this.enderecos.remove(endereco);
      endereco.setCliente(null); // Remove a referência
    }
  }

  /**
   * Retorna o endereço principal do cliente. Por convenção, considera o primeiro endereço como
   * principal.
   *
   * @return Endereço principal ou null se não houver endereços
   */
  public Endereco getEnderecoPrincipal() {
    return enderecos.isEmpty() ? null : enderecos.get(0);
  }

  /**
   * Verifica se o cliente possui endereços cadastrados.
   *
   * @return true se possui pelo menos um endereço
   */
  public boolean possuiEnderecos() {
    return !enderecos.isEmpty();
  }
}
