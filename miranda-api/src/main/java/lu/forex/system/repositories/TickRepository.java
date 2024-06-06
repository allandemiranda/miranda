package lu.forex.system.repositories;

import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TickRepository extends JpaRepository<Tick, UUID>, JpaSpecificationExecutor<Tick> {

  Optional<Tick> getFirstBySymbolOrderByTimestampDesc(@NonNull Symbol symbol);

}