package lu.forex.system.untitled;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public class Tick implements Serializable, Comparable<Tick> {

  @Serial
  private static final long serialVersionUID = -8088631827153683626L;

  private LocalDateTime time;
  private double bid;
  private double ask;

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
