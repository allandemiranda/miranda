package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link lu.forex.system.entities.AcIndicator}
 */
public record AcIndicatorDto(@NotNull UUID id, @PositiveOrZero double mp, Double ao, Double ac) implements Serializable {

}