package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Tick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TickRepository extends JpaRepository<Tick, UUID>, JpaSpecificationExecutor<Tick> {

  @NonNull
  Optional<@NotNull Tick> getFirstBySymbol_IdOrderByTimestampDesc(@NonNull UUID id);

  @NonNull
  List<@NotNull Tick> findBySymbol_CurrencyPair_NameOrderByTimestampAsc(@NonNull String symbolName);
}