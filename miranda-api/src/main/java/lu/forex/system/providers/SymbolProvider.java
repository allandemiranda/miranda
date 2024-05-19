package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.services.SymbolService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class SymbolProvider implements SymbolService {

  private final SymbolRepository symbolRepository;
  private final SymbolMapper symbolMapper;

  @NotNull
  @Override
  public Collection<@NotNull SymbolResponseDto> getSymbols() {
    return this.getSymbolRepository().findAll().stream().map(symbolMapper::toDto).toList();
  }

  @NotNull
  @Override
  public Optional<@NotNull SymbolResponseDto> getSymbol(@NotNull final String name) {
    return this.getSymbolRepository().findFirstByNameOrderByNameAsc(name).map(symbolMapper::toDto);
  }

  @NotNull
  @Override
  public SymbolResponseDto addSymbol(@NotNull final SymbolCreateDto symbolCreateDto) {
    final Symbol symbol = this.getSymbolMapper().toEntity(symbolCreateDto);
    final Symbol saved = this.getSymbolRepository().saveAndFlush(symbol);
    return this.getSymbolMapper().toDto(saved);
  }

  @Override
  public void updateSymbol(@NotNull final SymbolUpdateDto symbolUpdateDto, @NotNull final String name) {
    this.getSymbolRepository()
        .updateDigitsAndSwapLongAndSwapShortByName(symbolUpdateDto.digits(), symbolUpdateDto.swapLong(), symbolUpdateDto.swapShort(), name);
  }

  @Override
  public void deleteSymbol(@NotNull final String name) {
    this.getSymbolRepository().deleteByName(name);
  }


}
