package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface SymbolService {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  @Nonnull
  Collection<@NotNull SymbolResponseDto> getSymbols();

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  @Nonnull
  Optional<@NotNull SymbolResponseDto> getSymbol(@Nonnull String name);

  @Transactional(propagation = Propagation.REQUIRED)
  @Nonnull
  SymbolResponseDto addSymbol(@Nonnull SymbolCreateDto symbolCreateDto);

  @Transactional(propagation = Propagation.REQUIRED)
  void updateSymbol(@Nonnull SymbolUpdateDto symbolUpdateDto, @Nonnull String name);

  @Transactional(propagation = Propagation.REQUIRED)
  void deleteSymbol(@Nonnull String name);
}
