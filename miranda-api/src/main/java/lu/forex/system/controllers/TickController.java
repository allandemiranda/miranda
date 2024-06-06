package lu.forex.system.controllers;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.ResponseTickDto;
import lu.forex.system.operations.TickOperations;
import lu.forex.system.services.TickService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TickController implements TickOperations {

  private final TickService tickService;

  @Override
  public Collection<ResponseTickDto> getTicksBySymbolName(final String symbolName) {
    return this.getTickService().getTicksBySymbolName(symbolName);
  }

  @Override
  public ResponseTickDto addTick(final NewTickDto tickDto, final String symbolName) {
    return this.getTickService().addTickBySymbolName(tickDto, symbolName);
  }
}
