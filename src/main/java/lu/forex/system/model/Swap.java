package lu.forex.system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
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
public class Swap implements Serializable {

  @Serial
  private static final long serialVersionUID = 5616569054693168805L;

  @Id
  @GeneratedValue()
  @Column(unique = true, nullable = false)
  @Setter(AccessLevel.PROTECTED)
  private UUID id;
  @Column(nullable = false)
  private double longTax;
  @Column(nullable = false)
  private double shortTax;
  @Column(nullable = false)
  private DayOfWeek rateTriple = DayOfWeek.WEDNESDAY;

  public Swap(final double longTax, final double shortTax, final DayOfWeek rateTriple) {
    this.longTax = longTax;
    this.shortTax = shortTax;
    this.rateTriple = rateTriple;
  }

  public Swap(final double longTax, final double shortTax) {
    this.longTax = longTax;
    this.shortTax = shortTax;
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
    final Swap swap = (Swap) o;
    return getId() != null && Objects.equals(getId(), swap.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
