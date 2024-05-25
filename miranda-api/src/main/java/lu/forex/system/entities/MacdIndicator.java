package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "macd_indicator")
public class MacdIndicator implements Serializable {

  @Serial
  private static final long serialVersionUID = 663517249615629883L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @Column(name = "fast_ema")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double fastEma;

  @Column(name = "slow_ema")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double slowEma;

  @Column(name = "macd")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double macd;
}
