package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lu.forex.system.listeners.TickListener;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@EntityListeners(TickListener.class)
@Table(name = "tick", indexes = {@Index(name = "idx_tick_symbol_id", columnList = "symbol_id")}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_tick_symbol_id_timestamp", columnNames = {"symbol_id", "timestamp"})})
public class Tick implements Serializable {

  @Serial
  private static final long serialVersionUID = 6203104273208402242L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, unique = true, updatable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @NotNull
  @ManyToOne(optional = false, targetEntity = Symbol.class)
  @JoinColumn(name = "symbol_id", nullable = false, updatable = false)
  private Symbol symbol;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "timestamp", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.TIMESTAMP)
  private LocalDateTime timestamp;

  @Positive
  @Column(name = "bid", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double bid;

  @Positive
  @Column(name = "ask", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double ask;

  @PositiveOrZero
  @Column(name = "spread", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double spread;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Tick tick = (Tick) o;
    return Objects.equals(getId(), tick.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}