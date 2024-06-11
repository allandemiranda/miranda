package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.CandlestickBody;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.entities.Scope;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.mappers.MovingAverageMapper;
import lu.forex.system.mappers.ScopeMapper;
import lu.forex.system.mappers.TechnicalIndicatorMapper;
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
  private final TechnicalIndicatorMapper technicalIndicatorMapper;
  private final MovingAverageMapper movingAverageMapper;

  @NotNull
  @Override
  public List<@NotNull CandlestickDto> findCandlesticksDescWithLimit(final @NotNull ScopeDto scopeDto, final int limit) {
    final Scope scope = this.getScopeMapper().toEntity(scopeDto);
    final List<Candlestick> byScopeOrderByTimestampDesc = this.getCandlestickRepository().findByScopeOrderByTimestampDesc(scope, limit);
    if(byScopeOrderByTimestampDesc.isEmpty()) {
      System.out.println();
    }
    return byScopeOrderByTimestampDesc.stream().map(this.getCandlestickMapper()::toDto).toList();
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

  @Override
  public @NotNull CandlestickDto addingTechnicalIndicators(final @NotNull Collection<TechnicalIndicatorDto> technicalIndicators, final @NotNull CandlestickDto candlestickDto) {
    final Candlestick candlestick = this.getCandlestickMapper().toEntity(candlestickDto);
    final Collection<TechnicalIndicator> collection = technicalIndicators.stream().map(tiDto -> this.getTechnicalIndicatorMapper().toEntity(tiDto)).toList();
    candlestick.getTechnicalIndicators().addAll(collection);
    final Candlestick saved = this.getCandlestickRepository().save(candlestick);
    return this.getCandlestickMapper().toDto(saved);
  }

  @Override
  public @NotNull CandlestickDto addingMovingAverages(final @NotNull Collection<MovingAverageDto> movingAverages, final @NotNull CandlestickDto candlestickDto) {
    final Candlestick candlestick = this.getCandlestickMapper().toEntity(candlestickDto);
    final Collection<MovingAverage> collection = movingAverages.stream().map(maDto -> this.getMovingAverageMapper().toEntity(maDto)).toList();
    candlestick.getMovingAverages().addAll(collection);
    final Candlestick saved = this.getCandlestickRepository().save(candlestick);
    return this.getCandlestickMapper().toDto(saved);
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
