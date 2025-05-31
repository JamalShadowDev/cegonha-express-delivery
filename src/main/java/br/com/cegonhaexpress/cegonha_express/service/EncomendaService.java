package br.com.cegonhaexpress.cegonha_express.service;

import br.com.cegonhaexpress.cegonha_express.dto.request.EncomendaRequestDTO;
import br.com.cegonhaexpress.cegonha_express.dto.response.EncomendaResponseDTO;
import br.com.cegonhaexpress.cegonha_express.dto.response.ViaCepResponseDto;
import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.entity.Frete;
import br.com.cegonhaexpress.cegonha_express.model.enums.UF;
import br.com.cegonhaexpress.cegonha_express.repository.ClienteRepository;
import br.com.cegonhaexpress.cegonha_express.repository.EncomendaRepository;
import br.com.cegonhaexpress.cegonha_express.repository.FreteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EncomendaService {

  private final EncomendaRepository encomendaRepository;
  private final FreteRepository freteRepository;
  private final ClienteRepository clienteRepository;
  private final FreteService freteService;
  private final ViaCepService viaCepService;

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
    encomenda.setCliente(
        new Cliente("Jailson Mendes", "jailsonmmm@gmail.com", "11976543211", "123.123.128-09"));
    encomenda.setEnderecoOrigem(enderecoOrigemPadrao);
    encomenda = encomendaRepository.save(encomenda); // cria ID
    Frete frete = freteService.calcularFreteComDistanciaReal(encomenda);
    encomenda.setFrete(frete);
    encomenda = encomendaRepository.save(encomenda); // atualiza garantindo o frete com ID correto
    return EncomendaResponseDTO.fromEntity(encomenda);
  }
}
