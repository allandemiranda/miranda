package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
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
@Table(name = "ac_indicator")
public class AcIndicator extends Indicator implements Serializable {

  @Serial
  private static final long serialVersionUID = -780377920070703541L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @PositiveOrZero
  @Column(name = "mp", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double mp;

  @Column(name = "ao")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double ao;

  @Column(name = "ac")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double ac;

  @Column(name = "lest_ac")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double lestAc;

  @Column(name = "lest_color")
  @JdbcTypeCode(SqlTypes.BOOLEAN)
  private Boolean lestColor;

  @Exclude
  @OneToOne(mappedBy = "acIndicator", cascade = CascadeType.ALL, optional = false)
  private Candlestick candlestick;

  public Boolean getColor() {
    if (Objects.nonNull(this.getAc()) && Objects.nonNull(this.getLestAc())) {
      final BigDecimal acBigDecimal = BigDecimal.valueOf(this.getAc());
      final BigDecimal lestAcBigDecimal = BigDecimal.valueOf(this.getLestAc());
      return acBigDecimal.compareTo(lestAcBigDecimal) > 0;
    } else {
      return null;
    }
  }

  @Override
  public SignalIndicatorStatus getStatus() {
    if (Objects.nonNull(this.getAc()) && Objects.nonNull(this.getLestAc()) && Objects.nonNull(this.getLestColor())) {
      final BigDecimal acBigDecimal = BigDecimal.valueOf(this.getAc());
      final BigDecimal lestAcBigDecimal = BigDecimal.valueOf(this.getLestAc());
      if (acBigDecimal.compareTo(BigDecimal.ZERO) > 0 && this.getLestColor() && acBigDecimal.compareTo(lestAcBigDecimal) > 0) {
        return SignalIndicatorStatus.BUY;
      } else if (acBigDecimal.compareTo(BigDecimal.ZERO) < 0 && !this.getLestColor() && acBigDecimal.compareTo(lestAcBigDecimal) < 0) {
        return SignalIndicatorStatus.SELL;
      } else {
        return SignalIndicatorStatus.NEUTRAL;
      }
    } else {
      return null;
    }
  }

}
