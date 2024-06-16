package lu.forex.system.dtos;

import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link lu.forex.system.entities.CandlestickBody}
 */
public record CandlestickBodyDto(@PositiveOrZero double high, @PositiveOrZero double low, @PositiveOrZero double open,
                                 @PositiveOrZero double close) implements Serializable {

  @Serial
  private static final long serialVersionUID = -8733521342996193762L;

}