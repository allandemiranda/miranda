package lu.forex.system.mappers;

import lombok.NonNull;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.models.SymbolDto;
import lu.forex.system.models.TickDto;

public class TickMapper {

  private TickMapper() {
    throw new IllegalStateException("TickMapper class");
  }

  // To DTO
  public static @NonNull TickDto toDto(final @NonNull Tick tick) {
    final SymbolDto symbolDto = SymbolMapper.toDto(tick.getSymbol());
    return new TickDto(tick.getDateTime(), tick.getBid(), tick.getAsk(), symbolDto);
  }

  // To Entity
  public static @NonNull Tick toEntity(final @NonNull TickDto tickDto) {
    final SymbolDto symbolDto = tickDto.symbol();
    return new Tick(tickDto.dateTime(), tickDto.bid(), tickDto.ask(),
        new Symbol(symbolDto.name(), symbolDto.description(), symbolDto.margin(), symbolDto.profit(), symbolDto.digits(), symbolDto.swapLong(),
            symbolDto.swapShort()));
  }
}
