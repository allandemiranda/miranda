package lu.forex.system.batch;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import lu.forex.system.batch.model.MovingAverageBatch;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.utils.BatchUtils;
import lu.forex.system.utils.MathUtils;
import org.springframework.stereotype.Service;

@Service
public class SmaMovingAverageBatch extends MovingAverageBatch {

  @Override
  public void calculateMovingAverage(final @NotNull List<Candlestick> candlesticksDesc) {
    final Candlestick currentCandlestick = BatchUtils.getCurrentCandlestick(candlesticksDesc);
    currentCandlestick.getMovingAverages().stream().filter(movingAverage -> this.getMovingAverageType().equals(movingAverage.getType()))
        .forEach(ma -> this.getMovingAverageConsumer(ma, candlesticksDesc));
  }

  private void getMovingAverageConsumer(final @NotNull MovingAverage movingAverage, final @NotNull List<Candlestick> candlesticksDesc) {
    final int period = movingAverage.getPeriod();
    final CandlestickApply candlestickApply = movingAverage.getCandlestickApply();
    final Collection<Double> collection = candlesticksDesc.stream().limit(period).map(candlestickApply::getPrice).toList();
    movingAverage.setValue(MathUtils.getMed(collection));
  }

  @Override
  public MovingAverageType getMovingAverageType() {
    return MovingAverageType.SMA;
  }
}
