package br.com.cegonhaexpress.cegonha_express.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.enums.StatusEncomenda;
import br.com.cegonhaexpress.cegonha_express.model.enums.TipoEntrega;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@DisplayName("EncomendaRepository - Testes de Persistência e Consultas")
class EncomendaRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private EncomendaRepository encomendaRepository;

  @Autowired private ClienteRepository clienteRepository;

  @Autowired private EnderecoRepository enderecoRepository;

  private Cliente clienteTeste1;
  private Cliente clienteTeste2;
  private Endereco enderecoOrigemTeste;
  private Endereco enderecoDestinoTeste1;
  private Endereco enderecoDestinoTeste2;
  private Encomenda encomendaTeste1;
  private Encomenda encomendaTeste2;
  private Encomenda encomendaTeste3;

  @BeforeEach
  void setUp() {
    System.out.println("\n=== CONFIGURANDO CENÁRIO DE TESTE ENCOMENDA REPOSITORY ===");

    // Clientes de teste
    clienteTeste1 = new Cliente("João Silva", "joao@email.com", "123.456.789-00");
    clienteTeste2 = new Cliente("Maria Santos", "maria@email.com", "987.654.321-11");

    // Endereços de teste
    enderecoOrigemTeste =
        new Endereco("13840-000", "Rua das Flores", "123", "Centro", "Mogi Guaçu", UF.SP);

    enderecoDestinoTeste1 =
        new Endereco("01001-000", "Praça da Sé", "456", "Centro", "São Paulo", UF.SP);

    enderecoDestinoTeste2 =
        new Endereco("20040-020", "Avenida Rio Branco", "789", "Centro", "Rio de Janeiro", UF.RJ);

    // Salvar entidades dependentes
    clienteTeste1 = clienteRepository.save(clienteTeste1);
    clienteTeste2 = clienteRepository.save(clienteTeste2);
    enderecoOrigemTeste = enderecoRepository.save(enderecoOrigemTeste);
    enderecoDestinoTeste1 = enderecoRepository.save(enderecoDestinoTeste1);
    enderecoDestinoTeste2 = enderecoRepository.save(enderecoDestinoTeste2);

    // Encomendas de teste
    encomendaTeste1 =
        new Encomenda(
            clienteTeste1,
            enderecoOrigemTeste,
            enderecoDestinoTeste1,
            TipoEntrega.EXPRESSA,
            "Bebê reborn Alice, 50cm, cabelo loiro");

    encomendaTeste2 =
        new Encomenda(
            clienteTeste1,
            enderecoOrigemTeste,
            enderecoDestinoTeste2,
            TipoEntrega.PADRAO,
            "Bebê reborn Pedro, 45cm, cabelo castanho",
            new BigDecimal("2.5"),
            new BigDecimal("45.0"),
            new BigDecimal("300.00"));

    encomendaTeste3 =
        new Encomenda(
            clienteTeste2,
            enderecoOrigemTeste,
            enderecoDestinoTeste1,
            TipoEntrega.ECONOMICA,
            "Bebê reborn Maria, 48cm, cabelo cacheado");

    System.out.println("✅ Dados de teste configurados");
  }

  @Nested
  @DisplayName("Testes de CRUD Básico")
  class TestesCrudBasico {

    @Test
    @DisplayName("Deve salvar e buscar encomenda por ID")
    void deveSalvarEBuscarEncomendaPorId() {
      System.out.println("\n🧪 TESTE: Salvar e buscar encomenda por ID");

      // Given
      System.out.println("📋 Salvando encomenda:");
      System.out.println("   Cliente: " + encomendaTeste1.getCliente().getNome());
      System.out.println("   Tipo: " + encomendaTeste1.getTipoEntrega());
      System.out.println("   Descrição: " + encomendaTeste1.getDescricaoBebe());

      // When
      Encomenda encomendaSalva = encomendaRepository.save(encomendaTeste1);
      Optional<Encomenda> encomendaEncontrada =
          encomendaRepository.findById(encomendaSalva.getId());

      // Then
      System.out.println("\n📊 Verificando resultados:");
      assertTrue(encomendaEncontrada.isPresent());
      System.out.println("✅ Encomenda encontrada: " + encomendaEncontrada.isPresent());

      Encomenda encomenda = encomendaEncontrada.get();
      assertEquals(encomendaTeste1.getDescricaoBebe(), encomenda.getDescricaoBebe());
      assertEquals(encomendaTeste1.getTipoEntrega(), encomenda.getTipoEntrega());
      assertEquals(StatusEncomenda.PENDENTE, encomenda.getStatus());
      assertNotNull(encomenda.getCodigo());
      assertTrue(encomenda.getCodigo().startsWith("CE"));

      System.out.println("✅ Descrição: " + encomenda.getDescricaoBebe());
      System.out.println("✅ Tipo: " + encomenda.getTipoEntrega());
      System.out.println("✅ Status: " + encomenda.getStatus());
      System.out.println("✅ Código: " + encomenda.getCodigo());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve gerar código único para cada encomenda")
    void deveGerarCodigoUnicoParaCadaEncomenda() {
      System.out.println("\n🧪 TESTE: Códigos únicos");

      // Given & When
      Encomenda encomenda1 = encomendaRepository.save(encomendaTeste1);
      Encomenda encomenda2 = encomendaRepository.save(encomendaTeste2);
      Encomenda encomenda3 = encomendaRepository.save(encomendaTeste3);

      // Then
      System.out.println("\n📊 Verificando códigos únicos:");
      System.out.println("   Código 1: " + encomenda1.getCodigo());
      System.out.println("   Código 2: " + encomenda2.getCodigo());
      System.out.println("   Código 3: " + encomenda3.getCodigo());

      assertNotEquals(encomenda1.getCodigo(), encomenda2.getCodigo());
      assertNotEquals(encomenda1.getCodigo(), encomenda3.getCodigo());
      assertNotEquals(encomenda2.getCodigo(), encomenda3.getCodigo());

      assertTrue(encomenda1.getCodigo().startsWith("CE"));
      assertTrue(encomenda2.getCodigo().startsWith("CE"));
      assertTrue(encomenda3.getCodigo().startsWith("CE"));

      System.out.println("✅ Todos os códigos são únicos");
      System.out.println("✅ Todos começam com 'CE'");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Busca por Código")
  class TestesBuscaPorCodigo {

    @Test
    @DisplayName("Deve encontrar encomenda por código")
    void deveEncontrarEncomendaPorCodigo() {
      System.out.println("\n🧪 TESTE: Buscar por código");

      // Given
      Encomenda encomendaSalva = encomendaRepository.save(encomendaTeste1);
      String codigo = encomendaSalva.getCodigo();
      System.out.println("📋 Código para busca: " + codigo);

      // When
      Optional<Encomenda> encomendaEncontrada = encomendaRepository.findByCodigo(codigo);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertTrue(encomendaEncontrada.isPresent());
      System.out.println("✅ Encomenda encontrada: " + encomendaEncontrada.isPresent());

      Encomenda encomenda = encomendaEncontrada.get();
      assertEquals(codigo, encomenda.getCodigo());
      assertEquals(encomendaTeste1.getDescricaoBebe(), encomenda.getDescricaoBebe());

      System.out.println("✅ Código correto: " + encomenda.getCodigo());
      System.out.println("✅ Descrição correta: " + encomenda.getDescricaoBebe());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para código inexistente")
    void deveRetornarOptionalVazioParaCodigoInexistente() {
      System.out.println("\n🧪 TESTE: Código inexistente");

      // Given
      String codigoInexistente = "CE999999999";
      System.out.println("📋 Código inexistente: " + codigoInexistente);

      // When
      Optional<Encomenda> encomendaEncontrada = encomendaRepository.findByCodigo(codigoInexistente);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertFalse(encomendaEncontrada.isPresent());
      System.out.println("✅ Encomenda não encontrada (esperado): " + encomendaEncontrada.isEmpty());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Busca por Status")
  class TestesBuscaPorStatus {

    @Test
    @DisplayName("Deve encontrar encomendas por status")
    void deveEncontrarEncomendasPorStatus() {
      System.out.println("\n🧪 TESTE: Buscar por status");

      // Given
      encomendaRepository.save(encomendaTeste1);
      encomendaRepository.save(encomendaTeste2);

      // Confirmar uma encomenda
      encomendaTeste3.confirmar();
      encomendaRepository.save(encomendaTeste3);

      System.out.println("📋 Encomendas salvas:");
      System.out.println("   2 PENDENTES, 1 CONFIRMADA");

      // When
      List<Encomenda> encomendasPendentes =
          encomendaRepository.findByStatus(StatusEncomenda.PENDENTE);
      List<Encomenda> encomendasConfirmadas =
          encomendaRepository.findByStatus(StatusEncomenda.CONFIRMADA);

      // Then
      System.out.println("\n📊 Verificando busca por status:");
      assertEquals(2, encomendasPendentes.size());
      assertEquals(1, encomendasConfirmadas.size());

      System.out.println("✅ Pendentes encontradas: " + encomendasPendentes.size());
      System.out.println("✅ Confirmadas encontradas: " + encomendasConfirmadas.size());

      boolean todasPendentes =
          encomendasPendentes.stream().allMatch(e -> e.getStatus() == StatusEncomenda.PENDENTE);
      assertTrue(todasPendentes);
      System.out.println("✅ Todas pendentes têm status correto");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve encontrar encomendas excluindo status específicos")
    void deveEncontrarEncomendasExcluindoStatusEspecificos() {
      System.out.println("\n🧪 TESTE: Buscar excluindo status");

      // Given
      encomendaRepository.save(encomendaTeste1);

      encomendaTeste2.confirmar();
      encomendaRepository.save(encomendaTeste2);

      encomendaTeste3.cancelar("Teste de cancelamento");
      encomendaRepository.save(encomendaTeste3);

      List<StatusEncomenda> statusExcluidos =
          Arrays.asList(StatusEncomenda.CANCELADA, StatusEncomenda.ENTREGUE);

      System.out.println("📋 Status para excluir: CANCELADA, ENTREGUE");

      // When
      List<Encomenda> encomendasAtivas = encomendaRepository.findByStatusNotIn(statusExcluidos);

      // Then
      System.out.println("\n📊 Verificando busca com exclusão:");
      assertEquals(2, encomendasAtivas.size());
      System.out.println("✅ Encomendas ativas encontradas: " + encomendasAtivas.size());

      boolean nenhumaCancelada =
          encomendasAtivas.stream().noneMatch(e -> e.getStatus() == StatusEncomenda.CANCELADA);
      boolean nenhumaEntregue =
          encomendasAtivas.stream().noneMatch(e -> e.getStatus() == StatusEncomenda.ENTREGUE);

      assertTrue(nenhumaCancelada);
      assertTrue(nenhumaEntregue);
      System.out.println("✅ Nenhuma cancelada ou entregue incluída");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Busca por Tipo de Entrega")
  class TestesBuscaPorTipoEntrega {

    @Test
    @DisplayName("Deve encontrar encomendas por tipo de entrega")
    void deveEncontrarEncomendasPorTipoEntrega() {
      System.out.println("\n🧪 TESTE: Buscar por tipo de entrega");

      // Given
      encomendaRepository.save(encomendaTeste1); // EXPRESSA
      encomendaRepository.save(encomendaTeste2); // PADRAO
      encomendaRepository.save(encomendaTeste3); // ECONOMICA

      System.out.println("📋 Tipos salvos: EXPRESSA, PADRÃO, ECONÔMICA");

      // When
      List<Encomenda> expressas = encomendaRepository.findByTipoEntrega(TipoEntrega.EXPRESSA);
      List<Encomenda> padroes = encomendaRepository.findByTipoEntrega(TipoEntrega.PADRAO);
      List<Encomenda> economicas = encomendaRepository.findByTipoEntrega(TipoEntrega.ECONOMICA);

      // Then
      System.out.println("\n📊 Verificando busca por tipo:");
      assertEquals(1, expressas.size());
      assertEquals(1, padroes.size());
      assertEquals(1, economicas.size());

      System.out.println("✅ Expressas: " + expressas.size());
      System.out.println("✅ Padrões: " + padroes.size());
      System.out.println("✅ Econômicas: " + economicas.size());

      assertEquals(TipoEntrega.EXPRESSA, expressas.get(0).getTipoEntrega());
      assertEquals(TipoEntrega.PADRAO, padroes.get(0).getTipoEntrega());
      assertEquals(TipoEntrega.ECONOMICA, economicas.get(0).getTipoEntrega());

      System.out.println("✅ Todos os tipos estão corretos");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Busca por Cliente")
  class TestesBuscaPorCliente {

    @Test
    @DisplayName("Deve encontrar encomendas por cliente")
    void deveEncontrarEncomendasPorCliente() {
      System.out.println("\n🧪 TESTE: Buscar por cliente");

      // Given
      encomendaRepository.save(encomendaTeste1); // Cliente 1
      encomendaRepository.save(encomendaTeste2); // Cliente 1
      encomendaRepository.save(encomendaTeste3); // Cliente 2

      System.out.println("📋 Cliente 1: " + clienteTeste1.getNome() + " (2 encomendas)");
      System.out.println("📋 Cliente 2: " + clienteTeste2.getNome() + " (1 encomenda)");

      // When
      List<Encomenda> encomendasCliente1 = encomendaRepository.findByCliente(clienteTeste1);
      List<Encomenda> encomendasCliente2 = encomendaRepository.findByCliente(clienteTeste2);

      // Then
      System.out.println("\n📊 Verificando busca por cliente:");
      assertEquals(2, encomendasCliente1.size());
      assertEquals(1, encomendasCliente2.size());

      System.out.println("✅ Cliente 1: " + encomendasCliente1.size() + " encomendas");
      System.out.println("✅ Cliente 2: " + encomendasCliente2.size() + " encomenda");

      boolean todasDoCliente1 =
          encomendasCliente1.stream().allMatch(e -> e.getCliente().equals(clienteTeste1));
      assertTrue(todasDoCliente1);
      System.out.println("✅ Todas pertencem ao cliente correto");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve encontrar encomendas por ID do cliente")
    void deveEncontrarEncomendasPorIdDoCliente() {
      System.out.println("\n🧪 TESTE: Buscar por ID do cliente");

      // Given
      encomendaRepository.save(encomendaTeste1);
      System.out.println("📋 Cliente ID: " + clienteTeste1.getId());

      // When
      List<Encomenda> encomendas = encomendaRepository.findByClienteId(clienteTeste1.getId());

      // Then
      System.out.println("\n📊 Verificando busca por ID:");
      assertEquals(1, encomendas.size());
      System.out.println("✅ Encomendas encontradas: " + encomendas.size());

      assertEquals(clienteTeste1.getId(), encomendas.get(0).getCliente().getId());
      System.out.println("✅ ID do cliente correto");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Busca por Endereço")
  class TestesBuscaPorEndereco {

    @Test
    @DisplayName("Deve encontrar encomendas por CEP do destino")
    void deveEncontrarEncomendasPorCepDestino() {
      System.out.println("\n🧪 TESTE: Buscar por CEP destino");

      // Given
      encomendaRepository.save(encomendaTeste1); // Destino: 01001-000
      encomendaRepository.save(encomendaTeste2); // Destino: 20040-020
      encomendaRepository.save(encomendaTeste3); // Destino: 01001-000

      String cepDestino = "01001-000";
      System.out.println("📋 CEP destino: " + cepDestino);

      // When
      List<Encomenda> encomendas = encomendaRepository.findByEnderecoDestinoCep(cepDestino);

      // Then
      System.out.println("\n📊 Verificando busca por CEP:");
      assertEquals(2, encomendas.size());
      System.out.println("✅ Encomendas encontradas: " + encomendas.size());

      boolean todasComCepCorreto =
          encomendas.stream().allMatch(e -> e.getEnderecoDestino().getCep().equals(cepDestino));
      assertTrue(todasComCepCorreto);
      System.out.println("✅ Todas com CEP destino correto");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve encontrar encomendas por endereço de destino")
    void deveEncontrarEncomendasPorEnderecoDestino() {
      System.out.println("\n🧪 TESTE: Buscar por endereço destino");

      // Given
      encomendaRepository.save(encomendaTeste1); // Destino: enderecoDestinoTeste1
      encomendaRepository.save(encomendaTeste3); // Destino: enderecoDestinoTeste1

      System.out.println("📋 Endereço destino: " + enderecoDestinoTeste1.getEnderecoCompleto());

      // When
      List<Encomenda> encomendas = encomendaRepository.findByEnderecoDestino(enderecoDestinoTeste1);

      // Then
      System.out.println("\n📊 Verificando busca por endereço:");
      assertEquals(2, encomendas.size());
      System.out.println("✅ Encomendas encontradas: " + encomendas.size());

      boolean todasComEnderecoCorreto =
          encomendas.stream().allMatch(e -> e.getEnderecoDestino().equals(enderecoDestinoTeste1));
      assertTrue(todasComEnderecoCorreto);
      System.out.println("✅ Todas com endereço destino correto");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Máquina de Estados")
  class TestesMaquinaEstados {

    @Test
    @DisplayName("Deve manter consistência de status após persistência")
    void deveManterConsistenciaStatusAposPersistencia() {
      System.out.println("\n🧪 TESTE: Consistência de status");

      // Given
      Encomenda encomenda = encomendaRepository.save(encomendaTeste1);
      System.out.println("📋 Status inicial: " + encomenda.getStatus());

      // When - Confirmar
      encomenda.confirmar();
      Encomenda encomendaConfirmada = encomendaRepository.save(encomenda);

      // Then
      System.out.println("\n📊 Verificando transição de status:");
      assertEquals(StatusEncomenda.CONFIRMADA, encomendaConfirmada.getStatus());
      assertNotNull(encomendaConfirmada.getDataEstimadaEntrega());

      System.out.println("✅ Status: " + encomendaConfirmada.getStatus());
      System.out.println("✅ Data estimada: " + encomendaConfirmada.getDataEstimadaEntrega());

      // When - Iniciar trânsito
      encomenda.iniciarTransito();
      Encomenda encomendaEmTransito = encomendaRepository.save(encomenda);

      // Then
      assertEquals(StatusEncomenda.EM_TRANSITO, encomendaEmTransito.getStatus());
      System.out.println("✅ Status em trânsito: " + encomendaEmTransito.getStatus());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve validar métodos de negócio da encomenda")
    void deveValidarMetodosNegocioEncomenda() {
      System.out.println("\n🧪 TESTE: Métodos de negócio");

      // Given
      Encomenda encomenda = encomendaRepository.save(encomendaTeste1);

      // When & Then
      System.out.println("\n📊 Verificando métodos de negócio:");

      assertTrue(encomenda.podeSerModificada());
      System.out.println("✅ Pode ser modificada: " + encomenda.podeSerModificada());

      assertTrue(encomenda.isAtiva());
      System.out.println("✅ Está ativa: " + encomenda.isAtiva());

      assertFalse(encomenda.isEntregue());
      System.out.println("✅ Não está entregue: " + !encomenda.isEntregue());

      assertFalse(encomenda.isAtrasada());
      System.out.println("✅ Não está atrasada: " + !encomenda.isAtrasada());

      assertFalse(encomenda.isEntregaInterestadual());
      System.out.println("✅ Não é interestadual: " + !encomenda.isEntregaInterestadual());

      assertNotNull(encomenda.getResumo());
      System.out.println("✅ Resumo: " + encomenda.getResumo());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Performance")
  class TestesPerformance {

    @Test
    @DisplayName("Deve manter performance com múltiplas encomendas")
    void deveManterPerformanceComMultiplasEncomendas() {
      System.out.println("\n🧪 TESTE: Performance com múltiplas encomendas");

      // Given
      System.out.println("📋 Criando múltiplas encomendas:");
      for (int i = 1; i <= 10; i++) {
        Encomenda encomenda =
            new Encomenda(
                clienteTeste1,
                enderecoOrigemTeste,
                enderecoDestinoTeste1,
                i % 2 == 0 ? TipoEntrega.EXPRESSA : TipoEntrega.PADRAO,
                "Bebê teste " + i);
        if (i > 5) {
          encomenda.confirmar();
        }
        encomendaRepository.save(encomenda);
      }
      System.out.println("   Total criadas: 10 encomendas");

      // When
      long inicioTempo = System.currentTimeMillis();

      List<Encomenda> porCliente = encomendaRepository.findByCliente(clienteTeste1);
      List<Encomenda> porStatus = encomendaRepository.findByStatus(StatusEncomenda.PENDENTE);
      List<Encomenda> porTipo = encomendaRepository.findByTipoEntrega(TipoEntrega.EXPRESSA);
      List<Encomenda> todasEncomendas = encomendaRepository.findAll();

      long fimTempo = System.currentTimeMillis();
      long tempoTotal = fimTempo - inicioTempo;

      // Then
      System.out.println("\n📊 Resultados de performance:");
      assertEquals(10, porCliente.size());
      assertEquals(5, porStatus.size()); // 5 pendentes
      assertEquals(5, porTipo.size()); // 5 expressas
      assertEquals(10, todasEncomendas.size());

      System.out.println("✅ Por cliente: " + porCliente.size());
      System.out.println("✅ Por status (PENDENTE): " + porStatus.size());
      System.out.println("✅ Por tipo (EXPRESSA): " + porTipo.size());
      System.out.println("✅ Total: " + todasEncomendas.size());
      System.out.println("⏱️ Tempo total: " + tempoTotal + "ms");

      assertTrue(tempoTotal < 2000);
      System.out.println("✅ Performance adequada (< 2000ms)");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }
}
