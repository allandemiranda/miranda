package lu.forex.system.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.enums.TimeFrame;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/candlesticks")
public interface CandlestickController {

  @GetMapping("/{symbolName}/{timeFrame}")
  @ResponseStatus(HttpStatus.OK)
  Collection<CandlestickDto> getCandlesticks(@PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String symbolName,
      @PathVariable @NotNull TimeFrame timeFrame);

  @GetMapping("/{symbolName}/{timeFrame}/lest")
  @ResponseStatus(HttpStatus.OK)
  CandlestickDto getLastCandlesticks(@PathVariable @NotNull @NotBlank @Size(max = 6, min = 6) String symbolName,
      @PathVariable @NotNull TimeFrame timeFrame);

}
