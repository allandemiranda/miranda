package lu.forex.system.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import lu.forex.system.services.TickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticks")
@Getter(AccessLevel.PRIVATE)
public class TickController {

  private final TickService tickService;

  @Autowired
  public TickController(final TickService tickService) {
    this.tickService = tickService;
  }

  @GetMapping("/{symbolName}")
  @ResponseStatus(HttpStatus.OK)
  public Collection<TickResponseDto> getTicksBySymbolName(@PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String symbolName) {
    return this.getTickService().getTicks(symbolName);
  }

  @PostMapping("/{symbolName}")
  @ResponseStatus(HttpStatus.CREATED)
  public TickResponseDto addTick(@RequestBody @NotNull @Valid TickCreateDto tickCreateDto,
      @PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String symbolName) {
    return this.getTickService().addTick(tickCreateDto, symbolName);
  }
}
