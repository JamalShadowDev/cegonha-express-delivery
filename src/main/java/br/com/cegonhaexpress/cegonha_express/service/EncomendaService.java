package br.com.cegonhaexpress.cegonha_express.service;

import br.com.cegonhaexpress.cegonha_express.dto.request.EncomendaRequestDTO;
import br.com.cegonhaexpress.cegonha_express.dto.response.EncomendaResponseDTO;
import br.com.cegonhaexpress.cegonha_express.dto.response.ViaCepResponseDto;
import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.entity.Frete;
import br.com.cegonhaexpress.cegonha_express.model.enums.StatusEncomenda;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import br.com.cegonhaexpress.cegonha_express.repository.ClienteRepository;
import br.com.cegonhaexpress.cegonha_express.repository.EncomendaRepository;
import br.com.cegonhaexpress.cegonha_express.repository.FreteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service
@Validated
public class EncomendaService {

  private final EncomendaRepository encomendaRepository;
  private final FreteRepository freteRepository;
  private final ClienteRepository clienteRepository;
  private final FreteService freteService;
  private final ViaCepService viaCepService;
  private static final Cliente clientePadrao =
      new Cliente("Jailson Mendes", "jailsonmmm@gmail.com", "11976543211", "123.123.128-09");

  @Transactional
  public EncomendaResponseDTO criaEncomenda(EncomendaRequestDTO dto) {
    ViaCepResponseDto buscaCep = viaCepService.buscarEnderecoPorCep("13801-005");
    Endereco enderecoOrigemPadrao;
    if (buscaCep != null) {
      enderecoOrigemPadrao =
          new Endereco(
              buscaCep.getCep(),
              buscaCep.getLogradouro(),
              "567",
              buscaCep.getBairro(),
              buscaCep.getLocalidade(),
              UF.valueOf(buscaCep.getUf()));
    } else {
      enderecoOrigemPadrao =
          new Endereco(
              "13801-005",
              "Rua Ariovaldo Silveira Franco",
              "567",
              "Jardim 31 de Março",
              "Mogi Mirim",
              UF.valueOf("SP"));
    }

    Encomenda encomenda = dto.toEntity();
    encomenda.setCliente(clientePadrao);
    encomenda.setEnderecoOrigem(enderecoOrigemPadrao);
    encomenda = encomendaRepository.save(encomenda); // cria ID
    Frete frete = freteService.calcularFreteComDistanciaReal(encomenda);
    encomenda.setFrete(frete);
    encomenda = encomendaRepository.save(encomenda); // atualiza garantindo o frete com ID correto
    return EncomendaResponseDTO.fromEntity(encomenda);
  }

  @Transactional
  public StatusEncomenda avancarStatus(Long id) {
    Encomenda encomenda =
        encomendaRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Não existe uma Encomenda com este ID"));
    if (encomenda.isAtiva()) {
      switch (encomenda.getStatus()) {
        case PENDENTE -> encomenda.confirmar();
        case CONFIRMADA -> encomenda.iniciarTransito();
        case EM_TRANSITO -> encomenda.finalizarEntrega();
      }
      ;
    }

    return encomenda.getStatus();
  }

  @Transactional(readOnly = true)
  public EncomendaResponseDTO buscarPorCodigo(@Pattern(regexp = "^CE\\d+$") String codigo) {
    Encomenda encomenda =
        encomendaRepository
            .findByCodigo(codigo)
            .orElseThrow(
                () -> new EntityNotFoundException("Não existe uma encomenda com este Código"));
    return EncomendaResponseDTO.fromEntity(encomenda);
  }
}
