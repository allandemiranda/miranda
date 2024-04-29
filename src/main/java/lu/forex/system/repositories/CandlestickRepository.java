package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CandlestickRepository extends JpaRepository<Candlestick, UUID>, JpaSpecificationExecutor<Candlestick> {

  @Transactional
  @Modifying
  @Query("update Candlestick c set c.high = ?1, c.low = ?2, c.close = ?3 where c.symbol = ?4 and c.timeFrame = ?5")
  void update(@NonNull double high, @NonNull double low, @NonNull double close, @NonNull Symbol symbol, @NonNull TimeFrame timeFrame);

  @NonNull List<@NotNull Candlestick> findBySymbol_NameAndTimeFrameOrderByTimestampAsc(@NonNull String name, @NonNull TimeFrame timeFrame);


}