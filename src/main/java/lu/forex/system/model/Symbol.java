package lu.forex.system.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
import lu.forex.system.enums.Currency;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Symbol implements Serializable {

  @Serial
  private static final long serialVersionUID = -8600247918110142179L;

  @Id
  @GeneratedValue()
  @Column(unique = true, nullable = false)
  @Setter(AccessLevel.PROTECTED)
  private UUID id;
  @Column(unique = true, nullable = false)
  private String value;
  @Column(nullable = false)
  private Currency margin;
  @Column(nullable = false)
  private Currency profit;
  @Column(nullable = false)
  private int digits;
  @JoinColumn(nullable = false)
  @OneToOne
  private Swap swap;

  public Symbol(final String value, final Currency margin, final Currency profit, final int digits, final Swap swap) {
    this.value = value;
    this.margin = margin;
    this.profit = profit;
    this.digits = digits;
    this.swap = swap;
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
    final Symbol symbol = (Symbol) o;
    return getId() != null && Objects.equals(getId(), symbol.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
