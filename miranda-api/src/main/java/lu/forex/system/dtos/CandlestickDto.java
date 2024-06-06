package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link lu.forex.system.entities.Candlestick}
 */
public record CandlestickDto(@NotNull UUID id, @NotNull ScopeDto scope, @NotNull @PastOrPresent LocalDateTime timestamp,
                             @NotNull CandlestickBodyDto body, @NotNull Set<MovingAverageDto> movingAverages,
                             @NotNull Set<TechnicalIndicatorDto> technicalIndicators) implements Serializable {

  @Serial
  private static final long serialVersionUID = 3488813708665947086L;
}