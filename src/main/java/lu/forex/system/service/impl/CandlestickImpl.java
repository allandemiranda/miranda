package lu.forex.system.service.impl;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickCreateDto;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.repositories.CandlestickRepository;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.service.CandlestickService;

@Getter(AccessLevel.PRIVATE)
public class CandlestickImpl implements CandlestickService {

  private final CandlestickRepository candlestickRepository;
  private final SymbolRepository symbolRepository;
  private final CandlestickMapper candlestickMapper;

  public CandlestickImpl(final CandlestickRepository candlestickRepository, final SymbolRepository symbolRepository,
      final CandlestickMapper candlestickMapper) {
    this.candlestickRepository = candlestickRepository;
    this.symbolRepository = symbolRepository;
    this.candlestickMapper = candlestickMapper;
  }

  @Override
  public CandlestickDto save(@Nonnull final CandlestickCreateDto candlestickCreateDto, final String symbolName) {
    final Symbol symbol = this.getSymbolRepository().findByName(symbolName).orElseThrow(SymbolNotFoundException::new);
    final Candlestick candlestick = new Candlestick();
    candlestick.setTimeFrame(candlestickCreateDto.timeFrame());
    candlestick.setTimestamp(candlestickCreateDto.timestamp());
    candlestick.setSymbol(symbol);
    candlestick.setHigh(candlestickCreateDto.open());
    candlestick.setLow(candlestickCreateDto.open());
    candlestick.setOpen(candlestickCreateDto.open());
    candlestick.setClose(candlestickCreateDto.open());
    return this.getCandlestickMapper().toDto(this.getCandlestickRepository().save(candlestick));
  }

  @Override
  public Collection<CandlestickDto> findAllBySymbolNameAndTimeFrameOrderByTimestampAsc(final String symbolName, final TimeFrame timeFrame) {
    return this.getCandlestickRepository().findAllBySymbolNameAndTimeFrameOrderByTimestampAsc(symbolName, timeFrame).stream()
        .map(this.getCandlestickMapper()::toDto).collect(Collectors.toCollection(ArrayList::new));
  }

  @Override
  public CandlestickDto findOneBySymbolNameAndTimeFrameOrderByTimeFrameAsc(final String symbolName, final TimeFrame timeFrame) {
    return this.getCandlestickMapper()
        .toDto(this.getCandlestickRepository().findOneBySymbolNameAndTimeFrameOrderByTimeFrameAsc(symbolName, timeFrame));
  }
}
