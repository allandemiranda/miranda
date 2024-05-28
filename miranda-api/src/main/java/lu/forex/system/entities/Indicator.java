package lu.forex.system.entities;

import lu.forex.system.enums.SignalIndicatorStatus;

public abstract class Indicator {

  public abstract SignalIndicatorStatus getStatus();
}
