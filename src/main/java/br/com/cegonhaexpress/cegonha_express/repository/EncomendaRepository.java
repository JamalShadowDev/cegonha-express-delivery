package br.com.cegonhaexpress.cegonha_express.repository;

import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import br.com.cegonhaexpress.cegonha_express.model.enums.*;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncomendaRepository extends JpaRepository<Encomenda, Long> {

  Optional<Encomenda> findByCodigo(String codigo);

  List<Encomenda> findByStatusNotIn(List<StatusEncomenda> status);

  List<Encomenda> findByStatus(StatusEncomenda status);

  List<Encomenda> findByTipoEntrega(TipoEntrega tipoEntrega);

  List<Encomenda> findByEnderecoDestinoCep(String cep);

  List<Encomenda> findByEnderecoDestino(Endereco enderecoDestino);

  List<Encomenda> findByCliente(Cliente cliente);

  List<Encomenda> findByClienteId(Long clienteId);
}
