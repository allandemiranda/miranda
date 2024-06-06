package lu.forex.system.controllers;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.NewSymbolDto;
import lu.forex.system.dtos.ResponseSymbolDto;
import lu.forex.system.operations.SymbolOperation;
import lu.forex.system.services.SymbolService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class SymbolController implements SymbolOperation {

  private final SymbolService symbolService;

  @Override
  public Collection<ResponseSymbolDto> getSymbols() {
    return this.getSymbolService().getSymbols();
  }

  @Override
  public ResponseSymbolDto getSymbol(final String name) {
    return this.getSymbolService().getSymbol(name);
  }

  @Override
  public ResponseSymbolDto addSymbol(final NewSymbolDto symbolDto) {
    return this.getSymbolService().addSymbol(symbolDto);
  }
}
