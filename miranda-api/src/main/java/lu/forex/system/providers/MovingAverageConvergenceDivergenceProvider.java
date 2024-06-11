package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.NewMovingAverageDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.enums.Indicator;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.enums.PriceType;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.exceptions.TechnicalIndicatorNotFoundException;
import lu.forex.system.mappers.CandlestickMapper;
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

  @Value("${indicator.macd.parameters.fast.period:12}")
  private int fastPeriod;

  @Value("${indicator.macd.parameters.slow.period:26}")
  private int slowPeriod;

  @Value("${indicator.macd.parameters.macd.period:9}")
  private int period;

  @Value("${indicator.macd.parameters.ema.apply:CLOSE}")
  private PriceType emaApply;

  @Getter(AccessLevel.PUBLIC)
  private final TechnicalIndicatorRepository technicalIndicatorRepository;
  private final CandlestickMapper candlestickMapper;
  @Getter(AccessLevel.PUBLIC)
  private final TechnicalIndicatorMapper technicalIndicatorMapper;
  private final MovingAverageMapper movingAverageMapper;

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

    return Stream.of(emaFast, emaSlow).map(this.getMovingAverageMapper()::toDto1).toList();
  }

  @Override
  public @NotNull TechnicalIndicatorDto calculateTechnicalIndicator(final @NotNull List<CandlestickDto> candlestickDtos) {
    final Candlestick currentCandlestick = this.getCandlestickMapper().toEntity(candlestickDtos.getFirst());
    final List<TechnicalIndicatorDto> technicalIndicatorDtos = candlestickDtos.stream().limit(IntStream.of(this.getFastPeriod(), this.getSlowPeriod(), this.getPeriod()).max().getAsInt()).map(c -> c.technicalIndicators().stream().filter(i -> this.getIndicator().equals(i.indicator())).findFirst().orElseThrow(() -> new TechnicalIndicatorNotFoundException(currentCandlestick.getScope().toString()))).toList();
    final TechnicalIndicatorDto currentTechnicalIndicatorDto = technicalIndicatorDtos.getFirst();

    final MovingAverage emaFast = currentCandlestick.getMovingAverages().stream()
        .filter(ema -> ema.getPeriod() == this.getFastPeriod() && this.getEmaApply().equals(ema.getPriceType()) && Objects.nonNull(ema.getValue()))
        .findFirst()
        .orElse(null);

    final MovingAverage emaSlow = currentCandlestick.getMovingAverages().stream()
        .filter(ema -> ema.getPeriod() == this.getSlowPeriod() && this.getEmaApply().equals(ema.getPriceType()) && Objects.nonNull(ema.getValue()))
        .findFirst()
        .orElse(null);

    if (Objects.nonNull(emaFast) && Objects.nonNull(emaSlow)) {
      final double macd = MathUtils.getSubtract(emaFast.getValue(), emaSlow.getValue());
      currentTechnicalIndicatorDto.data().put(KEY_MACD, macd);

      final Collection<Double> collectionMacd = technicalIndicatorDtos.stream()
          .limit(this.getPeriod())
          .filter(tiDto -> Objects.nonNull(tiDto.data().get(KEY_MACD)))
          .map(tiDto -> tiDto.data().get(KEY_MACD))
          .toList();
      if (collectionMacd.size() == this.getPeriod()) {
        final double signal = MathUtils.getMed(collectionMacd);
        currentTechnicalIndicatorDto.data().put(KEY_SIGNAL, signal);
      }
    }

    final TechnicalIndicator technicalIndicator = this.getTechnicalIndicatorMapper().toEntity(currentTechnicalIndicatorDto);
    technicalIndicator.setSignal(this.processingSignal(technicalIndicatorDtos));
    final TechnicalIndicator saved = this.getTechnicalIndicatorRepository().save(technicalIndicator);
    return this.getTechnicalIndicatorMapper().toDto(saved);
  }

  private SignalIndicator processingSignal(final @NotNull List<TechnicalIndicatorDto> technicalIndicatorDtos) {
    if(technicalIndicatorDtos.size() > 1) {
      final Double signal = technicalIndicatorDtos.getFirst().data().get(KEY_SIGNAL);
      final Double macd = technicalIndicatorDtos.getFirst().data().get(KEY_MACD);
      if(Objects.nonNull(signal) && Objects.nonNull(macd)) {
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
