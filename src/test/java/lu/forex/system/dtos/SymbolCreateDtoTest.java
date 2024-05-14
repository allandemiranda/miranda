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
class SymbolCreateDtoTest {

  @ParameterizedTest
  @ValueSource(strings = {"EURUSD", "USDBRL", "123456"})
  void testSymbolCreateDtoWhenNameNotNullAndSixCharacterIsValid(String name) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolCreateDto.class, "name", name);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "12345", "1234567"})
  void testSymbolCreateDtoWhenNameLessOrPlusThanSixCharacterIsInvalid(String name) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolCreateDto.class, "name", name);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testSymbolCreateDtoWhenNameIsNullIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolCreateDto.class, "name", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolCreateDtoWhenCurrencyBaseNotNullIsValid(Currency currencyBase) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolCreateDto.class, "currencyBase", currencyBase);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testSymbolCreateDtoWhenCurrencyBaseIsNullIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolCreateDto.class, "currencyBase", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolCreateDtoWhenCurrencyQuoteNotNullIsValid(Currency currencyQuote) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolCreateDto.class, "currencyQuote", currencyQuote);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testSymbolCreateDtoWhenCurrencyQuoteIsNullIsInvalid() {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolCreateDto.class, "currencyQuote", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {4, 5, 6})
  void testSymbolCreateDtoWhenDigitsPositiveIsValid(int digits) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolCreateDto.class, "digits", digits);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, -1, 0})
  void testSymbolCreateDtoWhenNegativeOrZeroIsValid(int digits) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolCreateDto.class, "digits", digits);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, 1.15, -1d, 0d, 1d, 1.15, Double.MAX_VALUE})
  void testSymbolCreateDtoWhenSwapLongIsValid(double swap) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolCreateDto.class, "swapLong", swap);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, 1.15, -1d, 0d, 1d, 1.15, Double.MAX_VALUE})
  void testSymbolCreateDtoWhenSwapShortIsValid(double swap) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(SymbolCreateDto.class, "swapShort", swap);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolCreateDtoWhenCurrenciesAreEqualIsInvalid(Currency currency) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var symbolCreateDto = new SymbolCreateDto("123456", currency, currency, 1, 0, 0);
      //when
      final var validated = validator.validate(symbolCreateDto);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "currencyBase".equals(violation.getPropertyPath().toString())));
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolCreateDtoWhenCurrenciesAreNotEqualIsValid(Currency currency) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var currencyQuote = Arrays.stream(Currency.values()).filter(c -> !currency.equals(c)).findFirst().orElse(null);
      assert currencyQuote != null;
      final var symbolCreateDto = new SymbolCreateDto("123456", currency, currencyQuote, 1, 0, 0);
      //when
      final var validated = validator.validate(symbolCreateDto);
      //then
      Assertions.assertFalse(validated.stream().anyMatch(violation -> "currencyBase".equals(violation.getPropertyPath().toString())));
    }
  }
}