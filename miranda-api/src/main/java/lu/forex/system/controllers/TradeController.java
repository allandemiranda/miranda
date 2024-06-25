package lu.forex.system.controllers;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.operations.TradeOperation;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.services.ScopeService;
import lu.forex.system.services.SymbolService;
import lu.forex.system.services.TickService;
import lu.forex.system.services.TradeService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TradeController implements TradeOperation {

  private final TradeService tradeService;
  private final SymbolService symbolService;
  private final ScopeService scopeService;
  private final CandlestickService candlestickService;
  private final TickService tickService;

  @Override
  public List<TradeDto> managementOfTradeActivation(final String symbolName) {
    return this.getTradeService().managementEfficientTradesScenariosToBeActivated(symbolName);
  }

  @Override
  public Collection<TradeDto> getTrades(final String symbolName) {
    final UUID symbolId = this.getSymbolService().getSymbol(symbolName).id();
    return this.getTradeService().getTrades(symbolId);
  }
}
