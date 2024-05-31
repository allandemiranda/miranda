package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.MapKeyClass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lu.forex.system.enums.Indicator;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "candlestick", indexes = {
    @Index(name = "idx_candlestick_unq", columnList = "symbol_name, time_frame, timestamp", unique = true)}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_candlestick_symbol_name", columnNames = {"symbol_name", "time_frame", "timestamp"})})
public class Candlestick implements Serializable {

  @Serial
  private static final long serialVersionUID = 8655855891835745603L;

  @EmbeddedId
  private CandlestickHead head;

  @Embedded
  private CandlestickBody body;

  @Exclude
  @OneToMany(cascade = CascadeType.ALL)
  private Set<MovingAverage> movingAverages = new LinkedHashSet<>();

  @Exclude
  @OneToMany(cascade = CascadeType.ALL)
  @MapKeyClass(Indicator.class)
  private Map<Indicator, TechnicalIndicator> indicators = new EnumMap<>(Indicator.class);

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Candlestick that = (Candlestick) o;
    return Objects.equals(this.getHead(), that.getHead());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getHead());
  }
}