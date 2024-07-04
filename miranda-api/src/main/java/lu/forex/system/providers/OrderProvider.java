package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Order;
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
@Log4j2
public class OrderProvider implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final TickMapper tickMapper;

  @Override
  public @NotNull Collection<OrderDto> getOrders(final @NotNull UUID symbolId, final @NotNull OrderStatus orderStatus) {
    return this.getOrderRepository().findByOpenTick_Symbol_IdAndOrderStatusOrderByOpenTick_TimestampAsc(symbolId, orderStatus).stream().map(this.getOrderMapper()::toDto).toList();
  }

  @Override
  public void batchProcessingInitOrders(final @NotNull TickDto @NotNull [] ticksDto) {
    log.info("Processing orders initiated");
    final Order[] orders = this.getOrderRepository().findByOpenTick_Symbol_Id(ticksDto[0].symbol().id()).toArray(Order[]::new);
    IntStream.range(0, orders.length).parallel().forEach(indexOrder -> {
      final Order order = orders[indexOrder];
      for(int i = IntStream.range(0, ticksDto.length).filter(t -> ticksDto[t].timestamp().isAfter(order.getCloseTick().getTimestamp())).findFirst().orElse(ticksDto.length); i < ticksDto.length; ++i){
        order.setCloseTick(this.getTickMapper().toEntity(ticksDto[i]));
        order.setProfit(OrderUtils.getProfit(order));
        if (order.getProfit() < 0D) {
          if(Math.abs(order.getProfit()) > order.getTrade().getStopLoss()) {
            order.setOrderStatus(OrderStatus.STOP_LOSS);
            break;
          }
        } else if (order.getProfit() >= order.getTrade().getTakeProfit()) {
          order.setOrderStatus(OrderStatus.TAKE_PROFIT);
          break;
        }
      }
    });
    log.info("Updating orders initiated");
    this.getOrderRepository().saveAll(Arrays.stream(orders).toList());
  }

  @Override
  public Collection<OrderDto> getOrders(final @NotNull UUID symbolId) {
    return this.getOrderRepository().findByOpenTick_Symbol_Id(symbolId).stream().map(this.getOrderMapper()::toDto).toList();
  }
}
