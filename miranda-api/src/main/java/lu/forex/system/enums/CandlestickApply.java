package lu.forex.system.enums;

import jakarta.validation.constraints.NotNull;
import java.util.stream.Stream;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.utils.MathUtils;

public enum CandlestickApply {
  //@formatter:off
  CLOSE,
  OPEN,
  HIGH,
  LOW,
  MEDIAN_PRICE,
  TYPICAL_PRICE;
  //@formatter:on

  public double getPrice(final @NotNull Candlestick candlestick) {
    return switch (this) {
      case CLOSE -> candlestick.getClose();
      case OPEN -> candlestick.getOpen();
      case HIGH -> candlestick.getHigh();
      case LOW -> candlestick.getLow();
      case MEDIAN_PRICE -> MathUtils.getDivision(MathUtils.getSum(Stream.of(candlestick.getHigh(), candlestick.getLow()).toList()), 2);
      case TYPICAL_PRICE ->
          MathUtils.getDivision(MathUtils.getSum(Stream.of(candlestick.getHigh(), candlestick.getLow(), candlestick.getClose()).toList()), 3);
    };
  }

}
