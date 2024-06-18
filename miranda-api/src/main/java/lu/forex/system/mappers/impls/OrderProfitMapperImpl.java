package lu.forex.system.mappers.impls;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.OrderProfitDto;
import lu.forex.system.entities.OrderProfit;
import lu.forex.system.mappers.OrderProfitMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderProfitMapperImpl implements OrderProfitMapper {

  @Override
  public @NotNull OrderProfit toEntity(final @NotNull OrderProfitDto orderProfitDto) {
    final var orderProfit = new OrderProfit();
    orderProfit.setId(orderProfitDto.id());
    orderProfit.setTimestamp(orderProfitDto.timestamp());
    orderProfit.setProfit(orderProfitDto.profit());
    return orderProfit;
  }

  @Override
  public @NotNull OrderProfitDto toDto(final @NotNull OrderProfit orderProfit) {
    final var id = orderProfit.getId();
    final var timestamp = orderProfit.getTimestamp();
    final var profit = orderProfit.getProfit();
    return new OrderProfitDto(id, timestamp, profit);
  }
}

