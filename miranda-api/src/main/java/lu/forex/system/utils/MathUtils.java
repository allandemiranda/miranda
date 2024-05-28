package lu.forex.system.utils;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.EmaStatistic;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.repositories.CandlestickRepository;
import lu.forex.system.repositories.EmaIndicatorRepository;

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

  public static double getSubtract(final double a, final double b) {
    return BigDecimal.valueOf(a).subtract(BigDecimal.valueOf(b)).doubleValue();
  }

  public static double getDivision(final double dividend, final double divisor) {
    return BigDecimal.valueOf(dividend).divide(BigDecimal.valueOf(divisor), 10, RoundingMode.HALF_UP).doubleValue();
  }

  public static double getMultiplication(final double a, final double b) {
    return BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(b)).doubleValue();
  }

  @Nullable
  public static Double getEma(final @NotNull EmaStatistic emaStatistic, final @NotNull CandlestickRepository candlestickRepository,
      final @NotNull EmaIndicatorRepository emaIndicatorRepository, final @NotNull Candlestick candlestick) {
    final Symbol symbol = candlestick.getSymbol();
    final TimeFrame timeFrame = candlestick.getTimeFrame();
    final int period = emaStatistic.getPeriod();
    final CandlestickApply candlestickApply = emaStatistic.getCandlestickApply();

    if (emaIndicatorRepository.existsByPeriodAndCandlestickApplyAndSymbolNameAndTimeFrameAndEmaNotNull(period, candlestickApply, symbol.getName(),
        timeFrame)) {
      final Double lastEma = emaStatistic.getLastEma();
      if (Objects.nonNull(lastEma)) {
        final double a = MathUtils.getMultiplication(candlestickApply.getPrice(candlestick), emaStatistic.getPercentagePrice());
        final double b = MathUtils.getSubtract(1, emaStatistic.getPercentagePrice());
        final double c = MathUtils.getMultiplication(lastEma, b);
        return MathUtils.getSum(Stream.of(a, c).toList());
      }
    } else {
      final Collection<Double> collection = candlestickRepository.streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame, period).map(candlestickApply::getPrice).toList();
      if (collection.size() == period) {
        return MathUtils.getMed(collection);
      }
    }

    return null;
  }
}
