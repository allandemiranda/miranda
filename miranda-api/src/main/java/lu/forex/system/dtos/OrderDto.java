package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;
import lu.forex.system.enums.OrderStatus;
import lu.forex.system.enums.OrderType;

/**
 * DTO for {@link lu.forex.system.entities.Order}
 */
public record OrderDto(@NotNull UUID id, @NotNull TickDto openTick, @NotNull TickDto closeTick, @NotNull OrderType orderType, UUID tradeId,
                       ScopeDto tradeScope, int tradeStopLoss, int tradeTakeProfit, int tradeSpreadMax, LocalTime tradeSlotStart,
                       LocalTime tradeSlotEnd, boolean tradeIsActivate, double tradeBalance, @NotNull OrderStatus orderStatus, boolean isSimulator,
                       double profit) implements
    Serializable {

}