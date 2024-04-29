package lu.forex.system.dtos;

import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link lu.forex.system.entities.Candlestick}
 */
public record CandlestickUpdateDto(@Positive double high, @Positive double low, @Positive double close) implements Serializable {

  @Serial
  private static final long serialVersionUID = -1637002516987829739L;
}