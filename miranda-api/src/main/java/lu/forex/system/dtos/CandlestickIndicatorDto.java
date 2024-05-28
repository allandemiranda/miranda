package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lu.forex.system.enums.TimeFrame;

/**
 * DTO for {@link lu.forex.system.entities.Candlestick}
 */
public record CandlestickIndicatorDto(UUID id, @NotNull SymbolResponseDto symbol, @NotNull TimeFrame timeFrame,
                                      @NotNull @PastOrPresent LocalDateTime timestamp, @Positive double high, @Positive double low,
                                      @Positive double open, @Positive double close, AcIndicatorDto acIndicator, AdxIndicatorDto adxIndicator,
                                      Set<EmaStatisticDto> emaStatistics, MacdIndicatorDto macdIndicator) implements
    Serializable {

}