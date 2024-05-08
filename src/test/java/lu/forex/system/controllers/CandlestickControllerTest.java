package lu.forex.system.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.services.CandlestickService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CandlestickControllerTest {

  @InjectMocks
  private CandlestickController candlestickController;

  @Mock
  private CandlestickService candlestickService;

  @Test
  void getSymbols_ReturnsCollectionOfCandlesticks() {
    String symbolName = "ABC";
    TimeFrame timeFrame = TimeFrame.D1;
    CandlestickResponseDto candlestick1 = new CandlestickResponseDto(UUID.randomUUID(), Mockito.mock(SymbolResponseDto.class), timeFrame,
        LocalDateTime.now(), 1d, 1d, 2d, 1d);
    CandlestickResponseDto candlestick2 = new CandlestickResponseDto(UUID.randomUUID(), Mockito.mock(SymbolResponseDto.class), timeFrame,
        LocalDateTime.now(), 1d, 1d, 2d, 1d);
    when(candlestickService.getCandlesticks(symbolName, timeFrame)).thenReturn(Arrays.asList(candlestick1, candlestick2));

    Collection<CandlestickResponseDto> candlesticks = candlestickController.getCandlesticks(symbolName, timeFrame);

    assertEquals(2, candlesticks.size());
    assertTrue(candlesticks.contains(candlestick1));
    assertTrue(candlesticks.contains(candlestick2));
  }
}