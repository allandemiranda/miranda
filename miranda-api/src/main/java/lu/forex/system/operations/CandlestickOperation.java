package lu.forex.system.operations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.enums.TimeFrame;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/candlesticks")
public interface CandlestickOperation {

  @GetMapping("/{symbolName}/{timeFrame}")
  @ResponseStatus(HttpStatus.OK)
  Collection<CandlestickDto> getCandlesticks(final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName,
      final @PathVariable TimeFrame timeFrame);
}
