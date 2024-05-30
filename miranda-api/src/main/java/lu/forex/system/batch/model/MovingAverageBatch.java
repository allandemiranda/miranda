package lu.forex.system.batch.model;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.MovingAverageType;

public abstract class MovingAverageBatch {

  public static void init(final @NotNull MovingAverageType movingAverageType, final @NotNull Candlestick currentCandlestick, final int period,
      final @NotNull CandlestickApply candlestickApply) {
    final MovingAverage ma = new MovingAverage();
    ma.setType(movingAverageType);
    ma.setCandlestickApply(candlestickApply);
    ma.setPeriod(period);
    currentCandlestick.getMovingAverages().add(ma);
  }

  public abstract void calculateMovingAverage(final @NotNull List<Candlestick> candlesticksDesc);

  public abstract MovingAverageType getMovingAverageType();

}
