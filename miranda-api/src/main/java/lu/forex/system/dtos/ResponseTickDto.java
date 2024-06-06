package lu.forex.system.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link lu.forex.system.entities.Tick}
 */
public record ResponseTickDto(@NotNull UUID id, @NotNull @Size(min = 6, max = 6) @NotEmpty @NotBlank String symbolName,
                              @NotNull @PastOrPresent LocalDateTime timestamp, @Positive double bid, @Positive double ask) implements Serializable {

  @Serial
  private static final long serialVersionUID = -2044172470840995958L;
}