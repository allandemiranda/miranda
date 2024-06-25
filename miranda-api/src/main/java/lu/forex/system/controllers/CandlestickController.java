package lu.forex.system.controllers;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.operations.CandlestickOperation;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.services.ScopeService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickController implements CandlestickOperation {

  private final CandlestickService candlestickService;
  private final ScopeService scopeService;

  @Override
  public Collection<CandlestickDto> getCandlesticks(final String symbolName, final TimeFrame timeFrame) {
    final ScopeDto scopeDto = this.getScopeService().getScope(symbolName, timeFrame);
    // NEED BE UPDATE TO THE FRONT END WITH PAGINABLE
    return this.getCandlestickService().findCandlesticksDescWithLimit(scopeDto.id(), 5);
  }

}
