package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

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
  @Column(unique = true, nullable = false)
  @Setter(AccessLevel.PROTECTED)
  @Size(max = 6, min = 6)
  @NonNull
  @NotBlank
  private String name;
  @Column(nullable = false)
  @NonNull
  @NotBlank
  private String description;
  @Column(nullable = false)
  @Size(max = 3, min = 3)
  @NonNull
  @NotBlank
  private String margin;
  @Column(nullable = false)
  @Size(max = 3, min = 3)
  @NonNull
  @NotBlank
  private String profit;
  @Column(nullable = false)
  private int digits;
  @Column(nullable = false)
  private double swapLong;
  @Column(nullable = false)
  private double swapShort;

  public Symbol(final @NonNull @NotBlank String description, final @NonNull @NotBlank String margin, final @NonNull @NotBlank String profit,
      final int digits, final double swapLong, final double swapShort) {
    this.description = description;
    this.margin = margin;
    this.profit = profit;
    this.digits = digits;
    this.swapLong = swapLong;
    this.swapShort = swapShort;
  }
}
