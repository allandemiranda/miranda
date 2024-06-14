package lu.forex.system.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TickRepository extends JpaRepository<Tick, UUID>, JpaSpecificationExecutor<Tick> {

  @NonNull
  Optional<Tick> getFirstBySymbolOrderByTimestampDesc(@NonNull Symbol symbol);

  Collection<Tick> findBySymbol(@NonNull Symbol symbol);

  @Query("select t from Tick t where t.symbol = ?1 order by t.timestamp DESC LIMIT 2")
  List<Tick> findBySymbolOrderByTimestampDescLimitTwo(@NonNull Symbol symbol);

}