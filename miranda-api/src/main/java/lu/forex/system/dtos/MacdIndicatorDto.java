package lu.forex.system.dtos;

import java.io.Serializable;
import java.util.UUID;
import lu.forex.system.enums.SignalIndicatorStatus;

/**
 * DTO for {@link lu.forex.system.entities.MacdIndicator}
 */
public record MacdIndicatorDto(UUID id, Double macd, Double signal, SignalIndicatorStatus status) implements Serializable {

}