package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.models.Ac;
import lu.forex.system.models.Adx;
import lu.forex.system.models.Macd;

/**
 * DTO for {@link lu.forex.system.entities.Candlestick}
 */
public record CandlestickIndicatorsDto(@NotNull UUID id, @NotNull Symbol symbol, @NotNull TimeFrame timeFrame,
                                       @NotNull @PastOrPresent LocalDateTime timestamp, @Positive double high, @Positive double low,
                                       @Positive double open, @Positive double close, @NotNull Ac ac, @NotNull Adx adx, @NotNull Macd macd) implements
    Serializable {

}