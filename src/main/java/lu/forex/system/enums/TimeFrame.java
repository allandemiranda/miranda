package lu.forex.system.enums;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeFrame {
  //@formatter:off
  M15("M15", 15, Frame.MINUTE),
  M30("M30", 30, Frame.MINUTE),
  H1("H1", 1, Frame.HOUR),
  H4("H4", 4, Frame.HOUR),
  D1("D1", 1, Frame.DAY);
  //@formatter:on

  @NotBlank
  @NotNull
  private final String name;
  private final int timeValue;
  @NotNull
  private final Frame frame;
}
