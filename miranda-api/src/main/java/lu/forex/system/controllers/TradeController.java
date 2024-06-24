package lu.forex.system.controllers;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.operations.TradeOperation;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.services.ScopeService;
import lu.forex.system.services.SymbolService;
import lu.forex.system.services.TickService;
import lu.forex.system.services.TradeService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TradeController implements TradeOperation {

  private final TradeService tradeService;
  private final SymbolService symbolService;
  private final ScopeService scopeService;
  private final CandlestickService candlestickService;
  private final TickService tickService;

  @Override
  public List<TradeDto> managementOfTradeActivation(final String symbolName) {
    return this.getTradeService().managementEfficientTradesScenariosToBeActivated(symbolName);
  }

  @Override
  public Collection<TradeDto> getTrades(final String symbolName) {
    final UUID symbolId = this.getSymbolService().getSymbol(symbolName).id();
    return this.getTradeService().getTrades(symbolId);
  }

  @Override
  public void initOrderByInitCandlesticks(final String symbolName) {
    final var entryCollection = Arrays.stream(TimeFrame.values()).parallel()
        .map(timeFrame -> this.getScopeService().getScope(symbolName, timeFrame).id())
        .flatMap(uuid -> this.getCandlestickService().getAllCandlestickNotNeutralByScopeIdAsync(uuid).stream()).map(candlestickDto -> {
          final TickDto tickDto = this.getTickService().getFirstAndNextTick(candlestickDto.scope().symbol().id(), candlestickDto.timestamp());
          return new SimpleEntry<TickDto, CandlestickDto>(tickDto, candlestickDto);
        }).collect(Collectors.toSet());

    final Map<TickDto, Set<CandlestickDto>> tickByCandlesticks = entryCollection.stream().collect(Collectors.groupingBy(SimpleEntry::getKey,
        Collectors.collectingAndThen(Collectors.toSet(),
            simpleEntries -> simpleEntries.stream().map(SimpleEntry::getValue).collect(Collectors.toSet()))));

    this.getTradeService().initOrders(tickByCandlesticks);
  }

}
