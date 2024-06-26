package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.entities.Order;
import lu.forex.system.entities.Tick;
import lu.forex.system.entities.Trade;
import lu.forex.system.enums.OrderStatus;
import lu.forex.system.enums.OrderType;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.mappers.OrderMapper;
import lu.forex.system.mappers.ScopeMapper;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.mappers.TradeMapper;
import lu.forex.system.repositories.TradeRepository;
import lu.forex.system.services.TradeService;
import lu.forex.system.utils.OrderUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
@Log4j2
public class TradeProvider implements TradeService {

  private final TradeRepository tradeRepository;
  private final TradeMapper tradeMapper;
  private final ScopeMapper scopeMapper;
  private final SymbolMapper symbolMapper;
  private final TickMapper tickMapper;
  private final OrderMapper orderMapper;
  @Value("${trade.slot.minutes:15}")
  private int slotMinutes;
  @Value("#{${trade.slot.config}}")
  private Map<String, Map<String, List<Integer>>> tradeConfig;

  @Override
  public @NotNull Collection<TradeDto> generateTrades(final @NotNull Set<ScopeDto> scopeDtos) {

    final int subTime = 1440 / this.getSlotMinutes();
    final Collection<LocalTime[]> localTimes = IntStream.range(0, subTime).mapToObj(i -> {
      final int hourInitial = (i * this.getSlotMinutes()) / 60;
      final int minuteInitial = (i * this.getSlotMinutes()) % 60;
      final LocalTime initialTime = LocalTime.of(hourInitial, minuteInitial);
      final int hourFinal = ((i + 1) * this.getSlotMinutes()) / 60;
      final int minuteFinal = ((i + 1) * this.getSlotMinutes()) % 60;
      final LocalTime initialFinal = hourFinal == 24 ? LocalTime.of(23, 59, 59) : LocalTime.of(hourFinal, minuteFinal).minusSeconds(1);
      return new LocalTime[]{initialTime, initialFinal};
    }).toList();
    final Collection<DayOfWeek> validWeeks = Arrays.stream(DayOfWeek.values())
        .filter(dayOfWeek -> !DayOfWeek.SATURDAY.equals(dayOfWeek) && !DayOfWeek.SUNDAY.equals(dayOfWeek)).toList();

    final Collection<Trade> collection = this.getTradeConfig().entrySet().parallelStream().flatMap(timeFrameInput -> {
      final TimeFrame timeFrame = TimeFrame.valueOf(timeFrameInput.getKey());

      final Collection<Integer> spreads = timeFrameInput.getValue().get("spread");
      final Collection<Integer> tps = timeFrameInput.getValue().get("tp");
      final Collection<Integer> sls = timeFrameInput.getValue().get("sl");

      return scopeDtos.parallelStream().filter(scopeDto -> scopeDto.timeFrame().equals(timeFrame))
          .map(scopeDto -> this.getScopeMapper().toEntity(scopeDto)).flatMap(scope -> spreads.parallelStream().flatMap(spread -> tps.parallelStream()
              .flatMap(tp -> sls.parallelStream().filter(sl -> sl <= tp && sl > spread)
                  .flatMap(sl -> validWeeks.parallelStream().flatMap(week -> localTimes.parallelStream().map(time -> {
                    final Trade trade = new Trade();
                    trade.setScope(scope);
                    trade.setStopLoss(sl);
                    trade.setTakeProfit(tp);
                    trade.setSpreadMax(spread);
                    trade.setSlotWeek(week);
                    trade.setSlotStart(time[0]);
                    trade.setSlotEnd(time[1]);
                    trade.setActivate(false);
                    return trade;
                  }))))));

    }).toList();

    return this.getTradeRepository().saveAll(collection).parallelStream().map(trade -> this.getTradeMapper().toDto(trade)).toList();
  }

  @Override
  public @NotNull Collection<TradeDto> getTrades(final @NotNull UUID symbolId) {
    return this.getTradeRepository().findByScope_Symbol_Id(symbolId).parallelStream().map(trade -> this.getTradeMapper().toDto(trade)).toList();
  }

  @Override
  public @NotNull Collection<TradeDto> getTradesForOpenPositionActivated(final @NotNull ScopeDto scopeDto, final @NotNull TickDto tickDto) {
    return this.getTradeRepository()
        .findTradeToOpenOrder(scopeDto.id(), (int) tickDto.spread(), tickDto.timestamp().getDayOfWeek(), tickDto.timestamp().toLocalTime(), true)
        .parallelStream().map(this.getTradeMapper()::toDto).toList();
  }

  @Override
  public @NotNull List<TradeDto> managementEfficientTradesScenarioToBeActivated(final @NotNull Stream<UUID> tradeIdStream) {
    final Collection<Trade> collection = tradeIdStream.parallel().map(uuid -> this.getTradeRepository().findById(uuid).orElseThrow()).filter(trade -> {
      if (trade.getBalance() > 0 && (trade.getBalance() - trade.getOrders().stream().filter(order -> OrderStatus.OPEN.equals(order.getOrderStatus())).mapToDouble(Order::getProfit).sum()) > 0) {
        if (trade.getOrders().stream().noneMatch(order -> OrderStatus.STOP_LOSS.equals(order.getOrderStatus()))) {
           return true;
        }
        final long totalOrdersClose = trade.getOrders().stream().filter(order -> !OrderStatus.OPEN.equals(order.getOrderStatus())).count();
        final long totalOrdersTP = trade.getOrders().stream().filter(order -> OrderStatus.TAKE_PROFIT.equals(order.getOrderStatus())).count();
        final long percentage = (totalOrdersTP * 100L) / totalOrdersClose;
        return percentage >= 66L;
      }
      return false;
    }).map(trade -> {
      trade.setActivate(true);
      return trade;
    }).toList();
    return this.getTradeRepository().saveAll(collection).stream().sorted(Comparator.comparingDouble(Trade::getBalance)).map(trade -> this.getTradeMapper().toDto(trade)).toList();
  }

  @Override
  public @NotNull Stream<TradeDto> initOrdersByTrade(final @NotNull Map<LocalDateTime, Set<CandlestickDto>> tickByCandlesticks, final @NotNull List<TickDto> ticks) {
    final String symbolName = ticks.getFirst().symbol().currencyPair().name();
    log.info("Starting initOrders({})", symbolName);

    final Map<UUID, Map<DayOfWeek, List<Trade>>> tradesMap = this.getTradeRepository().findBySymbolName(symbolName).stream()
        .collect(Collectors.groupingBy(trade -> trade.getScope().getId(), Collectors.groupingBy(Trade::getSlotWeek)));

    final var trades = tickByCandlesticks.entrySet().parallelStream()
      .map(entry -> {
        final var timestamp = entry.getKey();
        final var tickDto = ticks.stream().filter(tick -> !tick.timestamp().isBefore(timestamp)).findFirst().orElse(null);
        return new SimpleEntry<>(tickDto, entry.getValue());
      }).flatMap(entry -> {
      final Tick tick = this.getTickMapper().toEntity(entry.getKey());
      return entry.getValue().parallelStream().flatMap(candlestickDto -> {
        final OrderType orderType = SignalIndicator.BULLISH.equals(candlestickDto.signalIndicator()) ? OrderType.BUY : OrderType.SELL;

        return tradesMap.get(candlestickDto.scope().id()).get(tick.getTimestamp().getDayOfWeek()).parallelStream()
            .filter(trade -> !tick.getTimestamp().toLocalTime().isBefore(trade.getSlotStart()) && !tick.getTimestamp().toLocalTime().isAfter(trade.getSlotEnd()) && trade.getSpreadMax() >= tick.getSpread())
            .map(trade -> {
              final Order order = new Order();
              order.setOpenTick(tick);
              order.setCloseTick(tick);
              order.setOrderType(orderType);
              order.setOrderStatus(OrderStatus.OPEN);
              final double profit = OrderUtils.getProfit(order);
              order.setProfit(profit);
              order.setTrade(trade);
              return order;
            });
      });
    }).collect(Collectors.groupingBy(Order::getTrade)).entrySet().parallelStream().map(entry -> {
      final Trade trade = entry.getKey();
      final Collection<Order> orders = entry.getValue();
      trade.getOrders().addAll(orders);
      return trade;
    }).toList();

    log.info("Ending initOrders({})", symbolName);
    return this.getTradeRepository().saveAll(trades).parallelStream().map(trade -> this.getTradeMapper().toDto(trade));

  }
}
