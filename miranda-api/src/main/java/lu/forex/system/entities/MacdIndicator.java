package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.SignalIndicatorStatus;
import lu.forex.system.utils.MathUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "macd_indicator", indexes = {@Index(name = "idx_macdindicator_id_unq", columnList = "id", unique = true)}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_macdindicator_id", columnNames = {"id"})})
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

  @Transient
  @Value("${macd.parameters.fast.period:12}")
  private int fastPeriod;

  @Transient
  @Value("${macd.parameters.slow.period:26}")
  private int slowPeriod;

  @Transient
  @Value("${macd.parameters.macd.period:9}")
  private int macdPeriod;

  @Transient
  @Value("${macd.parameters.ema.apply:CLOSE}")
  private CandlestickApply emaApply;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final MacdIndicator that = (MacdIndicator) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }

  @Override
  public int numberOfCandlesticksToCalculate() {
    return IntStream.of(this.getFastPeriod(), this.getSlowPeriod(), this.getMacdPeriod()).max().getAsInt();
  }

  @Override
  public void calculateIndicator(@NotNull final Collection<Candlestick> lastCandlesticks) {
    final EmaStatistic emaFast = this.getCandlestick().getEmaStatistics().stream().filter(
            ema -> ema.getPeriod() == this.getFastPeriod() && this.getEmaApply().equals(ema.getCandlestickApply()) && Objects.nonNull(ema.getEma()))
        .findFirst().orElse(null);
    final EmaStatistic emaSlow = this.getCandlestick().getEmaStatistics().stream().filter(
            ema -> ema.getPeriod() == this.getSlowPeriod() && this.getEmaApply().equals(ema.getCandlestickApply()) && Objects.nonNull(ema.getEma()))
        .findFirst().orElse(null);
    if (Objects.nonNull(emaFast) && Objects.nonNull(emaSlow)) {
      this.setMacd(MathUtils.getSubtract(emaFast.getEma(), emaSlow.getEma()));

      final Collection<Double> collectionMacd = lastCandlesticks.stream().limit(this.getMacdPeriod())
          .filter(c -> !c.getId().equals(this.getCandlestick().getId())).filter(c -> Objects.nonNull(c.getMacdIndicator().getMacd()))
          .map(c -> c.getMacdIndicator().getMacd()).collect(Collectors.toCollection(ArrayList::new));
      if (collectionMacd.size() == 8) {
        collectionMacd.add(this.getMacd());
        this.setSignal(MathUtils.getMed(collectionMacd));
      }
    }
  }

  @Override
  public boolean isReadyToGetStatus() {
    return Objects.nonNull(this.getSignal()) && Objects.nonNull(this.getMacd());
  }

  @Override
  public SignalIndicatorStatus getStatus() {
    final BigDecimal signalBigDecimal = BigDecimal.valueOf(this.getSignal());
    final BigDecimal macdBigDecimal = BigDecimal.valueOf(this.getMacd());
    if (signalBigDecimal.compareTo(macdBigDecimal) > 0) {
      return SignalIndicatorStatus.SELL;
    } else if (signalBigDecimal.compareTo(macdBigDecimal) < 0) {
      return SignalIndicatorStatus.BUY;
    } else {
      return SignalIndicatorStatus.NEUTRAL;
    }
  }
}
