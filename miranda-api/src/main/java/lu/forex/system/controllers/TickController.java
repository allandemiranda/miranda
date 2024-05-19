package lu.forex.system.controllers;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import lu.forex.system.operations.TickOperations;
import lu.forex.system.services.TickService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TickController implements TickOperations {

  private final TickService tickService;

  @Override
  public Collection<TickResponseDto> getTicksBySymbolName(final String symbolName) {
    return this.getTickService().getTicks(symbolName);
  }

  @Override
  public TickResponseDto addTick(final TickCreateDto tickCreateDto, final String symbolName) {
    return this.getTickService().addTick(tickCreateDto, symbolName);
  }
}
