package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.NewMovingAverageDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.CandlestickBody;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.enums.Indicator;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.exceptions.TechnicalIndicatorNotFoundException;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.mappers.TechnicalIndicatorMapper;
import lu.forex.system.repositories.TechnicalIndicatorRepository;
import lu.forex.system.services.TechnicalIndicatorService;
import lu.forex.system.utils.MathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("averageDirectionalIndex")
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class AverageDirectionalIndexProvider implements TechnicalIndicatorService {

  private static final String KEY_ADX = "adx";
  private static final String KEY_P_DI_P = "+di(P)";
  private static final String KEY_N_DI_P = "-di(P)";
  private static final String KEY_TR_1 = "tr1";
  private static final String KEY_P_DM_1 = "+dm1";
  private static final String KEY_N_DM_1 = "-dm1";
  private static final String KEY_DX = "dx";

  @Value("${adx.parameters.period:14}")
  private int period;

  @Value("${adx.parameters.tendencyLine:50}")
  private int tendencyLine;

  @Getter(AccessLevel.PUBLIC)
  private final TechnicalIndicatorRepository technicalIndicatorRepository;
  private final CandlestickMapper candlestickMapper;
  @Getter(AccessLevel.PUBLIC)
  private final TechnicalIndicatorMapper technicalIndicatorMapper;

  @Override
  public Indicator getIndicator() {
    return Indicator.ADX;
  }

  @Override
  public int getNumberOfCandlesticksToCalculate() {
    return period;
  }

  @Override
  public @NotNull TechnicalIndicatorDto calculateTechnicalIndicator(final @NotNull List<CandlestickDto> candlestickDtos) {
    final Candlestick currentCandlestick = this.getCandlestickMapper().toEntity(candlestickDtos.getFirst());
    final List<TechnicalIndicatorDto> technicalIndicatorDtos = candlestickDtos.stream().limit(this.getPeriod()).map(c -> c.technicalIndicators().stream().filter(i -> this.getIndicator().equals(i.indicator())).findFirst().orElseThrow(() -> new TechnicalIndicatorNotFoundException(currentCandlestick.getScope().toString()))).toList();
    final TechnicalIndicatorDto currentTechnicalIndicatorDto = technicalIndicatorDtos.getFirst();

    if (candlestickDtos.size() > 1) {
      final CandlestickBody candlestickCandlestickBody = currentCandlestick.getBody();
      final BigDecimal cHigh = BigDecimal.valueOf(candlestickCandlestickBody.getHigh());
      final BigDecimal cLow = BigDecimal.valueOf(candlestickCandlestickBody.getLow());
      final BigDecimal cClose = BigDecimal.valueOf(candlestickCandlestickBody.getClose());

      final Candlestick lastCandlestick = this.getCandlestickMapper().toEntity(candlestickDtos.get(1));
      final CandlestickBody lastCandlestickCandlestickBody = lastCandlestick.getBody();
      final BigDecimal lastClose = BigDecimal.valueOf(lastCandlestickCandlestickBody.getClose());
      final BigDecimal lastHigh = BigDecimal.valueOf(lastCandlestickCandlestickBody.getHigh());
      final BigDecimal lastLow = BigDecimal.valueOf(lastCandlestickCandlestickBody.getLow());

      // get TR1
      final double trOne = MathUtils.getMax(cHigh.subtract(cLow).doubleValue(), cHigh.subtract(cClose).doubleValue(), Math.abs(cLow.subtract(lastClose).doubleValue()));
      currentTechnicalIndicatorDto.data().put(KEY_TR_1, trOne);

      // get +DM1
      final double pDmOne = cHigh.subtract(lastHigh).compareTo(lastLow.subtract(cLow)) > 0 ? MathUtils.getMax(cHigh.subtract(lastHigh).doubleValue(), 0d) : 0d;
      currentTechnicalIndicatorDto.data().put(KEY_P_DM_1, pDmOne);

      // get -DM1
      final double nDmOne = lastLow.subtract(cLow).compareTo(cHigh.subtract(lastHigh)) > 0 ? MathUtils.getMax(lastLow.subtract(cLow).doubleValue(), 0d) : 0d;
      currentTechnicalIndicatorDto.data().put(KEY_N_DM_1, nDmOne);

      final Collection<double[]> collectionOne = technicalIndicatorDtos.stream()
          .limit(this.getPeriod())
          .filter(tiDto -> Objects.nonNull(tiDto.data().get(KEY_TR_1)) && Objects.nonNull(tiDto.data().get(KEY_P_DM_1)) && Objects.nonNull(tiDto.data().get(KEY_N_DM_1)))
          .map(tiDto -> new double[]{tiDto.data().get(KEY_TR_1), tiDto.data().get(KEY_P_DM_1), tiDto.data().get(KEY_N_DM_1)}).toList();
      if (collectionOne.size() == this.getPeriod()) {
        // get TR(P)
        final double trP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[0]).toList());

        // get +DM(P)
        final double pDmP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[1]).toList());

        // get -DM(P)
        final double nDmP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[2]).toList());

        // get +DI(P)
        final double pDiP = MathUtils.getMultiplication(100, MathUtils.getDivision(pDmP, trP));
        currentTechnicalIndicatorDto.data().put(KEY_P_DI_P, pDiP);

        // get -DI(P)
        final double nDiP = MathUtils.getMultiplication(100, MathUtils.getDivision(nDmP, trP));
        currentTechnicalIndicatorDto.data().put(KEY_N_DI_P, nDiP);

        // get DI diff
        final double diDiff = Math.abs(BigDecimal.valueOf(pDiP).subtract(BigDecimal.valueOf(nDiP)).doubleValue());

        // get DI sum
        final double diSum = BigDecimal.valueOf(pDiP).add(BigDecimal.valueOf(nDiP)).doubleValue();

        // get DX
        final double dx = MathUtils.getMultiplication(100, MathUtils.getDivision(diDiff, diSum));
        currentTechnicalIndicatorDto.data().put(KEY_DX, dx);

        final Collection<Double> collectionDx = technicalIndicatorDtos.stream()
            .limit(this.getPeriod())
            .filter(tiDto -> Objects.nonNull(tiDto.data().get(KEY_DX)))
            .map(tiDto -> tiDto.data().get(KEY_DX))
            .toList();
        if (collectionDx.size() == this.getPeriod()) {
          // get ADX
          final double adx = MathUtils.getMed(collectionDx);
          currentTechnicalIndicatorDto.data().put(KEY_ADX, adx);
        }
      }
    }

    final TechnicalIndicator technicalIndicator = this.getTechnicalIndicatorMapper().toEntity(currentTechnicalIndicatorDto);
    technicalIndicator.setSignal(this.processingSignal(technicalIndicatorDtos));
    final TechnicalIndicator saved = this.getTechnicalIndicatorRepository().save(technicalIndicator);
    return this.getTechnicalIndicatorMapper().toDto(saved);
  }

  private SignalIndicator processingSignal(final @NotNull List<TechnicalIndicatorDto> technicalIndicatorDtos) {
    if(technicalIndicatorDtos.size() > 1) {
      final TechnicalIndicatorDto currentTechnicalIndicatorDto = technicalIndicatorDtos.getFirst();
      final Double adx = currentTechnicalIndicatorDto.data().get(KEY_ADX);
      final Double pDiP = currentTechnicalIndicatorDto.data().get(KEY_P_DI_P);
      final Double nDiP = currentTechnicalIndicatorDto.data().get(KEY_N_DI_P);
      if (Objects.nonNull(adx) && Objects.nonNull(pDiP) && Objects.nonNull(nDiP)) {
        final BigDecimal adxBigDecimal = BigDecimal.valueOf(adx);
        if (adxBigDecimal.compareTo(BigDecimal.valueOf(this.getTendencyLine())) >= 0) {
          final BigDecimal pDiBigDecimal = BigDecimal.valueOf(pDiP);
          final BigDecimal nDiBigDecimal = BigDecimal.valueOf(nDiP);
          if (pDiBigDecimal.compareTo(nDiBigDecimal) > 0) {
            return SignalIndicator.BULLISH;
          }
          if (pDiBigDecimal.compareTo(nDiBigDecimal) < 0) {
            return SignalIndicator.BEARISH;
          }
        }
      }
    }
    return SignalIndicator.NEUTRAL;
  }
}
