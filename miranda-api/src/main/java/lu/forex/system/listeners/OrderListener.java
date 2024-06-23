package lu.forex.system.listeners;

import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import lu.forex.system.entities.Order;
import lu.forex.system.enums.OrderStatus;
import lu.forex.system.utils.OrderUtils;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {

  @PreUpdate
  public void preUpdate(final @NotNull Order order) {
    final double profit = OrderUtils.getProfit(order);
    order.setProfit(profit);
    if (profit <= 0D && Math.abs(profit) > order.getTrade().getStopLoss()) {
      order.setOrderStatus(OrderStatus.STOP_LOSS);
    } else if (order.getProfit() >= order.getTrade().getTakeProfit()) {
      order.setOrderStatus(OrderStatus.TAKE_PROFIT);
    }
  }
}
