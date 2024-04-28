package lu.forex.system.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link lu.forex.system.entities.Symbol}
 */
public record SymbolUpdateDto(@Min(1) @Positive int digits, double swapLong, double swapShort) implements Serializable {

  @Serial
  private static final long serialVersionUID = 2904813128819566105L;
}