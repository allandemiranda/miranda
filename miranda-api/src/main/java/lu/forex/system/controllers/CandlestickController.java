package lu.forex.system.controllers;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.NewMovingAverageDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.operations.CandlestickOperation;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.services.MovingAverageService;
import lu.forex.system.services.ScopeService;
import lu.forex.system.services.TechnicalIndicatorService;
import lu.forex.system.services.TickService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Getter(AccessLevel.PRIVATE)
public class CandlestickController implements CandlestickOperation {

  private final CandlestickService candlestickService;
  private final ScopeService scopeService;
  private final TickService tickService;
  private final TechnicalIndicatorService acceleratorOscillatorService;
  private final TechnicalIndicatorService averageDirectionalIndexService;
  private final TechnicalIndicatorService movingAverageConvergenceDivergenceService;
  private final MovingAverageService simpleMovingAverageService;
  private final MovingAverageService exponentialMovingAverageService;

  public CandlestickController(final CandlestickService candlestickService, final ScopeService scopeService, final TickService tickService,
      @Qualifier("acceleratorOscillator") final TechnicalIndicatorService acceleratorOscillatorService,
      @Qualifier("averageDirectionalIndex") final TechnicalIndicatorService averageDirectionalIndexService,
      @Qualifier("movingAverageConvergenceDivergence") final TechnicalIndicatorService movingAverageConvergenceDivergenceService,
      @Qualifier("simpleMovingAverage") final MovingAverageService simpleMovingAverageService,
      @Qualifier("exponentialMovingAverage") final MovingAverageService exponentialMovingAverageService) {
    this.candlestickService = candlestickService;
    this.scopeService = scopeService;
    this.tickService = tickService;
    this.acceleratorOscillatorService = acceleratorOscillatorService;
    this.averageDirectionalIndexService = averageDirectionalIndexService;
    this.movingAverageConvergenceDivergenceService = movingAverageConvergenceDivergenceService;
    this.simpleMovingAverageService = simpleMovingAverageService;
    this.exponentialMovingAverageService = exponentialMovingAverageService;
  }

  @Override
  public Collection<CandlestickDto> getCandlesticks(final String symbolName, final TimeFrame timeFrame) {
    final ScopeDto scopeDto = this.getScopeService().getScope(symbolName, timeFrame);
    // NEED BE UPDATE TO THE FRONT END WITH PAGINABLE
    return this.getCandlestickService().findCandlesticksDescWithLimit(scopeDto.id(), 5);
  }

  @Override
  public void initCandlestickFromTicks(final String symbolName) {
    final var tickDtoList = this.getTickService().getTicksBySymbolNameNotOrdered(symbolName);
    this.getScopeService().getScopesBySymbolName(symbolName).parallelStream()
        .forEach(scopeDto -> this.getCandlestickService().readTicksToGenerateCandlesticks(scopeDto, tickDtoList));
  }

  @Override
  public void initIndicatorsOnCandlesticks(final String symbolName) {
    final var indicatorServices = List.of(this.getAcceleratorOscillatorService(), this.getAverageDirectionalIndexService(), this.getMovingAverageConvergenceDivergenceService());
    final var candlesticksDto = Arrays.stream(TimeFrame.values()).parallel()
        .map(timeFrame -> this.getScopeService().getScope(symbolName, timeFrame).id())
        .flatMap(uuid -> this.getCandlestickService().getAllCandlestickByScopeIdAsync(uuid).stream())
        .toList();
    this.getCandlestickService().initIndicatorsOnCandlesticks(candlesticksDto, indicatorServices);
  }

  @Override
  public void initAveragesOnCandlesticks(final String symbolName) {
    final var indicatorServices = List.of(this.getAcceleratorOscillatorService(), this.getAverageDirectionalIndexService(), this.getMovingAverageConvergenceDivergenceService());
    final var newMovingAverages = indicatorServices.stream().flatMap(indicatorService -> indicatorService.generateMAs().stream()).collect(Collectors.toSet());
    final Collection<SimpleEntry<Collection<MovingAverageDto>, UUID>> candlesticksToSave = Arrays.stream(TimeFrame.values()).parallel()
        .map(timeFrame -> this.getScopeService().getScope(symbolName, timeFrame).id())
        .flatMap(uuid -> this.getCandlestickService().getAllCandlestickByScopeIdAsync(uuid).stream())
        .map(candlestickDto -> {
          final Collection<MovingAverageDto> theMovingAverages = newMovingAverages.stream()
              .map(newMovingAverageDto -> switch (newMovingAverageDto.type()) {
                case EMA -> this.getExponentialMovingAverageService().createMovingAverage(newMovingAverageDto);
                case SMA -> this.getSimpleMovingAverageService().createMovingAverage(newMovingAverageDto);
                default -> throw new IllegalStateException("Unexpected value: " + newMovingAverageDto.type());
              }).toList();
          return new SimpleEntry<>(theMovingAverages, candlestickDto.id());
        }).toList();

    this.getCandlestickService().initAveragesToCandlesticks(candlesticksToSave);
  }

  @Override
  public void initComputingIndicatorsOnCandlesticks(final String symbolName) {
    final var indicatorServices = List.of(this.getAcceleratorOscillatorService(), this.getAverageDirectionalIndexService(), this.getMovingAverageConvergenceDivergenceService());
    final var movingAverageServices = List.of(this.getSimpleMovingAverageService(), this.getExponentialMovingAverageService());
    final var technicalIndicatorSize = indicatorServices.stream().mapToInt(TechnicalIndicatorService::getNumberOfCandlesticksToCalculate).max() .orElse(0);

    final Map<UUID, List<List<UUID>>> scopeIdByCandlestickDtos = Arrays.stream(TimeFrame.values()).parallel()
        .map(timeFrame -> this.getScopeService().getScope(symbolName, timeFrame).id())
        .collect(Collectors.toMap(scopeId -> scopeId, scopeId -> {
          final List<CandlestickDto> candlestickDtos = this.getCandlestickService().getAllCandlestickByScopeIdDesc(scopeId);
          return IntStream.range(0, candlestickDtos.size()).boxed().sorted(Collections.reverseOrder()).map(i -> {
            final var lastIndexFix = Math.min(i + technicalIndicatorSize, candlestickDtos.size());
            return IntStream.range(i, lastIndexFix).mapToObj(j -> candlestickDtos.get(j).id()).toList();
          }).toList();
        }));

    this.getCandlestickService().computingIndicatorsByInit(indicatorServices, movingAverageServices, scopeIdByCandlestickDtos);
  }

}
