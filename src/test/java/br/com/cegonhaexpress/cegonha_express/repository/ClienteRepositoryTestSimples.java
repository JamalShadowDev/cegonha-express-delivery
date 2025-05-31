package br.com.cegonhaexpress.cegonha_express.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@DisplayName("ClienteRepository - Testes Básicos")
class ClienteRepositoryTestSimples {

  @Autowired private TestEntityManager entityManager;

  @Autowired private ClienteRepository clienteRepository;

  @Test
  @DisplayName("Deve salvar e buscar cliente por ID")
  void deveSalvarEBuscarClientePorId() {
    // Given
    Cliente cliente = new Cliente("João Silva", "joao@email.com", "123.456.789-00");

    // When
    Cliente clienteSalvo = clienteRepository.save(cliente);
    Optional<Cliente> clienteEncontrado = clienteRepository.findById(clienteSalvo.getId());

    // Then
    assertTrue(clienteEncontrado.isPresent());
    assertEquals("João Silva", clienteEncontrado.get().getNome());
    assertEquals("joao@email.com", clienteEncontrado.get().getEmail());
    assertEquals("123.456.789-00", clienteEncontrado.get().getCpf());
  }

  @Test
  @DisplayName("Deve buscar cliente por CPF")
  void deveBuscarClientePorCpf() {
    // Given
    Cliente cliente = new Cliente("Maria Santos", "maria@email.com", "987.654.321-00");
    clienteRepository.save(cliente);

    // When
    Optional<Cliente> encontrado = clienteRepository.findByCpf("987.654.321-00");

    // Then
    assertTrue(encontrado.isPresent());
    assertEquals("Maria Santos", encontrado.get().getNome());
  }

  @Test
  @DisplayName("Deve verificar se CPF existe")
  void deveVerificarSeCpfExiste() {
    // Given
    Cliente cliente = new Cliente("Pedro Costa", "pedro@email.com", "111.222.333-44");
    clienteRepository.save(cliente);

    // When
    boolean existe = clienteRepository.existsByCpf("111.222.333-44");
    boolean naoExiste = clienteRepository.existsByCpf("999.888.777-66");

    // Then
    assertTrue(existe);
    assertFalse(naoExiste);
  }
}
