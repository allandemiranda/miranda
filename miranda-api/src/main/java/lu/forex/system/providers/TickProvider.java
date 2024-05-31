package lu.forex.system.providers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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
    final String symbolName = tickCreateDto.symbolName();
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    if (this.getTickRepository().existsBySymbolAndTimestamp(symbol, tickCreateDto.timestamp())) {
      throw new TickExistException(tickCreateDto, symbol);
    } else {
      this.getTickRepository().getFirstBySymbolOrderByTimestampDesc(symbol).ifPresent(tick -> {
        final LocalDateTime timestamp = tick.getTimestamp();
        if (timestamp.isAfter(tickCreateDto.timestamp())) {
          throw new TickConflictException(symbol.getName(), tickCreateDto.timestamp(), timestamp);
        } else if (tick.getBid() == tickCreateDto.bid() && tick.getAsk() == tickCreateDto.ask()) {
          throw new TickExistException(symbol);
        }
      });

      final Tick tick = this.getTickMapper().toEntity(tickCreateDto, symbol);
      final Tick saved = this.getTickRepository().save(tick);
      return this.getTickMapper().toDto(saved);
    }
  }
}
