package lu.forex.system.repositories;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Scope;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, UUID>, JpaSpecificationExecutor<Scope> {

  Collection<Scope> findBySymbol(@NonNull Symbol symbol);

  Optional<Scope> getBySymbolAndTimeFrame(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame);

}