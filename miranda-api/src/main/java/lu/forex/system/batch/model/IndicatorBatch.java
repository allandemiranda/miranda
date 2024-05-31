package lu.forex.system.batch.model;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.enums.Indicator;
import lu.forex.system.enums.SignalIndicatorStatus;

public abstract class IndicatorBatch {

  public abstract void calculateIndicator(final @NotNull List<Candlestick> candlesticksDesc);

  public abstract int numberOfCandlesticksToCalculate();

  public abstract SignalIndicatorStatus getStatus(final @NotNull List<Candlestick> candlesticksDesc);

  public abstract Indicator getIndicator();

  public TechnicalIndicator getTechnicalIndicator(final @NotNull Candlestick candlestick){
    return candlestick.getIndicators().get(getIndicator());
  }

  public void initIndicator(final @NotNull Candlestick candlestick){
    final TechnicalIndicator indicator = new TechnicalIndicator();
    indicator.setSignalStatus(SignalIndicatorStatus.NEUTRAL);
    candlestick.getIndicators().put(this.getIndicator(), indicator);
  }

}
