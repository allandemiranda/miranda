package lu.forex.system.listeners;

import java.time.LocalDateTime;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.services.CandlestickService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TickListenerTest {

  @Mock
  private CandlestickService candlestickService;

  @InjectMocks
  private TickListener tickListener;

  @Test
  void testPrePersist1() {
    //given
    final var tick = Mockito.spy(new Tick());
    final var symbol = Mockito.mock(Symbol.class);
    final var timestamp = LocalDateTime.now();
    final var bid = 1d;
    //when
    Mockito.when(tick.getSymbol()).thenReturn(symbol);
    Mockito.when(tick.getTimestamp()).thenReturn(timestamp);
    Mockito.when(tick.getBid()).thenReturn(bid);
    tickListener.prePersist(tick);
    //then
    Mockito.verify(candlestickService, Mockito.times(1)).createOrUpdateCandlestick(symbol, timestamp, bid);
  }
}