package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
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
  public void calculateTechnicalIndicator(final @NotNull CandlestickDto @NotNull [] candlesticksDesc) {
    final TechnicalIndicatorDto[] technicalIndicatorDtos = IntStream.range(0, candlesticksDesc.length < this.getPeriod() ? 1 : this.getPeriod())
        .mapToObj(i -> candlesticksDesc[i].technicalIndicators().stream()
            .filter(cIndicator -> this.getIndicator().equals(cIndicator.indicator())).findFirst()
            .orElseThrow(() -> new TechnicalIndicatorNotFoundException(candlesticksDesc[i].scope().toString())))
        .toArray(TechnicalIndicatorDto[]::new);

    final MovingAverageDto emaFast = candlesticksDesc[0].movingAverages().stream()
        .filter(ema -> ema.period() == this.getFastPeriod() && this.getEmaApply().equals(ema.priceType()) && Objects.nonNull(ema.value())).findFirst()
        .orElse(null);

    final MovingAverageDto emaSlow = candlesticksDesc[0].movingAverages().stream()
        .filter(ema -> ema.period() == this.getSlowPeriod() && this.getEmaApply().equals(ema.priceType()) && Objects.nonNull(ema.value())).findFirst()
        .orElse(null);

    if (Objects.nonNull(emaFast) && Objects.nonNull(emaSlow)) {
      final double macd = MathUtils.getSubtract(emaFast.value(), emaSlow.value());
      technicalIndicatorDtos[0].data().put(KEY_MACD, macd);

      if (technicalIndicatorDtos.length == this.getPeriod() && IntStream.range(0, this.getPeriod()).noneMatch(i -> Objects.isNull(technicalIndicatorDtos[i].data().get(KEY_MACD)))) {
        final Collection<Double> collectionMacd = IntStream.range(0, this.getPeriod()).parallel().mapToObj(i -> technicalIndicatorDtos[i].data().get(KEY_MACD)).toList();
        final double signal = MathUtils.getMed(collectionMacd);
        technicalIndicatorDtos[0].data().put(KEY_SIGNAL, signal);
      }
    }

    final TechnicalIndicator technicalIndicator = this.getTechnicalIndicatorMapper().toEntity(technicalIndicatorDtos[0]);
    if(technicalIndicatorDtos.length >= 2) {
      technicalIndicator.setSignal(this.processingSignal(technicalIndicatorDtos[0], technicalIndicatorDtos[1]));
    } else {
      technicalIndicator.setSignal(SignalIndicator.NEUTRAL);
    }
    this.getTechnicalIndicatorRepository().save(technicalIndicator);
  }

  private SignalIndicator processingSignal(final @NotNull TechnicalIndicatorDto currentTechnicalIndicatorDto, final @NotNull TechnicalIndicatorDto lastTechnicalIndicatorDto) {
    final Double signalCurrent = currentTechnicalIndicatorDto.data().get(KEY_SIGNAL);
    final Double macdCurrent = currentTechnicalIndicatorDto.data().get(KEY_MACD);
    final Double signalLast = lastTechnicalIndicatorDto.data().get(KEY_SIGNAL);
    final Double macdLast = lastTechnicalIndicatorDto.data().get(KEY_MACD);
    if (Objects.nonNull(signalCurrent) && Objects.nonNull(macdCurrent) && Objects.nonNull(signalLast) && Objects.nonNull(macdLast)) {
      final BigDecimal signalCurrentBigDecimal = BigDecimal.valueOf(signalCurrent);
      final BigDecimal macdCurrentBigDecimal = BigDecimal.valueOf(macdCurrent);
      if (signalCurrentBigDecimal.compareTo(macdCurrentBigDecimal) > 0) {
        final BigDecimal signalLastBigDecimal = BigDecimal.valueOf(signalLast);
        final BigDecimal macdLastBigDecimal = BigDecimal.valueOf(macdLast);
        if(signalLastBigDecimal.compareTo(macdLastBigDecimal) < 0) {
          return SignalIndicator.BEARISH;
        }
      } else if (signalCurrentBigDecimal.compareTo(macdCurrentBigDecimal) < 0) {
        final BigDecimal signalLastBigDecimal = BigDecimal.valueOf(signalLast);
        final BigDecimal macdLastBigDecimal = BigDecimal.valueOf(macdLast);
        if(signalLastBigDecimal.compareTo(macdLastBigDecimal) > 0) {
          return SignalIndicator.BULLISH;
        }
      }
    }
    return SignalIndicator.NEUTRAL;
  }
}
