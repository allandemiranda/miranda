package lu.forex.system.controllers;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.operations.SymbolOperations;
import lu.forex.system.services.SymbolService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class SymbolController implements SymbolOperations {

  private final SymbolService symbolService;

  @Override
  public Collection<SymbolResponseDto> getSymbols() {
    return this.getSymbolService().getSymbols();
  }

  @Override
  public SymbolResponseDto getSymbol(final String name) {
    return this.getSymbolService().getSymbol(name).orElseThrow(() -> new SymbolNotFoundException(name));
  }

  @Override
  public SymbolResponseDto addSymbol(final SymbolCreateDto symbolCreateDto) {
    return this.getSymbolService().addSymbol(symbolCreateDto);
  }

  @Override
  public void updateSymbol(final SymbolUpdateDto symbolUpdateDto, final String name) {
    this.getSymbolService().updateSymbol(symbolUpdateDto, name);
  }

  @Override
  public void deleteSymbol(final String name) {
    this.getSymbolService().deleteSymbol(name);
  }
}
