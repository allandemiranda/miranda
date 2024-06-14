package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.SymbolDto;
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
  List<TradeDto> testServiceRemove(SymbolDto symbol);

  @Transactional(readOnly = true)
  @NotNull
  Collection<TradeDto> getTradesForOpenPosition(final @NonNull ScopeDto scopeDto, final @NonNull TickDto tickDto);

  @Transactional
  @NotNull
  TradeDto addOrder(final @NotNull TickDto openTick, final @NotNull OrderType orderType, final boolean isSimulator, final @NotNull TradeDto tradeDto);

}
