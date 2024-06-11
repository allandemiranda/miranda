package lu.forex.system.repositories;

import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CandlestickRepository extends JpaRepository<Candlestick, UUID>, JpaSpecificationExecutor<Candlestick> {

  @NonNull
  Optional<Candlestick> getFirstByScopeAndTimestamp(@NonNull Scope scope, @NonNull LocalDateTime timestamp);

  @Query("select c from Candlestick c where c.scope = ?1 order by c.timestamp DESC LIMIT ?2")
  List<Candlestick> findByScopeOrderByTimestampDesc(@NonNull Scope scope, @Positive int limit);

}