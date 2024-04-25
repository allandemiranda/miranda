package lu.forex.system.mappers;

import lombok.NonNull;
import lu.forex.system.entities.Symbol;
import lu.forex.system.models.SymbolDto;

public class SymbolMapper {

  private SymbolMapper() {
    throw new IllegalStateException("SymbolMapper class");
  }

  // To DTO
  public static @NonNull SymbolDto toDto(final @NonNull Symbol symbol) {
    return new SymbolDto(symbol.getName(), symbol.getDescription(), symbol.getMargin(), symbol.getProfit(), symbol.getDigits(), symbol.getSwapLong(),
        symbol.getSwapShort());
  }

  // To Entity
  public static @NonNull Symbol toEntity(final @NonNull SymbolDto symbolDto) {
    return new Symbol(symbolDto.name(), symbolDto.description(), symbolDto.margin(), symbolDto.profit(), symbolDto.digits(), symbolDto.swapLong(),
        symbolDto.swapShort());
  }

}
