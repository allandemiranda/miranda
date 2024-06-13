package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link lu.forex.system.entities.OrderProfit}
 */
public record OrderProfitDto(@NotNull UUID id, @NotNull LocalDateTime timestamp, double profit) implements Serializable {

  @Serial
  private static final long serialVersionUID = -6294913633752568932L;
}