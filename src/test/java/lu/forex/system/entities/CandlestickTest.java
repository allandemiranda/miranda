package lu.forex.system.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lu.forex.system.enums.TimeFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CandlestickTest {

  private UUID uuid;
  private ValidatorFactory validatorFactory;

  @BeforeEach
  void setUp() {
    uuid = UUID.randomUUID();
    validatorFactory = Validation.buildDefaultValidatorFactory();
  }

  @AfterEach
  void tearDown() {
    validatorFactory.close();
  }

  @Test
  void testCandlestickAnyIdIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setId(uuid);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(candlestickConstraintViolation -> "id".equals(candlestickConstraintViolation.getPropertyPath().toString())));
  }

  @Test
  void testCandlestickWithNullIdIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setId(null);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "id".equals(candlestickConstraintViolation.getPropertyPath().toString())
                                          && "{jakarta.validation.constraints.NotNull.message}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickId() {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setId(uuid);

    //then
    Assertions.assertEquals(uuid, candlestick.getId());
  }

  @Test
  void testCandlestickAnySymbolIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();
    final Symbol symbol = new Symbol();

    //when
    candlestick.setSymbol(symbol);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(candlestickConstraintViolation -> "symbol".equals(candlestickConstraintViolation.getPropertyPath().toString())));
  }

  @Test
  void testCandlestickWithNullSymbolIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setSymbol(null);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "symbol".equals(candlestickConstraintViolation.getPropertyPath().toString())
                                          && "{jakarta.validation.constraints.NotNull.message}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickSymbol() {
    //given
    final Candlestick candlestick = new Candlestick();
    final Symbol symbol = new Symbol();
    symbol.setName("EURUSD");

    //when
    candlestick.setSymbol(symbol);

    //then
    Assertions.assertEquals(symbol, candlestick.getSymbol());
  }

  @ParameterizedTest
  @EnumSource(TimeFrame.class)
  void testCandlestickWithTimeFrameIsValid(TimeFrame timeFrame) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setTimeFrame(timeFrame);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(validate.stream()
        .anyMatch(candlestickConstraintViolation -> "timeFrame".equals(candlestickConstraintViolation.getPropertyPath().toString())));
  }

  @Test
  void testCandlestickWithNullTimeFrameIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setTimeFrame(null);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "timeFrame".equals(candlestickConstraintViolation.getPropertyPath().toString())
                                          && "{jakarta.validation.constraints.NotNull.message}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @EnumSource(TimeFrame.class)
  void testCandlestickSymbol(TimeFrame timeFrame) {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setTimeFrame(timeFrame);

    //then
    Assertions.assertEquals(timeFrame, candlestick.getTimeFrame());
  }

  @Test
  void testCandlestickAnyTimestampIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();
    final LocalDateTime timestamp = LocalDateTime.now();

    //when
    candlestick.setTimestamp(timestamp);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(validate.stream()
        .anyMatch(candlestickConstraintViolation -> "timestamp".equals(candlestickConstraintViolation.getPropertyPath().toString())));
  }

  @Test
  void testCandlestickWithNullTimestampIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setTimestamp(null);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "timestamp".equals(candlestickConstraintViolation.getPropertyPath().toString())
                                          && "{jakarta.validation.constraints.NotNull.message}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickTimestamp() {
    //given
    final Candlestick candlestick = new Candlestick();
    final LocalDateTime timestamp = LocalDateTime.now();

    //when
    candlestick.setTimestamp(timestamp);

    //then
    Assertions.assertEquals(timestamp, candlestick.getTimestamp());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1d, 2d, Double.MAX_VALUE})
  void testCandlestickPositiveAndNotZeroHighIsValid(double high) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(high);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(candlestickConstraintViolation -> "high".equals(candlestickConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, Double.MIN_EXPONENT})
  void testCandlestickNegativeOrZeroHighIsValid(double high) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(high);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "high".equals(candlestickConstraintViolation.getPropertyPath().toString())
                                          && "{jakarta.validation.constraints.Positive.message}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, Double.MIN_EXPONENT})
  void testCandlestickHigh(double high) {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(high);

    //then
    Assertions.assertEquals(high, candlestick.getHigh());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1d, 2d, Double.MAX_VALUE})
  void testCandlestickPositiveAndNotZeroLowIsValid(double low) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setLow(low);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(candlestickConstraintViolation -> "low".equals(candlestickConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, Double.MIN_EXPONENT})
  void testCandlestickNegativeOrZeroLowIsValid(double low) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setLow(low);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "low".equals(candlestickConstraintViolation.getPropertyPath().toString())
                                          && "{jakarta.validation.constraints.Positive.message}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, Double.MIN_EXPONENT})
  void testCandlestickLow(double low) {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setLow(low);

    //then
    Assertions.assertEquals(low, candlestick.getLow());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1d, 2d, Double.MAX_VALUE})
  void testCandlestickPositiveAndNotZeroOpenIsValid(double open) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setOpen(open);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(candlestickConstraintViolation -> "open".equals(candlestickConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, Double.MIN_EXPONENT})
  void testCandlestickNegativeOrZeroOpenIsValid(double open) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setOpen(open);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "open".equals(candlestickConstraintViolation.getPropertyPath().toString())
                                          && "{jakarta.validation.constraints.Positive.message}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, Double.MIN_EXPONENT})
  void testCandlestickOpen(double open) {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setOpen(open);

    //then
    Assertions.assertEquals(open, candlestick.getOpen());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1d, 2d, Double.MAX_VALUE})
  void testCandlestickPositiveAndNotZeroCloseIsValid(double close) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setClose(close);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(candlestickConstraintViolation -> "close".equals(candlestickConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, Double.MIN_EXPONENT})
  void testCandlestickNegativeOrZeroCloseIsValid(double close) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setClose(close);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "close".equals(candlestickConstraintViolation.getPropertyPath().toString())
                                          && "{jakarta.validation.constraints.Positive.message}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, Double.MIN_EXPONENT})
  void testCandlestickClose(double close) {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setClose(close);

    //then
    Assertions.assertEquals(close, candlestick.getClose());
  }

  @Test
  void testCandlestickRepresentationIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(4d);
    candlestick.setOpen(3d);
    candlestick.setClose(2d);
    candlestick.setLow(1d);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(validate.stream().anyMatch(
        candlestickConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickRepresentationIsValidScenario1() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(1d);
    candlestick.setOpen(1d);
    candlestick.setClose(1d);
    candlestick.setLow(1d);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(validate.stream().anyMatch(
        candlestickConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickRepresentationIsValidScenario2() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(3d);
    candlestick.setOpen(2d);
    candlestick.setClose(2d);
    candlestick.setLow(1d);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(validate.stream().anyMatch(
        candlestickConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickRepresentationIsValidScenario3() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(2d);
    candlestick.setOpen(2d);
    candlestick.setClose(2d);
    candlestick.setLow(1d);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(validate.stream().anyMatch(
        candlestickConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickRepresentationIsValidScenario4() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(3d);
    candlestick.setOpen(2d);
    candlestick.setClose(2d);
    candlestick.setLow(2d);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertFalse(validate.stream().anyMatch(
        candlestickConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickRepresentationIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(1d);
    candlestick.setOpen(2d);
    candlestick.setClose(3d);
    candlestick.setLow(4d);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickRepresentationIsInvalidScenario1() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(2d);
    candlestick.setOpen(3d);
    candlestick.setClose(1d);
    candlestick.setLow(1d);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickRepresentationIsInvalidScenario2() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(2d);
    candlestick.setOpen(1d);
    candlestick.setClose(3d);
    candlestick.setLow(1d);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickRepresentationIsInvalidScenario3() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(2d);
    candlestick.setOpen(1d);
    candlestick.setClose(1d);
    candlestick.setLow(3d);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickRepresentationIsInvalidScenario4() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setHigh(1d);
    candlestick.setOpen(1d);
    candlestick.setClose(1d);
    candlestick.setLow(3d);
    final Set<ConstraintViolation<Candlestick>> validate = validator.validate(candlestick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickEqualsAndHashCode() {
    //given
    final Candlestick candlestick1 = new Candlestick();
    final Candlestick candlestick2 = new Candlestick();
    final TimeFrame timeFrame = TimeFrame.M15;
    final LocalDateTime timestamp = LocalDateTime.now();
    final Symbol symbol = new Symbol();

    //when
    candlestick1.setId(uuid);
    candlestick1.setSymbol(symbol);
    candlestick1.setTimeFrame(timeFrame);
    candlestick1.setTimestamp(timestamp);

    candlestick2.setId(uuid);
    candlestick2.setSymbol(symbol);
    candlestick2.setTimeFrame(timeFrame);
    candlestick2.setTimestamp(timestamp);

    //then
    Assertions.assertEquals(candlestick1, candlestick2);
    Assertions.assertEquals(candlestick1.hashCode(), candlestick2.hashCode());
  }

  @Test
  void testCandlestickSymbolNotEqualsAndHashCode() {
    //given
    final Candlestick candlestick1 = new Candlestick();
    final Candlestick candlestick2 = new Candlestick();
    final TimeFrame timeFrame = TimeFrame.M15;
    final LocalDateTime timestamp = LocalDateTime.now();
    final Symbol symbol = new Symbol();
    symbol.setName("EURUSD");
    final Symbol symbol1 = new Symbol();
    symbol1.setName("ABCDEF");

    //when
    candlestick1.setId(uuid);
    candlestick1.setSymbol(symbol);
    candlestick1.setTimeFrame(timeFrame);
    candlestick1.setTimestamp(timestamp);

    candlestick2.setId(uuid);
    candlestick2.setSymbol(symbol1);
    candlestick2.setTimeFrame(timeFrame);
    candlestick2.setTimestamp(timestamp);

    //then
    Assertions.assertNotEquals(candlestick1, candlestick2);
    Assertions.assertNotEquals(candlestick1.hashCode(), candlestick2.hashCode());
  }

  @Test
  void testCandlestickTimeFrameNotEqualsAndHashCode() {
    //given
    final Candlestick candlestick1 = new Candlestick();
    final Candlestick candlestick2 = new Candlestick();
    final LocalDateTime timestamp = LocalDateTime.now();
    final Symbol symbol = new Symbol();

    //when
    candlestick1.setId(uuid);
    candlestick1.setSymbol(symbol);
    candlestick1.setTimeFrame(TimeFrame.M15);
    candlestick1.setTimestamp(timestamp);

    candlestick2.setId(uuid);
    candlestick2.setSymbol(symbol);
    candlestick2.setTimeFrame(TimeFrame.D1);
    candlestick2.setTimestamp(timestamp);

    //then
    Assertions.assertNotEquals(candlestick1, candlestick2);
    Assertions.assertNotEquals(candlestick1.hashCode(), candlestick2.hashCode());
  }

  @Test
  void testCandlestickTimestampNotEqualsAndHashCode() {
    //given
    final Candlestick candlestick1 = new Candlestick();
    final Candlestick candlestick2 = new Candlestick();
    final TimeFrame timeFrame = TimeFrame.M15;
    final LocalDateTime timestamp = LocalDateTime.now();
    final Symbol symbol = new Symbol();

    //when
    candlestick1.setId(uuid);
    candlestick1.setSymbol(symbol);
    candlestick1.setTimeFrame(timeFrame);
    candlestick1.setTimestamp(timestamp);

    candlestick2.setId(uuid);
    candlestick2.setSymbol(symbol);
    candlestick2.setTimeFrame(timeFrame);
    candlestick2.setTimestamp(timestamp.plusYears(1));

    //then
    Assertions.assertNotEquals(candlestick1, candlestick2);
    Assertions.assertNotEquals(candlestick1.hashCode(), candlestick2.hashCode());
  }

  @Test
  void testCandlestickEquals() {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    final boolean equals = candlestick.equals(candlestick);

    //then
    Assertions.assertTrue(equals);
  }

  @Test
  void testCandlestickEqualsNull() {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    final boolean equals = candlestick.equals(null);

    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testCandlestickEqualsWrongObjectType() {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    final boolean equals = candlestick.equals(new Object());

    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testCandlestickToString() {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    final String toString = candlestick.toString();

    //then
    Assertions.assertEquals("Candlestick(id=null, timeFrame=null, timestamp=null, high=0.0, low=0.0, open=0.0, close=0.0)", toString);
  }
}