package br.com.cegonhaexpress.cegonha_express.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculoDeDistanciaResult {

  private BigDecimal distanciaKm;
  private Long duracaoMinutos;
  private String enderecoOrigemFormatado;
  private String enderecoDestinoFormatado;

  /** Retorna a distância formatada para exibição. Exemplo: "142.5 Km" */
  public String getDistanciaFormatada() {
    return String.format("%.1f Km", distanciaKm);
  }

  /**
   * Retorna a duração formatada com dias, horas e minutos. Exemplos: "2 dias, 5h e 30min", "8h e
   * 15min", "45min"
   */
  public String getDuracaoFormatada() {
    long dias = duracaoMinutos / (60 * 24);
    long horas = (duracaoMinutos % (60 * 24)) / 60;
    long minutos = duracaoMinutos % 60;

    StringBuilder resultado = new StringBuilder();

    if (dias > 0) {
      resultado.append(String.format("%d dia%s", dias, dias > 1 ? "s" : ""));
    }

    if (horas > 0) {
      if (resultado.length() > 0) {
        resultado.append(", ");
      }
      resultado.append(String.format("%dh", horas));
    }

    if (minutos > 0) {
      if (resultado.length() > 0) {
        resultado.append(" e ");
      }
      resultado.append(String.format("%dmin", minutos));
    }

    // Caso especial: 0 minutos
    if (resultado.length() == 0) {
      return "0min";
    }

    return resultado.toString();
  }
}
