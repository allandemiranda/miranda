package lu.forex.system.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.repositories.TickRepository;
import lu.forex.system.service.TickService;

@Getter(AccessLevel.PRIVATE)
public class TickImpl implements TickService {

  private final TickRepository tickRepository;
  private final SymbolRepository symbolRepository;
  private final TickMapper tickMapper;

  public TickImpl(final TickRepository tickRepository, final SymbolRepository symbolRepository, final TickMapper tickMapper) {
    this.tickRepository = tickRepository;
    this.symbolRepository = symbolRepository;
    this.tickMapper = tickMapper;
  }

  @Override
  public TickDto save(final TickCreateDto tickCreateDto, final String symbolName) {
    final Symbol symbol = this.getSymbolRepository().findByName(symbolName).orElseThrow(SymbolNotFoundException::new);
    final Tick entity = this.getTickMapper().toEntity(tickCreateDto);
    entity.setSymbol(symbol);
    final Tick saved = this.getTickRepository().save(entity);
    return this.getTickMapper().toDto(saved);
  }

  @Override
  public Collection<TickDto> findAllBySymbolNameOrderByTimestampTimestampDesc(final String symbolName) {
    return this.getTickRepository().findAllBySymbolNameOrderByTimestampDesc(symbolName).stream().map(tickMapper::toDto)
        .collect(Collectors.toCollection(ArrayList::new));
  }

}
