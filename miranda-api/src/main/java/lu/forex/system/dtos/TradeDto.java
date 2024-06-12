package lu.forex.system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link lu.forex.system.entities.Trade}
 */
public record TradeDto(@NotNull UUID id, @NotNull ScopeDto scope, @PositiveOrZero int stopLoss, @Positive int takeProfit,
                       @PositiveOrZero int spreadMax, @NotNull LocalTime slotStart, @NotNull LocalTime slotEnd, boolean isActivate,
                       @NotNull Set<OrderDto> orders, double balance) implements
    Serializable {

}