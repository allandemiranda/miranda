package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.MovingAverageType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "moving_average")
public class MovingAverage implements Serializable {

  @Serial
  private static final long serialVersionUID = -8303806047193487792L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  private UUID id;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private MovingAverageType type;

  @Positive
  @Column(name = "period", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.SMALLINT)
  private int period;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "apply", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private CandlestickApply apply;

  @PositiveOrZero
  @Column(name = "value_ma")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double value;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, targetEntity = Candlestick.class)
  @JoinColumn(name = "candlestick_id", nullable = false, updatable = false)
  @Exclude
  private Candlestick candlestick;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final MovingAverage that = (MovingAverage) o;
    return getPeriod() == that.getPeriod() && getType() == that.getType() && getApply() == that.getApply();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getType(), getPeriod(), getApply());
  }
}
