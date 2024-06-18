package lu.forex.system.mappers;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;

public interface TickMapper {

  @NotNull
  Tick toEntity(final @NotNull NewTickDto newTickDto, final @NotNull Symbol symbol);

  @NotNull
  TickDto toDto(final @NotNull Tick tick);

  @NotNull
  Tick toEntity(final @NotNull TickDto tickDto);
}