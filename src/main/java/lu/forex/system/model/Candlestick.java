package lu.forex.system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Candlestick implements Serializable {

  @Serial
  private static final long serialVersionUID = 3707234909834765373L;

  @Id
  @GeneratedValue()
  @Column(unique = true, nullable = false)
  @Setter(AccessLevel.PROTECTED)
  private UUID id;
  @Column(nullable = false)
  private double high;
  @Column(nullable = false)
  private double low;
  @Column(nullable = false)
  private double open;
  @Column(nullable = false)
  private double close;
  @JoinColumn(nullable = false)
  @ManyToOne
  private Symbol symbol;

  public Candlestick(final double high, final double low, final double open, final double close, final Symbol symbol) {
    this.high = high;
    this.low = low;
    this.open = open;
    this.close = close;
    this.symbol = symbol;
  }

  public Candlestick(final double open, final Symbol symbol) {
    this.high = open;
    this.low = open;
    this.open = open;
    this.close = open;
    this.symbol = symbol;
  }

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    final Candlestick that = (Candlestick) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
