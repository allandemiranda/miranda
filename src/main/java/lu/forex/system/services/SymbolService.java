package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface SymbolService {

  @NotNull
  Collection<@NotNull SymbolResponseDto> getSymbols();

  @NotNull
  Optional<@NotNull SymbolResponseDto> getSymbol(@NotNull String name);

  @NotNull
  SymbolResponseDto addSymbol(@NotNull SymbolCreateDto symbolCreateDto);

  @Transactional
  @NotNull
  SymbolResponseDto updateSymbol(@NotNull SymbolUpdateDto symbolUpdateDto, @NotNull String name);

  @Transactional
  boolean deleteSymbol(@NotNull String name);
}
