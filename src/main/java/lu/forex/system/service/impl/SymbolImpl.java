package lu.forex.system.service.impl;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.service.SymbolService;

@Getter(AccessLevel.PRIVATE)
public class SymbolImpl implements SymbolService {

  private final SymbolRepository symbolRepository;
  private final SymbolMapper symbolMapper;

  public SymbolImpl(final SymbolRepository symbolRepository, final SymbolMapper symbolMapper) {
    this.symbolRepository = symbolRepository;
    this.symbolMapper = symbolMapper;
  }

  @Override
  public Collection<SymbolDto> findAll() {
    return this.getSymbolRepository().findAll().stream().map(this.getSymbolMapper()::toDto).collect(Collectors.toCollection(ArrayList::new));
  }

  @Override
  public Optional<SymbolDto> findByName(final String name) {
    return this.getSymbolRepository().findByName(name).map(this.getSymbolMapper()::toDto);
  }

  @Override
  public SymbolDto save(final SymbolDto symbolDto) {
    final Symbol symbol = this.getSymbolMapper().toEntity(symbolDto);
    final Symbol saved = this.getSymbolRepository().save(symbol);
    return this.getSymbolMapper().toDto(saved);
  }

  @Override
  public Optional<SymbolDto> updateDigitsAndSwapLongAndSwapShortByName(@Nonnull final SymbolUpdateDto symbolUpdateDto, final String name) {
    this.getSymbolRepository()
        .updateDigitsAndSwapLongAndSwapShortByNameContains(symbolUpdateDto.digits(), symbolUpdateDto.swapLong(), symbolUpdateDto.swapShort(), name);
    return this.getSymbolRepository().findByName(name).map(this.getSymbolMapper()::toDto);
  }

  @Override
  public boolean deleteByName(final String name) {
    this.getSymbolRepository().deleteByName(name);
    return this.getSymbolRepository().findByName(name).isEmpty();
  }
}
