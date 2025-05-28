package br.com.cegonhaexpress.cegonha_express.repository;

import br.com.cegonhaexpress.cegonha_express.model.entity.Cliente;
import br.com.cegonhaexpress.cegonha_express.model.entity.Endereco;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

  List<Endereco> findByCliente(Cliente cliente);

  List<Endereco> findByClienteId(Long clienteId);

  List<Endereco> findByCep(String cep);
}
