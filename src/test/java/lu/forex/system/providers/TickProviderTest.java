package lu.forex.system.providers;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.exceptions.TickConflictException;
import lu.forex.system.exceptions.TickExistException;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.repositories.CandlestickRepository;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.repositories.TickRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TickProviderTest {

  @Mock
  private TickRepository tickRepository;

  @Mock
  private SymbolRepository symbolRepository;

  @Mock
  private CandlestickRepository candlestickRepository;

  @Mock
  private TickMapper tickMapper;

  @InjectMocks
  private TickProvider tickProvider;

  @Test
  void testGetTicksSuccessful() {
    //given
    final Collection<Tick> ticks = List.of(new Tick());
    final TickResponseDto tickResponseDto = Mockito.mock(TickResponseDto.class);

    //when
    Mockito.when(tickRepository.findBySymbol_NameOrderByTimestampAsc("TestSymbol")).thenReturn(ticks);
    Mockito.when(tickMapper.toDto(Mockito.any(Tick.class))).thenReturn(tickResponseDto);

    //then
    Assertions.assertNotNull(tickProvider.getTicks("TestSymbol"));
  }

  @Test
  void testAddTickSuccessful() {
    //given
    final Symbol symbol = new Symbol();
    symbol.setName("TestSymbol");
    final Optional<Symbol> optionalSymbol = Optional.of(symbol);
    final LocalDateTime timestamp = LocalDateTime.now();
    final TickCreateDto tickCreateDto = new TickCreateDto(timestamp, 1.0, 2.0);
    final Tick tick = new Tick();
    tick.setTimestamp(timestamp);

    //when
    Mockito.when(symbolRepository.findFirstByNameOrderByNameAsc("TestSymbol")).thenReturn(optionalSymbol);
    Mockito.when(tickRepository.existsBySymbol_NameAndTimestamp("TestSymbol", timestamp)).thenReturn(false);
    Mockito.when(tickRepository.save(Mockito.any(Tick.class))).thenAnswer(invocation -> invocation.getArgument(0));
    Mockito.when(tickMapper.toEntity(tickCreateDto)).thenReturn(tick);
    Mockito.when(tickMapper.toDto(Mockito.any(Tick.class))).thenReturn(Mockito.mock(TickResponseDto.class));

    //then
    final TickResponseDto response = tickProvider.addTick(tickCreateDto, "TestSymbol");
    Assertions.assertNotNull(response);
  }

  @Test
  void testAddTickTickExistsException() {
    //given
    final LocalDateTime now = LocalDateTime.now();
    final TickCreateDto tickCreateDto = new TickCreateDto(now, 1.0, 2.0);
    Symbol symbol = new Symbol();
    symbol.setName("TestSymbol");

    //when
    Mockito.when(symbolRepository.findFirstByNameOrderByNameAsc("TestSymbol")).thenReturn(Optional.of(symbol));
    Mockito.when(tickRepository.existsBySymbol_NameAndTimestamp("TestSymbol", now)).thenReturn(true);

    //then
    Assertions.assertThrows(TickExistException.class, () -> tickProvider.addTick(tickCreateDto, "TestSymbol"));
  }

  @Test
  void testAddTickTickConflictException() {
    //given
    final LocalDateTime now = LocalDateTime.now();
    final TickCreateDto tickCreateDto = new TickCreateDto(now.minusDays(1), 1.0, 2.0);
    Symbol symbol = new Symbol();
    symbol.setName("TestSymbol");
    final Optional<Symbol> optionalSymbol = Optional.of(symbol);
    Tick tick = new Tick();
    tick.setTimestamp(now);
    final Optional<Tick> optionalTick = Optional.of(tick);

    //when
    Mockito.when(symbolRepository.findFirstByNameOrderByNameAsc("TestSymbol")).thenReturn(optionalSymbol);
    Mockito.when(tickRepository.existsBySymbol_NameAndTimestamp("TestSymbol", now.minusDays(1))).thenReturn(false);
    Mockito.when(tickRepository.findFirstBySymbol_NameOrderByTimestampDesc("TestSymbol")).thenReturn(optionalTick);
    Mockito.when(tickMapper.toEntity(tickCreateDto)).thenReturn(tick);

    //then
    Assertions.assertThrows(TickConflictException.class, () -> tickProvider.addTick(tickCreateDto, "TestSymbol"));
  }
}