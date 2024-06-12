package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "trade")
public class Trade implements Serializable {

  @Serial
  private static final long serialVersionUID = 2701254574961269153L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false, targetEntity = Scope.class)
  @JoinColumn(name = "scope_id", nullable = false, updatable = false)
  private Scope scope;

  @PositiveOrZero
  @Column(name = "stop_loss", nullable = false)
  @JdbcTypeCode(SqlTypes.SMALLINT)
  private int stopLoss;

  @Positive
  @Column(name = "take_profit", nullable = false)
  @JdbcTypeCode(SqlTypes.SMALLINT)
  private int takeProfit;

  @PositiveOrZero
  @Column(name = "spread_max", nullable = false)
  @JdbcTypeCode(SqlTypes.SMALLINT)
  private int spreadMax;

  @NotNull
  @Column(name = "slot_start", nullable = false)
  @JdbcTypeCode(SqlTypes.TIME)
  private LocalTime slotStart;

  @NotNull
  @Column(name = "slot_end", nullable = false)
  @JdbcTypeCode(SqlTypes.TIME)
  private LocalTime slotEnd;

  @Column(name = "is_activate", nullable = false)
  @JdbcTypeCode(SqlTypes.BOOLEAN)
  private boolean isActivate;

  @Exclude
  @NotNull
  @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Order.class)
  private Set<Order> orders = new LinkedHashSet<>();

  @Column(name = "balance", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double balance;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Trade trade = (Trade) o;
    return Objects.equals(getScope(), trade.getScope());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getScope());
  }
}