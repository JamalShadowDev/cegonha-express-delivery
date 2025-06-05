package br.com.cegonhaexpress.cegonha_express.controller;

import br.com.cegonhaexpress.cegonha_express.dto.request.CancelamentoRequestDTO;
import br.com.cegonhaexpress.cegonha_express.dto.request.EncomendaRequestDTO;
import br.com.cegonhaexpress.cegonha_express.dto.response.BebeResponseDTO;
import br.com.cegonhaexpress.cegonha_express.dto.response.EncomendaResponseDTO;
import br.com.cegonhaexpress.cegonha_express.dto.response.ErrorResponse;
import br.com.cegonhaexpress.cegonha_express.dto.response.ValidationErrorResponse;
import br.com.cegonhaexpress.cegonha_express.model.enums.StatusEncomenda;
import br.com.cegonhaexpress.cegonha_express.repository.EncomendaRepository;
import br.com.cegonhaexpress.cegonha_express.service.EncomendaService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações com encomendas do sistema CegonhaExpress.
 *
 * <p>Gerencia o ciclo completo de vida das encomendas, desde a criação até o acompanhamento e
 * cancelamento. Integra cálculo de frete em tempo real usando Google Maps Distance Matrix API e
 * validação automática de endereços via ViaCEP.
 *
 * <p>Todos os tratamentos de exceção são delegados para o GlobalExceptionHandler, mantendo este
 * controller focado apenas na lógica de negócio.
 *
 * @author Gabriel Coelho Soares
 * @version 1.1
 * @since 2025-06-05
 */
@RestController
@RequestMapping("/api/encomendas")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "Encomendas",
    description =
        "API para gerenciamento completo de encomendas de bebês reborn. "
            + "Inclui criação, consulta, acompanhamento de status e cancelamento de entregas.")
public class EncomendaController {

  private final EncomendaService encomendaService;
  private final EncomendaRepository encomendaRepository;
  private final ObjectMapper objectMapper;

  @Value("classpath:catalogo-bebes.json")
  private Resource catalogoResource;

  /**
   * Lista todas as encomendas cadastradas no sistema.
   *
   * @return Lista de encomendas ou 204 No Content se não houver encomendas
   */
  @GetMapping
  @Operation(
      summary = "Listar todas as encomendas",
      description =
          "Retorna uma lista completa de todas as encomendas cadastradas no sistema, "
              + "incluindo informações de status, frete e prazos de entrega.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de encomendas retornada com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @ArraySchema(schema = @Schema(implementation = EncomendaResponseDTO.class)),
                    examples =
                        @ExampleObject(
                            name = "Lista de Encomendas",
                            value =
                                """
                                [
                                  {
                                    "codigo": "CE1234567890123",
                                    "status": "Em Trânsito",
                                    "valorFrete": "R$ 45,50",
                                    "tempoEstimadoEntrega": "3 dias úteis"
                                  },
                                  {
                                    "codigo": "CE9876543210987",
                                    "status": "Entregue",
                                    "valorFrete": "R$ 32,75",
                                    "tempoEstimadoEntrega": "1 dia útil"
                                  }
                                ]
                                """))),
        @ApiResponse(responseCode = "204", description = "Nenhuma encomenda encontrada no sistema"),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<List<EncomendaResponseDTO>> getEncomendas() {
    List<EncomendaResponseDTO> encomendas = encomendaService.buscarTodasAsEncomendas();

    return encomendas.isEmpty()
        ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(encomendas);
  }

  /**
   * Cria uma nova encomenda no sistema.
   *
   * @param dto Dados da encomenda a ser criada
   * @return Encomenda criada com informações de frete calculado
   */
  @PostMapping(consumes = "application/json")
  @Operation(
      summary = "Criar nova encomenda",
      description =
          "Cria uma nova encomenda de entrega de bebê reborn. "
              + "Calcula automaticamente o frete usando distância real via Google Maps, "
              + "valida o endereço de destino via ViaCEP e gera código único de rastreamento.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Encomenda criada com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EncomendaResponseDTO.class),
                    examples =
                        @ExampleObject(
                            name = "Encomenda Criada",
                            value =
                                """
                                {
                                  "codigo": "CE1735834567123",
                                  "status": "Pendente",
                                  "valorFrete": "R$ 67,25",
                                  "tempoEstimadoEntrega": "5 dias úteis"
                                }
                                """))),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Erro de Validação",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 400,
                                  "error": "Erro de validação",
                                  "message": "Dados fornecidos são inválidos",
                                  "path": "/api/encomendas",
                                  "fieldErrors": {
                                    "enderecoDestino.cep": "CEP deve ter formato válido (00000-000)",
                                    "descricaoBebe": "Descrição do bebê é obrigatória"
                                  }
                                }
                                """))),
        @ApiResponse(
            responseCode = "415",
            description = "Tipo de mídia não suportado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "503",
            description = "Serviço indisponível (erro na integração com Google Maps ou ViaCEP)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Dados para criação da encomenda",
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = EncomendaRequestDTO.class),
              examples =
                  @ExampleObject(
                      name = "Exemplo de Encomenda",
                      value =
                          """
                          {
                            "enderecoDestino": {
                              "cep": "01001-000",
                              "logradouro": "Praça da Sé",
                              "numero": "123",
                              "complemento": "Apto 45",
                              "bairro": "Sé",
                              "cidade": "São Paulo",
                              "uf": "SP",
                              "referencia": "Próximo à Catedral"
                            },
                            "tipoEntrega": "PADRAO",
                            "descricaoBebe": "Bebê Alice, 50cm, cabelo loiro cacheado, olhos azuis, vestido rosa com laço",
                            "pesoKg": 2.5,
                            "alturaCm": 50.0,
                            "valorDeclarado": 150.00
                          }
                          """)))
  public ResponseEntity<EncomendaResponseDTO> createEncomenda(
      @Valid @RequestBody EncomendaRequestDTO dto) {

    EncomendaResponseDTO encomenda = encomendaService.criaEncomenda(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(encomenda);
  }

  /**
   * Lista apenas encomendas ativas (exclui entregues e canceladas).
   *
   * @return Lista de encomendas ativas ou 204 No Content se vazia
   */
  @GetMapping("/ativas")
  @Operation(
      summary = "Listar encomendas ativas",
      description =
          "Retorna apenas as encomendas que estão em andamento, "
              + "excluindo aquelas que já foram entregues ou canceladas. "
              + "Útil para acompanhamento operacional e dashboard de status.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de encomendas ativas retornada com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    array =
                        @ArraySchema(schema = @Schema(implementation = EncomendaResponseDTO.class)),
                    examples =
                        @ExampleObject(
                            name = "Encomendas Ativas",
                            value =
                                """
                                [
                                  {
                                    "codigo": "CE1234567890123",
                                    "status": "Pendente",
                                    "valorFrete": "R$ 45,50",
                                    "tempoEstimadoEntrega": "3 dias úteis"
                                  },
                                  {
                                    "codigo": "CE9876543210987",
                                    "status": "Em Trânsito",
                                    "valorFrete": "R$ 32,75",
                                    "tempoEstimadoEntrega": "1 dia útil"
                                  }
                                ]
                                """))),
        @ApiResponse(responseCode = "204", description = "Nenhuma encomenda ativa encontrada"),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public ResponseEntity<List<EncomendaResponseDTO>> getEncomendasAtivas() {
    List<StatusEncomenda> statusExcluidos =
        List.of(StatusEncomenda.ENTREGUE, StatusEncomenda.CANCELADA);

    List<EncomendaResponseDTO> encomendas =
        encomendaService.buscarPorStatusDiferentesDe(statusExcluidos);

    return encomendas.isEmpty()
        ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(encomendas);
  }

  /**
   * Busca encomenda específica pelo código de rastreamento.
   *
   * @param codigo Código único da encomenda (formato: CE + 13 dígitos)
   * @return Encomenda encontrada ou 404 se não existir
   */
  @GetMapping("/{codigo}")
  @Operation(
      summary = "Buscar encomenda por código",
      description =
          "Localiza uma encomenda específica usando seu código único de rastreamento. O código é"
              + " gerado automaticamente no momento da criação e segue o formato CE + timestamp +"
              + " sufixo aleatório.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Encomenda encontrada com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EncomendaResponseDTO.class),
                    examples =
                        @ExampleObject(
                            name = "Encomenda Encontrada",
                            value =
                                """
                                {
                                  "codigo": "CE1234567890123",
                                  "status": "Em Trânsito",
                                  "valorFrete": "R$ 45,50",
                                  "tempoEstimadoEntrega": "3 dias úteis"
                                }
                                """))),
        @ApiResponse(
            responseCode = "400",
            description = "Formato de código inválido",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Código Inválido",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 400,
                                  "error": "Parâmetro inválido",
                                  "message": "Código precisa estar com formatação correta",
                                  "path": "/api/encomendas/ABC123"
                                }
                                """))),
        @ApiResponse(
            responseCode = "404",
            description = "Encomenda não encontrada",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Encomenda Não Encontrada",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 404,
                                  "error": "Recurso não encontrado",
                                  "message": "Não existe uma encomenda com este Código",
                                  "path": "/api/encomendas/CE9999999999999"
                                }
                                """)))
      })
  public ResponseEntity<EncomendaResponseDTO> getSingleEncomenda(
      @Parameter(
              description = "Código único da encomenda",
              example = "CE1234567890123",
              required = true,
              schema =
                  @Schema(type = "string", pattern = "^CE\\d+$", minLength = 15, maxLength = 20))
          @Valid
          @Pattern(regexp = "^CE\\d+$", message = "Código precisa estar com formatação correta")
          @PathVariable
          String codigo) {

    EncomendaResponseDTO encomenda = encomendaService.buscarPorCodigo(codigo);
    return ResponseEntity.ok(encomenda);
  }

  /**
   * Avança o status da encomenda para o próximo estado válido.
   *
   * @param codigo Código único da encomenda (formato: CE + 13 dígitos)
   * @return Novo status após a transição
   */
  @PutMapping("/{codigo}/status")
  @Operation(
      summary = "Avançar status da encomenda",
      description =
          "Avança o status da encomenda para o próximo estado válido na sequência: "
              + "PENDENTE → CONFIRMADA → EM_TRANSITO → ENTREGUE. "
              + "Apenas encomendas ativas podem ter status avançado. "
              + "Utiliza o código de rastreamento para identificar a encomenda.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Status avançado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatusEncomenda.class),
                    examples =
                        @ExampleObject(name = "Status Atualizado", value = "\"CONFIRMADA\""))),
        @ApiResponse(
            responseCode = "204",
            description =
                "Nenhuma transição de status foi possível (encomenda já finalizada ou cancelada)"),
        @ApiResponse(
            responseCode = "400",
            description = "Formato de código inválido",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Código Inválido",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 400,
                                  "error": "Parâmetro inválido",
                                  "message": "Código precisa estar no formato correto",
                                  "path": "/api/encomendas/ABC123/status"
                                }
                                """))),
        @ApiResponse(
            responseCode = "404",
            description = "Encomenda não encontrada",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Encomenda Não Encontrada",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 404,
                                  "error": "Recurso não encontrado",
                                  "message": "Não existe uma Encomenda com este código",
                                  "path": "/api/encomendas/CE9999999999999/status"
                                }
                                """))),
        @ApiResponse(
            responseCode = "409",
            description = "Conflito de estado (tentativa de avançar status de encomenda inválida)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Conflito de Estado",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 409,
                                  "error": "Conflito de estado",
                                  "message": "Só é possível confirmar encomendas pendentes",
                                  "path": "/api/encomendas/CE1234567890123/status"
                                }
                                """)))
      })
  public ResponseEntity<StatusEncomenda> updateStatus(
      @Parameter(
              description = "Código único da encomenda",
              example = "CE1234567890123",
              required = true,
              schema =
                  @Schema(type = "string", pattern = "^CE\\d+$", minLength = 15, maxLength = 20))
          @PathVariable
          @Pattern(regexp = "^CE\\d+$", message = "Código precisa estar no formato correto")
          String codigo) {

    StatusEncomenda novoStatus = encomendaService.avancarStatus(codigo);

    return novoStatus != null ? ResponseEntity.ok(novoStatus) : ResponseEntity.noContent().build();
  }

  /**
   * Cancela uma encomenda com motivo obrigatório.
   *
   * @param id ID numérico da encomenda
   * @param dto DTO contendo o motivo do cancelamento
   * @return Status após cancelamento (sempre CANCELADA se bem-sucedido)
   */
  @PutMapping(value = "/{codigo}/cancelar", consumes = "application/json")
  @Operation(
      summary = "Cancelar encomenda",
      description =
          "Cancela uma encomenda ativa, registrando o motivo nas observações. "
              + "Apenas encomendas que ainda não foram entregues podem ser canceladas.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Encomenda cancelada com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatusEncomenda.class),
                    examples =
                        @ExampleObject(name = "Cancelamento Realizado", value = "\"CANCELADA\""))),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de cancelamento inválidos",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Motivo Inválido",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 400,
                                  "error": "Erro de validação",
                                  "message": "Dados fornecidos são inválidos",
                                  "path": "/api/encomendas/1/cancelar",
                                  "fieldErrors": {
                                    "motivo": "Motivo do cancelamento é obrigatório"
                                  }
                                }
                                """))),
        @ApiResponse(
            responseCode = "404",
            description = "Encomenda não encontrada",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Encomenda não pode ser cancelada (já entregue)",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Cancelamento Impossível",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 409,
                                  "error": "Conflito de estado",
                                  "message": "Não é possível cancelar encomendas já entregues",
                                  "path": "/api/encomendas/1/cancelar"
                                }
                                """)))
      })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Motivo do cancelamento",
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CancelamentoRequestDTO.class),
              examples =
                  @ExampleObject(
                      name = "Cancelamento",
                      value =
                          """
                          {
                            "motivo": "Cliente solicitou cancelamento devido a mudança de endereço"
                          }
                          """)))
  public ResponseEntity<StatusEncomenda> cancelarEncomenda(
      @Parameter(description = "ID numérico da encomenda", example = "1", required = true)
          @PathVariable
          @Pattern(regexp = "^CE\\d+$", message = "Codigo precisa estar no formato correto")
          String codigo,
      @Valid @RequestBody CancelamentoRequestDTO dto) {
    StatusEncomenda status = encomendaService.cancelarEncomendaPorCodigo(codigo, dto.getMotivo());
    return ResponseEntity.ok(status);
  }

  /**
   * Lista o catálogo de bebês reborn disponíveis.
   *
   * @return Lista de bebês disponíveis com especificações
   */
  @GetMapping("/bebes")
  @Operation(
      summary = "Listar catálogo de bebês",
      description =
          "Retorna o catálogo completo de bebês reborn disponíveis para entrega, "
              + "incluindo especificações detalhadas como peso, altura, acessórios e descrição.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Catálogo de bebês retornado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = BebeResponseDTO.class)),
                    examples =
                        @ExampleObject(
                            name = "Catálogo de Bebês",
                            value =
                                """
                                [
                                  {
                                    "id": "BB001",
                                    "nome": "Alice",
                                    "descricao": "Bebê reborn com cabelo loiro cacheado e olhos azuis",
                                    "acessorios": "Vestido rosa, sapatinhos, chupeta",
                                    "peso_kg": 2.5,
                                    "altura_cm": 50.0
                                  },
                                  {
                                    "id": "BB002",
                                    "nome": "Miguel",
                                    "descricao": "Bebê reborn com cabelo castanho e olhos verdes",
                                    "acessorios": "Macacão azul, boné, mamadeira",
                                    "peso_kg": 2.8,
                                    "altura_cm": 52.0
                                  }
                                ]
                                """))),
        @ApiResponse(
            responseCode = "500",
            description = "Erro ao carregar catálogo",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples =
                        @ExampleObject(
                            name = "Erro no Catálogo",
                            value =
                                """
                                {
                                  "timestamp": "2025-01-15 14:30:00",
                                  "status": 500,
                                  "error": "Erro interno do servidor",
                                  "message": "Catálogo de bebês não encontrado",
                                  "path": "/api/encomendas/bebes"
                                }
                                """)))
      })
  public ResponseEntity<List<BebeResponseDTO>> getBebesDisponiveis() {
    try {
      InputStream inputStream = catalogoResource.getInputStream();
      List<BebeResponseDTO> bebes =
          objectMapper.readValue(inputStream, new TypeReference<List<BebeResponseDTO>>() {});
      return ResponseEntity.ok(bebes);
    } catch (IOException e) {
      throw new RuntimeException("Catálogo de bebês não encontrado", e);
    }
  }
}
