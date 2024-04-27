package lu.forex.system.repositories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.UUID;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.enums.TimeFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CandlestickRepository extends JpaRepository<Candlestick, UUID>, JpaSpecificationExecutor<Candlestick> {

  @NotNull
  Collection<Candlestick> findAllBySymbolNameAndTimeFrameOrderByTimestampAsc(@NotNull @NotBlank String symbolName, @NotNull TimeFrame timeFrame);

  @NotNull
  Candlestick findOneBySymbolNameAndTimeFrameOrderByTimeFrameAsc(@NotNull @NotBlank String symbolName, @NotNull TimeFrame timeFrame);
}