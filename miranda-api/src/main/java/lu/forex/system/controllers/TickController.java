package lu.forex.system.controllers;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.entities.Order;
import lu.forex.system.enums.OrderStatus;
import lu.forex.system.enums.OrderType;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.operations.TickOperation;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.services.MovingAverageService;
import lu.forex.system.services.ScopeService;
import lu.forex.system.services.SymbolService;
import lu.forex.system.services.TechnicalIndicatorService;
import lu.forex.system.services.TickService;
import lu.forex.system.services.TradeService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Getter(AccessLevel.PRIVATE)
@Log4j2
public class TickController implements TickOperation {

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

  public TickController(final TickService tickService, final SymbolService symbolService, final CandlestickService candlestickService,
      final ScopeService scopeService, @Qualifier("acceleratorOscillator") final TechnicalIndicatorService acceleratorOscillatorService,
      @Qualifier("averageDirectionalIndex") final TechnicalIndicatorService averageDirectionalIndexService,
      @Qualifier("movingAverageConvergenceDivergence") final TechnicalIndicatorService movingAverageConvergenceDivergenceService,
      @Qualifier("simpleMovingAverage") final MovingAverageService simpleMovingAverageService,
      @Qualifier("exponentialMovingAverage") final MovingAverageService exponentialMovingAverageService, final TradeService tradeService) {
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
  }

  @Override
  public List<TickDto> getTicksBySymbolName(final String symbolName) {
    return this.getTickService().getTicksBySymbolName(symbolName);
  }

  @Override
  public String addTickBySymbolName(final NewTickDto newTickDto, final String symbolName) {

    final SymbolDto symbolDto = this.getSymbolService().getSymbol(symbolName);
    final TickDto tickDto = this.getTickService().addTickBySymbol(newTickDto, symbolDto);
    final TechnicalIndicatorService[] indicatorServices = new TechnicalIndicatorService[]{this.getAcceleratorOscillatorService(), this.getAverageDirectionalIndexService(), this.getMovingAverageConvergenceDivergenceService()};
    final MovingAverageService[] movingAverageServices = new MovingAverageService[]{this.getSimpleMovingAverageService(), this.getExponentialMovingAverageService()};

    final String response =
        this.getScopeService().getScopesBySymbolName(symbolName).parallelStream()
        .map(scopeDto -> this.getCandlestickService().processingCandlestick(tickDto, scopeDto))
        .filter(candlestickDto -> candlestickDto.technicalIndicators().isEmpty())
        .map(candlestickDto -> {
          final Stream<TechnicalIndicatorDto> newTechnicalIndicators = IntStream.range(0, indicatorServices.length).boxed().map(i -> indicatorServices[i].initTechnicalIndicator());
          final Stream<MovingAverageDto> newMovingAverages = IntStream.range(0, indicatorServices.length).boxed().flatMap(i -> indicatorServices[i].generateMAs().stream()).distinct()
              .map(newMovingAverageDto -> switch (newMovingAverageDto.type()) {
                case EMA -> this.getExponentialMovingAverageService().createMovingAverage(newMovingAverageDto);
                case SMA -> this.getSimpleMovingAverageService().createMovingAverage(newMovingAverageDto);
                default -> throw new IllegalStateException("Unexpected value: " + newMovingAverageDto.type());
              });
          return this.getCandlestickService().addingTechnicalIndicatorsAndMovingAverage(newTechnicalIndicators, newMovingAverages, candlestickDto.id()).scope().id();
        })
        .map(scopeId -> {
          final CandlestickDto[] lastCandlesticks = Arrays.stream(this.getCandlestickService().findCandlesticksDescLimited(scopeId)).skip(1).toArray(CandlestickDto[]::new);
          IntStream.range(0, movingAverageServices.length).parallel().forEach(i -> movingAverageServices[i].calculateMovingAverage(lastCandlesticks));
          lastCandlesticks[0] = this.getCandlestickService().getCandlestick(lastCandlesticks[0].id());
          IntStream.range(0, indicatorServices.length).parallel().forEach(i -> indicatorServices[i].calculateTechnicalIndicator(lastCandlesticks));
          return this.getCandlestickService().computingSignal(lastCandlesticks[0].id());
        })
        .filter(lastCandlestick -> !SignalIndicator.NEUTRAL.equals(lastCandlestick.signalIndicator()))
        .map(candlestickDto -> {
          final OrderType orderType = SignalIndicator.BULLISH.equals(candlestickDto.signalIndicator()) ? OrderType.BUY : OrderType.SELL;
          final Map<TimeFrame, List<TradeDto>> mapCollection = this.getTradeService().getTradesForOpenPositionActivated(candlestickDto.scope(), tickDto).stream()
              .collect(Collectors.groupingBy(tradeDto -> tradeDto.scope().timeFrame()));
          if(mapCollection.size() >= 2 || mapCollection.keySet().stream().allMatch(TimeFrame.D1::equals)) {
            // A new indicator: need be more than X number of timeframes requesting to open the position

            final TradeDto tradeSelect = mapCollection.values().stream().flatMap(Collection::stream).reduce((tradeDto, tradeDto2) ->
                tradeDto.orders().stream().filter(orderDto -> !OrderStatus.OPEN.equals(orderDto.orderStatus())).mapToDouble(OrderDto::profit).sum() >=
                tradeDto2.orders().stream().filter(orderDto -> !OrderStatus.OPEN.equals(orderDto.orderStatus())).mapToDouble(OrderDto::profit).sum()
                    ? tradeDto : tradeDto2).orElseThrow();
            return String.format("%s %s %s %s %s",
                tickDto.timestamp(),
                Arrays.toString(mapCollection.keySet().stream().sorted().toArray()),
                orderType,
                tradeSelect.takeProfit(),
                tradeSelect.stopLoss());
          } else {
            return "";
          }
        }).filter(s -> !s.isEmpty() && !s.isBlank())
        .reduce("", (a, b) -> {
          if (a.isEmpty()) {
            return b;
          } else if (b.isEmpty()) {
            return a;
          } else {
            return a.concat(",").concat(b);
          }
        });

    this.getTickService().addLastTickPerformed(tickDto);

    if(!response.isEmpty()){
      log.info("Response: {}", response);
    }
    return response;

  }
}
