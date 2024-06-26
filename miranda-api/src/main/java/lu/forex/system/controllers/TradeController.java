package lu.forex.system.controllers;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.enums.OrderStatus;
import lu.forex.system.operations.TradeOperation;
import lu.forex.system.services.SymbolService;
import lu.forex.system.services.TradeService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TradeController implements TradeOperation {

  private final TradeService tradeService;
  private final SymbolService symbolService;

  @Override
  public Collection<TradeDto> getTrades(final String symbolName) {
    final UUID symbolId = this.getSymbolService().getSymbol(symbolName).id();
    final Collection<TradeDto> trades = this.getTradeService().getTrades(symbolId);
    trades.forEach(t -> System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", t.scope().timeFrame(), t.stopLoss(), t.takeProfit(), t.spreadMax(), t.slotWeek(), t.slotStart(), t.slotEnd(), t.isActivate(), t.balance(),
        t.orders().stream().filter(orderDto -> !orderDto.orderStatus().equals(OrderStatus.OPEN)).count(), t.orders().size())));
    return List.of();
  }
}
