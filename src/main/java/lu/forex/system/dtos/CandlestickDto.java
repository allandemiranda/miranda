package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.enums.TimeFrame;

/**
 * DTO for {@link Candlestick}
 */
public record CandlestickDto(UUID id, @NotNull TimeFrame timeFrame, @NotNull LocalDateTime timestamp, @NotNull SymbolDto symbol, double high,
                             double low, double open, double close) implements Serializable {

  @Serial
  private static final long serialVersionUID = 2115903891503978256L;
}