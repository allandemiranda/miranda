package lu.forex.system.controllers;

import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.enums.OrderStatus;
import lu.forex.system.operations.OrderOperation;
import lu.forex.system.services.OrderService;
import lu.forex.system.services.SymbolService;
import lu.forex.system.services.TickService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class OrderController implements OrderOperation {

  private final OrderService orderService;
  private final TickService tickService;
  private final SymbolService symbolService;

  @Override
  public void cleanOperationUntilLastDays(final String symbolName, final int days) {
    this.getOrderService().cleanOrdersCloseAfterDays(symbolName, days);
  }

  @Override
  public Collection<OrderDto> getOrdersOpen(final String symbolName) {
    final UUID symbolId = this.getSymbolService().getSymbol(symbolName).id();
    return this.getOrderService().getOrders(symbolId, OrderStatus.OPEN);
  }

  @Override
  public Collection<OrderDto> getOrdersClose(final String symbolName) {
    final UUID symbolId = this.getSymbolService().getSymbol(symbolName).id();
    return Stream.concat(this.getOrderService().getOrders(symbolId, OrderStatus.STOP_LOSS).stream(),
            this.getOrderService().getOrders(symbolId, OrderStatus.TAKE_PROFIT).stream())
        .sorted(Comparator.comparing(orderDto -> orderDto.openTick().timestamp())).toList();
  }

  @Override
  public Collection<OrderDto> getOrdersTakeProfit(final String symbolName) {
    final UUID symbolId = this.getSymbolService().getSymbol(symbolName).id();
    return this.getOrderService().getOrders(symbolId, OrderStatus.TAKE_PROFIT);
  }
}
