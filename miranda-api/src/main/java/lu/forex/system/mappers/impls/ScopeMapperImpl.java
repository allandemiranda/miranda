package lu.forex.system.mappers.impls;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.entities.Scope;
import lu.forex.system.mappers.ScopeMapper;
import lu.forex.system.mappers.SymbolMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class ScopeMapperImpl implements ScopeMapper {

  private final SymbolMapper symbolMapper;

  @Override
  public @NotNull Scope toEntity(final @NotNull ScopeDto scopeDto) {
    final var scope = new Scope();
    scope.setId(scopeDto.id());
    final var symbol = this.getSymbolMapper().toEntity(scopeDto.symbol());
    scope.setSymbol(symbol);
    scope.setTimeFrame(scopeDto.timeFrame());
    return scope;
  }

  @Override
  public @NotNull ScopeDto toDto(final @NotNull Scope scope) {
    final var id = scope.getId();
    final var scopeSymbol = scope.getSymbol();
    final var symbol = this.getSymbolMapper().toDto(scopeSymbol);
    final var timeFrame = scope.getTimeFrame();
    return new ScopeDto(id, symbol, timeFrame);
  }
}
