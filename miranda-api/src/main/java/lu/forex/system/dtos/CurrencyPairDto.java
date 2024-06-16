package lu.forex.system.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import lu.forex.system.enums.Currency;

/**
 * DTO for {@link lu.forex.system.entities.CurrencyPair}
 */
public record CurrencyPairDto(@NotNull Currency base, @NotNull Currency quote, @NotNull @Size(min = 6, max = 6) @NotBlank String name,
                              @NotNull @NotBlank String description) implements Serializable {

  @Serial
  private static final long serialVersionUID = -9101405847976389731L;
}