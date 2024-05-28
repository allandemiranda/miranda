package lu.forex.system.entities;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import lu.forex.system.enums.SignalIndicatorStatus;

public abstract class Indicator {

  public abstract int numberOfCandlesticksToCalculate();
  public abstract void calculateIndicator(final @Nonnull Collection<Candlestick> lastCandlesticks);
  public abstract boolean isReadyToGetStatus();
  public abstract SignalIndicatorStatus getStatus();
}
