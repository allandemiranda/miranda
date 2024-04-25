package lu.forex.system.controllers;

import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.models.SymbolDto;
import lu.forex.system.models.TickCreateDto;
import lu.forex.system.models.TickDto;
import lu.forex.system.services.SymbolService;
import lu.forex.system.services.TickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticks")
@Getter(AccessLevel.PRIVATE)
public class TickController {

  private final TickService tickService;
  private final SymbolService symbolService;

  @Autowired
  public TickController(final TickService tickService, final SymbolService symbolService) {
    this.tickService = tickService;
    this.symbolService = symbolService;
  }

  @GetMapping("/{symbolName}")
  public Collection<TickDto> getTicks(@PathVariable @NonNull @Size(max = 6, min = 6) final String symbolName) {
    return this.getTickService().getTicksBySymbol(symbolName).stream().map(TickMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
  }

  @PostMapping("/{symbolName}")
  public TickDto addTick(@RequestBody final TickCreateDto tickCreateDto, @PathVariable @NonNull @Size(max = 6, min = 6) final String symbolName) {
    final SymbolDto symbolDto = this.getSymbolService().findByName(symbolName).orElseThrow(SymbolNotFoundException::new);
    return this.getTickService().save(tickCreateDto, symbolDto);
  }
}
