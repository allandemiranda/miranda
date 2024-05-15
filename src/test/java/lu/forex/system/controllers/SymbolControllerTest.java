package lu.forex.system.controllers;

import java.util.Collections;
import java.util.Optional;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.services.SymbolService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SymbolControllerTest {

  @InjectMocks
  private SymbolController symbolController;

  @Mock
  private SymbolService symbolService;

  @Test
  void testGetSymbolsSuccessful() {
    //given
    final var symbolResponseDto = Mockito.mock(SymbolResponseDto.class);
    //when
    Mockito.when(symbolService.getSymbols()).thenReturn(Collections.singletonList(symbolResponseDto));
    final var symbols = symbolController.getSymbols();
    //then
    Assertions.assertEquals(1, symbols.size());
    Assertions.assertTrue(symbols.contains(symbolResponseDto));
  }

  @Test
  void testGetSymbolSuccessful() {
    //given
    final var symbolResponseDto = Mockito.mock(SymbolResponseDto.class);
    //when
    Mockito.when(symbolService.getSymbol(Mockito.anyString())).thenReturn(Optional.of(symbolResponseDto));
    final var symbol = symbolController.getSymbol("EURUSD");
    //then
    Assertions.assertEquals(symbolResponseDto, symbol);
  }

  @Test
  void testGetSymbolNotFoundException() {
    //given
    //when
    Mockito.when(symbolService.getSymbol(Mockito.anyString())).thenReturn(Optional.empty());
    //then
    Assertions.assertThrows(SymbolNotFoundException.class, () -> symbolController.getSymbol("EURUSD"));
  }

  @Test
  void testAddSymbolSuccessful() {
    //given
    final var symbolCreateDto = Mockito.mock(SymbolCreateDto.class);
    final var symbolResponseDto = Mockito.mock(SymbolResponseDto.class);
    //when
    Mockito.when(symbolService.addSymbol(symbolCreateDto)).thenReturn(symbolResponseDto);
    final var symbol = symbolController.addSymbol(symbolCreateDto);
    //then
    Assertions.assertEquals(symbolResponseDto, symbol);
  }

  @Test
  void testUpdateSymbolSuccessful() {
    //given
    final var symbolUpdateDto = Mockito.mock(SymbolUpdateDto.class);
    //when
    final Executable executable = () -> symbolController.updateSymbol(symbolUpdateDto, "EURUSD");
    //then
    Assertions.assertDoesNotThrow(executable);
    Mockito.verify(symbolService).updateSymbol(symbolUpdateDto, "EURUSD");
  }

  @Test
  void testDeleteSymbolSuccessful() {
    //given
    final var symbolName = "EURUSD";
    //when
    final Executable executable = () -> symbolController.deleteSymbol(symbolName);
    //then
    Assertions.assertDoesNotThrow(executable);
    Mockito.verify(symbolService).deleteSymbol(symbolName);
  }
}