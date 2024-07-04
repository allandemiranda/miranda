package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.enums.PriceType;
import lu.forex.system.mappers.MovingAverageMapper;
import lu.forex.system.repositories.MovingAverageRepository;
import lu.forex.system.services.MovingAverageService;
import lu.forex.system.utils.MathUtils;
import org.springframework.stereotype.Service;

@Getter
@Service("exponentialMovingAverage")
@RequiredArgsConstructor
public class ExponentialMovingAverageProvider implements MovingAverageService {

  private final MovingAverageRepository movingAverageRepository;
  private final MovingAverageMapper movingAverageMapper;

  @Override
  public MovingAverageType getMovingAverageType() {
    return MovingAverageType.EMA;
  }

  @Override
  public void calculateMovingAverage(final @NotNull CandlestickDto @NotNull [] candlesticksDesc) {
    final Collection<MovingAverage> collection = candlesticksDesc[0].movingAverages().stream()
        .filter(movingAverage -> this.getMovingAverageType().equals(movingAverage.type()))
        .map(movingAverageDto -> this.getMovingAverageMapper().toEntity(movingAverageDto))
        .map(movingAverage -> this.getMovingAverageConsumer(movingAverage, candlesticksDesc)).toList();
    this.getMovingAverageRepository().saveAll(collection);
  }

  @NotNull
  private MovingAverage getMovingAverageConsumer(final @NotNull MovingAverage movingAverage, final @NotNull CandlestickDto @NotNull [] candlesticksDesc) {
    final int period = movingAverage.getPeriod();
    final PriceType candlestickApply = movingAverage.getPriceType();

    if (candlesticksDesc.length > period) {
      final Optional<MovingAverageDto> lestMA = candlesticksDesc[1].movingAverages().stream()
          .filter(ma -> this.getMovingAverageType().equals(ma.type()) && period == ma.period() && candlestickApply.equals(ma.priceType()))
          .findFirst();

      if (lestMA.isPresent()) {
        final Double lastEma = lestMA.get().value();
        if (Objects.nonNull(lastEma)) {
          final double a = MathUtils.getMultiplication(candlestickApply.getPrice(candlesticksDesc[0]), this.getPercentagePrice(period));
          final double b = MathUtils.getSubtract(1, this.getPercentagePrice(period));
          final double c = MathUtils.getMultiplication(lastEma, b);
          final double ema = MathUtils.getSum(List.of(a, c));
          movingAverage.setValue(ema);
        }
      }
    } else if (candlesticksDesc.length == period) {
      final Collection<Double> collection = IntStream.range(0, period).parallel().mapToObj(i -> movingAverage.getPriceType().getPrice(candlesticksDesc[i])).toList();
      final double ema = MathUtils.getMed(collection);
      movingAverage.setValue(ema);
    }
    return movingAverage;
  }

  private double getPercentagePrice(final @Positive int period) {
    return MathUtils.getDivision(2, period + 1d);
  }
}
