package lu.forex.system.operations;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/ticks")
public interface TickOperations {

  @GetMapping("/{symbolName}")
  @ResponseStatus(HttpStatus.OK)
  Collection<TickResponseDto> getTicksBySymbolName(final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName);

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  TickResponseDto addTick(final @RequestBody @Valid TickCreateDto tickCreateDto);
}
