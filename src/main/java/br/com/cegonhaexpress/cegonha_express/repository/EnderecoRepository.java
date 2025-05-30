package br.com.cegonhaexpress.cegonha_express.repository;

import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository para operações CRUD da entidade Endereco.
 *
 * <p>Implementa consultas por CEP, localização (cidade/UF) e relacionamentos com Cliente. Suporta
 * integração com ViaCEP para validação automática.
 *
 * <p><strong>Relacionamentos:</strong> Endereco (N) → (1) Cliente
 *
 * @author Gabriel Coelho Soares
 * @see Endereco
 */
@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

  /**
   * Busca endereços pelo cliente proprietário.
   *
   * @param cliente Cliente proprietário dos endereços
   * @return Lista de endereços do cliente
   */
  List<Endereco> findByCliente(Cliente cliente);

  /**
   * Busca endereços pelo ID do cliente.
   *
   * @param clienteId ID do cliente proprietário
   * @return Lista de endereços do cliente
   */
  List<Endereco> findByClienteId(Long clienteId);

  /**
   * Busca endereços pelo CEP.
   *
   * @param cep CEP no formato 00000-000 ou 00000000
   * @return Lista de endereços com o CEP especificado
   */
  List<Endereco> findByCep(String cep);

  /**
   * Busca endereços por Unidade Federativa.
   *
   * @param uf Estado brasileiro (enum UF)
   * @return Lista de endereços do estado especificado
   */
  List<Endereco> findByUf(UF uf);

  /**
   * Busca endereços por cidade (case-insensitive).
   *
   * @param cidade Nome da cidade
   * @return Lista de endereços da cidade especificada
   */
  List<Endereco> findByCidadeIgnoreCase(String cidade);

  /**
   * Busca endereços por bairro (case-insensitive).
   *
   * @param bairro Nome do bairro
   * @return Lista de endereços do bairro especificado
   */
  List<Endereco> findByBairroIgnoreCase(String bairro);

  /**
   * Busca endereços completos (todos os campos obrigatórios preenchidos).
   *
   * @return Lista de endereços com dados completos
   */
  @Query(
      "SELECT e FROM Endereco e WHERE e.cep IS NOT NULL AND e.logradouro IS NOT NULL AND e.numero"
          + " IS NOT NULL AND e.bairro IS NOT NULL AND e.cidade IS NOT NULL AND e.uf IS NOT NULL")
  List<Endereco> findCompleteAddresses();

  /**
   * Conta endereços por UF para estatísticas.
   *
   * @param uf Estado brasileiro
   * @return Número de endereços cadastrados no estado
   */
  long countByUf(UF uf);
}
