package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.enums.PriceType;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.mappers.MovingAverageMapper;
import lu.forex.system.repositories.MovingAverageRepository;
import lu.forex.system.services.MovingAverageService;
import lu.forex.system.utils.MathUtils;
import org.springframework.stereotype.Service;

@Service("exponentialMovingAverage")
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class ExponentialMovingAverageProvider implements MovingAverageService {

  @Getter(AccessLevel.PUBLIC)
  private final MovingAverageRepository movingAverageRepository;
  @Getter(AccessLevel.PUBLIC)
  private final MovingAverageMapper movingAverageMapper;
  private final CandlestickMapper candlestickMapper;

  @Override
  public MovingAverageType getMovingAverageType() {
    return MovingAverageType.EMA;
  }

  @Override
  public void calculateMovingAverage(final @NotNull List<@NotNull CandlestickDto> candlestickDtos) {
    final CandlestickDto candlestickDtosFirst = candlestickDtos.getFirst();
    final Candlestick currentCandlestick = this.getCandlestickMapper().toEntity(candlestickDtosFirst);
    final Collection<MovingAverage> collection = currentCandlestick.getMovingAverages().stream()
        .filter(movingAverage -> this.getMovingAverageType().equals(movingAverage.getType()))
        .map(ma -> this.getMovingAverageConsumer(ma, candlestickDtos, currentCandlestick)).toList();
    if (!collection.isEmpty()) {
      this.getMovingAverageRepository().saveAll(collection);
      collection.forEach(movingAverage -> candlestickDtosFirst.movingAverages().removeIf(ma -> ma.id().equals(movingAverage.getId())));
      final Collection<MovingAverageDto> updateDto = collection.stream()
          .map(movingAverage -> this.getMovingAverageMapper().toDto(movingAverage)).toList();
      candlestickDtosFirst.movingAverages().addAll(updateDto);
    }
  }

  @NotNull
  private MovingAverage getMovingAverageConsumer(final @NotNull MovingAverage movingAverage, final @NotNull List<CandlestickDto> candlesticksDesc,
      final @NotNull Candlestick currentCandlestick) {
    final int period = movingAverage.getPeriod();
    final PriceType candlestickApply = movingAverage.getPriceType();

    if (candlesticksDesc.size() > period) {
      final Optional<MovingAverageDto> lestMA = candlesticksDesc.get(1).movingAverages().stream()
          .filter(ma -> this.getMovingAverageType().equals(ma.type()) && period == ma.period() && candlestickApply.equals(ma.priceType()))
          .findFirst();

      if (lestMA.isPresent()) {
        final Double lastEma = lestMA.get().value();
        if (Objects.nonNull(lastEma)) {
          final double a = MathUtils.getMultiplication(candlestickApply.getPrice(currentCandlestick), this.getPercentagePrice(period));
          final double b = MathUtils.getSubtract(1, this.getPercentagePrice(period));
          final double c = MathUtils.getMultiplication(lastEma, b);
          final double ema = MathUtils.getSum(List.of(a, c));
          movingAverage.setValue(ema);
        }
      }
    } else if (candlesticksDesc.size() == period) {
      final List<Double> collection = candlesticksDesc.stream().map(candlestickApply::getPrice).toList();
      final double ema = MathUtils.getMed(collection);
      movingAverage.setValue(ema);
    }
    return movingAverage;
  }

  private double getPercentagePrice(final @Positive int period) {
    return MathUtils.getDivision(2, period + 1d);
  }
}
