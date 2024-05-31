package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TickRepository extends JpaRepository<Tick, UUID>, JpaSpecificationExecutor<Tick> {

  @NonNull
  Optional<@NotNull Tick> getFirstBySymbolOrderByTimestampDesc(@NonNull Symbol symbol);

  @NonNull
  Stream<@NotNull Tick> streamBySymbolOrderByTimestampAsc(@NonNull Symbol symbol);

  boolean existsBySymbolAndTimestamp(@NonNull Symbol symbol, @NonNull LocalDateTime timestamp);

}