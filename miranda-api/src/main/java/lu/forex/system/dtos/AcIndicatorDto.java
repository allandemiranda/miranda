package lu.forex.system.dtos;

import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.util.UUID;
import lu.forex.system.enums.SignalIndicatorStatus;

/**
 * DTO for {@link lu.forex.system.entities.AcIndicator}
 */
public record AcIndicatorDto(UUID id, @PositiveOrZero double mp, Double ao, Double ac, Double lestAc, Boolean lestColor, Boolean color,
                             SignalIndicatorStatus status) implements
    Serializable {

}