package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Tick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TickRepository extends JpaRepository<Tick, UUID>, JpaSpecificationExecutor<Tick> {

  @NonNull
  Optional<@NotNull Tick> getFirstBySymbol_IdOrderByTimestampDesc(@NonNull UUID id);

  @NonNull
  List<@NotNull Tick> findBySymbol_CurrencyPair_NameOrderByTimestampAsc(@NonNull String symbolName);

  @Query("select t from Tick t where t.symbol.currencyPair.name = ?1 order by t.timestamp DESC LIMIT 2")
  List<Tick> findBySymbolNameOrderByTimestampDescLimitTwo(@NonNull String symbolName);

  Collection<Tick> findBySymbol_CurrencyPair_Name(@NonNull String name);

  @Query(value = "WITH RankedTicks AS (" +
                 "    SELECT ASK, BID, SPREAD, TIMESTAMP, ID, SYMBOL_ID, " +
                 "           ROW_NUMBER() OVER (PARTITION BY SYMBOL_ID ORDER BY TIMESTAMP) AS rn " +
                 "    FROM PUBLIC.TICK " +
                 "    WHERE SYMBOL_ID = :symbolId AND TIMESTAMP >= :timestamp" +
                 "), FirstTicks AS (" +
                 "    SELECT ASK, BID, SPREAD, TIMESTAMP, ID, SYMBOL_ID " +
                 "    FROM RankedTicks " +
                 "    WHERE rn = 1 " +
                 "), SecondTicks AS (" +
                 "    SELECT ASK, BID, SPREAD, TIMESTAMP, ID, SYMBOL_ID " +
                 "    FROM RankedTicks " +
                 "    WHERE rn = 2 " +
                 ") " +
                 "SELECT f.ASK, f.BID, f.SPREAD, f.TIMESTAMP, f.ID, f.SYMBOL_ID " +
                 "FROM FirstTicks f " +
                 "UNION ALL " +
                 "SELECT s.ASK, s.BID, s.SPREAD, s.TIMESTAMP, s.ID, s.SYMBOL_ID " +
                 "FROM SecondTicks s " +
                 "WHERE NOT EXISTS (" +
                 "    SELECT 1 " +
                 "    FROM FirstTicks " +
                 "    WHERE FirstTicks.SYMBOL_ID = s.SYMBOL_ID " +
                 "    AND FirstTicks.TIMESTAMP = s.TIMESTAMP " +
                 ") " +
                 "ORDER BY TIMESTAMP " +
                 "LIMIT 1",
      nativeQuery = true)
  Optional<Tick> findFirstAndNextTick(@Param("symbolId") UUID symbolId, @Param("timestamp") LocalDateTime timestamp);
}