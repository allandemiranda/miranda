package lu.forex.system.enums;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeFrame {
  //@formatter:off
  M15("M15", 900),
  M30("M30", M15.getTime() + M15.getTime()),
  H1("H1", M30.getTime() + M30.getTime()),
  H2("H2", H1.getTime() + H1.getTime()),
  H12("H12", H1.getTime() * 12),
  D1("D1", H1.getTime() * 24);
  //@formatter:on

  @NotBlank
  private final String name;
  private final int time;
}
