package br.com.cegonhaexpress.cegonha_express.model.entity;

import br.com.cegonhaexpress.cegonha_express.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "cliente")
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Cliente extends BaseEntity {

  @NotBlank(message = "Nome do cliente é obrigatório")
  @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
  @Column(name = "nome", nullable = false, length = 150)
  private String nome;

  @NotBlank(message = "E-mail do cliente é obrigatório")
  @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
  @Column(name = "email", nullable = false, length = 255)
  private String email;

  @Size(max = 16, message = "Telefone deve ter no máximo 16 caracteres")
  @Column(name = "telefone")
  private String telefone;

  @NotBlank(message = "CPF do cliente é obrigatório")
  @Size(max = 14, message = "CPF deve ter no máximo 14 caracteres")
  @Column(name = "cpf", nullable = false, length = 14)
  private String cpf;

  private List<Endereco> endereco;
}
