package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lu.forex.system.annotations.CandlestickRepresentation;
import lu.forex.system.enums.TimeFrame;

/**
 * DTO for {@link lu.forex.system.entities.Candlestick}
 */
@CandlestickRepresentation
public record CandlestickResponseDto(@NotNull UUID id, @NotNull SymbolResponseDto symbol, @NotNull TimeFrame timeFrame,
                                     @NotNull @Past LocalDateTime timestamp, @Positive double high, @Positive double low, @Positive double open,
                                     @Positive double close) implements Serializable {

  @Serial
  private static final long serialVersionUID = -6945362025070999889L;
}