package lu.forex.system.controllers.impl;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.controllers.TickController;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.service.TickService;

@Getter(AccessLevel.PRIVATE)
public class TickImpl implements TickController {

  private final TickService tickService;

  public TickImpl(final TickService tickService) {
    this.tickService = tickService;
  }

  public Collection<TickDto> getTicksBySymbolName(final String symbolName) {
    return this.getTickService().findAllBySymbolNameOrderByTimestampTimestampDesc(symbolName);
  }

  public TickDto addTick(final TickCreateDto tickCreateDto, final String symbolName) {
    return this.getTickService().save(tickCreateDto, symbolName);
  }
}
