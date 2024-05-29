package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
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

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "ac_indicator", indexes = {@Index(name = "idx_acindicator_id_unq", columnList = "id", unique = true)}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_acindicator_id", columnNames = {"id", "lest_ac_indicator_id"})})
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

  @Exclude
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
  @JoinColumn(name = "lest_ac_indicator_id", nullable = false, unique = true, updatable = false)
  private AcIndicator lestAcIndicator;

  @Exclude
  @OneToOne(mappedBy = "acIndicator", cascade = CascadeType.ALL, optional = false)
  private Candlestick candlestick;

  public Boolean getColor() {
    if (Objects.nonNull(this.getAc()) && Objects.nonNull(this.getLestAcIndicator().getAc())) {
      final BigDecimal acBigDecimal = BigDecimal.valueOf(this.getAc());
      final BigDecimal lestAcBigDecimal = BigDecimal.valueOf(this.getLestAcIndicator().getAc());
      return acBigDecimal.compareTo(lestAcBigDecimal) > 0;
    } else {
      return null;
    }
  }

  @Override
  public int numberOfCandlesticksToCalculate() {
    return 34;
  }

  @Override
  public void calculateIndicator(final @NotNull Collection<Candlestick> lastCandlesticks) {
    // set the MP value
    this.setMp(CandlestickApply.TYPICAL_PRICE.getPrice(this.getCandlestick()));

    final Collection<Double> collectionSmaMp34 = lastCandlesticks.stream().limit(34).map(c -> c.getAcIndicator().getMp()).toList();
    if (collectionSmaMp34.size() != 34) {
      this.setAo(null);
      this.setAc(null);
    } else {
      // get SMA(MP,34)
      final double smaMp34 = MathUtils.getMed(collectionSmaMp34);

      // get SMA(MP,5)
      final double smaMp5 = MathUtils.getMed(lastCandlesticks.stream().limit(5).map(c -> c.getAcIndicator().getMp()).toList());

      // get SMA(MP,5) - SMA(MP,34)
      this.setAo(BigDecimal.valueOf(smaMp5).subtract(BigDecimal.valueOf(smaMp34)).doubleValue());

      // get SMA(ao,5)
      final Collection<Double> collectionSmaAo5 = lastCandlesticks.stream().limit(5).filter(c -> !c.getId().equals(this.getCandlestick().getId()))
          .filter(c -> Objects.nonNull(c.getAcIndicator().getAo())).map(c -> c.getAcIndicator().getAo())
          .collect(Collectors.toCollection(ArrayList::new));
      if (collectionSmaAo5.size() == 4) {
        collectionSmaAo5.add(this.getAo());
        final double smaAo5 = MathUtils.getMed(collectionSmaAo5);

        // get ao - SMA(ao,5)
        this.setAc(BigDecimal.valueOf(this.getAo()).subtract(BigDecimal.valueOf(smaAo5)).doubleValue());
      } else {
        this.setAc(null);
      }
    }
  }

  @Override
  public boolean isReadyToGetStatus() {
    return Objects.nonNull(this.getAc()) && Objects.nonNull(this.getStatus());
  }

  @Override
  public SignalIndicatorStatus getStatus() {
    final BigDecimal acBigDecimal = BigDecimal.valueOf(this.getAc());
    final BigDecimal lestAcBigDecimal = BigDecimal.valueOf(this.getLestAcIndicator().getAc());
    if (acBigDecimal.compareTo(BigDecimal.ZERO) > 0 && this.getLestAcIndicator().getColor() && acBigDecimal.compareTo(lestAcBigDecimal) > 0) {
      return SignalIndicatorStatus.BUY;
    } else if (acBigDecimal.compareTo(BigDecimal.ZERO) < 0 && !this.getLestAcIndicator().getColor() && acBigDecimal.compareTo(lestAcBigDecimal) < 0) {
      return SignalIndicatorStatus.SELL;
    } else {
      return SignalIndicatorStatus.NEUTRAL;
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final AcIndicator that = (AcIndicator) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
