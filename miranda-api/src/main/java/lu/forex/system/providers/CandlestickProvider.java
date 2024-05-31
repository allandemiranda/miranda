package lu.forex.system.providers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.batch.AcIndicatorBatch;
import lu.forex.system.batch.AdxIndicatorBatch;
import lu.forex.system.batch.EmaMovingAverageBatch;
import lu.forex.system.batch.MacdIndicatorBatch;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.CandlestickBody;
import lu.forex.system.entities.CandlestickHead;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.mappers.CandlestickMapper;
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
  private final SymbolRepository symbolRepository;

  private final CandlestickMapper candlestickMapper;

  private final AcIndicatorBatch acIndicatorBatch;
  private final AdxIndicatorBatch adxIndicatorBatch;
  private final MacdIndicatorBatch macdIndicatorBatch;
  private final EmaMovingAverageBatch emaMovingAverageBatch;

  @Override
  public void createOrUpdateCandlestickByPrice(final @Nonnull Symbol symbol, final @NotNull LocalDateTime timestamp, final @NotNull TimeFrame timeFrame, final double price) {
    final Optional<Candlestick> lastCandlestickOptional = this.getCandlestickRepository().findFirstByHead_SymbolAndHead_TimeFrameOrderByHead_TimestampDesc(symbol, timeFrame);
    final LocalDateTime candlestickTimestamp = TimeFrameUtils.getCandlestickTimestamp(timestamp, timeFrame);
    final Candlestick currentCandlestick = (lastCandlestickOptional.isPresent() && lastCandlestickOptional.get().getHead().getTimestamp().equals(candlestickTimestamp))
        ? this.updateCandlestick(price, lastCandlestickOptional.get())
        : this.createCandlestick(timeFrame, price, candlestickTimestamp, symbol);
    this.getCandlestickRepository().save(currentCandlestick);
    this.calculatingIndicators(currentCandlestick);
    this.getCandlestickRepository().save(currentCandlestick);
  }

  private @NotNull Candlestick createCandlestick(final @NotNull TimeFrame timeFrame, final double price, final LocalDateTime timestamp, final Symbol symbol) {
    final Candlestick candlestick = this.initCandlestick(timeFrame, price, timestamp, symbol);

    //init indicators
    this.getAcIndicatorBatch().initIndicator(candlestick);
    this.getAdxIndicatorBatch().initIndicator(candlestick);
    this.getMacdIndicatorBatch().initIndicator(candlestick);

    return candlestick;
  }

  private @NotNull Candlestick initCandlestick(final @NotNull TimeFrame timeFrame, final double price, final LocalDateTime timestamp, final Symbol symbol) {
    final CandlestickHead head = new CandlestickHead();
    head.setTimestamp(timestamp);
    head.setTimeFrame(timeFrame);
    head.setSymbol(symbol);

    final CandlestickBody body = new CandlestickBody();
    body.setHigh(price);
    body.setLow(price);
    body.setOpen(price);
    body.setClose(price);

    final Candlestick candlestick = new Candlestick();
    candlestick.setHead(head);
    candlestick.setBody(body);

    return candlestick;
  }

  private @NotNull Candlestick updateCandlestick(final double price, final @NotNull Candlestick candlestick) {
    final CandlestickBody body = candlestick.getBody();
    if (body.getHigh() < price) {
      body.setHigh(price);
    } else if (body.getLow() > price) {
      body.setLow(price);
    }
    body.setClose(price);

    return candlestick;
  }

  private void calculatingIndicators(final @NotNull Candlestick candlestick) {
    final int maxMaPeriod = candlestick.getMovingAverages().stream().mapToInt(MovingAverage::getPeriod).max().orElse(0);
    final int maxIndicatorPeriod = IntStream.of(this.getAcIndicatorBatch().numberOfCandlesticksToCalculate(), this.getAdxIndicatorBatch().numberOfCandlesticksToCalculate(), this.getMacdIndicatorBatch().numberOfCandlesticksToCalculate()).max().orElse(0);
    final int maxPeriod = maxIndicatorPeriod + maxMaPeriod;

    final ArrayList<Candlestick> candlesticks = this.getCandlestickRepository().streamByHead_Symbol_NameAndHead_TimeFrameOrderByHead_TimestampDesc(candlestick.getHead().getSymbol().getName(), candlestick.getHead().getTimeFrame(), maxPeriod).collect(Collectors.toCollection(ArrayList::new));

    this.getEmaMovingAverageBatch().calculateMovingAverage(candlesticks);
    this.getAcIndicatorBatch().calculateIndicator(candlesticks);
    this.getAdxIndicatorBatch().calculateIndicator(candlesticks);
    this.getMacdIndicatorBatch().calculateIndicator(candlesticks);
  }

  @NotNull
  @Override
  public Stream<CandlestickResponseDto> getCandlesticks(@NotNull final String symbolName, @NotNull final TimeFrame timeFrame) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    return this.getCandlestickRepository().streamByHead_SymbolAndHead_TimeFrameOrderByHead_TimestampAsc(symbol, timeFrame).map(this.getCandlestickMapper()::toDto);
  }

  @Override
  public @NotNull Stream<CandlestickResponseDto> getLastCandlesticks(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName, @NotNull final TimeFrame timeFrame, final int limit) {
//    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    return this.getCandlestickRepository().streamByHead_Symbol_NameAndHead_TimeFrameOrderByHead_TimestampDesc(symbolName, timeFrame, limit)
        .sorted(Comparator.comparing(c -> c.getHead().getTimestamp())).map(this.getCandlestickMapper()::toDto);
  }
}
