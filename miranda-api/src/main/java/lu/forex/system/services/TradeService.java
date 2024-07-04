package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.enums.OrderType;
import org.springframework.lang.NonNull;
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
  Collection<TradeDto> getTradesActive();

  @Transactional(readOnly = true)
  @NotNull
  Collection<TradeDto> getTradesForOpenPositionActivated(final @NonNull ScopeDto scopeDto, final @NonNull TickDto tickDto);

  @Transactional
  void batchInitManagementTrades(final @NotNull UUID[] tradesIds);

  @Transactional
  UUID[] batchInitOrdersByTrade(final @NotNull Map<UUID, Map<TickDto, Set<OrderType>>> ordersMap);

}
