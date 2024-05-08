package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Collection;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface CandlestickService {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  @Nonnull
  Collection<CandlestickResponseDto> getCandlesticks(final @Nonnull String symbolName, final @Nonnull TimeFrame timeFrame);

  @Transactional(propagation = Propagation.REQUIRED)
  void createOrUpdateCandlestick(final @Nonnull Symbol symbol, final @Nonnull LocalDateTime timestamp, final @Positive double price);
}
