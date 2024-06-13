package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Trade;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.mappers.ScopeMapper;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.mappers.TradeMapper;
import lu.forex.system.repositories.TradeRepository;
import lu.forex.system.services.TradeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TradeProvider implements TradeService {

  @Value("${trade.slot.minutes:15}")
  private int slotMinutes;

  @Value("#{${trade.slot.config}}")
  private Map<String, Map<String, List<Integer>>> tradeConfig;

  private final TradeRepository tradeRepository;
  private final TradeMapper tradeMapper;
  private final ScopeMapper scopeMapper;
  private final SymbolMapper symbolMapper;

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
    final Collection<DayOfWeek> validWeeks = Arrays.stream(DayOfWeek.values()).filter(dayOfWeek -> !DayOfWeek.SATURDAY.equals(dayOfWeek) && !DayOfWeek.SUNDAY.equals(dayOfWeek)).toList();

    return this.getTradeConfig().entrySet().stream().flatMap(timeFrameInput -> {
      final TimeFrame timeFrame = TimeFrame.valueOf(timeFrameInput.getKey());

      final Collection<Integer> spreads = timeFrameInput.getValue().get("spread");
      final Collection<Integer> tps = timeFrameInput.getValue().get("tp");
      final Collection<Integer> sls = timeFrameInput.getValue().get("sl");

      return scopeDtos.stream().filter(scopeDto -> scopeDto.timeFrame().equals(timeFrame)).map(scopeDto -> this.getScopeMapper().toEntity(scopeDto)).flatMap(scope -> spreads.stream().flatMap(
          spread -> tps.stream()
              .flatMap(tp -> sls.stream().filter(sl -> sl <= tp && sl > spread).flatMap(sl -> validWeeks.stream().flatMap(week -> localTimes.stream().map(time -> {
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

    }).map(trade -> this.getTradeRepository().save(trade)).map(trade -> this.getTradeMapper().toDto(trade)).toList();
  }

}
