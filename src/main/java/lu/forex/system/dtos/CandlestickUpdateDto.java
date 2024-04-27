package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link lu.forex.system.entities.Candlestick}
 */
public record CandlestickUpdateDto(@NotNull LocalDateTime timestamp, @Positive double high, @Positive double low, @Positive double close) implements
    Serializable {

  @Serial
  private static final long serialVersionUID = -1637002516987829739L;
}