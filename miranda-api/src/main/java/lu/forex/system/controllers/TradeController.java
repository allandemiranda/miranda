package lu.forex.system.controllers;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TradeDto;
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
  public List<TradeDto> getTrades(final String symbolName) {
    final SymbolDto symbol = this.getSymbolService().getSymbol(symbolName);
    // UPDATE !!!
    return List.of();
  }
}
