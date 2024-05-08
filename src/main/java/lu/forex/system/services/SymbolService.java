package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import org.springframework.stereotype.Service;

@Service
public interface SymbolService {

  @Nonnull
  Collection<@NotNull SymbolResponseDto> getSymbols();

  @Nonnull
  Optional<@NotNull SymbolResponseDto> getSymbol(@Nonnull String name);

  @Nonnull
  SymbolResponseDto addSymbol(@Nonnull SymbolCreateDto symbolCreateDto);

  void updateSymbol(@Nonnull SymbolUpdateDto symbolUpdateDto, @Nonnull String name);

  void deleteSymbol(@Nonnull String name);
}
