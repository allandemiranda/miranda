package lu.forex.system.repositories;

import java.util.Optional;
import lu.forex.system.entities.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SymbolRepository extends JpaRepository<Symbol, String>, JpaSpecificationExecutor<Symbol> {

  @NonNull
  Optional<Symbol> findFirstByNameOrderByNameAsc(@NonNull String name);

  @Transactional
  long deleteByName(@NonNull String name);

}