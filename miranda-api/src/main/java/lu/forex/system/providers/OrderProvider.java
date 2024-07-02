package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.dtos.TradeDto;
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
  public @NotNull List<OrderDto> getOrders(final @NotNull UUID symbolId, final @NotNull OrderStatus orderStatus) {
    return this.getOrderRepository().findByOpenTick_Symbol_IdAndOrderStatusOrderByOpenTick_TimestampAsc(symbolId, orderStatus).stream().map(this.getOrderMapper()::toDto).collect(Collectors.toList());
  }

  @Override
  public @NotNull Stream<OrderDto> processingInitOrders(final @NotNull List<TickDto> tickDtoList, final @NotNull Stream<TradeDto> tradeDtos) {
    log.info("Starting processingInitOrders({})", tickDtoList.getFirst().symbol().currencyPair().name());
    final var orders = tradeDtos.parallel().flatMap(tradeDto -> tradeDto.orders().stream())
        .map(orderDto -> this.getOrderRepository().findById(orderDto.id()).orElseThrow())
        .map(order -> {
          final List<TickDto> ticksFit = tickDtoList.stream().filter(tickDto -> tickDto.timestamp().isAfter(order.getCloseTick().getTimestamp())).toList();
          for (final TickDto tickDto : ticksFit) {
            final var tick = this.getTickMapper().toEntity(tickDto);
            order.setCloseTick(tick);
            order.setProfit(OrderUtils.getProfit(order));
            if (order.getProfit() < 0D) {
              if(Math.abs(order.getProfit()) > (double) order.getTrade().getStopLoss()) {
                order.setOrderStatus(OrderStatus.STOP_LOSS);
                break;
              }
            } else if (order.getProfit() >= (double) order.getTrade().getTakeProfit()) {
              order.setOrderStatus(OrderStatus.TAKE_PROFIT);
              break;
            }
          }
          return order;
        }).toList();

    log.info("Ending processingInitOrders({})", tickDtoList.getFirst().symbol().currencyPair().name());
    return this.getOrderRepository().saveAll(orders).stream().map(this.getOrderMapper()::toDto);
  }
}
