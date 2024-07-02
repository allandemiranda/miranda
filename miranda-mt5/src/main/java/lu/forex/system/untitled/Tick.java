package lu.forex.system.untitled;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
public class Tick implements Serializable, Comparable<Tick> {

  @Serial
  private static final long serialVersionUID = -8088631827153683626L;

  private LocalDateTime time;
  private double bid;
  private double ask;

  //! Digits set to 5, please update !
  public double getSpread() {
    final BigDecimal price = BigDecimal.valueOf(this.getAsk()).subtract(BigDecimal.valueOf(this.getBid()));
    final BigDecimal pow = BigDecimal.valueOf(Math.pow(10, 5));
    return price.multiply(pow).doubleValue();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Tick tick = (Tick) o;
    return Objects.equals(time, tick.time);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(time);
  }

  @Override
  public int compareTo(@NotNull final Tick o) {
    return time.compareTo(o.time);
  }
}
