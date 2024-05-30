package lu.forex.system.controllers;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.operations.CandlestickOperations;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.services.CandlestickService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickController implements CandlestickOperations {

  private final CandlestickService candlestickService;
  private final SymbolRepository symbolRepository; // REMOVE AFTER TESTS

  @Override
  public Collection<CandlestickResponseDto> getCandlesticks(final String symbolName, final TimeFrame timeFrame) {
    return this.getCandlestickService().getCandlesticks(symbolName, timeFrame).toList();
  }

  // REMOVE AFTER TESTS
  @GetMapping("/{symbolName}/{timeFrame}/{limit}")
  @ResponseStatus(HttpStatus.OK)
  public Collection<CandlestickResponseDto> getCandlesticksLimit(final @PathVariable("symbolName") String symbolName, final @PathVariable("timeFrame") TimeFrame timeFrame, final @PathVariable("limit") int limit) {
    Symbol symbol = getSymbolRepository().findFirstByName(symbolName).orElseThrow(SymbolNotFoundException::new);
    return this.getCandlestickService().getLastCandlesticks(symbol.getName(), timeFrame, limit).toList();
  }
}
