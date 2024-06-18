package lu.forex.system.mappers;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.entities.Trade;

public interface TradeMapper {

  @NotNull
  Trade toEntity(final @NotNull TradeDto tradeDto);

  @NotNull
  TradeDto toDto(final @NotNull Trade trade);
}