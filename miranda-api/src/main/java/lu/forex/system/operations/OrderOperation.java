package lu.forex.system.operations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lu.forex.system.dtos.OrderDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/order")
public interface OrderOperation {

  @GetMapping("/{symbolName}/open")
  @ResponseStatus(HttpStatus.OK)
  Collection<OrderDto> getOrdersOpen(final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName);

  @GetMapping("/{symbolName}/close")
  @ResponseStatus(HttpStatus.OK)
  Collection<OrderDto> getOrdersClose(final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName);

  @GetMapping("/{symbolName}/takeProfit")
  @ResponseStatus(HttpStatus.OK)
  Collection<OrderDto> getOrdersTakeProfit(final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName);
}
