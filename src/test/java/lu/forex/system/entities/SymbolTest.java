package lu.forex.system.entities;

import lu.forex.system.enums.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SymbolTest {

  @Test
  void getName() {
    //given
    final Symbol symbol = new Symbol();
    final String name = "EURUSD";

    //when
    symbol.setName(name);

    //then
    Assertions.assertEquals(name, symbol.getName());
  }

  @Test
  void getCurrency() {
    //given
    final Symbol symbol = new Symbol();
    final Currency currencyBase = Currency.EUR;
    final Currency currencyQuote = Currency.USD;

    //when
    symbol.setCurrencyBase(currencyBase);
    symbol.setCurrencyQuote(currencyQuote);

    //then
    Assertions.assertEquals(Currency.EUR, symbol.getCurrencyBase());
    Assertions.assertEquals(Currency.USD, symbol.getCurrencyQuote());
  }

  @Test
  void getDescription() {
    //given
    final Symbol symbol = new Symbol();

    //when
    symbol.setCurrencyBase(Currency.EUR);
    symbol.setCurrencyQuote(Currency.USD);

    //then
    Assertions.assertEquals("Euro vs US Dollar", symbol.getDescription());
  }

  @Test
  void getDescriptionBaseThrow() {
    //given
    final Symbol symbol = new Symbol();

    //when
    symbol.setCurrencyQuote(Currency.USD);
    final Executable executable = symbol::getDescription;

    //then
    Assertions.assertThrows(NullPointerException.class, executable);
  }

  @Test
  void getDescriptionQuoteThrow() {
    //given
    final Symbol symbol = new Symbol();

    //when
    symbol.setCurrencyQuote(Currency.USD);
    final Executable executable = symbol::getDescription;

    //then
    Assertions.assertThrows(NullPointerException.class, executable);
  }

  @Test
  void getDigits() {
    //given
    final Symbol symbol = new Symbol();

    //when
    symbol.setDigits(5);

    //then
    Assertions.assertEquals(5, symbol.getDigits());
  }

  @Test
  void testSymbolSwapLong() {
    //given
    final Symbol symbol = new Symbol();

    //when
    symbol.setSwapLong(0.25);

    //then
    Assertions.assertEquals(0.25, symbol.getSwapLong());
  }

  @Test
  void testSymbolSwapShort() {
    //given
    final Symbol symbol = new Symbol();

    //when
    symbol.setSwapShort(-0.1);

    //then
    Assertions.assertEquals(-0.1, symbol.getSwapShort());
  }

  @Test
  void testSymbolEquals() {
    //given
    final Symbol symbol1 = new Symbol();
    symbol1.setCurrencyBase(Currency.EUR);
    symbol1.setCurrencyQuote(Currency.USD);
    symbol1.setDigits(5);
    symbol1.setSwapShort(0.1);
    symbol1.setSwapLong(0.2);
    final Symbol symbol2 = new Symbol();
    symbol2.setCurrencyBase(Currency.GBP);
    symbol2.setCurrencyQuote(Currency.AUD);
    symbol2.setDigits(3);
    symbol2.setSwapShort(-0.1);
    symbol2.setSwapLong(-0.2);

    //when
    symbol1.setName("EURUSD");
    symbol2.setName("EURUSD");

    //then
    Assertions.assertEquals(symbol1, symbol2);
  }

  @Test
  void testSymbolHashCode() {
    //given
    final Symbol symbol1 = new Symbol();
    symbol1.setCurrencyBase(Currency.EUR);
    symbol1.setCurrencyQuote(Currency.USD);
    symbol1.setDigits(5);
    symbol1.setSwapShort(0.1);
    symbol1.setSwapLong(0.2);
    final Symbol symbol2 = new Symbol();
    symbol2.setCurrencyBase(Currency.GBP);
    symbol2.setCurrencyQuote(Currency.AUD);
    symbol2.setDigits(3);
    symbol2.setSwapShort(-0.1);
    symbol2.setSwapLong(-0.2);

    //when
    symbol1.setName("EURUSD");
    symbol2.setName("EURUSD");

    //then
    Assertions.assertEquals(symbol1.hashCode(), symbol2.hashCode());
  }
}