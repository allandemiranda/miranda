package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.enums.TimeFrame;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface CandlestickService {

  @Transactional(readOnly = true)
  @Nonnull
  Stream<@NotNull CandlestickResponseDto> getCandlesticks(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName,
      final @Nonnull TimeFrame timeFrame);

  @Transactional()
  void createOrUpdateCandlestickByPrice(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName, final @Nonnull LocalDateTime timestamp,
      final @NotNull TimeFrame timeFrame, final @Positive double price);

  @Transactional(readOnly = true)
  @Nonnull
  Stream<@NotNull CandlestickResponseDto> getLastCandlesticks(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName,
      final @Nonnull TimeFrame timeFrame, final @Positive int limit);
}
