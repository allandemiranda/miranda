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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "trade", indexes = {
    @Index(name = "idx_trade_scope_id_spread_max", columnList = "scope_id, spread_max, slot_week, slot_start, slot_end")}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_trade_scope_id_stop_loss", columnNames = {"scope_id", "stop_loss", "take_profit", "spread_max", "slot_week",
        "slot_start", "slot_end"})})
public class Trade implements Serializable {

  @Serial
  private static final long serialVersionUID = 2701254574961269153L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @NotNull
  @ManyToOne(optional = false, targetEntity = Scope.class)
  @JoinColumn(name = "scope_id", nullable = false, updatable = false)
  private Scope scope;

  @PositiveOrZero
  @Column(name = "stop_loss", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.SMALLINT)
  private int stopLoss;

  @Positive
  @Column(name = "take_profit", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.SMALLINT)
  private int takeProfit;

  @PositiveOrZero
  @Column(name = "spread_max", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.SMALLINT)
  private int spreadMax;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "slot_week", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private DayOfWeek slotWeek;

  @NotNull
  @Column(name = "slot_start", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.TIME)
  private LocalTime slotStart;

  @NotNull
  @Column(name = "slot_end", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.TIME)
  private LocalTime slotEnd;

  @Column(name = "is_activate", nullable = false)
  @JdbcTypeCode(SqlTypes.BOOLEAN)
  private boolean isActivate;

  @NotNull
  @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Order> orders = new ArrayList<>();

  @Transient
  public double getBalance() {
    return this.getOrders().stream().mapToDouble(Order::getProfit).sum();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Trade trade = (Trade) o;
    return Objects.equals(getId(), trade.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}