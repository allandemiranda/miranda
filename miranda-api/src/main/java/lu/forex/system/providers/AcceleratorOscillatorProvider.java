package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.enums.Indicator;
import lu.forex.system.enums.PriceType;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.exceptions.TechnicalIndicatorNotFoundException;
import lu.forex.system.mappers.TechnicalIndicatorMapper;
import lu.forex.system.repositories.TechnicalIndicatorRepository;
import lu.forex.system.services.TechnicalIndicatorService;
import lu.forex.system.utils.MathUtils;
import org.springframework.stereotype.Service;

@Service("acceleratorOscillator")
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class AcceleratorOscillatorProvider implements TechnicalIndicatorService {

  private static final String KEY_AC = "ac";
  private static final String KEY_AO = "ao";
  private static final String KEY_MP = "mp";

  @Getter(AccessLevel.PUBLIC)
  private final TechnicalIndicatorRepository technicalIndicatorRepository;
  @Getter(AccessLevel.PUBLIC)
  private final TechnicalIndicatorMapper technicalIndicatorMapper;

  @Override
  public Indicator getIndicator() {
    return Indicator.AC;
  }

  @Override
  public int getNumberOfCandlesticksToCalculate() {
    return 34;
  }

  @Override
  public void calculateTechnicalIndicator(final @NotNull CandlestickDto @NotNull [] candlesticksDesc) {
    final TechnicalIndicatorDto[] technicalIndicatorDtos = IntStream.range(0, candlesticksDesc.length < 34 ? 1 : 34)
        .mapToObj(i -> candlesticksDesc[i].technicalIndicators().stream()
            .filter(cIndicator -> this.getIndicator().equals(cIndicator.indicator())).findFirst()
            .orElseThrow(() -> new TechnicalIndicatorNotFoundException(candlesticksDesc[i].scope().toString())))
        .toArray(TechnicalIndicatorDto[]::new);

    // set the MP value
    final double mp = PriceType.TYPICAL_PRICE.getPrice(candlesticksDesc[0]);
    technicalIndicatorDtos[0].data().put(KEY_MP, mp);

    if (technicalIndicatorDtos.length == 34) {
      // get SMA(MP,34)
      final Collection<Double> collectionSmaMp34 = IntStream.range(0, technicalIndicatorDtos.length).parallel().mapToObj(i -> technicalIndicatorDtos[i].data().get(KEY_MP)).toList();
      final double smaMp34 = MathUtils.getMed(collectionSmaMp34);

      // get SMA(MP,5)
      final Collection<Double> collectionMp5 = IntStream.range(0, 5).parallel().mapToObj(i -> technicalIndicatorDtos[i].data().get(KEY_MP)).toList();
      final double smaMp5 = MathUtils.getMed(collectionMp5);

      // get SMA(MP,5) - SMA(MP,34)
      final double ao = BigDecimal.valueOf(smaMp5).subtract(BigDecimal.valueOf(smaMp34)).doubleValue();
      technicalIndicatorDtos[0].data().put(KEY_AO, ao);

      // get SMA(ao,5)
      if (IntStream.range(0, 5).noneMatch(i -> Objects.isNull(technicalIndicatorDtos[i].data().get(KEY_AO)))) {
        final Collection<Double> collectionSmaAo5 = IntStream.range(0, 5).parallel().mapToObj(i -> technicalIndicatorDtos[i].data().get(KEY_AO)).toList();
        final double smaAo5 = MathUtils.getMed(collectionSmaAo5);

        // get ao - SMA(ao,5)
        final double ac = BigDecimal.valueOf(ao).subtract(BigDecimal.valueOf(smaAo5)).doubleValue();
        technicalIndicatorDtos[0].data().put(KEY_AC, ac);
      }
    }

    final TechnicalIndicator technicalIndicator = this.getTechnicalIndicatorMapper().toEntity(technicalIndicatorDtos[0]);
    if(technicalIndicatorDtos.length >= 3) {
      technicalIndicator.setSignal(this.processingSignal(technicalIndicatorDtos[0], technicalIndicatorDtos[1], technicalIndicatorDtos[2]));
    } else {
      technicalIndicator.setSignal(SignalIndicator.NEUTRAL);
    }
    this.getTechnicalIndicatorRepository().save(technicalIndicator);
  }

  private SignalIndicator processingSignal(final @NotNull TechnicalIndicatorDto currentTechnicalIndicatorDto, final @NotNull TechnicalIndicatorDto lastTechnicalIndicatorDto, final @NotNull TechnicalIndicatorDto beforeLastTechnicalIndicatorDto) {
    final Double currentAc = currentTechnicalIndicatorDto.data().get(KEY_AC);
    final Double lestAc = lastTechnicalIndicatorDto.data().get(KEY_AC);
    final Double beforeLastAc = beforeLastTechnicalIndicatorDto.data().get(KEY_AC);
    if (Objects.nonNull(currentAc) && Objects.nonNull(lestAc) && Objects.nonNull(beforeLastAc)) {
      final BigDecimal currentAcBigDecimal = BigDecimal.valueOf(currentAc);
      final BigDecimal lestAcBigDecimal = BigDecimal.valueOf(lestAc);
      if (currentAcBigDecimal.compareTo(BigDecimal.ZERO) > 0 && BigDecimal.valueOf(beforeLastAc).compareTo(lestAcBigDecimal) > 0 && lestAcBigDecimal.compareTo(currentAcBigDecimal) > 0) {
        return SignalIndicator.BULLISH;
      } else if (currentAcBigDecimal.compareTo(BigDecimal.ZERO) < 0 && BigDecimal.valueOf(beforeLastAc).compareTo(lestAcBigDecimal) < 0 && lestAcBigDecimal.compareTo(currentAcBigDecimal) < 0) {
        return SignalIndicator.BEARISH;
      }
    }
    return SignalIndicator.NEUTRAL;
  }
}
