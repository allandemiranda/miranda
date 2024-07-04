package lu.forex.system.batchs;

import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.enums.OrderType;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.services.MovingAverageService;
import lu.forex.system.services.OrderService;
import lu.forex.system.services.TechnicalIndicatorService;
import lu.forex.system.services.TickService;
import lu.forex.system.services.TradeService;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Getter(AccessLevel.PRIVATE)
@Log4j2
public class InitDataJob {

  private final TickService tickService;
  private final CandlestickService candlestickService;
  private final OrderService orderService;
  private final TradeService tradeService;
  private final TechnicalIndicatorService acceleratorOscillatorService;
  private final TechnicalIndicatorService averageDirectionalIndexService;
  private final TechnicalIndicatorService movingAverageConvergenceDivergenceService;
  private final MovingAverageService simpleMovingAverageService;
  private final MovingAverageService exponentialMovingAverageService;

  @Value("${init.filePath}")
  private String filePathInit;

  public InitDataJob(final TickService tickService,
      final CandlestickService candlestickService, final OrderService orderService, final TradeService tradeService,
      @Qualifier("acceleratorOscillator") final TechnicalIndicatorService acceleratorOscillatorService,
      @Qualifier("averageDirectionalIndex") final TechnicalIndicatorService averageDirectionalIndexService,
      @Qualifier("movingAverageConvergenceDivergence") final TechnicalIndicatorService movingAverageConvergenceDivergenceService,
      @Qualifier("simpleMovingAverage") final MovingAverageService simpleMovingAverageService,
      @Qualifier("exponentialMovingAverage") final MovingAverageService exponentialMovingAverageService) {
    this.tickService = tickService;
    this.candlestickService = candlestickService;
    this.orderService = orderService;
    this.tradeService = tradeService;
    this.acceleratorOscillatorService = acceleratorOscillatorService;
    this.averageDirectionalIndexService = averageDirectionalIndexService;
    this.movingAverageConvergenceDivergenceService = movingAverageConvergenceDivergenceService;
    this.simpleMovingAverageService = simpleMovingAverageService;
    this.exponentialMovingAverageService = exponentialMovingAverageService;
  }

  @Async
  public void start(final @NotNull SymbolDto symbolDto, final @NotNull Set<ScopeDto> scopesDto, final Collection<TradeDto> tradeDtos) {
    log.warn("Starting batch job for symbol {}", symbolDto.currencyPair().name());
    final File root = new File(this.getFilePathInit());
    if (root.exists() && root.isDirectory()) {
      final TickDto[] ticksDto = this.readingTicks(root, symbolDto);
      log.info("Found {} ticks", ticksDto.length);
      if(ticksDto.length > 0) {
        final UUID[][] candlesticksCollection = scopesDto.parallelStream().map(scopeDto -> this.getCandlestickService().batchReadTicksToGenerateCandlesticks(scopeDto, ticksDto)).toArray(UUID[][]::new);
        log.info("Found {} candlesticks", candlesticksCollection.length);
        final TechnicalIndicatorService[] indicatorServices = new TechnicalIndicatorService[]{this.getAcceleratorOscillatorService(), this.getAverageDirectionalIndexService(), this.getMovingAverageConvergenceDivergenceService()};
        this.initIndicatorsAndMovingAverages(candlesticksCollection, indicatorServices);
        final int periodMax = Arrays.stream(indicatorServices).mapToInt(TechnicalIndicatorService::getNumberOfCandlesticksToCalculate).max().orElse(0);
        log.info("Processing indicators");
        final CandlestickDto[] notNeutralCandlesticks = this.processIndicators(candlesticksCollection, periodMax, indicatorServices);
        log.info("Found {} not neutral candlesticks", notNeutralCandlesticks.length);
        final UUID[] tradesOperated = this.openOrders(notNeutralCandlesticks, tradeDtos, ticksDto);
        log.info("Found {} trades operated", tradeDtos.size());
        this.getOrderService().batchProcessingInitOrders(ticksDto);
        this.getTradeService().batchInitManagementTrades(tradesOperated);
        log.info("Trades active: {}", this.getTradeService().getTradesActive().size());
        log.info("Orders processed: {}", this.getOrderService().getOrders(symbolDto.id()).size());
      }
      log.warn("Stack process complete!");
    } else {
      log.error("Folder {} not exists", root.getAbsolutePath());
    }
  }

  @NotNull
  private TickDto @NotNull [] readingTicks(final @NotNull File folder, final @NotNull SymbolDto symbolDto) {
    final String fileName = symbolDto.currencyPair().name().concat(".csv");
    final File inputFile = new File(folder, fileName);
    if (inputFile.exists()) {
      return this.getTickService().batchReadPreDataBase(symbolDto, inputFile);
    } else {
      log.error("File {} not exists", inputFile.getAbsolutePath());
      return new TickDto[0];
    }
  }

  private void initIndicatorsAndMovingAverages(final @NotNull UUID @NotNull [] @NotNull [] candlesticksIdMatrix, final @NotNull TechnicalIndicatorService[] indicatorServices) {
    log.info("Initializing indicators and moving average on candlesticks");
    final Stream<Triple<UUID, Stream<TechnicalIndicatorDto>, Stream<MovingAverageDto>>> candlesticksToProcess = IntStream.range(0, candlesticksIdMatrix.length).parallel().boxed()
        .flatMap(k ->
            IntStream.range(0, candlesticksIdMatrix[k].length).parallel().mapToObj(i -> {
              final Stream<TechnicalIndicatorDto> newTechnicalIndicators = IntStream.range(0, indicatorServices.length).boxed().map(j -> indicatorServices[j].initTechnicalIndicator());
              final Stream<MovingAverageDto> newMovingAverages = IntStream.range(0, indicatorServices.length).boxed().flatMap(j -> indicatorServices[j].generateMAs().stream()).distinct()
                  .map(newMovingAverageDto -> switch (newMovingAverageDto.type()) {
                    case EMA -> this.getExponentialMovingAverageService().createMovingAverage(newMovingAverageDto);
                    case SMA -> this.getSimpleMovingAverageService().createMovingAverage(newMovingAverageDto);
                    default -> throw new IllegalStateException("Unexpected value: " + newMovingAverageDto.type());
                  });
              return Triple.of(candlesticksIdMatrix[k][i], newTechnicalIndicators, newMovingAverages);
            }));
    this.getCandlestickService().batchInitIndicatorsAndAveragesOnCandlesticks(candlesticksToProcess);
  }

  private CandlestickDto @NotNull [] processIndicators(final @NotNull UUID @NotNull [] @NotNull [] candlesticksIdMatrix, final int periodMax, final TechnicalIndicatorService[] indicatorServices) {
    final MovingAverageService[] movingAverageServices = new MovingAverageService[]{this.getSimpleMovingAverageService(), this.getExponentialMovingAverageService()};
    return IntStream.range(0, candlesticksIdMatrix.length).parallel().boxed().flatMap(l -> {
      final LinkedList<CandlestickDto> lastCandlesticks = new LinkedList<>();
      final Collection<CandlestickDto> notNeutralCandlesticks = new LinkedList<>();
      IntStream.range(0, candlesticksIdMatrix[l].length).forEach(j -> {
        lastCandlesticks.addFirst(this.getCandlestickService().getCandlestick(candlesticksIdMatrix[l][j]));
        if(lastCandlesticks.size() > periodMax) {
          lastCandlesticks.removeLast();
        }
        IntStream.range(0, movingAverageServices.length).parallel().forEach(i -> movingAverageServices[i].calculateMovingAverage(lastCandlesticks.toArray(CandlestickDto[]::new)));
        lastCandlesticks.removeFirst();
        lastCandlesticks.addFirst(this.getCandlestickService().getCandlestick(candlesticksIdMatrix[l][j]));
        IntStream.range(0, indicatorServices.length).parallel().forEach(i -> indicatorServices[i].calculateTechnicalIndicator(lastCandlesticks.toArray(CandlestickDto[]::new)));
        lastCandlesticks.removeFirst();
        final CandlestickDto candlestickUpdated = this.getCandlestickService().computingSignal(candlesticksIdMatrix[l][j]);
        lastCandlesticks.addFirst(candlestickUpdated);
        if(!candlestickUpdated.signalIndicator().equals(SignalIndicator.NEUTRAL)){
          notNeutralCandlesticks.add(candlestickUpdated);
        }
      });
      return notNeutralCandlesticks.stream();
    }).toArray(CandlestickDto[]::new);
  }

  private UUID[] openOrders(final @NotNull CandlestickDto @NotNull [] notNeutralCandlesticks, final @NotNull Collection<TradeDto> tradesDto, final @NotNull TickDto[] ticksDto){
    final Map<ScopeDto, Map<DayOfWeek, List<TradeDto>>> bigMapTrade = tradesDto.stream().collect(Collectors.groupingBy(TradeDto::scope, Collectors.groupingBy(TradeDto::slotWeek)));
    final Map<UUID, Map<TickDto, Set<OrderType>>> ordersMap = IntStream.range(0, notNeutralCandlesticks.length).parallel().boxed().flatMap(k -> {
      final CandlestickDto candlestickDto = notNeutralCandlesticks[k];
      final int indexTick = IntStream.range(0, ticksDto.length).filter(i -> !ticksDto[i].timestamp().isBefore(candlestickDto.timestamp())).findFirst().orElseThrow();
      return bigMapTrade.getOrDefault(candlestickDto.scope(), new HashMap<>()).getOrDefault(ticksDto[indexTick].timestamp().getDayOfWeek(), new ArrayList<>())
        .parallelStream().filter(tradeDto -> {
          final LocalTime localTime = ticksDto[indexTick].timestamp().toLocalTime();
          return !localTime.isBefore(tradeDto.slotStart()) && !localTime.isAfter(tradeDto.slotEnd());
        })
        .map(tradeDto -> {
          final OrderType orderType = SignalIndicator.BULLISH.equals(candlestickDto.signalIndicator()) ? OrderType.BUY : OrderType.SELL;
          return Triple.of(tradeDto.id(), ticksDto[indexTick], orderType);
        });
    })
    .collect(Collectors.groupingBy(Triple::getLeft, Collectors.groupingBy(Triple::getMiddle, Collectors.collectingAndThen(Collectors.toSet(), triples -> triples.stream().map(Triple::getRight).collect(Collectors.toSet())))));
    return this.getTradeService().batchInitOrdersByTrade(ordersMap);
  }
}
