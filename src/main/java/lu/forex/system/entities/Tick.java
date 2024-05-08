package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import lombok.ToString;
import lombok.ToString.Exclude;
import lu.forex.system.listeners.TickListener;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@Entity
@EntityListeners(TickListener.class)
@Table(name = "tick", indexes = {
    @Index(name = "idx_tick_symbol_name", columnList = "symbol_name")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_tick_id_symbol_name", columnNames = {"id", "symbol_name", "timestamp"})
})
public class Tick implements Serializable {

  @Serial
  private static final long serialVersionUID = 8640594898040399917L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, unique = true)
  private UUID id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, targetEntity = Symbol.class)
  @JoinColumn(name = "symbol_name", referencedColumnName = "name", nullable = false, updatable = false)
  @Exclude
  private Symbol symbol;

  @NotNull
  @Column(name = "timestamp", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.TIMESTAMP)
  private LocalDateTime timestamp;

  @Positive
  @Column(name = "high", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double bid;

  @Positive
  @Column(name = "low", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
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
    return Objects.equals(id, tick.id) && Objects.equals(symbol, tick.symbol) && Objects.equals(timestamp, tick.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, symbol, timestamp);
  }
}