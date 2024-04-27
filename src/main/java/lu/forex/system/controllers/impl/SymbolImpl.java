package lu.forex.system.controllers.impl;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.controllers.SymbolController;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import lu.forex.system.exceptions.SymbolNotDeletedException;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.service.SymbolService;

@Getter(AccessLevel.PRIVATE)
public class SymbolImpl implements SymbolController {

  private final SymbolService symbolService;

  public SymbolImpl(final SymbolService symbolService) {
    this.symbolService = symbolService;
  }

  public Collection<SymbolDto> getSymbols() {
    return this.getSymbolService().findAll();
  }


  public SymbolDto getSymbol(final String name) {
    return this.getSymbolService().findByName(name).orElseThrow(SymbolNotFoundException::new);
  }

  public SymbolDto addSymbol(final SymbolDto symbolDto) {
    return this.getSymbolService().save(symbolDto);
  }

  public SymbolDto updateSymbol(final SymbolUpdateDto symbolUpdateDto, final String name) {
    return this.getSymbolService().updateDigitsAndSwapLongAndSwapShortByName(symbolUpdateDto, name).orElseThrow(SymbolNotFoundException::new);
  }

  public void deleteSymbol(final String name) {
    if (!this.getSymbolService().deleteByName(name)) {
      throw new SymbolNotDeletedException();
    }
  }

}
