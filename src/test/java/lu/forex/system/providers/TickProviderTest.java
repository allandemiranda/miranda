package lu.forex.system.providers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.exceptions.TickConflictException;
import lu.forex.system.exceptions.TickExistException;
import lu.forex.system.mappers.TickMapper;
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
  private TickMapper tickMapper;

  @InjectMocks
  private TickProvider tickProvider;

  @Test
  void testGetTicksSuccessful() {
    //given
    final var tickCollection = List.of(new Tick());
    final var tickResponseDto = Mockito.mock(TickResponseDto.class);
    final var symbolName = "TestSymbolName";
    //when
    Mockito.when(tickRepository.findBySymbol_NameOrderByTimestampAsc(symbolName)).thenReturn(tickCollection);
    Mockito.when(tickMapper.toDto(Mockito.any(Tick.class))).thenReturn(tickResponseDto);
    final var ticks = tickProvider.getTicks(symbolName);
    //then
    Assertions.assertNotNull(ticks);
    Assertions.assertEquals(1, ticks.size());
    Assertions.assertTrue(ticks.stream().anyMatch(tickResponseDto::equals));
  }

  @Test
  void testAddTickSuccessful() {
    //given
    final var tickCreateDto = Mockito.mock(TickCreateDto.class);
    final var symbolName = "TestSymbolName";
    final var symbol = Mockito.mock(Symbol.class);
    final var timestamp = LocalDateTime.now().minusSeconds(1);
    final var tick = Mockito.mock(Tick.class);
    final var tickResponseDto = Mockito.mock(TickResponseDto.class);
    //when
    Mockito.when(symbolRepository.findFirstByNameOrderByNameAsc(symbolName)).thenReturn(Optional.of(symbol));
    Mockito.when(symbol.getName()).thenReturn(symbolName);
    Mockito.when(tickCreateDto.timestamp()).thenReturn(timestamp);
    Mockito.when(tickRepository.existsBySymbol_NameAndTimestamp(symbolName, timestamp)).thenReturn(false);
    Mockito.when(tickRepository.findFirstBySymbol_NameOrderByTimestampDesc(symbolName)).thenReturn(Optional.of(tick));
    Mockito.when(tick.getTimestamp()).thenReturn(timestamp);
    Mockito.when(tickMapper.toEntity(tickCreateDto)).thenReturn(tick);
    Mockito.when(tickRepository.saveAndFlush(tick)).thenReturn(tick);
    Mockito.when(tickMapper.toDto(tick)).thenReturn(tickResponseDto);
    //then
    Assertions.assertDoesNotThrow(() -> tickProvider.addTick(tickCreateDto, symbolName));
  }

  @Test
  void testAddTickConflictException() {
    //given
    final var tickCreateDto = Mockito.mock(TickCreateDto.class);
    final var symbolName = "TestSymbol";
    final var symbol = Mockito.mock(Symbol.class);
    final var timestamp = LocalDateTime.now().minusSeconds(1);
    final var tick = Mockito.mock(Tick.class);
    final var plussed = timestamp.plusDays(1);
    //when
    Mockito.when(symbolRepository.findFirstByNameOrderByNameAsc(symbolName)).thenReturn(Optional.of(symbol));
    Mockito.when(symbol.getName()).thenReturn(symbolName);
    Mockito.when(tickCreateDto.timestamp()).thenReturn(timestamp);
    Mockito.when(tickRepository.existsBySymbol_NameAndTimestamp(symbolName, timestamp)).thenReturn(false);
    Mockito.when(tickRepository.findFirstBySymbol_NameOrderByTimestampDesc(symbolName)).thenReturn(Optional.of(tick));
    Mockito.when(tick.getTimestamp()).thenReturn(plussed);
    //then
    Assertions.assertThrows(TickConflictException.class, () -> tickProvider.addTick(tickCreateDto, symbolName));
  }

  @Test
  void testAddTickExistException() {
    //given
    final var tickCreateDto = Mockito.mock(TickCreateDto.class);
    final var symbolName = "TestSymbol";
    final var symbol = Mockito.mock(Symbol.class);
    final var timestamp = LocalDateTime.now().minusSeconds(1);
    //when
    Mockito.when(symbolRepository.findFirstByNameOrderByNameAsc(symbolName)).thenReturn(Optional.of(symbol));
    Mockito.when(symbol.getName()).thenReturn(symbolName);
    Mockito.when(tickCreateDto.timestamp()).thenReturn(timestamp);
    Mockito.when(tickRepository.existsBySymbol_NameAndTimestamp(symbolName, timestamp)).thenReturn(true);
    //then
    Assertions.assertThrows(TickExistException.class, () -> tickProvider.addTick(tickCreateDto, symbolName));
  }

}