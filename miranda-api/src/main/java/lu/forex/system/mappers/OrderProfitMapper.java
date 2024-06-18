package lu.forex.system.mappers;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.OrderProfitDto;
import lu.forex.system.entities.OrderProfit;

public interface OrderProfitMapper {

  @NotNull
  OrderProfit toEntity(final @NotNull OrderProfitDto orderProfitDto);

  @NotNull
  OrderProfitDto toDto(final @NotNull OrderProfit orderProfit);
}