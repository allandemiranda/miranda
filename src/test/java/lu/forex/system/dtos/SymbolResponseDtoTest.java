package lu.forex.system.dtos;

import jakarta.validation.Validation;
import java.util.Arrays;
import lu.forex.system.enums.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SymbolResponseDtoTest {

  @ParameterizedTest
  @ValueSource(strings = {"EURUSD", "USDBRL", "123456"})
  void testSymbolResponseDtoWhenNameNotNullAndSixCharacterIsValid(String name) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "name", name);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "12345", "1234567"})
  void testSymbolResponseDtoWhenNameLessOrPlusThanSixCharacterIsInvalid(String name) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "name", name);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testSymbolResponseDtoWhenNameIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "name", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolResponseDtoWhenCurrencyBaseNotNullIsValid(Currency currencyBase) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "currencyBase", currencyBase);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testSymbolResponseDtoWhenCurrencyBaseIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "currencyBase", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolResponseDtoWhenCurrencyQuoteNotNullIsValid(Currency currencyQuote) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "currencyQuote", currencyQuote);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testSymbolResponseDtoWhenCurrencyQuoteIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "currencyQuote", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {4, 5, 6})
  void testSymbolResponseDtoWhenDigitsPositiveIsValid(int digits) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "digits", digits);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, -1, 0})
  void testSymbolResponseDtoWhenNegativeOrZeroIsValid(int digits) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "digits", digits);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, 1.15, -1d, 0d, 1d, 1.15, Double.MAX_VALUE})
  void testSymbolResponseDtoWhenSwapLongIsValid(double swap) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "swapLong", swap);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, 1.15, -1d, 0d, 1d, 1.15, Double.MAX_VALUE})
  void testSymbolResponseDtoWhenSwapShortIsValid(double swap) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "swapShort", swap);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolResponseDtoWhenCurrenciesAreEqualIsInvalid(Currency currency) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var SymbolResponseDto = new SymbolResponseDto("123456", currency, currency, 1, 0, 0, "");
      //when
      final var validated = validator.validate(SymbolResponseDto);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "currencyBase".equals(violation.getPropertyPath().toString())));
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolResponseDtoWhenCurrenciesAreNotEqualIsValid(Currency currency) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var currencyQuote = Arrays.stream(Currency.values()).filter(c -> !currency.equals(c)).findFirst().orElse(null);
      assert currencyQuote != null;
      final var SymbolResponseDto = new SymbolResponseDto("123456", currency, currencyQuote, 1, 0, 0, "");
      //when
      final var validated = validator.validate(SymbolResponseDto);
      //then
      Assertions.assertFalse(validated.stream().anyMatch(violation -> "currencyBase".equals(violation.getPropertyPath().toString())));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"EUR vs USD", "notEmpty"})
  void testSymbolResponseDtoWhenDescriptionNotNullAndNotBlankIsValid(String description) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "description", description);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testSymbolResponseDtoWhenDescriptionNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "description", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testSymbolResponseDtoWhenDescriptionNotNullAndBlankIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolResponseDto.class, "description", "");
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }
}