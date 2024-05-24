package lu.forex.system.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lu.forex.system.annotations.TickRepresentation;

/**
 * DTO for {@link lu.forex.system.entities.Tick}
 */
@TickRepresentation
public record TickCreateDto(@NotNull @PastOrPresent LocalDateTime timestamp, @Positive double bid, @Positive double ask,
                            @NotBlank @Size(max = 6, min = 6) String symbolName) implements Serializable {

  @Serial
  private static final long serialVersionUID = 3843334268772263525L;

}