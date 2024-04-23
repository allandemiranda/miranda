package lu.forex.system.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lu.forex.system.models.SymbolDto;
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
import org.springframework.web.bind.annotation.RequestParam;
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
  public List<SymbolDto> getSymbols() {
    return this.getSymbolService().findAll();
  }

  @GetMapping("/{name}")
  @ResponseStatus(HttpStatus.OK)
  public SymbolDto getSymbol(@PathVariable("name") @NonNull @NotBlank @Size(max = 6, min = 6) final String name) {
    return this.getSymbolService().findByName(name);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SymbolDto addSymbol(@RequestBody @Valid final SymbolDto symbol) {
    return this.getSymbolService().save(symbol);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SymbolDto updateSymbol(@RequestBody @Valid final SymbolDto symbol) {
    return this.getSymbolService().save(symbol);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.OK)
  public void deleteSymbol(final @RequestParam UUID id) {
    this.getSymbolService().delete(id);
  }

}
