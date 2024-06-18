package lu.forex.system.mappers.impls;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.mappers.TickMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TickMapperImpl implements TickMapper {

  private final SymbolMapper symbolMapper;

  @Override
  public @NotNull Tick toEntity(final @NotNull NewTickDto newTickDto, final @NotNull Symbol symbol) {
    final var tick = new Tick();
    tick.setTimestamp(newTickDto.timestamp());
    tick.setBid(newTickDto.bid());
    tick.setAsk(newTickDto.ask());
    tick.setSymbol(symbol);
    tick.setId(symbol.getId());
    return tick;
  }

  @Override
  public @NotNull TickDto toDto(final @NotNull Tick tick) {
    final var id = tick.getId();
    final var symbol = this.getSymbolMapper().toDto(tick.getSymbol());
    final var timestamp = tick.getTimestamp();
    final var bid = tick.getBid();
    final var ask = tick.getAsk();
    final var spread = tick.getSpread();
    return new TickDto(id, symbol, timestamp, bid, ask, spread);
  }

  @Override
  public @NotNull Tick toEntity(final @NotNull TickDto tickDto) {
    final var tick = new Tick();
    tick.setId(tickDto.id());
    final var symbol = this.getSymbolMapper().toEntity(tickDto.symbol());
    tick.setSymbol(symbol);
    tick.setTimestamp(tickDto.timestamp());
    tick.setBid(tickDto.bid());
    tick.setAsk(tickDto.ask());
    tick.setSpread(tickDto.spread());
    return tick;
  }
}

