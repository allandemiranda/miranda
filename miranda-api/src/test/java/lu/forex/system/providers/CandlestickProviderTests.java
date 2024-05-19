package lu.forex.system.providers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.repositories.CandlestickRepository;
import lu.forex.system.utils.TimeFrameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CandlestickProviderTests {

  @Mock
  private CandlestickRepository candlestickRepository;

  @Mock
  private CandlestickMapper candlestickMapper;

  @InjectMocks
  private CandlestickProvider candlestickProvider;

  @ParameterizedTest
  @EnumSource(TimeFrame.class)
  void testGetCandlesticksSuccessful(TimeFrame timeFrame) {
    //given
    final var symbolName = "TestSymbolName";
    final var candlestickCollection = List.of(new Candlestick());
    final var candlestickResponseDto = Mockito.mock(CandlestickResponseDto.class);
    //when
    Mockito.when(candlestickRepository.findBySymbol_NameAndTimeFrameOrderByTimestampAsc(symbolName, timeFrame)).thenReturn(candlestickCollection);
    Mockito.when(candlestickMapper.toDto(Mockito.any(Candlestick.class))).thenReturn(candlestickResponseDto);
    final var result = candlestickProvider.getCandlesticks(symbolName, timeFrame);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.size());
    Assertions.assertTrue(result.stream().anyMatch(candlestickResponseDto::equals));
  }

  @Test
  void testCreateOrUpdateCandlestickSuccessfulCreate() {
    //given
    final var symbol = Mockito.mock(Symbol.class);
    final var timestamp = LocalDateTime.now();
    final double price = 1.1234;
    //when
    Mockito.when(candlestickRepository.findFirstBySymbolAndTimeFrameOrderByTimestampDesc(Mockito.eq(symbol), Mockito.any(TimeFrame.class)))
        .thenReturn(Optional.empty());
    candlestickProvider.createOrUpdateCandlestick(symbol, timestamp, price);
    //then
    Mockito.verify(candlestickRepository, Mockito.times(TimeFrame.values().length))
        .findFirstBySymbolAndTimeFrameOrderByTimestampDesc(Mockito.eq(symbol), Mockito.any(TimeFrame.class));
    Mockito.verify(candlestickRepository, Mockito.times(1)).saveAllAndFlush(Mockito.any());
  }

  @Test
  void testCreateOrUpdateCandlestickSuccessfulUpdate() {
    //given
    final var timestamp = LocalDateTime.now();
    try (final var timeFrameUtilsMockedStatic = Mockito.mockStatic(TimeFrameUtils.class)) {
      final var symbol = Mockito.mock(Symbol.class);
      final double price = 1.1234;
      final var candlestick = Mockito.spy(new Candlestick());
      //when
      Mockito.when(candlestickRepository.findFirstBySymbolAndTimeFrameOrderByTimestampDesc(Mockito.eq(symbol), Mockito.any(TimeFrame.class)))
          .thenReturn(Optional.of(candlestick));
      timeFrameUtilsMockedStatic.when(() -> TimeFrameUtils.getCandlestickDateTime(Mockito.any(), Mockito.any())).thenReturn(timestamp);
      Mockito.when(candlestick.getTimestamp()).thenReturn(timestamp);

      Mockito.when(candlestick.getHigh()).thenReturn(price);
      Mockito.when(candlestick.getLow()).thenReturn(price);

      candlestickProvider.createOrUpdateCandlestick(symbol, timestamp, price);
      //then
      Mockito.verify(candlestickRepository, Mockito.times(TimeFrame.values().length))
          .findFirstBySymbolAndTimeFrameOrderByTimestampDesc(Mockito.eq(symbol), Mockito.any(TimeFrame.class));
      Mockito.verify(candlestickRepository, Mockito.times(1)).saveAllAndFlush(Mockito.any());
      Assertions.assertEquals(price, candlestick.getHigh());
      Assertions.assertEquals(price, candlestick.getClose());
      Assertions.assertEquals(0.0, candlestick.getOpen());
    }
  }

  @Test
  void testCreateOrUpdateCandlestickSuccessfulUpdateLowerPrice() {
    //given
    final var timestamp = LocalDateTime.now();
    try (final var timeFrameUtilsMockedStatic = Mockito.mockStatic(TimeFrameUtils.class)) {
      final var symbol = Mockito.mock(Symbol.class);
      final double price = 1.1234;
      final var candlestick = Mockito.spy(new Candlestick());
      candlestick.setLow(2d);
      candlestick.setHigh(price);
      //when
      Mockito.when(candlestickRepository.findFirstBySymbolAndTimeFrameOrderByTimestampDesc(Mockito.eq(symbol), Mockito.any(TimeFrame.class)))
          .thenReturn(Optional.of(candlestick));
      timeFrameUtilsMockedStatic.when(() -> TimeFrameUtils.getCandlestickDateTime(Mockito.any(), Mockito.any())).thenReturn(timestamp);
      Mockito.when(candlestick.getTimestamp()).thenReturn(timestamp);

      candlestickProvider.createOrUpdateCandlestick(symbol, timestamp, price);
      //then
      Mockito.verify(candlestickRepository, Mockito.times(TimeFrame.values().length))
          .findFirstBySymbolAndTimeFrameOrderByTimestampDesc(Mockito.eq(symbol), Mockito.any(TimeFrame.class));
      Mockito.verify(candlestickRepository, Mockito.times(1)).saveAllAndFlush(Mockito.any());
      Assertions.assertEquals(price, candlestick.getHigh());
      Assertions.assertEquals(price, candlestick.getLow());
      Assertions.assertEquals(price, candlestick.getClose());
      Assertions.assertEquals(0.0, candlestick.getOpen());
    }
  }
}