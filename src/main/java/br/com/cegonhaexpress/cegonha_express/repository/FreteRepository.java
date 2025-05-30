package br.com.cegonhaexpress.cegonha_express.repository;

import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import br.com.cegonhaexpress.cegonha_express.model.entity.Frete;
import br.com.cegonhaexpress.cegonha_express.model.enums.TipoEntrega;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository para operações CRUD da entidade Frete.
 *
 * <p>Implementa consultas especializadas para cálculos de frete, análise de custos e relatórios
 * financeiros. Mantém relacionamento 1:1 único com Encomenda garantido por constraint de unicidade
 * no banco.
 *
 * <p><strong>Relacionamentos:</strong> Frete (1) ↔ (1) Encomenda (unique constraint)
 *
 * @author Gabriel Coelho Soares
 * @see Frete
 */
@Repository
public interface FreteRepository extends JpaRepository<Frete, Long> {

  /**
   * Busca frete pela encomenda associada (relacionamento 1:1).
   *
   * @param encomenda Encomenda vinculada ao frete
   * @return Frete da encomenda ou Optional.empty()
   */
  Optional<Frete> findByEncomenda(Encomenda encomenda);

  /**
   * Busca frete pelo ID da encomenda.
   *
   * @param encomendaId ID da encomenda vinculada
   * @return Frete da encomenda ou Optional.empty()
   */
  Optional<Frete> findByEncomendaId(Long encomendaId);

  /**
   * Busca fretes por tipo de entrega.
   *
   * @param tipoEntrega Modalidade de entrega (EXPRESSA, PADRAO, ECONOMICA)
   * @return Lista de fretes do tipo especificado
   */
  List<Frete> findByTipoEntrega(TipoEntrega tipoEntrega);

  /**
   * Busca fretes por faixa de valores.
   *
   * @param valorMinimo Valor mínimo do frete (inclusive)
   * @param valorMaximo Valor máximo do frete (inclusive)
   * @return Lista de fretes na faixa de valores especificada
   */
  List<Frete> findByValorBetween(BigDecimal valorMinimo, BigDecimal valorMaximo);

  /**
   * Busca fretes por faixa de distância.
   *
   * @param distanciaMinima Distância mínima em km (inclusive)
   * @param distanciaMaxima Distância máxima em km (inclusive)
   * @return Lista de fretes na faixa de distância especificada
   */
  List<Frete> findByDistanciaKmBetween(BigDecimal distanciaMinima, BigDecimal distanciaMaxima);

  /**
   * Busca fretes calculados em período específico.
   *
   * @param inicio Data/hora inicial do período
   * @param fim Data/hora final do período
   * @return Lista de fretes calculados no período
   */
  List<Frete> findByDataCalculoBetween(LocalDateTime inicio, LocalDateTime fim);

  /**
   * Calcula valor médio de frete por tipo de entrega.
   *
   * @param tipoEntrega Tipo de entrega para cálculo da média
   * @return Valor médio dos fretes do tipo especificado
   */
  @Query("SELECT AVG(f.valor) FROM Frete f WHERE f.tipoEntrega = :tipoEntrega")
  BigDecimal findAverageValueByTipoEntrega(@Param("tipoEntrega") TipoEntrega tipoEntrega);

  /**
   * Calcula distância média por tipo de entrega.
   *
   * @param tipoEntrega Tipo de entrega para cálculo da média
   * @return Distância média em km dos fretes do tipo especificado
   */
  @Query("SELECT AVG(f.distanciaKm) FROM Frete f WHERE f.tipoEntrega = :tipoEntrega")
  BigDecimal findAverageDistanceByTipoEntrega(@Param("tipoEntrega") TipoEntrega tipoEntrega);

  /**
   * Busca fretes mais caros (para análise de outliers).
   *
   * @param limite Número máximo de fretes a retornar
   * @return Lista dos fretes mais caros ordenados por valor decrescente
   */
  @Query("SELECT f FROM Frete f ORDER BY f.valor DESC")
  List<Frete> findMostExpensiveFretes(@Param("limite") int limite);

  /**
   * Conta fretes por tipo de entrega.
   *
   * @param tipoEntrega Tipo de entrega a ser contabilizado
   * @return Número de fretes do tipo especificado
   */
  long countByTipoEntrega(TipoEntrega tipoEntrega);

  /**
   * Calcula receita total por tipo de entrega.
   *
   * @param tipoEntrega Tipo de entrega para cálculo da receita
   * @return Soma total dos valores de frete do tipo especificado
   */
  @Query("SELECT SUM(f.valor) FROM Frete f WHERE f.tipoEntrega = :tipoEntrega")
  BigDecimal calculateRevenueByTipoEntrega(@Param("tipoEntrega") TipoEntrega tipoEntrega);
}
