package lu.forex.system.mappers;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.entities.Order;

public interface OrderMapper {

  @NotNull
  Order toEntity(final @NotNull OrderDto orderDto);

  @NotNull
  OrderDto toDto(final @NotNull Order order);
}