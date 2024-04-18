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
import java.time.LocalDateTime;
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
public class Tick implements Serializable {

  @Serial
  private static final long serialVersionUID = 8689684652979027409L;

  @Id
  @GeneratedValue()
  @Column(unique = true, nullable = false)
  @Setter(AccessLevel.PROTECTED)
  private UUID id;
  @Column(nullable = false)
  private LocalDateTime dateTime;
  @Column(nullable = false)
  private double bid;
  @Column(nullable = false)
  private double ask;
  @JoinColumn(nullable = false)
  @ManyToOne
  private Symbol symbol;

  private Tick(final LocalDateTime dateTime, final double bid, final double ask, final Symbol symbol) {
    this.dateTime = dateTime;
    this.bid = bid;
    this.ask = ask;
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
    final Tick tick = (Tick) o;
    return getId() != null && Objects.equals(getId(), tick.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
