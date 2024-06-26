package lu.forex.system.utils;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import lombok.experimental.UtilityClass;
import lu.forex.system.entities.Order;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.enums.SignalIndicator;

@UtilityClass
public class OrderUtils {

  public static double getProfit(@NotNull Order order) {
    return switch (order.getOrderType()) {
      case BUY -> BigDecimal.valueOf(order.getCloseTick().getBid()).subtract(BigDecimal.valueOf(order.getOpenTick().getAsk())).multiply(BigDecimal.valueOf(Math.pow(10, order.getCloseTick().getSymbol().getDigits()))).doubleValue();
      case SELL -> BigDecimal.valueOf(order.getOpenTick().getBid()).subtract(BigDecimal.valueOf(order.getCloseTick().getAsk())).multiply(BigDecimal.valueOf(Math.pow(10, order.getCloseTick().getSymbol().getDigits()))).doubleValue();
    };
  }

  public static SignalIndicator getSignalIndicator(final @NotNull Collection<TechnicalIndicator> technicalIndicators) {
    final int power = technicalIndicators.stream().mapToInt(ti -> switch (ti.getSignal()) {
      case NEUTRAL -> 0;
      case BULLISH -> 1;
      case BEARISH -> -1;
    }).sum();
    if (Math.abs(power) >= (technicalIndicators.size() / 2)) {
      if (power < 0) {
        return SignalIndicator.BEARISH;
      } else if (power > 0) {
        return SignalIndicator.BULLISH;
      }
    }
    return SignalIndicator.NEUTRAL;
  }
}
