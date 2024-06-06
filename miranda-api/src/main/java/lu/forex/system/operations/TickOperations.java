package lu.forex.system.operations;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.ResponseTickDto;
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
  Collection<ResponseTickDto> getTicksBySymbolName(final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName);

  @PostMapping("/{symbolName}")
  @ResponseStatus(HttpStatus.CREATED)
  ResponseTickDto addTick(final @RequestBody @Valid NewTickDto tickDto, final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName);

}
