package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import java.util.stream.Stream;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface SymbolService {

  @Transactional(readOnly = true)
  @Nonnull
  Stream<@NotNull SymbolResponseDto> getSymbols();

  @Transactional(readOnly = true)
  @Nonnull
  Optional<@NotNull SymbolResponseDto> getSymbol(@Nonnull @NotBlank @Size(min = 6, max = 6) String name);

  @Transactional()
  @Nonnull
  SymbolResponseDto addSymbol(@Nonnull SymbolCreateDto symbolCreateDto);

  @Transactional()
  void updateSymbol(@Nonnull SymbolUpdateDto symbolUpdateDto, @Nonnull @NotBlank @Size(min = 6, max = 6) String name);

  @Transactional()
  void deleteSymbol(@Nonnull @NotBlank @Size(min = 6, max = 6) String name);
}
