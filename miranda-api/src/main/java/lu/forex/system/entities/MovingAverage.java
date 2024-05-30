package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
  private static final long serialVersionUID = -9215994383059260651L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, unique = true)
  @JdbcTypeCode(SqlTypes.UUID)
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
  private CandlestickApply candlestickApply;

  @PositiveOrZero
  @Column(name = "value_ma")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double value;

}
