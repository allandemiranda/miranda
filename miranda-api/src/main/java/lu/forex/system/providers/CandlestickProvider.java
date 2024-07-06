package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
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
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.exceptions.CandlestickNotFoundException;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.mappers.MovingAverageMapper;
import lu.forex.system.mappers.ScopeMapper;
import lu.forex.system.mappers.TechnicalIndicatorMapper;
import lu.forex.system.repositories.CandlestickRepository;
import lu.forex.system.repositories.LastCandlesticksPerformedRepository;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.utils.OrderUtils;
import lu.forex.system.utils.TimeFrameUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
@Log4j2
public class CandlestickProvider implements CandlestickService {

  private final CandlestickRepository candlestickRepository;
  private final LastCandlesticksPerformedRepository lastCandlesticksPerformedRepository;
  private final CandlestickMapper candlestickMapper;
  private final ScopeMapper scopeMapper;
  private final TechnicalIndicatorMapper technicalIndicatorMapper;
  private final MovingAverageMapper movingAverageMapper;

  @Override
  public @NotNull CandlestickDto getCandlestick(@NotNull final UUID id) {
    return this.getCandlestickRepository().findById(id).map(candlestick -> this.getCandlestickMapper().toDto(candlestick)).orElseThrow(CandlestickNotFoundException::new);
  }

  @NotNull
  @Override
  public List<@NotNull CandlestickDto> findCandlesticksDescWithLimit(final @NotNull UUID scopeId, final int limit) {
    return this.getCandlestickRepository().findByScope_IdOrderByTimestampDescWithLimit(scopeId, limit).stream().map(this.getCandlestickMapper()::toDto).toList();
  }

  @Override
  public @NotNull CandlestickDto @NotNull [] findCandlesticksDescLimited(final @NotNull UUID scopeId) {
    final UUID[] lastCandlesticksId = this.getLastCandlesticksPerformedRepository().getLastCandlesticksNotIncludingFirst(scopeId);
    final CandlestickDto[] lastCandlesticksDto = new CandlestickDto[lastCandlesticksId.length];
    IntStream.range(0, lastCandlesticksId.length).parallel().forEach(i -> {
      final Candlestick candlestick = this.getCandlestickRepository().findById(lastCandlesticksId[i]).orElseThrow(CandlestickNotFoundException::new);
      lastCandlesticksDto[i] = this.getCandlestickMapper().toDto(candlestick);
    });
    return lastCandlesticksDto;
  }

  @Override
  public @NotNull List<@NotNull CandlestickDto> findCandlesticksAsc(final @NotNull UUID scopeId) {
    return this.getCandlestickRepository().findByScope_IdOrderByTimestampAsc(scopeId).stream().map(candlestick -> this.getCandlestickMapper().toDto(candlestick)).toList();
  }

  @NotNull
  @Override
  public CandlestickDto processingCandlestick(final @NotNull TickDto tickDto, final @NotNull ScopeDto scopeDto) {
    final double price = tickDto.bid();
    final Scope scope = this.getScopeMapper().toEntity(scopeDto);
    final LocalDateTime candlestickTimestamp = TimeFrameUtils.getCandlestickTimestamp(tickDto.timestamp(), scope.getTimeFrame());
    final Candlestick candlestick = this.getCandlestickRepository().getFirstByScope_IdAndTimestamp(scope.getId(), candlestickTimestamp).orElseGet(() -> this.createCandlestick(price, scope, candlestickTimestamp));
    candlestick.getBody().setClose(price);
    final Candlestick savedCandlestick = this.getCandlestickRepository().save(candlestick);
    this.getLastCandlesticksPerformedRepository().getRealLastCandlestickId(savedCandlestick.getScope().getId()).ifPresent(lastCandlestickId -> {
      if(!lastCandlestickId.equals(savedCandlestick.getId())) {
        this.getLastCandlesticksPerformedRepository().addNextCandlestick(savedCandlestick);
      }
    });
    return this.getCandlestickMapper().toDto(savedCandlestick);
  }

  @Override
  public CandlestickDto computingSignal(final @NotNull UUID candlestickId) {
    final Candlestick candlestick = this.getCandlestickRepository().findById(candlestickId).orElseThrow(CandlestickNotFoundException::new);
    final SignalIndicator signalIndicator = OrderUtils.getSignalIndicator(candlestick.getTechnicalIndicators());
    candlestick.setSignalIndicator(signalIndicator);
    final Candlestick saved = this.getCandlestickRepository().save(candlestick);
    return this.getCandlestickMapper().toDto(saved);
  }

  @Override
  public @NotNull CandlestickDto addingTechnicalIndicatorsAndMovingAverage(final @NotNull Stream<TechnicalIndicatorDto> technicalIndicators, final @NotNull Stream<MovingAverageDto> movingAverages, final @NotNull UUID candlestickId) {
    final Candlestick candlestick = this.getCandlestickRepository().findById(candlestickId).orElseThrow(CandlestickNotFoundException::new);
    final Set<TechnicalIndicator> indicators = technicalIndicators.map(tiDto -> this.getTechnicalIndicatorMapper().toEntity(tiDto)).collect(Collectors.toSet());
    final Set<MovingAverage> averages = movingAverages.map(maDto -> this.getMovingAverageMapper().toEntity(maDto)).collect(Collectors.toSet());
    candlestick.setTechnicalIndicators(indicators);
    candlestick.setMovingAverages(averages);
    final Candlestick saved = this.getCandlestickRepository().save(candlestick);
    return this.getCandlestickMapper().toDto(saved);
  }

  @Override
  public @NotNull UUID @NotNull [] batchReadTicksToGenerateCandlesticks(final @NotNull ScopeDto scopeDto, final @NotNull TickDto @NotNull [] ticksDto) {
    log.info("Starting generate candlesticks for timeframe {}", scopeDto.timeFrame());
    final var scope = this.getScopeMapper().toEntity(scopeDto);
    final var candlesticks = IntStream.range(0, ticksDto.length).boxed()
        .collect(Collectors.groupingBy(i -> TimeFrameUtils.getCandlestickTimestamp(ticksDto[i].timestamp(), scopeDto.timeFrame())))
        .entrySet().parallelStream().map(entry -> {
          final double[] prices = entry.getValue().stream().sorted().mapToDouble(i -> ticksDto[i].bid()).toArray();

          final var candlestickBody = new CandlestickBody();
          candlestickBody.setOpen(prices[0]);
          candlestickBody.setClose(prices[prices.length - 1]);
          candlestickBody.setHigh(Arrays.stream(prices).max().orElseThrow());
          candlestickBody.setLow(Arrays.stream(prices).min().orElseThrow());

          final var candlestick = new Candlestick();
          candlestick.setScope(scope);
          candlestick.setTimestamp(entry.getKey());
          candlestick.setBody(candlestickBody);

          return candlestick;
        }).toList();
    log.info("Saving, sorting, and mapping the generated candlesticks for timeframe {}", scopeDto.timeFrame());
    return this.getCandlestickRepository().saveAll(candlesticks).stream().sorted(Comparator.comparing(Candlestick::getTimestamp)).map(Candlestick::getId).toArray(UUID[]::new);
  }

  @Override
  public void batchInitIndicatorsAndAveragesOnCandlesticks(final @NotNull Stream<Triple<UUID, Stream<TechnicalIndicatorDto>, Stream<MovingAverageDto>>> candlesticksToProcess) {
    log.info("Setting the indicators and averages on candlesticks");
    final Collection<Candlestick> candlesticks = candlesticksToProcess.parallel().map(triple -> {
      final Candlestick candlestick = this.getCandlestickRepository().findById(triple.getLeft()).orElseThrow(CandlestickNotFoundException::new);
      final Set<TechnicalIndicator> indicators = triple.getMiddle().map(tiDto -> this.getTechnicalIndicatorMapper().toEntity(tiDto)).collect(Collectors.toSet());
      final Set<MovingAverage> averages = triple.getRight().map(maDto -> this.getMovingAverageMapper().toEntity(maDto)).collect(Collectors.toSet());
      candlestick.setTechnicalIndicators(indicators);
      candlestick.setMovingAverages(averages);
      return candlestick;
    }).toList();
    log.info("Saving the modifications on candlesticks");
    this.getCandlestickRepository().saveAll(candlesticks);
    log.info("Modifications saved");
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
