package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lu.forex.system.enums.TimeFrame;

/**
 * DTO for {@link lu.forex.system.entities.Candlestick}
 */
public record UpdateCandlestickDto(@NotNull @PastOrPresent LocalDateTime tickTimestamp, @NotNull TimeFrame timeFrame, @PositiveOrZero double price) implements Serializable {

  @Serial
  private static final long serialVersionUID = 530292200347528864L;
}