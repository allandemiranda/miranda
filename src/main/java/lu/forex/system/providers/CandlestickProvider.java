package lu.forex.system.providers;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.repositories.CandlestickRepository;
import lu.forex.system.services.CandlestickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter(AccessLevel.PRIVATE)
public class CandlestickProvider implements CandlestickService {

  private final CandlestickRepository candlestickRepository;
  private final CandlestickMapper candlestickMapper;

  @Autowired
  public CandlestickProvider(final @Nonnull CandlestickRepository candlestickRepository, final CandlestickMapper candlestickMapper) {
    this.candlestickRepository = candlestickRepository;
    this.candlestickMapper = candlestickMapper;
  }

  @Override
  public @Nonnull Collection<CandlestickResponseDto> getCandlesticks(final @Nonnull String symbolName, final @Nonnull TimeFrame timeFrame) {
    return this.getCandlestickRepository().findBySymbol_NameAndTimeFrameOrderByTimestampAsc(symbolName, timeFrame).stream()
        .map(candlestickMapper::toDto).collect(Collectors.toList());
  }
}
