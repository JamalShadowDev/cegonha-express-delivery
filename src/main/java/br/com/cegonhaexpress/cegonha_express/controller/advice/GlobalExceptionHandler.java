package br.com.cegonhaexpress.cegonha_express.controller.advice;

import br.com.cegonhaexpress.cegonha_express.dto.response.ErrorResponse;
import br.com.cegonhaexpress.cegonha_express.dto.response.ValidationErrorResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Tratamento global de exceções para todos os controllers da aplicação.
 *
 * <p>Centraliza o handling de erros para manter consistência nas respostas e reduzir duplicação de
 * código nos controllers.
 *
 * @author Gabriel Coelho Soares
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /** Trata EntityNotFoundException (404 - Entidade não encontrada) */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFound(
      EntityNotFoundException e, WebRequest request) {

    log.warn("Entidade não encontrada: {}", e.getMessage());

    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Recurso não encontrado")
            .message(e.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  /**
   * Trata IllegalStateException (409 - Conflito de estado) Ex: Tentar avançar status de encomenda
   * já entregue
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalState(
      IllegalStateException e, WebRequest request) {

    log.warn("Estado inválido para operação: {}", e.getMessage());

    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Conflito de estado")
            .message(e.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  /**
   * Trata IllegalArgumentException (400 - Argumento inválido) Ex: CEP com formato inválido,
   * parâmetros incorretos
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException e, WebRequest request) {

    log.warn("Argumento inválido: {}", e.getMessage());

    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Argumento inválido")
            .message(e.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return ResponseEntity.badRequest().body(error);
  }

  /**
   * Trata MethodArgumentNotValidException (400 - Validação Bean Validation) Ex: @Valid falhou em
   * DTO
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationException(
      MethodArgumentNotValidException e, WebRequest request) {

    log.warn("Erro de validação: {}", e.getMessage());

    Map<String, String> errors = new HashMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    ValidationErrorResponse errorResponse =
        ValidationErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Erro de validação")
            .message("Dados fornecidos são inválidos")
            .path(request.getDescription(false).replace("uri=", ""))
            .fieldErrors(errors)
            .build();

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Trata ConstraintViolationException (400 - Validação de parâmetros) Ex: @PathVariable
   * com @Pattern inválido
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException e, WebRequest request) {

    log.warn("Violação de constraint: {}", e.getMessage());

    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Parâmetro inválido")
            .message(e.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return ResponseEntity.badRequest().body(error);
  }

  /** Trata exceções genéricas não mapeadas (500 - Erro interno) */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception e, WebRequest request) {

    log.error("Erro interno não tratado: ", e);

    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Erro interno do servidor")
            .message("Ocorreu um erro inesperado. Tente novamente mais tarde.")
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
      HttpMediaTypeNotSupportedException e, WebRequest request) {

    log.warn("Tipo de mídia não suportado: {}", e.getMessage());

    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
            .error("Tipo de mídia não suportado")
            .message("Content-Type deve ser application/json")
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
  }

  // ✅ CORREÇÃO 7: Tratamento para JSON malformado
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleJsonParseError(
      HttpMessageNotReadableException e, WebRequest request) {

    log.warn("Erro ao processar JSON: {}", e.getMessage());

    String message = "JSON malformado ou formato inválido";

    // Detectar tipos específicos de erro JSON
    if (e.getCause() instanceof JsonParseException) {
      message = "JSON malformado - verificar sintaxe";
    } else if (e.getCause() instanceof InvalidFormatException) {
      InvalidFormatException ife = (InvalidFormatException) e.getCause();
      message =
          String.format(
              "Tipo inválido para campo '%s': esperado %s",
              ife.getPath().get(0).getFieldName(), ife.getTargetType().getSimpleName());
    } else if (e.getCause() instanceof JsonMappingException) {
      message = "Erro no mapeamento JSON - verificar tipos de dados";
    }

    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Erro de formato JSON")
            .message(message)
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return ResponseEntity.badRequest().body(error);
  }

  // ✅ CORREÇÃO 8: Tratamento específico para erros de parsing JSON
  @ExceptionHandler({JsonParseException.class, JsonMappingException.class})
  public ResponseEntity<ErrorResponse> handleJsonException(Exception e, WebRequest request) {

    log.warn("Erro específico de JSON: {}", e.getMessage());

    ErrorResponse error =
        ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("JSON inválido")
            .message("Verificar formato e tipos de dados no JSON")
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

    return ResponseEntity.badRequest().body(error);
  }
}
