package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.UUID;
import lu.forex.system.entities.Tick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TickRepository extends JpaRepository<Tick, UUID>, JpaSpecificationExecutor<Tick> {

  @NonNull
  Collection<@NotNull Tick> findBySymbol_NameOrderByTimestampAsc(@NonNull String name);

}