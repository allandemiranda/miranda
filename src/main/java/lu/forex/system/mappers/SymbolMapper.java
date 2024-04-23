package lu.forex.system.mappers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lu.forex.system.entities.Swap;
import lu.forex.system.entities.Symbol;
import lu.forex.system.models.SwapDto;
import lu.forex.system.models.SymbolDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter(AccessLevel.PRIVATE)
public class SymbolMapper {

  private final SwapMapper swapMapper;

  @Autowired
  public SymbolMapper(final SwapMapper swapMapper) {
    this.swapMapper = swapMapper;
  }

  // To DTO
  public @NonNull SymbolDto toDto(final @NonNull Symbol symbol) {
    final Swap swap = symbol.getSwap();
    final SwapDto swapDto = this.getSwapMapper().toDto(swap);
    return new SymbolDto(symbol.getName(), symbol.getMargin(), symbol.getProfit(), symbol.getDigits(), swapDto);
  }

  // To Entity
  public @NonNull Symbol toEntity(final @NonNull SymbolDto symbolDto) {
    final SwapDto swapDto = symbolDto.swap();
    final Swap swap = this.getSwapMapper().toEntity(swapDto);
    return new Symbol(symbolDto.name(), symbolDto.margin(), symbolDto.profit(), symbolDto.digits(), swap);
  }

}
