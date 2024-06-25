package lu.forex.system.controllers;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.enums.OrderType;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.operations.TickOperation;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.services.MovingAverageService;
import lu.forex.system.services.OrderService;
import lu.forex.system.services.ScopeService;
import lu.forex.system.services.SymbolService;
import lu.forex.system.services.TechnicalIndicatorService;
import lu.forex.system.services.TickService;
import lu.forex.system.services.TradeService;
import lu.forex.system.utils.TimeFrameUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Getter(AccessLevel.PRIVATE)
public class TickController implements TickOperation {

  @Value("${init.filePath}")
  private String filePathInit;

  private final TickService tickService;
  private final SymbolService symbolService;
  private final CandlestickService candlestickService;
  private final ScopeService scopeService;
  private final TechnicalIndicatorService acceleratorOscillatorService;
  private final TechnicalIndicatorService averageDirectionalIndexService;
  private final TechnicalIndicatorService movingAverageConvergenceDivergenceService;
  private final MovingAverageService simpleMovingAverageService;
  private final MovingAverageService exponentialMovingAverageService;
  private final TradeService tradeService;
  private final OrderService orderService;

  public TickController(final TickService tickService, final SymbolService symbolService, final CandlestickService candlestickService,
      final ScopeService scopeService, @Qualifier("acceleratorOscillator") final TechnicalIndicatorService acceleratorOscillatorService,
      @Qualifier("averageDirectionalIndex") final TechnicalIndicatorService averageDirectionalIndexService,
      @Qualifier("movingAverageConvergenceDivergence") final TechnicalIndicatorService movingAverageConvergenceDivergenceService,
      @Qualifier("simpleMovingAverage") final MovingAverageService simpleMovingAverageService,
      @Qualifier("exponentialMovingAverage") final MovingAverageService exponentialMovingAverageService, final TradeService tradeService,
      final OrderService orderService) {
    this.tickService = tickService;
    this.symbolService = symbolService;
    this.candlestickService = candlestickService;
    this.scopeService = scopeService;
    this.acceleratorOscillatorService = acceleratorOscillatorService;
    this.averageDirectionalIndexService = averageDirectionalIndexService;
    this.movingAverageConvergenceDivergenceService = movingAverageConvergenceDivergenceService;
    this.simpleMovingAverageService = simpleMovingAverageService;
    this.exponentialMovingAverageService = exponentialMovingAverageService;
    this.tradeService = tradeService;
    this.orderService = orderService;
  }

  @Override
  public List<TickDto> getTicksBySymbolName(final String symbolName) {
    return this.getTickService().getTicksBySymbolName(symbolName);
  }

  @Override
  public String addTickBySymbolName(final NewTickDto newTickDto, final String symbolName) {
    final SymbolDto symbolDto = this.getSymbolService().getSymbol(symbolName);
    final TickDto tickDto = this.getTickService().addTickBySymbol(newTickDto, symbolDto);

    final Collection<TechnicalIndicatorService> indicatorServices = List.of(this.getAcceleratorOscillatorService(), this.getAverageDirectionalIndexService(), this.getMovingAverageConvergenceDivergenceService());
    final Collection<MovingAverageService> movingAverageServices = List.of(this.getSimpleMovingAverageService(), this.getExponentialMovingAverageService());
    final int technicalIndicatorSize = indicatorServices.stream().mapToInt(TechnicalIndicatorService::getNumberOfCandlesticksToCalculate).max() .orElse(0);
    final TickDto lastTickDto = this.getTickService().getLestTickBySymbolName(symbolName).orElse(tickDto);

    final String response = this.getScopeService().getScopesBySymbolName(symbolName).parallelStream()
        .map(scopeDto -> this.getCandlestickService().processingCandlestick(tickDto, scopeDto))
        .map(candlestickDto -> {
          if (candlestickDto.technicalIndicators().isEmpty()) {
            final Collection<TechnicalIndicatorDto> newTechnicalIndicators = indicatorServices.stream().map(TechnicalIndicatorService::initTechnicalIndicator).toList();
            return this.getCandlestickService().addingTechnicalIndicators(newTechnicalIndicators, candlestickDto.id());
          } else {
            return candlestickDto;
          }
        })
        .map(candlestickDto -> {
          if (candlestickDto.movingAverages().isEmpty()) {
            final Collection<MovingAverageDto> newMovingAverages = indicatorServices.stream()
                .flatMap(indicatorService -> indicatorService.generateMAs().stream()).distinct()
                .map(newMovingAverageDto -> switch (newMovingAverageDto.type()) {
                  case EMA -> this.getExponentialMovingAverageService().createMovingAverage(newMovingAverageDto);
                  case SMA -> this.getSimpleMovingAverageService().createMovingAverage(newMovingAverageDto);
                  default -> throw new IllegalStateException("Unexpected value: " + newMovingAverageDto.type());
                }).toList();
            return this.getCandlestickService().addingMovingAverages(newMovingAverages, candlestickDto.id()).scope();
          } else {
            return candlestickDto.scope();
          }
        })
        .filter(scopeDto -> !TimeFrameUtils.getCandlestickTimestamp(tickDto.timestamp(), scopeDto.timeFrame()).equals(TimeFrameUtils.getCandlestickTimestamp(lastTickDto.timestamp(), scopeDto.timeFrame())))
        .map(scopeDto -> {
          final List<CandlestickDto> lastCandlesticks = this.getCandlestickService().findCandlesticksDescWithLimit(scopeDto.id(), technicalIndicatorSize + 1).stream().skip(1).toList();
          movingAverageServices.forEach(movingAverageService -> movingAverageService.calculateMovingAverage(lastCandlesticks));
          indicatorServices.parallelStream().forEach(indicatorService -> indicatorService.calculateTechnicalIndicator(lastCandlesticks));
          return this.getCandlestickService().processSignalIndicatorByCandlestickId(lastCandlesticks.getFirst().id());
        })
        .filter(lastCandlestick -> !SignalIndicator.NEUTRAL.equals(lastCandlestick.signalIndicator()))
        .map(candlestickDto -> {
          final Set<TradeDto> tradeDtos = this.getTradeService().getTradesForOpenPositionActivated(candlestickDto.scope(), tickDto).parallelStream().collect(Collectors.toSet());
          return new SimpleEntry<>(candlestickDto, tradeDtos);
        })
        .filter(entry -> !entry.getValue().isEmpty())
        .flatMap(entry -> {
          final var candlestickDto = entry.getKey();
          final OrderType orderType = SignalIndicator.BULLISH.equals(candlestickDto.signalIndicator()) ? OrderType.BUY : OrderType.SELL;
          return entry.getValue().stream().collect(Collectors.groupingBy(tradeDto -> new SimpleEntry<>(tradeDto.takeProfit(), tradeDto.stopLoss())))
              .entrySet().parallelStream().map(simpleEntry -> {
                final var takeProfit = simpleEntry.getKey().getKey();
                final var stopLoss = simpleEntry.getKey().getValue();
                return String.format("%s %s %s %s %s",
                    tickDto.timestamp(),
                    Arrays.toString(simpleEntry.getValue().stream().map(tradeDto -> tradeDto.scope().timeFrame()).sorted().toArray()),
                    orderType,
                    takeProfit,
                    stopLoss);
              });
        })
        .reduce("", (a, b) -> {
          if (a.isEmpty()) {
            return b;
          } else if (b.isEmpty()) {
            return a;
          } else {
            return a.concat(",").concat(b);
          }
        });

    this.getOrderService().updateOrders(tickDto);

    return response;

  }

  @Override
  public void initDataBase(final String symbolName, final String dateFileName) {
    final SymbolDto symbolDto = this.getSymbolService().getSymbol(symbolName);
    final var fileName = this.getFilePathInit().concat(symbolName).concat(dateFileName).concat(".csv");
    final var inputFile = new File(fileName);
    this.getTickService().readPreDataBase(symbolDto, inputFile);
  }
}
