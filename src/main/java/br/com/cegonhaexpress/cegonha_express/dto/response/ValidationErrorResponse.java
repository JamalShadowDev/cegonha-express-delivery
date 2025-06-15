package br.com.cegonhaexpress.cegonha_express.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO específico para erros de validação Bean Validation.
 *
 * <p>Usa composição ao invés de herança para evitar problemas com Lombok @Builder em hierarquias de
 * classes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime timestamp;

  private int status;
  private String error;
  private String message;
  private String path;

  /**
   * Mapa com campos que falharam na validação. Chave: nome do campo Valor: mensagem de erro
   * específica
   */
  private Map<String, String> fieldErrors;
}
