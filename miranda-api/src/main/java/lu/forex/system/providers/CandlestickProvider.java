package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.CandlestickBody;
import lu.forex.system.entities.Scope;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.mappers.ScopeMapper;
import lu.forex.system.repositories.CandlestickRepository;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.utils.TimeFrameUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickProvider implements CandlestickService {

  private final CandlestickRepository candlestickRepository;
  private final CandlestickMapper candlestickMapper;
  private final ScopeMapper scopeMapper;

  @NotNull
  @Override
  public Collection<@NotNull CandlestickDto> getCandlesticks(final @NotNull ScopeDto scopeDto) {
    final Scope scope = this.getScopeMapper().toEntity(scopeDto);
    return this.getCandlestickRepository().findByScope(scope).stream().map(this.getCandlestickMapper()::toDto).toList();
  }

  @NotNull
  @Override
  public CandlestickDto processingCandlestick(final @NotNull TickDto tickDto, final @NotNull ScopeDto scopeDto) {
    final double price = tickDto.bid();
    final Scope scope = this.getScopeMapper().toEntity(scopeDto);
    final LocalDateTime candlestickTimestamp = TimeFrameUtils.getCandlestickTimestamp(tickDto.timestamp(), scope.getTimeFrame());
    final Candlestick candlestick = this.getCandlestickRepository().getFirstByScopeAndTimestamp(scope, candlestickTimestamp).orElseGet(() -> this.createCandlestick(price, scope, candlestickTimestamp));
    candlestick.getBody().setClose(price);
    final Candlestick savedCandlestick = this.getCandlestickRepository().save(candlestick);
    return this.getCandlestickMapper().toDto(savedCandlestick);
  }

  private @NotNull Candlestick createCandlestick(final double price, final @NotNull Scope scope, final @NotNull LocalDateTime timestamp) {
    final CandlestickBody body = new CandlestickBody();
    body.setHigh(price);
    body.setLow(price);
    body.setOpen(price);

    final Candlestick candlestick = new Candlestick();
    candlestick.setScope(scope);
    candlestick.setTimestamp(timestamp);
    candlestick.setBody(body);

    return candlestick;
  }
}
