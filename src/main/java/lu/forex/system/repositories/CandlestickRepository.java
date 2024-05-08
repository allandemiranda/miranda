package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CandlestickRepository extends JpaRepository<Candlestick, UUID>, JpaSpecificationExecutor<Candlestick> {

  @NonNull
  List<@NotNull Candlestick> findBySymbol_NameAndTimeFrameOrderByTimestampAsc(@NonNull String name, @NonNull TimeFrame timeFrame);

  @NonNull
  Optional<@NotNull Candlestick> findFirstBySymbolAndTimeFrameOrderByTimestampDesc(@NonNull Symbol symbol, @NonNull TimeFrame timeFrame);

}