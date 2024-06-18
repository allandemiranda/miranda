package lu.forex.system.mappers;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.entities.Scope;

public interface ScopeMapper {

  @NotNull
  Scope toEntity(final @NotNull ScopeDto scopeDto);

  @NotNull
  ScopeDto toDto(final @NotNull Scope scope);

}