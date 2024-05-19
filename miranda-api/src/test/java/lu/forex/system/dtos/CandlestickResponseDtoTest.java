package lu.forex.system.dtos;

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
class CandlestickResponseDtoTest {

  @Test
  void testCandlestickResponseDtoWhenIdNotNullIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var id = UUID.randomUUID();
      //when      
      final var validated = validator.validateValue(CandlestickResponseDto.class, "id", id);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoWhenIdNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "id", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoWhenSymbolNotNullIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenSymbolNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenTimeFrameNotNullIsValid(TimeFrame timeFrame) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "timeFrame", timeFrame);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoWhenTimeFrameNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "timeFrame", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoWhenTimestampNotNullOnPastIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenTimestampNotNullOnPresentIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenTimestampNotNullOnFutureIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenTimestampIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenHighPositiveIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenHighNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenLowPositiveIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenLowNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenOpenPositiveIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenOpenNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenClosePositiveIsValid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenCloseNegativeOrZeroIsInvalid(double price) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(CandlestickResponseDto.class, "close", price);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCandlestickResponseDtoWhenPriceEqualIsValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 1d, 1d, 1d, 1d);
      //when
      final var validated = validator.validate(candlestickResponseDto);
      //then
      Assertions.assertFalse(validated.stream()
          .anyMatch(violation -> "high".equals(violation.getPropertyPath().toString()) || "low".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickResponseDtoWhenPriceValid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 5d, 2d, 3d, 4d);
      //when
      final var validated = validator.validate(candlestickResponseDto);
      //then
      Assertions.assertFalse(validated.stream()
          .anyMatch(violation -> "high".equals(violation.getPropertyPath().toString()) || "low".equals(violation.getPropertyPath().toString())));
    }
  }

  @Test
  void testCandlestickResponseDtoWhenHighLowerThanLowIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenHighLowerThanOpenIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenHighLowerThanCloseIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenLowHigherThanOpenIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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
  void testCandlestickResponseDtoWhenLowHigherThanCloseIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
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