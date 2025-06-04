package br.com.cegonhaexpress.cegonha_express.dto.request;

import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import br.com.cegonhaexpress.cegonha_express.model.enums.TipoEntrega;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de requisição para criação de encomendas.
 *
 * <p>Utilizado para receber dados do cliente via API REST ou formulários web, contendo informações
 * necessárias para processar um pedido de entrega de bebê reborn. Inclui validações robustas nos
 * campos obrigatórios e dimensões permitidas.
 *
 * <p><strong>Fluxo de Uso:</strong>
 *
 * <ol>
 *   <li>Cliente/Frontend envia dados via POST /api/encomendas
 *   <li>Bean Validation executa validações automáticas
 *   <li>Controller chama Service com DTO validado
 *   <li>Service usa toEntity() e injeta cliente/origem
 * </ol>
 *
 * @author Gabriel Coelho Soares
 * @see Encomenda
 * @see EnderecoDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncomendaRequestDTO {

  /**
   * Endereço de destino da entrega. Será validado automaticamente pela anotação @Valid, executando
   * todas as validações definidas em EnderecoDTO.
   */
  @NotNull(message = "enderecoDestino é obrigatório")
  @Valid
  private EnderecoDTO enderecoDestino;

  /**
   * Modalidade de entrega selecionada pelo cliente. Determina prazo e valor do frete
   * (EXPRESSA=1dia, PADRAO=3dias, ECONOMICA=7dias).
   */
  @NotNull(message = "tipoEntrega é obrigatório")
  private TipoEntrega tipoEntrega;

  /**
   * Descrição detalhada do bebê reborn para identificação. Ex: "Bebê Alice, 50cm, cabelo loiro
   * cacheado, olhos azuis"
   */
  @NotBlank(message = "descricaoBebe é obrigatório")
  @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
  private String descricaoBebe;

  /**
   * Peso do bebê reborn em quilogramas. Influencia diretamente no cálculo do frete. Opcional para
   * cálculo estimativo.
   */
  @DecimalMin(value = "0.1", message = "Peso deve ser maior que 0.1kg")
  @DecimalMax(value = "15.0", message = "Peso deve ser menor que 15kg")
  private BigDecimal pesoKg;

  /**
   * Altura do bebê reborn em centímetros. Usada para validação de limites de transporte. Campo
   * opcional.
   */
  @DecimalMin(value = "20.0", message = "Altura deve ser maior que 20cm")
  @DecimalMax(value = "100.0", message = "Altura deve ser menor que 100cm")
  private BigDecimal alturaCm;

  /**
   * Valor declarado do bebê para fins de seguro. Impacta no cálculo de taxas adicionais de
   * segurança. Campo opcional.
   */
  @DecimalMin(value = "0.0", message = "Valor declarado não pode ser negativo")
  private BigDecimal valorDeclarado;

  /**
   * Converte DTO para entidade Encomenda (conversão parcial).
   *
   * <p><strong>IMPORTANTE:</strong> Cliente e endereço origem devem ser injetados no Service layer
   * após a conversão, pois não estão disponíveis no contexto da requisição.
   *
   * <p><strong>Responsabilidade do Service:</strong>
   *
   * <ul>
   *   <li>Identificar cliente autenticado (sessão/token)
   *   <li>Definir endereço origem (configuração/cliente)
   *   <li>Calcular frete usando GoogleMapsDistanceService
   * </ul>
   *
   * @return Encomenda com dados do DTO, cliente e origem = null
   */
  public Encomenda toEntity() {
    return new Encomenda(
        null, // Cliente será injetado no Service
        null, // Endereço origem será injetado no Service
        enderecoDestino.toEntity(),
        tipoEntrega,
        descricaoBebe,
        pesoKg,
        alturaCm,
        valorDeclarado);
  }
}
