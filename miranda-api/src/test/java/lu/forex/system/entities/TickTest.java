package lu.forex.system.entities;

import jakarta.validation.Validation;
import java.time.LocalDateTime;
import java.util.UUID;
import lu.forex.system.dtos.SymbolResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TickTest {

  @Test
  void testTickWhenIdNotNullIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var id = UUID.randomUUID();
      //when
      final var validated = validator.validateValue(Tick.class, "id", id);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testTickWhenIdNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Tick.class, "id", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testTickWhenSymbolNotNullIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var symbolResponseDto = Mockito.mock(SymbolResponseDto.class);
      //when
      final var validated = validator.validateValue(Tick.class, "symbol", symbolResponseDto);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testTickWhenSymbolNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Tick.class, "symbol", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testTickWhenTimestampNotNullOnPastIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now().minusYears(1);
      //when
      final var validated = validator.validateValue(Tick.class, "timestamp", timestamp);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testTickWhenTimestampNotNullOnPresentIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now();
      //when
      final var validated = validator.validateValue(Tick.class, "timestamp", timestamp);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testTickWhenTimestampNotNullOnFutureIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now().plusYears(1);
      //when
      final var validated = validator.validateValue(Tick.class, "timestamp", timestamp);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testTickWhenTimestampIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Tick.class, "timestamp", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.000001, 0.00001, 0.0001, 1d, Double.MAX_VALUE})
  void testTickWhenBidNotNegativeOrZeroIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Tick.class, "bid", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_EXPONENT, -1d, 0d})
  void testTickWhenBidIsNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Tick.class, "bid", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.000001, 0.00001, 0.0001, 1d, Double.MAX_VALUE})
  void testTickWhenAskNotNegativeOrZeroIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Tick.class, "ask", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_EXPONENT, -1d, 0d})
  void testTickWhenAskIsNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Tick.class, "ask", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {1.12345, 2.12345})
  void testTickWhenAskAndBidEqualsIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var tick = new Tick();
      //when
      tick.setAsk(price);
      tick.setBid(price);
      final var validated = validator.validate(tick);
      //then
      Assertions.assertFalse(validated.stream().anyMatch(violation -> "bid".equals(violation.getPropertyPath().toString())));
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {1.12345, 2.12345})
  void testTickWhenAskLowerThanBidIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var tick = new Tick();
      //when
      tick.setAsk(price - 0.00001);
      tick.setBid(price);
      final var validated = validator.validate(tick);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "bid".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testTickId() {
    //given
    final var tick = new Tick();
    final var id = UUID.randomUUID();
    //when
    tick.setId(id);
    //then
    Assertions.assertEquals(id, tick.getId());
  }

//  @Test
//  void testTickSymbol() {
//    //given
//    final var tick = new Tick();
//    final var symbol = new Symbol();
//    //when
//    tick.setSymbol(symbol);
//    //then
//    Assertions.assertEquals(symbol, tick.getSymbol());
//  }

  @Test
  void testTickTimestamp() {
    //given
    final var tick = new Tick();
    final var timestamp = LocalDateTime.now();
    //when
    tick.setTimestamp(timestamp);
    //then
    Assertions.assertEquals(timestamp, tick.getTimestamp());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1d, 2d, Double.MAX_VALUE})
  void testTickBid(double bid) {
    //given
    final var tick = new Tick();
    //when
    tick.setBid(bid);
    //then
    Assertions.assertEquals(bid, tick.getBid());
  }

  @ParameterizedTest
  @ValueSource(doubles = {1d, 2d, Double.MAX_VALUE})
  void testTickAsk(double ask) {
    //given
    final var tick = new Tick();
    //when
    tick.setAsk(ask);
    //then
    Assertions.assertEquals(ask, tick.getAsk());
  }

//  @Test
//  void testTickEqualsAndHashCode() {
//    //given
//    final var tick1 = new Tick();
//    final var tick2 = new Tick();
//
//    final var id = UUID.randomUUID();
//    final var symbol = new Symbol();
//    final var timestamp = LocalDateTime.now();
//    final var ask = 0.2;
//    final var bid = 0.1;
//
//    //when
//    tick1.setId(id);
//    tick1.setSymbol(symbol);
//    tick1.setTimestamp(timestamp);
//    tick1.setBid(ask);
//    tick1.setAsk(bid);
//
//    tick2.setId(id);
//    tick2.setSymbol(symbol);
//    tick2.setTimestamp(timestamp);
//    tick2.setBid(ask);
//    tick2.setAsk(bid);
//
//    //then
//    Assertions.assertEquals(tick1, tick2);
//    Assertions.assertEquals(tick1.hashCode(), tick2.hashCode());
//  }

  @Test
  void testTickEquals() {
    //given
    final var tick = new Tick();
    //when
    final var equals = tick.equals(tick);
    //then
    Assertions.assertTrue(equals);
  }

  @Test
  void testTickEqualsNull() {
    //given
    final var tick = new Tick();
    //when
    final var equals = tick.equals(null);
    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testTickEqualsWrongObjectType() {
    //given
    final var tick = new Tick();
    //when
    final var equals = tick.equals(new Object());
    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testTickToString() {
    //given
    final var tick = new Tick();
    //when
    final var toString = tick.toString();
    //then
    Assertions.assertEquals("Tick(id=null, timestamp=null, bid=0.0, ask=0.0)", toString);
  }
}