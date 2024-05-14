package lu.forex.system.enums;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimeFrameTest {

  @ParameterizedTest
  @EnumSource(TimeFrame.class)
  void testTimeFrameGetName(TimeFrame timeFrame) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateProperty(timeFrame, "name");
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCurrencyWhenNameIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TimeFrame.class, "name", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCurrencyWhenNameIsBlankIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TimeFrame.class, "name", "");
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(TimeFrame.class)
  void testTimeFrameGetTimeValue(TimeFrame timeFrame) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateProperty(timeFrame, "timeValue");
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {-2, -1, 0})
  void testCurrencyWhenTimeValueIsNegativeOrZeroIsInvalid(int timeValue) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TimeFrame.class, "timeValue", timeValue);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(TimeFrame.class)
  void testTimeFrameGetFrame(TimeFrame timeFrame) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateProperty(timeFrame, "frame");
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCurrencyWhenFrameIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(TimeFrame.class, "frame", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }
}