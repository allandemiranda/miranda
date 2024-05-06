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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TickTest {

  private UUID uuid;
  private ValidatorFactory validatorFactory;

  @Mock
  private Symbol symbol;

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
  void tickIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setId(uuid);
    tick.setSymbol(symbol);
    tick.setTimestamp(LocalDateTime.now());
    tick.setBid(1d);
    tick.setAsk(2d);

    //then
    Assertions.assertTrue(validator.validate(tick).isEmpty());
  }

  @Test
  void tickIsValidTimestampOld() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setId(uuid);
    tick.setSymbol(symbol);
    tick.setTimestamp(LocalDateTime.now().minusYears(1));
    tick.setBid(1d);
    tick.setAsk(2d);

    //then
    Assertions.assertTrue(validator.validate(tick).isEmpty());
  }

  @Test
  void tickIsValidTimestampFuture() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setId(uuid);
    tick.setSymbol(symbol);
    tick.setTimestamp(LocalDateTime.now().plusYears(1));
    tick.setBid(1d);
    tick.setAsk(2d);

    //then
    Assertions.assertTrue(validator.validate(tick).isEmpty());
  }

  @Test
  void tickWithoutSymbolIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setId(uuid);
    tick.setSymbol(null);
    tick.setTimestamp(LocalDateTime.now());
    tick.setBid(1d);
    tick.setAsk(2d);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then  
    Assertions.assertEquals(1, validate.size());
    Assertions.assertEquals("symbol", validate.iterator().next().getPropertyPath().toString());
    Assertions.assertEquals("{jakarta.validation.constraints.NotNull.message}", validate.iterator().next().getMessageTemplate());
  }

  @Test
  void tickWithoutTimestampIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setId(uuid);
    tick.setSymbol(symbol);
    tick.setTimestamp(null);
    tick.setBid(1d);
    tick.setAsk(2d);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then  
    Assertions.assertEquals(1, validate.size());
    Assertions.assertEquals("timestamp", validate.iterator().next().getPropertyPath().toString());
    Assertions.assertEquals("{jakarta.validation.constraints.NotNull.message}", validate.iterator().next().getMessageTemplate());
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, -2d})
  void tickWithNegativeOrZeroBidIsInvalid(double value) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setId(uuid);
    tick.setSymbol(symbol);
    tick.setTimestamp(LocalDateTime.now());
    tick.setBid(value);
    tick.setAsk(1d);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then  
    Assertions.assertEquals(1, validate.size());
    Assertions.assertEquals("bid", validate.iterator().next().getPropertyPath().toString());
    Assertions.assertEquals("{jakarta.validation.constraints.Positive.message}", validate.iterator().next().getMessageTemplate());
  }

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, -2d})
  void tickWithNegativeAskIsInvalid(double value) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setId(uuid);
    tick.setSymbol(symbol);
    tick.setTimestamp(LocalDateTime.now());
    tick.setBid(1d);
    tick.setAsk(value);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //then  
    Assertions.assertEquals(1, validate.size());
    Assertions.assertEquals("ask", validate.iterator().next().getPropertyPath().toString());
    Assertions.assertEquals("{jakarta.validation.constraints.Positive.message}", validate.iterator().next().getMessageTemplate());
  }

  @Test
  void testEqualsAndHashCode() {
    //given
    final Tick tick1 = new Tick();
    final Tick tick2 = new Tick();
    final LocalDateTime timestamp = LocalDateTime.now();

    //when
    tick1.setId(uuid);
    tick1.setSymbol(symbol);
    tick1.setTimestamp(timestamp);

    tick2.setId(uuid);
    tick2.setSymbol(symbol);
    tick2.setTimestamp(timestamp);

    //then
    Assertions.assertEquals(tick1, tick2);
    Assertions.assertEquals(tick1.hashCode(), tick2.hashCode());
  }

  @Test
  void testIdNotEqualsAndHashCode() {
    //given
    final Tick tick1 = new Tick();
    final Tick tick2 = new Tick();
    final LocalDateTime timestamp = LocalDateTime.now();

    //when
    tick1.setId(uuid);
    tick1.setSymbol(symbol);
    tick1.setTimestamp(timestamp);

    tick2.setId(UUID.randomUUID());
    tick2.setSymbol(symbol);
    tick2.setTimestamp(timestamp);

    //then
    Assertions.assertNotEquals(tick1, tick2);
    Assertions.assertNotEquals(tick1.hashCode(), tick2.hashCode());
  }

  @Test
  void testSymbolNotEqualsAndHashCode() {
    //given
    final Tick tick1 = new Tick();
    final Tick tick2 = new Tick();
    final LocalDateTime timestamp = LocalDateTime.now();
    final Symbol symbol1 = Mockito.mock(Symbol.class);

    //when
    tick1.setId(uuid);
    tick1.setSymbol(symbol1);
    tick1.setTimestamp(timestamp);

    tick2.setId(uuid);
    tick2.setSymbol(symbol);
    tick2.setTimestamp(timestamp);

    //then
    Assertions.assertNotEquals(tick1, tick2);
    Assertions.assertNotEquals(tick1.hashCode(), tick2.hashCode());
  }

  @Test
  void testTimestampNotEqualsAndHashCode() {
    //given
    final Tick tick1 = new Tick();
    final Tick tick2 = new Tick();
    final LocalDateTime timestamp = LocalDateTime.now();

    //when
    tick1.setId(uuid);
    tick1.setSymbol(symbol);
    tick1.setTimestamp(timestamp);

    tick2.setId(uuid);
    tick2.setSymbol(symbol);
    tick2.setTimestamp(timestamp.plusYears(1));

    //then
    Assertions.assertNotEquals(tick1, tick2);
    Assertions.assertNotEquals(tick1.hashCode(), tick2.hashCode());
  }

  @Test
  void testId() {
    //given
    final Tick tick = new Tick();

    //when
    tick.setId(uuid);

    //then
    Assertions.assertEquals(uuid, tick.getId());
  }

  @Test
  void testSymbol() {
    //given
    final Tick tick = new Tick();

    //when
    tick.setSymbol(symbol);

    //then
    Assertions.assertEquals(symbol, tick.getSymbol());
  }

  @Test
  void testTimestamp() {
    //given
    final Tick tick = new Tick();

    //when
    tick.setSymbol(symbol);

    //then
    Assertions.assertEquals(symbol, tick.getSymbol());
  }

  @Test
  void testBid() {
    //given
    final Tick tick = new Tick();
    final double price = 1d;

    //when
    tick.setBid(price);

    //then
    Assertions.assertEquals(price, tick.getBid());
  }

  @Test
  void testAsk() {
    //given
    final Tick tick = new Tick();
    final double price = 1d;

    //when
    tick.setAsk(price);

    //then
    Assertions.assertEquals(price, tick.getAsk());
  }
}