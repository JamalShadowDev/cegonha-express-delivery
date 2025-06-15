package br.com.cegonhaexpress.cegonha_express.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class GoogleMapsIntegrationException extends RuntimeException {

  public GoogleMapsIntegrationException(String message) {
    super(message);
  }

  public GoogleMapsIntegrationException(String message, Throwable cause) {
    super(message, cause);
  }
}
