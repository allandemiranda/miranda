package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
  public @NotNull TechnicalIndicatorDto calculateTechnicalIndicator(final @NotNull List<CandlestickDto> candlestickDtos) {
    final CandlestickDto currentCandlestick = candlestickDtos.getFirst();
    final List<TechnicalIndicatorDto> technicalIndicatorDtos = candlestickDtos.stream().limit(34).parallel().map(
        c -> c.technicalIndicators().stream().filter(i -> this.getIndicator().equals(i.indicator())).findFirst()
            .orElseThrow(() -> new TechnicalIndicatorNotFoundException(currentCandlestick.scope().toString()))).toList();
    final TechnicalIndicatorDto currentTechnicalIndicatorDto = technicalIndicatorDtos.getFirst();

    // set the MP value
    final double mp = PriceType.TYPICAL_PRICE.getPrice(currentCandlestick);
    currentTechnicalIndicatorDto.data().put(KEY_MP, mp);

    if (technicalIndicatorDtos.size() == 34) {
      // get SMA(MP,34)
      final Collection<Double> collectionSmaMp34 = technicalIndicatorDtos.parallelStream().map(ti -> ti.data().get(KEY_MP)).toList();
      final double smaMp34 = MathUtils.getMed(collectionSmaMp34);

      // get SMA(MP,5)
      final Collection<Double> collectionMp5 = technicalIndicatorDtos.stream().limit(5).parallel().map(ti -> ti.data().get(KEY_MP)).toList();
      final double smaMp5 = MathUtils.getMed(collectionMp5);

      // get SMA(MP,5) - SMA(MP,34)
      final double ao = BigDecimal.valueOf(smaMp5).subtract(BigDecimal.valueOf(smaMp34)).doubleValue();
      currentTechnicalIndicatorDto.data().put(KEY_AO, ao);

      // get SMA(ao,5)
      final Collection<Double> collectionSmaAo5 = technicalIndicatorDtos.stream().limit(5).parallel().filter(ti -> Objects.nonNull(ti.data().get(KEY_AO)))
          .map(ti -> ti.data().get(KEY_AO)).toList();
      if (collectionSmaAo5.size() == 4) {
        final double smaAo5 = MathUtils.getMed(collectionSmaAo5);

        // get ao - SMA(ao,5)
        final double ac = BigDecimal.valueOf(ao).subtract(BigDecimal.valueOf(smaAo5)).doubleValue();
        currentTechnicalIndicatorDto.data().put(KEY_AO, ac);
      }
    }

    final TechnicalIndicator technicalIndicator = this.getTechnicalIndicatorMapper().toEntity(currentTechnicalIndicatorDto);
    technicalIndicator.setSignal(this.processingSignal(technicalIndicatorDtos));
    final TechnicalIndicator saved = this.getTechnicalIndicatorRepository().save(technicalIndicator);
    return this.getTechnicalIndicatorMapper().toDto(saved);
  }

  private SignalIndicator processingSignal(final @NotNull List<TechnicalIndicatorDto> technicalIndicatorDtos) {
    if (technicalIndicatorDtos.size() >= 2) {
      final Double currentAc = technicalIndicatorDtos.getFirst().data().get(KEY_AC);
      final Double lestAc = technicalIndicatorDtos.get(1).data().get(KEY_AC);
      if (Objects.nonNull(currentAc) && Objects.nonNull(lestAc)) {
        final BigDecimal acBigDecimal = BigDecimal.valueOf(currentAc);
        final int compared = BigDecimal.valueOf(currentAc).compareTo(BigDecimal.valueOf(lestAc));
        if (acBigDecimal.compareTo(BigDecimal.ZERO) > 0 && compared > 0) {
          return SignalIndicator.BULLISH;
        } else if (acBigDecimal.compareTo(BigDecimal.ZERO) < 0 && compared < 0) {
          return SignalIndicator.BEARISH;
        } else {
          return SignalIndicator.NEUTRAL;
        }
      }
    }
    return SignalIndicator.NEUTRAL;
  }
}
