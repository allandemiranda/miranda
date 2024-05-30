package lu.forex.system.providers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.exceptions.TickConflictException;
import lu.forex.system.exceptions.TickExistException;
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
  public Stream<@NotNull TickResponseDto> getTicksBySymbolName(@NotNull final String symbolName) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    return this.getTickRepository().streamBySymbolOrderByTimestampAsc(symbol).map(this.getTickMapper()::toDto);
  }

  @NotNull
  @Override
  public TickResponseDto addTick(@NotNull final TickCreateDto tickCreateDto) {
    final Symbol symbol = this.getSymbolByName(tickCreateDto.symbolName());
    this.validateTickNotExist(tickCreateDto, symbol);
    final Optional<Tick> optionalTick = this.findLatestTickBySymbol(symbol);
    if (optionalTick.isPresent() && optionalTick.get().getTimestamp().isAfter(tickCreateDto.timestamp())) {
      throw new TickConflictException(symbol.getName(), tickCreateDto.timestamp(), optionalTick.get().getTimestamp());
    } else if (optionalTick.isPresent() && (optionalTick.get().getBid() == tickCreateDto.bid() && optionalTick.get().getAsk() == tickCreateDto.ask())) {
      throw new TickExistException(symbol);
    } else {
      final Tick tick = this.createTickFromDto(tickCreateDto, symbol);
      final Tick saved = this.getTickRepository().save(tick);
      return this.getTickMapper().toDto(saved);
    }
  }

  @Nonnull
  private Symbol getSymbolByName(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName) {
    return this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
  }

  private void validateTickNotExist(final @Nonnull TickCreateDto tickCreateDto, final @Nonnull Symbol symbol) {
    if (this.getTickRepository().existsBySymbolAndTimestamp(symbol, tickCreateDto.timestamp())) {
      throw new TickExistException(tickCreateDto, symbol);
    }
  }

  @Nonnull
  private Optional<@NotNull Tick> findLatestTickBySymbol(final @Nonnull Symbol symbol) {
    return this.getTickRepository().findFirstBySymbolOrderByTimestampDesc(symbol);
  }

  @Nonnull
  private Tick createTickFromDto(final @Nonnull TickCreateDto tickCreateDto, final @Nonnull Symbol symbol) {
    return this.getTickMapper().toEntity(tickCreateDto, symbol);
  }
}
