package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.dtos.NewSymbolDto;
import lu.forex.system.dtos.ResponseSymbolDto;
import org.springframework.transaction.annotation.Transactional;

public interface SymbolService {

  @Transactional(readOnly = true)
  @Nonnull
  Collection<@NotNull ResponseSymbolDto> getSymbols();

  @Transactional(readOnly = true)
  @Nonnull
  ResponseSymbolDto getSymbol(@Nonnull String symbolName);

  @Transactional()
  @Nonnull
  ResponseSymbolDto addSymbol(@Nonnull NewSymbolDto symbolDto);
}
