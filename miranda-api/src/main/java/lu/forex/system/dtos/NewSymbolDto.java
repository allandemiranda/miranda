package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import lu.forex.system.enums.Currency;

/**
 * DTO for {@link lu.forex.system.entities.Symbol}
 */
public record NewSymbolDto(@NotNull Currency currencyBase, @NotNull Currency currencyQuote, @Positive int digits, double swapLong,
                           double swapShort) implements Serializable {

  @Serial
  private static final long serialVersionUID = -4803297545198308765L;
}