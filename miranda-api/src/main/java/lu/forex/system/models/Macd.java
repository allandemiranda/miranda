package lu.forex.system.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Macd extends Indicator implements Serializable {

  @Serial
  private static final long serialVersionUID = -8032186423265530275L;

  private Double fastEma;
  private Double slowEma;
  private Double macdValue;

  @Override
  public boolean isValidData() {
    return Stream.of(this.getFastEma(), this.getSlowEma(), this.getMacdValue()).noneMatch(Objects::isNull);
  }
}
