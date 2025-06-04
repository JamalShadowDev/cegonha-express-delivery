package br.com.cegonhaexpress.cegonha_express.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BebeResponseDTO {

  private String id;
  private String nome;
  private String descricao;
  private String acessorios;
  private BigDecimal peso_kg;
  private BigDecimal altura_cm;
}
