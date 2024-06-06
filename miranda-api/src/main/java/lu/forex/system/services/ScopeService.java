package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.enums.TimeFrame;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lu.forex.system.dtos.SymbolDto;

@Service
public interface ScopeService {

  @Transactional()
  @NotNull
  ScopeDto addScope(final @NotNull SymbolDto symbolDto, final @NotNull TimeFrame timeFrame);

  @Transactional(readOnly = true)
  @NotNull
  Collection<ScopeDto> getScopesBySymbol(final @NotNull SymbolDto symbolDto);

  @Transactional(readOnly = true)
  @NotNull
  ScopeDto getScope(final @NotNull SymbolDto symbolDto, final @NotNull TimeFrame timeFrame);
}
