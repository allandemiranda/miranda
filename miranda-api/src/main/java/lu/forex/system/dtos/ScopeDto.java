package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lu.forex.system.enums.TimeFrame;

/**
 * DTO for {@link lu.forex.system.entities.Scope}
 */
public record ScopeDto(@NotNull UUID id, @NotNull SymbolDto symbol, @NotNull TimeFrame timeFrame) implements Serializable {

  @Serial
  private static final long serialVersionUID = -7574732354832408461L;
}