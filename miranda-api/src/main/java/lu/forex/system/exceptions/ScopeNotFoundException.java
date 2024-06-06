package lu.forex.system.exceptions;

import lu.forex.system.enums.TimeFrame;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ScopeNotFoundException extends RuntimeException {

  public ScopeNotFoundException(final TimeFrame timeFrame, final String symbolName) {
    super(String.format("Scope %s in %s not found", symbolName, timeFrame));
  }
}
