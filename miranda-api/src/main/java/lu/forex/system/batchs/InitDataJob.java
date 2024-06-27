package lu.forex.system.batchs;

import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.services.MovingAverageService;
import lu.forex.system.services.OrderService;
import lu.forex.system.services.ScopeService;
import lu.forex.system.services.SymbolService;
import lu.forex.system.services.TechnicalIndicatorService;
import lu.forex.system.services.TickService;
import lu.forex.system.services.TradeService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Getter(AccessLevel.PRIVATE)
@Log4j2
public class InitDataJob {

  private final ProjectInfoProperties projectInfoProperties;
  @Value("${init.filePath}")
  private String filePathInit;

  private final SymbolService symbolService;
  private final TickService tickService;
  private final ScopeService scopeService;
  private final CandlestickService candlestickService;
  private final OrderService orderService;
  private final TradeService tradeService;
  private final TechnicalIndicatorService acceleratorOscillatorService;
  private final TechnicalIndicatorService averageDirectionalIndexService;
  private final TechnicalIndicatorService movingAverageConvergenceDivergenceService;
  private final MovingAverageService simpleMovingAverageService;
  private final MovingAverageService exponentialMovingAverageService;

  public InitDataJob(final SymbolService symbolService, final TickService tickService, final ScopeService scopeService,
      final CandlestickService candlestickService, final OrderService orderService, final TradeService tradeService, @Qualifier("acceleratorOscillator") final TechnicalIndicatorService acceleratorOscillatorService,
      @Qualifier("averageDirectionalIndex") final TechnicalIndicatorService averageDirectionalIndexService,
      @Qualifier("movingAverageConvergenceDivergence") final TechnicalIndicatorService movingAverageConvergenceDivergenceService,
      @Qualifier("simpleMovingAverage") final MovingAverageService simpleMovingAverageService,
      @Qualifier("exponentialMovingAverage") final MovingAverageService exponentialMovingAverageService,
      final ProjectInfoProperties projectInfoProperties) {
    this.symbolService = symbolService;
    this.tickService = tickService;
    this.scopeService = scopeService;
    this.candlestickService = candlestickService;
    this.orderService = orderService;
    this.tradeService = tradeService;
    this.acceleratorOscillatorService = acceleratorOscillatorService;
    this.averageDirectionalIndexService = averageDirectionalIndexService;
    this.movingAverageConvergenceDivergenceService = movingAverageConvergenceDivergenceService;
    this.simpleMovingAverageService = simpleMovingAverageService;
    this.exponentialMovingAverageService = exponentialMovingAverageService;
    this.projectInfoProperties = projectInfoProperties;
  }

  @Async
  public void start() {
    final var root = new File(this.getFilePathInit());
    if (root.exists() && root.isDirectory()) {
      this.stackProcess(root);
      log.warn("Stack process complete!");
    } else {
      log.error("Folder {} not exists", root.getAbsolutePath());
    }
  }
  private void stackProcess(final @NotNull File folder) {
    this.getSymbolService().getSymbols().parallelStream().map(symbolDto -> {
    final var fileName = symbolDto.currencyPair().name().concat(".csv");
    final var inputFile = new File(folder, fileName);
    if(inputFile.exists()) {
      log.info("Added Batch Job: {} -> {}", symbolDto, inputFile.getAbsolutePath());
      return new SimpleEntry<>(symbolDto, inputFile);
    } else {
      log.error("File {} not exists", inputFile.getAbsolutePath());
      return null;
    }
    }).filter(Objects::nonNull).forEach(entry -> {
      final var symbolDto = entry.getKey();
      final var inputFile = entry.getValue();
      final var ticksDtoSorted = this.getTickService().readPreDataBase(symbolDto, inputFile);
      if(!ticksDtoSorted.isEmpty()) {
        Stream<CandlestickDto> candlesticksDto = this.getScopeService().getScopesBySymbolName(symbolDto.currencyPair().name()).parallelStream()
            .flatMap(scopeDto -> this.getCandlestickService().readTicksToGenerateCandlesticks(scopeDto, ticksDtoSorted).stream());

        final var indicatorServices = List.of(this.getAcceleratorOscillatorService(), this.getAverageDirectionalIndexService(),
            this.getMovingAverageConvergenceDivergenceService());

        final var newMovingAverageServices = indicatorServices.stream().flatMap(indicatorService -> indicatorService.generateMAs().stream())
            .collect(Collectors.toSet());
        final Stream<SimpleEntry<Collection<MovingAverageDto>, CandlestickDto>> modelWithMovingAverages =
            this.getCandlestickService().initIndicatorsOnCandlesticks(candlesticksDto, indicatorServices).parallel()
            .map(candlestickDto -> {
              final Collection<MovingAverageDto> theMovingAverages = newMovingAverageServices.stream()
                  .map(newMovingAverageDto -> switch (newMovingAverageDto.type()) {
                    case EMA -> this.getExponentialMovingAverageService().createMovingAverage(newMovingAverageDto);
                    case SMA -> this.getSimpleMovingAverageService().createMovingAverage(newMovingAverageDto);
                    default -> throw new IllegalStateException("Unexpected value: " + newMovingAverageDto.type());
                  }).toList();
              return new SimpleEntry<>(theMovingAverages, candlestickDto);
            });

        final var technicalIndicatorSize = indicatorServices.stream().mapToInt(TechnicalIndicatorService::getNumberOfCandlesticksToCalculate).max()
            .orElse(0);
        final var movingAverageServices = List.of(this.getSimpleMovingAverageService(), this.getExponentialMovingAverageService());
        final Map<UUID, List<List<UUID>>> groupLastCandlesticksDto = this.getCandlestickService().initAveragesToCandlesticks(modelWithMovingAverages)
            .collect(Collectors.groupingBy(CandlestickDto::scope, Collectors.collectingAndThen(Collectors.toList(),
                candlestickDtos -> candlestickDtos.stream().sorted(Comparator.comparing(CandlestickDto::timestamp).reversed()).toList())))
            .entrySet().stream()
            .collect(Collectors.toMap(m -> m.getKey().id(), m ->
                IntStream.range(0, m.getValue().size()).boxed().sorted(Collections.reverseOrder()).map(i -> {
                  final var lastIndexFix = Math.min(i + technicalIndicatorSize, m.getValue().size());
                  return IntStream.range(i, lastIndexFix).mapToObj(j -> m.getValue().get(j).id()).toList();
                }).toList()
            ));

        final Map<LocalDateTime, Set<CandlestickDto>> entryCollection =
            this.getCandlestickService().computingIndicatorsByInit(indicatorServices, movingAverageServices, groupLastCandlesticksDto).parallel()
            .filter(candlestickDto -> !SignalIndicator.NEUTRAL.equals(candlestickDto.signalIndicator()))
            .collect(Collectors.groupingBy(CandlestickDto::timestamp, Collectors.toSet()));

        final List<TradeDto> tradesActivated = this.getTradeService().managementEfficientTradesScenarioToBeActivated(
            this.getOrderService().processingInitOrders(
                ticksDtoSorted,
                this.getTradeService().initOrdersByTrade(entryCollection, ticksDtoSorted)
            ).map(OrderDto::tradeId).distinct()
        );
        log.info("Activated trades: {}", tradesActivated.size());
      }
    });
  }
}
