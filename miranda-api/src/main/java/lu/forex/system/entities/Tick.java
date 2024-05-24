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
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lu.forex.system.annotations.TickRepresentation;
import lu.forex.system.listeners.TickListener;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@EntityListeners(TickListener.class)
@Table(name = "tick", indexes = {@Index(name = "idx_tick_symbol_name", columnList = "symbol_name")}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_tick_id_symbol_name", columnNames = {"id", "symbol_name", "timestamp"})})
@TickRepresentation
public class Tick implements Serializable {

  @Serial
  private static final long serialVersionUID = 8640594898040399917L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, unique = true)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @NotNull
  @Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, targetEntity = Symbol.class)
  @JoinColumn(name = "symbol_name", referencedColumnName = "name", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private Symbol symbol;

  @NotNull
  @PastOrPresent
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

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Tick tick = (Tick) o;
    return Double.compare(this.getBid(), tick.getBid()) == 0 && Double.compare(this.getAsk(), tick.getAsk()) == 0 && Objects.equals(this.getId(),
        tick.getId()) && Objects.equals(this.getSymbol(), tick.getSymbol()) && Objects.equals(this.getTimestamp(), tick.getTimestamp());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getId(), this.getSymbol(), this.getTimestamp(), this.getBid(), this.getAsk());
  }
}