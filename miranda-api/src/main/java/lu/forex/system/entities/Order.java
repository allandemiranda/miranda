package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lu.forex.system.enums.OrderStatus;
import lu.forex.system.enums.OrderType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "order_operation", indexes = {@Index(name = "idx_order_open_tick_id", columnList = "open_tick_id"),
    @Index(name = "idx_order_open_tick_status", columnList = "open_tick_id, order_status")})
public class Order implements Serializable {

  @Serial
  private static final long serialVersionUID = -226600900389020655L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, unique = true, updatable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @NotNull
  @ManyToOne(optional = false, targetEntity = Tick.class)
  @JoinColumn(name = "open_tick_id", nullable = false, updatable = false)
  private Tick openTick;

  @NotNull
  @ManyToOne(optional = false, targetEntity = Tick.class)
  @JoinColumn(name = "close_tick_id", nullable = false)
  private Tick closeTick;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "order_type", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private OrderType orderType;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "order_status", nullable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private OrderStatus orderStatus;

  @Column(name = "profit", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double profit;

  @NotNull
  @ManyToOne(optional = false)
  @JoinColumn(name = "trade_id", nullable = false)
  @Exclude
  private Trade trade;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Order order = (Order) o;
    return Objects.equals(getId(), order.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}