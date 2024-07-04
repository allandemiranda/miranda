package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.UUID;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface OrderService {

  @Transactional
  @NotNull
  Collection<OrderDto> getOrders(final @NotNull UUID symbolId, final @NotNull OrderStatus orderStatus);

  @Transactional
  void batchProcessingInitOrders(final @NotNull TickDto @NotNull [] ticksDto);

  @Transactional(readOnly = true)
  Collection<OrderDto> getOrders(final @NotNull UUID symbolId);
}