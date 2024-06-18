package lu.forex.system.mappers;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.entities.Candlestick;

public interface CandlestickMapper {

  @NotNull
  Candlestick toEntity(final @NotNull CandlestickDto candlestickDto);

  @NotNull
  CandlestickDto toDto(final @NotNull Candlestick candlestick);
}