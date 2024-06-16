package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.dtos.TickDto;
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
          return this.getOrderRepository().save(order);
        }).map(order -> this.getOrderMapper().toDto(order)).toList();
  }
}
