package lu.forex.system.dtos;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link lu.forex.system.entities.MacdIndicator}
 */
public record MacdIndicatorDto(UUID id, Double macd, Double signal) implements Serializable {

}