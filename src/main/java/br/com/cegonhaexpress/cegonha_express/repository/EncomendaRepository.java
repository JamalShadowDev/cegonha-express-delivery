package br.com.cegonhaexpress.cegonha_express.repository;

import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.enums.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository para operações CRUD da entidade Encomenda.
 *
 * <p>Implementa consultas por código de rastreamento, status, tipo de entrega e relacionamentos.
 * Suporta consultas por localização e cliente para relatórios e dashboard administrativo.
 *
 * <p><strong>Relacionamentos:</strong> Encomenda (N) → (1) Cliente, (1) → (1) Frete
 *
 * @author Gabriel Coelho Soares
 * @see Encomenda
 */
@Repository
public interface EncomendaRepository extends JpaRepository<Encomenda, Long> {
  /**
   * Busca encomenda pelo código único de rastreamento.
   *
   * @param codigo Código único da encomenda (formato: CE + timestamp + sufixo)
   * @return Encomenda encontrada ou Optional.empty()
   */
  Optional<Encomenda> findByCodigo(String codigo);

  /**
   * Busca encomendas excluindo status específicos.
   *
   * @param status Lista de status a serem excluídos da consulta
   * @return Lista de encomendas que não possuem os status especificados
   */
  List<Encomenda> findByStatusNotIn(List<StatusEncomenda> status);

  /**
   * Busca encomendas por status único.
   *
   * @param status Status da encomenda (PENDENTE, CONFIRMADA, EM_TRANSITO, etc.)
   * @return Lista de encomendas com o status especificado
   */
  List<Encomenda> findByStatus(StatusEncomenda status);

  /**
   * Busca encomendas por tipo de entrega.
   *
   * @param tipoEntrega Modalidade de entrega (EXPRESSA, PADRAO, ECONOMICA)
   * @return Lista de encomendas do tipo especificado
   */
  List<Encomenda> findByTipoEntrega(TipoEntrega tipoEntrega);

  /**
   * Busca encomendas por CEP de destino.
   *
   * @param cep CEP do endereço de destino
   * @return Lista de encomendas com destino no CEP especificado
   */
  List<Encomenda> findByEnderecoDestinoCep(String cep);

  /**
   * Busca encomendas por endereço de destino específico.
   *
   * @param enderecoDestino Endereço completo de destino
   * @return Lista de encomendas para o endereço especificado
   */
  List<Encomenda> findByEnderecoDestino(Endereco enderecoDestino);

  /**
   * Busca encomendas do cliente específico.
   *
   * @param cliente Cliente proprietário das encomendas
   * @return Lista de encomendas do cliente
   */
  List<Encomenda> findByCliente(Cliente cliente);

  /**
   * Busca encomendas pelo ID do cliente.
   *
   * @param clienteId ID do cliente proprietário
   * @return Lista de encomendas do cliente
   */
  List<Encomenda> findByClienteId(Long clienteId);

  /**
   * Busca encomendas em faixa de datas de pedido.
   *
   * @param inicio Data/hora inicial do período
   * @param fim Data/hora final do período
   * @return Lista de encomendas criadas no período especificado
   */
  List<Encomenda> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

  /**
   * Busca encomendas ativas (não canceladas) por status.
   *
   * @return Lista de encomendas ativas (excluindo CANCELADA)
   */
  @Query("SELECT e FROM Encomenda e WHERE e.status != 'CANCELADA'")
  List<Encomenda> findActiveEncomendas();

  /**
   * Busca encomendas atrasadas (data estimada passou).
   *
   * @return Lista de encomendas com entrega em atraso
   */
  @Query(
      "SELECT e FROM Encomenda e WHERE e.dataEstimadaEntrega < CURRENT_DATE "
          + "AND e.status NOT IN ('ENTREGUE', 'CANCELADA')")
  List<Encomenda> findOverdueEncomendas();

  /**
   * Conta encomendas por status para dashboard.
   *
   * @param status Status a ser contabilizado
   * @return Número de encomendas no status especificado
   */
  long countByStatus(StatusEncomenda status);

  /**
   * Conta encomendas por tipo de entrega.
   *
   * @param tipoEntrega Tipo de entrega a ser contabilizado
   * @return Número de encomendas do tipo especificado
   */
  long countByTipoEntrega(TipoEntrega tipoEntrega);
}
