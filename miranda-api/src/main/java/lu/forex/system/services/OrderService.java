package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lu.forex.system.dtos.TickDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface OrderService {

  @Transactional
  void updateOrders(@NotNull TickDto tickDto);

  @Transactional
  void cleanOrdersCloseAfterDays(final @NotNull String symbolName, final @Positive int days);

  @Async
  void processingInitOrders(final @NotNull List<TickDto> tickDtoList);

}