package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lu.forex.system.enums.SignalIndicator;

/**
 * DTO for {@link lu.forex.system.entities.Candlestick}
 */
public record CandlestickDto(@NotNull UUID id, @NotNull ScopeDto scope, @NotNull LocalDateTime timestamp, @NotNull CandlestickBodyDto body,
                             @NotNull Set<MovingAverageDto> movingAverages, @NotNull Set<TechnicalIndicatorDto> technicalIndicators,
                             @NotNull SignalIndicator signalIndicator) implements Serializable {

  @Serial
  private static final long serialVersionUID = 3488813708665947086L;
}