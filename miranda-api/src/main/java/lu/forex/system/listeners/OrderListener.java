package lu.forex.system.listeners;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.entities.Order;
import lu.forex.system.entities.OrderProfit;
import org.springframework.stereotype.Component;

@Component
@Getter(AccessLevel.PRIVATE)
public class OrderListener {

  @PersistenceContext
  private EntityManager entityManager;

  @PrePersist
  public void prePersistOrder(@NotNull Order order) {
    updateProfitAndHistoric(order, order.getOpenTick().getTimestamp());
  }

  @PreUpdate
  public void preUpdateOrder(@NotNull Order order) {
    updateProfitAndHistoric(order, order.getCloseTick().getTimestamp());
  }

  private void updateProfitAndHistoric(final @NotNull Order order, final LocalDateTime timestamp) {
    final double profit = this.getProfit(order);
    order.setProfit(profit);
    final OrderProfit orderProfit = new OrderProfit();
    orderProfit.setTimestamp(timestamp);
    orderProfit.setProfit(order.getProfit());
    this.getEntityManager().persist(orderProfit);
    order.getHistoricProfit().add(orderProfit);
  }

  public double getProfit(@NotNull Order order) {
    return switch (order.getOrderType()) {
      case BUY -> BigDecimal.valueOf(order.getCloseTick().getBid()).subtract(BigDecimal.valueOf(order.getOpenTick().getAsk())).doubleValue();
      case SELL -> BigDecimal.valueOf(order.getOpenTick().getBid()).subtract(BigDecimal.valueOf(order.getCloseTick().getAsk())).doubleValue();
    };
  }
}
