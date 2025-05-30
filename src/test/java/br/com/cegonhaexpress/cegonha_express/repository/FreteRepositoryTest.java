package br.com.cegonhaexpress.cegonha_express.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.entity.Frete;
import br.com.cegonhaexpress.cegonha_express.model.enums.TipoEntrega;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@DisplayName("FreteRepository - Testes de Persistência e Consultas")
class FreteRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private FreteRepository freteRepository;

  @Autowired private ClienteRepository clienteRepository;

  @Autowired private EnderecoRepository enderecoRepository;

  @Autowired private EncomendaRepository encomendaRepository;

  private Cliente clienteTeste;
  private Endereco enderecoOrigemTeste;
  private Endereco enderecoDestinoTeste;
  private Encomenda encomendaTeste1;
  private Encomenda encomendaTeste2;
  private Frete freteTeste1;
  private Frete freteTeste2;

  @BeforeEach
  void setUp() {
    System.out.println("\n=== CONFIGURANDO CENÁRIO DE TESTE FRETE REPOSITORY ===");

    // Cliente de teste
    clienteTeste = new Cliente("João Silva", "joao@email.com", "123.456.789-00");

    // Endereços de teste
    enderecoOrigemTeste =
        new Endereco("13840-000", "Rua das Flores", "123", "Centro", "Mogi Guaçu", UF.SP);

    enderecoDestinoTeste =
        new Endereco("01001-000", "Praça da Sé", "456", "Centro", "São Paulo", UF.SP);

    // Salvar entidades dependentes
    clienteTeste = clienteRepository.save(clienteTeste);
    enderecoOrigemTeste = enderecoRepository.save(enderecoOrigemTeste);
    enderecoDestinoTeste = enderecoRepository.save(enderecoDestinoTeste);

    // Encomendas de teste
    encomendaTeste1 =
        new Encomenda(
            clienteTeste,
            enderecoOrigemTeste,
            enderecoDestinoTeste,
            TipoEntrega.EXPRESSA,
            "Bebê reborn Alice");

    encomendaTeste2 =
        new Encomenda(
            clienteTeste,
            enderecoOrigemTeste,
            enderecoDestinoTeste,
            TipoEntrega.PADRAO,
            "Bebê reborn Pedro");

    encomendaTeste1 = encomendaRepository.save(encomendaTeste1);
    encomendaTeste2 = encomendaRepository.save(encomendaTeste2);

    // Fretes de teste
    freteTeste1 =
        new Frete(
            encomendaTeste1,
            TipoEntrega.EXPRESSA,
            new BigDecimal("65.50"),
            new BigDecimal("120.0"),
            1);

    freteTeste2 =
        new Frete(
            encomendaTeste2,
            TipoEntrega.PADRAO,
            new BigDecimal("45.00"),
            new BigDecimal("120.0"),
            3);

    System.out.println("✅ Dados de teste configurados");
  }

  // Adicione este teste temporário
  @Test
  void testeSuper() {
    Frete frete =
        new Frete(
            encomendaTeste1,
            TipoEntrega.PADRAO,
            new BigDecimal("50.00"),
            new BigDecimal("100.0"),
            3);

    System.out.println("Created_at: " + frete.getCreatedAt());
    System.out.println("Updated_at: " + frete.getUpdatedAt());
  }

  @Nested
  @DisplayName("Testes de CRUD Básico")
  class TestesCrudBasico {

    @Test
    @DisplayName("Deve salvar e buscar frete por ID")
    void deveSalvarEBuscarFretePorId() {
      System.out.println("\n🧪 TESTE: Salvar e buscar frete por ID");

      // Given
      System.out.println("📋 Salvando frete:");
      System.out.println("   Encomenda: " + freteTeste1.getEncomenda().getCodigo());
      System.out.println("   Tipo: " + freteTeste1.getTipoEntrega());
      System.out.println("   Valor: R$ " + freteTeste1.getValor());
      System.out.println("   Distância: " + freteTeste1.getDistanciaKm() + " km");

      // When
      Frete freteSalvo = freteRepository.save(freteTeste1);
      Optional<Frete> freteEncontrado = freteRepository.findById(freteSalvo.getId());

      // Then
      System.out.println("\n📊 Verificando resultados:");
      assertTrue(freteEncontrado.isPresent());
      System.out.println("✅ Frete encontrado: " + freteEncontrado.isPresent());

      Frete frete = freteEncontrado.get();
      assertEquals(freteTeste1.getValor(), frete.getValor());
      assertEquals(freteTeste1.getTipoEntrega(), frete.getTipoEntrega());
      assertEquals(freteTeste1.getDistanciaKm(), frete.getDistanciaKm());
      assertEquals(freteTeste1.getPrazoDias(), frete.getPrazoDias());

      System.out.println("✅ Valor: R$ " + frete.getValor());
      System.out.println("✅ Tipo: " + frete.getTipoEntrega());
      System.out.println("✅ Distância: " + frete.getDistanciaKm() + " km");
      System.out.println("✅ Prazo: " + frete.getPrazoDias() + " dias");

      assertNotNull(frete.getDataCalculo());
      System.out.println("✅ Data cálculo: " + frete.getDataCalculo());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve listar todos os fretes")
    void deveListarTodosOsFretes() {
      System.out.println("\n🧪 TESTE: Listar todos os fretes");

      // Given
      freteRepository.save(freteTeste1);
      freteRepository.save(freteTeste2);
      System.out.println("📋 Total de fretes salvos: 2");

      // When
      List<Frete> todosFretes = freteRepository.findAll();

      // Then
      System.out.println("\n📊 Verificando resultados:");
      assertEquals(2, todosFretes.size());
      System.out.println("✅ Quantidade correta: " + todosFretes.size());

      boolean contemExpressa =
          todosFretes.stream().anyMatch(f -> f.getTipoEntrega() == TipoEntrega.EXPRESSA);
      boolean contemPadrao =
          todosFretes.stream().anyMatch(f -> f.getTipoEntrega() == TipoEntrega.PADRAO);

      assertTrue(contemExpressa);
      assertTrue(contemPadrao);

      System.out.println("✅ Contém frete expressa: " + contemExpressa);
      System.out.println("✅ Contém frete padrão: " + contemPadrao);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve atualizar frete existente")
    void deveAtualizarFreteExistente() {
      System.out.println("\n🧪 TESTE: Atualizar frete existente");

      // Given
      Frete freteSalvo = freteRepository.save(freteTeste1);
      BigDecimal valorOriginal = freteSalvo.getValor();
      System.out.println("📋 Valor original: R$ " + valorOriginal);

      // When
      BigDecimal novoValor = new BigDecimal("75.00");
      freteSalvo.setValor(novoValor);
      Frete freteAtualizado = freteRepository.save(freteSalvo);

      // Then
      System.out.println("\n📊 Verificando atualização:");
      assertEquals(novoValor, freteAtualizado.getValor());
      System.out.println("✅ Valor atualizado: R$ " + freteAtualizado.getValor());

      assertNotNull(freteAtualizado.getUpdatedAt());
      System.out.println("✅ UpdatedAt foi atualizado: " + freteAtualizado.getUpdatedAt());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve deletar frete por ID")
    void deveDeletarFretePorId() {
      System.out.println("\n🧪 TESTE: Deletar frete por ID");

      // Given
      Frete freteSalvo = freteRepository.save(freteTeste1);
      Long freteId = freteSalvo.getId();
      System.out.println("📋 Frete ID: " + freteId);

      // When
      freteRepository.deleteById(freteId);
      System.out.println("🗑️ Frete deletado");

      // Then
      Optional<Frete> freteEncontrado = freteRepository.findById(freteId);
      System.out.println("\n📊 Verificando deleção:");
      assertFalse(freteEncontrado.isPresent());
      System.out.println("✅ Frete não encontrado: " + freteEncontrado.isEmpty());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Busca por Encomenda")
  class TestesBuscaPorEncomenda {

    @Test
    @DisplayName("Deve encontrar frete por encomenda")
    void deveEncontrarFretePorEncomenda() {
      System.out.println("\n🧪 TESTE: Buscar frete por encomenda");

      // Given
      Frete freteSalvo = freteRepository.save(freteTeste1);
      System.out.println("📋 Encomenda: " + encomendaTeste1.getCodigo());
      System.out.println("📋 Frete ID: " + freteSalvo.getId());

      // When
      Optional<Frete> freteEncontrado = freteRepository.findByEncomenda(encomendaTeste1);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertTrue(freteEncontrado.isPresent());
      System.out.println("✅ Frete encontrado: " + freteEncontrado.isPresent());

      Frete frete = freteEncontrado.get();
      assertEquals(encomendaTeste1.getId(), frete.getEncomenda().getId());
      assertEquals(freteSalvo.getId(), frete.getId());

      System.out.println("✅ Encomenda ID: " + frete.getEncomenda().getId());
      System.out.println("✅ Frete ID: " + frete.getId());
      System.out.println("✅ Valor: R$ " + frete.getValor());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve encontrar frete por ID da encomenda")
    void deveEncontrarFretePorIdDaEncomenda() {
      System.out.println("\n🧪 TESTE: Buscar frete por ID da encomenda");

      // Given
      Frete freteSalvo = freteRepository.save(freteTeste2);
      Long encomendaId = encomendaTeste2.getId();
      System.out.println("📋 Encomenda ID: " + encomendaId);

      // When
      Optional<Frete> freteEncontrado = freteRepository.findByEncomendaId(encomendaId);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertTrue(freteEncontrado.isPresent());
      System.out.println("✅ Frete encontrado: " + freteEncontrado.isPresent());

      Frete frete = freteEncontrado.get();
      assertEquals(encomendaId, frete.getEncomenda().getId());
      assertEquals(freteSalvo.getId(), frete.getId());

      System.out.println("✅ Encomenda ID correto: " + frete.getEncomenda().getId());
      System.out.println("✅ Tipo entrega: " + frete.getTipoEntrega());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para encomenda sem frete")
    void deveRetornarOptionalVazioParaEncomendaSemFrete() {
      System.out.println("\n🧪 TESTE: Encomenda sem frete");

      // Given - criar nova encomenda sem frete associado
      Encomenda encomendaSemFrete =
          new Encomenda(
              clienteTeste,
              enderecoOrigemTeste,
              enderecoDestinoTeste,
              TipoEntrega.ECONOMICA,
              "Bebê sem frete");
      encomendaSemFrete = encomendaRepository.save(encomendaSemFrete);
      System.out.println("📋 Encomenda sem frete: " + encomendaSemFrete.getCodigo());

      // When
      Optional<Frete> freteEncontrado = freteRepository.findByEncomenda(encomendaSemFrete);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertFalse(freteEncontrado.isPresent());
      System.out.println("✅ Frete não encontrado (esperado): " + freteEncontrado.isEmpty());

      // Teste com ID também
      Optional<Frete> fretePorId = freteRepository.findByEncomendaId(encomendaSemFrete.getId());
      assertFalse(fretePorId.isPresent());
      System.out.println("✅ Busca por ID também vazia: " + fretePorId.isEmpty());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Relacionamento")
  class TestesRelacionamento {

    @Test
    @DisplayName("Deve manter relacionamento um-para-um com encomenda")
    void deveManterRelacionamentoUmParaUmComEncomenda() {
      System.out.println("\n🧪 TESTE: Relacionamento 1:1 com encomenda");

      // Given
      Frete freteSalvo = freteRepository.save(freteTeste1);
      System.out.println("📋 Frete salvo: " + freteSalvo.getId());
      System.out.println("📋 Encomenda: " + freteSalvo.getEncomenda().getCodigo());

      // When
      entityManager.clear(); // Força nova busca
      Optional<Frete> freteEncontrado = freteRepository.findById(freteSalvo.getId());

      // Then
      System.out.println("\n📊 Verificando relacionamento:");
      assertTrue(freteEncontrado.isPresent());

      Frete frete = freteEncontrado.get();
      assertNotNull(frete.getEncomenda());
      assertEquals(encomendaTeste1.getId(), frete.getEncomenda().getId());
      assertEquals(encomendaTeste1.getCodigo(), frete.getEncomenda().getCodigo());

      System.out.println("✅ Encomenda associada: " + frete.getEncomenda().getCodigo());
      System.out.println("✅ Cliente da encomenda: " + frete.getEncomenda().getCliente().getNome());
      System.out.println("✅ Relacionamento mantido após persistência");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve extrair CEPs da encomenda automaticamente")
    void deveExtrairCepsDaEncomendaAutomaticamente() {
      System.out.println("\n🧪 TESTE: Extração automática de CEPs");

      // Given
      System.out.println("📋 CEP origem: " + enderecoOrigemTeste.getCep());
      System.out.println("📋 CEP destino: " + enderecoDestinoTeste.getCep());

      // When
      Frete freteSalvo = freteRepository.save(freteTeste1);

      // Then
      System.out.println("\n📊 Verificando extração de CEPs:");
      assertEquals(enderecoOrigemTeste.getCep(), freteSalvo.getCepOrigem());
      assertEquals(enderecoDestinoTeste.getCep(), freteSalvo.getCepDestino());

      System.out.println("✅ CEP origem extraído: " + freteSalvo.getCepOrigem());
      System.out.println("✅ CEP destino extraído: " + freteSalvo.getCepDestino());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve manter integridade referencial com encomenda")
    void deveManterIntegridadeReferencialComEncomenda() {
      System.out.println("\n🧪 TESTE: Integridade referencial");

      // Given
      Frete freteSalvo = freteRepository.save(freteTeste1);
      Long freteId = freteSalvo.getId();
      Long encomendaId = encomendaTeste1.getId();

      System.out.println("📋 Frete ID: " + freteId);
      System.out.println("📋 Encomenda ID: " + encomendaId);

      // When - deletar frete
      freteRepository.deleteById(freteId);
      System.out.println("🗑️ Frete deletado");

      // Then - encomenda deve continuar existindo
      Optional<Frete> freteEncontrado = freteRepository.findById(freteId);
      assertFalse(freteEncontrado.isPresent());
      System.out.println("✅ Frete não encontrado (deletado)");

      Optional<Encomenda> encomendaEncontrada = encomendaRepository.findById(encomendaId);
      assertTrue(encomendaEncontrada.isPresent());
      System.out.println("✅ Encomenda ainda existe: " + encomendaEncontrada.get().getCodigo());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Métodos de Negócio")
  class TestesMetodosNegocio {

    @Test
    @DisplayName("Deve calcular frete usando método estático")
    void deveCalcularFreteUsandoMetodoEstatico() {
      System.out.println("\n🧪 TESTE: Cálculo de frete estático");

      // Given
      BigDecimal distancia = new BigDecimal("100.0");
      BigDecimal peso = new BigDecimal("2.5");

      System.out.println("📋 Parâmetros:");
      System.out.println("   Distância: " + distancia + " km");
      System.out.println("   Peso: " + peso + " kg");

      // When
      BigDecimal valorExpressa = Frete.calcularFrete(TipoEntrega.EXPRESSA, distancia, peso);
      BigDecimal valorPadrao = Frete.calcularFrete(TipoEntrega.PADRAO, distancia, peso);
      BigDecimal valorEconomica = Frete.calcularFrete(TipoEntrega.ECONOMICA, distancia, peso);

      // Then
      System.out.println("\n📊 Verificando cálculos:");
      assertTrue(valorExpressa.compareTo(BigDecimal.ZERO) > 0);
      assertTrue(valorPadrao.compareTo(BigDecimal.ZERO) > 0);
      assertTrue(valorEconomica.compareTo(BigDecimal.ZERO) > 0);

      // Hierarquia de preços: EXPRESSA > PADRAO > ECONOMICA
      assertTrue(valorExpressa.compareTo(valorPadrao) > 0);
      assertTrue(valorPadrao.compareTo(valorEconomica) > 0);

      System.out.println("✅ Frete expressa: R$ " + valorExpressa);
      System.out.println("✅ Frete padrão: R$ " + valorPadrao);
      System.out.println("✅ Frete econômica: R$ " + valorEconomica);
      System.out.println("✅ Hierarquia de preços respeitada");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve recalcular frete com novos parâmetros")
    void deveRecalcularFreteComNovosParametros() {
      System.out.println("\n🧪 TESTE: Recálculo de frete");

      // Given
      Frete freteSalvo = freteRepository.save(freteTeste1);
      BigDecimal valorOriginal = freteSalvo.getValor();
      BigDecimal distanciaOriginal = freteSalvo.getDistanciaKm();

      System.out.println("📋 Valores originais:");
      System.out.println("   Valor: R$ " + valorOriginal);
      System.out.println("   Distância: " + distanciaOriginal + " km");

      // When
      BigDecimal novaDistancia = new BigDecimal("200.0");
      BigDecimal novoPeso = new BigDecimal("3.0");

      System.out.println("📋 Novos parâmetros:");
      System.out.println("   Nova distância: " + novaDistancia + " km");
      System.out.println("   Novo peso: " + novoPeso + " kg");

      freteSalvo.recalcular(novaDistancia, novoPeso);
      Frete freteRecalculado = freteRepository.save(freteSalvo);

      // Then
      System.out.println("\n📊 Verificando recálculo:");
      assertNotEquals(valorOriginal, freteRecalculado.getValor());
      assertEquals(novaDistancia, freteRecalculado.getDistanciaKm());

      System.out.println("✅ Novo valor: R$ " + freteRecalculado.getValor());
      System.out.println("✅ Nova distância: " + freteRecalculado.getDistanciaKm() + " km");
      System.out.println("✅ Valor foi recalculado corretamente");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve validar métodos utilitários do frete")
    void deveValidarMetodosUtilitariosDoFrete() {
      System.out.println("\n🧪 TESTE: Métodos utilitários");

      // Given
      Frete freteSalvo = freteRepository.save(freteTeste1);

      // When & Then
      System.out.println("\n📊 Verificando métodos utilitários:");

      String valorFormatado = freteSalvo.getValorFormatado();
      System.out.println("✅ Valor formatado: " + valorFormatado);
      assertTrue(valorFormatado.startsWith("R$"));

      String distanciaFormatada = freteSalvo.getDistanciaFormatada();
      System.out.println("✅ Distância formatada: " + distanciaFormatada);
      assertTrue(distanciaFormatada.contains("km"));

      String prazoFormatado = freteSalvo.getPrazoFormatado();
      System.out.println("✅ Prazo formatado: " + prazoFormatado);
      assertTrue(prazoFormatado.contains("dia"));

      String resumo = freteSalvo.getResumo();
      System.out.println("✅ Resumo: " + resumo);
      assertNotNull(resumo);
      assertTrue(resumo.contains("Expressa"));

      BigDecimal valorPorKm = freteSalvo.getValorPorKm();
      System.out.println("✅ Valor por km: R$ " + valorPorKm);
      assertTrue(valorPorKm.compareTo(BigDecimal.ZERO) > 0);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve verificar se frete é interestadual")
    void deveVerificarSeFreteEInterestadual() {
      System.out.println("\n🧪 TESTE: Verificação frete interestadual");

      // Given - criar endereços de estados diferentes
      Endereco enderecoRJ =
          new Endereco("20040-020", "Av. Rio Branco", "100", "Centro", "Rio de Janeiro", UF.RJ);
      enderecoRJ = enderecoRepository.save(enderecoRJ);

      Encomenda encomendaInterestadual =
          new Encomenda(
              clienteTeste,
              enderecoOrigemTeste, // SP
              enderecoRJ, // RJ
              TipoEntrega.EXPRESSA,
              "Bebê interestadual");
      encomendaInterestadual = encomendaRepository.save(encomendaInterestadual);

      Frete freteInterestadual =
          new Frete(
              encomendaInterestadual,
              TipoEntrega.EXPRESSA,
              new BigDecimal("85.00"),
              new BigDecimal("400.0"),
              1);

      System.out.println("📋 Origem: " + enderecoOrigemTeste.getUf());
      System.out.println("📋 Destino: " + enderecoRJ.getUf());

      // When
      Frete freteSalvo = freteRepository.save(freteInterestadual);

      // Then
      System.out.println("\n📊 Verificando se é interestadual:");
      assertTrue(freteSalvo.isInterestadual());
      System.out.println("✅ É interestadual: " + freteSalvo.isInterestadual());

      // Comparar com frete estadual
      Frete freteEstadual = freteRepository.save(freteTeste1);
      assertFalse(freteEstadual.isInterestadual());
      System.out.println("✅ Frete estadual (SP-SP): " + freteEstadual.isInterestadual());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Performance e Escalabilidade")
  class TestesPerformance {

    @Test
    @DisplayName("Deve manter performance com múltiplos fretes")
    void deveManterPerformanceComMultiplosFretes() {
      System.out.println("\n🧪 TESTE: Performance com múltiplos fretes");

      // Given
      System.out.println("📋 Criando múltiplos fretes:");
      for (int i = 1; i <= 10; i++) {
        Encomenda encomenda =
            new Encomenda(
                clienteTeste,
                enderecoOrigemTeste,
                enderecoDestinoTeste,
                i % 3 == 0
                    ? TipoEntrega.EXPRESSA
                    : i % 2 == 0 ? TipoEntrega.PADRAO : TipoEntrega.ECONOMICA,
                "Bebê teste " + i);
        encomenda = encomendaRepository.save(encomenda);

        Frete frete =
            new Frete(
                encomenda,
                encomenda.getTipoEntrega(),
                new BigDecimal(30 + (i * 5)),
                new BigDecimal("50.0"),
                encomenda.getTipoEntrega().getDiasMinimosEntrega());
        freteRepository.save(frete);
      }
      System.out.println("   Total criados: 10 fretes");

      // When
      long inicioTempo = System.currentTimeMillis();

      List<Frete> todosFretes = freteRepository.findAll();

      // Buscar fretes por diferentes encomendas
      for (int i = 0; i < 5; i++) {
        freteRepository.findByEncomendaId((long) (i + 1));
      }

      long fimTempo = System.currentTimeMillis();
      long tempoTotal = fimTempo - inicioTempo;

      // Then
      System.out.println("\n📊 Resultados de performance:");
      assertEquals(10, todosFretes.size());
      System.out.println("✅ Total de fretes: " + todosFretes.size());

      System.out.println("⏱️ Tempo total: " + tempoTotal + "ms");
      assertTrue(tempoTotal < 2000); // Permitir até 2s para múltiplas operações
      System.out.println("✅ Performance adequada (< 2000ms)");

      // Verificar distribuição por tipo
      long expressas =
          todosFretes.stream().filter(f -> f.getTipoEntrega() == TipoEntrega.EXPRESSA).count();
      long padroes =
          todosFretes.stream().filter(f -> f.getTipoEntrega() == TipoEntrega.PADRAO).count();
      long economicas =
          todosFretes.stream().filter(f -> f.getTipoEntrega() == TipoEntrega.ECONOMICA).count();

      System.out.println("📊 Distribuição por tipo:");
      System.out.println("   Expressas: " + expressas);
      System.out.println("   Padrões: " + padroes);
      System.out.println("   Econômicas: " + economicas);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve otimizar consultas com relacionamentos")
    void deveOtimizarConsultasComRelacionamentos() {
      System.out.println("\n🧪 TESTE: Otimização de consultas com relacionamentos");

      // Given
      Frete freteSalvo = freteRepository.save(freteTeste1);
      System.out.println("📋 Frete salvo: " + freteSalvo.getId());

      // When - buscar frete e acessar relacionamentos
      long inicioTempo = System.currentTimeMillis();

      Optional<Frete> freteEncontrado = freteRepository.findById(freteSalvo.getId());
      assertTrue(freteEncontrado.isPresent());

      Frete frete = freteEncontrado.get();

      // Acessar relacionamentos (pode gerar queries lazy)
      String nomeCliente = frete.getEncomenda().getCliente().getNome();
      String cidadeOrigem = frete.getEncomenda().getEnderecoOrigem().getCidade();
      String cidadeDestino = frete.getEncomenda().getEnderecoDestino().getCidade();

      long fimTempo = System.currentTimeMillis();
      long tempoTotal = fimTempo - inicioTempo;

      // Then
      System.out.println("\n📊 Verificando acesso a relacionamentos:");
      assertEquals("João Silva", nomeCliente);
      assertEquals("Mogi Guaçu", cidadeOrigem);
      assertEquals("São Paulo", cidadeDestino);

      System.out.println("✅ Cliente: " + nomeCliente);
      System.out.println("✅ Origem: " + cidadeOrigem);
      System.out.println("✅ Destino: " + cidadeDestino);
      System.out.println("⏱️ Tempo acesso relacionamentos: " + tempoTotal + "ms");

      assertTrue(tempoTotal < 1000);
      System.out.println("✅ Performance de relacionamentos adequada");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Integridade e Validação")
  class TestesIntegridade {

    @Test
    @DisplayName("Deve garantir consistência dos dados persistidos")
    void deveGarantirConsistenciaDadosPersistidos() {
      System.out.println("\n🧪 TESTE: Consistência de dados persistidos");

      // Given
      System.out.println("📋 Dados originais do frete:");
      System.out.println("   Valor: R$ " + freteTeste1.getValor());
      System.out.println("   Distância: " + freteTeste1.getDistanciaKm() + " km");
      System.out.println("   Prazo: " + freteTeste1.getPrazoDias() + " dias");

      // When
      Frete freteSalvo = freteRepository.save(freteTeste1);
      entityManager.clear(); // Força nova consulta

      Optional<Frete> freteRecuperado = freteRepository.findById(freteSalvo.getId());

      // Then
      System.out.println("\n📊 Verificando consistência após persistência:");
      assertTrue(freteRecuperado.isPresent());

      Frete frete = freteRecuperado.get();
      assertEquals(freteTeste1.getValor(), frete.getValor());
      assertEquals(freteTeste1.getDistanciaKm(), frete.getDistanciaKm());
      assertEquals(freteTeste1.getPrazoDias(), frete.getPrazoDias());
      assertEquals(freteTeste1.getTipoEntrega(), frete.getTipoEntrega());

      System.out.println("✅ Valor mantido: R$ " + frete.getValor());
      System.out.println("✅ Distância mantida: " + frete.getDistanciaKm() + " km");
      System.out.println("✅ Prazo mantido: " + frete.getPrazoDias() + " dias");
      System.out.println("✅ Tipo mantido: " + frete.getTipoEntrega());

      // Verificar timestamps BaseEntity
      assertNotNull(frete.getCreatedAt());
      assertNotNull(frete.getUpdatedAt());
      System.out.println("✅ Timestamps BaseEntity configurados");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve validar unicidade implícita frete-encomenda")
    void deveValidarUnicidadeImplicitaFreteEncomenda() {
      System.out.println("\n🧪 TESTE: Validação unicidade frete-encomenda");

      // Given
      Frete primeiroFrete = freteRepository.save(freteTeste1);
      System.out.println("📋 Primeiro frete salvo: " + primeiroFrete.getId());

      // When - tentar criar segundo frete para mesma encomenda
      Frete segundoFrete =
          new Frete(
              encomendaTeste1, // Mesma encomenda!
              TipoEntrega.ECONOMICA,
              new BigDecimal("30.00"),
              new BigDecimal("120.0"),
              7);

      System.out.println("🔄 Tentando salvar segundo frete para mesma encomenda...");

      // Then - deve LANÇAR exceção por violação de constraint unique
      DataIntegrityViolationException exception =
          assertThrows(
              DataIntegrityViolationException.class,
              () -> {
                freteRepository.save(segundoFrete);
                freteRepository.flush(); // Força o flush para capturar a exceção
              },
              "Deveria lançar exceção ao tentar salvar segundo frete para mesma encomenda");

      System.out.println(
          "✅ Exceção capturada corretamente: " + exception.getClass().getSimpleName());
      System.out.println("📋 Mensagem da exceção: " + exception.getMessage());

      // Limpar o contexto de persistência após a exceção
      entityManager.clear();

      // Verificar que apenas o primeiro frete existe no banco
      List<Frete> todosFretes = freteRepository.findAll();
      System.out.println("✅ Total de fretes no banco após tentativa: " + todosFretes.size());

      // Deve ter apenas 1 frete (o primeiro que foi salvo com sucesso)
      assertEquals(1, todosFretes.size(), "Deve haver apenas 1 frete no banco");
      assertEquals(
          primeiroFrete.getId(),
          todosFretes.get(0).getId(),
          "O frete no banco deve ser o primeiro frete salvo");

      // Verificar que o primeiro frete não foi afetado
      Frete freteNoBanco = todosFretes.get(0);
      assertEquals(TipoEntrega.EXPRESSA, freteNoBanco.getTipoEntrega());
      assertEquals(new BigDecimal("65.50"), freteNoBanco.getValor());

      System.out.println("ℹ️ Constraint de unicidade funcionando corretamente no banco de dados");
      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve manter integridade ao deletar relacionamentos")
    void deveManterIntegridadeAoDeletarRelacionamentos() {
      System.out.println("\n🧪 TESTE: Integridade ao deletar relacionamentos");

      // Given
      Frete freteSalvo = freteRepository.save(freteTeste1);
      Long freteId = freteSalvo.getId();
      Long encomendaId = encomendaTeste1.getId();
      Long clienteId = clienteTeste.getId();

      System.out.println("📋 IDs salvos:");
      System.out.println("   Frete: " + freteId);
      System.out.println("   Encomenda: " + encomendaId);
      System.out.println("   Cliente: " + clienteId);

      // When - deletar frete
      freteRepository.deleteById(freteId);
      System.out.println("🗑️ Frete deletado");

      // Then - outras entidades devem permanecer
      Optional<Frete> freteEncontrado = freteRepository.findById(freteId);
      assertFalse(freteEncontrado.isPresent());
      System.out.println("✅ Frete não encontrado (deletado corretamente)");

      Optional<Encomenda> encomendaEncontrada = encomendaRepository.findById(encomendaId);
      assertTrue(encomendaEncontrada.isPresent());
      System.out.println("✅ Encomenda mantida: " + encomendaEncontrada.get().getCodigo());

      Optional<Cliente> clienteEncontrado = clienteRepository.findById(clienteId);
      assertTrue(clienteEncontrado.isPresent());
      System.out.println("✅ Cliente mantido: " + clienteEncontrado.get().getNome());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Casos Extremos")
  class TestesCasosExtremos {

    @Test
    @DisplayName("Deve lidar com valores limite nos cálculos")
    void deveLidarComValoresLimiteNosCalculos() {
      System.out.println("\n🧪 TESTE: Valores limite nos cálculos");

      // Given - valores extremos
      BigDecimal distanciaMinima = new BigDecimal("0.1");
      BigDecimal distanciaMaxima = new BigDecimal("9999.0");
      BigDecimal pesoMinimo = new BigDecimal("0.1");
      BigDecimal pesoMaximo = new BigDecimal("15.0");

      System.out.println("📋 Testando valores extremos:");
      System.out.println("   Distância mínima: " + distanciaMinima + " km");
      System.out.println("   Peso mínimo: " + pesoMinimo + " kg");

      // When & Then
      assertDoesNotThrow(
          () -> {
            BigDecimal valorMinimo =
                Frete.calcularFrete(TipoEntrega.ECONOMICA, distanciaMinima, pesoMinimo);
            System.out.println("✅ Cálculo mínimo: R$ " + valorMinimo);
            assertTrue(valorMinimo.compareTo(BigDecimal.ZERO) > 0);
          });

      assertDoesNotThrow(
          () -> {
            BigDecimal valorMaximo =
                Frete.calcularFrete(TipoEntrega.EXPRESSA, distanciaMaxima, pesoMaximo);
            System.out.println("✅ Cálculo máximo: R$ " + valorMaximo);
            assertTrue(valorMaximo.compareTo(new BigDecimal("1000")) > 0); // Deve ser um valor alto
          });

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve tratar parâmetros nulos nos cálculos")
    void deveTratarParametrosNulosNosCalculos() {
      System.out.println("\n🧪 TESTE: Parâmetros nulos nos cálculos");

      // Given
      BigDecimal distanciaValida = new BigDecimal("100.0");
      BigDecimal pesoValido = new BigDecimal("2.0");

      System.out.println("📋 Testando cenários com nulls:");

      // When & Then - tipo null
      IllegalArgumentException exception1 =
          assertThrows(
              IllegalArgumentException.class,
              () -> Frete.calcularFrete(null, distanciaValida, pesoValido));
      System.out.println("✅ Tipo null: " + exception1.getMessage());

      // Distância null
      IllegalArgumentException exception2 =
          assertThrows(
              IllegalArgumentException.class,
              () -> Frete.calcularFrete(TipoEntrega.PADRAO, null, pesoValido));
      System.out.println("✅ Distância null: " + exception2.getMessage());

      // Peso null deve usar peso padrão
      assertDoesNotThrow(
          () -> {
            BigDecimal valor = Frete.calcularFrete(TipoEntrega.PADRAO, distanciaValida, null);
            System.out.println("✅ Peso null (usa padrão): R$ " + valor);
            assertTrue(valor.compareTo(BigDecimal.ZERO) > 0);
          });

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve persistir fretes com observações longas")
    void devePersistirFretesComObservacoesLongas() {
      System.out.println("\n🧪 TESTE: Observações longas");

      // Given
      String observacaoLonga =
          "Esta é uma observação muito longa ".repeat(15).substring(0, 500); // ~525 caracteres
      System.out.println("📋 Tamanho da observação: " + observacaoLonga.length() + " caracteres");

      freteTeste1.setObservacoes(observacaoLonga);

      // When
      assertDoesNotThrow(
          () -> {
            Frete freteSalvo = freteRepository.save(freteTeste1);
            System.out.println("✅ Frete com observação longa salvo: " + freteSalvo.getId());

            // Verificar se foi salva corretamente
            Optional<Frete> freteRecuperado = freteRepository.findById(freteSalvo.getId());
            assertTrue(freteRecuperado.isPresent());
            assertEquals(observacaoLonga.trim(), freteRecuperado.get().getObservacoes());
            System.out.println("✅ Observação recuperada corretamente");
          });

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }
}
