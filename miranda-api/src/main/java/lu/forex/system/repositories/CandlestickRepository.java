package lu.forex.system.repositories;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.Scope;
import lu.forex.system.entities.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CandlestickRepository extends JpaRepository<Candlestick, UUID>, JpaSpecificationExecutor<Candlestick> {

  @NonNull
  Optional<Candlestick> getFirstByScopeAndTimestamp(@NonNull Scope scope, @NonNull LocalDateTime timestamp);

  Collection<Candlestick> findByScope(@NonNull Scope scope);

}