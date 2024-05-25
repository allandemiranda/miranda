package lu.forex.system.utils;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

public class MathUtils {

  private MathUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static double getSMA(final @NotNull Collection<Double> collection) {
    return collection.stream().map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(BigDecimal.valueOf(collection.size()), 10, RoundingMode.HALF_UP).doubleValue();
  }
}
