package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.enums.PriceType;

/**
 * DTO for {@link lu.forex.system.entities.MovingAverage}
 */
public record MovingAverageDto(@NotNull UUID id, @NotNull MovingAverageType type, @Positive int period, @NotNull PriceType priceType,
                               @PositiveOrZero Double value) implements Serializable {

  @Serial
  private static final long serialVersionUID = 7463603133261133755L;
}