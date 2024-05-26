package lu.forex.system.dtos;

import jakarta.validation.Validation;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TickCreateDtoTest {

  @Test
  void testTickCreateDtoWhenTimestampNotNullOnPastIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now().minusYears(1);
      //when
      final var validated = validator.validateValue(TickCreateDto.class, "timestamp", timestamp);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testTickCreateDtoWhenTimestampNotNullOnPresentIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now();
      //when
      final var validated = validator.validateValue(TickCreateDto.class, "timestamp", timestamp);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testTickCreateDtoWhenTimestampNotNullOnFutureIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now().plusYears(1);
      //when
      final var validated = validator.validateValue(TickCreateDto.class, "timestamp", timestamp);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testTickCreateDtoWhenTimestampIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickCreateDto.class, "timestamp", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.000001, 0.00001, 0.0001, 1d, Double.MAX_VALUE})
  void testTickCreateDtoWhenBidNotNegativeOrZeroIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickCreateDto.class, "bid", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_EXPONENT, -1d, 0d})
  void testTickCreateDtoWhenBidIsNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickCreateDto.class, "bid", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.000001, 0.00001, 0.0001, 1d, Double.MAX_VALUE})
  void testTickCreateDtoWhenAskNotNegativeOrZeroIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickCreateDto.class, "ask", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_EXPONENT, -1d, 0d})
  void testTickCreateDtoWhenAskIsNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TickCreateDto.class, "ask", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

//  @ParameterizedTest
//  @ValueSource(doubles = {1.12345, 2.12345})
//  void testTickCreateDtoWhenAskAndBidEqualsIsValid(double getPrice) {
//    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
//      //given
//      final var validator = validatorFactory.getValidator();
//      final var TickCreateDto = new TickCreateDto(null, getPrice, getPrice);
//      //when
//      final var validated = validator.validate(TickCreateDto);
//      //then
//      Assertions.assertFalse(validated.stream().anyMatch(violation -> "bid".equals(violation.getPropertyPath().toString())));
//    }
//  }
//
//  @ParameterizedTest
//  @ValueSource(doubles = {1.12345, 2.12345})
//  void testTickCreateDtoWhenAskLowerThanBidIsInvalid(double getPrice) {
//    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
//      //given
//      final var validator = validatorFactory.getValidator();
//      final var TickCreateDto = new TickCreateDto(null, getPrice, getPrice - 0.00001);
//      //when
//      final var validated = validator.validate(TickCreateDto);
//      //then
//      Assertions.assertTrue(validated.stream().anyMatch(violation -> "bid".equals(violation.getPropertyPath().toString())));
//    }
//  }
}