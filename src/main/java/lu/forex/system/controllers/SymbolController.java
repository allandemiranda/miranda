package lu.forex.system.controllers;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.operations.SymbolOperations;
import lu.forex.system.services.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Getter(AccessLevel.PRIVATE)
public class SymbolController implements SymbolOperations {

  private final SymbolService symbolService;

  @Autowired
  public SymbolController(final SymbolService symbolService) {
    this.symbolService = symbolService;
  }

  @Override
  public Collection<SymbolResponseDto> getSymbols() {
    return this.getSymbolService().getSymbols();
  }

  @Override
  public SymbolResponseDto getSymbol(final String name) {
    return this.getSymbolService().getSymbol(name).orElseThrow(SymbolNotFoundException::new);
  }

  @Override
  public SymbolResponseDto addSymbol(final SymbolCreateDto symbolCreateDto) {
    return this.getSymbolService().addSymbol(symbolCreateDto);
  }

  @Override
  public SymbolResponseDto updateSymbol(final SymbolUpdateDto symbolUpdateDto, final String name) {
    return this.getSymbolService().updateSymbol(symbolUpdateDto, name);
  }

  @Override
  public void deleteSymbol(final String name) {
    if (!this.getSymbolService().deleteSymbol(name)) {
      throw new SymbolNotFoundException();
    }
  }
}
