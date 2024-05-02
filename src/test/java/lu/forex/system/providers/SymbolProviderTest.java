package lu.forex.system.providers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.repositories.SymbolRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SymbolProviderTest {

  @Mock
  private SymbolRepository symbolRepository;

  @Mock
  private SymbolMapper symbolMapper;

  @InjectMocks
  private SymbolProvider symbolProvider;

  @Test
  void getSymbols() {
    //given
    final SymbolResponseDto symbolResponseDto = Mockito.mock(SymbolResponseDto.class);
    final Symbol symbol = new Symbol();
    final List<Symbol> symbolList = Collections.singletonList(symbol);

    //when
    Mockito.when(symbolRepository.findAll()).thenReturn(symbolList);
    Mockito.when(symbolMapper.toDto(Mockito.any(Symbol.class))).thenReturn(symbolResponseDto);

    //then
    Assertions.assertNotNull(symbolProvider.getSymbols());
  }

  @Test
  void getSymbol() {
    //given
    final Optional<Symbol> optionalSymbol = Optional.of(new Symbol());
    final SymbolResponseDto symbolResponseDto = Mockito.mock(SymbolResponseDto.class);

    //when
    Mockito.when(symbolRepository.findFirstByNameOrderByNameAsc(Mockito.anyString())).thenReturn(optionalSymbol);
    Mockito.when(symbolMapper.toDto(Mockito.any(Symbol.class))).thenReturn(symbolResponseDto);

    //then
    Assertions.assertNotNull(symbolProvider.getSymbol("TestSymbol"));
  }

  @Test
  void addSymbol() {
    //given
    final SymbolCreateDto symbolCreateDto = Mockito.mock(SymbolCreateDto.class);
    final Symbol symbol = new Symbol();
    final SymbolResponseDto symbolResponseDto = Mockito.mock(SymbolResponseDto.class);

    //when
    Mockito.when(symbolMapper.toEntity(symbolCreateDto)).thenReturn(symbol);
    Mockito.when(symbolRepository.save(symbol)).thenReturn(symbol);
    Mockito.when(symbolMapper.toDto(symbol)).thenReturn(symbolResponseDto);

    //then
    Assertions.assertNotNull(symbolProvider.addSymbol(symbolCreateDto));
  }

  @Test
  void updateSymbol() {
    //given
    final SymbolUpdateDto symbolUpdateDto = Mockito.mock(SymbolUpdateDto.class);
    final String name = "TestSymbol";

    //when
    symbolProvider.updateSymbol(symbolUpdateDto, name);

    //then
    Mockito.verify(symbolRepository)
        .updateDigitsAndSwapLongAndSwapShortByName(symbolUpdateDto.digits(), symbolUpdateDto.swapLong(), symbolUpdateDto.swapShort(), name);
  }

  @Test
  void deleteSymbol() {
    //given
    final String name = "TestSymbol";

    //when
    symbolProvider.deleteSymbol(name);

    //then
    Mockito.verify(symbolRepository).deleteByName(name);
  }


}