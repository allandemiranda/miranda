package lu.forex.system.mappers.impls;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickBodyDto;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.CandlestickBody;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.mappers.ScopeMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickMapperImpl implements CandlestickMapper {

  private final ScopeMapper scopeMapper;

  @Override
  public @NotNull Candlestick toEntity(final @NotNull CandlestickDto candlestickDto) {
    final var candlestick = new Candlestick();
    candlestick.setId(candlestickDto.id());
    final var scope = this.getScopeMapper().toEntity(candlestickDto.scope());
    candlestick.setScope(scope);
    candlestick.setTimestamp(candlestickDto.timestamp());
    final var body = this.candlestickBodyDtoToCandlestickBody(candlestickDto.body());
    candlestick.setBody(body);
    final var movingAverages = this.movingAverageDtoSetToMovingAverageSet(candlestickDto.movingAverages());
    candlestick.setMovingAverages(movingAverages);
    final var technicalIndicators = this.technicalIndicatorDtoSetToTechnicalIndicatorSet(candlestickDto.technicalIndicators());
    candlestick.setTechnicalIndicators(technicalIndicators);
    candlestick.setSignalIndicator(candlestickDto.signalIndicator());
    return candlestick;
  }

  @Override
  public @NotNull CandlestickDto toDto(final @NotNull Candlestick candlestick) {
    final var id = candlestick.getId();
    final var scope = this.getScopeMapper().toDto(candlestick.getScope());
    final var timestamp = candlestick.getTimestamp();
    final var body = this.candlestickBodyToCandlestickBodyDto(candlestick.getBody());
    final var movingAverages = this.movingAverageSetToMovingAverageDtoSet(candlestick.getMovingAverages());
    final var technicalIndicators = this.technicalIndicatorSetToTechnicalIndicatorDtoSet(candlestick.getTechnicalIndicators());
    final var signalIndicator = candlestick.getSignalIndicator();
    return new CandlestickDto(id, scope, timestamp, body, movingAverages, technicalIndicators, signalIndicator);
  }

  private @NotNull CandlestickBody candlestickBodyDtoToCandlestickBody(final @NotNull CandlestickBodyDto candlestickBodyDto) {
    final var candlestickBody = new CandlestickBody();
    candlestickBody.setHigh(candlestickBodyDto.high());
    candlestickBody.setLow(candlestickBodyDto.low());
    candlestickBody.setOpen(candlestickBodyDto.open());
    candlestickBody.setClose(candlestickBodyDto.close());
    return candlestickBody;
  }

  private @NotNull MovingAverage movingAverageDtoToMovingAverage(final @NotNull MovingAverageDto movingAverageDto) {
    final var movingAverage = new MovingAverage();
    movingAverage.setId(movingAverageDto.id());
    movingAverage.setType(movingAverageDto.type());
    movingAverage.setPeriod(movingAverageDto.period());
    movingAverage.setPriceType(movingAverageDto.priceType());
    movingAverage.setValue(movingAverageDto.value());
    return movingAverage;
  }

  private @NotNull Set<@NotNull MovingAverage> movingAverageDtoSetToMovingAverageSet(final @NotNull Set<@NotNull MovingAverageDto> set) {
    return set.parallelStream().map(this::movingAverageDtoToMovingAverage).collect(Collectors.toSet());
  }

  private @NotNull TechnicalIndicator technicalIndicatorDtoToTechnicalIndicator(final @NotNull TechnicalIndicatorDto technicalIndicatorDto) {
    final var technicalIndicator = new TechnicalIndicator();
    technicalIndicator.setId(technicalIndicatorDto.id());
    technicalIndicator.setIndicator(technicalIndicatorDto.indicator());
    technicalIndicator.setData(technicalIndicatorDto.data());
    technicalIndicator.setSignal(technicalIndicatorDto.signal());
    return technicalIndicator;
  }

  private @NotNull Set<@NotNull TechnicalIndicator> technicalIndicatorDtoSetToTechnicalIndicatorSet(
      final @NotNull Set<@NotNull TechnicalIndicatorDto> set) {
    return set.parallelStream().map(this::technicalIndicatorDtoToTechnicalIndicator).collect(Collectors.toSet());
  }

  private @NotNull CandlestickBodyDto candlestickBodyToCandlestickBodyDto(final @NotNull CandlestickBody candlestickBody) {
    final var high = candlestickBody.getHigh();
    final var low = candlestickBody.getLow();
    final var open = candlestickBody.getOpen();
    final var close = candlestickBody.getClose();
    return new CandlestickBodyDto(high, low, open, close);
  }

  private @NotNull MovingAverageDto movingAverageToMovingAverageDto(final @NotNull MovingAverage movingAverage) {
    final var id = movingAverage.getId();
    final var type = movingAverage.getType();
    final var period = movingAverage.getPeriod();
    final var priceType = movingAverage.getPriceType();
    final var value = movingAverage.getValue();
    return new MovingAverageDto(id, type, period, priceType, value);
  }

  private @NotNull Set<@NotNull MovingAverageDto> movingAverageSetToMovingAverageDtoSet(final @NotNull Set<@NotNull MovingAverage> set) {
    return set.parallelStream().map(this::movingAverageToMovingAverageDto).collect(Collectors.toSet());
  }

  private @NotNull TechnicalIndicatorDto technicalIndicatorToTechnicalIndicatorDto(final @NotNull TechnicalIndicator technicalIndicator) {
    final var id = technicalIndicator.getId();
    final var indicator = technicalIndicator.getIndicator();
    final var data = technicalIndicator.getData();
    final var signal = technicalIndicator.getSignal();
    return new TechnicalIndicatorDto(id, indicator, data, signal);
  }

  private @NotNull Set<@NotNull TechnicalIndicatorDto> technicalIndicatorSetToTechnicalIndicatorDtoSet(
      final @NotNull Set<@NotNull TechnicalIndicator> set) {
    return set.parallelStream().map(this::technicalIndicatorToTechnicalIndicatorDto).collect(Collectors.toSet());
  }
}
