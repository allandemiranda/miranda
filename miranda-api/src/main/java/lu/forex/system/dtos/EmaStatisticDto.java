package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.TimeFrame;

/**
 * DTO for {@link lu.forex.system.entities.EmaStatistic}
 */
public record EmaStatisticDto(UUID id, int period, CandlestickApply candlestickApply, Double ema, EmaStatisticDto lastEmaStatistic, String symbolName,
                              @NotNull TimeFrame timeFrame, @NotNull @PastOrPresent LocalDateTime timestamp) implements
    Serializable {

}