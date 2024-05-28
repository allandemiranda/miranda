package lu.forex.system.dtos;

import java.io.Serializable;
import java.util.UUID;
import lu.forex.system.enums.SignalIndicatorStatus;

/**
 * DTO for {@link lu.forex.system.entities.AdxIndicator}
 */
public record AdxIndicatorDto(UUID id, Double trOne, Double pDmOne, Double nDmOne, Double pDiP, Double nDiP, Double dx, Double adx,
                              SignalIndicatorStatus status) implements
    Serializable {

}