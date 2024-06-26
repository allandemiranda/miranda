package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.dtos.TradeDto;
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
  Collection<TradeDto> getTradesForOpenPositionActivated(final @NonNull ScopeDto scopeDto, final @NonNull TickDto tickDto);

  @Transactional
  List<TradeDto> managementEfficientTradesScenarioToBeActivated(final @NotNull Stream<UUID> tradeIdStream);

  @Transactional
  @NotNull
  Stream<TradeDto> initOrdersByTrade(final @NotNull Map<LocalDateTime, Set<CandlestickDto>> tickByCandlesticks,final @NotNull List<TickDto> ticks);

}
