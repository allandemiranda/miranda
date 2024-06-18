package lu.forex.system.mappers.impls;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.CurrencyPairDto;
import lu.forex.system.dtos.NewSymbolDto;
import lu.forex.system.dtos.SwapDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.entities.CurrencyPair;
import lu.forex.system.entities.Swap;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.Currency;
import lu.forex.system.mappers.SymbolMapper;
import org.springframework.stereotype.Component;

@Component
public class SymbolMapperImpl implements SymbolMapper {

  @Override
  public @NotNull Symbol toEntity(final @NotNull NewSymbolDto newSymbolDto) {
    final var symbol = new Symbol();
    final var swap = this.newSymbolDtoToSwap(newSymbolDto);
    symbol.setSwap(swap);
    final var currencyPair = this.newSymbolDtoToCurrencyPair(newSymbolDto);
    symbol.setCurrencyPair(currencyPair);
    symbol.setDigits(newSymbolDto.digits());
    return symbol;
  }

  @Override
  @NotNull
  public Symbol toEntity(final @NotNull SymbolDto symbolDto) {
    final var symbol = new Symbol();
    symbol.setId(symbolDto.id());
    final var currencyPair = this.currencyPairDtoToCurrencyPair(symbolDto.currencyPair());
    symbol.setCurrencyPair(currencyPair);
    symbol.setDigits(symbolDto.digits());
    final var swap = this.swapDtoToSwap(symbolDto.swap());
    symbol.setSwap(swap);
    return symbol;
  }

  @Override
  public @NotNull SymbolDto toDto(final @NotNull Symbol symbol) {
    final var id = symbol.getId();
    final var currencyPair = this.currencyPairToCurrencyPairDto(symbol.getCurrencyPair());
    final var digits = symbol.getDigits();
    final var swap = swapToSwapDto(symbol.getSwap());
    return new SymbolDto(id, currencyPair, digits, swap);
  }

  private @NotNull Swap newSymbolDtoToSwap(final @NotNull NewSymbolDto newSymbolDto) {
    final var swap = new Swap();
    swap.setPercentageShort(newSymbolDto.swapShort());
    swap.setPercentageLong(newSymbolDto.swapLong());
    return swap;
  }

  private @NotNull CurrencyPair newSymbolDtoToCurrencyPair(final @NotNull NewSymbolDto newSymbolDto) {
    final var currencyPair = new CurrencyPair();
    currencyPair.setQuote(newSymbolDto.currencyQuote());
    currencyPair.setBase(newSymbolDto.currencyBase());
    return currencyPair;
  }

  private @NotNull CurrencyPair currencyPairDtoToCurrencyPair(final @NotNull CurrencyPairDto currencyPairDto) {
    final var currencyPair = new CurrencyPair();
    currencyPair.setName(currencyPairDto.name());
    currencyPair.setBase(currencyPairDto.base());
    currencyPair.setQuote(currencyPairDto.quote());
    return currencyPair;
  }

  private @NotNull Swap swapDtoToSwap(final @NotNull SwapDto swapDto) {
    final var swap = new Swap();
    swap.setPercentageLong(swapDto.percentageLong());
    swap.setPercentageShort(swapDto.percentageShort());
    return swap;
  }

  private @NotNull CurrencyPairDto currencyPairToCurrencyPairDto(final @NotNull CurrencyPair currencyPair) {
    final Currency base = currencyPair.getBase();
    final Currency quote = currencyPair.getQuote();
    final String name = currencyPair.getName();
    final String description = currencyPair.getDescription();
    return new CurrencyPairDto(base, quote, name, description);
  }

  private @NotNull SwapDto swapToSwapDto(final @NotNull Swap swap) {
    final var percentageLong = swap.getPercentageLong();
    final var percentageShort = swap.getPercentageShort();
    return new SwapDto(percentageLong, percentageShort);
  }
}
