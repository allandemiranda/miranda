package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lu.forex.system.enums.TimeFrame;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "candlestick", indexes = {
    @Index(name = "idx_candlestick", columnList = "symbol_name, time_frame")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_candlestick_id_symbol_name", columnNames = {"id", "symbol_name", "time_frame", "timestamp"})
})
public class Candlestick implements Serializable {

  @Serial
  private static final long serialVersionUID = 8655855891835745603L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, unique = true)
  private UUID id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, targetEntity = Symbol.class)
  @JoinColumn(name = "symbol_name", referencedColumnName = "name", nullable = false, updatable = false)
  private Symbol symbol;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "time_frame", nullable = false, length = 3, updatable = false)
  private TimeFrame timeFrame;

  @NotNull
  @Column(name = "timestamp", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.TIMESTAMP)
  private LocalDateTime timestamp;

  @Positive
  @Column(name = "high", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double high;

  @Positive
  @Column(name = "low", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double low;

  @Positive
  @Column(name = "open", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double open;

  @Positive
  @Column(name = "close", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double close;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Candlestick that = (Candlestick) o;
    return Objects.equals(id, that.id) && Objects.equals(symbol, that.symbol) && timeFrame == that.timeFrame && Objects.equals(timestamp,
        that.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, symbol, timeFrame, timestamp);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + "id = " + id + ", " + "timeFrame = " + timeFrame + ", " + "timestamp = " + timestamp + ", " + "high = "
           + high + ", " + "low = " + low + ", " + "open = " + open + ", " + "close = " + close + ")";
  }
}