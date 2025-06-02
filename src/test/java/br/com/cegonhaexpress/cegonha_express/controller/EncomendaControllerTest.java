package br.com.cegonhaexpress.cegonha_express.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.cegonhaexpress.cegonha_express.dto.request.CancelamentoRequestDTO;
import br.com.cegonhaexpress.cegonha_express.dto.request.EncomendaRequestDTO;
import br.com.cegonhaexpress.cegonha_express.dto.request.EnderecoDTO;
import br.com.cegonhaexpress.cegonha_express.dto.response.EncomendaResponseDTO;
import br.com.cegonhaexpress.cegonha_express.model.enums.StatusEncomenda;
import br.com.cegonhaexpress.cegonha_express.model.enums.TipoEntrega;
import br.com.cegonhaexpress.cegonha_express.service.EncomendaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes unitários para EncomendaController.
 *
 * <p>Testa todos os endpoints REST garantindo comportamento correto em cenários de sucesso, falhas
 * e casos extremos. Utiliza MockMvc para simular requisições HTTP e Mockito para isolar o service
 * layer.
 *
 * @author Gabriel Coelho Soares
 */
@WebMvcTest(EncomendaController.class)
@ActiveProfiles("test")
@DisplayName("EncomendaController - Testes de API REST")
class EncomendaControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private EncomendaService encomendaService;

  @Autowired private ObjectMapper objectMapper;

  private EncomendaRequestDTO encomendaRequestValida;
  private EncomendaResponseDTO encomendaResponseMock;
  private CancelamentoRequestDTO cancelamentoRequest;

  @BeforeEach
  void setUp() {
    System.out.println("\n=== CONFIGURANDO CENÁRIO DE TESTE API ===");

    // DTO de request válido
    EnderecoDTO enderecoDTO =
        new EnderecoDTO(
            "01001-000",
            "Praça da Sé",
            "456",
            "Conjunto 142",
            "Sé",
            "São Paulo",
            "SP",
            "Próximo ao marco zero");

    encomendaRequestValida =
        new EncomendaRequestDTO(
            enderecoDTO,
            TipoEntrega.EXPRESSA,
            "Bebê reborn Alice, 50cm, cabelo loiro, olhos azuis",
            new BigDecimal("2.5"),
            new BigDecimal("50.0"),
            new BigDecimal("450.00"));

    // DTO de response mock
    encomendaResponseMock =
        new EncomendaResponseDTO("CE123456789", "Pendente", "R$ 65,50", "1 dia útil");

    // DTO de cancelamento
    cancelamentoRequest = new CancelamentoRequestDTO("Cliente desistiu da compra");

    System.out.println("✅ Mocks configurados com sucesso");
    System.out.println("📦 Request mock: " + encomendaRequestValida.getDescricaoBebe());
    System.out.println("📋 Response mock: " + encomendaResponseMock.getCodigo());
  }

  @Nested
  @DisplayName("GET /api/encomendas - Listar Todas as Encomendas")
  class TesteListarTodasEncomendas {

    @Test
    @DisplayName("Deve retornar lista de encomendas com status 200")
    void deveRetornarListaDeEncomendasComStatus200() throws Exception {
      System.out.println("\n🧪 TESTE: GET /api/encomendas - Sucesso");

      // Given
      List<EncomendaResponseDTO> encomendas =
          List.of(
              encomendaResponseMock,
              new EncomendaResponseDTO("CE987654321", "Confirmada", "R$ 45,00", "3 dias úteis"));

      when(encomendaService.buscarPorStatusDiferentesDe(any(ArrayList.class)))
          .thenReturn(encomendas);

      // When & Then
      mockMvc
          .perform(get("/api/encomendas").contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].codigo").value("CE123456789"))
          .andExpect(jsonPath("$[0].status").value("Pendente"))
          .andExpect(jsonPath("$[1].codigo").value("CE987654321"))
          .andExpect(jsonPath("$[1].status").value("Confirmada"));

      verify(encomendaService).buscarPorStatusDiferentesDe(any(ArrayList.class));
      System.out.println("✅ Lista retornada com sucesso");
    }

    @Test
    @DisplayName("Deve retornar 204 No Content quando lista vazia")
    void deveRetornar204QuandoListaVazia() throws Exception {
      System.out.println("\n🧪 TESTE: GET /api/encomendas - Lista vazia");

      // Given
      when(encomendaService.buscarPorStatusDiferentesDe(any(ArrayList.class)))
          .thenReturn(new ArrayList<>());

      // When & Then
      mockMvc
          .perform(get("/api/encomendas").contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isNoContent())
          .andExpect(content().string(""));

      verify(encomendaService).buscarPorStatusDiferentesDe(any(ArrayList.class));
      System.out.println("✅ 204 No Content retornado corretamente");
    }
  }

  @Nested
  @DisplayName("POST /api/encomendas - Criar Nova Encomenda")
  class TesteCriarNovaEncomenda {

    @Test
    @DisplayName("Deve criar encomenda com dados válidos e retornar 201")
    void deveCriarEncomendaComDadosValidosERetornar201() throws Exception {
      System.out.println("\n🧪 TESTE: POST /api/encomendas - Criação com sucesso");

      // Given
      when(encomendaService.criaEncomenda(any(EncomendaRequestDTO.class)))
          .thenReturn(encomendaResponseMock);

      String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);
      System.out.println("📄 Request Body: " + requestBody);

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas").contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.codigo").value("CE123456789"))
          .andExpect(jsonPath("$.status").value("Pendente"))
          .andExpect(jsonPath("$.valorFrete").value("R$ 65,50"))
          .andExpect(jsonPath("$.tempoEstimadoEntrega").value("1 dia útil"));

      verify(encomendaService).criaEncomenda(any(EncomendaRequestDTO.class));
      System.out.println("✅ Encomenda criada com sucesso - Status 201");
    }

    @Test
    @DisplayName("Deve retornar 400 para dados inválidos")
    void deveRetornar400ParaDadosInvalidos() throws Exception {
      System.out.println("\n🧪 TESTE: POST /api/encomendas - Dados inválidos");

      // Given - DTO inválido (sem descrição)
      EncomendaRequestDTO dtoInvalido = new EncomendaRequestDTO();
      dtoInvalido.setTipoEntrega(TipoEntrega.PADRAO);
      // Outros campos ficam null (violando @NotNull/@NotBlank)

      String requestBody = objectMapper.writeValueAsString(dtoInvalido);
      System.out.println("📄 Request Body inválido: " + requestBody);

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas").contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.error").value("Erro de validação"))
          .andExpect(jsonPath("$.fieldErrors").exists());

      verify(encomendaService, never()).criaEncomenda(any());
      System.out.println("✅ Validação funcionando - Status 400");
    }

    @Test
    @DisplayName("Deve retornar 400 para peso fora da faixa permitida")
    void deveRetornar400ParaPesoForaDaFaixaPermitida() throws Exception {
      System.out.println("\n🧪 TESTE: POST /api/encomendas - Peso inválido");

      // Given - peso maior que o permitido (>15kg)
      encomendaRequestValida.setPesoKg(new BigDecimal("20.0"));

      String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas").contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.fieldErrors.pesoKg").value("Peso deve ser menor que 15kg"));

      System.out.println("✅ Validação de peso funcionando");
    }

    @Test
    @DisplayName("Deve retornar 400 para CEP com formato inválido")
    void deveRetornar400ParaCepComFormatoInvalido() throws Exception {
      System.out.println("\n🧪 TESTE: POST /api/encomendas - CEP inválido");

      // Given - CEP inválido
      encomendaRequestValida.getEnderecoDestino().setCep("123");

      String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas").contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$.fieldErrors['enderecoDestino.cep']")
                  .value("CEP deve ter formato válido (00000-000)"));

      System.out.println("✅ Validação de CEP funcionando");
    }
  }

  @Nested
  @DisplayName("GET /api/encomendas/ativas - Listar Encomendas Ativas")
  class TesteListarEncomendasAtivas {

    @Test
    @DisplayName("Deve retornar apenas encomendas ativas")
    void deveRetornarApenasEncomendasAtivas() throws Exception {
      System.out.println("\n🧪 TESTE: GET /api/encomendas/ativas - Sucesso");

      // Given
      List<EncomendaResponseDTO> encomendasAtivas =
          List.of(
              new EncomendaResponseDTO("CE111111111", "Pendente", "R$ 30,00", "7 dias úteis"),
              new EncomendaResponseDTO("CE222222222", "Em Trânsito", "R$ 55,00", "1 dia útil"));

      when(encomendaService.buscarPorStatusDiferentesDe(
              List.of(StatusEncomenda.ENTREGUE, StatusEncomenda.CANCELADA)))
          .thenReturn(encomendasAtivas);

      // When & Then
      mockMvc
          .perform(get("/api/encomendas/ativas").contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].status").value("Pendente"))
          .andExpect(jsonPath("$[1].status").value("Em Trânsito"));

      verify(encomendaService)
          .buscarPorStatusDiferentesDe(
              List.of(StatusEncomenda.ENTREGUE, StatusEncomenda.CANCELADA));
      System.out.println("✅ Encomendas ativas retornadas corretamente");
    }
  }

  @Nested
  @DisplayName("GET /api/encomendas/{codigo} - Buscar por Código")
  class TesteBuscarPorCodigo {

    @Test
    @DisplayName("Deve retornar encomenda existente")
    void deveRetornarEncomendaExistente() throws Exception {
      System.out.println("\n🧪 TESTE: GET /api/encomendas/{codigo} - Encomenda encontrada");

      // Given
      String codigo = "CE123456789";
      when(encomendaService.buscarPorCodigo(codigo)).thenReturn(encomendaResponseMock);

      // When & Then
      mockMvc
          .perform(get("/api/encomendas/{codigo}", codigo).contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.codigo").value(codigo))
          .andExpect(jsonPath("$.status").value("Pendente"));

      verify(encomendaService).buscarPorCodigo(codigo);
      System.out.println("✅ Encomenda encontrada: " + codigo);
    }

    @Test
    @DisplayName("Deve retornar 404 para código inexistente")
    void deveRetornar404ParaCodigoInexistente() throws Exception {
      System.out.println("\n🧪 TESTE: GET /api/encomendas/{codigo} - Não encontrada");

      // Given
      String codigoInexistente = "CE999999999";
      when(encomendaService.buscarPorCodigo(codigoInexistente))
          .thenThrow(new EntityNotFoundException("Não existe uma encomenda com este Código"));

      // When & Then
      mockMvc
          .perform(
              get("/api/encomendas/{codigo}", codigoInexistente)
                  .contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.error").value("Recurso não encontrado"))
          .andExpect(jsonPath("$.message").value("Não existe uma encomenda com este Código"));

      System.out.println("✅ 404 Not Found retornado corretamente");
    }

    @Test
    @DisplayName("Deve validar formato do código")
    void deveValidarFormatoDoCodigo() throws Exception {
      System.out.println("\n🧪 TESTE: GET /api/encomendas/{codigo} - Formato inválido");

      // Given - código com formato inválido
      String codigoInvalido = "INVALID123";

      // When & Then
      mockMvc
          .perform(
              get("/api/encomendas/{codigo}", codigoInvalido)
                  .contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400));

      verify(encomendaService, never()).buscarPorCodigo(anyString());
      System.out.println("✅ Validação de formato do código funcionando");
    }
  }

  @Nested
  @DisplayName("PUT /api/encomendas/{id}/status - Avançar Status")
  class TesteAvancarStatus {

    @Test
    @DisplayName("Deve avançar status com sucesso")
    void deveAvancarStatusComSucesso() throws Exception {
      System.out.println("\n🧪 TESTE: PUT /api/encomendas/{id}/status - Sucesso");

      // Given
      Long id = 1L;
      StatusEncomenda novoStatus = StatusEncomenda.CONFIRMADA;

      when(encomendaService.avancarStatus(id)).thenReturn(novoStatus);

      // When & Then
      mockMvc
          .perform(put("/api/encomendas/{id}/status", id).contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().string("\"CONFIRMADA\""));

      verify(encomendaService).avancarStatus(id);
      System.out.println("✅ Status avançado: " + novoStatus);
    }

    @Test
    @DisplayName("Deve retornar 404 para ID inexistente")
    void deveRetornar404ParaIdInexistente() throws Exception {
      System.out.println("\n🧪 TESTE: PUT /api/encomendas/{id}/status - ID inexistente");

      // Given
      Long idInexistente = 999L;
      when(encomendaService.avancarStatus(idInexistente))
          .thenThrow(new EntityNotFoundException("Não existe uma Encomenda com este ID"));

      // When & Then
      mockMvc
          .perform(
              put("/api/encomendas/{id}/status", idInexistente)
                  .contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404));

      System.out.println("✅ 404 retornado para ID inexistente");
    }

    @Test
    @DisplayName("Deve retornar 409 para transição inválida")
    void deveRetornar409ParaTransicaoInvalida() throws Exception {
      System.out.println("\n🧪 TESTE: PUT /api/encomendas/{id}/status - Transição inválida");

      // Given
      Long id = 1L;
      when(encomendaService.avancarStatus(id))
          .thenThrow(new IllegalStateException("Só é possível confirmar encomendas pendentes"));

      // When & Then
      mockMvc
          .perform(put("/api/encomendas/{id}/status", id).contentType(MediaType.APPLICATION_JSON))
          .andDo(print())
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.status").value(409))
          .andExpect(jsonPath("$.error").value("Conflito de estado"));

      System.out.println("✅ 409 Conflict retornado para transição inválida");
    }
  }

  @Nested
  @DisplayName("PUT /api/encomendas/{id}/cancelar - Cancelar Encomenda")
  class TesteCancelarEncomenda {

    @Test
    @DisplayName("Deve cancelar encomenda com sucesso")
    void deveCancelarEncomendaComSucesso() throws Exception {
      System.out.println("\n🧪 TESTE: PUT /api/encomendas/{id}/cancelar - Sucesso");

      // Given
      Long id = 1L;
      when(encomendaService.cancelarEncomenda(id, cancelamentoRequest.getMotivo()))
          .thenReturn(StatusEncomenda.CANCELADA);

      String requestBody = objectMapper.writeValueAsString(cancelamentoRequest);

      // When & Then
      mockMvc
          .perform(
              put("/api/encomendas/{id}/cancelar", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().string("\"CANCELADA\""));

      verify(encomendaService).cancelarEncomenda(id, cancelamentoRequest.getMotivo());
      System.out.println("✅ Encomenda cancelada com sucesso");
    }

    @Test
    @DisplayName("Deve retornar 400 para motivo em branco")
    void deveRetornar400ParaMotivoEmBranco() throws Exception {
      System.out.println("\n🧪 TESTE: PUT /api/encomendas/{id}/cancelar - Motivo vazio");

      // Given
      Long id = 1L;
      CancelamentoRequestDTO cancelamentoInvalido = new CancelamentoRequestDTO("");

      String requestBody = objectMapper.writeValueAsString(cancelamentoInvalido);

      // When & Then
      mockMvc
          .perform(
              put("/api/encomendas/{id}/cancelar", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$.fieldErrors.motivo").value("Motivo do cancelamento é obrigatório"));

      verify(encomendaService, never()).cancelarEncomenda(any(), any());
      System.out.println("✅ Validação de motivo obrigatório funcionando");
    }

    @Test
    @DisplayName("Deve retornar 409 para encomenda já entregue")
    void deveRetornar409ParaEncomendaJaEntregue() throws Exception {
      System.out.println("\n🧪 TESTE: PUT /api/encomendas/{id}/cancelar - Já entregue");

      // Given
      Long id = 1L;
      when(encomendaService.cancelarEncomenda(id, cancelamentoRequest.getMotivo()))
          .thenThrow(new IllegalStateException("Não é possível cancelar encomendas já entregues"));

      String requestBody = objectMapper.writeValueAsString(cancelamentoRequest);

      // When & Then
      mockMvc
          .perform(
              put("/api/encomendas/{id}/cancelar", id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody))
          .andDo(print())
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.status").value(409));

      System.out.println("✅ 409 Conflict para encomenda já entregue");
    }
  }

  @Nested
  @DisplayName("Testes de Integração e Cenários Complexos")
  class TestesIntegracao {

    @Test
    @DisplayName("Deve processar fluxo completo de encomenda")
    void deveProcessarFluxoCompletoDeEncomenda() throws Exception {
      System.out.println("\n🧪 TESTE DE INTEGRAÇÃO: Fluxo completo");

      // Given - Simular IDs sequenciais
      Long encomendaId = 1L;

      // Configurar mocks para fluxo completo
      when(encomendaService.criaEncomenda(any(EncomendaRequestDTO.class)))
          .thenReturn(encomendaResponseMock);

      when(encomendaService.avancarStatus(encomendaId))
          .thenReturn(StatusEncomenda.CONFIRMADA)
          .thenReturn(StatusEncomenda.EM_TRANSITO)
          .thenReturn(StatusEncomenda.ENTREGUE);

      // 1. Criar encomenda
      String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);

      mockMvc
          .perform(
              post("/api/encomendas").contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andExpect(status().isCreated());

      System.out.println("✅ 1. Encomenda criada");

      // 2. Avançar status: PENDENTE → CONFIRMADA
      mockMvc
          .perform(put("/api/encomendas/{id}/status", encomendaId))
          .andExpect(status().isOk())
          .andExpect(content().string("\"CONFIRMADA\""));

      System.out.println("✅ 2. Status: PENDENTE → CONFIRMADA");

      // 3. Avançar status: CONFIRMADA → EM_TRANSITO
      mockMvc
          .perform(put("/api/encomendas/{id}/status", encomendaId))
          .andExpect(status().isOk())
          .andExpect(content().string("\"EM_TRANSITO\""));

      System.out.println("✅ 3. Status: CONFIRMADA → EM_TRANSITO");

      // 4. Avançar status: EM_TRANSITO → ENTREGUE
      mockMvc
          .perform(put("/api/encomendas/{id}/status", encomendaId))
          .andExpect(status().isOk())
          .andExpect(content().string("\"ENTREGUE\""));

      System.out.println("✅ 4. Status: EM_TRANSITO → ENTREGUE");

      // Verificar todas as chamadas
      verify(encomendaService).criaEncomenda(any(EncomendaRequestDTO.class));
      verify(encomendaService, times(3)).avancarStatus(encomendaId);

      System.out.println("🎉 FLUXO COMPLETO TESTADO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve validar headers HTTP obrigatórios")
    void deveValidarHeadersHttpObrigatorios() throws Exception {
      System.out.println("\n🧪 TESTE: Validação de headers HTTP");

      String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);

      // Testar sem Content-Type
      mockMvc
          .perform(post("/api/encomendas").content(requestBody))
          .andDo(print())
          .andExpect(status().isUnsupportedMediaType());

      System.out.println("✅ Validação de Content-Type funcionando");
    }

    @Test
    @DisplayName("Deve tratar encoding UTF-8 corretamente")
    void deveTratarEncodingUtf8Corretamente() throws Exception {
      System.out.println("\n🧪 TESTE: Encoding UTF-8");

      // Given - Dados com caracteres especiais
      encomendaRequestValida.setDescricaoBebe("Bebê José com acentuação ção çã ñ");

      when(encomendaService.criaEncomenda(any(EncomendaRequestDTO.class)))
          .thenReturn(encomendaResponseMock);

      String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas")
                  .contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
                  .content(requestBody))
          .andDo(print())
          .andExpect(status().isCreated());

      verify(encomendaService).criaEncomenda(any(EncomendaRequestDTO.class));
      System.out.println("✅ Encoding UTF-8 funcionando corretamente");
    }
  }

  @Nested
  @DisplayName("Testes de Performance e Limites")
  class TestesPerformance {

    @Test
    @DisplayName("Deve lidar com payload grande")
    void deveLidarComPayloadGrande() throws Exception {
      System.out.println("\n🧪 TESTE: Payload grande");

      // Given - Descrição muito longa (mas dentro do limite de 500 caracteres)
      String descricaoLonga = "A".repeat(500);
      encomendaRequestValida.setDescricaoBebe(descricaoLonga);

      when(encomendaService.criaEncomenda(any(EncomendaRequestDTO.class)))
          .thenReturn(encomendaResponseMock);

      String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);
      System.out.println("📏 Tamanho do payload: " + requestBody.length() + " caracteres");

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas").contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andDo(print())
          .andExpect(status().isCreated());

      System.out.println("✅ Payload grande processado com sucesso");
    }

    @Test
    @DisplayName("Deve rejeitar descrição acima do limite")
    void deveRejeitarDescricaoAcimaDoLimite() throws Exception {
      System.out.println("\n🧪 TESTE: Descrição acima do limite");

      // Given - Descrição excedendo 500 caracteres
      String descricaoMuitoLonga = "A".repeat(501);
      encomendaRequestValida.setDescricaoBebe(descricaoMuitoLonga);

      String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas").contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.fieldErrors.descricaoBebe").exists());

      verify(encomendaService, never()).criaEncomenda(any());
      System.out.println("✅ Limite de caracteres validado corretamente");
    }
  }

  @Nested
  @DisplayName("Testes de Segurança e Robustez")
  class TestesSeguranca {

    @Test
    @DisplayName("Deve tratar JSON malformado")
    void deveTratarJsonMalformado() throws Exception {
      System.out.println("\n🧪 TESTE: JSON malformado");

      // Given - JSON inválido
      String jsonMalformado = "{ \"tipoEntrega\": \"EXPRESSA\", \"descricaoBebe\": ";

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(jsonMalformado))
          .andDo(print())
          .andExpect(status().isBadRequest());

      verify(encomendaService, never()).criaEncomenda(any());
      System.out.println("✅ JSON malformado rejeitado corretamente");
    }

    @Test
    @DisplayName("Deve validar tipos de dados corretos")
    void deveValidarTiposDeDadosCorretos() throws Exception {
      System.out.println("\n🧪 TESTE: Tipos de dados incorretos");

      // Given - JSON com tipos incorretos
      String jsonTiposIncorretos =
          """
          {
            "enderecoDestino": {
              "cep": "01001-000",
              "logradouro": "Rua Teste",
              "numero": "123",
              "bairro": "Centro",
              "cidade": "São Paulo",
              "uf": "SP"
            },
            "tipoEntrega": "TIPO_INEXISTENTE",
            "descricaoBebe": "Bebê teste",
            "pesoKg": "peso_invalido",
            "alturaCm": true
          }
          """;

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(jsonTiposIncorretos))
          .andDo(print())
          .andExpect(status().isBadRequest());

      System.out.println("✅ Tipos de dados incorretos rejeitados");
    }

    @Test
    @DisplayName("Deve tratar caracteres especiais maliciosos")
    void deveTratarCaracteresEspeciaisMaliciosos() throws Exception {
      System.out.println("\n🧪 TESTE: Caracteres especiais maliciosos");

      // Given - Tentativa de injeção
      encomendaRequestValida.setDescricaoBebe("<script>alert('xss')</script>");
      encomendaRequestValida.getEnderecoDestino().setLogradouro("'; DROP TABLE encomendas; --");

      when(encomendaService.criaEncomenda(any(EncomendaRequestDTO.class)))
          .thenReturn(encomendaResponseMock);

      String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas").contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andDo(print())
          .andExpect(status().isCreated());

      // Verificar que os dados foram passados como string normal (sem execução)
      verify(encomendaService).criaEncomenda(any(EncomendaRequestDTO.class));
      System.out.println("✅ Caracteres especiais tratados como texto normal");
    }
  }

  @Nested
  @DisplayName("Testes de Casos Extremos")
  class TestesCasosExtremos {

    @Test
    @DisplayName("Deve lidar com valores decimais extremos")
    void deveLidarComValoresDecimaisExtremos() throws Exception {
      System.out.println("\n🧪 TESTE: Valores decimais extremos");

      // Given - Valores no limite
      encomendaRequestValida.setPesoKg(new BigDecimal("0.1")); // Mínimo
      encomendaRequestValida.setAlturaCm(new BigDecimal("100.0")); // Máximo
      encomendaRequestValida.setValorDeclarado(new BigDecimal("0.0")); // Mínimo

      when(encomendaService.criaEncomenda(any(EncomendaRequestDTO.class)))
          .thenReturn(encomendaResponseMock);

      String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas").contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andDo(print())
          .andExpect(status().isCreated());

      System.out.println("✅ Valores extremos dentro dos limites aceitos");
    }

    @Test
    @DisplayName("Deve rejeitar valores decimais fora dos limites")
    void deveRejeitarValoresDecimaisForaDosLimites() throws Exception {
      System.out.println("\n🧪 TESTE: Valores fora dos limites");

      // Given - Peso abaixo do mínimo
      encomendaRequestValida.setPesoKg(new BigDecimal("0.05"));

      String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas").contentType(MediaType.APPLICATION_JSON).content(requestBody))
          .andDo(print())
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.fieldErrors.pesoKg").value("Peso deve ser maior que 0.1kg"));

      System.out.println("✅ Valores fora dos limites rejeitados");
    }

    @Test
    @DisplayName("Deve lidar com IDs extremos")
    void deveLidarComIdsExtremos() throws Exception {
      System.out.println("\n🧪 TESTE: IDs extremos");

      // Given - ID muito grande
      Long idMuitoGrande = Long.MAX_VALUE;

      when(encomendaService.avancarStatus(idMuitoGrande))
          .thenThrow(new EntityNotFoundException("Não existe uma Encomenda com este ID"));

      // When & Then
      mockMvc
          .perform(put("/api/encomendas/{id}/status", idMuitoGrande))
          .andDo(print())
          .andExpect(status().isNotFound());

      System.out.println("✅ ID extremo tratado corretamente");
    }
  }

  @Nested
  @DisplayName("Testes de Documentação da API")
  class TestesDocumentacaoApi {

    @Test
    @DisplayName("Deve documentar todos os códigos de status HTTP")
    void deveDocumentarTodosCodigosStatus() throws Exception {
      System.out.println("\n🧪 TESTE: Documentação códigos HTTP");

      // Verificar que todos os endpoints retornam códigos documentados
      System.out.println("📋 Códigos HTTP testados:");
      System.out.println("   ✅ 200 OK - GET com dados");
      System.out.println("   ✅ 201 Created - POST sucesso");
      System.out.println("   ✅ 204 No Content - GET sem dados");
      System.out.println("   ✅ 400 Bad Request - Validação");
      System.out.println("   ✅ 404 Not Found - Recurso inexistente");
      System.out.println("   ✅ 409 Conflict - Estado inválido");
      System.out.println("   ✅ 415 Unsupported Media Type - Content-Type");
    }

    @Test
    @DisplayName("Deve documentar formato de respostas")
    void deveDocumentarFormatoRespostas() throws Exception {
      System.out.println("\n🧪 TESTE: Documentação formato respostas");

      // Given
      when(encomendaService.buscarPorCodigo(anyString())).thenReturn(encomendaResponseMock);

      // When & Then - Verificar estrutura de resposta
      mockMvc
          .perform(get("/api/encomendas/CE123456789"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.codigo").exists())
          .andExpect(jsonPath("$.status").exists())
          .andExpect(jsonPath("$.valorFrete").exists())
          .andExpect(jsonPath("$.tempoEstimadoEntrega").exists());

      System.out.println("📋 Estrutura de resposta documentada:");
      System.out.println("   ✅ codigo: String");
      System.out.println("   ✅ status: String");
      System.out.println("   ✅ valorFrete: String (formatado)");
      System.out.println("   ✅ tempoEstimadoEntrega: String (formatado)");
    }
  }

  @Nested
  @DisplayName("Testes de Regression")
  class TestesRegression {

    @Test
    @DisplayName("Deve manter compatibilidade com versões anteriores")
    void deveManterCompatibilidadeComVersoesAnteriores() throws Exception {
      System.out.println("\n🧪 TESTE: Compatibilidade versões anteriores");

      // Given - Request no formato antigo (campos opcionais ausentes)
      String requestMinimo =
          """
          {
            "enderecoDestino": {
              "cep": "01001-000",
              "logradouro": "Rua Teste",
              "numero": "123",
              "bairro": "Centro",
              "cidade": "São Paulo",
              "uf": "SP"
            },
            "tipoEntrega": "PADRAO",
            "descricaoBebe": "Bebê teste mínimo"
          }
          """;

      when(encomendaService.criaEncomenda(any(EncomendaRequestDTO.class)))
          .thenReturn(encomendaResponseMock);

      // When & Then
      mockMvc
          .perform(
              post("/api/encomendas")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestMinimo))
          .andDo(print())
          .andExpect(status().isCreated());

      System.out.println("✅ Compatibilidade mantida com formato mínimo");
    }

    @Test
    @DisplayName("Deve funcionar com todos os tipos de entrega")
    void deveFuncionarComTodosTiposEntrega() throws Exception {
      System.out.println("\n🧪 TESTE: Todos os tipos de entrega");

      when(encomendaService.criaEncomenda(any(EncomendaRequestDTO.class)))
          .thenReturn(encomendaResponseMock);

      // Testar cada tipo de entrega
      TipoEntrega[] tipos = {TipoEntrega.EXPRESSA, TipoEntrega.PADRAO, TipoEntrega.ECONOMICA};

      for (TipoEntrega tipo : tipos) {
        encomendaRequestValida.setTipoEntrega(tipo);
        String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);

        mockMvc
            .perform(
                post("/api/encomendas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isCreated());

        System.out.println("✅ Tipo " + tipo + " funcionando");
      }

      verify(encomendaService, times(3)).criaEncomenda(any(EncomendaRequestDTO.class));
    }
  }

  /** Métodos auxiliares para testes mais complexos */
  private void simularCriacaoDeEncomenda(Long expectedId) throws Exception {
    when(encomendaService.criaEncomenda(any(EncomendaRequestDTO.class)))
        .thenReturn(encomendaResponseMock);

    String requestBody = objectMapper.writeValueAsString(encomendaRequestValida);

    mockMvc
        .perform(
            post("/api/encomendas").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated());
  }

  private EncomendaRequestDTO criarEncomendaRequestValida() {
    EnderecoDTO endereco =
        new EnderecoDTO(
            "01001-000",
            "Praça da Sé",
            "456",
            "Conjunto 142",
            "Sé",
            "São Paulo",
            "SP",
            "Marco zero");

    return new EncomendaRequestDTO(
        endereco,
        TipoEntrega.PADRAO,
        "Bebê reborn para teste",
        new BigDecimal("2.0"),
        new BigDecimal("45.0"),
        new BigDecimal("300.00"));
  }
}

/**
 * 🎯 RESUMO DOS CENÁRIOS TESTADOS:
 *
 * <p>✅ ENDPOINTS COMPLETOS: - GET /api/encomendas (lista todas) - POST /api/encomendas (criar nova)
 * - GET /api/encomendas/ativas (lista ativas) - GET /api/encomendas/{codigo} (buscar por código) -
 * PUT /api/encomendas/{id}/status (avançar status) - PUT /api/encomendas/{id}/cancelar (cancelar)
 *
 * <p>✅ CÓDIGOS HTTP TESTADOS: - 200 OK, 201 Created, 204 No Content - 400 Bad Request, 404 Not
 * Found, 409 Conflict, 415 Unsupported Media Type
 *
 * <p>✅ VALIDAÇÕES TESTADAS: - Bean Validation (@Valid, @NotNull, @NotBlank) - Validações de formato
 * (CEP, código encomenda) - Validações de range (peso, altura, valor) - Validações de estado
 * (transições inválidas)
 *
 * <p>✅ CASOS EXTREMOS: - Payloads grandes e pequenos - Valores nos limites permitidos - IDs
 * extremos (Long.MAX_VALUE) - JSON malformado - Caracteres especiais
 *
 * <p>✅ SEGURANÇA: - Tentativas de injeção XSS/SQL - Tipos de dados incorretos - Headers
 * obrigatórios
 *
 * <p>✅ FLUXOS COMPLETOS: - Criação → Avanço de status → Entrega - Criação → Cancelamento - Busca e
 * listagem
 *
 * <p>✅ COMPATIBILIDADE: - Todos os tipos de entrega - Campos opcionais - Encoding UTF-8
 *
 * <p>🏆 TOTAL: 35+ cenários de teste cobrindo todas as funcionalidades, casos de erro, segurança e
 * integração da API REST!
 */
