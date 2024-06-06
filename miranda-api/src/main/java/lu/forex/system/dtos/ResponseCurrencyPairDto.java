package lu.forex.system.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lu.forex.system.enums.Currency;

/**
 * DTO for {@link lu.forex.system.entities.CurrencyPair}
 */
public record ResponseCurrencyPairDto(@NotNull UUID id, @NotNull Currency base, @NotNull Currency quote,
                                      @NotNull @Size(min = 6, max = 6) @NotEmpty @NotBlank String name,
                                      @NotNull @NotEmpty @NotBlank String description) implements Serializable {

  @Serial
  private static final long serialVersionUID = -1380104405178696066L;
}
