package lu.forex.system.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lu.forex.system.models.SwapDto;
import lu.forex.system.models.SymbolDto;
import lu.forex.system.entities.Swap;
import lu.forex.system.entities.Symbol;
import lu.forex.system.repositories.SymbolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter(AccessLevel.PRIVATE)
public class SymbolService {

  private final SymbolRepository symbolRepository;

  @Autowired
  public SymbolService(final SymbolRepository symbolRepository) {
    this.symbolRepository = symbolRepository;
  }

  @Transactional
  public SymbolDto save(final @NonNull SymbolDto symbol) {
    return this.modelToDto(this.getSymbolRepository().save(this.dtoToModel(symbol)));
  }

  @Transactional
  public void delete(final @NonNull UUID id) {
    this.getSymbolRepository().deleteById(id);
  }

  public List<SymbolDto> findAll() {
    return this.getSymbolRepository().findAll().stream().map(this::modelToDto).toList();
  }

  public SymbolDto findByName(final @NonNull @NotBlank String name) {
    return this.modelToDto(this.getSymbolRepository().findByName(name));
  }

  private @NonNull SymbolDto modelToDto(final @NonNull Symbol symbol) {
    final Swap swap = symbol.getSwap();
    final SwapDto swapDto = new SwapDto(swap.getLongTax(), swap.getShortTax(), swap.getRateTriple());
    return new SymbolDto(symbol.getId(), symbol.getName(), symbol.getMargin(), symbol.getProfit(), symbol.getDigits(), swapDto);
  }

  private @NonNull Symbol dtoToModel(final @NonNull SymbolDto symbolDto) {
    final SwapDto swapDto = symbolDto.getSwap();
    final Swap swap = new Swap(swapDto.getLongTax(), swapDto.getShortTax(), swapDto.getRateTriple());
    return new Symbol(symbolDto.getName(), symbolDto.getMargin(), symbolDto.getProfit(), symbolDto.getDigits(), swap);
  }
}
