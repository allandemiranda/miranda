package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
  Stream<Candlestick> streamByHead_SymbolAndHead_TimeFrameOrderByHead_TimestampAsc(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame);

  @NonNull
  Optional<@NotNull Candlestick> findFirstByHead_SymbolAndHead_TimeFrameOrderByHead_TimestampDesc(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame);

  @Query("select c from Candlestick c where c.head.symbol.name = ?1 and c.head.timeFrame = ?2 order by c.head.timestamp DESC LIMIT ?3")
  Stream<Candlestick> streamByHead_Symbol_NameAndHead_TimeFrameOrderByHead_TimestampDesc(@NonNull String name, @NonNull TimeFrame timeFrame,
      @Positive int limit);
}