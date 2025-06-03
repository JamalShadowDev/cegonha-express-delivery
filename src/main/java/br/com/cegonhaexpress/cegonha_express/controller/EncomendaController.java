package br.com.cegonhaexpress.cegonha_express.controller;

import br.com.cegonhaexpress.cegonha_express.dto.request.CancelamentoRequestDTO;
import br.com.cegonhaexpress.cegonha_express.dto.request.EncomendaRequestDTO;
import br.com.cegonhaexpress.cegonha_express.dto.response.EncomendaResponseDTO;
import br.com.cegonhaexpress.cegonha_express.model.enums.StatusEncomenda;
import br.com.cegonhaexpress.cegonha_express.service.EncomendaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações com encomendas.
 *
 * <p>Todos os tratamentos de exceção são delegados para o GlobalExceptionHandler, mantendo este
 * controller focado apenas na lógica de negócio.
 *
 * @author Gabriel Coelho Soares
 */
@RestController
@RequestMapping("/api/encomendas")
@RequiredArgsConstructor
@Validated
public class EncomendaController {

  private final EncomendaService encomendaService;

  /**
   * Lista todas as encomendas (exceto as com status especificados).
   *
   * @return Lista de encomendas ou 204 No Content se vazia
   */
  @GetMapping
  public ResponseEntity<List<EncomendaResponseDTO>> getEncomendas() {
    List<EncomendaResponseDTO> encomendas =
        encomendaService.buscarPorStatusDiferentesDe(new ArrayList<>());

    return encomendas.isEmpty()
        ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(encomendas);
  }

  /**
   * Cria uma nova encomenda.
   *
   * @param dto Dados da encomenda a ser criada
   * @return Encomenda criada com status 201 Created
   */
  @PostMapping(consumes = "application/json")
  public ResponseEntity<EncomendaResponseDTO> createEncomenda(
      @Valid @RequestBody EncomendaRequestDTO dto) {

    EncomendaResponseDTO encomenda = encomendaService.criaEncomenda(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(encomenda);
  }

  /**
   * Lista apenas encomendas ativas (exclui ENTREGUE e CANCELADA).
   *
   * @return Lista de encomendas ativas ou 204 No Content se vazia
   */
  @GetMapping("/ativas")
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
   * @param codigo Código único da encomenda (formato: CE + dígitos)
   * @return Encomenda encontrada ou 404 se não existir
   */
  @GetMapping("/{codigo}")
  public ResponseEntity<EncomendaResponseDTO> getSingleEncomenda(
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
   * @param id ID da encomenda
   * @return Novo status ou 204 se nenhuma transição foi possível
   */
  @PutMapping("/{id}/status")
  public ResponseEntity<StatusEncomenda> updateStatus(@PathVariable Long id) {
    StatusEncomenda novoStatus = encomendaService.avancarStatus(id);

    return novoStatus != null ? ResponseEntity.ok(novoStatus) : ResponseEntity.noContent().build();
  }

  /**
   * Cancela uma encomenda com motivo específico.
   *
   * @param id ID da encomenda
   * @param dto DTO contendo o motivo do cancelamento
   * @return Status após cancelamento
   */
  @PutMapping(value = "/{id}/cancelar", consumes = "application/json")
  public ResponseEntity<StatusEncomenda> cancelarEncomenda(
      @PathVariable Long id, @Valid @RequestBody CancelamentoRequestDTO dto) {

    StatusEncomenda status = encomendaService.cancelarEncomenda(id, dto.getMotivo());
    return ResponseEntity.ok(status);
  }
}
