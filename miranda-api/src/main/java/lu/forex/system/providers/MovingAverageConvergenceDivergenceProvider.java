package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.NewMovingAverageDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.enums.Indicator;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.enums.PriceType;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.exceptions.TechnicalIndicatorNotFoundException;
import lu.forex.system.mappers.MovingAverageMapper;
import lu.forex.system.mappers.TechnicalIndicatorMapper;
import lu.forex.system.repositories.TechnicalIndicatorRepository;
import lu.forex.system.services.TechnicalIndicatorService;
import lu.forex.system.utils.MathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("movingAverageConvergenceDivergence")
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class MovingAverageConvergenceDivergenceProvider implements TechnicalIndicatorService {

  private static final String KEY_SIGNAL = "signal";
  private static final String KEY_MACD = "macd";
  @Getter(AccessLevel.PUBLIC)
  private final TechnicalIndicatorRepository technicalIndicatorRepository;
  @Getter(AccessLevel.PUBLIC)
  private final TechnicalIndicatorMapper technicalIndicatorMapper;
  private final MovingAverageMapper movingAverageMapper;
  @Value("${indicator.macd.parameters.fast.period:12}")
  private int fastPeriod;
  @Value("${indicator.macd.parameters.slow.period:26}")
  private int slowPeriod;
  @Value("${indicator.macd.parameters.macd.period:9}")
  private int period;
  @Value("${indicator.macd.parameters.ema.apply:CLOSE}")
  private PriceType emaApply;

  @Override
  public Indicator getIndicator() {
    return Indicator.MACD;
  }

  @Override
  public int getNumberOfCandlesticksToCalculate() {
    return IntStream.of(this.getFastPeriod(), this.getSlowPeriod(), this.getPeriod()).max().getAsInt();
  }

  @Override
  public Collection<NewMovingAverageDto> generateMAs() {
    final MovingAverage emaFast = new MovingAverage();
    emaFast.setType(MovingAverageType.EMA);
    emaFast.setPeriod(this.getFastPeriod());
    emaFast.setPriceType(PriceType.CLOSE);

    final MovingAverage emaSlow = new MovingAverage();
    emaSlow.setType(MovingAverageType.EMA);
    emaSlow.setPeriod(this.getSlowPeriod());
    emaSlow.setPriceType(PriceType.CLOSE);

    return Stream.of(emaFast, emaSlow).map(this.getMovingAverageMapper()::toNewDto).toList();
  }

  @Override
  public void calculateTechnicalIndicator(final @NotNull List<CandlestickDto> candlestickDtos) {
    final List<TechnicalIndicatorDto> technicalIndicatorDtos = candlestickDtos.stream()
        .limit(IntStream.of(this.getFastPeriod(), this.getSlowPeriod(), this.getPeriod()).max().getAsInt()).map(
            c -> c.technicalIndicators().stream().filter(i -> this.getIndicator().equals(i.indicator())).findFirst()
                .orElseThrow(() -> new TechnicalIndicatorNotFoundException(candlestickDtos.getFirst().scope().toString()))).toList();
    final TechnicalIndicatorDto currentTechnicalIndicatorDto = technicalIndicatorDtos.getFirst();
    final Set<MovingAverageDto> currentMovingAverageDtos = candlestickDtos.getFirst().movingAverages();

    final MovingAverageDto emaFast = currentMovingAverageDtos.stream()
        .filter(ema -> ema.period() == this.getFastPeriod() && this.getEmaApply().equals(ema.priceType()) && Objects.nonNull(ema.value())).findFirst()
        .orElse(null);

    final MovingAverageDto emaSlow = currentMovingAverageDtos.stream()
        .filter(ema -> ema.period() == this.getSlowPeriod() && this.getEmaApply().equals(ema.priceType()) && Objects.nonNull(ema.value())).findFirst()
        .orElse(null);

    if (Objects.nonNull(emaFast) && Objects.nonNull(emaSlow)) {
      final double macd = MathUtils.getSubtract(emaFast.value(), emaSlow.value());
      currentTechnicalIndicatorDto.data().put(KEY_MACD, macd);

      final Collection<TechnicalIndicatorDto> technicalIndicatorLimitPeriod = technicalIndicatorDtos.stream().limit(this.getPeriod()).toList();
      final Collection<Double> collectionMacd = technicalIndicatorLimitPeriod.parallelStream()
          .filter(tiDto -> Objects.nonNull(tiDto.data().get(KEY_MACD))).map(tiDto -> tiDto.data().get(KEY_MACD)).toList();
      if (collectionMacd.size() == this.getPeriod()) {
        final double signal = MathUtils.getMed(collectionMacd);
        currentTechnicalIndicatorDto.data().put(KEY_SIGNAL, signal);
      }
    }

    final TechnicalIndicator technicalIndicator = this.getTechnicalIndicatorMapper().toEntity(currentTechnicalIndicatorDto);
    technicalIndicator.setSignal(this.processingSignal(currentTechnicalIndicatorDto));
    this.getTechnicalIndicatorRepository().save(technicalIndicator);
  }

  private SignalIndicator processingSignal(final @NotNull TechnicalIndicatorDto currentTechnicalIndicatorDto) {
    if (currentTechnicalIndicatorDto.data().containsKey(KEY_SIGNAL) && currentTechnicalIndicatorDto.data().containsKey(KEY_MACD)) {
      final Double signal = currentTechnicalIndicatorDto.data().get(KEY_SIGNAL);
      final Double macd = currentTechnicalIndicatorDto.data().get(KEY_MACD);
      if (Objects.nonNull(signal) && Objects.nonNull(macd)) {
        final BigDecimal signalBigDecimal = BigDecimal.valueOf(signal);
        final BigDecimal macdBigDecimal = BigDecimal.valueOf(macd);
        if (signalBigDecimal.compareTo(macdBigDecimal) > 0) {
          return SignalIndicator.BEARISH;
        } else if (signalBigDecimal.compareTo(macdBigDecimal) < 0) {
          return SignalIndicator.BULLISH;
        }
      }
    }
    return SignalIndicator.NEUTRAL;
  }
}
