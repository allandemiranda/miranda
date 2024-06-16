package lu.forex.system.controllers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.enums.OrderType;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.operations.TickOperation;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.services.MovingAverageService;
import lu.forex.system.services.ScopeService;
import lu.forex.system.services.SymbolService;
import lu.forex.system.services.TechnicalIndicatorService;
import lu.forex.system.services.TickService;
import lu.forex.system.services.TradeService;
import lu.forex.system.utils.TimeFrameUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Getter(AccessLevel.PRIVATE)
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
      @Qualifier("movingAverageConvergenceDivergence") final TechnicalIndicatorService movingAverageConvergenceDivergenceService, @Qualifier("simpleMovingAverage") final MovingAverageService simpleMovingAverageService,
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
  public Collection<OrderDto> addTickBySymbolName(final NewTickDto newTickDto, final String symbolName) {
    final SymbolDto symbolDto = this.getSymbolService().getSymbol(symbolName);
    final TickDto tickDto = this.getTickService().addTickBySymbol(newTickDto, symbolDto);

    final Collection<TechnicalIndicatorService> indicatorServices = List.of(this.getAcceleratorOscillatorService(), this.getAverageDirectionalIndexService(), this.getMovingAverageConvergenceDivergenceService());
    final Collection<MovingAverageService> movingAverageServices = List.of(this.getSimpleMovingAverageService(), this.getExponentialMovingAverageService());
    final int technicalIndicatorSize = indicatorServices.stream().mapToInt(TechnicalIndicatorService::getNumberOfCandlesticksToCalculate).max().orElse(0);

    final Collection<ScopeDto> scopeDtos = this.getScopeService().getScopesBySymbol(symbolDto).stream()
      .map(scopeDto -> this.getCandlestickService().processingCandlestick(tickDto, scopeDto))
      .map(candlestickDto -> {
        if (candlestickDto.technicalIndicators().isEmpty()) {
          final Collection<TechnicalIndicatorDto> newTechnicalIndicators = indicatorServices.stream().map(TechnicalIndicatorService::initTechnicalIndicator).toList();
          return this.getCandlestickService().addingTechnicalIndicators(newTechnicalIndicators, candlestickDto);
        } else {
          return candlestickDto;
        }
      }).map(candlestickDto -> {
        if(candlestickDto.movingAverages().isEmpty()) {
          final Collection<MovingAverageDto> newMovingAverages = indicatorServices.stream().flatMap(indicatorService -> indicatorService.generateMAs().stream()).distinct().map(newMovingAverageDto ->
            switch (newMovingAverageDto.type()) {
              case EMA -> this.getExponentialMovingAverageService().createMovingAverage(newMovingAverageDto);
              case SMA -> this.getSimpleMovingAverageService().createMovingAverage(newMovingAverageDto);
              default -> throw new IllegalStateException("Unexpected value: " + newMovingAverageDto.type());
            }
          ).toList();
          return this.getCandlestickService().addingMovingAverages(newMovingAverages, candlestickDto);
        } else {
          return candlestickDto;
        }
      }).map(CandlestickDto::scope).collect(Collectors.toSet());

    final Collection<List<CandlestickDto>> preMa = scopeDtos.stream().map(scopeDto -> this.getCandlestickService().findCandlesticksDescWithLimit(scopeDto, technicalIndicatorSize)).toList();
    preMa.forEach(lastCandlesticks ->  movingAverageServices.forEach(movingAverageService -> movingAverageService.calculateMovingAverage(lastCandlesticks)));

    final Collection<List<CandlestickDto>> postMa = scopeDtos.stream().map(scopeDto -> this.getCandlestickService().findCandlesticksDescWithLimit(scopeDto, technicalIndicatorSize)).toList();
    postMa.forEach(lastCandlesticks -> indicatorServices.forEach(indicatorService -> indicatorService.calculateTechnicalIndicator(lastCandlesticks)));

    final TickDto lastTickDto = this.getTickService().getLestTickBySymbolId(symbolDto.id()).orElse(tickDto);

    return scopeDtos.stream()
        .filter(scopeDto -> !TimeFrameUtils.getCandlestickTimestamp(tickDto.timestamp(), scopeDto.timeFrame()).equals(TimeFrameUtils.getCandlestickTimestamp(lastTickDto.timestamp(), scopeDto.timeFrame())))
        .map(scopeDto -> this.getCandlestickService().findCandlesticksDescWithLimit(scopeDto, 2))
        .map(List::getLast)
        .filter(candlestickDto -> !SignalIndicator.NEUTRAL.equals(candlestickDto.signalIndicator()))
        .flatMap(candlestickDto -> {
          final ScopeDto scopeDto = candlestickDto.scope();
          final OrderType orderType = SignalIndicator.BULLISH.equals(candlestickDto.signalIndicator()) ? OrderType.BUY : OrderType.SELL;
          return this.getTradeService().getTradesForOpenPosition(scopeDto, tickDto).stream().map(tradeDto -> this.getTradeService().addOrder(tickDto, orderType, true, tradeDto));
        }).flatMap(tradeDto -> tradeDto.orders().stream().filter(orderDto -> orderDto.openTick().equals(tickDto)))
        .toList();

  }
}
