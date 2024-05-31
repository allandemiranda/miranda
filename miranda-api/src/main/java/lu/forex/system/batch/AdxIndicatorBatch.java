package lu.forex.system.batch;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lu.forex.system.batch.model.IndicatorBatch;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.CandlestickBody;
import lu.forex.system.enums.Indicator;
import lu.forex.system.enums.SignalIndicatorStatus;
import lu.forex.system.utils.BatchUtils;
import lu.forex.system.utils.MathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
public class AdxIndicatorBatch extends IndicatorBatch {

  private static final String KEY_ADX = "adx";
  private static final String KEY_P_DI_P = "+di(P)";
  private static final String KEY_N_DI_P = "-di(P)";
  private static final String KEY_TR_1 = "tr1";
  private static final String KEY_P_DM_1 = "+dm1";
  private static final String KEY_N_DM_1 = "-dm1";
  private static final String KEY_DX = "dx";

  @Value("${adx.parameters.period:14}")
  private int period;

  @Value("${adx.parameters.tendencyLine:50}")
  private int tendencyLine;

  @Override
  public SignalIndicatorStatus getStatus(final @NotNull List<Candlestick> candlesticksDesc) {
    if(candlesticksDesc.size() > 1) {
      final Candlestick currentCandlestick = BatchUtils.getCurrentCandlestick(candlesticksDesc);
      final Double adx = this.getAdx(currentCandlestick);
      final Double pDiP = this.getPDiP(currentCandlestick);
      final Double nDiP = this.getNDiP(currentCandlestick);
      if (Objects.nonNull(adx) && Objects.nonNull(pDiP) && Objects.nonNull(nDiP)) {
        final BigDecimal adxBigDecimal = BigDecimal.valueOf(adx);
        if (adxBigDecimal.compareTo(BigDecimal.valueOf(this.getTendencyLine())) >= 0) {
          final BigDecimal pDiBigDecimal = BigDecimal.valueOf(pDiP);
          final BigDecimal nDiBigDecimal = BigDecimal.valueOf(nDiP);
          if (pDiBigDecimal.compareTo(nDiBigDecimal) > 0) {
            return SignalIndicatorStatus.BUY;
          }
          if (pDiBigDecimal.compareTo(nDiBigDecimal) < 0) {
            return SignalIndicatorStatus.SELL;
          }
        }
      }
    }
    return SignalIndicatorStatus.NEUTRAL;
  }

  @Override
  public Indicator getIndicator() {
    return Indicator.ADX;
  }

  @Override
  public int numberOfCandlesticksToCalculate() {
    return this.getPeriod();
  }

  @Override
  public void calculateIndicator(final @NotNull List<Candlestick> candlesticksDesc) {
    final Candlestick currentCandlestick = BatchUtils.getCurrentCandlestick(candlesticksDesc);

    if (candlesticksDesc.size() > 1) {
      final CandlestickBody candlestickCandlestickBody = currentCandlestick.getBody();
      final BigDecimal cHigh = BigDecimal.valueOf(candlestickCandlestickBody.getHigh());
      final BigDecimal cLow = BigDecimal.valueOf(candlestickCandlestickBody.getLow());
      final BigDecimal cClose = BigDecimal.valueOf(candlestickCandlestickBody.getClose());

      final Candlestick lastCandlestick = BatchUtils.getLastCandlestick(candlesticksDesc);
      final CandlestickBody lastCandlestickCandlestickBody = lastCandlestick.getBody();
      final BigDecimal lastClose = BigDecimal.valueOf(lastCandlestickCandlestickBody.getClose());
      final BigDecimal lastHigh = BigDecimal.valueOf(lastCandlestickCandlestickBody.getHigh());
      final BigDecimal lastLow = BigDecimal.valueOf(lastCandlestickCandlestickBody.getLow());

      // get TR1
      final double trOne = MathUtils.getMax(cHigh.subtract(cLow).doubleValue(), cHigh.subtract(cClose).doubleValue(), Math.abs(cLow.subtract(lastClose).doubleValue()));
      this.setTrOne(currentCandlestick, trOne);

      // get +DM1
      final double pDmOne = cHigh.subtract(lastHigh).compareTo(lastLow.subtract(cLow)) > 0 ? MathUtils.getMax(cHigh.subtract(lastHigh).doubleValue(), 0d) : 0d;
      this.setPDmOne(currentCandlestick, pDmOne);

      // get -DM1
      final double nDmOne = lastLow.subtract(cLow).compareTo(cHigh.subtract(lastHigh)) > 0 ? MathUtils.getMax(lastLow.subtract(cLow).doubleValue(), 0d) : 0d;
      this.setNDmOne(currentCandlestick, nDmOne);

      final Collection<double[]> collectionOne = candlesticksDesc.stream()
          .limit(this.getPeriod())
          .filter(c -> Objects.nonNull(this.getTrOne(c)) && Objects.nonNull(this.getPDmOne(c)) && Objects.nonNull(this.getNDmOne(c)))
          .map(c -> new double[]{this.getTrOne(c), this.getPDmOne(c), this.getNDmOne(c)}).toList();
      if (collectionOne.size() == this.getPeriod()) {
        // get TR(P)
        final double trP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[0]).toList());

        // get +DM(P)
        final double pDmP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[1]).toList());

        // get -DM(P)
        final double nDmP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[2]).toList());

        // get +DI(P)
        final double pDiP = MathUtils.getMultiplication(100, MathUtils.getDivision(pDmP, trP));
        this.setPDiP(currentCandlestick, pDiP);

        // get -DI(P)
        final double nDiP = MathUtils.getMultiplication(100, MathUtils.getDivision(nDmP, trP));
        this.setNDiP(currentCandlestick, nDiP);

        // get DI diff
        final double diDiff = Math.abs(BigDecimal.valueOf(pDiP).subtract(BigDecimal.valueOf(nDiP)).doubleValue());

        // get DI sum
        final double diSum = BigDecimal.valueOf(pDiP).add(BigDecimal.valueOf(nDiP)).doubleValue();

        // get DX
        final double dx = MathUtils.getMultiplication(100, MathUtils.getDivision(diDiff, diSum));
        this.setDx(currentCandlestick, dx);

        final Collection<Double> collectionDx = candlesticksDesc.stream()
            .limit(this.getPeriod())
            .filter(c -> Objects.nonNull(this.getDx(c)))
            .map(this::getDx)
            .toList();
        if (collectionDx.size() == this.getPeriod()) {
          // get ADX
          final double adx = MathUtils.getMed(collectionDx);
          this.setAdx(currentCandlestick, adx);
        }
      }
    }

    this.getTechnicalIndicator(BatchUtils.getCurrentCandlestick(candlesticksDesc)).setSignalStatus(this.getStatus(candlesticksDesc));
  }

  private Double getDx(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_DX);
  }

  private void setDx(final @NotNull Candlestick candlestick, final double dx) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_DX, dx);
  }

  private Double getAdx(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_ADX);
  }

  private void setAdx(final @NotNull Candlestick candlestick, final double adx) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_ADX, adx);
  }

  private Double getPDiP(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_P_DI_P);
  }

  private void setPDiP(final @NotNull Candlestick candlestick, final double pDiP) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_P_DI_P, pDiP);
  }

  private Double getNDiP(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_N_DI_P);
  }

  private void setNDiP(final @NotNull Candlestick candlestick, final double nDiP) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_N_DI_P, nDiP);
  }

  private Double getTrOne(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_TR_1);
  }

  private void setTrOne(final @NotNull Candlestick candlestick, final double trOne) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_TR_1, trOne);
  }

  private Double getPDmOne(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_P_DM_1);
  }

  private void setPDmOne(final @NotNull Candlestick candlestick, final double pdmOne) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_P_DM_1, pdmOne);
  }

  private Double getNDmOne(final @NotNull Candlestick candlestick) {
    return this.getTechnicalIndicator(candlestick).getDataMap().get(KEY_N_DM_1);
  }

  private void setNDmOne(final @NotNull Candlestick candlestick, final double nDmOne) {
    this.getTechnicalIndicator(candlestick).getDataMap().put(KEY_N_DM_1, nDmOne);
  }

}
