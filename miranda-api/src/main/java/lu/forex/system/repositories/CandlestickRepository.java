package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.models.Ac;
import lu.forex.system.models.Adx;
import lu.forex.system.models.Macd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CandlestickRepository extends JpaRepository<Candlestick, UUID>, JpaSpecificationExecutor<Candlestick> {

  @NotNull
  Stream<@NotNull Candlestick> streamBySymbolAndTimeFrameOrderByTimestampAsc(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame);

  @NonNull
  Optional<@NotNull Candlestick> findFirstBySymbolAndTimeFrameOrderByTimestampDesc(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame);

  @NonNull
  Stream<@NotNull Candlestick> streamBySymbolAndTimeFrameOrderByTimestampDesc(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame);

}