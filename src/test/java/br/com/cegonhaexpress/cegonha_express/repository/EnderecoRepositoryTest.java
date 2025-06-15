package br.com.cegonhaexpress.cegonha_express.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
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
@DisplayName("EnderecoRepository - Testes de Persistência e Consultas")
class EnderecoRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private EnderecoRepository enderecoRepository;

  @Autowired private ClienteRepository clienteRepository;

  private Cliente clienteTeste1;
  private Cliente clienteTeste2;
  private Endereco enderecoTeste1;
  private Endereco enderecoTeste2;
  private Endereco enderecoTeste3;

  @BeforeEach
  void setUp() {
    System.out.println("\n=== CONFIGURANDO CENÁRIO DE TESTE ENDERECO REPOSITORY ===");

    // Clientes de teste
    clienteTeste1 = new Cliente("João Silva", "joao@email.com", "123.456.789-00");
    clienteTeste2 = new Cliente("Maria Santos", "maria@email.com", "987.654.321-11");

    // Endereços de teste
    enderecoTeste1 =
        new Endereco("13840-000", "Rua das Flores", "123", "Centro", "Mogi Guaçu", UF.SP);

    enderecoTeste2 = new Endereco("01001-000", "Praça da Sé", "456", "Centro", "São Paulo", UF.SP);

    enderecoTeste3 =
        new Endereco("20040-020", "Avenida Rio Branco", "789", "Centro", "Rio de Janeiro", UF.RJ);

    System.out.println("✅ Dados de teste configurados");
  }

  @Nested
  @DisplayName("Testes de CRUD Básico")
  class TestesCrudBasico {

    @Test
    @DisplayName("Deve salvar e buscar endereço por ID")
    void deveSalvarEBuscarEnderecoPorId() {
      System.out.println("\n🧪 TESTE: Salvar e buscar endereço por ID");

      // Given
      System.out.println("📋 Salvando endereço:");
      System.out.println("   CEP: " + enderecoTeste1.getCep());
      System.out.println("   Logradouro: " + enderecoTeste1.getLogradouro());
      System.out.println("   Cidade: " + enderecoTeste1.getCidade());

      // When
      Endereco enderecoSalvo = enderecoRepository.save(enderecoTeste1);
      Optional<Endereco> enderecoEncontrado = enderecoRepository.findById(enderecoSalvo.getId());

      // Then
      System.out.println("\n📊 Verificando resultados:");
      assertTrue(enderecoEncontrado.isPresent());
      System.out.println("✅ Endereço encontrado: " + enderecoEncontrado.isPresent());

      Endereco endereco = enderecoEncontrado.get();
      assertEquals(enderecoTeste1.getCep(), endereco.getCep());
      assertEquals(enderecoTeste1.getLogradouro(), endereco.getLogradouro());
      assertEquals(enderecoTeste1.getCidade(), endereco.getCidade());
      assertEquals(enderecoTeste1.getUf(), endereco.getUf());

      System.out.println("✅ CEP: " + endereco.getCep());
      System.out.println("✅ Logradouro: " + endereco.getLogradouro());
      System.out.println("✅ Cidade: " + endereco.getCidade());
      System.out.println("✅ UF: " + endereco.getUf());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve listar todos os endereços")
    void deveListarTodosOsEnderecos() {
      System.out.println("\n🧪 TESTE: Listar todos os endereços");

      // Given
      enderecoRepository.save(enderecoTeste1);
      enderecoRepository.save(enderecoTeste2);
      enderecoRepository.save(enderecoTeste3);
      System.out.println("📋 Total de endereços salvos: 3");

      // When
      List<Endereco> todosEnderecos = enderecoRepository.findAll();

      // Then
      System.out.println("\n📊 Verificando resultados:");
      assertEquals(3, todosEnderecos.size());
      System.out.println("✅ Quantidade correta: " + todosEnderecos.size());

      boolean contemMogiGuacu =
          todosEnderecos.stream().anyMatch(e -> e.getCidade().equals("Mogi Guaçu"));
      boolean contemSaoPaulo =
          todosEnderecos.stream().anyMatch(e -> e.getCidade().equals("São Paulo"));
      boolean contemRioJaneiro =
          todosEnderecos.stream().anyMatch(e -> e.getCidade().equals("Rio de Janeiro"));

      assertTrue(contemMogiGuacu);
      assertTrue(contemSaoPaulo);
      assertTrue(contemRioJaneiro);

      System.out.println("✅ Contém Mogi Guaçu: " + contemMogiGuacu);
      System.out.println("✅ Contém São Paulo: " + contemSaoPaulo);
      System.out.println("✅ Contém Rio de Janeiro: " + contemRioJaneiro);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Busca por CEP")
  class TestesBuscaPorCep {

    @Test
    @DisplayName("Deve encontrar endereços por CEP")
    void deveEncontrarEnderecosPorCep() {
      System.out.println("\n🧪 TESTE: Buscar endereços por CEP");

      // Given
      enderecoRepository.save(enderecoTeste1);
      enderecoRepository.save(enderecoTeste2);

      // Criar outro endereço com mesmo CEP
      Endereco enderecoMesmoCep =
          new Endereco("13840-000", "Rua das Palmeiras", "999", "Vila Nova", "Mogi Guaçu", UF.SP);
      enderecoRepository.save(enderecoMesmoCep);

      String cepBusca = "13840-000";
      System.out.println("📋 CEP para busca: " + cepBusca);

      // When
      List<Endereco> enderecosEncontrados = enderecoRepository.findByCep(cepBusca);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertEquals(2, enderecosEncontrados.size());
      System.out.println("✅ Quantidade encontrada: " + enderecosEncontrados.size());

      boolean todosComMesmoCep =
          enderecosEncontrados.stream().allMatch(e -> e.getCep().equals(cepBusca));
      assertTrue(todosComMesmoCep);
      System.out.println("✅ Todos com CEP correto: " + todosComMesmoCep);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve retornar lista vazia para CEP inexistente")
    void deveRetornarListaVaziaParaCepInexistente() {
      System.out.println("\n🧪 TESTE: Buscar CEP inexistente");

      // Given
      enderecoRepository.save(enderecoTeste1);
      String cepInexistente = "99999-999";
      System.out.println("📋 CEP inexistente: " + cepInexistente);

      // When
      List<Endereco> enderecosEncontrados = enderecoRepository.findByCep(cepInexistente);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertTrue(enderecosEncontrados.isEmpty());
      assertEquals(0, enderecosEncontrados.size());
      System.out.println("✅ Lista vazia (esperado): " + enderecosEncontrados.isEmpty());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Busca por Cliente")
  class TestesBuscaPorCliente {

    @Test
    @DisplayName("Deve encontrar endereços por cliente")
    void deveEncontrarEnderecosPorCliente() {
      System.out.println("\n🧪 TESTE: Buscar endereços por cliente");

      // Given
      Cliente clienteSalvo = clienteRepository.save(clienteTeste1);

      enderecoTeste1.setCliente(clienteSalvo);
      enderecoTeste2.setCliente(clienteSalvo);
      enderecoRepository.save(enderecoTeste1);
      enderecoRepository.save(enderecoTeste2);

      // Endereço de outro cliente
      Cliente outroCliente = clienteRepository.save(clienteTeste2);
      enderecoTeste3.setCliente(outroCliente);
      enderecoRepository.save(enderecoTeste3);

      System.out.println("📋 Cliente: " + clienteSalvo.getNome());
      System.out.println("📋 Endereços do cliente: 2 esperados");

      // When
      List<Endereco> enderecosDoCliente = enderecoRepository.findByCliente(clienteSalvo);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertEquals(2, enderecosDoCliente.size());
      System.out.println("✅ Quantidade encontrada: " + enderecosDoCliente.size());

      boolean todosDoCliente =
          enderecosDoCliente.stream().allMatch(e -> e.getCliente().equals(clienteSalvo));
      assertTrue(todosDoCliente);
      System.out.println("✅ Todos do cliente correto: " + todosDoCliente);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve encontrar endereços por ID do cliente")
    void deveEncontrarEnderecosPorIdDoCliente() {
      System.out.println("\n🧪 TESTE: Buscar endereços por ID do cliente");

      // Given
      Cliente clienteSalvo = clienteRepository.save(clienteTeste1);

      enderecoTeste1.setCliente(clienteSalvo);
      enderecoRepository.save(enderecoTeste1);

      System.out.println("📋 Cliente ID: " + clienteSalvo.getId());

      // When
      List<Endereco> enderecosEncontrados =
          enderecoRepository.findByClienteId(clienteSalvo.getId());

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertEquals(1, enderecosEncontrados.size());
      System.out.println("✅ Quantidade encontrada: " + enderecosEncontrados.size());

      Endereco endereco = enderecosEncontrados.get(0);
      assertEquals(clienteSalvo.getId(), endereco.getCliente().getId());
      System.out.println("✅ Cliente ID correto: " + endereco.getCliente().getId());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve retornar lista vazia para cliente sem endereços")
    void deveRetornarListaVaziaParaClienteSemEnderecos() {
      System.out.println("\n🧪 TESTE: Cliente sem endereços");

      // Given
      Cliente clienteSemEnderecos = clienteRepository.save(clienteTeste1);
      System.out.println("📋 Cliente sem endereços: " + clienteSemEnderecos.getNome());

      // When
      List<Endereco> enderecosEncontrados = enderecoRepository.findByCliente(clienteSemEnderecos);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertTrue(enderecosEncontrados.isEmpty());
      assertEquals(0, enderecosEncontrados.size());
      System.out.println("✅ Lista vazia (esperado): " + enderecosEncontrados.isEmpty());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Relacionamentos")
  class TestesRelacionamentos {

    @Test
    @DisplayName("Deve manter relacionamento bidirecional com cliente")
    void deveManterRelacionamentoBidirecionalComCliente() {
      System.out.println("\n🧪 TESTE: Relacionamento bidirecional");

      // Given
      Cliente cliente = clienteRepository.save(clienteTeste1);
      enderecoTeste1.setCliente(cliente);
      Endereco enderecoSalvo = enderecoRepository.save(enderecoTeste1);

      System.out.println("📋 Cliente: " + cliente.getNome());
      System.out.println("📋 Endereço: " + enderecoSalvo.getEnderecoCompleto());

      // When
      entityManager.clear(); // Força nova busca
      Optional<Endereco> enderecoEncontrado = enderecoRepository.findById(enderecoSalvo.getId());

      // Then
      System.out.println("\n📊 Verificando relacionamento:");
      assertTrue(enderecoEncontrado.isPresent());
      System.out.println("✅ Endereço encontrado: " + enderecoEncontrado.isPresent());

      Endereco endereco = enderecoEncontrado.get();
      assertNotNull(endereco.getCliente());
      assertEquals(cliente.getId(), endereco.getCliente().getId());
      assertEquals(cliente.getNome(), endereco.getCliente().getNome());

      System.out.println("✅ Cliente associado: " + endereco.getCliente().getNome());
      System.out.println("✅ IDs coincidem: " + endereco.getCliente().getId());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve funcionar sem cliente associado")
    void deveFuncionarSemClienteAssociado() {
      System.out.println("\n🧪 TESTE: Endereço sem cliente");

      // Given & When
      Endereco enderecoSemCliente = enderecoRepository.save(enderecoTeste1);

      // Then
      System.out.println("\n📊 Verificando endereço independente:");
      assertNotNull(enderecoSemCliente.getId());
      assertNull(enderecoSemCliente.getCliente());
      assertTrue(enderecoSemCliente.isCompleto());

      System.out.println("✅ Endereço salvo: ID " + enderecoSemCliente.getId());
      System.out.println("✅ Sem cliente: " + (enderecoSemCliente.getCliente() == null));
      System.out.println("✅ Dados completos: " + enderecoSemCliente.isCompleto());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Métodos Utilitários")
  class TestesMetodosUtilitarios {

    @Test
    @DisplayName("Deve funcionar com métodos utilitários do Endereco")
    void deveFuncionarComMetodosUtilitariosDoEndereco() {
      System.out.println("\n🧪 TESTE: Métodos utilitários");

      // Given
      Endereco endereco =
          new Endereco("12345678", "Av. Central", "1000", "Centro", "São Paulo", UF.SP);
      endereco.setComplemento("Sala 101");
      endereco.setPontoReferencia("Próximo ao metrô");

      System.out.println("📋 Endereço antes de salvar:");
      System.out.println("   CEP original: " + endereco.getCep());
      System.out.println("   CEP limpo: " + endereco.getCepLimpo());
      System.out.println("   CEP formatado: " + endereco.getCepFormatado());
      System.out.println("   Endereço completo: " + endereco.getEnderecoCompleto());
      System.out.println("   É completo: " + endereco.isCompleto());

      // When
      Endereco enderecoSalvo = enderecoRepository.save(endereco);

      // Then
      System.out.println("\n📊 Verificando após persistência:");
      assertEquals("12345678", enderecoSalvo.getCepLimpo());
      assertEquals("12345-678", enderecoSalvo.getCepFormatado());
      assertTrue(enderecoSalvo.isCompleto());
      assertTrue(enderecoSalvo.getEnderecoCompleto().contains("Av. Central"));
      assertTrue(enderecoSalvo.getEnderecoCompleto().contains("São Paulo"));

      System.out.println("✅ CEP limpo: " + enderecoSalvo.getCepLimpo());
      System.out.println("✅ CEP formatado: " + enderecoSalvo.getCepFormatado());
      System.out.println("✅ É completo: " + enderecoSalvo.isCompleto());
      System.out.println("✅ Endereço completo: " + enderecoSalvo.getEnderecoCompleto());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Performance")
  class TestesPerformance {

    @Test
    @DisplayName("Deve manter performance com múltiplos endereços")
    void deveManterPerformanceComMultiplosEnderecos() {
      System.out.println("\n🧪 TESTE: Performance com múltiplos endereços");

      // Given
      Cliente cliente = clienteRepository.save(clienteTeste1);

      System.out.println("📋 Criando múltiplos endereços:");
      for (int i = 1; i <= 5; i++) {
        Endereco endereco =
            new Endereco(
                String.format("1234%d-00%d", i, i),
                "Rua Teste " + i,
                String.valueOf(i * 100),
                "Bairro " + i,
                "Cidade " + i,
                UF.SP);
        endereco.setCliente(cliente);
        enderecoRepository.save(endereco);
      }
      System.out.println("   Total criados: 5 endereços");

      // When
      long inicioTempo = System.currentTimeMillis();

      List<Endereco> enderecosPorCliente = enderecoRepository.findByCliente(cliente);
      List<Endereco> enderecosPorCep = enderecoRepository.findByCep("12341-001");
      List<Endereco> todosEnderecos = enderecoRepository.findAll();

      long fimTempo = System.currentTimeMillis();
      long tempoTotal = fimTempo - inicioTempo;

      // Then
      System.out.println("\n📊 Resultados de performance:");
      assertEquals(5, enderecosPorCliente.size());
      assertEquals(1, enderecosPorCep.size());
      assertEquals(5, todosEnderecos.size());

      System.out.println("✅ Por cliente: " + enderecosPorCliente.size());
      System.out.println("✅ Por CEP: " + enderecosPorCep.size());
      System.out.println("✅ Total: " + todosEnderecos.size());
      System.out.println("⏱️ Tempo total: " + tempoTotal + "ms");

      assertTrue(tempoTotal < 1000);
      System.out.println("✅ Performance adequada (< 1000ms)");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }
}
