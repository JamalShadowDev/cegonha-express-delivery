package br.com.cegonhaexpress.cegonha_express.repository;

import br.com.cegonhaexpress.cegonha_express.model.entity.Encomenda;
import br.com.cegonhaexpress.cegonha_express.model.entity.Frete;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreteRepository extends JpaRepository<Frete, Long> {

  Optional<Frete> findByEncomenda(Encomenda encomenda);

  Optional<Frete> findByEncomendaId(Long encomendaId);
}
