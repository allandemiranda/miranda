package lu.forex.system.enums;

import jakarta.validation.constraints.NotNull;
import java.util.stream.Stream;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.CandlestickBody;
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
    final CandlestickBody body = candlestick.getBody();
    return switch (this) {
      case CLOSE -> body.getClose();
      case OPEN -> body.getOpen();
      case HIGH -> body.getHigh();
      case LOW -> body.getLow();
      case MEDIAN_PRICE -> MathUtils.getDivision(MathUtils.getSum(Stream.of(body.getHigh(), body.getLow()).toList()), 2);
      case TYPICAL_PRICE -> MathUtils.getDivision(MathUtils.getSum(Stream.of(body.getHigh(), body.getLow(), body.getClose()).toList()), 3);
    };
  }

}
