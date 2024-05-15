package lu.forex.system.providers;

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
  void getSymbolsSuccessful() {
    //given
    final var symbolCollection = List.of(new Symbol());
    final var symbolResponseDto = Mockito.mock(SymbolResponseDto.class);
    //when
    Mockito.when(symbolRepository.findAll()).thenReturn(symbolCollection);
    Mockito.when(symbolMapper.toDto(Mockito.any(Symbol.class))).thenReturn(symbolResponseDto);
    final var symbols = symbolProvider.getSymbols();
    //then
    Assertions.assertNotNull(symbols);
    Assertions.assertEquals(1, symbols.size());
    Assertions.assertTrue(symbols.stream().anyMatch(symbolResponseDto::equals));
  }

  @Test
  void getSymbolSuccessful() {
    //given
    final var name = "TestName";
    final var optionalSymbol = Optional.of(new Symbol());
    final var symbolResponseDto = Mockito.mock(SymbolResponseDto.class);
    //when
    Mockito.when(symbolRepository.findFirstByNameOrderByNameAsc(name)).thenReturn(optionalSymbol);
    Mockito.when(symbolMapper.toDto(Mockito.any(Symbol.class))).thenReturn(symbolResponseDto);
    final var symbolResponseDtoOptional = symbolProvider.getSymbol(name);
    //then
    Assertions.assertNotNull(symbolResponseDtoOptional);
    Assertions.assertTrue(symbolResponseDtoOptional.isPresent());
    Assertions.assertEquals(symbolResponseDto, symbolResponseDtoOptional.get());
  }

  @Test
  void addSymbolSuccessful() {
    //given
    final var symbolCreateDto = Mockito.mock(SymbolCreateDto.class);
    final var symbol = Mockito.mock(Symbol.class);
    final var symbolResponseDto = Mockito.mock(SymbolResponseDto.class);
    //when
    Mockito.when(symbolMapper.toEntity(symbolCreateDto)).thenReturn(symbol);
    Mockito.when(symbolRepository.saveAndFlush(symbol)).thenReturn(symbol);
    Mockito.when(symbolMapper.toDto(symbol)).thenReturn(symbolResponseDto);
    final var responseDto = symbolProvider.addSymbol(symbolCreateDto);
    //then
    Assertions.assertNotNull(responseDto);
    Assertions.assertEquals(symbolResponseDto, responseDto);
  }

  @Test
  void updateSymbolSuccessful() {
    //given
    final var symbolUpdateDto = Mockito.mock(SymbolUpdateDto.class);
    final var name = "TestSymbol";
    //when
    symbolProvider.updateSymbol(symbolUpdateDto, name);
    //then
    Mockito.verify(symbolRepository)
        .updateDigitsAndSwapLongAndSwapShortByName(symbolUpdateDto.digits(), symbolUpdateDto.swapLong(), symbolUpdateDto.swapShort(), name);
  }

  @Test
  void deleteSymbolSuccessful() {
    //given
    final var name = "TestSymbol";
    //when
    symbolProvider.deleteSymbol(name);
    //then
    Mockito.verify(symbolRepository).deleteByName(name);
  }

}