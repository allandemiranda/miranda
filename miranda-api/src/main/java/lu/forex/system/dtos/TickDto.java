package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link lu.forex.system.entities.Tick}
 */
public record TickDto(@NotNull UUID id, @NotNull LocalDateTime timestamp, @Positive double bid, @Positive double ask) implements Serializable {

  @Serial
  private static final long serialVersionUID = -2044172470840995958L;
}