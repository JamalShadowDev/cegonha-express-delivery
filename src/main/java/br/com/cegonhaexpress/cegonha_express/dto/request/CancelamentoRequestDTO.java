package br.com.cegonhaexpress.cegonha_express.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisições de cancelamento de encomenda.
 *
 * <p>Contém o motivo obrigatório para justificar o cancelamento, que será registrado nas
 * observações da encomenda.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelamentoRequestDTO {

  @NotBlank(message = "Motivo do cancelamento é obrigatório")
  @Size(max = 500, message = "Motivo deve ter no máximo 500 caracteres")
  private String motivo;
}
