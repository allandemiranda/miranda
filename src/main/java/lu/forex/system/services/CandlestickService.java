package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Collection;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import org.springframework.stereotype.Service;

@Service
public interface CandlestickService {

  @Nonnull
  Collection<CandlestickResponseDto> getCandlesticks(final @Nonnull String symbolName, final @Nonnull TimeFrame timeFrame);

  void createOrUpdateCandlestick(final @Nonnull Symbol symbol, final @Nonnull LocalDateTime timestamp, final @Positive double price);
}
