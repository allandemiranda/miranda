package lu.forex.system.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import org.springframework.stereotype.Service;

@Service
public interface SymbolService {

  @NotNull
  Collection<SymbolDto> findAll();

  @NotNull
  Optional<SymbolDto> findByName(@NotNull @NotBlank String name);

  @NotNull
  SymbolDto save(@NotNull SymbolDto symbolDto);

  @NotNull
  Optional<SymbolDto> updateDigitsAndSwapLongAndSwapShortByName(@NotNull SymbolUpdateDto symbolUpdateDto, @NotNull @NotBlank String name);

  boolean deleteByName(@NotNull @NotBlank String name);

}
