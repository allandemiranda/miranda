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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
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
@Table(name = "adx_indicator", indexes = {@Index(name = "idx_adxindicator_id_unq", columnList = "id", unique = true)}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_adxindicator_id", columnNames = {"id"})})
public class AdxIndicator extends Indicator implements Serializable {

  @Serial
  private static final long serialVersionUID = -9215994383059260651L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @Column(name = "tr_one")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double trOne;

  @Column(name = "p_dm_one")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double pDmOne;

  @Column(name = "n_dm_one")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double nDmOne;

  @Column(name = "p_di_p")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double pDiP;

  @Column(name = "n_di_p")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double nDiP;

  @Column(name = "dx")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double dx;

  @Column(name = "adx")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double adx;

  @Exclude
  @OneToOne(mappedBy = "adxIndicator", cascade = CascadeType.ALL, optional = false)
  private Candlestick candlestick;

  @Transient
  @Value("${adx.parameters.period:14}")
  private int period;

  @Transient
  @Value("${adx.parameters.tendencyLine:50}")
  private int tendencyLine;

  @Override
  public SignalIndicatorStatus getStatus() {
    final BigDecimal adxBigDecimal = BigDecimal.valueOf(this.getAdx());
    if (adxBigDecimal.compareTo(BigDecimal.valueOf(this.getTendencyLine())) >= 0) {
      final BigDecimal pDiBigDecimal = BigDecimal.valueOf(this.getPDiP());
      final BigDecimal nDiBigDecimal = BigDecimal.valueOf(this.getNDiP());
      if (pDiBigDecimal.compareTo(nDiBigDecimal) > 0) {
        return SignalIndicatorStatus.BUY;
      }
      if (pDiBigDecimal.compareTo(nDiBigDecimal) < 0) {
        return SignalIndicatorStatus.SELL;
      }
    }
    return SignalIndicatorStatus.NEUTRAL;
  }

  @Override
  public int numberOfCandlesticksToCalculate() {
    return this.getPeriod();
  }

  @Override
  public void calculateIndicator(final @NotNull Collection<Candlestick> lastCandlesticks) {
    final Collection<Candlestick> collection = lastCandlesticks.stream().limit(2).filter(c -> !c.getId().equals(this.getCandlestick().getId()))
        .toList();
    if (collection.size() == 1) {
      final BigDecimal cHigh = BigDecimal.valueOf(this.getCandlestick().getHigh());
      final BigDecimal cLow = BigDecimal.valueOf(this.getCandlestick().getLow());
      final BigDecimal cClose = BigDecimal.valueOf(this.getCandlestick().getClose());

      final Candlestick lastCandlestick = collection.iterator().next();
      final BigDecimal lastClose = BigDecimal.valueOf(lastCandlestick.getClose());
      final BigDecimal lastHigh = BigDecimal.valueOf(lastCandlestick.getHigh());
      final BigDecimal lastLow = BigDecimal.valueOf(lastCandlestick.getLow());

      // get TR1
      this.setTrOne(MathUtils.getMax(cHigh.subtract(cLow).doubleValue(), cHigh.subtract(cClose).doubleValue(),
          Math.abs(cLow.subtract(lastClose).doubleValue())));

      // get +DM1
      this.setPDmOne(
          cHigh.subtract(lastHigh).compareTo(lastLow.subtract(cLow)) > 0 ? MathUtils.getMax(cHigh.subtract(lastHigh).doubleValue(), 0d) : 0d);

      // get -DM1
      this.setNDmOne(
          lastLow.subtract(cLow).compareTo(cHigh.subtract(lastHigh)) > 0 ? MathUtils.getMax(lastLow.subtract(cLow).doubleValue(), 0d) : 0d);

      final Collection<double[]> collectionOne = lastCandlesticks.stream().limit(this.getPeriod())
          .filter(c -> !c.getId().equals(this.getCandlestick().getId())).filter(
              c -> Objects.nonNull(c.getAdxIndicator().getTrOne()) && Objects.nonNull(c.getAdxIndicator().getPDmOne()) && Objects.nonNull(
                  c.getAdxIndicator().getNDmOne()))
          .map(c -> new double[]{c.getAdxIndicator().getTrOne(), c.getAdxIndicator().getPDmOne(), c.getAdxIndicator().getNDmOne()})
          .collect(Collectors.toCollection(ArrayList::new));
      if (collectionOne.size() == this.getPeriod() - 1) {
        collectionOne.add(new double[]{this.getTrOne(), this.getPDmOne(), this.getNDmOne()});
        // get TR(P)
        final double trP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[0]).toList());

        // get +DM(P)
        final double pDmP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[1]).toList());

        // get -DM(P)
        final double nDmP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[2]).toList());

        // get +DI(P)
        this.setPDiP(MathUtils.getMultiplication(100, MathUtils.getDivision(pDmP, trP)));

        // get -DI(P)
        this.setNDiP(MathUtils.getMultiplication(100, MathUtils.getDivision(nDmP, trP)));

        // get DI diff
        final double diDiff = Math.abs(BigDecimal.valueOf(this.getPDiP()).subtract(BigDecimal.valueOf(this.getNDiP())).doubleValue());

        // get DI sum
        final double diSum = BigDecimal.valueOf(this.getPDiP()).add(BigDecimal.valueOf(this.getNDiP())).doubleValue();

        // get DX
        this.setDx(MathUtils.getMultiplication(100, MathUtils.getDivision(diDiff, diSum)));

        final Collection<Double> collectionDx = lastCandlesticks.stream().limit(this.getPeriod())
            .filter(c -> !c.getId().equals(this.getCandlestick().getId())).filter(c -> Objects.nonNull(c.getAdxIndicator().getDx()))
            .map(c -> c.getAdxIndicator().getDx()).collect(Collectors.toCollection(ArrayList::new));
        if (collectionDx.size() == this.getPeriod() - 1) {
          collectionDx.add(this.getDx());
          // get ADX
          this.setAdx(MathUtils.getMed(collectionDx));
        }
      }
    }
  }

  @Override
  public boolean isReadyToGetStatus() {
    return Objects.nonNull(this.getAdx()) && Objects.nonNull(this.getPDiP()) && Objects.nonNull(this.getNDiP());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final AdxIndicator that = (AdxIndicator) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
