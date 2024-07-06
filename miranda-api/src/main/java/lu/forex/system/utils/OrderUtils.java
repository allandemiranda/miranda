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
    final long powerBull = technicalIndicators.stream().filter(technicalIndicator -> SignalIndicator.BULLISH.equals(technicalIndicator.getSignal())).count();
    final long powerBear = technicalIndicators.stream().filter(technicalIndicator -> SignalIndicator.BEARISH.equals(technicalIndicator.getSignal())).count();
    final long powerNeutral = technicalIndicators.stream().filter(technicalIndicator -> SignalIndicator.NEUTRAL.equals(technicalIndicator.getSignal())).count();

    if(powerBull == 3 || powerBull == 2 && powerNeutral == 1 /*|| powerBull == 2 && powerBear == 1 || powerBull == 1 && powerNeutral == 2*/) {
      return SignalIndicator.BULLISH;
    } else if(powerBear == 3 || powerBear == 2 && powerNeutral == 1 /*|| powerBear == 2 && powerBull == 1 || powerBear == 1 && powerNeutral == 2*/) {
      return SignalIndicator.BEARISH;
    } else {
      return SignalIndicator.NEUTRAL;
    }
  }
}
