package lu.forex.system.operations;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.TickDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/ticks")
public interface TickOperation {

  @GetMapping("/{symbolName}")
  @ResponseStatus(HttpStatus.OK)
  Collection<TickDto> getTicksBySymbolName(final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName);

  @PostMapping("/{symbolName}")
  @ResponseStatus(HttpStatus.CREATED)
  Collection<CandlestickDto> addTickBySymbolName(final @RequestBody @Valid NewTickDto newTickDto, final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName);

}
