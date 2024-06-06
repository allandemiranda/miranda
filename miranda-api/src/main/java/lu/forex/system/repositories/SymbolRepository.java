package lu.forex.system.repositories;

import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolRepository extends JpaRepository<Symbol, UUID>, JpaSpecificationExecutor<Symbol> {

  @NonNull
  Optional<Symbol> getFirstByCurrencyPair_Name(@NonNull String name);

}