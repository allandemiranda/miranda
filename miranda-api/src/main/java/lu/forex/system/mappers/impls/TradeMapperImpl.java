package lu.forex.system.mappers.impls;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.entities.Order;
import lu.forex.system.entities.Trade;
import lu.forex.system.mappers.OrderMapper;
import lu.forex.system.mappers.ScopeMapper;
import lu.forex.system.mappers.TradeMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TradeMapperImpl implements TradeMapper {

  private final ScopeMapper scopeMapper;
  private final OrderMapper orderMapper;

  @Override
  public @NotNull Trade toEntity(final @NotNull TradeDto tradeDto) {
    final var trade = new Trade();
    trade.setId(tradeDto.id());
    final var scope = this.getScopeMapper().toEntity(tradeDto.scope());
    trade.setScope(scope);
    trade.setStopLoss(tradeDto.stopLoss());
    trade.setTakeProfit(tradeDto.takeProfit());
    trade.setSpreadMax(tradeDto.spreadMax());
    trade.setSlotWeek(tradeDto.slotWeek());
    trade.setSlotStart(tradeDto.slotStart());
    trade.setSlotEnd(tradeDto.slotEnd());
    final var orders = this.orderDtoSetToOrderList(tradeDto.orders());
    trade.setOrders(orders);
    return trade;
  }

  @Override
  public @NotNull TradeDto toDto(final @NotNull Trade trade) {
    final var id = trade.getId();
    final var scope = this.getScopeMapper().toDto(trade.getScope());
    final var stopLoss = trade.getStopLoss();
    final var takeProfit = trade.getTakeProfit();
    final var spreadMax = trade.getSpreadMax();
    final var slotWeek = trade.getSlotWeek();
    final var slotStart = trade.getSlotStart();
    final var slotEnd = trade.getSlotEnd();
    final var orders = this.orderSetToOrderDtoList(trade.getOrders());
    final var balance = trade.getBalance();
    final var isActivate = trade.isActivate();
    return new TradeDto(id, scope, stopLoss, takeProfit, spreadMax, slotWeek, slotStart, slotEnd, isActivate, orders, balance);
  }

  private @NotNull List<@NotNull Order> orderDtoSetToOrderList(final @NotNull List<@NotNull OrderDto> set) {
    return set.stream().map(orderDto -> this.getOrderMapper().toEntity(orderDto)).toList();
  }

  private @NotNull List<@NotNull OrderDto> orderSetToOrderDtoList(final @NotNull List<@NotNull Order> set) {
    return set.stream().map(order -> this.getOrderMapper().toDto(order)).toList();
  }
}
