package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.TickDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface CandlestickService {

  @Transactional(readOnly = true)
  @NotNull
  Collection<@NotNull CandlestickDto> getCandlesticks(final @NotNull ScopeDto scopeDto);

  @Transactional()
  @NotNull
  CandlestickDto processingCandlestick(final @NotNull TickDto tickDto, final @NotNull ScopeDto scopeDto);
}
