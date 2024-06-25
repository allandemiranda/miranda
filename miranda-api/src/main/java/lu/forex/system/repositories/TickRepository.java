package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Tick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TickRepository extends JpaRepository<Tick, UUID>, JpaSpecificationExecutor<Tick> {

  @NonNull
  Optional<@NotNull Tick> getFirstBySymbol_IdOrderByTimestampDesc(@NonNull UUID id);

  @NonNull
  List<@NotNull Tick> findBySymbol_CurrencyPair_NameOrderByTimestampAsc(@NonNull String symbolName);

  @Query("select t from Tick t where t.symbol.currencyPair.name = ?1 order by t.timestamp DESC LIMIT 2")
  List<Tick> findBySymbolNameOrderByTimestampDescLimitTwo(@NonNull String symbolName);

  Collection<Tick> findBySymbol_CurrencyPair_Name(@NonNull String name);
}