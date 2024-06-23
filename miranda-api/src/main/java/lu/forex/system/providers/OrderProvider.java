package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Order;
import lu.forex.system.entities.Tick;
import lu.forex.system.enums.OrderStatus;
import lu.forex.system.mappers.OrderMapper;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.repositories.OrderRepository;
import lu.forex.system.services.OrderService;
import lu.forex.system.utils.OrderUtils;
import org.springframework.scheduling.annotation.Async;
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
  public void updateOrders(@NotNull TickDto tickDto) {
    final Tick currentTick = this.getTickMapper().toEntity(tickDto);
    final Collection<Order> collection = this.getOrderRepository()
        .findBySymbolNameAndOrderStatus(currentTick.getSymbol().getCurrencyPair().getName(), OrderStatus.OPEN).parallelStream()
        .map(order -> {
          order.setCloseTick(currentTick);
          return order;
        }).toList();
    this.getOrderRepository().saveAll(collection);
  }

  @Override
  public void cleanOrdersCloseAfterDays(final @NotNull String symbolName, final int days) {
    final LocalDateTime timeAfter = LocalDateTime.now().minusDays(days);
    final Collection<Order> collection = this.getOrderRepository().findBySymbolName(symbolName).stream()
        .filter(order -> order.getOpenTick().getTimestamp().isBefore(timeAfter)).toList();
    this.getOrderRepository().findAll().removeAll(collection);
  }

  @Async
  @Override
  public void processingInitOrders(final @NotNull List<TickDto> tickDtoList) {
    log.info(" Starting processingInitOrders()");
//    final UUID symbolId = tickDtoList.getFirst().symbol().id();
//    for (final TickDto tickDto : tickDtoList) {
//      final Tick currentTick = this.getTickMapper().toEntity(tickDto);
//      final var updateOrders = this.getOrderRepository().findByOpenTick_Symbol_IdAndOrderStatusAndCloseTick_TimestampAfterAllIgnoreCase(symbolId, OrderStatus.OPEN, currentTick.getTimestamp())
//          .parallelStream().map(order -> {
//            order.setCloseTick(currentTick);
//            return order;
//          }).toList();
//      if(!updateOrders.isEmpty()) {
//        this.getOrderRepository().saveAll(updateOrders);
//      }
//    }
    final UUID symbolId = tickDtoList.getFirst().symbol().id();
    final var orders = this.getOrderRepository().findAll().parallelStream().filter(order -> symbolId.equals(order.getOpenTick().getId())).collect(Collectors.toCollection(HashSet::new));
    for (final TickDto tickDto : tickDtoList) {
      final Tick currentTick = this.getTickMapper().toEntity(tickDto);
      orders.parallelStream()
          .filter(order -> OrderStatus.OPEN.equals(order.getOrderStatus()) && order.getCloseTick().getTimestamp().isAfter(currentTick.getTimestamp()))
          .forEach(order -> {
            order.setCloseTick(currentTick);
            order.setProfit(OrderUtils.getProfit(order));
            if (order.getProfit() <= 0D && Math.abs(order.getProfit()) > order.getTrade().getStopLoss()) {
              order.setOrderStatus(OrderStatus.STOP_LOSS);
            } else if (order.getProfit() >= order.getTrade().getTakeProfit()) {
              order.setOrderStatus(OrderStatus.TAKE_PROFIT);
            }
          });
    }
    this.getOrderRepository().saveAll(orders);
    log.info(" End processingInitOrders()");
  }
}
