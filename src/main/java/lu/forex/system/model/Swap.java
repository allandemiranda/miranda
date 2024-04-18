package lu.forex.system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
}
