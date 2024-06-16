package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.dtos.OrderDto;
import lu.forex.system.dtos.TickDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface OrderService {

  @Transactional
  @NotNull
  Collection<@NotNull OrderDto> updateOrders(@NotNull TickDto tickDto);

}
