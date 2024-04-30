package lu.forex.system.providers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.exceptions.TickConflictException;
import lu.forex.system.exceptions.TickExistException;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.repositories.CandlestickRepository;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.repositories.TickRepository;
import lu.forex.system.services.TickService;
import lu.forex.system.utils.TimeFrameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter(AccessLevel.PRIVATE)
public class TickProvider implements TickService {

  private final TickRepository tickRepository;
  private final SymbolRepository symbolRepository;
  private final CandlestickRepository candlestickRepository;
  private final TickMapper tickMapper;

  @Autowired
  public TickProvider(final TickRepository tickRepository, final SymbolRepository symbolRepository, final CandlestickRepository candlestickRepository,
      final TickMapper tickMapper) {
    this.tickRepository = tickRepository;
    this.symbolRepository = symbolRepository;
    this.candlestickRepository = candlestickRepository;
    this.tickMapper = tickMapper;
  }

  @Override
  public @Nonnull Collection<@NotNull TickResponseDto> getTicks(final @Nonnull String symbolName) {
    return this.getTickRepository().findBySymbol_NameOrderByTimestampAsc(symbolName).stream().map(this.getTickMapper()::toDto).toList();
  }

  @Override
  public @Nonnull TickResponseDto addTick(final @Nonnull TickCreateDto tickCreateDto, final @Nonnull String symbolName) {
    final Symbol symbol = this.getSymbolByName(symbolName);
    validateTickNotExist(tickCreateDto, symbol);
    final Optional<Tick> optionalTick = this.findLatestTickBySymbolName(symbolName);
    final Tick tick = createTickFromDto(tickCreateDto, symbol);
    if (optionalTick.isPresent() && optionalTick.get().getTimestamp().isAfter(tickCreateDto.timestamp())) {
      throw new TickConflictException(symbolName, tickCreateDto.timestamp(), optionalTick.get().getTimestamp());
    } else {
      final Tick saved = saveTick(tick);
      this.createOrUpdateCandlestick(tick);
      return getTickMapper().toDto(saved);
    }
  }

  private void createOrUpdateCandlestick(final Tick tick) {
    Arrays.stream(TimeFrame.values()).forEachOrdered(timeFrame -> {
      final LocalDateTime localTimesFrame = TimeFrameUtils.getCandlestickDateTime(tick.getTimestamp(), timeFrame);
      final Optional<Candlestick> lastCandlestickOptional = this.getCandlestickRepository()
          .getFirstBySymbol_NameAndTimeFrameAndTimestampOrderByTimestampDesc(tick.getSymbol().getName(), timeFrame, localTimesFrame);
      if (lastCandlestickOptional.isPresent()) {
        final Candlestick lastCandlestick = lastCandlestickOptional.get();
        if (lastCandlestick.getHigh() < tick.getBid()) {
          lastCandlestick.setHigh(tick.getBid());
        } else if (lastCandlestick.getLow() > tick.getBid()) {
          lastCandlestick.setLow(tick.getBid());
        }
        lastCandlestick.setClose(tick.getBid());
        this.getCandlestickRepository().save(lastCandlestick);
      } else {
        final Candlestick candlestick = new Candlestick();
        candlestick.setTimestamp(localTimesFrame);
        candlestick.setTimeFrame(timeFrame);
        candlestick.setSymbol(tick.getSymbol());
        candlestick.setHigh(tick.getBid());
        candlestick.setLow(tick.getBid());
        candlestick.setOpen(tick.getBid());
        candlestick.setClose(tick.getBid());
        this.getCandlestickRepository().save(candlestick);
      }
    });
  }

  private Symbol getSymbolByName(final String symbolName) {
    return getSymbolRepository().findFirstByNameOrderByNameAsc(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
  }

  private void validateTickNotExist(final @Nonnull TickCreateDto tickCreateDto, final @Nonnull Symbol symbol) {
    if (getTickRepository().existsBySymbol_NameAndTimestamp(symbol.getName(), tickCreateDto.timestamp())) {
      throw new TickExistException(tickCreateDto, symbol);
    }
  }

  private Optional<Tick> findLatestTickBySymbolName(final String symbolName) {
    return getTickRepository().findFirstBySymbol_NameOrderByTimestampDesc(symbolName);
  }

  private @Nonnull Tick createTickFromDto(final @Nonnull TickCreateDto tickCreateDto, final @Nonnull Symbol symbol) {
    Tick tick = this.getTickMapper().toEntity(tickCreateDto);
    tick.setSymbol(symbol);
    return tick;
  }

  private @Nonnull Tick saveTick(final @Nonnull Tick tick) {
    return getTickRepository().save(tick);
  }
}
