package lu.forex.system.dtos;

import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link lu.forex.system.entities.Tick}
 */
public record TickCreateDto(LocalDateTime timestamp, @Positive double bid, @Positive double ask) implements Serializable {

  @Serial
  private static final long serialVersionUID = 3843334268772263525L;
}