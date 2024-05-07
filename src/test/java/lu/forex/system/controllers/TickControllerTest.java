package lu.forex.system.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import lu.forex.system.services.TickService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TickControllerTest {

  @InjectMocks
  private TickController tickController;

  @Mock
  private TickService tickService;

  @Test
  void getTicksBySymbolName_ReturnsCollectionOfTicks() {
    String symbolName = "ABC";
    TickResponseDto tick1 = new TickResponseDto(UUID.randomUUID(), Mockito.mock(SymbolResponseDto.class), LocalDateTime.now(), 1d, 2d);
    TickResponseDto tick2 = new TickResponseDto(UUID.randomUUID(), Mockito.mock(SymbolResponseDto.class), LocalDateTime.now(), 1d, 2d);
    when(tickService.getTicks(symbolName)).thenReturn(Arrays.asList(tick1, tick2));

    Collection<TickResponseDto> ticks = tickController.getTicksBySymbolName(symbolName);

    assertEquals(2, ticks.size());
    assertTrue(ticks.contains(tick1));
    assertTrue(ticks.contains(tick2));
  }

  @Test
  void addTick_ReturnsAddedTick() {
    String symbolName = "ABC";
    TickCreateDto createDto = new TickCreateDto(LocalDateTime.now(), 1d, 2d);
    TickResponseDto addedTick = new TickResponseDto(UUID.randomUUID(), Mockito.mock(SymbolResponseDto.class), LocalDateTime.now(), 1d, 2d);
    when(tickService.addTick(createDto, symbolName)).thenReturn(addedTick);

    TickResponseDto result = tickController.addTick(createDto, symbolName);

    assertEquals(addedTick, result);
  }
}