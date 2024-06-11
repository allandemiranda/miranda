package lu.forex.system.enums;

import jakarta.validation.constraints.NotNull;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import lu.forex.system.dtos.CandlestickBodyDto;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.CandlestickBody;
import lu.forex.system.utils.MathUtils;

public enum PriceType {
  //@formatter:off
  CLOSE,
  OPEN,
  HIGH,
  LOW,
  MEDIAN_PRICE,
  TYPICAL_PRICE;
  //@formatter:on

  private double getPrice(ToDoubleFunction<PriceType> valueExtractor) {
    return switch (this) {
      case CLOSE -> valueExtractor.applyAsDouble(CLOSE);
      case OPEN -> valueExtractor.applyAsDouble(OPEN);
      case HIGH -> valueExtractor.applyAsDouble(HIGH);
      case LOW -> valueExtractor.applyAsDouble(LOW);
      case MEDIAN_PRICE ->
          MathUtils.getDivision(MathUtils.getSum(Stream.of(valueExtractor.applyAsDouble(HIGH), valueExtractor.applyAsDouble(LOW)).toList()), 2);
      case TYPICAL_PRICE -> MathUtils.getDivision(MathUtils.getSum(
          Stream.of(valueExtractor.applyAsDouble(HIGH), valueExtractor.applyAsDouble(LOW), valueExtractor.applyAsDouble(CLOSE)).toList()), 3);
    };
  }

  public double getPrice(@NotNull Candlestick candlestick) {
    final CandlestickBody body = candlestick.getBody();
    return this.getPrice(priceType -> switch (priceType) {
      case CLOSE -> body.getClose();
      case OPEN -> body.getOpen();
      case HIGH -> body.getHigh();
      case LOW -> body.getLow();
      default -> throw new IllegalStateException("Unexpected value: " + priceType);
    });
  }

  public double getPrice(@NotNull CandlestickDto candlestick) {
    final CandlestickBodyDto body = candlestick.body();
    return this.getPrice(priceType -> switch (priceType) {
      case CLOSE -> body.close();
      case OPEN -> body.open();
      case HIGH -> body.high();
      case LOW -> body.low();
      default -> throw new IllegalStateException("Unexpected value: " + priceType);
    });
  }

}
