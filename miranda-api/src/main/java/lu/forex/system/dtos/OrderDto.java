package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lu.forex.system.enums.OrderStatus;
import lu.forex.system.enums.OrderType;

/**
 * DTO for {@link lu.forex.system.entities.Order}
 */
public record OrderDto(@NotNull UUID id, @NotNull TickDto openTick, @NotNull TickDto closeTick, @NotNull OrderType orderType,
                       @NotNull OrderStatus orderStatus, double profit, @NotNull UUID tradeId, int tradeStopLoss, int tradeTakeProfit, int tradeSpreadMax,
                       boolean tradeIsActivate, @NotNull ScopeDto tradeScope) implements Serializable {

  @Serial
  private static final long serialVersionUID = -3428499167913412098L;
}