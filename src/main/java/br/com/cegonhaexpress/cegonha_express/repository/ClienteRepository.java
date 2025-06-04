package br.com.cegonhaexpress.cegonha_express.repository;

import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para operações CRUD da entidade Cliente.
 *
 * <p>Implementa consultas otimizadas por CPF e email (índices únicos), busca por nome e validações
 * de existência.
 *
 * <p><strong>Relacionamentos:</strong> Cliente (1) → (N) Endereco, Encomenda
 *
 * @author Gabriel Coelho Soares
 * @see Cliente
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

  /**
   * Busca cliente pelo CPF único.
   *
   * @param cpf CPF no formato 000.000.000-00 ou 00000000000
   * @return Cliente encontrado ou Optional.empty()
   */
  Optional<Cliente> findByCpf(String cpf);

  /**
   * Busca cliente pelo email único (case-insensitive).
   *
   * @param email Endereço de email do cliente
   * @return Cliente encontrado ou Optional.empty()
   */
  Optional<Cliente> findByEmail(String email);

  /**
   * Busca clientes por nome parcial (case-insensitive).
   *
   * @param nome Termo de busca no nome do cliente
   * @return Lista de clientes que contém o termo no nome
   */
  List<Cliente> findByNomeContainingIgnoreCase(String nome);

  /**
   * Verifica existência de cliente pelo CPF.
   *
   * @param cpf CPF a ser verificado
   * @return true se CPF já existe, false caso contrário
   */
  boolean existsByCpf(String cpf);
}
