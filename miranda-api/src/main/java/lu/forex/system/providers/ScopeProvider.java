package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.entities.Scope;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.exceptions.ScopeNotFoundException;
import lu.forex.system.mappers.ScopeMapper;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.repositories.ScopeRepository;
import lu.forex.system.services.ScopeService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class ScopeProvider implements ScopeService {

  private final ScopeRepository scopeRepository;
  private final SymbolMapper symbolMapper;
  private final ScopeMapper scopeMapper;

  @Override
  public @NotNull Set<ScopeDto> addScope(@NotNull final SymbolDto symbolDto, @NotNull final Collection<TimeFrame> timeFrames) {
    final Symbol symbol = this.getSymbolMapper().toEntity(symbolDto);
    final Collection<Scope> collection = timeFrames.parallelStream().map(timeFrame -> {
      final Scope scope = new Scope();
      scope.setSymbol(symbol);
      scope.setTimeFrame(timeFrame);
      return scope;
    }).toList();
    return this.getScopeRepository().saveAll(collection).parallelStream().map(scope -> this.getScopeMapper().toDto(scope)).collect(Collectors.toSet());
  }

  @Override
  public @NotNull Collection<ScopeDto> getScopesBySymbolName(final @NotNull String symbolName) {
    return this.getScopeRepository().findBySymbolName(symbolName).parallelStream().map(this.getScopeMapper()::toDto).toList();
  }

  @Override
  public @NotNull ScopeDto getScope(final @NotNull String symbolName, final @NotNull TimeFrame timeFrame) {
    final Scope scope = this.getScopeRepository().getBySymbolNameAndTimeFrame(symbolName, timeFrame).orElseThrow(() -> new ScopeNotFoundException(timeFrame, symbolName));
    return this.getScopeMapper().toDto(scope);
  }
}
