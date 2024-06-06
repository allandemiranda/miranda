package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link lu.forex.system.entities.Symbol}
 */
public record SymbolDto(@NotNull UUID id, @NotNull CurrencyPairDto currencyPair, @Positive int digits, @NotNull SwapDto swap) implements
    Serializable {
  @Serial
  private static final long serialVersionUID = 976211379668406865L;
  }