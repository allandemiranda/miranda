package lu.forex.system.controllers;

import java.util.Collections;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.services.CandlestickService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

//  @ParameterizedTest
//  @EnumSource(TimeFrame.class)
//  void testGetCandlesticksSuccessful(TimeFrame timeFrame) {
//    //given
//    final var candlestick = Mockito.mock(CandlestickResponseDto.class);
//    //when
//    Mockito.when(candlestickService.getCandlesticks(Mockito.anyString(), Mockito.eq(timeFrame))).thenReturn(Collections.singletonList(candlestick));
//    final var candlesticks = candlestickController.getCandlesticks("EURUSD", timeFrame);
//    //then
//    Assertions.assertEquals(1, candlesticks.size());
//    Assertions.assertTrue(candlesticks.contains(candlestick));
//  }
}