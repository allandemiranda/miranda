package lu.forex.system.mappers;

import lombok.NonNull;
import lu.forex.system.entities.Swap;
import lu.forex.system.entities.Symbol;
import lu.forex.system.models.SwapDto;
import lu.forex.system.models.SymbolDto;
import org.springframework.stereotype.Component;

@Component
public class SymbolMapper {

  // To DTO
  public @NonNull SymbolDto toDto(final @NonNull Symbol symbol) {
    final Swap swap = symbol.getSwap();
    final SwapDto swapDto = new SwapDto(swap.getLongTax(), swap.getShortTax(), swap.getRateTriple());
    return new SymbolDto(symbol.getId(), symbol.getName(), symbol.getMargin(), symbol.getProfit(), symbol.getDigits(), swapDto);
  }

  // To Entity
  public @NonNull Symbol toEntity(final @NonNull SymbolDto symbolDto) {
    final SwapDto swapDto = symbolDto.getSwap();
    final Swap swap = new Swap(swapDto.getLongTax(), swapDto.getShortTax(), swapDto.getRateTriple());
    return new Symbol(symbolDto.getName(), symbolDto.getMargin(), symbolDto.getProfit(), symbolDto.getDigits(), swap);
  }

}
