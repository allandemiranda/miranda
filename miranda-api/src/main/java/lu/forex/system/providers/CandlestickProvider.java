package lu.forex.system.providers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickIndicatorsDto;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.models.Ac;
import lu.forex.system.models.Adx;
import lu.forex.system.models.Macd;
import lu.forex.system.repositories.CandlestickRepository;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.utils.TimeFrameUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickProvider implements CandlestickService {

  private final CandlestickRepository candlestickRepository;
  private final CandlestickMapper candlestickMapper;
  private final SymbolRepository symbolRepository;

  @NotNull
  @Override
  public Stream<CandlestickResponseDto> getCandlesticks(@NotNull final String symbolName, @NotNull final TimeFrame timeFrame) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    return this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampAsc(symbol, timeFrame).map(this.getCandlestickMapper()::toDto);
  }

  @Override
  public void createOrUpdateCandlestickByPrice(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName, @NotNull final LocalDateTime timestamp,
      final @NotNull TimeFrame timeFrame, final double price) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    final LocalDateTime localTimesFrame = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);
    final Optional<Candlestick> lastCandlestickOptional = this.getCandlestickRepository()
        .findFirstBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame);
    if (lastCandlestickOptional.isPresent() && lastCandlestickOptional.get().getTimestamp().equals(localTimesFrame)) {
      this.getCandlestickRepository().save(this.updateCandlestick(price, lastCandlestickOptional.get()));
    } else {
      this.getCandlestickRepository().save(this.createCandlestick(symbol, price, timeFrame, localTimesFrame));
    }
  }

  @Override
  public @NotNull Stream<CandlestickIndicatorsDto> getLastCandlesticks(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName, @NotNull final TimeFrame timeFrame,
      final int last) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    final Stream<@NotNull Candlestick> candlestickStream = this.getCandlestickRepository()
        .streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame);
    return candlestickStream.limit(last).sorted(Comparator.comparing(Candlestick::getTimestamp)).map(candlestickMapper::toDtoIndicator);
  }

  private @NotNull Candlestick updateCandlestick(final @Positive double price, final @Nonnull Candlestick lastCandlestick) {
    if (lastCandlestick.getHigh() < price) {
      lastCandlestick.setHigh(price);
    } else if (lastCandlestick.getLow() > price) {
      lastCandlestick.setLow(price);
    }
    lastCandlestick.setClose(price);
    return lastCandlestick;
  }

  private @NotNull Candlestick createCandlestick(final @Nonnull Symbol symbol, final @Positive double price, final @Nonnull TimeFrame timeFrame,
      final @Nonnull LocalDateTime localTimesFrame) {
    final Candlestick candlestick = new Candlestick();
    candlestick.setTimestamp(localTimesFrame);
    candlestick.setTimeFrame(timeFrame);
    candlestick.setSymbol(symbol);
    candlestick.setHigh(price);
    candlestick.setLow(price);
    candlestick.setOpen(price);
    candlestick.setClose(price);
    candlestick.setAc(new Ac(price));
    candlestick.setAdx(new Adx());
    candlestick.setMacd(new Macd());

    return candlestick;
  }
}
