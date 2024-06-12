package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TradeDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface TradeService {

  @Transactional
  @NotNull
  Collection<TradeDto> generateTrades(final @NotNull Set< @NotNull ScopeDto> scopeDtos);

  @Transactional(readOnly = true)
  @NotNull
  List<TradeDto> getTradesBySymbolByBalanceDesc(final @NotNull SymbolDto symbolDto);
}
