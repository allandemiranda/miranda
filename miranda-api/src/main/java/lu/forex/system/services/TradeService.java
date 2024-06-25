package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.dtos.TradeDto;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface TradeService {

  @Transactional
  @NotNull
  Collection<TradeDto> generateTrades(final @NotNull Set<@NotNull ScopeDto> scopeDtos);

  @Transactional(readOnly = true)
  @NotNull
  Collection<TradeDto> getTrades(final @NotNull UUID symbolId);

  @Transactional(readOnly = true)
  @NotNull
  Collection<TradeDto> getTradesForOpenPositionActivated(final @NonNull ScopeDto scopeDto, final @NonNull TickDto tickDto);

  @Transactional
  @NotNull
  List<TradeDto> managementEfficientTradesScenariosToBeActivated(final @NotNull String symbolName);

  @Async
  void initOrders(final @NotNull Map<LocalDateTime, Set<CandlestickDto>> tickByCandlesticks,final @NotNull List<TickDto> ticks);

}
