package lu.forex.system.repositories;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.AcIndicator;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface AcIndicatorRepository extends JpaRepository<AcIndicator, UUID>, JpaSpecificationExecutor<AcIndicator> {

  @Nonnull
  Optional<AcIndicator> getFirstByCandlestick_SymbolAndCandlestick_TimeFrameOrderByCandlestick_TimestampDesc(@NonNull Symbol symbol,
      @NonNull TimeFrame timeFrame);
}