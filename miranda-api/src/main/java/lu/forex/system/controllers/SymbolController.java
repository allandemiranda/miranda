package lu.forex.system.controllers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.NewSymbolDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.operations.SymbolOperation;
import lu.forex.system.services.ScopeService;
import lu.forex.system.services.SymbolService;
import lu.forex.system.services.TradeService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class SymbolController implements SymbolOperation {

  private final SymbolService symbolService;
  private final ScopeService scopeService;
  private final TradeService tradeService;

  @Override
  public Collection<SymbolDto> getSymbols() {
    return this.getSymbolService().getSymbols();
  }

  @Override
  public SymbolDto getSymbol(final String name) {
    return this.getSymbolService().getSymbol(name);
  }

  @Override
  public Collection<TradeDto> addSymbol(final NewSymbolDto newSymbolDto) {
    final SymbolDto symbolDto = this.getSymbolService().addSymbol(newSymbolDto);
    final Set<ScopeDto> scopeDtos = this.getScopeService().addScope(symbolDto, Arrays.stream(TimeFrame.values()).toList());
    return this.getTradeService().generateTrades(scopeDtos);
  }
}
