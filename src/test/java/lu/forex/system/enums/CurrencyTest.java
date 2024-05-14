package lu.forex.system.enums;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrencyTest {

  @Test
  void testCurrencySize() {
    //given
    final var target = 2;
    //when
    final var realSize = Currency.values().length;
    //then
    Assertions.assertTrue(realSize >= target);
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testCurrencyGetName(Currency currency) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateProperty(currency, "name");
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
      final var validated = validator.validateValue(Currency.class, "name", null);
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
      final var validated = validator.validateValue(Currency.class, "name", "");
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testCurrencyGetDescription(Currency currency) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateProperty(currency, "description");
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testCurrencyWhenDescriptionIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Currency.class, "description", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testCurrencyWhenDescriptionIsBlankIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Currency.class, "description", "");
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }
}