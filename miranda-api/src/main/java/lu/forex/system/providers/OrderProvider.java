package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Order;
import lu.forex.system.entities.OrderProfit;
import lu.forex.system.entities.Tick;
import lu.forex.system.enums.OrderStatus;
import lu.forex.system.mappers.OrderMapper;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.repositories.OrderRepository;
import lu.forex.system.services.OrderService;
import lu.forex.system.utils.OrderUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class OrderProvider implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final TickMapper tickMapper;

  @Override
  public @NotNull Collection<@NotNull OrderDto> updateOrders(@NotNull TickDto tickDto) {
    final Tick currentTick = this.getTickMapper().toEntity(tickDto);
    return this.getOrderRepository().findBySymbolNameAndOrderStatus(currentTick.getSymbol().getCurrencyPair().getName(), OrderStatus.OPEN).stream()
        .map(order -> {
          order.setCloseTick(currentTick);
          final double profit = OrderUtils.getProfit(order);
          order.setProfit(profit);
          final OrderProfit orderProfit = new OrderProfit();
          orderProfit.setProfit(profit);
          orderProfit.setTimestamp(currentTick.getTimestamp());
          order.getHistoricProfit().add(orderProfit);

          if (profit <= 0D && Math.abs(profit) > order.getTrade().getStopLoss()) {
            order.setOrderStatus(OrderStatus.STOP_LOSS);
          } else if (order.getProfit() >= order.getTrade().getTakeProfit()) {
            order.setOrderStatus(OrderStatus.TAKE_PROFIT);
          }

          return this.getOrderRepository().save(order);
        }).map(order -> this.getOrderMapper().toDto(order)).toList();
  }

  @Override
  public @NotNull Collection<@NotNull OrderDto> getOrdersByTick(@NotNull final TickDto tickDto) {
    return this.getOrderRepository().findByOpenTick_Id(tickDto.id()).stream().filter(order -> OrderStatus.OPEN.equals(order.getOrderStatus()))
        .map(order -> this.getOrderMapper().toDto(order)).toList();
  }

  @Override
  public void cleanOrdersCloseAfterDays(final @NotNull String symbolName, final int days) {
    final LocalDateTime timeAfter = LocalDateTime.now().minusDays(days);
    final Collection<Order> collection = this.getOrderRepository().findBySymbolName(symbolName).stream()
        .filter(order -> order.getOpenTick().getTimestamp().isBefore(timeAfter)).toList();
    this.getOrderRepository().findAll().removeAll(collection);
  }
}
