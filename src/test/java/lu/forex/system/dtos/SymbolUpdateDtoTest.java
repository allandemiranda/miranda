package lu.forex.system.dtos;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SymbolUpdateDtoTest {

  @ParameterizedTest
  @ValueSource(ints = {4, 5, 6})
  void testSymbolUpdateDtoWhenDigitsPositiveIsValid(int digits) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolUpdateDto.class, "digits", digits);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, -1, 0})
  void testSymbolUpdateDtoWhenNegativeOrZeroIsValid(int digits) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolUpdateDto.class, "digits", digits);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, 1.15, -1d, 0d, 1d, 1.15, Double.MAX_VALUE})
  void testSymbolUpdateDtoWhenSwapLongIsValid(double swap) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolUpdateDto.class, "swapLong", swap);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, 1.15, -1d, 0d, 1d, 1.15, Double.MAX_VALUE})
  void testSymbolUpdateDtoWhenSwapShortIsValid(double swap) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolUpdateDto.class, "swapShort", swap);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }
}