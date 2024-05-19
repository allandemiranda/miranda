package lu.forex.system.entities;

import jakarta.validation.Validation;
import java.time.LocalDateTime;
import java.util.UUID;
import lu.forex.system.enums.TimeFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CandlestickTest {

  @Test
  void testCandlestickWithNotNullIdIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var id = UUID.randomUUID();
      //when
      final var validated = validator.validateValue(Candlestick.class, "id", id);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickWithNullIdIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "id", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickId() {
    //given
    final var candlestick = new Candlestick();
    final var id = UUID.randomUUID();
    //when
    candlestick.setId(id);
    //then
    Assertions.assertEquals(id, candlestick.getId());
  }

  @Test
  void testCandlestickWhenSymbolNotNullIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var symbol = Mockito.mock(Symbol.class);
      //when
      final var validated = validator.validateValue(Candlestick.class, "symbol", symbol);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickWhenSymbolNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "symbol", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickSymbol() {
    //given
    final var candlestick = new Candlestick();
    final var symbol = new Symbol();
    //when
    candlestick.setSymbol(symbol);
    //then
    Assertions.assertEquals(symbol, candlestick.getSymbol());
  }

  @ParameterizedTest
  @EnumSource(TimeFrame.class)
  void testCandlestickWhenTimeFrameNotNullIsValid(TimeFrame timeFrame) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "timeFrame", timeFrame);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickWhenTimeFrameNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "timeFrame", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(TimeFrame.class)
  void testCandlestickSymbol(TimeFrame timeFrame) {
    //given
    final var candlestick = new Candlestick();
    //when
    candlestick.setTimeFrame(timeFrame);
    //then
    Assertions.assertEquals(timeFrame, candlestick.getTimeFrame());
  }

  @Test
  void testCandlestickWhenTimestampNotNullOnPastIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now().minusYears(1);
      //when
      final var validated = validator.validateValue(Candlestick.class, "timestamp", timestamp);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickWhenTimestampNotNullOnPresentIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now();
      //when
      final var validated = validator.validateValue(Candlestick.class, "timestamp", timestamp);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickWhenTimestampNotNullOnFutureIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now().plusYears(1);
      //when
      final var validated = validator.validateValue(Candlestick.class, "timestamp", timestamp);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickWhenTimestampIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "timestamp", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0001, 0.00001, 0.000001, 1d, Double.MAX_VALUE})
  void testCandlestickWhenHighPositiveIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "high", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {-1d, 0})
  void testCandlestickWhenHighNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "high", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0001, 0.00001, 0.000001, 1d, Double.MAX_VALUE})
  void testCandlestickWhenLowPositiveIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "low", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {-1d, 0})
  void testCandlestickWhenLowNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "low", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0001, 0.00001, 0.000001, 1d, Double.MAX_VALUE})
  void testCandlestickWhenOpenPositiveIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "open", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {-1d, 0})
  void testCandlestickWhenOpenNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "open", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0001, 0.00001, 0.000001, 1d, Double.MAX_VALUE})
  void testCandlestickWhenClosePositiveIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "close", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {-1d, 0})
  void testCandlestickWhenCloseNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Candlestick.class, "close", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickWhenPriceEqualIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestick = new Candlestick();
      candlestick.setHigh(1d);
      candlestick.setLow(1d);
      candlestick.setOpen(1d);
      candlestick.setClose(1d);
      //when
      final var validated = validator.validate(candlestick);
      //then
      Assertions.assertFalse(validated.stream()
          .anyMatch(violation -> "high".equals(violation.getPropertyPath().toString()) || "low".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickWhenPriceValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestick = new Candlestick();
      candlestick.setHigh(5d);
      candlestick.setLow(3d);
      candlestick.setOpen(3d);
      candlestick.setClose(4d);
      //when
      final var validated = validator.validate(candlestick);
      //then
      Assertions.assertFalse(validated.stream()
          .anyMatch(violation -> "high".equals(violation.getPropertyPath().toString()) || "low".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickWhenHighLowerThanLowIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestick = new Candlestick();
      candlestick.setHigh(2d);
      candlestick.setLow(3d);
      candlestick.setOpen(2d);
      candlestick.setClose(2d);
      //when
      final var validated = validator.validate(candlestick);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "high".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickWhenHighLowerThanOpenIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestick = new Candlestick();
      candlestick.setHigh(2d);
      candlestick.setLow(2d);
      candlestick.setOpen(3d);
      candlestick.setClose(2d);
      //when
      final var validated = validator.validate(candlestick);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "high".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickWhenHighLowerThanCloseIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestick = new Candlestick();
      candlestick.setHigh(2d);
      candlestick.setLow(2d);
      candlestick.setOpen(2d);
      candlestick.setClose(3d);
      //when
      final var validated = validator.validate(candlestick);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "high".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickWhenLowHigherThanOpenIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestick = new Candlestick();
      candlestick.setHigh(3d);
      candlestick.setLow(3d);
      candlestick.setOpen(2d);
      candlestick.setClose(3d);
      //when
      final var validated = validator.validate(candlestick);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "low".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickWhenLowHigherThanCloseIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestick = new Candlestick();
      candlestick.setHigh(3d);
      candlestick.setLow(3d);
      candlestick.setOpen(3d);
      candlestick.setClose(2d);
      //when
      final var validated = validator.validate(candlestick);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "low".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickTimestamp() {
    //given
    final var candlestick = new Candlestick();
    final var timestamp = LocalDateTime.now();
    //when
    candlestick.setTimestamp(timestamp);
    //then
    Assertions.assertEquals(timestamp, candlestick.getTimestamp());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1.12345, 1.23456, 1.1234})
  void testCandlestickHigh(double high) {
    //given
    final var candlestick = new Candlestick();
    //when
    candlestick.setHigh(high);
    //then
    Assertions.assertEquals(high, candlestick.getHigh());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1.12345, 1.23456, 1.1234})
  void testCandlestickLow(double low) {
    //given
    final var candlestick = new Candlestick();
    //when
    candlestick.setLow(low);
    //then
    Assertions.assertEquals(low, candlestick.getLow());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1.12345, 1.23456, 1.1234})
  void testCandlestickOpen(double open) {
    //given
    final var candlestick = new Candlestick();
    //when
    candlestick.setOpen(open);
    //then
    Assertions.assertEquals(open, candlestick.getOpen());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1.12345, 1.23456, 1.1234})
  void testCandlestickClose(double close) {
    //given
    final var candlestick = new Candlestick();
    //when
    candlestick.setClose(close);
    //then
    Assertions.assertEquals(close, candlestick.getClose());
  }


  @Test
  void testCandlestickEqualsAndHashCode() {
    //given
    final var candlestick1 = new Candlestick();
    final var candlestick2 = new Candlestick();

    final var symbol = Mockito.mock(Symbol.class);
    final var timeFrame = TimeFrame.values()[0];
    final var timestamp = LocalDateTime.now();

    //when
    candlestick1.setSymbol(symbol);
    candlestick2.setSymbol(symbol);

    candlestick1.setTimeFrame(timeFrame);
    candlestick2.setTimeFrame(timeFrame);

    candlestick1.setTimestamp(timestamp);
    candlestick2.setTimestamp(timestamp);

    //then
    Assertions.assertEquals(candlestick1, candlestick2);
    Assertions.assertEquals(candlestick1.hashCode(), candlestick2.hashCode());
  }

  @Test
  void testCandlestickEquals() {
    //given
    final var candlestick = new Candlestick();
    //when
    final var equals = candlestick.equals(candlestick);
    //then
    Assertions.assertTrue(equals);
  }

  @Test
  void testCandlestickEqualsNull() {
    //given
    final var candlestick = new Candlestick();
    //when
    final var equals = candlestick.equals(null);
    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testCandlestickEqualsWrongObjectType() {
    //given
    final var candlestick = new Candlestick();
    //when
    final var equals = candlestick.equals(new Object());
    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testCandlestickToString() {
    //given
    final var candlestick = new Candlestick();
    //when
    final var toString = candlestick.toString();
    //then
    Assertions.assertEquals("Candlestick(id=null, timeFrame=null, timestamp=null, high=0.0, low=0.0, open=0.0, close=0.0)", toString);
  }
}