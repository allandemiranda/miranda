package lu.forex.system.untitled;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeFrame {
  //@formatter:off
  M1("M1", 1, Frame.MINUTE),
  M5("M5", 5, Frame.MINUTE),
  M15("M15", 15, Frame.MINUTE),
  M30("M30", 30, Frame.MINUTE),
  H1("H1", 1, Frame.HOUR),
  H2("H2", 2, Frame.HOUR),
  H4("H4", 4, Frame.HOUR),
  H8("H8", 8, Frame.HOUR),
  D1("D1", 1, Frame.DAY);
  //@formatter:on

  private final String name;
  private final int timeValue;
  private final Frame frame;
}
