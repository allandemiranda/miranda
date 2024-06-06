package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.MovingAverageType;

/**
 * DTO for {@link lu.forex.system.entities.MovingAverage}
 */
public record MovingAverageDto(@NotNull UUID id, @NotNull MovingAverageType type, @Positive int period, @NotNull CandlestickApply apply,
                               @PositiveOrZero Double value) implements
    Serializable {

  @Serial
  private static final long serialVersionUID = -8009489505290688038L;
}