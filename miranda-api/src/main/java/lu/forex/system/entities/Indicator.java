package lu.forex.system.entities;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.enums.SignalIndicatorStatus;

@Valid
public abstract class Indicator {

  public abstract int numberOfCandlesticksToCalculate();

  public abstract void calculateIndicator(final @NotNull Collection<Candlestick> lastCandlesticks);

  public abstract boolean isReadyToGetStatus();

  public abstract SignalIndicatorStatus getStatus();
}
