package lu.forex.system.repositories;

import java.util.UUID;
import lu.forex.system.entities.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolRepository extends JpaRepository<Symbol, UUID> {

  Symbol findByName(String name);
}
