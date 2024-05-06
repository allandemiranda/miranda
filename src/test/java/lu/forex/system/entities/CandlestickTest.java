package lu.forex.system.entities;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.UUID;
import lu.forex.system.enums.TimeFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CandlestickTest {

  private UUID uuid;
  private ValidatorFactory validatorFactory;

  @Mock
  private Symbol symbol;

  @BeforeEach
  void setUp() {
    uuid = UUID.randomUUID();
    validatorFactory = Validation.buildDefaultValidatorFactory();
  }

  @AfterEach
  void tearDown() {
    validatorFactory.close();
  }

  @Test
  void testId() {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setId(uuid);

    //then
    Assertions.assertEquals(uuid, candlestick.getId());
  }

  @Test
  void testSymbol() {
    //given
    final Candlestick candlestick = new Candlestick();

    //when
    candlestick.setSymbol(symbol);

    //then
    Assertions.assertEquals(symbol, candlestick.getSymbol());
  }

  @Test
  void testTimeFrame() {
    //given
    final Candlestick candlestick = new Candlestick();
    final TimeFrame timeFrame = TimeFrame.M15;

    //when
    candlestick.setTimeFrame(timeFrame);

    //then
    Assertions.assertEquals(timeFrame, candlestick.getTimeFrame());
  }

  @Test
  void testTimestamp() {
    //given
    final Candlestick candlestick = new Candlestick();
    final LocalDateTime now = LocalDateTime.now();

    //when
    candlestick.setTimestamp(now);

    //then
    Assertions.assertEquals(now, candlestick.getTimestamp());
  }

  @Test
  void testHigh() {
    //given
    final Candlestick candlestick = new Candlestick();
    final double price = 1d;

    //when
    candlestick.setHigh(price);

    //then
    Assertions.assertEquals(price, candlestick.getHigh());
  }

  @Test
  void testLow() {
    //given
    final Candlestick candlestick = new Candlestick();
    final double price = 1d;

    //when
    candlestick.setLow(price);

    //then
    Assertions.assertEquals(price, candlestick.getLow());
  }

  @Test
  void testOpen() {
    //given
    final Candlestick candlestick = new Candlestick();
    final double price = 1d;

    //when
    candlestick.setOpen(price);

    //then
    Assertions.assertEquals(price, candlestick.getOpen());
  }

  @Test
  void testClose() {
    //given
    final Candlestick candlestick = new Candlestick();
    final double price = 1d;

    //when
    candlestick.setClose(price);

    //then
    Assertions.assertEquals(price, candlestick.getClose());
  }

  @Test
  void testEqualsAndHashCode() {
    //given
    final Candlestick candlestick1 = new Candlestick();
    final Candlestick candlestick2 = new Candlestick();
    final TimeFrame timeFrame = TimeFrame.M15;
    final LocalDateTime timestamp = LocalDateTime.now();

    //when
    candlestick1.setId(uuid);
    candlestick1.setSymbol(symbol);
    candlestick1.setTimeFrame(timeFrame);
    candlestick1.setTimestamp(timestamp);

    candlestick2.setId(uuid);
    candlestick2.setSymbol(symbol);
    candlestick2.setTimeFrame(timeFrame);
    candlestick2.setTimestamp(timestamp);

    //then
    Assertions.assertEquals(candlestick1, candlestick2);
    Assertions.assertEquals(candlestick1.hashCode(), candlestick2.hashCode());
  }

  @Test
  void testIdNotEqualsAndHashCode() {
    //given
    final Candlestick candlestick1 = new Candlestick();
    final Candlestick candlestick2 = new Candlestick();
    final TimeFrame timeFrame = TimeFrame.M15;
    final LocalDateTime timestamp = LocalDateTime.now();

    //when
    candlestick1.setId(uuid);
    candlestick1.setSymbol(symbol);
    candlestick1.setTimeFrame(timeFrame);
    candlestick1.setTimestamp(timestamp);

    UUID randomUUID = UUID.randomUUID();
    while (randomUUID.equals(candlestick1.getId())) {
      randomUUID = UUID.randomUUID();
    }
    candlestick2.setId(randomUUID);
    candlestick2.setSymbol(symbol);
    candlestick2.setTimeFrame(timeFrame);
    candlestick2.setTimestamp(timestamp);

    //then
    Assertions.assertNotEquals(candlestick1, candlestick2);
    Assertions.assertNotEquals(candlestick1.hashCode(), candlestick2.hashCode());
  }

  @Test
  void testSymbolNotEqualsAndHashCode() {
    //given
    final Candlestick candlestick1 = new Candlestick();
    final Candlestick candlestick2 = new Candlestick();
    final TimeFrame timeFrame = TimeFrame.M15;
    final LocalDateTime timestamp = LocalDateTime.now();
    final Symbol symbolMocked = Mockito.mock(Symbol.class);

    //when
    candlestick1.setId(uuid);
    candlestick1.setSymbol(symbol);
    candlestick1.setTimeFrame(timeFrame);
    candlestick1.setTimestamp(timestamp);

    candlestick2.setId(uuid);
    candlestick2.setSymbol(symbolMocked);
    candlestick2.setTimeFrame(timeFrame);
    candlestick2.setTimestamp(timestamp);

    //then
    Assertions.assertNotEquals(candlestick1, candlestick2);
    Assertions.assertNotEquals(candlestick1.hashCode(), candlestick2.hashCode());
  }

  @Test
  void testTimeFrameNotEqualsAndHashCode() {
    //given
    final Candlestick candlestick1 = new Candlestick();
    final Candlestick candlestick2 = new Candlestick();
    final LocalDateTime timestamp = LocalDateTime.now();

    //when
    candlestick1.setId(uuid);
    candlestick1.setSymbol(symbol);
    candlestick1.setTimeFrame(TimeFrame.M15);
    candlestick1.setTimestamp(timestamp);

    candlestick2.setId(uuid);
    candlestick2.setSymbol(symbol);
    candlestick2.setTimeFrame(TimeFrame.D1);
    candlestick2.setTimestamp(timestamp);

    //then
    Assertions.assertNotEquals(candlestick1, candlestick2);
    Assertions.assertNotEquals(candlestick1.hashCode(), candlestick2.hashCode());
  }

  @Test
  void testTimestampNotEqualsAndHashCode() {
    //given
    final Candlestick candlestick1 = new Candlestick();
    final Candlestick candlestick2 = new Candlestick();
    final TimeFrame timeFrame = TimeFrame.M15;
    final LocalDateTime timestamp = LocalDateTime.now();

    //when
    candlestick1.setId(uuid);
    candlestick1.setSymbol(symbol);
    candlestick1.setTimeFrame(timeFrame);
    candlestick1.setTimestamp(timestamp);

    candlestick2.setId(uuid);
    candlestick2.setSymbol(symbol);
    candlestick2.setTimeFrame(timeFrame);
    candlestick2.setTimestamp(timestamp.plusYears(1));

    //then
    Assertions.assertNotEquals(candlestick1, candlestick2);
    Assertions.assertNotEquals(candlestick1.hashCode(), candlestick2.hashCode());
  }
}