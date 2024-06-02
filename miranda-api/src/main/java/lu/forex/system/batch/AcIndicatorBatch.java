package lu.forex.system.batch;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lu.forex.system.batch.model.IndicatorBatch;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.SignalIndicatorStatus;
import lu.forex.system.enums.Indicator;
import lu.forex.system.utils.BatchUtils;
import lu.forex.system.utils.MathUtils;
import org.springframework.stereotype.Service;

@Service
public class AcIndicatorBatch extends IndicatorBatch {

  private static final String KEY_AC = "ac";
  private static final String KEY_AO = "ao";
  private static final String KEY_MP = "mp";

  @Override
  public int numberOfCandlesticksToCalculate() {
    return 34;
  }

  @Override
  public void calculateIndicator(final @NotNull List<Candlestick> candlesticksDesc) {
    // set the MP value
    final double mp = CandlestickApply.TYPICAL_PRICE.getPrice(BatchUtils.getCurrentCandlestick(candlesticksDesc));
    this.setMp(BatchUtils.getCurrentCandlestick(candlesticksDesc), mp);

    final Collection<Double> collectionSmaMp34 = candlesticksDesc.stream()
        .limit(34)
        .map(this::getMp)
        .toList();
    if (collectionSmaMp34.size() == 34) {
      // get SMA(MP,34)
      final double smaMp34 = MathUtils.getMed(collectionSmaMp34);

      // get SMA(MP,5)
      final Collection<Double> collectionMp5 = candlesticksDesc.stream()
          .limit(5)
          .map(this::getMp)
          .toList();
      final double smaMp5 = MathUtils.getMed(collectionMp5);

      // get SMA(MP,5) - SMA(MP,34)
      final double ao = BigDecimal.valueOf(smaMp5).subtract(BigDecimal.valueOf(smaMp34)).doubleValue();
      this.setAo(BatchUtils.getCurrentCandlestick(candlesticksDesc), ao);

      // get SMA(ao,5)
      final Collection<Double> collectionSmaAo5 = candlesticksDesc.stream()
          .limit(5)
          .filter(c -> Objects.nonNull(this.getAo(c)))
          .map(this::getAo)
          .toList();
      if (collectionSmaAo5.size() == 4) {
        final double smaAo5 = MathUtils.getMed(collectionSmaAo5);

        // get ao - SMA(ao,5)
        final double ac = BigDecimal.valueOf(ao).subtract(BigDecimal.valueOf(smaAo5)).doubleValue();
        this.setAc(BatchUtils.getCurrentCandlestick(candlesticksDesc),ac);
      }
    }

    this.getTechnicalIndicator(BatchUtils.getCurrentCandlestick(candlesticksDesc)).setSignalStatus(this.getStatus(candlesticksDesc));
  }

  @Override
  public SignalIndicatorStatus getStatus(final @NotNull List<Candlestick> candlesticksDesc) {
    if(candlesticksDesc.size() > 1) {
      final Double currentAc = this.getAc(BatchUtils.getCurrentCandlestick(candlesticksDesc));
      final Double lestAc = this.getAc(BatchUtils.getLastCandlestick(candlesticksDesc));
      if(Objects.nonNull(currentAc) && Objects.nonNull(lestAc)) {
        final BigDecimal acBigDecimal = BigDecimal.valueOf(currentAc);
        final int compared = BigDecimal.valueOf(currentAc).compareTo(BigDecimal.valueOf(lestAc));
        if (acBigDecimal.compareTo(BigDecimal.ZERO) > 0 && compared > 0) {
          return SignalIndicatorStatus.BULLISH;
        } else if (acBigDecimal.compareTo(BigDecimal.ZERO) < 0 && compared < 0) {
          return SignalIndicatorStatus.BEARISH;
        } else {
          return SignalIndicatorStatus.NEUTRAL;
        }
      }
    }
    return SignalIndicatorStatus.NEUTRAL;
  }

  @Override
  public Indicator getIndicator() {
    return Indicator.AC;
  }

  private Double getAc(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_AC);
  }

  private void setAc(final @NotNull Candlestick candlestick, final double ac) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_AC, ac);
  }

  private Double getAo(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_AO);
  }

  private void setAo(final @NotNull Candlestick candlestick, final double ao) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_AO, ao);
  }

  private Double getMp(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_MP);
  }

  private void setMp(final @NotNull Candlestick candlestick, final double mp) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_MP, mp);
  }

}
