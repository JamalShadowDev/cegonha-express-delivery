package br.com.cegonhaexpress.cegonha_express.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO padrão para respostas de erro da API.
 *
 * <p>Fornece estrutura consistente para todos os tipos de erro, facilitando o tratamento no
 * frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime timestamp;

  private int status;
  private String error;
  private String message;
  private String path;
}
