package lu.forex.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ColorHistogram {
  //@formatter:off
  BLUE(SignalIndicatorStatus.BUY),
  RED(SignalIndicatorStatus.SELL),
  BLACK(SignalIndicatorStatus.NEUTRAL);
  //@formatter:on

  private final SignalIndicatorStatus status;
}
