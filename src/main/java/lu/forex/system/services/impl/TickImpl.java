package lu.forex.system.services.impl;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.repositories.TickRepository;
import lu.forex.system.services.TickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter(AccessLevel.PRIVATE)
public class TickImpl implements TickService {

  private final TickRepository tickRepository;
  private final SymbolRepository symbolRepository;
  private final TickMapper tickMapper;

  @Autowired
  public TickImpl(final TickRepository tickRepository, final SymbolRepository symbolRepository, final TickMapper tickMapper) {
    this.tickRepository = tickRepository;
    this.symbolRepository = symbolRepository;
    this.tickMapper = tickMapper;
  }

  @Override
  public @Nonnull Collection<@NotNull TickResponseDto> getTicks(final @Nonnull String symbolName) {
    return this.getTickRepository().findBySymbol_NameOrderByTimestampAsc(symbolName).stream().map(this.getTickMapper()::toDto).toList();
  }

  @Override
  public @Nonnull TickResponseDto addTick(final @Nonnull TickCreateDto tickCreateDto, final @Nonnull String symbolName) {
    final Symbol symbol = this.getSymbolRepository().findFirstByNameOrderByNameAsc(symbolName)
        .orElseThrow(() -> new SymbolNotFoundException("Symbol not found"));
    final Tick tick = this.getTickMapper().toEntity(tickCreateDto);
    tick.setSymbol(symbol);
    final Tick saved = this.getTickRepository().save(tick);
    return this.getTickMapper().toDto(saved);
  }
}
