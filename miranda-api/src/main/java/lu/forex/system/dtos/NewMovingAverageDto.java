package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.enums.PriceType;

/**
 * DTO for {@link lu.forex.system.entities.MovingAverage}
 */
public record NewMovingAverageDto(@NotNull MovingAverageType type, @Positive int period, @NotNull PriceType priceType) implements Serializable {

  @Serial
  private static final long serialVersionUID = 6735763909170414963L;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final NewMovingAverageDto that = (NewMovingAverageDto) o;
    return period == that.period && priceType == that.priceType && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, period, priceType);
  }
}