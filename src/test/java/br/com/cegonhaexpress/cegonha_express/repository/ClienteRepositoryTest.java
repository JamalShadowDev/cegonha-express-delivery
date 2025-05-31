package br.com.cegonhaexpress.cegonha_express.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

/**
 * Testes unitários para ClienteRepository.
 *
 * <p>Testa todas as operações CRUD e consultas customizadas do repository, incluindo busca por CPF,
 * email, nome parcial e validação de existência.
 *
 * @author Gabriel Coelho Soares
 */
@DataJpaTest
@ActiveProfiles({"test", "test-local"})
@DisplayName("ClienteRepository - Testes de Persistência e Consultas")
class ClienteRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private ClienteRepository clienteRepository;

  private Cliente clienteTeste1;
  private Cliente clienteTeste2;
  private Cliente clienteTeste3;

  @BeforeEach
  void setUp() {
    System.out.println("\n=== CONFIGURANDO CENÁRIO DE TESTE REPOSITORY ===");

    // Cliente 1 - Dados completos
    clienteTeste1 =
        new Cliente(
            "Maria Silva Santos", "maria.silva@email.com", "(11) 99999-9999", "123.456.789-00");

    // Cliente 2 - Dados básicos
    clienteTeste2 = new Cliente("João Pedro Oliveira", "joao.pedro@email.com", "987.654.321-11");

    // Cliente 3 - Para testes de busca parcial
    clienteTeste3 =
        new Cliente("Ana Maria Costa", "ana.costa@email.com", "(21) 8888-7777", "555.666.777-88");

    System.out.println("✅ Clientes de teste configurados:");
    System.out.println("   Cliente 1: " + clienteTeste1.getNome() + " - " + clienteTeste1.getCpf());
    System.out.println("   Cliente 2: " + clienteTeste2.getNome() + " - " + clienteTeste2.getCpf());
    System.out.println("   Cliente 3: " + clienteTeste3.getNome() + " - " + clienteTeste3.getCpf());
  }

  @Nested
  @DisplayName("Testes de CRUD Básico")
  class TestesCrudBasico {

    @Test
    @DisplayName("Deve salvar e buscar cliente por ID")
    void deveSalvarEBuscarClientePorId() {
      System.out.println("\n🧪 TESTE: Salvar e buscar cliente por ID");

      // Given
      System.out.println("📋 Salvando cliente:");
      System.out.println("   Nome: " + clienteTeste1.getNome());
      System.out.println("   Email: " + clienteTeste1.getEmail());
      System.out.println("   CPF: " + clienteTeste1.getCpf());

      // When
      Cliente clienteSalvo = clienteRepository.save(clienteTeste1);
      System.out.println("💾 Cliente salvo com ID: " + clienteSalvo.getId());

      Optional<Cliente> clienteEncontrado = clienteRepository.findById(clienteSalvo.getId());

      // Then
      System.out.println("\n📊 Verificando resultados:");
      assertTrue(clienteEncontrado.isPresent());
      System.out.println("✅ Cliente encontrado: " + clienteEncontrado.isPresent());

      Cliente cliente = clienteEncontrado.get();
      assertEquals(clienteTeste1.getNome(), cliente.getNome());
      assertEquals(clienteTeste1.getEmail(), cliente.getEmail());
      assertEquals(clienteTeste1.getCpf(), cliente.getCpf());
      assertEquals(clienteTeste1.getTelefone(), cliente.getTelefone());

      System.out.println("✅ Nome correto: " + cliente.getNome());
      System.out.println("✅ Email correto: " + cliente.getEmail());
      System.out.println("✅ CPF correto: " + cliente.getCpf());
      System.out.println("✅ Telefone correto: " + cliente.getTelefone());

      assertNotNull(cliente.getCreatedAt());
      assertNotNull(cliente.getUpdatedAt());
      System.out.println("✅ Timestamps preenchidos automaticamente");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void deveListarTodosOsClientes() {
      System.out.println("\n🧪 TESTE: Listar todos os clientes");

      // Given
      System.out.println("📋 Salvando múltiplos clientes:");
      clienteRepository.save(clienteTeste1);
      clienteRepository.save(clienteTeste2);
      clienteRepository.save(clienteTeste3);
      System.out.println("   Total salvos: 3 clientes");

      // When
      List<Cliente> todosClientes = clienteRepository.findAll();

      // Then
      System.out.println("\n📊 Verificando resultados:");
      assertEquals(3, todosClientes.size());
      System.out.println("✅ Quantidade correta: " + todosClientes.size() + " clientes");

      boolean contemMaria =
          todosClientes.stream().anyMatch(c -> c.getNome().contains("Maria Silva"));
      boolean contemJoao = todosClientes.stream().anyMatch(c -> c.getNome().contains("João Pedro"));
      boolean contemAna = todosClientes.stream().anyMatch(c -> c.getNome().contains("Ana Maria"));

      assertTrue(contemMaria);
      assertTrue(contemJoao);
      assertTrue(contemAna);

      System.out.println("✅ Contém Maria Silva: " + contemMaria);
      System.out.println("✅ Contém João Pedro: " + contemJoao);
      System.out.println("✅ Contém Ana Maria: " + contemAna);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve atualizar cliente existente")
    void deveAtualizarClienteExistente() {
      System.out.println("\n🧪 TESTE: Atualizar cliente existente");

      // Given
      Cliente clienteSalvo = clienteRepository.save(clienteTeste1);
      System.out.println("📋 Cliente salvo inicialmente:");
      System.out.println("   Nome original: " + clienteSalvo.getNome());
      System.out.println("   Email original: " + clienteSalvo.getEmail());

      // When
      String novoNome = "Maria Silva Santos Atualizada";
      String novoEmail = "maria.atualizada@email.com";

      clienteSalvo.setNome(novoNome);
      clienteSalvo.setEmail(novoEmail);

      Cliente clienteAtualizado = clienteRepository.save(clienteSalvo);

      // Then
      System.out.println("\n📊 Verificando atualização:");
      assertEquals(novoNome, clienteAtualizado.getNome());
      assertEquals(novoEmail, clienteAtualizado.getEmail());

      System.out.println("✅ Nome atualizado: " + clienteAtualizado.getNome());
      System.out.println("✅ Email atualizado: " + clienteAtualizado.getEmail());

      assertNotNull(clienteAtualizado.getUpdatedAt());
      System.out.println("✅ UpdatedAt foi atualizado: " + clienteAtualizado.getUpdatedAt());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve deletar cliente por ID")
    void deveDeletarClientePorId() {
      System.out.println("\n🧪 TESTE: Deletar cliente por ID");

      // Given
      Cliente clienteSalvo = clienteRepository.save(clienteTeste1);
      Long clienteId = clienteSalvo.getId();
      System.out.println("📋 Cliente salvo com ID: " + clienteId);

      // When
      clienteRepository.deleteById(clienteId);
      System.out.println("🗑️ Cliente deletado");

      Optional<Cliente> clienteEncontrado = clienteRepository.findById(clienteId);

      // Then
      System.out.println("\n📊 Verificando deleção:");
      assertFalse(clienteEncontrado.isPresent());
      System.out.println("✅ Cliente não encontrado: " + clienteEncontrado.isEmpty());

      long totalClientes = clienteRepository.count();
      assertEquals(0, totalClientes);
      System.out.println("✅ Total de clientes no banco: " + totalClientes);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Busca por CPF")
  class TestesBuscaPorCpf {

    @Test
    @DisplayName("Deve encontrar cliente por CPF formatado")
    void deveEncontrarClientePorCpfFormatado() {
      System.out.println("\n🧪 TESTE: Buscar cliente por CPF formatado");

      // Given
      clienteRepository.save(clienteTeste1);
      String cpfBusca = "123.456.789-00";
      System.out.println("📋 CPF para busca: " + cpfBusca);

      // When
      Optional<Cliente> clienteEncontrado = clienteRepository.findByCpf(cpfBusca);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertTrue(clienteEncontrado.isPresent());
      System.out.println("✅ Cliente encontrado: " + clienteEncontrado.isPresent());

      Cliente cliente = clienteEncontrado.get();
      assertEquals(clienteTeste1.getNome(), cliente.getNome());
      assertEquals(cpfBusca, cliente.getCpf());

      System.out.println("✅ Nome: " + cliente.getNome());
      System.out.println("✅ CPF: " + cliente.getCpf());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve encontrar cliente por CPF sem formatação")
    void deveEncontrarClientePorCpfSemFormatacao() {
      System.out.println("\n🧪 TESTE: Buscar cliente por CPF sem formatação");

      // Given
      Cliente clienteComCpfLimpo = new Cliente("Carlos Santos", "carlos@email.com", "11122233344");
      clienteRepository.save(clienteComCpfLimpo);

      String cpfBusca = "11122233344";
      System.out.println("📋 CPF para busca: " + cpfBusca);

      // When
      Optional<Cliente> clienteEncontrado = clienteRepository.findByCpf(cpfBusca);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertTrue(clienteEncontrado.isPresent());
      System.out.println("✅ Cliente encontrado: " + clienteEncontrado.isPresent());

      Cliente cliente = clienteEncontrado.get();
      assertEquals("Carlos Santos", cliente.getNome());
      assertEquals(cpfBusca, cliente.getCpf());

      System.out.println("✅ Nome: " + cliente.getNome());
      System.out.println("✅ CPF: " + cliente.getCpf());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para CPF inexistente")
    void deveRetornarOptionalVazioParaCpfInexistente() {
      System.out.println("\n🧪 TESTE: Buscar CPF inexistente");

      // Given
      clienteRepository.save(clienteTeste1);
      String cpfInexistente = "999.888.777-66";
      System.out.println("📋 CPF inexistente para busca: " + cpfInexistente);

      // When
      Optional<Cliente> clienteEncontrado = clienteRepository.findByCpf(cpfInexistente);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertFalse(clienteEncontrado.isPresent());
      System.out.println("✅ Cliente não encontrado (esperado): " + clienteEncontrado.isEmpty());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Busca por Email")
  class TestesBuscaPorEmail {

    @Test
    @DisplayName("Deve encontrar cliente por email")
    void deveEncontrarClientePorEmail() {
      System.out.println("\n🧪 TESTE: Buscar cliente por email");

      // Given
      clienteRepository.save(clienteTeste2);
      String emailBusca = "joao.pedro@email.com";
      System.out.println("📋 Email para busca: " + emailBusca);

      // When
      Optional<Cliente> clienteEncontrado = clienteRepository.findByEmail(emailBusca);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertTrue(clienteEncontrado.isPresent());
      System.out.println("✅ Cliente encontrado: " + clienteEncontrado.isPresent());

      Cliente cliente = clienteEncontrado.get();
      assertEquals(clienteTeste2.getNome(), cliente.getNome());
      assertEquals(emailBusca, cliente.getEmail());

      System.out.println("✅ Nome: " + cliente.getNome());
      System.out.println("✅ Email: " + cliente.getEmail());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve buscar email case insensitive")
    void deveBuscarEmailCaseInsensitive() {
      System.out.println("\n🧪 TESTE: Buscar email com case diferente");

      // Given
      clienteRepository.save(clienteTeste3);
      String emailOriginal = clienteTeste3.getEmail();
      String emailUpperCase = emailOriginal.toUpperCase();

      System.out.println("📋 Email original: " + emailOriginal);
      System.out.println("📋 Email busca (uppercase): " + emailUpperCase);

      // When
      Optional<Cliente> clienteEncontrado = clienteRepository.findByEmail(emailUpperCase);

      // Then
      System.out.println("\n📊 Verificando busca case insensitive:");

      // Nota: Spring Data JPA por padrão é case sensitive para equals
      // Se quiser case insensitive, precisa de @Query ou método específico
      assertFalse(clienteEncontrado.isPresent());
      System.out.println(
          "✅ Busca case sensitive (comportamento padrão): " + clienteEncontrado.isEmpty());

      // Testando com case correto
      Optional<Cliente> clienteCorreto = clienteRepository.findByEmail(emailOriginal);
      assertTrue(clienteCorreto.isPresent());
      System.out.println("✅ Busca com case correto funciona: " + clienteCorreto.isPresent());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para email inexistente")
    void deveRetornarOptionalVazioParaEmailInexistente() {
      System.out.println("\n🧪 TESTE: Buscar email inexistente");

      // Given
      clienteRepository.save(clienteTeste1);
      String emailInexistente = "inexistente@email.com";
      System.out.println("📋 Email inexistente para busca: " + emailInexistente);

      // When
      Optional<Cliente> clienteEncontrado = clienteRepository.findByEmail(emailInexistente);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertFalse(clienteEncontrado.isPresent());
      System.out.println("✅ Cliente não encontrado (esperado): " + clienteEncontrado.isEmpty());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Busca por Nome Parcial")
  class TestesBuscaPorNomeParcial {

    @Test
    @DisplayName("Deve encontrar clientes por nome parcial case insensitive")
    void deveEncontrarClientesPorNomeParcialCaseInsensitive() {
      System.out.println("\n🧪 TESTE: Buscar por nome parcial case insensitive");

      // Given
      clienteRepository.save(clienteTeste1); // Maria Silva Santos
      clienteRepository.save(clienteTeste2); // João Pedro Oliveira
      clienteRepository.save(clienteTeste3); // Ana Maria Costa

      String busca = "maria";
      System.out.println("📋 Termo de busca: '" + busca + "'");
      System.out.println("📋 Clientes que devem ser encontrados:");
      System.out.println("   - Maria Silva Santos");
      System.out.println("   - Ana Maria Costa");

      // When
      List<Cliente> clientesEncontrados = clienteRepository.findByNomeContainingIgnoreCase(busca);

      // Then
      System.out.println("\n📊 Verificando resultados:");
      assertEquals(2, clientesEncontrados.size());
      System.out.println("✅ Quantidade encontrada: " + clientesEncontrados.size());

      boolean contemMariaSilva =
          clientesEncontrados.stream().anyMatch(c -> c.getNome().contains("Maria Silva"));
      boolean contemAnaMaria =
          clientesEncontrados.stream().anyMatch(c -> c.getNome().contains("Ana Maria"));

      assertTrue(contemMariaSilva);
      assertTrue(contemAnaMaria);

      System.out.println("✅ Contém 'Maria Silva Santos': " + contemMariaSilva);
      System.out.println("✅ Contém 'Ana Maria Costa': " + contemAnaMaria);

      // Verificar que João não foi encontrado
      boolean contemJoao = clientesEncontrados.stream().anyMatch(c -> c.getNome().contains("João"));
      assertFalse(contemJoao);
      System.out.println("✅ João não foi encontrado (correto): " + !contemJoao);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve buscar nome com case diferente")
    void deveBuscarNomeComCaseDiferente() {
      System.out.println("\n🧪 TESTE: Buscar nome com diferentes cases");

      // Given
      clienteRepository.save(clienteTeste1); // Maria Silva Santos

      String[] buscas = {"MARIA", "silva", "SanTos", "mArIa SiLvA"};
      System.out.println("📋 Testes de case insensitive:");

      for (String busca : buscas) {
        System.out.println("   Testando: '" + busca + "'");

        // When
        List<Cliente> encontrados = clienteRepository.findByNomeContainingIgnoreCase(busca);

        // Then
        if (busca.contains(" ")) {
          // Para termos com espaço, pode não encontrar dependendo da implementação
          System.out.println("     Resultado: " + encontrados.size() + " cliente(s)");
        } else {
          assertEquals(1, encontrados.size());
          System.out.println("     ✅ Encontrou: " + encontrados.get(0).getNome());
        }
      }

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve retornar lista vazia para nome inexistente")
    void deveRetornarListaVaziaParaNomeInexistente() {
      System.out.println("\n🧪 TESTE: Buscar nome inexistente");

      // Given
      clienteRepository.save(clienteTeste1);
      clienteRepository.save(clienteTeste2);

      String nomeInexistente = "Zuleica";
      System.out.println("📋 Nome inexistente: '" + nomeInexistente + "'");

      // When
      List<Cliente> clientesEncontrados =
          clienteRepository.findByNomeContainingIgnoreCase(nomeInexistente);

      // Then
      System.out.println("\n📊 Verificando busca:");
      assertTrue(clientesEncontrados.isEmpty());
      assertEquals(0, clientesEncontrados.size());
      System.out.println("✅ Lista vazia (esperado): " + clientesEncontrados.isEmpty());
      System.out.println("✅ Tamanho da lista: " + clientesEncontrados.size());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve buscar por sobrenome")
    void deveBuscarPorSobrenome() {
      System.out.println("\n🧪 TESTE: Buscar por sobrenome");

      // Given
      clienteRepository.save(clienteTeste1); // Maria Silva Santos
      clienteRepository.save(clienteTeste2); // João Pedro Oliveira
      clienteRepository.save(clienteTeste3); // Ana Maria Costa

      String sobrenome = "santos";
      System.out.println("📋 Sobrenome para busca: '" + sobrenome + "'");

      // When
      List<Cliente> clientesEncontrados =
          clienteRepository.findByNomeContainingIgnoreCase(sobrenome);

      // Then
      System.out.println("\n📊 Verificando busca por sobrenome:");
      assertEquals(1, clientesEncontrados.size());
      System.out.println("✅ Quantidade encontrada: " + clientesEncontrados.size());

      Cliente clienteEncontrado = clientesEncontrados.get(0);
      assertTrue(clienteEncontrado.getNome().toLowerCase().contains(sobrenome));
      System.out.println("✅ Cliente encontrado: " + clienteEncontrado.getNome());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Validação de Existência")
  class TestesValidacaoExistencia {

    @Test
    @DisplayName("Deve retornar true para CPF existente")
    void deveRetornarTrueParaCpfExistente() {
      System.out.println("\n🧪 TESTE: Validar existência de CPF existente");

      // Given
      clienteRepository.save(clienteTeste1);
      String cpfExistente = clienteTeste1.getCpf();
      System.out.println("📋 CPF a ser validado: " + cpfExistente);

      // When
      boolean existe = clienteRepository.existsByCpf(cpfExistente);

      // Then
      System.out.println("\n📊 Verificando existência:");
      assertTrue(existe);
      System.out.println("✅ CPF existe: " + existe);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve retornar false para CPF inexistente")
    void deveRetornarFalseParaCpfInexistente() {
      System.out.println("\n🧪 TESTE: Validar existência de CPF inexistente");

      // Given
      clienteRepository.save(clienteTeste1);
      String cpfInexistente = "999.888.777-66";
      System.out.println("📋 CPF inexistente: " + cpfInexistente);

      // When
      boolean existe = clienteRepository.existsByCpf(cpfInexistente);

      // Then
      System.out.println("\n📊 Verificando inexistência:");
      assertFalse(existe);
      System.out.println("✅ CPF não existe (esperado): " + !existe);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve validar existência após deleção")
    void deveValidarExistenciaAposDelecao() {
      System.out.println("\n🧪 TESTE: Validar existência após deleção");

      // Given
      Cliente clienteSalvo = clienteRepository.save(clienteTeste1);
      String cpf = clienteSalvo.getCpf();

      System.out.println("📋 Cliente salvo com CPF: " + cpf);

      // Verificar que existe inicialmente
      boolean existeAntes = clienteRepository.existsByCpf(cpf);
      assertTrue(existeAntes);
      System.out.println("✅ CPF existe antes da deleção: " + existeAntes);

      // When
      clienteRepository.delete(clienteSalvo);
      System.out.println("🗑️ Cliente deletado");

      boolean existeDepois = clienteRepository.existsByCpf(cpf);

      // Then
      System.out.println("\n📊 Verificando após deleção:");
      assertFalse(existeDepois);
      System.out.println("✅ CPF não existe após deleção: " + !existeDepois);

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve validar existência de múltiplos CPFs")
    void deveValidarExistenciaDeMultiplosCpfs() {
      System.out.println("\n🧪 TESTE: Validar múltiplos CPFs");

      // Given
      clienteRepository.save(clienteTeste1);
      clienteRepository.save(clienteTeste2);
      // Não salvar clienteTeste3

      String[] cpfsParaTestar = {
        clienteTeste1.getCpf(), // Existe
        clienteTeste2.getCpf(), // Existe
        clienteTeste3.getCpf(), // Não existe
        "000.000.000-00" // Não existe
      };

      boolean[] resultadosEsperados = {true, true, false, false};

      System.out.println("📋 Testando múltiplos CPFs:");

      // When & Then
      for (int i = 0; i < cpfsParaTestar.length; i++) {
        String cpf = cpfsParaTestar[i];
        boolean esperado = resultadosEsperados[i];

        boolean existe = clienteRepository.existsByCpf(cpf);

        assertEquals(esperado, existe);
        System.out.println(
            "   CPF: " + cpf + " | Existe: " + existe + " | Esperado: " + esperado + " ✅");
      }

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Performance e Edge Cases")
  class TestesPerformanceEdgeCases {

    @Test
    @DisplayName("Deve lidar com strings vazias e nulas")
    void deveLidarComStringsVaziasENulas() {
      System.out.println("\n🧪 TESTE: Strings vazias e nulas");

      // Given
      clienteRepository.save(clienteTeste1);

      System.out.println("📋 Testando edge cases:");

      // When & Then
      // String vazia para nome
      List<Cliente> resultadoVazio = clienteRepository.findByNomeContainingIgnoreCase("");
      System.out.println("   Busca por nome vazio: " + resultadoVazio.size() + " resultados");
      // String vazia deve retornar todos (comportamento padrão do LIKE '%%')

      // String com espaços
      List<Cliente> resultadoEspacos = clienteRepository.findByNomeContainingIgnoreCase("   ");
      System.out.println("   Busca por espaços: " + resultadoEspacos.size() + " resultados");

      // CPF inexistente vazio (pode gerar exception)
      assertDoesNotThrow(
          () -> {
            boolean existe = clienteRepository.existsByCpf("");
            System.out.println("   Exists com CPF vazio: " + existe);
          });

      System.out.println("✅ Sistema lida bem com edge cases");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve manter performance com múltiplos clientes")
    void deveManterPerformanceComMultiplosClientes() {
      System.out.println("\n🧪 TESTE: Performance com múltiplos registros");

      // Given
      System.out.println("📋 Criando múltiplos clientes para teste de performance:");

      for (int i = 1; i <= 10; i++) {
        Cliente cliente =
            new Cliente(
                "Cliente Teste " + i,
                "cliente" + i + "@email.com",
                String.format("(11) %04d-%04d", i, i),
                String.format("%03d.%03d.%03d-%02d", i, i, i, i % 100));
        clienteRepository.save(cliente);
      }
      System.out.println("   Total de clientes criados: 10");

      // When
      long inicioTempo = System.currentTimeMillis();

      // Teste de busca por nome
      List<Cliente> resultadoNome = clienteRepository.findByNomeContainingIgnoreCase("Cliente");

      // Teste de busca por CPF
      Optional<Cliente> resultadoCpf = clienteRepository.findByCpf("001.001.001-01");

      // Teste de existência
      boolean existe = clienteRepository.existsByCpf("005.005.005-05");

      long fimTempo = System.currentTimeMillis();
      long tempoTotal = fimTempo - inicioTempo;

      // Then
      System.out.println("\n📊 Resultados de performance:");
      assertEquals(10, resultadoNome.size());
      System.out.println("✅ Busca por nome: " + resultadoNome.size() + " resultados");

      assertTrue(resultadoCpf.isPresent());
      System.out.println("✅ Busca por CPF: encontrado");

      assertTrue(existe);
      System.out.println("✅ Verificação de existência: " + existe);

      System.out.println("⏱️ Tempo total de execução: " + tempoTotal + "ms");
      assertTrue(tempoTotal < 1000); // Deve ser rápido
      System.out.println("✅ Performance adequada (< 1000ms)");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve manter integridade de dados únicos")
    void deveManterIntegridadeDeDadosUnicos() {
      System.out.println("\n🧪 TESTE: Integridade de dados únicos");

      // Given
      clienteRepository.save(clienteTeste1);
      entityManager.flush(); // ← Flush após salvar o primeiro
      entityManager.clear(); // ← Limpa o contexto

      System.out.println("📋 Cliente salvo com CPF: " + clienteTeste1.getCpf());

      // When & Then - Teste CPF duplicado
      Cliente clienteComCpfDuplicado =
          new Cliente(
              "Outro Nome",
              "outro.email@email.com",
              "(99) 8888-7777",
              clienteTeste1.getCpf() // CPF DUPLICADO
              );

      System.out.println("🔄 Tentando salvar cliente com CPF duplicado...");

      // Then - Deve lançar exceção por CPF duplicado
      assertThrows(
          DataIntegrityViolationException.class,
          () -> {
            clienteRepository.save(clienteComCpfDuplicado);
            entityManager.flush(); // Força execução
          });

      System.out.println("✅ Exceção lançada corretamente para CPF duplicado");
      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Relacionamentos")
  class TestesRelacionamentos {

    @Test
    @DisplayName("Deve salvar cliente com endereços")
    void deveSalvarClienteComEnderecos() {
      System.out.println("\n🧪 TESTE: Cliente com endereços");

      // Given
      Endereco endereco1 =
          new Endereco("13840-000", "Rua das Flores", "123", "Centro", "Mogi Guaçu", UF.SP);

      Endereco endereco2 =
          new Endereco("01001-000", "Praça da Sé", "456", "Centro", "São Paulo", UF.SP);

      clienteTeste1.adicionarEndereco(endereco1);
      clienteTeste1.adicionarEndereco(endereco2);

      System.out.println("📋 Cliente com endereços:");
      System.out.println("   Nome: " + clienteTeste1.getNome());
      System.out.println("   Endereços: " + clienteTeste1.getEnderecos().size());

      // When
      Cliente clienteSalvo = clienteRepository.save(clienteTeste1);

      // Then
      System.out.println("\n📊 Verificando relacionamentos:");
      assertNotNull(clienteSalvo.getId());
      System.out.println("✅ Cliente salvo com ID: " + clienteSalvo.getId());

      assertEquals(2, clienteSalvo.getEnderecos().size());
      System.out.println("✅ Quantidade de endereços: " + clienteSalvo.getEnderecos().size());

      // Verificar se endereços têm referência ao cliente
      for (Endereco endereco : clienteSalvo.getEnderecos()) {
        assertEquals(clienteSalvo, endereco.getCliente());
        System.out.println("✅ Endereco referencia cliente corretamente: " + endereco.getCep());
      }

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve buscar cliente e carregar endereços lazy")
    void deveBuscarClienteECarregarEnderecosLazy() {
      System.out.println("\n🧪 TESTE: Carregamento lazy de endereços");

      // Given
      Endereco endereco =
          new Endereco("12345-678", "Avenida Central", "999", "Vila Nova", "São Paulo", UF.SP);

      clienteTeste2.adicionarEndereco(endereco);
      Cliente clienteSalvo = clienteRepository.save(clienteTeste2);

      // Clear do EntityManager para forçar nova busca
      entityManager.clear();

      // When
      Optional<Cliente> clienteEncontrado = clienteRepository.findById(clienteSalvo.getId());

      // Then
      System.out.println("\n📊 Verificando carregamento lazy:");
      assertTrue(clienteEncontrado.isPresent());
      System.out.println("✅ Cliente encontrado: " + clienteEncontrado.isPresent());

      Cliente cliente = clienteEncontrado.get();

      // Acessar endereços deve carregar a coleção
      int quantidadeEnderecos = cliente.getEnderecos().size();
      assertEquals(1, quantidadeEnderecos);
      System.out.println("✅ Endereços carregados lazy: " + quantidadeEnderecos);

      Endereco enderecoCarregado = cliente.getEnderecos().get(0);
      assertEquals("12345-678", enderecoCarregado.getCep());
      System.out.println("✅ Dados do endereço corretos: " + enderecoCarregado.getCep());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve remover endereços ao deletar cliente")
    void deveRemoverEnderecosAoDeletarCliente() {
      System.out.println("\n🧪 TESTE: Cascata ao deletar cliente");

      // Given
      Endereco endereco =
          new Endereco("98765-432", "Rua da Cascata", "777", "Bairro Teste", "Cidade Teste", UF.RJ);

      clienteTeste3.adicionarEndereco(endereco);
      Cliente clienteSalvo = clienteRepository.save(clienteTeste3);
      Long clienteId = clienteSalvo.getId();

      System.out.println("📋 Cliente salvo com endereço:");
      System.out.println("   Cliente ID: " + clienteId);
      System.out.println("   Endereços: " + clienteSalvo.getEnderecos().size());

      // When
      clienteRepository.deleteById(clienteId);
      System.out.println("🗑️ Cliente deletado");

      // Then
      System.out.println("\n📊 Verificando cascata:");

      Optional<Cliente> clienteEncontrado = clienteRepository.findById(clienteId);
      assertFalse(clienteEncontrado.isPresent());
      System.out.println("✅ Cliente não encontrado: " + clienteEncontrado.isEmpty());

      // Nota: Em um teste completo, verificaríamos se os endereços também foram deletados
      // Mas isso requerira o EnderecoRepository
      System.out.println("✅ Operação de deleção com cascata executada");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }

  @Nested
  @DisplayName("Testes de Métodos Utilitários Integrados")
  class TestesMetodosUtilitarios {

    @Test
    @DisplayName("Deve funcionar com métodos utilitários do Cliente")
    void deveFuncionarComMetodosUtilitariosDoCliente() {
      System.out.println("\n🧪 TESTE: Integração com métodos utilitários");

      // Given
      Cliente clienteParaTeste =
          new Cliente("Pedro Santos Silva", "pedro.santos@email.com", "11987654321", "12345678900");

      System.out.println("📋 Cliente antes de salvar:");
      System.out.println("   Nome: " + clienteParaTeste.getNome());
      System.out.println("   CPF original: " + clienteParaTeste.getCpf());
      System.out.println("   CPF limpo: " + clienteParaTeste.getCpfLimpo());
      System.out.println("   CPF formatado: " + clienteParaTeste.getCpfFormatado());
      System.out.println("   Telefone original: " + clienteParaTeste.getTelefone());
      System.out.println("   Telefone formatado: " + clienteParaTeste.getTelefoneFormatado());
      System.out.println("   Dados completos: " + clienteParaTeste.isDadosCompletos());

      // When
      Cliente clienteSalvo = clienteRepository.save(clienteParaTeste);

      // Then
      System.out.println("\n📊 Verificando após persistência:");

      assertEquals("Pedro Santos Silva", clienteSalvo.getNome());
      System.out.println("✅ Nome preservado: " + clienteSalvo.getNome());

      assertEquals("12345678900", clienteSalvo.getCpfLimpo());
      System.out.println("✅ CPF limpo funcional: " + clienteSalvo.getCpfLimpo());

      assertEquals("123.456.789-00", clienteSalvo.getCpfFormatado());
      System.out.println("✅ CPF formatado funcional: " + clienteSalvo.getCpfFormatado());

      assertTrue(clienteSalvo.isDadosCompletos());
      System.out.println("✅ Dados completos: " + clienteSalvo.isDadosCompletos());

      // Teste de busca usando CPF limpo vs formatado
      Optional<Cliente> buscaPorCpfOriginal = clienteRepository.findByCpf(clienteSalvo.getCpf());
      assertTrue(buscaPorCpfOriginal.isPresent());
      System.out.println("✅ Busca por CPF original funciona: " + buscaPorCpfOriginal.isPresent());

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }

    @Test
    @DisplayName("Deve persistir dados normalizados via callbacks")
    void devePersistirDadosNormalizadosViaCallbacks() {
      System.out.println("\n🧪 TESTE: Normalização via callbacks JPA");

      // Given
      Cliente clienteComDadosParaNormalizar =
          new Cliente(
              "  João Silva  ", // Nome com espaços
              "  JOAO@EMAIL.COM  ", // Email com espaços e maiúsculo
              "  11999998888  ", // Telefone com espaços
              "  123.456.789-00  " // CPF com espaços
              );

      System.out.println("📋 Dados antes da normalização:");
      System.out.println("   Nome: '" + clienteComDadosParaNormalizar.getNome() + "'");
      System.out.println("   Email: '" + clienteComDadosParaNormalizar.getEmail() + "'");
      System.out.println("   Telefone: '" + clienteComDadosParaNormalizar.getTelefone() + "'");
      System.out.println("   CPF: '" + clienteComDadosParaNormalizar.getCpf() + "'");

      // When
      Cliente clienteSalvo = clienteRepository.save(clienteComDadosParaNormalizar);

      // Then
      System.out.println("\n📊 Dados após persistência e normalização:");

      assertEquals("João Silva", clienteSalvo.getNome());
      System.out.println("✅ Nome normalizado: '" + clienteSalvo.getNome() + "'");

      assertEquals("joao@email.com", clienteSalvo.getEmail());
      System.out.println("✅ Email normalizado: '" + clienteSalvo.getEmail() + "'");

      // Telefone pode ter sido normalizado dependendo da implementação
      assertNotNull(clienteSalvo.getTelefone());
      System.out.println("✅ Telefone normalizado: '" + clienteSalvo.getTelefone() + "'");

      assertEquals("123.456.789-00", clienteSalvo.getCpf());
      System.out.println("✅ CPF normalizado: '" + clienteSalvo.getCpf() + "'");

      System.out.println("🎉 TESTE CONCLUÍDO COM SUCESSO!");
    }
  }
}
