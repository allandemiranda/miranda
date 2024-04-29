package lu.forex.system.operations;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface SymbolOperations {

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  Collection<SymbolResponseDto> getSymbols();

  @GetMapping("/{name}")
  @ResponseStatus(HttpStatus.OK)
  SymbolResponseDto getSymbol(@PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String name);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  SymbolResponseDto addSymbol(@RequestBody @NotNull @Valid SymbolCreateDto symbolCreateDto);

  @PutMapping("/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  SymbolResponseDto updateSymbol(@RequestBody @NotNull @Valid SymbolUpdateDto symbolUpdateDto,
      @PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String name);

  @DeleteMapping("/{name}")
  @ResponseStatus(HttpStatus.OK)
  void deleteSymbol(@PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String name);
}
