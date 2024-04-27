package lu.forex.system.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import lu.forex.system.entities.Symbol;

/**
 * DTO for {@link Symbol}
 */
public record SymbolDto(@Size(min = 6, max = 6) @NotBlank String name, @NotBlank String description,
                        @Size(min = 3, max = 3) @NotBlank String currencyBase, @Size(min = 3, max = 3) @NotBlank String currencyQuote,
                        @Min(1) @Positive int digits, double swapLong, double swapShort) implements Serializable {

  @Serial
  private static final long serialVersionUID = 8605014685376603308L;
}