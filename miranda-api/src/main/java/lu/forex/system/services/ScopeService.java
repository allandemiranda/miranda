package lu.forex.system.services;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.enums.TimeFrame;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface ScopeService {

  @Transactional()
  @NotNull
  ScopeDto addScope(final @NotNull SymbolDto symbolDto, final @NotNull TimeFrame timeFrame);

  @Transactional(readOnly = true)
  @NotNull
  Collection<ScopeDto> getScopesBySymbolName(final @NotNull @NotBlank String symbolName);

  @Transactional(readOnly = true)
  @NotNull
  ScopeDto getScope(final @NotNull @NotBlank String symbolName, final @NotNull TimeFrame timeFrame);
}
