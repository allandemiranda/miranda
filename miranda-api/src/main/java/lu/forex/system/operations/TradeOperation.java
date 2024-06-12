package lu.forex.system.operations;

import java.util.List;
import lu.forex.system.dtos.TradeDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/trade")
public interface TradeOperation {

  @GetMapping("/{symbolName}")
  @ResponseStatus(HttpStatus.OK)
  List<TradeDto> getTrades(@PathVariable("symbolName") String symbolName);
}
