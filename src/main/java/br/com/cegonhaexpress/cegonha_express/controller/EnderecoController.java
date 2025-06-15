package br.com.cegonhaexpress.cegonha_express.controller;

import br.com.cegonhaexpress.cegonha_express.dto.response.ErrorResponse;
import br.com.cegonhaexpress.cegonha_express.dto.response.ViaCepResponseDto;
import br.com.cegonhaexpress.cegonha_express.service.ViaCepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para operações com endereços e consulta de CEP.
 *
 * <p>Oferece serviços de consulta e validação de endereços brasileiros através da integração com a
 * API ViaCEP. Permite busca de informações completas de logradouro, bairro, cidade e UF utilizando
 * apenas o código postal.
 *
 * <p>Todos os tratamentos de exceção são delegados para o GlobalExceptionHandler, mantendo este
 * controller focado apenas na lógica de negócio de endereçamento.
 *
 * @author Gabriel Coelho Soares
 * @version 1.0
 * @since 2025-06-05
 * @see ViaCepService
 */
@RestController
@RequestMapping("/api/enderecos")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "Endereços",
    description =
        "API para consulta e validação de endereços brasileiros. Integração com ViaCEP para"
            + " obtenção de dados completos de logradouro através do CEP.")
public class EnderecoController {

  private final ViaCepService viaCepService;

  /**
   * Consulta informações completas de um endereço através do CEP.
   *
   * @param cep Código de Endereçamento Postal brasileiro (formato: 00000-000 ou 00000000)
   * @return Dados completos do endereço encontrado ou 404 se CEP não existir
   */
  @GetMapping("/cep/{cep}")
  @Operation(
      summary = "Consultar endereço por CEP",
      description =
          "Busca informações completas de um endereço brasileiro através do CEP utilizando a API"
              + " ViaCEP. Retorna dados como logradouro, bairro, cidade, UF, DDD e códigos do IBGE."
              + " Aceita CEPs nos formatos 00000-000 ou 00000000.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "CEP encontrado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ViaCepResponseDto.class),
                    examples =
                        @ExampleObject(
                            name = "CEP Encontrado",
                            value =
                                """
                                {
                                  "cep": "01001-000",
                                  "logradouro": "Praça da Sé",
                                  "complemento": "lado ímpar",
                                  "bairro": "Sé",
                                  "localidade": "São Paulo",
                                  "uf": "SP",
                                  "ibge": "3550308",
                                  "gia": "1004",
                                  "ddd": "11",
                                  "siafi": "7107"
                                }
                                """))),
        @ApiResponse(
            responseCode = "400",
            description = "Formato de CEP inválido",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "CEP Inválido",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 400,
                                  "error": "Parâmetro inválido",
                                  "message": "CEP precisa estar com formatação correta",
                                  "path": "/api/enderecos/cep/abc123"
                                }
                                """))),
        @ApiResponse(
            responseCode = "404",
            description = "CEP não encontrado na base de dados dos Correios",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "CEP Não Encontrado",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 404,
                                  "error": "Recurso não encontrado",
                                  "message": "CEP não encontrado na base de dados dos Correios",
                                  "path": "/api/enderecos/cep/99999-999"
                                }
                                """))),
        @ApiResponse(
            responseCode = "503",
            description = "Serviço ViaCEP temporariamente indisponível",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Serviço Indisponível",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 503,
                                  "error": "Serviço indisponível",
                                  "message": "ViaCEP temporariamente fora do ar. Tente novamente em alguns minutos.",
                                  "path": "/api/enderecos/cep/01001-000"
                                }
                                """)))
      })
  public ResponseEntity<ViaCepResponseDto> getCep(
      @Parameter(
              description = "Código de Endereçamento Postal brasileiro",
              example = "01001-000",
              required = true,
              schema =
                  @Schema(
                      type = "string",
                      pattern = "\\d{5}-?\\d{3}",
                      minLength = 8,
                      maxLength = 9,
                      example = "01001-000"))
          @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP precisa estar com formatação correta")
          @PathVariable
          String cep) {

    ViaCepResponseDto response = viaCepService.buscarEnderecoPorCep(cep);

    if (response != null) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
