package lu.forex.system.controllers;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.operations.CandlestickOperation;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.services.ScopeService;
import lu.forex.system.services.SymbolService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickController implements CandlestickOperation {

  private final CandlestickService candlestickService;
  private final SymbolService symbolService;
  private final ScopeService scopeService;

  @Override
  public Collection<CandlestickDto> getCandlesticks(final String symbolName, final TimeFrame timeFrame) {
    final SymbolDto symbolDto = this.getSymbolService().getSymbol(symbolName);
    final ScopeDto scopeDto = this.getScopeService().getScope(symbolDto, timeFrame);
    return this.getCandlestickService().getCandlesticks(scopeDto);
  }

}
