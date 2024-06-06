package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Embeddable
public class CandlestickBody implements Serializable {

  @Serial
  private static final long serialVersionUID = 6476346148561262056L;

  @PositiveOrZero
  @Column(name = "high", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double high;

  @PositiveOrZero
  @Column(name = "low", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double low;

  @PositiveOrZero
  @Column(name = "open", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double open;

  @PositiveOrZero
  @Column(name = "close", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double close;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final CandlestickBody that = (CandlestickBody) o;
    return Double.compare(getHigh(), that.getHigh()) == 0 && Double.compare(getLow(), that.getLow()) == 0
           && Double.compare(getOpen(), that.getOpen()) == 0 && Double.compare(getClose(), that.getClose()) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getHigh(), getLow(), getOpen(), getClose());
  }
}
