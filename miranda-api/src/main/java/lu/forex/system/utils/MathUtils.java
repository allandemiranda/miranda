package lu.forex.system.utils;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

public class MathUtils {

  private MathUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static double getMed(final @NotNull Collection<Double> collection) {
    return collection.stream().map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(BigDecimal.valueOf(collection.size()), 10, RoundingMode.HALF_UP).doubleValue();
  }

  public static double getMax(final double @NotNull ... values) {
    BigDecimal max = BigDecimal.valueOf(values[0]);
    for (double d : values) {
      final BigDecimal bigDecimal = BigDecimal.valueOf(d);
      if (bigDecimal.compareTo(max) > 0) {
        max = bigDecimal;
      }
    }
    return max.doubleValue();
  }

  public static double getSum(final @NotNull Collection<Double> collection) {
    return collection.stream().map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
  }

  public static double getDivision(final double dividend, final double divisor) {
    return BigDecimal.valueOf(dividend).divide(BigDecimal.valueOf(divisor), 10, RoundingMode.HALF_UP).doubleValue();
  }

  public static double getMultiplication(final double a, final double b) {
    return BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(b)).doubleValue();
  }
}
