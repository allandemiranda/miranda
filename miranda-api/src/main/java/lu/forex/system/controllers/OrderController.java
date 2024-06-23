package lu.forex.system.controllers;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.operations.OrderOperation;
import lu.forex.system.services.OrderService;
import lu.forex.system.services.TickService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class OrderController implements OrderOperation {

  private final OrderService orderService;
  private final TickService tickService;

  @Override
  public void cleanOperationUntilLastDays(final String symbolName, final int days) {
    this.getOrderService().cleanOrdersCloseAfterDays(symbolName, days);
  }

  @Override
  public void initOrderByInitCandlesticks(final String symbolName) {
    final List<TickDto> ticks = this.getTickService().getTicksBySymbolName(symbolName);
    this.getOrderService().processingInitOrders(ticks);
  }
}
