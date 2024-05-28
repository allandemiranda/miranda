package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lu.forex.system.enums.SignalIndicatorStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "macd_indicator")
public class MacdIndicator extends Indicator implements Serializable {

  @Serial
  private static final long serialVersionUID = 663517249615629883L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @Column(name = "macd")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double macd;

  @Column(name = "signal")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double signal;

  @Exclude
  @OneToOne(mappedBy = "macdIndicator", cascade = CascadeType.ALL, optional = false)
  private Candlestick candlestick;

  @Override
  public SignalIndicatorStatus getStatus() {
    if(Objects.nonNull(this.getSignal()) && Objects.nonNull(this.getMacd())) {
      final BigDecimal signalBigDecimal = BigDecimal.valueOf(this.getSignal());
      final BigDecimal macdBigDecimal = BigDecimal.valueOf(this.getMacd());
      if (signalBigDecimal.compareTo(macdBigDecimal) > 0) {
        return SignalIndicatorStatus.SELL;
      } else if (signalBigDecimal.compareTo(macdBigDecimal) < 0) {
        return SignalIndicatorStatus.BUY;
      } else {
        return SignalIndicatorStatus.NEUTRAL;
      }
    } else {
      return null;
    }
  }
}
