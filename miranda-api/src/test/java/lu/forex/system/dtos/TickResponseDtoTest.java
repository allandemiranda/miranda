package lu.forex.system.dtos;

import jakarta.validation.Validation;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TickResponseDtoTest {

  @Test
  void testTickResponseDtoWhenIdNotNullIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var id = UUID.randomUUID();
      //when      
      final var validated = validator.validateValue(TickResponseDto.class, "id", id);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testTickResponseDtoWhenIdNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickResponseDto.class, "id", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testTickResponseDtoWhenSymbolNotNullIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var symbolResponseDto = Mockito.mock(SymbolResponseDto.class);
      //when
      final var validated = validator.validateValue(TickResponseDto.class, "symbol", symbolResponseDto);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testTickResponseDtoWhenSymbolNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickResponseDto.class, "symbol", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testTickResponseDtoWhenTimestampNotNullOnPastIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now().minusYears(1);
      //when
      final var validated = validator.validateValue(TickResponseDto.class, "timestamp", timestamp);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testTickResponseDtoWhenTimestampNotNullOnPresentIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now();
      //when
      final var validated = validator.validateValue(TickResponseDto.class, "timestamp", timestamp);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testTickResponseDtoWhenTimestampNotNullOnFutureIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now().plusYears(1);
      //when
      final var validated = validator.validateValue(TickResponseDto.class, "timestamp", timestamp);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testTickResponseDtoWhenTimestampIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickResponseDto.class, "timestamp", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.000001, 0.00001, 0.0001, 1d, Double.MAX_VALUE})
  void testTickResponseDtoWhenBidNotNegativeOrZeroIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickResponseDto.class, "bid", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_EXPONENT, -1d, 0d})
  void testTickResponseDtoWhenBidIsNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickResponseDto.class, "bid", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.000001, 0.00001, 0.0001, 1d, Double.MAX_VALUE})
  void testTickResponseDtoWhenAskNotNegativeOrZeroIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickResponseDto.class, "ask", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_EXPONENT, -1d, 0d})
  void testTickResponseDtoWhenAskIsNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickResponseDto.class, "ask", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {1.12345, 2.12345})
  void testTickResponseDtoWhenAskAndBidEqualsIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var tickResponseDto = new TickResponseDto(null, null, null, price, price);
      //when
      final var validated = validator.validate(tickResponseDto);
      //then
      Assertions.assertFalse(validated.stream().anyMatch(violation -> "bid".equals(violation.getPropertyPath().toString())));
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {1.12345, 2.12345})
  void testTickResponseDtoWhenAskLowerThanBidIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var tickResponseDto = new TickResponseDto(null, null, null, price, price - 0.00001);
      //when
      final var validated = validator.validate(tickResponseDto);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "bid".equals(violation.getPropertyPath().toString())));
    }
  }
}