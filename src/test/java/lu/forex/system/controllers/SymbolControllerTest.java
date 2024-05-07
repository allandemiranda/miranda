package lu.forex.system.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import lu.forex.system.enums.Currency;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.services.SymbolService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
  void getSymbols_ReturnsCollectionOfSymbols() {
    SymbolResponseDto symbol1 = new SymbolResponseDto("ABC", Mockito.mock(Currency.class), Mockito.mock(Currency.class), 1, 1d, 1d, "");
    SymbolResponseDto symbol2 = new SymbolResponseDto("XYZ", Mockito.mock(Currency.class), Mockito.mock(Currency.class), 1, 1d, 1d, "");
    when(symbolService.getSymbols()).thenReturn(Arrays.asList(symbol1, symbol2));

    Collection<SymbolResponseDto> symbols = symbolController.getSymbols();

    assertEquals(2, symbols.size());
    assertTrue(symbols.contains(symbol1));
    assertTrue(symbols.contains(symbol2));
  }

  @Test
  void getSymbol_ExistingSymbol_ReturnsSymbol() {
    SymbolResponseDto symbol = new SymbolResponseDto("ABC", Mockito.mock(Currency.class), Mockito.mock(Currency.class), 1, 1d, 1d, "");
    when(symbolService.getSymbol("ABC")).thenReturn(Optional.of(symbol));

    SymbolResponseDto result = symbolController.getSymbol("ABC");

    assertEquals(symbol, result);
  }

  @Test
  void getSymbol_NonExistingSymbol_ThrowsException() {
    when(symbolService.getSymbol("NON_EXISTING")).thenReturn(Optional.empty());

    assertThrows(SymbolNotFoundException.class, () -> symbolController.getSymbol("NON_EXISTING"));
  }

  @Test
  void addSymbol_ReturnsAddedSymbol() {
    SymbolCreateDto createDto = new SymbolCreateDto("ABC", Mockito.mock(Currency.class), Mockito.mock(Currency.class), 1, 1d, 1d);
    SymbolResponseDto addedSymbol = new SymbolResponseDto("ABC", Mockito.mock(Currency.class), Mockito.mock(Currency.class), 1, 1d, 1d, "");
    when(symbolService.addSymbol(createDto)).thenReturn(addedSymbol);

    SymbolResponseDto result = symbolController.addSymbol(createDto);

    assertEquals(addedSymbol, result);
  }

  @Test
  void updateSymbol_ValidSymbol_UpdatesSuccessfully() {
    SymbolUpdateDto updateDto = new SymbolUpdateDto(1, 2d, 3d);
    String name = "ABC";

    symbolController.updateSymbol(updateDto, name);

    verify(symbolService).updateSymbol(updateDto, name);
  }

  @Test
  void deleteSymbol_ValidSymbol_DeletesSuccessfully() {
    String name = "ABC";

    symbolController.deleteSymbol(name);

    verify(symbolService).deleteSymbol(name);
  }
}