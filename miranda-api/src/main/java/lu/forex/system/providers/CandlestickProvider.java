package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
import lu.forex.system.services.MovingAverageService;
import lu.forex.system.services.TechnicalIndicatorService;
import lu.forex.system.utils.OrderUtils;
import lu.forex.system.utils.TimeFrameUtils;
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

  @NotNull
  @Override
  public List<@NotNull CandlestickDto> findCandlesticksDescWithLimit(final @NotNull UUID scopeId, final int limit) {
    return this.getCandlestickRepository().findByScope_IdOrderByTimestampDescWithLimit(scopeId, limit).stream().map(this.getCandlestickMapper()::toDto).toList();
  }

  @Override
  public @NotNull List<@NotNull CandlestickDto> findCandlesticksDescPerformed(final @NotNull UUID scopeId) {
    return this.getLastCandlesticksPerformedRepository().getLastCandlesticks(scopeId).stream().map(candlestick -> this.getCandlestickMapper().toDto(candlestick)).toList();
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
    final Candlestick candlestick = this.getCandlestickRepository().getFirstByScope_IdAndTimestamp(scope.getId(), candlestickTimestamp)
        .orElseGet(() -> this.createCandlestick(price, scope, candlestickTimestamp));
    candlestick.getBody().setClose(price);
    final Candlestick savedCandlestick = this.getCandlestickRepository().save(candlestick);
    this.getLastCandlesticksPerformedRepository().getLastCandlestickId(savedCandlestick.getScope().getId()).ifPresent(lastCandlestickId -> {
      if(!lastCandlestickId.equals(savedCandlestick.getId())) {
        this.getLastCandlesticksPerformedRepository().addNextCandlestick(savedCandlestick.getId(), savedCandlestick.getScope().getId());
      }
    });
    return this.getCandlestickMapper().toDto(savedCandlestick);
  }

  @Override
  public @NotNull CandlestickDto addingTechnicalIndicators(final @NotNull Collection<TechnicalIndicatorDto> technicalIndicators,
      final @NotNull UUID candlestickId) {
    final Candlestick candlestick = this.getCandlestickRepository().findById(candlestickId).orElseThrow(CandlestickNotFoundException::new);
    final Collection<TechnicalIndicator> collection = technicalIndicators.stream()
        .map(tiDto -> this.getTechnicalIndicatorMapper().toEntity(tiDto)).toList();
    candlestick.getTechnicalIndicators().addAll(collection);
    final Candlestick saved = this.getCandlestickRepository().save(candlestick);
    return this.getCandlestickMapper().toDto(saved);
  }

  @Override
  public @NotNull CandlestickDto addingMovingAverages(final @NotNull Collection<MovingAverageDto> movingAverages, final @NotNull UUID candlestickId) {
    final Candlestick candlestick = this.getCandlestickRepository().findById(candlestickId).orElseThrow(CandlestickNotFoundException::new);
    final Collection<MovingAverage> collection = movingAverages.stream().map(maDto -> this.getMovingAverageMapper().toEntity(maDto)).toList();
    candlestick.getMovingAverages().addAll(collection);
    final Candlestick saved = this.getCandlestickRepository().save(candlestick);
    return this.getCandlestickMapper().toDto(saved);
  }

  @Override
  public @NotNull CandlestickDto processSignalIndicatorByCandlestickId(final @NotNull UUID candlestickId) {
    final Candlestick candlestick = this.getCandlestickRepository().findById(candlestickId).orElseThrow(CandlestickNotFoundException::new);
    candlestick.setSignalIndicator(OrderUtils.getSignalIndicator(candlestick.getTechnicalIndicators()));
    final Candlestick saved = this.getCandlestickRepository().save(candlestick);
    return this.getCandlestickMapper().toDto(saved);
  }

  @Override
  public @NotNull Collection<CandlestickDto> readTicksToGenerateCandlesticks(final @NotNull ScopeDto scopeDto, final @NotNull Collection<TickDto> tickDtoList) {
    log.info("Starting readTicksToGenerateCandlesticks({}, {})",scopeDto.symbol().currencyPair().name(), scopeDto.timeFrame());
    final var scope = this.getScopeMapper().toEntity(scopeDto);
    final var candlesticks = tickDtoList.stream()
        .collect(Collectors.groupingBy(
            tickDto -> TimeFrameUtils.getCandlestickTimestamp(tickDto.timestamp(), scopeDto.timeFrame()),
            Collectors.collectingAndThen(
                Collectors.toList(),
                list ->  list.stream().sorted(Comparator.comparing(TickDto::timestamp)).map(TickDto::bid).toList()
                )))
        .entrySet().stream().map(entry -> {
          final var candlestickBody = new CandlestickBody();
          candlestickBody.setOpen(entry.getValue().getFirst());
          candlestickBody.setClose(entry.getValue().getLast());
          candlestickBody.setHigh(entry.getValue().stream().max(Comparator.comparingDouble(value -> value)).orElseThrow());
          candlestickBody.setLow(entry.getValue().stream().min(Comparator.comparingDouble(value -> value)).orElseThrow());

          final var candlestick = new Candlestick();
          candlestick.setScope(scope);
          candlestick.setTimestamp(entry.getKey());
          candlestick.setBody(candlestickBody);

          return candlestick;
        }).toList();
    log.info("Ending readTicksToGenerateCandlesticks({}, {})",scopeDto.symbol().currencyPair().name(), scopeDto.timeFrame());
    return this.getCandlestickRepository().saveAll(candlesticks).stream().map(candlestick -> this.getCandlestickMapper().toDto(candlestick)).toList();
  }

  @Override
  public @NotNull Stream<CandlestickDto> initIndicatorsOnCandlesticks(final @NotNull Stream<CandlestickDto> candlesticksDto, final @NotNull Collection<TechnicalIndicatorService> indicatorServices) {
    log.info("Starting initIndicatorsOnCandlesticks()");
    final Collection<Candlestick> candlesticksToSave = candlesticksDto.parallel()
    .map(candlestickDto -> {
      final Candlestick candlestick = this.getCandlestickMapper().toEntity(candlestickDto);
      final List<TechnicalIndicator> indicators = indicatorServices.stream().map(TechnicalIndicatorService::initTechnicalIndicator).map(tiDto -> this.getTechnicalIndicatorMapper().toEntity(tiDto)).toList();
      candlestick.getTechnicalIndicators().addAll(indicators);
      return candlestick;
    }).toList();
    log.info("Ending initIndicatorsOnCandlesticks()");
    return this.getCandlestickRepository().saveAll(candlesticksToSave).stream().map(candlestick -> this.getCandlestickMapper().toDto(candlestick));
  }

  @Override
  public @NotNull Stream<CandlestickDto> initAveragesToCandlesticks(final @NotNull Stream<SimpleEntry<Collection<MovingAverageDto>, CandlestickDto>> candlesticksToSave) {
    log.info("Starting initAveragesOnCandlesticks()");
    final Collection<Candlestick> toSave = candlesticksToSave.map(entry -> {
      final var candlestick = this.getCandlestickMapper().toEntity(entry.getValue());
      final Collection<MovingAverage> collection = entry.getKey().stream().map(maDto -> this.getMovingAverageMapper().toEntity(maDto)).toList();
      candlestick.getMovingAverages().addAll(collection);
      return candlestick;
    }).toList();
    log.info("Ending initAveragesOnCandlesticks()");
    return this.getCandlestickRepository().saveAll(toSave).stream().map(entry -> this.getCandlestickMapper().toDto(entry));
  }

  @Override
  public @NotNull Stream<CandlestickDto> computingIndicatorsByInit(final @NotNull Collection<TechnicalIndicatorService> indicatorServices, final @NotNull Collection<MovingAverageService> movingAverageServices,
      final @NotNull Map<UUID, List<List<UUID>>> groupLastCandlesticksDto) {
    log.info("Starting computingIndicatorsByInit()");
    final Collection<Candlestick> collection = groupLastCandlesticksDto.entrySet().stream().flatMap(entry -> entry.getValue().stream().map(candlestickIds -> {
      final List<CandlestickDto> lastCandlesticks = candlestickIds.stream().map(uuid -> this.getCandlestickRepository().findById(uuid).orElseThrow()).map(candlestick -> this.getCandlestickMapper().toDto(candlestick)).toList();
      movingAverageServices.forEach(movingAverageService -> movingAverageService.calculateMovingAverage(lastCandlesticks));
      indicatorServices.forEach(indicatorService -> indicatorService.calculateTechnicalIndicator(lastCandlesticks));
      return lastCandlesticks.getFirst();
    })).map(candlestickDto -> {
      final Candlestick candlestick = this.getCandlestickRepository().findById(candlestickDto.id()).orElseThrow();
      final SignalIndicator signalIndicator = OrderUtils.getSignalIndicator(candlestick.getTechnicalIndicators());
      candlestick.setSignalIndicator(signalIndicator);
      return candlestick;
    }).toList();
    log.info("Ending computingIndicatorsByInit()");

    final List<Candlestick> candlesticks = this.getCandlestickRepository().saveAll(collection);
    log.warn("nº Candlestick not Neutral: {}", candlesticks.stream().filter(candlestick -> !SignalIndicator.NEUTRAL.equals(candlestick.getSignalIndicator())).count());
    return candlesticks.stream().map(candlestick -> this.getCandlestickMapper().toDto(candlestick));
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
