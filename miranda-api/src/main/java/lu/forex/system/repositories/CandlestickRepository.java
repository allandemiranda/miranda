package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CandlestickRepository extends JpaRepository<Candlestick, UUID>, JpaSpecificationExecutor<Candlestick> {

  @NotNull
  Stream<@NotNull Candlestick> streamBySymbolAndTimeFrameOrderByTimestampAsc(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame);

  @NonNull
  Optional<@NotNull Candlestick> findFirstBySymbolAndTimeFrameOrderByTimestampDesc(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame);


  @Query("select c from Candlestick c where c.symbol = ?1 and c.timeFrame = ?2 order by c.timestamp DESC LIMIT ?3")
  Stream<Candlestick> streamBySymbolAndTimeFrameOrderByTimestampDesc(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame, int limit);

}