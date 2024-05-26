package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lu.forex.system.enums.TimeFrame;

/**
 * DTO for {@link lu.forex.system.entities.Candlestick}
 */
public record CandlestickIndicatorDto(UUID id, @NotNull SymbolResponseDto symbol, @NotNull TimeFrame timeFrame,
                                      @NotNull @PastOrPresent LocalDateTime timestamp, @Positive double high, @Positive double low,
                                      @Positive double open, @Positive double close, @NotNull AcIndicatorDto acIndicator, @NotNull AdxIndicatorDto adxIndicator) implements
    Serializable {

  @Serial
  private static final long serialVersionUID = -6277398754006411775L;
}