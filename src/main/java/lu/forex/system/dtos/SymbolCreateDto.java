package lu.forex.system.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import lu.forex.system.annotations.SymbolCurrencyRepresentation;
import lu.forex.system.enums.Currency;

/**
 * DTO for {@link lu.forex.system.entities.Symbol}
 */
@SymbolCurrencyRepresentation
public record SymbolCreateDto(@NotNull @NotEmpty @NotBlank String name, @NotNull Currency currencyBase, @NotNull Currency currencyQuote,
                              @Min(1) @Positive int digits, double swapLong, double swapShort) implements Serializable {

  @Serial
  private static final long serialVersionUID = 8605014685376603308L;
}