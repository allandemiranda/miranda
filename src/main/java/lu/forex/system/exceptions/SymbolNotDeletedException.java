package lu.forex.system.exceptions;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SymbolNotDeletedException extends RuntimeException {

  public SymbolNotDeletedException(final String symbolName) {
    super(String.format("Symbol %s not deleted", symbolName));
  }
}
