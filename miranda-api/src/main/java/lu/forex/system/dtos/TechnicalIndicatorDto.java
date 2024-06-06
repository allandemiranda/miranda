package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import lu.forex.system.enums.Indicator;
import lu.forex.system.enums.SignalIndicator;

/**
 * DTO for {@link lu.forex.system.entities.TechnicalIndicator}
 */
public record TechnicalIndicatorDto(@NotNull UUID id, @NotNull Indicator indicator, @NotNull Map<String, Double> data,
                                    @NotNull SignalIndicator signal) implements Serializable {

  @Serial
  private static final long serialVersionUID = -2523732907819865274L;
}
