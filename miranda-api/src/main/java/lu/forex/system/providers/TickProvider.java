package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.exceptions.TickTimestampOlderException;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.repositories.TickRepository;
import lu.forex.system.services.TickService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TickProvider implements TickService {

  private final TickRepository tickRepository;
  private final SymbolMapper symbolMapper;
  private final TickMapper tickMapper;

  @NotNull
  @Override
  public TickDto addTickBySymbol(@NotNull final NewTickDto newTickDto, final @NotNull SymbolDto symbolDto) {
    final Symbol symbol = this.getSymbolMapper().toEntity(symbolDto);
    final boolean valid = this.getTickRepository().getFirstBySymbolOrderByTimestampDesc(symbol)
        .map(tick -> tick.getTimestamp().isBefore(newTickDto.timestamp())).orElse(true);
    if (valid) {
      final Tick tick = this.getTickMapper().toEntity(newTickDto, symbol);
      final Tick saved = this.getTickRepository().save(tick);
      return this.getTickMapper().toDto(saved);
    } else {
      throw new TickTimestampOlderException(newTickDto.timestamp(), symbolDto.currencyPair().name());
    }
  }

  @Override
  public @NotNull Collection<@NotNull TickDto> getTicksBySymbol(final @NotNull SymbolDto symbolDto) {
    final Symbol symbol = this.getSymbolMapper().toEntity(symbolDto);
    return this.getTickRepository().findBySymbol(symbol).stream().map(this.getTickMapper()::toDto).toList();
  }

  @Override
  public @NotNull Optional<@NotNull TickDto> getLestTickBySymbol(final @NotNull SymbolDto symbolDto) {
    final Symbol symbol = this.getSymbolMapper().toEntity(symbolDto);
    final List<Tick> collection = this.getTickRepository().findBySymbolOrderByTimestampDescLimitTwo(symbol);
    if(collection.size() == 2) {
      final Tick tick = collection.getLast();
      final TickDto tickDto = this.getTickMapper().toDto(tick);
      return Optional.of(tickDto);
    } else {
      return Optional.empty();
    }
  }

}
