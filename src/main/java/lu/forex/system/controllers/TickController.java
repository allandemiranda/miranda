package lu.forex.system.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/symbols")
public interface TickController {

  @GetMapping("/{symbolName}")
  Collection<TickDto> getTicksBySymbolName(@PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String symbolName);

  @PostMapping("/{symbolName}")
  TickDto addTick(@RequestBody @Valid TickCreateDto tickCreateDto, @PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String symbolName);
}
