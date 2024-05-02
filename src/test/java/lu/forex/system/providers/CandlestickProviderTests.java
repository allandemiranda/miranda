package lu.forex.system.providers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.repositories.CandlestickRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
  private CandlestickProvider candlestickService;

  @ParameterizedTest
  @CsvSource(textBlock = """
      EURUSD, M15
      USDJPY, H1
      GBPUSD, D1
      """)
  void getCandlesticks(final String symbolName, final TimeFrame timeFrame) {
    //given
    final Candlestick candlestick1 = new Candlestick();
    final Candlestick candlestick2 = new Candlestick();
    final List<Candlestick> candlesticks = Arrays.asList(candlestick1, candlestick2);
    final CandlestickResponseDto dto1 = Mockito.mock(CandlestickResponseDto.class);
    final CandlestickResponseDto dto2 = Mockito.mock(CandlestickResponseDto.class);
    final List<CandlestickResponseDto> expectedDtos = Arrays.asList(dto1, dto2);

    //when
    Mockito.when(candlestickRepository.findBySymbol_NameAndTimeFrameOrderByTimestampAsc(symbolName, timeFrame)).thenReturn(candlesticks);
    Mockito.when(candlestickMapper.toDto(candlestick1)).thenReturn(dto1);
    Mockito.when(candlestickMapper.toDto(candlestick2)).thenReturn(dto2);
    final Collection<CandlestickResponseDto> actualDtos = candlestickService.getCandlesticks(symbolName, timeFrame);

    //then
    Assertions.assertEquals(expectedDtos.size(), actualDtos.size());
    Assertions.assertEquals(expectedDtos.get(0), actualDtos.iterator().next());
    Assertions.assertEquals(expectedDtos.get(1), actualDtos.toArray()[1]);
    Mockito.verify(candlestickRepository).findBySymbol_NameAndTimeFrameOrderByTimestampAsc(symbolName, timeFrame);
    Mockito.verify(candlestickMapper, Mockito.times(2)).toDto(Mockito.any());
  }
}