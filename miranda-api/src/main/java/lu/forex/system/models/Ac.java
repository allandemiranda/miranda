package lu.forex.system.models;

import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Ac extends Indicator implements Serializable {

  @Serial
  private static final long serialVersionUID = -86035851672680870L;

  @PositiveOrZero
  private double mp;
  private Double ao;
  private Double acValue;

  public Ac(final @PositiveOrZero double mp) {
    this.mp = mp;
  }

  @Override
  public boolean isValidData() {
    return Stream.of(this.getAo(), this.getAcValue()).noneMatch(Objects::isNull);
  }
}
