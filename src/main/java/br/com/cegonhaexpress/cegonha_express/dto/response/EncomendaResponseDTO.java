package br.com.cegonhaexpress.cegonha_express.dto.response;

import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para criação de encomendas.
 *
 * <p>Retorna apenas informações essenciais ao usuário após confirmação do pedido, com dados
 * formatados para exibição direta no frontend.
 *
 * @author Gabriel Coelho Soares
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncomendaResponseDTO {

  /** Código único de rastreamento da encomenda. Formato: CE + timestamp + sufixo aleatório */
  private String codigo;

  /** Status atual da encomenda formatado para exibição. Ex: "Pendente", "Confirmada", etc. */
  private String status;

  /** Valor do frete formatado como moeda brasileira. Ex: "R$ 45,50" */
  private String valorFrete;

  /** Tempo estimado de entrega formatado. Ex: "3 dias úteis", "1 dia útil" */
  private String tempoEstimadoEntrega;

  /**
   * Factory method para criar DTO a partir de entidade Encomenda.
   *
   * <p>IMPORTANTE: Encomenda deve ter Frete carregado (JOIN FETCH) para evitar
   * LazyInitializationException.
   *
   * @param encomenda Encomenda com frete carregado
   * @return DTO formatado para resposta
   */
  public static EncomendaResponseDTO fromEntity(Encomenda encomenda) {
    return new EncomendaResponseDTO(
        encomenda.getCodigo(),
        encomenda.getStatus().getDescricao(),
        encomenda.getFrete().getValorFormatado(),
        encomenda.getFrete().getPrazoFormatado());
  }
}
