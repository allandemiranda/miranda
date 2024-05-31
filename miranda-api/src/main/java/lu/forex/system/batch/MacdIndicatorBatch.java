package lu.forex.system.batch;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.Getter;
import lu.forex.system.batch.model.IndicatorBatch;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.Indicator;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.enums.SignalIndicatorStatus;
import lu.forex.system.utils.BatchUtils;
import lu.forex.system.utils.MathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
public class MacdIndicatorBatch extends IndicatorBatch {

  private static final String KEY_SIGNAL = "signal";
  private static final String KEY_MACD = "macd";

  @Value("${macd.parameters.fast.period:12}")
  private int fastPeriod;

  @Value("${macd.parameters.slow.period:26}")
  private int slowPeriod;

  @Value("${macd.parameters.macd.period:9}")
  private int period;

  @Value("${macd.parameters.ema.apply:CLOSE}")
  private CandlestickApply emaApply;

  @Override
  public int numberOfCandlesticksToCalculate() {
    return IntStream.of(this.getFastPeriod(), this.getSlowPeriod(), this.getPeriod()).max().getAsInt();
  }

  @Override
  public void calculateIndicator(final @NotNull List<Candlestick> candlesticksDesc) {
    final Candlestick currentCandlestick = BatchUtils.getCurrentCandlestick(candlesticksDesc);

    final MovingAverage emaFast = currentCandlestick.getMovingAverages().stream()
        .filter(ema -> ema.getPeriod() == this.getFastPeriod() && this.getEmaApply().equals(ema.getCandlestickApply()) && Objects.nonNull(ema.getValue()))
        .findFirst()
        .orElse(null);

    final MovingAverage emaSlow = currentCandlestick.getMovingAverages().stream()
        .filter(ema -> ema.getPeriod() == this.getSlowPeriod() && this.getEmaApply().equals(ema.getCandlestickApply()) && Objects.nonNull(ema.getValue()))
        .findFirst()
        .orElse(null);

    if (Objects.nonNull(emaFast) && Objects.nonNull(emaSlow)) {
      final double macd = MathUtils.getSubtract(emaFast.getValue(), emaSlow.getValue());
      this.setMacd(currentCandlestick, macd);

      final Collection<Double> collectionMacd = candlesticksDesc.stream()
          .limit(this.getPeriod())
          .filter(c -> Objects.nonNull(this.getMacd(c)))
          .map(this::getMacd)
          .toList();
      if (collectionMacd.size() == this.getPeriod()) {
        final double signal = MathUtils.getMed(collectionMacd);
        this.setSignal(currentCandlestick, signal);
      }
    }

    this.getTechnicalIndicator(BatchUtils.getCurrentCandlestick(candlesticksDesc)).setSignalStatus(this.getStatus(candlesticksDesc));
  }

  @Override
  public SignalIndicatorStatus getStatus(final @NotNull List<Candlestick> candlesticksDesc) {
    if(candlesticksDesc.size() > 1) {
      final Candlestick currentCandlestick = BatchUtils.getCurrentCandlestick(candlesticksDesc);
      final Double signal = this.getSignal(currentCandlestick);
      final Double macd = this.getMacd(currentCandlestick);
      if(Objects.nonNull(signal) && Objects.nonNull(macd)) {
        final BigDecimal signalBigDecimal = BigDecimal.valueOf(signal);
        final BigDecimal macdBigDecimal = BigDecimal.valueOf(macd);
        if (signalBigDecimal.compareTo(macdBigDecimal) > 0) {
          return SignalIndicatorStatus.SELL;
        } else if (signalBigDecimal.compareTo(macdBigDecimal) < 0) {
          return SignalIndicatorStatus.BUY;
        }
      }
    }
    return SignalIndicatorStatus.NEUTRAL;
  }

  private Double getSignal(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_SIGNAL);
  }

  private void setSignal(final @NotNull Candlestick candlestick, final double macd) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_SIGNAL, macd);
  }

  private Double getMacd(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_MACD);
  }

  private void setMacd(final @NotNull Candlestick candlestick, final double signal) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_MACD, signal);
  }

  @Override
  public Indicator getIndicator() {
    return Indicator.MACD;
  }

  @Override
  public void initIndicator(final @NotNull Candlestick candlestick) {
    super.initIndicator(candlestick);

    final MovingAverage emaFast = new MovingAverage();
    emaFast.setType(MovingAverageType.EMA);
    emaFast.setPeriod(this.getFastPeriod());
    emaFast.setCandlestickApply(CandlestickApply.CLOSE);
    candlestick.getMovingAverages().add(emaFast);

    final MovingAverage emaSlow = new MovingAverage();
    emaSlow.setType(MovingAverageType.EMA);
    emaSlow.setPeriod(this.getSlowPeriod());
    emaSlow.setCandlestickApply(CandlestickApply.CLOSE);
    candlestick.getMovingAverages().add(emaSlow);
  }
}
