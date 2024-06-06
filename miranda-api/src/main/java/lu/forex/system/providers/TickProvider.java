package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.ResponseTickDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.exceptions.TickTimestampOlderException;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.repositories.TickRepository;
import lu.forex.system.services.TickService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TickProvider implements TickService {

  private final TickRepository tickRepository;
  private final SymbolRepository symbolRepository;
  private final TickMapper tickMapper;

  @NotNull
  @Override
  public ResponseTickDto addTickBySymbolName(@NotNull final NewTickDto tickDto, @NotNull final String symbolName) {
    final Symbol symbol = this.getSymbolRepository().getFirstByCurrencyPair_Name(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    final boolean valid = this.getTickRepository()
        .getFirstBySymbolOrderByTimestampDesc(symbol)
        .map(tick -> tick.getTimestamp().isBefore(tickDto.timestamp()))
        .orElse(true);
    if (valid) {
      final Tick tick = this.getTickMapper().toEntity(tickDto, symbol);
      final Tick saved = this.getTickRepository().save(tick);
      return this.getTickMapper().toDto(saved);
    } else {
      throw new TickTimestampOlderException(tickDto.timestamp(), symbolName);
    }
  }

  @NotNull
  @Override
  public Collection<@NotNull ResponseTickDto> getTicksBySymbolName(@NotNull final String symbolName) {
    return this.getTickRepository().findAll().stream().map(this.getTickMapper()::toDto).toList();
  }
}
