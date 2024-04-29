package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link lu.forex.system.entities.Candlestick}
 */
public record CandlestickCreateDto(@NotNull LocalDateTime timestamp, @Positive double price) implements Serializable {

  @Serial
  private static final long serialVersionUID = -1819044480692248248L;
}