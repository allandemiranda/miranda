package lu.forex.system.repositories;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Scope;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, UUID>, JpaSpecificationExecutor<Scope> {

  @Query("select s from Scope s where s.symbol.currencyPair.name = ?1")
  Collection<Scope> findBySymbolName(@NonNull String symbolName);

  @Query("select s from Scope s where s.symbol.currencyPair.name = ?1 and s.timeFrame = ?2")
  @NonNull
  Optional<Scope> getBySymbolNameAndTimeFrame(@NonNull String symbolName, @NonNull TimeFrame timeFrame);

}