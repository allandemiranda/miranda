package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.NewSymbolDto;
import lu.forex.system.dtos.ResponseSymbolDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.exceptions.SymbolNotFoundException;
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
  public Collection<@NotNull ResponseSymbolDto> getSymbols() {
    return this.getSymbolRepository().findAll().stream().map(this.getSymbolMapper()::toDto).toList();
  }

  @NotNull
  @Override
  public ResponseSymbolDto getSymbol(@NotNull final String symbolName) {
    final Symbol symbol = this.getSymbolRepository().getFirstByCurrencyPair_Name(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    return this.getSymbolMapper().toDto(symbol);
  }

  @NotNull
  @Override
  public ResponseSymbolDto addSymbol(@NotNull final NewSymbolDto symbolDto) {
    final Symbol symbol = this.getSymbolMapper().toEntity(symbolDto);
    final Symbol savedSymbol = this.getSymbolRepository().save(symbol);
    return this.getSymbolMapper().toDto(savedSymbol);
  }
}
