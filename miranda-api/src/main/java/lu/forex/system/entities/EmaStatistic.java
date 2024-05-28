package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.utils.MathUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.transaction.annotation.Transactional;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "ema_statistic")
public class EmaStatistic implements Serializable {

  @Serial
  private static final long serialVersionUID = -9215994383059260651L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @Column(name = "period", nullable = false)
  @JdbcTypeCode(SqlTypes.SMALLINT)
  private int period;

  @Enumerated
  @Column(name = "candlestick_apply", nullable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private CandlestickApply candlestickApply;

  @Column(name = "ema")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double ema;

  @Column(name = "last_ema")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double lastEma;

  @Column(name = "symbol_name", nullable = false, length = 6, updatable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private String symbolName;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "time_frame", nullable = false, length = 3, updatable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private TimeFrame timeFrame;

  @NotNull
  @PastOrPresent
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "timestamp", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.TIMESTAMP)
  private LocalDateTime timestamp;

  @Transactional
  public double getPercentagePrice() {
    return MathUtils.getDivision(2, this.getPeriod() + 1L);
  }

}
