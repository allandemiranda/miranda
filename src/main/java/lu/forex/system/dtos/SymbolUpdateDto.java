package lu.forex.system.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import lu.forex.system.entities.Symbol;

/**
 * DTO for {@link Symbol}
 */
public record SymbolUpdateDto(@Min(1) @Positive int digits, @PositiveOrZero double swapLong, @PositiveOrZero double swapShort) implements
    Serializable {

  @Serial
  private static final long serialVersionUID = 3484491098272107660L;
}