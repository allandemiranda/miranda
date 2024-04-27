package lu.forex.system.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/symbols")
public interface SymbolController {

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  Collection<SymbolDto> getSymbols();

  @GetMapping("/{name}")
  @ResponseStatus(HttpStatus.OK)
  SymbolDto getSymbol(@PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String name);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  SymbolDto addSymbol(@RequestBody @Valid SymbolDto symbolDto);

  @PutMapping("/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  SymbolDto updateSymbol(@RequestBody @Valid SymbolUpdateDto symbolUpdateDto, @PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String name);

  @DeleteMapping("/{name}")
  @ResponseStatus(HttpStatus.OK)
  void deleteSymbol(@PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String name);
}
