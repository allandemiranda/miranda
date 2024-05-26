package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link lu.forex.system.entities.AdxIndicator}
 */
public record AdxIndicatorDto(UUID id, Double trOne, Double pDmOne, Double nDmOne, Double dx, Double adx) implements
    Serializable {

  @Serial
  private static final long serialVersionUID = 6316236814819223351L;
}