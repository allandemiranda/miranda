package lu.forex.system.operations;

import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/order")
public interface OrderOperation {

  @GetMapping("/{symbolName}/clean/{days}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void cleanOperationUntilLastDays(@PathVariable("symbolName") String symbolName, @PathVariable("days") @Positive int days);

}
