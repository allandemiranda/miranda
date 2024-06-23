package lu.forex.system.repositories;

import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.enums.SignalIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CandlestickRepository extends JpaRepository<Candlestick, UUID>, JpaSpecificationExecutor<Candlestick> {

  @NonNull
  Optional<Candlestick> getFirstByScope_IdAndTimestamp(@NonNull UUID scopeId, @NonNull LocalDateTime timestamp);

  @Transactional(readOnly = true)
  @Query("select c from Candlestick c where c.scope.id = ?1 order by c.timestamp desc limit ?2")
  List<Candlestick> findByScope_IdOrderByTimestampDescWithLimit(@NonNull UUID scopeId, @Positive int limit);

  List<Candlestick> findByScope_IdOrderByTimestampDesc(@NonNull UUID id);

  Collection<Candlestick> findByScope_Id(@NonNull UUID id);

  Collection<Candlestick> findByScope_IdAndSignalIndicatorIn(@NonNull UUID id, @NonNull Collection<SignalIndicator> signalIndicators);

}