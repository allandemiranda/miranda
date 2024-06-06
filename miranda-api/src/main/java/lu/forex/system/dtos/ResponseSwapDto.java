package lu.forex.system.dtos;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for {@link lu.forex.system.entities.Swap}
 */
public record ResponseSwapDto(double percentageLong, double percentageShort) implements Serializable {

  @Serial
  private static final long serialVersionUID = -5781157356453748659L;
}
