package lu.forex.system.operations;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lu.forex.system.dtos.NewSymbolDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.SymbolDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/symbols")
public interface SymbolOperation {

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  Collection<SymbolDto> getSymbols();

  @GetMapping("/{name}")
  @ResponseStatus(HttpStatus.OK)
  SymbolDto getSymbol(final @PathVariable @NotBlank @Size(max = 6, min = 6) String name);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Collection<ScopeDto> addSymbol(final @RequestBody @Valid NewSymbolDto newSymbolDto);
}
