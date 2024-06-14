package lu.forex.system.utils;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.experimental.UtilityClass;
import lu.forex.system.entities.Order;

@UtilityClass
public class OrderUtils {

  public static double getProfit(@NotNull Order order) {
    return switch (order.getOrderType()) {
      case BUY -> BigDecimal.valueOf(order.getCloseTick().getBid()).subtract(BigDecimal.valueOf(order.getOpenTick().getAsk())).doubleValue();
      case SELL -> BigDecimal.valueOf(order.getOpenTick().getBid()).subtract(BigDecimal.valueOf(order.getCloseTick().getAsk())).doubleValue();
    };
  }
}
