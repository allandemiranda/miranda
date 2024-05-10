package lu.forex.system.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TickTest {

  private static UUID uuid;
  private ValidatorFactory validatorFactory;

  @BeforeAll
  static void setUpBeforeClass() {
    uuid = UUID.randomUUID();
  }

  @BeforeEach
  void setUp() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
  }

  @AfterEach
  void tearDown() {
    validatorFactory.close();
  }

  @Test
  void testTickAnyIdIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setId(uuid);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then
    Assertions.assertFalse(validate.stream().anyMatch(tickConstraintViolation -> "id".equals(tickConstraintViolation.getPropertyPath().toString())));
  }

  @Test
  void testTickWithNullIdIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setId(null);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(tickConstraintViolation -> "id".equals(tickConstraintViolation.getPropertyPath().toString())
                                                                                && "{jakarta.validation.constraints.NotNull.message}".equals(
        tickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testTickAnySymbolIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();
    final Symbol symbol = new Symbol();

    //when
    tick.setSymbol(symbol);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(tickConstraintViolation -> "symbol".equals(tickConstraintViolation.getPropertyPath().toString())));
  }

  @Test
  void testTickWithNullSymbolIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setSymbol(null);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(tickConstraintViolation -> "symbol".equals(tickConstraintViolation.getPropertyPath().toString())
                                                                                && "{jakarta.validation.constraints.NotNull.message}".equals(
        tickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testTickAnyTimestampIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();
    final LocalDateTime timestamp = LocalDateTime.now();

    //when
    tick.setTimestamp(timestamp);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(tickConstraintViolation -> "timestamp".equals(tickConstraintViolation.getPropertyPath().toString())));
  }

  @Test
  void testTickWithNullTimestampIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setTimestamp(null);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        tickConstraintViolation -> "timestamp".equals(tickConstraintViolation.getPropertyPath().toString())
                                   && "{jakarta.validation.constraints.NotNull.message}".equals(tickConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {1d, 2d, Double.MAX_VALUE})
  void testTickPositiveAndNotZeroBidIsValid(double bid) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setBid(bid);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then
    Assertions.assertFalse(validate.stream().anyMatch(tickConstraintViolation -> "bid".equals(tickConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, Double.MIN_VALUE - 1d})
  void testTickNegativeOrZeroBidIsValid(double bid) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setBid(bid);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(tickConstraintViolation -> "bid".equals(tickConstraintViolation.getPropertyPath().toString())
                                                                                && "{jakarta.validation.constraints.Positive.message}".equals(
        tickConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {1d, 2d, Double.MAX_VALUE})
  void testTickPositiveAndNotZeroAskIsValid(double ask) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setAsk(ask);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then
    Assertions.assertFalse(validate.stream().anyMatch(tickConstraintViolation -> "ask".equals(tickConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, Double.MIN_VALUE - 1d})
  void testTickNegativeOrZeroAskIsValid(double ask) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setAsk(ask);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(tickConstraintViolation -> "bid".equals(tickConstraintViolation.getPropertyPath().toString())
                                                                                && "{jakarta.validation.constraints.Positive.message}".equals(
        tickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testTickEqualsAndHashCode() {
    //given
    final Tick tick1 = new Tick();
    final Tick tick2 = new Tick();
    final LocalDateTime timestamp = LocalDateTime.now();
    final Symbol symbol = new Symbol();

    //when
    tick1.setId(uuid);
    tick1.setSymbol(symbol);
    tick1.setTimestamp(timestamp);
    tick1.setBid(1d);
    tick1.setAsk(2d);

    UUID randomUUID = UUID.randomUUID();
    while (randomUUID.equals(tick1.getId())) {
      randomUUID = UUID.randomUUID();
    }
    tick2.setId(randomUUID);
    tick2.setSymbol(symbol);
    tick2.setTimestamp(timestamp);
    tick2.setBid(3d);
    tick2.setAsk(4d);

    //then
    Assertions.assertEquals(tick1, tick2);
    Assertions.assertEquals(tick1.hashCode(), tick2.hashCode());
  }

  @Test
  void testTickWithSymbolNotEqualsAndHashCode() {
    //given
    final Tick tick1 = new Tick();
    final Tick tick2 = new Tick();
    final LocalDateTime timestamp = LocalDateTime.now();
    final Symbol symbol = new Symbol();
    final Symbol symbol1 = new Symbol();

    //when
    symbol1.setName("FGHIJK");
    tick1.setSymbol(symbol1);
    tick1.setTimestamp(timestamp);

    symbol.setName("ABCDEF");
    tick2.setSymbol(symbol);
    tick2.setTimestamp(timestamp);

    //then
    Assertions.assertNotEquals(tick1, tick2);
    Assertions.assertNotEquals(tick1.hashCode(), tick2.hashCode());
  }

  @Test
  void testTickTimestampNotEqualsAndHashCode() {
    //given
    final Tick tick1 = new Tick();
    final Tick tick2 = new Tick();
    final LocalDateTime timestamp1 = LocalDateTime.now();
    final LocalDateTime timestamp2 = LocalDateTime.now().plusYears(1);
    final Symbol symbol = new Symbol();

    //when
    tick1.setSymbol(symbol);
    tick1.setTimestamp(timestamp1);

    tick2.setSymbol(symbol);
    tick2.setTimestamp(timestamp2);

    //then
    Assertions.assertNotEquals(tick1, tick2);
    Assertions.assertNotEquals(tick1.hashCode(), tick2.hashCode());
  }

  @Test
  void testTickEquals() {
    //given
    final Tick tick = new Tick();

    //when
    final boolean equals = tick.equals(tick);

    //then
    Assertions.assertTrue(equals);
  }

  @Test
  void testTickEqualsNull() {
    //given
    final Tick tick = new Tick();

    //when
    final boolean equals = tick.equals(null);

    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testTickEqualsWrongObjectType() {
    //given
    final Tick tick = new Tick();

    //when
    final boolean equals = tick.equals(new Object());

    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testTickToString() {
    //given
    final Tick tick = new Tick();

    //when
    final String toString = tick.toString();

    //then
    Assertions.assertEquals("Tick(id=null, timestamp=null, bid=0.0, ask=0.0)", toString);
  }
}