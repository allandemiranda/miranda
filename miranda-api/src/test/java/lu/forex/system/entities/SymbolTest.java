package lu.forex.system.entities;

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
class SymbolTest {

  @ParameterizedTest
  @ValueSource(strings = {"EURUSD", "USDBRL", "123456"})
  void testSymbolWhenNameNotNullAndSixCharacterIsValid(String name) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Symbol.class, "name", name);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "12345", "1234567"})
  void testSymbolWhenNameLessOrPlusThanSixCharacterIsInvalid(String name) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Symbol.class, "name", name);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @Test
  void testSymbolWhenNameIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Symbol.class, "name", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolWhenCurrencyBaseNotNullIsValid(Currency currencyBase) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Symbol.class, "currencyBase", currencyBase);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testSymbolWhenCurrencyBaseIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Symbol.class, "currencyBase", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolWhenCurrencyQuoteNotNullIsValid(Currency currencyQuote) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Symbol.class, "currencyQuote", currencyQuote);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @Test
  void testSymbolWhenCurrencyQuoteIsNullIsInvalid() {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Symbol.class, "currencyQuote", null);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {4, 5, 6})
  void testSymbolWhenDigitsPositiveIsValid(int digits) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Symbol.class, "digits", digits);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, -1, 0})
  void testSymbolWhenNegativeOrZeroIsValid(int digits) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Symbol.class, "digits", digits);
      //then
      Assertions.assertFalse(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, 1.15, -1d, 0d, 1d, 1.15, Double.MAX_VALUE})
  void testSymbolWhenSwapLongIsValid(double swap) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Symbol.class, "swapLong", swap);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, 1.15, -1d, 0d, 1d, 1.15, Double.MAX_VALUE})
  void testSymbolWhenSwapShortIsValid(double swap) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      //when
      final var validated = validator.validateValue(Symbol.class, "swapShort", swap);
      //then
      Assertions.assertTrue(validated.isEmpty());
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolWhenCurrenciesAreEqualIsInvalid(Currency currency) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var symbol = new Symbol();
      symbol.setCurrencyBase(currency);
      symbol.setCurrencyQuote(currency);
      //when
      final var validated = validator.validate(symbol);
      //then
      Assertions.assertTrue(validated.stream().anyMatch(violation -> "currencyBase".equals(violation.getPropertyPath().toString())));
    }
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolWhenCurrenciesAreNotEqualIsValid(Currency currency) {
    try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      //given
      final var validator = validatorFactory.getValidator();
      final var currencyQuote = Arrays.stream(Currency.values()).filter(c -> !currency.equals(c)).findFirst().orElse(null);
      assert currencyQuote != null;
      final var symbol = new Symbol();
      symbol.setCurrencyBase(currency);
      symbol.setCurrencyQuote(currencyQuote);
      //when
      final var validated = validator.validate(symbol);
      //then
      Assertions.assertFalse(validated.stream().anyMatch(violation -> "currencyBase".equals(violation.getPropertyPath().toString())));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"EURUSD", "USDJPY", "GBPUSD", "AUDUSD", "123456"})
  void testSymbolName(String name) {
    //given
    final var symbol = new Symbol();
    //when
    symbol.setName(name);
    //then
    Assertions.assertEquals(name, symbol.getName());
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolCurrencyBase(Currency currencyBase) {
    //given
    final var symbol = new Symbol();
    //when
    symbol.setCurrencyBase(currencyBase);
    //then
    Assertions.assertEquals(currencyBase, symbol.getCurrencyBase());
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolCurrencyQuote(Currency currencyQuote) {
    //given
    final var symbol = new Symbol();
    //when
    symbol.setCurrencyQuote(currencyQuote);
    //then
    Assertions.assertEquals(currencyQuote, symbol.getCurrencyQuote());
  }

  @ParameterizedTest
  @ValueSource(ints = {4, 5, 6})
  void testSymbolDigits(int digits) {
    //given
    final var symbol = new Symbol();
    //when
    symbol.setDigits(digits);
    //then
    Assertions.assertEquals(digits, symbol.getDigits());
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, -1d, 0d, 1d, Double.MAX_VALUE})
  void testSymbolSwapLong(double swapLong) {
    //given
    final var symbol = new Symbol();
    //when
    symbol.setSwapLong(swapLong);
    //then
    Assertions.assertEquals(swapLong, symbol.getSwapLong());
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, -1d, 0d, 1d, Double.MAX_VALUE})
  void testSymbolSwapShort(double swapShort) {
    //given
    final var symbol = new Symbol();
    //when
    symbol.setSwapShort(swapShort);
    //then
    Assertions.assertEquals(swapShort, symbol.getSwapShort());
  }

  @Test
  void testSymbolDescription() {
    //given
    final var symbol = new Symbol();
    final var currencyBase = Currency.values()[0];
    final var currencyQuote = Currency.values()[1];
    //when
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    //then
    final var baseDescription = currencyBase.getDescription();
    final var quoteDescription = currencyQuote.getDescription();
    final var description = baseDescription.concat(" vs ").concat(quoteDescription);
    Assertions.assertEquals(description, symbol.getDescription());
  }

  @Test
  void testSymbolEqualsAndHashCode() {
    //given
    final var name = "EURUSD";
    final var currencyBase = Currency.values()[0];
    final var currencyQuote = Currency.values()[1];
    final var symbol1 = new Symbol();
    final var symbol2 = new Symbol();

    //when
    symbol1.setName(name);
    symbol1.setCurrencyBase(currencyBase);
    symbol1.setCurrencyQuote(currencyQuote);

    symbol2.setName(name);
    symbol2.setCurrencyBase(currencyBase);
    symbol2.setCurrencyQuote(currencyQuote);

    //then
    Assertions.assertEquals(symbol1, symbol2);
    Assertions.assertEquals(symbol1.hashCode(), symbol2.hashCode());
  }

  @Test
  void testSymbolEquals() {
    //given
    final var symbol = new Symbol();
    //when
    final var equals = symbol.equals(symbol);
    //then
    Assertions.assertTrue(equals);
  }

  @Test
  void testSymbolEqualsNull() {
    //given
    final var symbol = new Symbol();
    //when
    final var equals = symbol.equals(null);
    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testSymbolEqualsWrongObjectType() {
    //given
    final var symbol = new Symbol();
    //when
    final var equals = symbol.equals(new Object());
    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testSymbolToString() {
    //given
    final var symbol = new Symbol();
    //when
    final var toString = symbol.toString();
    //then
    Assertions.assertEquals("Symbol(name=null, currencyBase=null, currencyQuote=null, digits=0, swapLong=0.0, swapShort=0.0)", toString);
  }
}