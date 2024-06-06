package lu.forex.system.dtos;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link lu.forex.system.entities.Swap}
 */
public record SwapDto(double percentageLong, double percentageShort) implements Serializable {
  @Serial
  private static final long serialVersionUID = -8459675499045285992L;
}