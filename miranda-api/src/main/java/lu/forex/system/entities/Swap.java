package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Embeddable
public class Swap implements Serializable {

  @Serial
  private static final long serialVersionUID = -6566371202120772499L;

  @Column(name = "percentage_long", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double percentageLong;

  @Column(name = "percentage_short", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double percentageShort;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Swap swap = (Swap) o;
    return Double.compare(percentageLong, swap.percentageLong) == 0 && Double.compare(percentageShort, swap.percentageShort) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(percentageLong, percentageShort);
  }
}
