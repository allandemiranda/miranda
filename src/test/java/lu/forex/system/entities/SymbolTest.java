package lu.forex.system.entities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import lu.forex.system.enums.Currency;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SymbolTest {

  private static ValidatorFactory validatorFactory;

  @NotNull
  private static Currency randomCurrency() {
    final Currency[] currencies = Currency.values();
    final int seed = LocalDateTime.now().getNano();
    final Random random = new Random(seed);
    final int nextInt = random.nextInt(currencies.length);
    return currencies[nextInt];
  }

  @NotNull
  private static Currency randomCurrency(final @NotNull Currency diffOf) {
    Currency next = randomCurrency();
    while (next == diffOf) {
      next = randomCurrency();
    }
    return next;
  }

  @BeforeAll
  static void setUpBeforeClass() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
  }

  @AfterAll
  static void tearDownAfterClass() {
    validatorFactory.close();
  }

  @ParameterizedTest
  @ValueSource(strings = {"ABCDEF", "123456", "EURUSD"})
  void testSymbolAnyNameWith6CharacterIsValid(String name) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);

    //when
    symbol.setName(name);
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(symbolConstraintViolation -> "name".equals(symbolConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ABC", "12345", "EURUSDBRL"})
  void testSymbolAnyNameWithOut6CharacterIsInvalid(String name) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);

    //when
    symbol.setName(name);
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        symbolConstraintViolation -> "name".equals(symbolConstraintViolation.getPropertyPath().toString())
                                     && "{jakarta.validation.constraints.Size.message}".equals(symbolConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @EmptySource
  void testSymbolWithEmptyNameIsInvalid(String name) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);

    //when
    symbol.setName(name);
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        symbolConstraintViolation -> "name".equals(symbolConstraintViolation.getPropertyPath().toString())
                                     && "{jakarta.validation.constraints.Size.message}".equals(symbolConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @NullSource
  void testSymbolWithNullNameIsInvalid(String name) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);

    //when
    symbol.setName(name);
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        symbolConstraintViolation -> "name".equals(symbolConstraintViolation.getPropertyPath().toString())
                                     && "{jakarta.validation.constraints.NotBlank.message}".equals(symbolConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @ValueSource(strings = {"EURUSD", "USDJPY", "GBPUSD", "AUDUSD", "123456"})
  void testSymbolName(String name) {
    //given
    final Symbol symbol = new Symbol();

    //when
    symbol.setName(name);

    //then
    Assertions.assertEquals(name, symbol.getName());
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolWithAnyCurrencyBaseIsValid(Currency currencyBase) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyQuote = randomCurrency(currencyBase);

    //when
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(symbolConstraintViolation -> "currencyBase".equals(symbolConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @NullSource
  void testSymbolWithNullCurrencyBaseIsInvalid(Currency currencyBase) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = Mockito.mock(Symbol.class);
    final Currency currencyQuote = randomCurrency();

    //when
    symbol.setCurrencyBase(currencyBase);
    Mockito.when(symbol.getCurrencyBase()).thenReturn(randomCurrency(currencyQuote));
    Mockito.when(symbol.getCurrencyQuote()).thenReturn(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        symbolConstraintViolation -> "currencyBase".equals(symbolConstraintViolation.getPropertyPath().toString())
                                     && "{jakarta.validation.constraints.NotNull.message}".equals(symbolConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolCurrencyBase(Currency currencyBase) {
    //given
    final Symbol symbol = new Symbol();

    //when
    symbol.setCurrencyBase(currencyBase);

    //then
    Assertions.assertEquals(currencyBase, symbol.getCurrencyBase());
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolWithAnyCurrencyQuoteIsValid(Currency currencyQuote) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency(currencyQuote);

    //when
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(symbolConstraintViolation -> "currencyQuote".equals(symbolConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @NullSource
  void testSymbolWithNullCurrencyQuoteIsInvalid(Currency currencyQuote) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = Mockito.mock(Symbol.class);
    final Currency currencyBase = randomCurrency();

    //when
    symbol.setCurrencyQuote(currencyQuote);
    Mockito.when(symbol.getCurrencyQuote()).thenReturn(currencyBase);
    Mockito.when(symbol.getCurrencyBase()).thenReturn(randomCurrency(currencyBase));
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        symbolConstraintViolation -> "currencyQuote".equals(symbolConstraintViolation.getPropertyPath().toString())
                                     && "{jakarta.validation.constraints.NotNull.message}".equals(symbolConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolCurrencyQuote(Currency currencyQuote) {
    //given
    final Symbol symbol = new Symbol();

    //when
    symbol.setCurrencyQuote(currencyQuote);

    //then
    Assertions.assertEquals(currencyQuote, symbol.getCurrencyQuote());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 4, 5, Integer.MAX_VALUE})
  void testSymbolWithAnyPositivePlus1DigitIsValid(int digits) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);

    //when
    symbol.setDigits(digits);
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(symbolConstraintViolation -> "digits".equals(symbolConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, -1, 0})
  void testSymbolWithNegativeOrZeroDigitIsInvalid(int digits) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);

    //when
    symbol.setDigits(digits);
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        symbolConstraintViolation -> "digits".equals(symbolConstraintViolation.getPropertyPath().toString())
                                     && "{jakarta.validation.constraints.Positive.message}".equals(symbolConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testSymbolDigits() {
    //given
    final Symbol symbol = new Symbol();
    final int digits = 1;

    //when
    symbol.setDigits(digits);

    //then
    Assertions.assertEquals(digits, symbol.getDigits());
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, -1d, 0d, 1d, Double.MAX_VALUE})
  void testSymbolWithAnySwapLongIsValid(double swapLong) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);

    //when
    symbol.setSwapLong(swapLong);
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(symbolConstraintViolation -> "swapLong".equals(symbolConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, -1d, 0d, 1d, Double.MAX_VALUE})
  void testSymbolSwapLong(double swapLong) {
    //given
    final Symbol symbol = new Symbol();

    //when
    symbol.setSwapLong(swapLong);

    //then
    Assertions.assertEquals(swapLong, symbol.getSwapLong());
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, -1d, 0d, 1d, Double.MAX_VALUE})
  void testSymbolWithAnySwapShortIsValid(double swapShort) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);

    //when
    symbol.setSwapShort(swapShort);
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //then
    Assertions.assertFalse(
        validate.stream().anyMatch(symbolConstraintViolation -> "swapShort".equals(symbolConstraintViolation.getPropertyPath().toString())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_VALUE, -1d, 0d, 1d, Double.MAX_VALUE})
  void testSymbolSwapShort(double swapShort) {
    //given
    final Symbol symbol = new Symbol();

    //when
    symbol.setSwapShort(swapShort);

    //then
    Assertions.assertEquals(swapShort, symbol.getSwapShort());
  }

  @Test
  void testSymbolDescription() {
    //given
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);

    //when
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);

    //then
    final String baseDescription = currencyBase.getDescription();
    final String quoteDescription = currencyQuote.getDescription();
    final String description = baseDescription.concat(" vs ").concat(quoteDescription);
    Assertions.assertEquals(description, symbol.getDescription());
  }

  @Test
  void testSymbolRepresentationCurrencyNotEqualIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);

    //when
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //when
    Assertions.assertFalse(validate.stream().anyMatch(
        symbolConstraintViolation -> "{lu.forex.system.annotations.SymbolCurrencyRepresentation}".equals(
            symbolConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testSymbolRepresentationCurrencyIsEqualIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Symbol symbol = new Symbol();
    final Currency currencyBase = randomCurrency();

    //when
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyBase);
    final Set<ConstraintViolation<Symbol>> validate = validator.validate(symbol);

    //when
    Assertions.assertTrue(validate.stream().anyMatch(symbolConstraintViolation -> "{lu.forex.system.annotations.SymbolCurrencyRepresentation}".equals(
        symbolConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testTickRepresentationBidHighThanAskIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final Tick tick = new Tick();

    //when
    tick.setAsk(1d);
    tick.setBid(2d);
    final Set<ConstraintViolation<Tick>> validate = validator.validate(tick);

    //when
    Assertions.assertTrue(validate.stream().anyMatch(
        tickConstraintViolation -> "{lu.forex.system.annotations.TickRepresentation}".equals(tickConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testSymbolEqualsAndHashCode() {
    //given
    final String name = "EURUSD";
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);
    final Symbol symbol1 = new Symbol();
    final Symbol symbol2 = new Symbol();

    //when
    symbol1.setName(name);
    symbol1.setCurrencyBase(currencyBase);
    symbol1.setCurrencyQuote(currencyQuote);
    symbol1.setDigits(1);
    symbol1.setSwapLong(2d);
    symbol1.setSwapLong(3d);

    symbol2.setName(name);
    symbol2.setCurrencyBase(currencyBase);
    symbol2.setCurrencyQuote(currencyQuote);
    symbol1.setDigits(4);
    symbol1.setSwapLong(5d);
    symbol1.setSwapLong(6d);

    //then
    Assertions.assertEquals(symbol1, symbol2);
    Assertions.assertEquals(symbol1.hashCode(), symbol2.hashCode());
  }

  @Test
  void testSymbolNameNotEqualsAndHashCode() {
    //given
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);
    final Symbol symbol1 = new Symbol();
    final Symbol symbol2 = new Symbol();

    //when
    symbol1.setName("name10");
    symbol1.setCurrencyBase(currencyBase);
    symbol1.setCurrencyQuote(currencyQuote);

    symbol2.setName("name11");
    symbol2.setCurrencyBase(currencyBase);
    symbol2.setCurrencyQuote(currencyQuote);

    //then
    Assertions.assertNotEquals(symbol1, symbol2);
    Assertions.assertNotEquals(symbol1.hashCode(), symbol2.hashCode());
  }

  @Test
  void testSymbolBaseNotEqualsAndHashCode() {
    //given
    final String name = "EURUSD";
    final Currency currencyQuote = randomCurrency();
    final Currency currencyBase = randomCurrency(currencyQuote);
    final Symbol symbol1 = new Symbol();
    final Symbol symbol2 = new Symbol();

    //when
    symbol1.setName(name);

    symbol1.setCurrencyBase(currencyBase);
    symbol1.setCurrencyQuote(currencyQuote);

    symbol2.setName(name);
    symbol2.setCurrencyBase(randomCurrency(currencyBase));
    symbol2.setCurrencyQuote(currencyQuote);

    //then
    Assertions.assertNotEquals(symbol1, symbol2);
    Assertions.assertNotEquals(symbol1.hashCode(), symbol2.hashCode());
  }

  @Test
  void testSymbolQuoteNotEqualsAndHashCode() {
    //given
    final String name = "EURUSD";
    final Currency currencyBase = randomCurrency();
    final Currency currencyQuote = randomCurrency(currencyBase);
    final Symbol symbol1 = new Symbol();
    final Symbol symbol2 = new Symbol();

    //when
    symbol1.setName(name);
    symbol1.setCurrencyBase(currencyBase);
    symbol1.setCurrencyQuote(currencyQuote);

    symbol2.setName(name);
    symbol2.setCurrencyBase(currencyBase);
    symbol2.setCurrencyQuote(randomCurrency(currencyQuote));

    //then
    Assertions.assertNotEquals(symbol1, symbol2);
    Assertions.assertNotEquals(symbol1.hashCode(), symbol2.hashCode());
  }

  @Test
  void testSymbolEquals() {
    //given
    final Symbol symbol = new Symbol();

    //when
    final boolean equals = symbol.equals(symbol);

    //then
    Assertions.assertTrue(equals);
  }

  @Test
  void testSymbolEqualsNull() {
    //given
    final Symbol symbol = new Symbol();

    //when
    final boolean equals = symbol.equals(null);

    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testSymbolEqualsWrongObjectType() {
    //given
    final Symbol symbol = new Symbol();

    //when
    final boolean equals = symbol.equals(new Object());

    //then
    Assertions.assertFalse(equals);
  }

  @Test
  void testSymbolToString() {
    //given
    final Symbol symbol = new Symbol();

    //when
    final String toString = symbol.toString();

    //then
    Assertions.assertEquals("Symbol(name=null, currencyBase=null, currencyQuote=null, digits=0, swapLong=0.0, swapShort=0.0)", toString);
  }
}