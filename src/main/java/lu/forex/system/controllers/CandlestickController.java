package lu.forex.system.controllers;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.operations.CandlestickOperations;
import lu.forex.system.services.CandlestickService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickController implements CandlestickOperations {

  private final CandlestickService candlestickService;

  @Override
  public Collection<CandlestickResponseDto> getCandlesticks(final String symbolName, final TimeFrame timeFrame) {
    return this.getCandlestickService().getCandlesticks(symbolName, timeFrame);
  }
}
