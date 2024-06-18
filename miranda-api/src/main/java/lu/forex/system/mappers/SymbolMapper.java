package lu.forex.system.mappers;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.NewSymbolDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.entities.Symbol;

public interface SymbolMapper {

  @NotNull
  Symbol toEntity(final @NotNull NewSymbolDto newSymbolDto);

  @NotNull
  Symbol toEntity(final @NotNull SymbolDto symbolDto);

  @NotNull
  SymbolDto toDto(final Symbol symbol);

}