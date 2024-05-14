package lu.forex.system.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;
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
class CandlestickResponseDtoTest {

  @Test
  void testCandlestickResponseDtoIdNotNullIsValid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "id", UUID.randomUUID());
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoIdNullIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "id", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoSymbolNotNullIsValid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var symbolResponseDto = Mockito.mock(SymbolResponseDto.class);
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "symbol", symbolResponseDto);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoSymbolNullIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "symbol", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(TimeFrame.class)
  void testCandlestickResponseDtoTimeFrameNotNullIsValid(TimeFrame timeFrame) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "timeFrame", timeFrame);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoTimeFrameNullIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "timeFrame", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoTimestampNotNullOnPastIsValid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now().minusYears(1);
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "timestamp", timestamp);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoTimestampNotNullOnPresentIsValid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "timestamp", timestamp);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoTimestampNotNullOnFutureIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var timestamp = LocalDateTime.now().plusYears(1);
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "timestamp", timestamp);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoTimestampIsNullIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "timestamp", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0001, 0.00001, 0.000001, 1d, Double.MAX_VALUE})
  void testCandlestickResponseDtoHighPositiveIsValid(double price) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "high", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {-1d, 0})
  void testCandlestickResponseDtoHighNegativeOrZeroIsInvalid(double price) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "high", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0001, 0.00001, 0.000001, 1d, Double.MAX_VALUE})
  void testCandlestickResponseDtoLowPositiveIsValid(double price) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "low", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {-1d, 0})
  void testCandlestickResponseDtoLowNegativeOrZeroIsInvalid(double price) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "low", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0001, 0.00001, 0.000001, 1d, Double.MAX_VALUE})
  void testCandlestickResponseDtoOpenPositiveIsValid(double price) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "open", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {-1d, 0})
  void testCandlestickResponseDtoOpenNegativeOrZeroIsInvalid(double price) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "open", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0001, 0.00001, 0.000001, 1d, Double.MAX_VALUE})
  void testCandlestickResponseDtoClosePositiveIsValid(double price) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "close", price);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {-1d, 0})
  void testCandlestickResponseDtoCloseNegativeOrZeroIsInvalid(double price) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "close", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoPriceEqualIsValid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 1d, 1d, 1d, 1d);
      //when
      final var validated = validator.validate(candlestickResponseDto);
      //then
      Assertions.assertFalse(validated.stream().anyMatch(violation -> "high".equals(violation.getPropertyPath().toString()) || "low".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickResponseDtoPriceValid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 5d, 2d, 3d, 4d);
      //when
      final var validated = validator.validate(candlestickResponseDto);
      //then
      Assertions.assertFalse(validated.stream().anyMatch(violation -> "high".equals(violation.getPropertyPath().toString()) || "low".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickResponseDtoHighLowerThanLowIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 2d, 3d, 2d, 2d);
      //when
      final var validated = validator.validate(candlestickResponseDto);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "high".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickResponseDtoHighLowerThanOpenIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 2d, 2d, 3d, 2d);
      //when
      final var validated = validator.validate(candlestickResponseDto);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "high".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickResponseDtoHighLowerThanCloseIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 2d, 2d, 2d, 3d);
      //when
      final var validated = validator.validate(candlestickResponseDto);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "high".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickResponseDtoLowHigherThanOpenIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 3d, 3d, 2d, 3d);
      //when
      final var validated = validator.validate(candlestickResponseDto);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "low".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickResponseDtoLowHigherThanCloseIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 3d, 3d, 3d, 2d);
      //when
      final var validated = validator.validate(candlestickResponseDto);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "low".equals(violation.getPropertyPath().toString())));
    }
  }
}