package lu.forex.system.batch;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lu.forex.system.batch.model.MovingAverageBatch;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.utils.BatchUtils;
import lu.forex.system.utils.MathUtils;
import org.springframework.stereotype.Service;

@Service
public class EmaMovingAverageBatch extends MovingAverageBatch {

  private double getPercentagePrice(final int period) {
    return MathUtils.getDivision(2, period + 1d);
  }

  @Override
  public void calculateMovingAverage(final @NotNull List<Candlestick> candlesticksDesc) {
    final Candlestick currentCandlestick = BatchUtils.getCurrentCandlestick(candlesticksDesc);
    currentCandlestick.getMovingAverages().stream()
        .filter(movingAverage -> this.getMovingAverageType().equals(movingAverage.getType()))
        .forEach(ma -> this.getMovingAverageConsumer(ma, candlesticksDesc, currentCandlestick));
  }

  private void getMovingAverageConsumer(final @NotNull MovingAverage movingAverage, final @NotNull List<Candlestick> candlesticksDesc, final Candlestick currentCandlestick) {
    final int period = movingAverage.getPeriod();
    final CandlestickApply candlestickApply = movingAverage.getCandlestickApply();

    if(candlesticksDesc.size() > period) {
      final Optional<MovingAverage> lestMA = BatchUtils.getLastCandlestick(candlesticksDesc).getMovingAverages().stream()
          .filter(ma -> this.getMovingAverageType().equals(ma.getType()) && period == ma.getPeriod() && candlestickApply.equals(ma.getCandlestickApply()))
          .findFirst();

      if (lestMA.isPresent()) {
        final Double lastEma = lestMA.get().getValue();
        if (Objects.nonNull(lastEma)) {
          final double a = MathUtils.getMultiplication(candlestickApply.getPrice(currentCandlestick), this.getPercentagePrice(period));
          final double b = MathUtils.getSubtract(1, this.getPercentagePrice(period));
          final double c = MathUtils.getMultiplication(lastEma, b);
          final double ema = MathUtils.getSum(Stream.of(a, c).toList());
          movingAverage.setValue(ema);
        }
      }
    } else if(candlesticksDesc.size() == period) {
      final List<Double> collection = candlesticksDesc.stream().map(candlestickApply::getPrice).toList();
      final double ema = MathUtils.getMed(collection);
      movingAverage.setValue(ema);
    }
  }

  @Override
  public MovingAverageType getMovingAverageType() {
    return MovingAverageType.EMA;
  }
}
