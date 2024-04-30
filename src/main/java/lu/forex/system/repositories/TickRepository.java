package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
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
  Collection<@NotNull Tick> findBySymbol_NameOrderByTimestampAsc(@NonNull String name);

  @Query("select (count(t) > 0) from Tick t where t.symbol.name = ?1 and t.timestamp = ?2")
  boolean existsBySymbol_NameAndTimestamp(@NonNull String name, @NonNull LocalDateTime timestamp);

  Optional<Tick> findFirstBySymbol_NameOrderByTimestampDesc(@NonNull String name);

}