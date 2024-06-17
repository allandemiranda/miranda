package lu.forex.system.controllers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.operations.OrderOperation;
import lu.forex.system.services.OrderService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class OrderController implements OrderOperation {

  private final OrderService orderService;

  @Override
  public void cleanOperationUntilLastDays(final String symbolName, final int days) {
    this.getOrderService().cleanOrdersCloseAfterDays(symbolName, days);
  }
}
