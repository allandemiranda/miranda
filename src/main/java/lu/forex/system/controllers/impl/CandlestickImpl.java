package lu.forex.system.controllers.impl;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.controllers.CandlestickController;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.service.CandlestickService;

@Getter(AccessLevel.PRIVATE)
public class CandlestickImpl implements CandlestickController {

  private final CandlestickService candlestickService;

  public CandlestickImpl(final CandlestickService candlestickService) {
    this.candlestickService = candlestickService;
  }

  @Override
  public Collection<CandlestickDto> getCandlesticks(final String symbolName, final TimeFrame timeFrame) {
    return this.getCandlestickService().findAllBySymbolNameAndTimeFrameOrderByTimestampAsc(symbolName, timeFrame);
  }

  @Override
  public CandlestickDto getLastCandlesticks(final String symbolName, final TimeFrame timeFrame) {
    return this.getCandlestickService().findOneBySymbolNameAndTimeFrameOrderByTimeFrameAsc(symbolName, timeFrame);
  }
}
