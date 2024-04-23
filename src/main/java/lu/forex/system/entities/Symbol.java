package lu.forex.system.entities;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lu.forex.system.enums.Currency;

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
  @NotBlank
  private String name;
  @Column(nullable = false)
  private Currency margin;
  @Column(nullable = false)
  private Currency profit;
  @Column(nullable = false)
  private int digits;
  @JoinColumn(nullable = false)
  @OneToOne(cascade = CascadeType.ALL)
  private Swap swap;

  public Symbol(final String name, final Currency margin, final Currency profit, final int digits, final Swap swap) {
    this.name = name;
    this.margin = margin;
    this.profit = profit;
    this.digits = digits;
    this.swap = swap;
  }
}
