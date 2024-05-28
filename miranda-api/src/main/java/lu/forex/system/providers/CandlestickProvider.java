package lu.forex.system.providers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickIndicatorDto;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.AcIndicator;
import lu.forex.system.entities.AdxIndicator;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.EmaStatistic;
import lu.forex.system.entities.Indicator;
import lu.forex.system.entities.MacdIndicator;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.repositories.AcIndicatorRepository;
import lu.forex.system.repositories.CandlestickRepository;
import lu.forex.system.repositories.EmaIndicatorRepository;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.utils.MathUtils;
import lu.forex.system.utils.TimeFrameUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickProvider implements CandlestickService {

  private final CandlestickRepository candlestickRepository;
  private final CandlestickMapper candlestickMapper;
  private final SymbolRepository symbolRepository;
  private final EmaIndicatorRepository emaIndicatorRepository;
  private final AcIndicatorRepository acIndicatorRepository;

  @NotNull
  @Override
  public Stream<CandlestickResponseDto> getCandlesticks(@NotNull final String symbolName, @NotNull final TimeFrame timeFrame) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    return this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampAsc(symbol, timeFrame).map(this.getCandlestickMapper()::toDto);
  }

  @Override
  public void createOrUpdateCandlestickByPrice(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName,
      @NotNull final LocalDateTime timestamp, final @NotNull TimeFrame timeFrame, final double price) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    final LocalDateTime candlestickTimestamp = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);
    final Optional<Candlestick> lastCandlestickOptional = this.getCandlestickRepository()
        .findFirstBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame);
    final Candlestick candlestick =
        (lastCandlestickOptional.isPresent() && lastCandlestickOptional.get().getTimestamp().equals(candlestickTimestamp)) ? updateCandlestick(price,
            lastCandlestickOptional.get()) : createCandlestick(timeFrame, price, candlestickTimestamp, symbol);
    this.getCandlestickRepository().save(candlestick);

    candlestick.getEmaStatistics()
        .forEach(emaIndicator -> emaIndicator.setEma(this.getEma(emaIndicator, candlestickRepository, emaIndicatorRepository, candlestick)));
    this.getCandlestickRepository().save(candlestick);

    this.calculatingIndicators(candlestick);
    this.getCandlestickRepository().save(candlestick);
  }

  public Double getEma(final @NotNull EmaStatistic emaStatistic, final @NotNull CandlestickRepository candlestickRepository,
      final @NotNull EmaIndicatorRepository emaIndicatorRepository, final @NotNull Candlestick candlestick) {
    final Symbol symbol = candlestick.getSymbol();
    final TimeFrame timeFrame = candlestick.getTimeFrame();
    final int period = emaStatistic.getPeriod();
    final CandlestickApply candlestickApply = emaStatistic.getCandlestickApply();

    if (emaIndicatorRepository.existsByPeriodAndCandlestickApplyAndSymbolNameAndTimeFrameAndEmaNotNull(period, candlestickApply, symbol.getName(),
        timeFrame)) {
      final Double lastEma = emaStatistic.getLestEmaStatistic().getEma();
      if (Objects.nonNull(lastEma)) {
        final double a = MathUtils.getMultiplication(candlestickApply.getPrice(candlestick), emaStatistic.getPercentagePrice());
        final double b = MathUtils.getSubtract(1, emaStatistic.getPercentagePrice());
        final double c = MathUtils.getMultiplication(lastEma, b);
        return MathUtils.getSum(Stream.of(a, c).toList());
      }
    } else {
      final Collection<Double> collection = candlestickRepository.streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame, period).map(candlestickApply::getPrice).toList();
      if (collection.size() == period) {
        return MathUtils.getMed(collection);
      }
    }
    return null;
  }

  private @NotNull Candlestick createCandlestick(final @NotNull TimeFrame timeFrame, final double price, final LocalDateTime timestamp,
      final Symbol symbol) {
    final Candlestick candlestick = new Candlestick();
    candlestick.setTimestamp(timestamp);
    candlestick.setTimeFrame(timeFrame);
    candlestick.setSymbol(symbol);
    candlestick.setHigh(price);
    candlestick.setLow(price);
    candlestick.setOpen(price);
    candlestick.setClose(price);

    final AcIndicator acIndicator = new AcIndicator();
    acIndicator.setCandlestick(candlestick);
    this.getAcIndicatorRepository().getFirstByCandlestick_SymbolAndCandlestick_TimeFrameOrderByCandlestick_TimestampDesc(symbol, timeFrame)
        .ifPresent(acIndicator::setLestAcIndicator);
    candlestick.setAcIndicator(acIndicator);

    final AdxIndicator adxIndicator = new AdxIndicator();
    adxIndicator.setCandlestick(candlestick);
    candlestick.setAdxIndicator(adxIndicator);

    final MacdIndicator macdIndicator = new MacdIndicator();
    candlestick.getEmaStatistics().add(this.emaIndicatorInit(candlestick, macdIndicator.getFastPeriod(), macdIndicator.getEmaApply()));
    candlestick.getEmaStatistics().add(this.emaIndicatorInit(candlestick, macdIndicator.getSlowPeriod(), macdIndicator.getEmaApply()));
    candlestick.setMacdIndicator(macdIndicator);

    return candlestick;
  }

  private void calculatingIndicators(final @NotNull Candlestick candlestick) {
    final Collection<Indicator> indicators = Arrays.asList(candlestick.getAcIndicator(), candlestick.getAdxIndicator(),
        candlestick.getMacdIndicator());
    final int maxLastCandlesticks = indicators.stream().mapToInt(Indicator::numberOfCandlesticksToCalculate).max().getAsInt();
    final List<Candlestick> lastCandlesticks = this.getCandlestickRepository()
        .streamBySymbolAndTimeFrameOrderByTimestampDesc(candlestick.getSymbol(), candlestick.getTimeFrame(), maxLastCandlesticks + 1).toList();
    indicators.forEach(indicator -> indicator.calculateIndicator(lastCandlesticks));
  }

  private @NotNull EmaStatistic emaIndicatorInit(final @NotNull Candlestick candlestick, final int period,
      final @NotNull CandlestickApply candlestickApply) {
    final String symbolName = candlestick.getSymbol().getName();
    final TimeFrame timeFrame = candlestick.getTimeFrame();
    final EmaStatistic emaStatistic = new EmaStatistic();

    emaStatistic.setPeriod(period);
    emaStatistic.setCandlestickApply(candlestickApply);
    emaStatistic.setSymbolName(symbolName);
    emaStatistic.setTimeFrame(timeFrame);
    emaStatistic.setTimestamp(candlestick.getTimestamp());
    this.getEmaIndicatorRepository().getFirstByPeriodAndCandlestickApplyAndSymbolNameAndTimeFrameOrderByTimestampDesc(emaStatistic.getPeriod(),
        emaStatistic.getCandlestickApply(), symbolName, timeFrame).ifPresent(emaStatistic::setLestEmaStatistic);

    return emaStatistic;
  }

  private @NotNull Candlestick updateCandlestick(final double price, final @NotNull Candlestick candlestick) {
    if (candlestick.getHigh() < price) {
      candlestick.setHigh(price);
    } else if (candlestick.getLow() > price) {
      candlestick.setLow(price);
    }
    candlestick.setClose(price);

    return candlestick;
  }

  @Override
  public @NotNull Stream<CandlestickIndicatorDto> getLastCandlesticks(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName,
      @NotNull final TimeFrame timeFrame, final int limit) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    return this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame, limit)
        .sorted(Comparator.comparing(Candlestick::getTimestamp)).map(candlestickMapper::toDto1);
  }
}
