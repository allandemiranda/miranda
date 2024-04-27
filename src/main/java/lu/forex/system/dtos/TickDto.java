package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lu.forex.system.entities.Tick;

/**
 * DTO for {@link Tick}
 */
public record TickDto(LocalDateTime timestamp, @Positive double bid, @Positive double ask, @NotNull SymbolDto symbol) implements Serializable {

  @Serial
  private static final long serialVersionUID = 3967193703565468347L;
}