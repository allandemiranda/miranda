package lu.forex.system.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.models.SymbolDto;
import lu.forex.system.models.SymbolUpdateDto;
import lu.forex.system.services.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
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
@Getter(AccessLevel.PRIVATE)
public class SymbolController {

  private final SymbolService symbolService;

  @Autowired
  public SymbolController(final SymbolService symbolService) {
    this.symbolService = symbolService;
  }

  @GetMapping
  public Collection<SymbolDto> getSymbols() {
    return this.getSymbolService().findAll();
  }

  @GetMapping("/{name}")
  @ResponseStatus(HttpStatus.OK)
  public SymbolDto getSymbol(@PathVariable @NonNull @NotBlank @Size(max = 6, min = 6) final String name) {
    return this.getSymbolService().findByName(name).orElseThrow(() -> new SymbolNotFoundException(name.concat(" not exist!")));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SymbolDto addSymbol(@RequestBody @Valid final SymbolDto symbolDto) {
    return this.getSymbolService().save(symbolDto);
  }

  @PutMapping("/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  public SymbolDto updateSymbol(@PathVariable @NonNull final String name, @RequestBody @Valid final SymbolUpdateDto symbolUpdateDto) {
    return this.getSymbolService().updateSymbolByName(symbolUpdateDto, name);
  }

  @DeleteMapping("/{name}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteSymbol(@PathVariable @NonNull final String name) {
    this.getSymbolService().deleteByName(name);
  }

}
