package lu.forex.system.controllers;

import java.util.Collections;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import lu.forex.system.services.TickService;
import org.junit.jupiter.api.Assertions;
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
  void testGetTicksBySymbolNameSuccessful() {
    //given
    final var tick = Mockito.mock(TickResponseDto.class);
    //when
    Mockito.when(tickService.getTicks(Mockito.anyString())).thenReturn(Collections.singletonList(tick));
    final var ticks = tickController.getTicksBySymbolName("EURUSD");
    //then
    Assertions.assertEquals(1, ticks.size());
    Assertions.assertTrue(ticks.contains(tick));
  }

  @Test
  void testAddTickSuccessful() {
    //given
    final var createDto = Mockito.mock(TickCreateDto.class);
    final var addedTick = Mockito.mock(TickResponseDto.class);
    //when
    Mockito.when(tickService.addTick(Mockito.any(TickCreateDto.class), Mockito.anyString())).thenReturn(addedTick);
    final var result = tickController.addTick(createDto, "EURUSD");
    //then
    Assertions.assertEquals(addedTick, result);
  }
}