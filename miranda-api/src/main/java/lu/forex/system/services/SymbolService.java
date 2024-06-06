package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.dtos.NewSymbolDto;
import lu.forex.system.dtos.SymbolDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface SymbolService {

  @Transactional(readOnly = true)
  @NotNull
  Collection<@NotNull SymbolDto> getSymbols();

  @Transactional(readOnly = true)
  @NotNull
  SymbolDto getSymbol(final @NotNull String symbolName);

  @Transactional()
  @NotNull
  SymbolDto addSymbol(final @NotNull NewSymbolDto newSymbolDto);
}
