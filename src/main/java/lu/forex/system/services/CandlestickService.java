package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.enums.TimeFrame;
import org.springframework.stereotype.Service;

@Service
public interface CandlestickService {

//  @Nonnull
//  CandlestickResponseDto addCandlestick(final @Nonnull String SymbolName, final @Nonnull TimeFrame timeFrame,
//      final @NotNull CandlestickCreateDto candlestickCreateDto);
//
//  @Nonnull
//  CandlestickResponseDto updateCandlestick(final @Nonnull String SymbolName, final @Nonnull TimeFrame timeFrame,
//      final @NotNull CandlestickUpdateDto candlestickUpdateDto);

  @Nonnull
  Collection<CandlestickResponseDto> getCandlesticks(final @Nonnull String symbolName, final @Nonnull TimeFrame timeFrame);
}
